#define SF 0
#define LR 26
#define LY 27
#define LB 28
#define LG 29

unsigned int medicion = 0;
unsigned int vol = 0;

void setup() {

  pinMode (SF, INPUT);
  pinMode (LR, OUTPUT);
  pinMode (LY, OUTPUT);
  pinMode (LB, OUTPUT);
  pinMode (LG, OUTPUT);

  digitalWrite(LR, LOW);
  digitalWrite(LY, LOW);
  digitalWrite(LB, LOW);
  digitalWrite(LG, LOW);

  Serial.begin(9600);
}

void loop() {

  medicion = analogRead(SF);
  vol = (5 * medicion) / 1023;

  if (medicion >= 0) {
    digitalWrite(LR, HIGH);
  }
  if ( medicion >= 255 && medicion <= 1023) {
    digitalWrite(LY, HIGH);
  } else {
    digitalWrite(LY, LOW);
  }
  if ( medicion >= 510 && medicion <= 1023) {
    digitalWrite(LB, HIGH);
  } else {
    digitalWrite(LB, LOW);
  }
  if ( medicion >= 765 && medicion <= 1023) {
    digitalWrite(LG, HIGH);
  } else {
    digitalWrite(LG, LOW);
  }

  Serial.print("Fuerza: ");
  Serial.print(medicion);
  Serial.print("   Voltage: ");
  Serial.println(vol);
  delay(500);
}
