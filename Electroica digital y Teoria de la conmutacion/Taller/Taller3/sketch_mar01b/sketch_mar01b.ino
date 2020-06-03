#define Rojo 44
#define Ama  43
#define Verde 42
#define Pulsador 30
#define Mantenimiento 31


void setup() {
  pinMode (Pulsador, INPUT);
  pinMode (Mantenimiento, INPUT);
  pinMode (Rojo, OUTPUT);
  pinMode (Ama, OUTPUT);
  pinMode (Verde, OUTPUT);

  digitalWrite(Rojo, LOW);
  digitalWrite(Ama, LOW);
  digitalWrite(Verde, LOW);

  Serial.begin(9600);
}

void loop() {

  if (digitalRead(Pulsador) == LOW && digitalRead(Mantenimiento) == LOW) {
    delay(1000);
    digitalWrite(Verde, LOW);
    digitalWrite(Rojo, HIGH);
    delay(1000);
    digitalWrite(Rojo, LOW);
    digitalWrite(Ama, HIGH);
    delay(1000);
    digitalWrite(Ama, LOW);
    digitalWrite(Verde, HIGH);
  } else {
    digitalWrite(Rojo, HIGH);
    digitalWrite(Verde, LOW);
    digitalWrite(Ama, LOW);
    delay(1000);
    digitalWrite(Rojo, LOW);
    delay(1000);
  }
}
