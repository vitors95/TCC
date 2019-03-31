#include<Wire.h>
#include<SoftwareSerial.h>

SoftwareSerial mySerial(3, 2); // RX, TX

const int MPU_addr = 0x68; // Endereço I2C do MPU6050

signed long accx, accy, accz, temp;
signed long auxx, auxy, auxz, auxt = 0;
int count = 0;
byte *p;

void sendData(signed long data) {
  p = (byte*)&data;
  mySerial.write(p, 2);
}

void setMPU() {
  // power management
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);

  // configure gyro
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x1B);                           // Access the gyro configuration register
  Wire.write(0);
  Wire.endTransmission(true);

  // configure accelerometer
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x1C);                           // Access the accelerometer configuration register
  Wire.write(0);
  Wire.endTransmission(true);

  Wire.beginTransmission(MPU_addr);
  Wire.write(0x19);
  Wire.write(7);
  Wire.endTransmission(true);
}

void setup() {
  Serial.begin(9600);
  mySerial.begin(9600);
  Wire.begin();
  setMPU();
}

void loop() {
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 6, true);  // request a total of 6 registers
  
  accx = Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)  
  accy = Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  accz = Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)

  Wire.beginTransmission(MPU_addr);
  Wire.write(0x41);  // starting with register 0x41 (TEMP_OUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true);  // request a total of 2 registers
  
  temp = Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)

  delay(1000);

  if (accx == 0 && accy == 0 && accz == 0) {
    setMPU();
  } else {
    auxx += accx;
    auxy += accy;
    auxz += accz;
    auxt += temp;
    count++;
  }

//  Serial.print("BUFF AcX = "); Serial.print(auxx);
//  Serial.print(" | AcY = "); Serial.print(auxy);
//  Serial.print(" | AcZ = "); Serial.print(auxz);
//  Serial.print(" | Tmp = "); Serial.print(auxt);
//  Serial.println();

  if (count >= 10) {
    accx = auxx/10;
    accy = auxy/10;
    accz = auxz/10;
    temp = auxt/10;

    auxx = 0;
    auxy = 0;
    auxz = 0;
    auxt = 0;
    count = 0;

    sendData(accx);
    sendData(accy);
    sendData(accz);
    sendData(temp);

//    Serial.print("AcX = "); Serial.print(accx);
//    Serial.print(" | AcY = "); Serial.print(accy);
//    Serial.print(" | AcZ = "); Serial.print(accz);
//    Serial.print(" | Tmp = "); Serial.print(temp);
//    Serial.println();
  }

  delay(5000);
}
