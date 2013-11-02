#include <Shieldbot.h>
#include "custom_types.h"

#define LEFT_GREEN_LED 2
#define RIGHT_GREEN_LED 1
#define RIGHT_RED_LED 0
#define LEFT_RED_LED 3

Shieldbot shieldbot = Shieldbot();
int S1,S2,S3,S4,S5;	//values to store state of sensors
double distance;
Mode mode;
boolean protectedState;

void setup() {
  Serial.begin(9600);

  setupBluetooth();
  //setupBot(Parktronic);

  //pinMode(LEFT_GREEN_LED, OUTPUT); 
  //pinMode(RIGHT_GREEN_LED, OUTPUT); 
  //pinMode(RIGHT_RED_LED, OUTPUT); 
  //pinMode(LEFT_RED_LED, OUTPUT); 
  
  //setupBip();
}

void setupBot(Mode m) {
  mode = m;
  protectedState = false;
  
  if (m == Idle) {
    stop();
  }
}

void loop() {
  updateSensors();
  
  if (mode == Parktronic) {
    checkBorder();
  }
  
  char* command = readCommand();
  if (command != NULL) {
    processCommand(command);
    delete[] command;
  }
}

void stop() {
  shieldbot.drive(0, 0);
}

void drive(char left, char right) {
  if (protectedState && mode == Parktronic) {
    Serial.println("In protected state");
    if (left > 0 || right > 0) {
      return;
    } else {
      protectedState = false;
    }
  }
  Serial.println("Drive shielbot");
  shieldbot.drive(left,right);
  //setLEDs(left, right);
}

void checkBorder() {
  if (!protectedState) {
    if (S2 == LOW || S3 == LOW || S4 == LOW) {
      Serial.print(S1);
      Serial.print(S2);
      Serial.print(S3);
      Serial.print(S4);
      Serial.print(S5);
      Serial.println("Set protected state");
      drive(-50, -50);
      delay(100);
      stop();
      protectedState = true;
    }
  }
}

void driveInLineFollowingMode() {
  shieldbot.setMaxSpeed(50,50);//255 is max, if one motor is faster than another, adjust values
  
  readLineSensors();
  
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

void initProtectedMode() {
  protectedState = false;
}

void driveInProtectedMode(byte left, byte right) {
  readLineSensors();
  
  if (!protectedState) {
    drive(left, right);
    
    if (
      (S3 == HIGH && S1 == LOW && S2 == LOW && S4 == LOW && S5 == LOW) ||
      (S3 == LOW && S1 == HIGH && S2 == HIGH && S4 == HIGH && S5 == HIGH) ||
      (S1 == HIGH && S2 == LOW && S3 == LOW && S4 == LOW && S5 == HIGH) ||
      (S1 == LOW && S2 == HIGH && S3 == HIGH && S4 == HIGH && S5 == LOW) ||
      (S3 == HIGH && S4 == HIGH && S1 == LOW && S2 == LOW && S5 == LOW) ||
      (S3 == LOW && S4 == LOW && S1 == HIGH && S2 == HIGH && S5 == HIGH) ||
      (S3 == HIGH && S2 == HIGH && S1 == LOW && S4 == LOW && S5 == LOW) ||
      (S3 == LOW && S2 == LOW && S1 == HIGH && S4 == HIGH && S5 == HIGH) ||
      (S3 == HIGH && S2 == HIGH && S1 == LOW && S4 == HIGH && S5 == HIGH) ||
      (S3 == HIGH && S2 == HIGH && S1 == HIGH && S4 == HIGH && S5 == LOW) ||
      (S1 == LOW && S2 == LOW && S3 == LOW && S4 == HIGH && S5 == HIGH) ||
      (S1 == HIGH && S2 == HIGH && S3 == HIGH && S4 == LOW && S5 == LOW) ||
      (S3 == HIGH && ((S1 == LOW && S2 == LOW) || (S4 == LOW && S5 == LOW))) ||
      (S3 == LOW && ((S1 == HIGH && S2 == HIGH) || (S4 == HIGH && S5 == HIGH)))
     ) {
     shieldbot.backward();
     delay(50);
     shieldbot.stop();
     protectedState = true;
   }
  } 
  else if (left <= 0 || right <= 0) {
    protectedState = false;
  }
}

void readLineSensors(){
   //Read all the sensors 
  S1 = shieldbot.readS1();
  S2 = shieldbot.readS2();
  S3 = shieldbot.readS3();
  S4 = shieldbot.readS4();
  S5 = shieldbot.readS5(); 
}

void setLEDs(byte left, byte right) {
  if (left > 0) {
    digitalWrite(LEFT_GREEN_LED, HIGH);
    digitalWrite(LEFT_RED_LED, LOW);
  } else {
    digitalWrite(LEFT_GREEN_LED, LOW);
    digitalWrite(LEFT_RED_LED, HIGH);
  }
  
  if (right > 0) {
    digitalWrite(RIGHT_GREEN_LED, HIGH);
    digitalWrite(RIGHT_RED_LED, LOW);
  } else {
    digitalWrite(RIGHT_GREEN_LED, LOW);
    digitalWrite(RIGHT_RED_LED, HIGH);
  }
}
