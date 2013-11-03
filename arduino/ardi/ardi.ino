#include <Shieldbot.h>
#include "custom_types.h"

#define LEFT_GREEN_LED 2
#define RIGHT_GREEN_LED 1
#define RIGHT_RED_LED 0
#define LEFT_RED_LED 3

const double critical_distance = 10;

Shieldbot shieldbot = Shieldbot();
int S1,S2,S3,S4,S5;	//values to store state of sensors
double distance;
Mode mode;
boolean protectedState;

void setup() {
  Serial.begin(9600);

  setupBluetooth();
  setupBot(Idle);
  setupBip();

  //pinMode(LEFT_GREEN_LED, OUTPUT); 
  //pinMode(RIGHT_GREEN_LED, OUTPUT); 
  //pinMode(RIGHT_RED_LED, OUTPUT); 
  //pinMode(LEFT_RED_LED, OUTPUT);
}

void setupBot(Mode m) {
  mode = m;
  protectedState = false;

  if (mode == FollowLine) {
    shieldbot.setMaxSpeed(50,50);//255 is max, if one motor is faster than another, adjust values
  }
  else if (mode == Protected) {
    shieldbot.setMaxSpeed(80,80);
  }
  else {
    shieldbot.setMaxSpeed(128,128);
  }

  if (m == Idle) {
    stop();
  }
}

void loop() {
  updateSensors();

  if (mode == Protected) {
    checkBorder();
    checkSonar();
  }
  else if (mode == FollowLine) {
    driveInLineFollowingMode(); 
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
  Serial.println("Drive shielbot");
  shieldbot.drive(left,right);
  //setLEDs(left, right);
}

void checkBorder() {
  if (S1 == LOW || S2 == LOW || S3 == LOW || S4 == LOW || S5 == LOW) {
    Serial.print(S1);
    Serial.print(S2);
    Serial.print(S3);
    Serial.print(S4);
    Serial.print(S5);
    Serial.println("Set protected state");
    drive(-127, -127);
    delay(100);
    stop();
  }
}

void checkSonar() {
  if (distance < critical_distance) {
    drive(-127, -127);
    delay(100);
    stop();
  }
}

void driveInLineFollowingMode() {

  if(S1 == HIGH && S5 == HIGH){	//if the two outer IR line sensors see background, go forward
    shieldbot.forward(); 
  }
  else if(S1 == LOW && S5 == LOW){	//if either of the two outer IR line sensors see empty space (like edge of a table) stop moving
    shieldbot.stop();
    delay(100);
  }
  else if((S1 == LOW) || (S2 == LOW)){	//if the two most right IR line sensors see black tape, turn right
    shieldbot.drive(127,-128);// to turn right, left motor goes forward and right motor backward
    delay(100);
  }
  else if((S5 == LOW) || (S4 == LOW)){	//if either of the two most left IR line sensors see black , turn left
    shieldbot.drive(-128,127);// to turn right, left motor goes backward and right motor forward
    delay(100);
  }
  else	//otherwise just go forward
  shieldbot.forward();
}

void setLEDs(byte left, byte right) {
  if (left > 0) {
    digitalWrite(LEFT_GREEN_LED, HIGH);
    digitalWrite(LEFT_RED_LED, LOW);
  } 
  else {
    digitalWrite(LEFT_GREEN_LED, LOW);
    digitalWrite(LEFT_RED_LED, HIGH);
  }

  if (right > 0) {
    digitalWrite(RIGHT_GREEN_LED, HIGH);
    digitalWrite(RIGHT_RED_LED, LOW);
  } 
  else {
    digitalWrite(RIGHT_GREEN_LED, LOW);
    digitalWrite(RIGHT_RED_LED, HIGH);
  }
}

