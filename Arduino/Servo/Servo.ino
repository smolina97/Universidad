#include <Servo.h>
#define POT 0
Servo servo;

int val;

void setup() {
  servo.attach(2);
}

void loop() {
  val = analogRead(POT);
  val = map(val, 0, 1023, 0, 180);
  servo.write(val);
  delay(15);
}
