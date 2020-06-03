#define uni 30
#define dec 31
#define boton 34

byte displayY[10] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
int contador;
int decenas;
int unidades;
int retrocede;

void setup() {
  DDRL = B11111111;
  pinMode(uni, OUTPUT);
  pinMode(dec, OUTPUT);
  pinMode(boton, INPUT);
  Serial.begin(9600);
}

void loop() {

  if (digitalRead(boton)) {
    retrocede = 1;
  }

  if (retrocede == 0) {
    while (contador < 99) {
      for (int i = 0; i < 30; i++) {
        mostrar();
      }
      contador++;
      decenas = contador / 10;
      unidades = contador % 10;
    }
  } else {

    while (contador > 0) {
      for (int i = 0; i < 30; i++) {
        mostrar();
      }
      contador--;
      decenas = contador / 10;
      unidades = contador % 10;
    }
  }
  retrocede = 0;
}



void mostrar() {
  Serial.println(retrocede);
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
