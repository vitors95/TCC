#include "BLEDevice.h"
#include <WiFi.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>

uint16_t BACKOFF_TIME = 1000;

hw_timer_t *timer = NULL; // faz o controle do temporizador (interrupção por tempo)
//callback do watchdog

// Serviço remoto que se deseja conectar
static BLEUUID serviceUUID("0000ffe0-0000-1000-8000-00805f9b34fb");
// Característica associada ao serviço desejado
static BLEUUID charUUID("0000ffe1-0000-1000-8000-00805f9b34fb");

// MAC do endpoint
String BEACON = "00:15:85:14:9c:09";

static BLEAddress *pServerAddress;
static boolean doConnect = false;
static BLERemoteCharacteristic* pRemoteCharacteristic;

struct Disp_BLE {
    BLEClient*  pClient  = BLEDevice::createClient();
    BLEAdvertisedDevice advertisedDevice;
    boolean received = false;
    signed int accx;
    signed int accy;
    signed int accz;
    signed int rmsx;
    signed int rmsy;
    signed int rmsz;
    signed int temp;
    signed char auxByte;
} aux;

static uint8_t restartScan = 0;
static long int last_time = 0, current_time= 0;
static String last_mac = "", current_mac = "";

// Ativação do watchdog - reinício da ESP32

void IRAM_ATTR resetModule(){
    ets_printf("WATCHDOG ATIVADO, REINICIANDO...\n"); 
    esp_restart(); 
}

bool connect_wifi(){
    const char* ssid = ""; // SSID da rede Wi-Fi
    const char* password = ""; // Senha da rede Wi-Fi

    WiFi.begin(ssid, password); 
  
    while (WiFi.status() != WL_CONNECTED) { 
      delay(1000);
      Serial.println("Conectando com a rede Wi-Fi...");
    }
    if(WiFi.status() == WL_CONNECTED){
      Serial.println("Conectado, enviando dados do sensor");
      Serial.println(WiFi.localIP());
      return true;
    }else{
      Serial.println("Erro na conexão!");
      return false;
    }
}

void send_data(int accx, int accy, int accz, int rmsx, int rmsy, int rmsz, int temp){  
    DynamicJsonBuffer JSONbuffer;   
    JsonObject& JSONencoder = JSONbuffer.createObject(); 

    Serial.println("MONTANDO JSON!");

    String gatewayMac = WiFi.macAddress();

    JsonObject& collect = JSONencoder.createNestedObject("collect");
    collect["accx"] = accx;
    collect["accy"] = accy;
    collect["accz"] = accz;
    collect["rmsx"] = rmsx;
    collect["rmsy"] = rmsy;
    collect["rmsz"] = rmsz;
    collect["temp"] = temp;

    JsonObject& gateway = JSONencoder.createNestedObject("gateway");
    gateway["mac"] = gatewayMac;

    JsonObject& endpoint = JSONencoder.createNestedObject("endpoint");
    endpoint["mac"] = aux.advertisedDevice.getAddress().toString().c_str();

    char JSONmessageBuffer[400];
    JSONencoder.prettyPrintTo(Serial);
    JSONencoder.prettyPrintTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));

    HTTPClient http;
    
    http.begin("http://ec2-34-215-199-111.us-west-2.compute.amazonaws.com:5000/collect"); 
    http.addHeader("Content-Type", "application/json");

    int httpCode = http.POST(JSONmessageBuffer);  
    http.end();  
    
    Serial.println("HTTP CODE");
    Serial.println(httpCode);
    
    if (httpCode == 200){
      Serial.println("DADOS ENVIADOS PARA O BANCO!");
    }
    else {
      Serial.println("Erro no envio, ignorando dados.");  
    }
    timerWrite(timer, 0); // reseta o temporizador (alimenta o watchdog) 
}

static void notifyCallback(BLERemoteCharacteristic* pBLERemoteCharacteristic, uint8_t* pData, size_t length, bool isNotify) {
    Serial.print("Notificação recebida de: ");
    Serial.println(pBLERemoteCharacteristic->getUUID().toString().c_str());

    // Tratamento dos dados enviados pelo endpoint

    aux.auxByte = pData[1];
    aux.accx = (aux.auxByte << 8) | (pData[0]);

    aux.auxByte = pData[3];
    aux.accy = (aux.auxByte << 8) | (pData[2]);

    aux.auxByte = pData[5];
    aux.accz = (aux.auxByte << 8) | (pData[4]);

    aux.auxByte = pData[7];
    aux.rmsx = (aux.auxByte << 8) | (pData[6]);

    aux.auxByte = pData[9];
    aux.rmsy = (aux.auxByte << 8) | (pData[8]);

    aux.auxByte = pData[11];
    aux.rmsz = (aux.auxByte << 8) | (pData[10]);

    aux.auxByte = pData[13];
    aux.temp = (aux.auxByte << 8) | (pData[12]);

    aux.received = true;
    timerWrite(timer, 0); // reseta o temporizador (alimenta o watchdog) 
    aux.pClient->disconnect();
    delay(1500);
}

