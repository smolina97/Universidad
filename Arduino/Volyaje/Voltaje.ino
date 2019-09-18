#define POT 0

float voltaje;

void setup() {
  pinMode(POT, INPUT);
  Serial.begin(9600);
}

void loop() {
  voltaje = analogRead(POT) * 5.0 /1023.0;
  Serial.println(voltaje);
}
