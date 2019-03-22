#include<Wire.h>
#include<SoftwareSerial.h>

SoftwareSerial mySerial(3, 2); // RX, TX

const int MPU_addr = 0x68; // Endereço I2C do MPU6050

int accx, accy, accz, girx, giry, girz, temp;

void sendData(int data) {
  Serial.println(data, HEX);
  mySerial.write(data);
}

void setup() {
  Serial.begin(9600);
  mySerial.begin(9600);

  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
}

void loop() {
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);  // request a total of 14 registers
  
  accx = Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)  
  accy = Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  accz = Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
  girx = Wire.read()<<8|Wire.read();  // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  giry = Wire.read()<<8|Wire.read();  // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  girz = Wire.read()<<8|Wire.read();  // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)
  temp = Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)

//  Serial.print("AcX = "); Serial.print(accx);
//  Serial.print(" | AcY = "); Serial.print(accy);
//  Serial.print(" | AcZ = "); Serial.print(accz);
//  Serial.print(" | Tmp = "); Serial.print(temp/340.00+36.53);
//  Serial.print(" | GyX = "); Serial.print(girx);
//  Serial.print(" | GyY = "); Serial.print(giry);
//  Serial.print(" | GyZ = "); Serial.println(girz);

  delay(1000);

  if (accx == 0 && accy == 0 && accz == 0) {
    Wire.begin();
    Wire.beginTransmission(MPU_addr);
    Wire.write(0x6B);
    Wire.write(0);
    Wire.endTransmission(true);
  } else {
    sendData((int) accx);
    sendData((int) accy);
    sendData((int) accz);
    sendData((int) girx);
    sendData((int) giry);
    sendData((int) girz);
    sendData((int) temp);  

  }

  delay(59000);
}
