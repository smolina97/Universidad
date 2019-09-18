#define SNH 37
#define SNL 36
#define Start 32
#define Vaciar  23
#define Llenar 22
#define Motor 24
#define Espera 25

int ciclo = 0;

void setup() {
  pinMode (SNH, INPUT);
  pinMode (SNL, INPUT);
  pinMode (Start, INPUT);
  pinMode (Vaciar, OUTPUT);
  pinMode (Llenar, OUTPUT);
  pinMode (Motor, OUTPUT);
  pinMode (Espera, OUTPUT);

  digitalWrite(Motor, LOW);
  digitalWrite(Espera, LOW);
  digitalWrite(Llenar, LOW);
  digitalWrite(Vaciar, LOW);

  Serial.begin(9600);
}

void vatiendo(){
  
}
void llenar() {

  while (digitalRead(SNH) == LOW) {
   Serial.println ("Llenando");
    digitalWrite(Vaciar, LOW);
    digitalWrite(Llenar, HIGH);

    while (ciclo <= 5) {
      Serial.println ("Vatiendo");
      digitalWrite(Motor, LOW);
      delay (1000);
      digitalWrite(Motor, HIGH);
      delay (3000);
      digitalWrite(Motor, LOW);
      Serial.println(ciclo);
      ciclo++;
    }
  }
}

void vaciar() {
  Serial.println ("Vaciando");
  digitalWrite(Llenar, LOW);
  digitalWrite(Vaciar, HIGH);

  while (digitalRead(SNL) == HIGH) {
    digitalWrite(Espera, HIGH);
    Serial.println ("ESPERA");
  }
}
void loop() {

  if (digitalRead(Start) == HIGH) {
    llenar();
    ciclo = 0;
    vaciar();
  }
}
