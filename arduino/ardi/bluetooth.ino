/*
Example from Arduino SoftwareSerial tutorial
 */
#include <SoftwareSerial.h>

SoftwareSerial mySerial(11, 12); // RX, TX

void setupBluetooth()  
{
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  mySerial.begin(9600);
}

void loopBluetooth() // run over and over
{
  if (mySerial.available())
    Serial.write(mySerial.read());
  if (Serial.available())
    mySerial.write(Serial.read());
}
