#define uni 30
#define dec 31
#define boton 34

byte displayY[10] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
int contador = 0;
int decenas = 0;
int unidades = 0;

void setup() {
  DDRL = B11111111;
  pinMode(uni, OUTPUT);
  pinMode(dec, OUTPUT);
  pinMode(boton, INPUT);
  Serial.begin(9600);
}

void loop() {
  while (contador < 99 && !digitalRead(boton)) {
    for (int i = 0; i < 30; i++) {
      mostrar();
    }
    contador++;
    decenas = contador / 10;
    unidades = contador % 10;
  }

  while (contador > 0 && digitalRead(boton)) {
    for (int i = 0; i < 30; i++) {
      mostrar();
    }
    contador--;
    decenas = contador / 10;
    unidades = contador % 10;
  }

  mostrar();
}



void mostrar() {
  Serial.println(contador);
  Serial.println(unidades);
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
