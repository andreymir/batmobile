#include <NewPing.h>

#define TRIGGER_PIN  3  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     2  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

const unsigned long sonarDelay = 200;

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.

void updateSensors() {
  double d = getDistance();
  if (d > 0) {
    distance = d;
  }
  
  S1 = shieldbot.readS1();
  S2 = shieldbot.readS2();
  S3 = shieldbot.readS3();
  S4 = shieldbot.readS4();
  S5 = shieldbot.readS5();
}

double getDistance() {
  static unsigned long time = 0;
  if (millis() - time < sonarDelay) {
    return -1; //  Too soon
  }
  
  unsigned int uS = sonar.ping();
  time = millis();
  
  Serial.print("Sonar: ");
  Serial.println(uS / US_ROUNDTRIP_CM);
  
  return uS / US_ROUNDTRIP_CM;
}