bool verifica_conexao(){
    current_mac = aux.advertisedDevice.getAddress().toString().c_str();

    // Implementação da abordagem do BACKOFF, para garantir que todos os endpoints consigam se conectar à ESP32.
    // Contudo, como apenas um endpoint foi utilizado no trabalho, o tempo de BACKOFF foi definido como apenas 1 segundo.
    // Sendo assim, como o endpoint envia apenas a cada 1 minuto, o BACKOFF não foi utilizado
    
    if ((current_mac.equals(last_mac))){
      long int tempo = current_time - last_time;
      if (tempo < BACKOFF_TIME){ // se houve um novo anuncio do mesmo beacon em menos de BACKOFF_TIME segundos
        Serial.println(aux.advertisedDevice.getName().c_str());
        Serial.print("Tempo em espera: ");
        Serial.print(tempo/1000);
        Serial.println("segundos");
        restartScan = 1;
        return false;
      } else{  // se já se passaram os BACKOFF_TIME segundos
        last_time = current_time;
        Serial.print("Dispositivo conectado: ");
        Serial.println(aux.advertisedDevice.getName().c_str());
        timerWrite(timer, 0); // reseta o temporizador (alimenta o watchdog) 
        return true;
      }
    } else{ // se o dispositivo que se conectou não é o mesmo que o anterior
        Serial.print("Dispositivo conectado: ");
        Serial.println(aux.advertisedDevice.getName().c_str());
        last_mac = current_mac;
        last_time = current_time;
        timerWrite(timer, 0); // reseta o temporizador (alimenta o watchdog) 
        return true;
     }
}

bool connectToServer(BLEAddress pAddress) {
    if (verifica_conexao()){
        BLEClient*  pClient  = BLEDevice::createClient();
        aux.pClient = pClient;
        pClient->connect(pAddress);
    
        // Obtém a referência do serviço do endpoint.
        BLERemoteService* pRemoteService = pClient->getService(serviceUUID);
        if (pRemoteService == nullptr) {
          Serial.print("Falha ao encontrar UUID de servico: ");
          Serial.println(serviceUUID.toString().c_str());
          return false;
        }
        
        // Obtém a referência da característica do serviço do endpoint.
        pRemoteCharacteristic = pRemoteService->getCharacteristic(charUUID);
        if (pRemoteCharacteristic == nullptr) {
          Serial.print("Falha ao encotrar UUID da caracteristica: ");
          Serial.println(charUUID.toString().c_str());
          return false;
        }
      
        pRemoteCharacteristic->registerForNotify(notifyCallback);
        return true;
    }else return false;
}

class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {

  // Callback executado sempre que um dispositivo é encontrado na rede BLE

  void onResult(BLEAdvertisedDevice advertisedDevice) {
    Serial.print("Dispositivo BLE Encontrado: ");
    Serial.println(advertisedDevice.toString().c_str());

    // Verifica se o serviço do endpoint é, de fato, o serviço desejado
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.getServiceUUID().equals(serviceUUID)) {

      advertisedDevice.getScan()->stop();
      current_time = millis();

      pServerAddress = new BLEAddress(advertisedDevice.getAddress());
      aux.advertisedDevice = advertisedDevice;
      doConnect = true;

    } 
  } 
}; 

void setup() {
  Serial.begin(230400);
  //---------------------------------------------------------------------------
  // Configuração do WATCHDOG
  //---------------------------------------------------------------------------
  timer = timerBegin(0, 80, true); // timerID 0, div 80
  
  //timer, callback, interrupção de borda
  timerAttachInterrupt(timer, &resetModule, true);
  
  //timer, tempo (us), repetição
  timerAlarmWrite(timer, 30000000, true);
  timerAlarmEnable(timer); //habilita a interrupção 
  
  Serial.println("Watchdog configurado!");
  delay(100);
  Serial.println("Iniciando Scanner BLE");
  //---------------------------------------------------------------------------
  // CONFIGURACAO DO SCANNER BLE
  //---------------------------------------------------------------------------

  // Início do Scanner BLE.
  // Definição do callback a ser executado quando um dispositivo é encontrado.
  // Duração do scan configurado para 30 segundos.

  BLEDevice::init("");
  BLEScan* pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setActiveScan(true);
  pBLEScan->start(30);
  //---------------------------------------------------------------------------
} 


void loop() {
  
  if (doConnect == true) {
    connectToServer(*pServerAddress);
    doConnect = false;
  }


  // Verifica se as informações enviadas pelo endpoint foram recebidas e lidas.
  // Após isso, tenta se conectar à rede Wi-Fi e checa se o MAC do endpoint é o desejado.
  // Por fim, envia as informações para o backend através da API Restful.
  
    if (aux.received){
      if (connect_wifi()){
        String mac_atual = aux.advertisedDevice.getAddress().toString().c_str();
        if(mac_atual.equals(BEACON)){
          send_data(
            (int) aux.accx,
            (int) aux.accy,
            (int) aux.accz,
            (int) aux.rmsx,
            (int) aux.rmsy,
            (int) aux.rmsz,
            (int) aux.temp
          );
        }
      }
      WiFi.disconnect();
      aux.received = false;
      delay(1000);
      Serial.println("Reiniciando scan BLE...");
      delay(500);
      
      BLEScan* pBLEScan = BLEDevice::getScan();
      pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
      pBLEScan->setActiveScan(true);
      pBLEScan->start(30);
    } else if(restartScan){
        Serial.println("Reiniciando scan BLE...");
        BLEScan* pBLEScan = BLEDevice::getScan();
        pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
        pBLEScan->setActiveScan(true);
        pBLEScan->start(30);
     }
}
