#define Mot 3
#define POT 0


float voltaje;
void setup() {
  pinMode(POT, INPUT);
  pinMode(Mot, OUTPUT);
}

void loop() {
  voltaje = analogRead(POT);
  analogWrite(Mot,voltaje / 4);
}
