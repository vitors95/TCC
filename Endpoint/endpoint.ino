#include<Wire.h>
#include<SoftwareSerial.h>
#include<MPU6050.h>

SoftwareSerial mySerial(3, 2); // TX, RX

const int MPU_addr = 0x68; // Endereço I2C do MPU6050
MPU6050 mpu(MPU_addr);

signed long accX, accY, accZ, temp;
signed long rmsX, rmsY, rmsZ;
signed long maxAccX, maxAccY, maxAccZ, minAccX, minAccY, minAccZ, auxTemp = 0;
unsigned long auxRmsX, auxRmsY, auxRmsZ, aux2RmsX, aux2RmsY, aux2RmsZ = 0;
int count = 0;
byte *p;

void sendData(signed long data) {
  
  // Cada valor enviado é representado por 2 bytes
  
  p = (byte*)&data;
  mySerial.write(p, 2);
}

void setMPU() {
  // power management
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);

  mpu.setFullScaleAccelRange(1); // range de valores: +-4g || fator de sensibilidade: 8192

  // Offsets obtidos a partir do sketch MPU-6050_Calibration, desenvolvido por Luis Ródenas <luisrodenaslorda@gmail.com>
  
  mpu.setXAccelOffset(1352);
  mpu.setYAccelOffset(353);
  mpu.setZAccelOffset(1207);
  mpu.setXGyroOffset(45);
  mpu.setYGyroOffset(38);
  mpu.setZGyroOffset(5);
  
}

void setup() {
  Serial.begin(9600);
  mySerial.begin(9600);
  Wire.begin();
  setMPU();
}

void loop() {
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  //
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 8, true);  
  
  accX = Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)  
  accY = Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  accZ = Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)  
  temp = Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)

  if (accX == 0 && accY == 0 && accZ == 0) {
    // Quando ocorre erro na leitura (todos os valores 0), o MPU-6050 é reconfigurado
    setMPU();
  } else {
    if (count == 0) {
      maxAccX = accX;
      maxAccY = accY;
      maxAccZ = accZ;
      minAccX = accX;
      minAccY = accY;
      minAccZ = accZ;
    } else {
      // Armazenamento dos valores máximo e mínimo para obtenção do pico-a-pico
      maxAccX = max(maxAccX, accX);
      maxAccY = max(maxAccY, accY);
      maxAccZ = max(maxAccZ, accZ);
      minAccX = min(minAccX, accX);
      minAccY = min(minAccY, accY);
      minAccZ = min(minAccZ, accZ);  
    }
    // Armazenamento dos quadrado das medidas para cálculo dos valores RMS
    // Para que não houvesse overflow, foram utilizados 2 variáveis para cada eixo
    if (count < 10) {
      auxRmsX += (accX * accX);
      auxRmsY += (accY * accY);
      auxRmsZ += (accZ * accZ);
    } else {
      aux2RmsX += (accX * accX);
      aux2RmsY += (accY * accY);
      aux2RmsZ += (accZ * accZ);
    }
    auxTemp += temp;
    count++;
  }

  Serial.print("BUFF Inst AccX = "); Serial.print(accX);
  Serial.print(" | Inst AccY = "); Serial.print(accY);
  Serial.print(" | Inst AccZ = "); Serial.print(accZ);
  Serial.print(" | Max AccX = "); Serial.print(maxAccX);
  Serial.print(" | Max AccY = "); Serial.print(maxAccY);
  Serial.print(" | Max AccZ = "); Serial.print(maxAccZ);
  Serial.print(" | Min AccX = "); Serial.print(minAccX);
  Serial.print(" | Min AccY = "); Serial.print(minAccY);
  Serial.print(" | Min AccZ = "); Serial.print(minAccZ);
  Serial.print(" | RmsX = "); Serial.print(auxRmsX);
  Serial.print(" | RmsY = "); Serial.print(auxRmsY);
  Serial.print(" | RmsZ = "); Serial.print(auxRmsZ);
  Serial.print(" | Rms2X = "); Serial.print(aux2RmsX);
  Serial.print(" | Rms2Y = "); Serial.print(aux2RmsY);
  Serial.print(" | Rms2Z = "); Serial.print(aux2RmsZ);
  Serial.print(" | Tmp = "); Serial.print(auxTemp);
  Serial.println();

  // A cada 1 minuto são enviadas as medidas à rede BLE através do módulo AT-09

  if (count >= 20) {
    accX = maxAccX - minAccX; // pico-a-pico
    accY = maxAccY - minAccY;
    accZ = maxAccZ - minAccZ;
    rmsX = (sqrt(auxRmsX/10) + sqrt(aux2RmsX/10))/2; // RMS
    rmsY = (sqrt(auxRmsY/10) + sqrt(aux2RmsY/10))/2;
    rmsZ = (sqrt(auxRmsZ/10) + sqrt(aux2RmsZ/10))/2;
    temp = auxTemp/20;

    maxAccX = 0;
    maxAccY = 0;
    maxAccZ = 0;
    minAccY = 0;
    minAccY = 0;
    minAccY = 0;
    auxTemp = 0; 
    auxRmsX = 0;
    auxRmsY = 0;
    auxRmsZ = 0;
    aux2RmsX = 0;
    aux2RmsY = 0;
    aux2RmsZ = 0;
    count = 0;

    sendData(accX);
    sendData(accY);
    sendData(accZ);
    sendData(rmsX);
    sendData(rmsY);
    sendData(rmsZ);
    sendData(temp);

//    Serial.print("AcX = "); Serial.print(accX);
//    Serial.print(" | AcY = "); Serial.print(accY);
//    Serial.print(" | AcZ = "); Serial.print(accZ);
//    Serial.print(" | RmsX = "); Serial.print(rmsX);
//    Serial.print(" | RmsY = "); Serial.print(rmsY);
//    Serial.print(" | RmsZ = "); Serial.print(rmsZ);
//    Serial.print(" | Tmp = "); Serial.print(temp);
//    Serial.println();
  }

  delay(3000);
}
