#define avanza 37
#define retrocede 36


byte displayY[10] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
int contador;
int retro;

void setup() {
  DDRL = B11111111;
  Serial.begin (9600);
}

void loop() {

  while (contador < 9 && digitalRead(avanza)) {

    PORTL = displayY[contador];
    contador ++;
    delay(500);
    while (digitalRead(avanza)) {
    }
  }

if(digitalRead(retrocede)){
  retro = 1;
}

  while (contador > 0 && retro == 1) {

    PORTL = displayY[contador];
    contador --;
    delay(500);
  }
  retro = 0;
  PORTL = displayY[contador];
}
