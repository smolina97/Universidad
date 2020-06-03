//Libreias
//definicion de pines
#define LM35 A2
#define HUM A1
#define LED 6
#define BOM 7

//variables
float temperatura;
float humedad;

void setup() {
  //configuracion de pine
  pinMode(LM35, INPUT);
  pinMode(HUM, INPUT);
  pinMode(LED, OUTPUT);
  pinMode(BOM, OUTPUT);
  digitalWrite(LED, LOW);
  digitalWrite(BOM, LOW);
  Serial.begin(9600);
}

void loop() {

  temperatura = (5 * analogRead(LM35) * 100) / 1023.0; //convertir el valor de temperatura a C
  humedad = 100 - (analogRead(HUM) / 1023.0) * 100;
  if (temperatura <= 30) {
    digitalWrite(LED, HIGH);
  } else{
    digitalWrite(LED, LOW);
  }
  if ( humedad <= 30) {
    digitalWrite(BOM, HIGH);
  } else {
    digitalWrite(BOM, LOW);
  }
}
