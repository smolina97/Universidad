#define uni 30
#define dec 31
#define botonU 34
#define botonD 35
#define boton 37

byte displayY[12] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
byte P[2] = { 62 , 115};
int contador;
int decenas;
int unidades;
int numero;
int avanza;

void setup() {
  DDRL = B11111111;
  pinMode(uni, OUTPUT);
  pinMode(dec, OUTPUT);
  pinMode(boton, INPUT);
  Serial.begin(9600);
}

void loop() {

  if (digitalRead(botonU)) {
    unidades++;
    while (digitalRead(botonU)) {
      for (int i = 0; i < 30; i++) {
        mostrar();
      }
    }
  }

  if (digitalRead(botonD)) {
    decenas++;
    while (digitalRead(botonD)) {
      for (int i = 0; i < 30; i++) {
        mostrar();
      }
    }
  }

  numero = decenas * 10 + unidades;

  if (digitalRead(boton)) {
    contador++;
  }

  while (contador < numero && avanza == 1) {
    for (int i = 0; i < 30; i++) {
      digitalWrite(dec, 0);
      digitalWrite(uni, 0);
      PORTL = displayY[unidades];
      digitalWrite(uni, 1);
      delay(10);
      digitalWrite(uni, 0);
      PORTL = displayY[decenas];
      digitalWrite(dec, 1);
      delay(10);
    }
    contador++;
    decenas = contador / 10;
    unidades = contador % 10;
  }

  if (contador != 0) {

    digitalWrite(dec, 0);
    digitalWrite(uni, 0);
    PORTL = P[0];
    digitalWrite(uni, 1);
    delay(10);
    digitalWrite(uni, 0);
    PORTL = P[1];
    digitalWrite(dec, 1);
    delay(10);
  }
}


void mostrar() {
  digitalWrite(dec, 0);
  digitalWrite(uni, 0);
  PORTL = displayY[unidades];
  digitalWrite(uni, 1);
  delay(10);
  digitalWrite(uni, 0);
  PORTL = displayY[decenas];
  digitalWrite(dec, 1);
  delay(10);
}
