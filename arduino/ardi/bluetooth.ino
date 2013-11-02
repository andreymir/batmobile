
#include <SoftwareSerial.h>

SoftwareSerial btSerial(11, 12); // RX, TX

void setupBluetooth() {
  // Open serial communications and wait for port to open:
  btSerial.begin(9600);
}

void loopBluetooth() {
  if (btSerial.available())
    Serial.write(btSerial.read());
  if (Serial.available())
    btSerial.write(Serial.read());
}
