#include <Shieldbot.h>

#define LEFT_GREEN_LED 2
#define RIGHT_GREEN_LED 1
#define RIGHT_RED_LED 0
#define LEFT_RED_LED 3

enum Mode {
  Idle,
  Manual,
  FollowLine,
  Parktronic
};

Shieldbot shieldbot = Shieldbot();
int S1,S2,S3,S4,S5;	//values to store state of sensors
double distance;
Mode mode;

void setup() {
  Serial.begin(9600);

  setupBluetooth();
  
  mode = Manual;

  pinMode(LEFT_GREEN_LED, OUTPUT); 
  pinMode(RIGHT_GREEN_LED, OUTPUT); 
  pinMode(RIGHT_RED_LED, OUTPUT); 
  pinMode(LEFT_RED_LED, OUTPUT); 
}

void loop() {
  updateSensors();
  
  char* command = readCommand();
  
  if (command != NULL) {
    processCommand(command);
    delete[] command;
  }
}

void drive(byte left, byte right) {
   shieldbot.drive(left,right);
   
   setLEDs(left, right);
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

boolean protectedState;

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
