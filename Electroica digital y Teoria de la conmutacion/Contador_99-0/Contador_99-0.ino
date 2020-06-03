#define uni 30
#define dec 31
#define botonU 34
#define botonD 35
#define boton 36

byte displayY[10] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
int contador = 0;
int decenas = 0;
int unidades = 0;
int retroceder;

void setup() {
  DDRL = B11111111;
  pinMode(uni, OUTPUT);
  pinMode(dec, OUTPUT);
  pinMode(boton, INPUT);
  Serial.begin(9600);
}

void loop() {
  if (digitalRead(botonU)) {
    contador++;
    decenas = contador / 10;
    unidades = contador % 10;
    while (digitalRead(botonU)) {
      for (int i = 0; i < 30; i++) {
        mostrar();
      }
    }
  }

  if (digitalRead(botonD)) {
    contador--;
    decenas = contador / 10;
    unidades = contador % 10;
    while (digitalRead(botonD)) {
      for (int i = 0; i < 30; i++) {
        mostrar();
      }
    }
  }
  mostrar();

if(digitalRead(boton)){
  retroceder = 1;
}

  while (contador > 0 && retroceder ==1) {
    for (int i = 0; i < 30; i++) {
      mostrar();
    }
    contador--;
    decenas = contador / 10;
    unidades = contador % 10;
  }
  retroceder = 0;
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
