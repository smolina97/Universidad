#define POT 0
#define POT1 1

void setup() {
  pinMode(POT, INPUT);
  pinMode(POT1, INPUT);
  Serial.begin(9600);
}

void loop() {
  Serial.print("POT ");
  Serial.println(analogRead(POT));
  delay(100);

}
