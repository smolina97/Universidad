unsigned int numero;
int unidades;
int decenas;
int centenas;

void setup() {
  DDRC = B00000000;
  DDRA = B11111111;
  DDRK = B11111111;
  DDRF = B11111111;

}

void loop() {
  numero = PINC;
  centenas = numero / 100;
  numero = numero % 100;
  decenas =  numero / 10;
  numero = numero % 10;
  unidades = numero;

  PORTA = unidades;
  PORTF = decenas;
  PORTK = centenas;
}
