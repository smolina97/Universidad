#define uni 30
#define dec 31
#define botonU 34
#define botonD 35
#define boton 37

byte displayY[10] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
int contador;
int decenas;
int unidades;
int numero;

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
    for (int i = 0; i < 30; i++) {
      mostrar();
    }
    while (digitalRead(botonD)) {
    }
  }
  numero = decenas * 10 + unidades;
  mostrar();

  while (contador < numero && digitalRead(boton)) {
    for (int i = 0; i < 30; i++) {
      mostrar();
    }
    contador++;
    decenas = contador / 10;
    unidades = contador % 10;
  }

  if (contador == numero && !digitalRead(boton)) {
    unidades = 0;
    decenas = 0;
    contador = 0;
  }
  mostrar();
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
