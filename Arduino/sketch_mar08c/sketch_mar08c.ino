//Definicion de pines de I/O
#define pA 2
#define pB 3
#define pC 4
#define pD 5
#define pE 6
#define pF 7
#define pG 8
//Definicion de variables
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
  digitalWrite(pA, LOW);
  digitalWrite(pB, LOW);
  digitalWrite(pC, LOW);
  digitalWrite(pD, LOW);
  digitalWrite(pE, LOW);
  digitalWrite(pF, LOW);
  digitalWrite(pG, LOW);
  Serial.begin(9600);
}
void loop()
{
  int2bcd(cont); //Convierto el contador a BCD y lo muestro en el display de 7 segmentos
  cont = cont + 1; //Incremento contador
  if (cont > 9) //Si el contador es mayor a 9, reinicie
  {
    cont = 0;
  }
  Serial.println(cont);
  delay(1000);
}
