/*
Example from Arduino SoftwareSerial tutorial
 */
#include <SoftwareSerial.h>

SoftwareSerial btSerial(11, 12); // RX, TX

void setupBluetooth()  
{
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  btSerial.begin(9600);
}

void loopBluetooth() // run over and over
{
  if (btSerial.available())
    Serial.write(btSerial.read());
  if (Serial.available())
    btSerial.write(Serial.read());
}
