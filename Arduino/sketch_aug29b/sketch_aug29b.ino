byte displayY[10] = { 63, 6, 91 , 79 , 102 , 109 , 125 , 7 , 127 , 103};
int contador = 0;

void setup() {
  DDRL = B11111111;
}

void loop() {
 while (contador < 10){ 
    delay(1000);
    PORTL = displayY[contador];
    contador ++;
  }
 contador = 0;
}
