//#include<Wire.h>

const int MPU_addr = 0x68; // Endereço I2C do MPU6050

int accx, accy, accz, girx, giry, girz, temp;

void sendData(int data) {
  Serial.println(data, HEX);
  Serial1.write(data);
}

void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);

//  Wire.begin();
//  Wire.beginTransmission(MPU_addr);
//  Wire.write(0x6B);
//  Wire.write(0);
//  Wire.endTransmission(true);
}

void loop() {
//  Wire.beginTransmission(MPU_addr);
//  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
//  Wire.endTransmission(false);
//  Wire.requestFrom(MPU_addr,14,true);  // request a total of 14 registers
//  
//  accx = Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)  
//  Serial.println(accx);   
//  accy = Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
//  Serial.println(accy);   
//  accz = Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
//  Serial.println(accz);   
//  girx = Wire.read()<<8|Wire.read();  // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
//  Serial.println(girx);   
//  giry = Wire.read()<<8|Wire.read();  // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
//  Serial.println(giry);   
//  girz = Wire.read()<<8|Wire.read();  // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)
//  Serial.println(girz);   
//  temp = Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)
//  Serial.println(temp);   

  accx = random(1, 100);
  accx = 99;
  accy = random(1, 100);
  accz = random(1, 100);
  girx = random(1, 100);
  giry = random(1, 100);
  girz = random(1, 100);
  temp = random(1, 100);

  sendData((int) accx);
  sendData((int) accy);
  sendData((int) accz);
  sendData((int) girx);
  sendData((int) giry);
  sendData((int) girz);
  sendData((int) temp);  

  delay(10000);
}
