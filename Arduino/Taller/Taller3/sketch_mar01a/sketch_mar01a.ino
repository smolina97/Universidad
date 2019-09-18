#define Motor1 27
#define Motor2 28
#define Motor3 29
#define Inicio 37
#define Detener 36



void setup() {

  pinMode (Inicio, INPUT);
  pinMode (Detener, INPUT);
  pinMode (Motor1, OUTPUT);
  pinMode (Motor2, OUTPUT);
  pinMode (Motor3, OUTPUT);

  digitalWrite(Motor1, LOW);
  digitalWrite(Motor2, LOW);
  digitalWrite(Motor3, LOW);

  Serial.begin(9600);
}

void loop() {

  while (digitalRead (Inicio) == HIGH) {
    digitalWrite(Motor1, HIGH);
    delay (1000);
    digitalWrite(Motor2, HIGH);
    delay (1000);
    digitalWrite(Motor3, HIGH);
  }
  while (digitalRead (Detener) == HIGH) {
    digitalWrite(Motor3, LOW);
    delay (1000);
    digitalWrite(Motor2, LOW);
    delay (1000);
    digitalWrite(Motor1, LOW);
  }

}
