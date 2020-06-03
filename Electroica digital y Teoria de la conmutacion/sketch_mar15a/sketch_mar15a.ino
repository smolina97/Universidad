#define pA A0
#define pB A1
#define pC A2
#define pD A3
#define pE A4
#define pF A5
#define pG A6
#define sensor 29
#define G 2
#define R 3
#define B 4
//Definicion de variables
unsigned int cont = 0;
unsigned int TEMP = 0;
void int2bcd(unsigned int num) //Función que toma un numero entero y lo convierte a BCD
{
  switch (num)
  {
    case 0:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, LOW);
      break;
    case 1:
      digitalWrite(pA, LOW);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, LOW);
      digitalWrite(pE, LOW);
      digitalWrite(pF, LOW);
      digitalWrite(pG, LOW);
      break;
    case 2:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, LOW);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, LOW);
      digitalWrite(pG, HIGH);
      break;
    case 3:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, LOW);
      digitalWrite(pF, LOW);
      digitalWrite(pG, HIGH);
      break;
    case 4:
      digitalWrite(pA, LOW);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, LOW);
      digitalWrite(pE, LOW);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      break;
    case 5:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, LOW);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, LOW);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      break;
    case 6:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, LOW);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      break;
    case 7:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, LOW);
      digitalWrite(pE, LOW);
      digitalWrite(pF, LOW);
      digitalWrite(pG, LOW);
      break;
    case 8:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      break;
    case 9:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, LOW);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      break;
  }
}
void setup()
{
  pinMode(pA, OUTPUT);
  pinMode(pB, OUTPUT);
  pinMode(pC, OUTPUT);
  pinMode(pD, OUTPUT);
  pinMode(pE, OUTPUT);
  pinMode(pF, OUTPUT);
  pinMode(pG, OUTPUT);
  pinMode(G, OUTPUT);
  pinMode(R, OUTPUT);
  pinMode(B, OUTPUT);
  pinMode(sensor, INPUT);
  digitalWrite(pA, LOW);
  digitalWrite(pB, LOW);
  digitalWrite(pC, LOW);
  digitalWrite(pD, LOW);
  digitalWrite(pE, LOW);
  digitalWrite(pF, LOW);
  digitalWrite(pG, LOW);
  digitalWrite(G, LOW);
  digitalWrite(R, LOW);
  digitalWrite(B, LOW);
  Serial.begin(9600);
}
void loop()
{
  int2bcd(cont);

  TEMP = ( 5 * analogRead(sensor) * 100) / 1023;

  cont = TEMP % 10;

  if (TEMP <= 28) {
    analogWrite(R, 0);
    analogWrite(G, 0);
    analogWrite(B, 255);
    Serial.println("amarillo");
    delay(1000);
  } else if (TEMP >= 29 && TEMP <= 32) {
    analogWrite(R, 0);
    analogWrite(G, 0);
    analogWrite(B,255);
    Serial.println("Naranja");
    delay(1000);
  } else if (TEMP >= 33) {
    analogWrite(R, 0);
    analogWrite(G, 0);
    analogWrite(B, 255);
    Serial.println("Rojo");
    delay(1000);
  }

  Serial.println(TEMP);
  // Serial.println(analogRead(sensor));
  delay(1000);

}
