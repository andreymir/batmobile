#include <Shieldbot.h>

Shieldbot shieldbot = Shieldbot();
int S1,S2,S3,S4,S5;	//values to store state of sensors

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

void lineFollowingMode() {
  shieldbot.setMaxSpeed(50,50);//255 is max, if one motor is faster than another, adjust values
  
  //Read all the sensors 
  S1 = shieldbot.readS1();
  S2 = shieldbot.readS2();
  S3 = shieldbot.readS3();
  S4 = shieldbot.readS4();
  S5 = shieldbot.readS5();
  
  if(S1 == HIGH && S5 == HIGH){	//if the two outer IR line sensors see background, go forward
    shieldbot.forward(); 
  }else if(S1 == LOW && S5 == LOW){	//if either of the two outer IR line sensors see empty space (like edge of a table) stop moving
    shieldbot.stop();
    delay(100);
  }else if((S1 == LOW) || (S2 == LOW)){	//if the two most right IR line sensors see black tape, turn right
    shieldbot.drive(127,-128);// to turn right, left motor goes forward and right motor backward
    delay(100);
  }else if((S5 == LOW) || (S4 == LOW)){	//if either of the two most left IR line sensors see black , turn left
    shieldbot.drive(-128,127);// to turn right, left motor goes backward and right motor forward
    delay(100);
  }else	//otherwise just go forward
  shieldbot.forward();
}
