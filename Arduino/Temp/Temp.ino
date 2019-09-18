#define TEM 0

float Temperatura;

void setup() {
  pinMode(TEM, INPUT);
  Serial.begin(9600);
}

void loop() {

  Temperatura = ( 5.0 * analogRead(TEM)) * 100 / 1023 ;
  Serial.println(Temperatura);
  delay(100);
}
