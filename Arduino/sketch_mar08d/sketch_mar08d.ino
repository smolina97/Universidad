//Definicion de pines de I/O
#define PUL 37
#define pA 29
#define pB 28
#define pC 27
#define pD 26
#define pE 25
#define pF 24
#define pG 23
#define LR 3
#define LG 2
#define LB 4

unsigned int cont = 0;
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
      analogWrite(LR, 50);
      analogWrite(LB, 0);
      analogWrite(LG, 0);
      break;
    case 1:
      digitalWrite(pA, LOW);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, LOW);
      digitalWrite(pE, LOW);
      digitalWrite(pF, LOW);
      digitalWrite(pG, LOW);
      analogWrite(LR, 0);
      analogWrite(LB, 0);
      analogWrite(LG, 50);
      break;
    case 2:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, LOW);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, LOW);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 0);
      analogWrite(LB, 50);
      analogWrite(LG, 0);
      break;
    case 3:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, LOW);
      digitalWrite(pF, LOW);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 150);
      analogWrite(LB, 150);
      analogWrite(LG, 0);
      break;
    case 4:
      digitalWrite(pA, LOW);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, LOW);
      digitalWrite(pE, LOW);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 150);
      analogWrite(LB, 150);
      analogWrite(LG, 150);
      break;
    case 5:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, LOW);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, LOW);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 255);
      analogWrite(LB, 0);
      analogWrite(LG, 0);
      break;
    case 6:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, LOW);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 0);
      analogWrite(LB, 255);
      analogWrite(LG, 0);
      break;
    case 7:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, LOW);
      digitalWrite(pE, LOW);
      digitalWrite(pF, LOW);
      digitalWrite(pG, LOW);
      analogWrite(LR, 0);
      analogWrite(LB, 0);
      analogWrite(LG, 255);
      break;
    case 8:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, HIGH);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 255);
      analogWrite(LB, 255);
      analogWrite(LG, 255);
      break;
    case 9:
      digitalWrite(pA, HIGH);
      digitalWrite(pB, HIGH);
      digitalWrite(pC, HIGH);
      digitalWrite(pD, HIGH);
      digitalWrite(pE, LOW);
      digitalWrite(pF, HIGH);
      digitalWrite(pG, HIGH);
      analogWrite(LR, 0);
      analogWrite(LB, 0);
      analogWrite(LG, 0);
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
  pinMode(LR, OUTPUT);
  pinMode(LG, OUTPUT);
  pinMode(LB, OUTPUT);

  digitalWrite(pA, LOW);
  digitalWrite(pB, LOW);
  digitalWrite(pC, LOW);
  digitalWrite(pD, LOW);
  digitalWrite(pE, LOW);
  digitalWrite(pF, LOW);
  digitalWrite(pG, LOW);
  digitalWrite(LR, LOW);
  digitalWrite(LG, LOW);
  digitalWrite(LB, LOW);
  Serial.begin(9600);
}
void loop()
{
  int2bcd(cont); //Convierto el contador a BCD y lo muestro en el display de 7 segmentos
  if (digitalRead(PUL) == HIGH){
    delay(500);
    cont = cont + 1;
  }

  if (cont > 9) {
    cont = 0;
  }

  Serial.println(cont);
  delay(1000);
}
