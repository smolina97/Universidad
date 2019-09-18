#define Rojo 27
#define Amarillo 28
#define Verde 29
#define PEAR 31
#define PEAV 32
#define boton 37

int estado = 0;
int tiempo = 0;
int tiempo2 = 0;
long Tactual = 0;
long Tinicial = 0;
int cambio = 15000;
int activo = 0;

void setup() {

  pinMode (Rojo, OUTPUT);
  pinMode (Amarillo, OUTPUT);
  pinMode (Verde, OUTPUT);
  pinMode (PEAR, OUTPUT);
  pinMode (PEAV, OUTPUT);
  pinMode (boton, INPUT);
  digitalWrite (Rojo, 0);
  digitalWrite (Amarillo, 0);
  digitalWrite (Verde, 0);
  digitalWrite (PEAR, 0);
  digitalWrite (PEAV, 0);

  Serial.begin(9600);
}

void loop() {

  Tactual = millis();
  delay(600);
  digitalWrite (Rojo, 0);
  digitalWrite (Amarillo, 0);
  digitalWrite (Verde, 1);
  digitalWrite (PEAR, 1);
  digitalWrite (PEAV, 0);
  delay(500);
  Serial.println(Tactual - Tinicial);
  Serial.println(Tinicial);

  if (digitalRead(boton) == 1) {
    activo = 1;
  }

  if ( activo == 1) {
    Serial.println("espera");
    sem();
  }
}


void sem() {

  if (Tactual - Tinicial >= cambio) {
    while (tiempo < 3) {
      delay(600);
      digitalWrite (Rojo, 0);
      digitalWrite (Amarillo, 0);
      digitalWrite (Verde, 1);
      digitalWrite (PEAR, 1);
      digitalWrite (PEAV, 0);
      delay(500);
      tiempo++;
    }
    tiempo = 0;
    while (tiempo < 3) {
      delay(500);
      digitalWrite (Rojo, 0);
      digitalWrite (Amarillo, 1);
      digitalWrite (Verde, 0);
      digitalWrite (PEAR, 1);
      digitalWrite (PEAV, 0);
      delay(500);
      tiempo++;
    }
    tiempo = 0;
    while (tiempo < 2) {
      digitalWrite (Rojo, 1);
      digitalWrite (Amarillo, 0);
      digitalWrite (Verde, 0);
      digitalWrite (PEAR, 1);
      digitalWrite (PEAV, 0);
      delay(500);
      tiempo++;
    }
    tiempo = 0;
    while (tiempo < 3) {
      delay(500);
      digitalWrite (Rojo, 1);
      digitalWrite (Amarillo, 0);
      digitalWrite (Verde, 0);
      digitalWrite (PEAR, 0);
      digitalWrite (PEAV, 1);
      delay(500);
      tiempo++;
    }

    tiempo = 0;

    while (tiempo2 < 3) {
      delay(500);
      digitalWrite (PEAV, 1);
      delay(500);
      digitalWrite (PEAV, 0);
      delay(500);
      tiempo2++;
    }
    digitalWrite (Rojo, 1);
    digitalWrite (PEAR, 1);
    delay(600);
    tiempo = 0;
    tiempo2 = 0;
    Tinicial = Tactual + 15401;
    activo = 0;
  }
}
