#include<Wire.h>
#include<SoftwareSerial.h>

SoftwareSerial mySerial(3, 2); // RX, TX

const int MPU_addr = 0x68; // Endereço I2C do MPU6050

signed long accX, accY, accZ, temp;
signed long rmsX, rmsY, rmsZ;
signed long auxAccX, auxAccY, auxAccZ, auxTemp = 0;
unsigned long auxRmsX, auxRmsY, auxRmsZ = 0;
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

   // configure accelerometer
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x1C);                           // Access the accelerometer configuration register
  Wire.write(0);
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
  Wire.requestFrom(MPU_addr, 8, true);  // request a total of 8 registers
  
  accX = Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)  
  accY = Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  accZ = Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)  
  temp = Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)

  delay(1000);

  if (accX == 0 && accY == 0 && accZ == 0) {
    setMPU();
  } else {
    auxAccX += accX;
    auxAccY += accY;
    auxAccZ += accZ;
    auxRmsX += (accX * accX);
    auxRmsY += (accY * accY);
    auxRmsZ += (accZ * accZ);
    auxTemp += temp;
    count++;
  }

//  Serial.print("BUFF AcX = "); Serial.print(auxAccX);
//  Serial.print(" | AcY = "); Serial.print(auxAccY);
//  Serial.print(" | AcZ = "); Serial.print(auxAccZ);
//  Serial.print(" | RmsX = "); Serial.print(auxRmsX);
//  Serial.print(" | RmsY = "); Serial.print(auxRmsY);
//  Serial.print(" | RmsZ = "); Serial.print(auxRmsZ);
//  Serial.print(" | Tmp = "); Serial.print(auxTemp);
//  Serial.println();

  if (count >= 10) {
    accX = auxAccX/10;
    accY = auxAccY/10;
    accZ = auxAccZ/10;
    rmsX = sqrt(auxRmsX/10);
    rmsY = sqrt(auxRmsY/10);
    rmsZ = sqrt(auxRmsZ/10);
    temp = auxTemp/10;

    auxAccX = 0;
    auxAccY = 0;
    auxAccZ = 0;
    auxTemp = 0;
    auxRmsX = 0;
    auxRmsY = 0;
    auxRmsZ = 0;
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

  delay(5000);
}
