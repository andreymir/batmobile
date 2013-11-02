#include <Shieldbot.h>

Shieldbot shieldbot = Shieldbot();

void setup() {
  //Serial.begin(9600);
  //shieldbot.setMaxSpeed(255);
  setupBluetooth();
}

void loop() {
  char* command = readCommand();
  
  if (command != NULL) {
    processCommand(command);
  }
}

void drive(byte left, byte right) {
   shieldbot.drive(left,right); 
}
