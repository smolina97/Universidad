#define MODIN 37
#define MOD   24
#define DIVIN 36
#define DIV   25
#define MULIN 35
#define MUL   26
#define RETURNIN 34
#define GOTOIN 33
#define LDAIN 47
#define LDA   22
#define ADDIN 46
#define ADD   28
#define SUBIN 45
#define SUB   27
#define OUTIN 44
#define HALTIN 43
#define POT2IN 42
#define POT2 23
#define LM 13
#define ER 12
#define LI 29
#define EI 11
#define LA 10
#define EU 50
#define LB 51
#define LO 52
#define EA 53
#define MD 32
#define BHLT 1
#define EP 7

int MODINT;
int numero2;
int numero;
int PC;
int valor;


void setup() {
  Serial.begin(9600);
  DDRK = B00000000;
  DDRJ = B00000000;
  DDRD = B11111111;
  pinMode(MODIN, INPUT);
  pinMode(MOD, OUTPUT);
  pinMode(DIVIN, INPUT);
  pinMode(DIV, OUTPUT);
  pinMode(MULIN, INPUT);
  pinMode(MUL, OUTPUT);
  pinMode(RETURNIN, INPUT);
  pinMode(GOTOIN, INPUT);
  pinMode(LDAIN, INPUT);
  pinMode(LDA, OUTPUT);
  pinMode(ADDIN, INPUT);
  pinMode(ADD, OUTPUT);
  pinMode(SUBIN, INPUT);
  pinMode(SUB, OUTPUT);
  pinMode(OUTIN, INPUT);
  pinMode(HALTIN, INPUT);
  pinMode(POT2IN, INPUT);
  pinMode(BHLT, INPUT);
  pinMode(MD, OUTPUT);
  pinMode(POT2, OUTPUT);
  pinMode(LM, OUTPUT);
  pinMode(ER, OUTPUT);
  pinMode(EI, OUTPUT);
  pinMode(LA, OUTPUT);
  pinMode(EU, OUTPUT);
  pinMode(LB, OUTPUT);
  pinMode(LO, OUTPUT);
  pinMode(EA, OUTPUT);
  pinMode(EP, OUTPUT);
  pinMode(LI, OUTPUT);


  digitalWrite(MOD, 0);
  digitalWrite(DIV, 0);
  digitalWrite(MUL, 0);
  digitalWrite(LDA, 0);
  digitalWrite(ADD, 0);
  digitalWrite(SUB, 0);
  digitalWrite(POT2, 0);
  digitalWrite(LM, 0);
  digitalWrite(ER, 0);
  digitalWrite(LI, 0);
  digitalWrite(EI, 0);
  digitalWrite(LA, 0);
  digitalWrite(EU, 0);
  digitalWrite(LB, 0);
  digitalWrite(LO, 0);
  digitalWrite(EA, 0);
  digitalWrite(EP, 0);
  digitalWrite(MD, 0);
  PC = 0;
}

void loop() {
  PORTF = PC;
  digitalWrite(EP, 1);
  digitalWrite(LM, 1);
  delay(1);
  digitalWrite(LM, 0);
  digitalWrite(EP, 0);
  digitalWrite(ER, 1);
  digitalWrite(LI, 1);
  delay(1);
  digitalWrite(ER, 0);
  digitalWrite(LI, 0);

  if (digitalRead(MODIN)) {
    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    digitalWrite(ER, 1);
    numero = PINK;
    delay(1);
    digitalWrite(LB, 1);
    digitalWrite(ER, 0);
    digitalWrite(MD, 1);
    delay(1);
    numero2 = PINJ;
    digitalWrite(LB, 0);
    MODINT = (numero % numero2);
    Serial.println(MODINT);
    delay(1);
    PORTD = MODINT;
    digitalWrite(LO, 1);
    delay(1);
    digitalWrite(MD, 0);
    digitalWrite(LO, 0);
    PC;
  }

  if (digitalRead(DIVIN)) {
    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    digitalWrite(ER, 1);
    digitalWrite(LB, 1);
    delay(1);
    digitalWrite(ER, 0);
    digitalWrite(LB, 0);
    digitalWrite(DIV, 1);
    digitalWrite(EU, 1);
    delay(1);
    digitalWrite(DIV, 0);
    digitalWrite(LA, 1);
    delay(1);
    digitalWrite(LA, 0);
    digitalWrite(EU, 0);
    PC++;
  }

  if (digitalRead(MULIN)) {
    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    digitalWrite(ER, 1);
    digitalWrite(LB, 1);
    delay(1);
    digitalWrite(ER, 0);
    digitalWrite(LB, 0);
    digitalWrite(MUL, 1);
    digitalWrite(EU, 1);
    delay(1);
    digitalWrite(MUL, 0);
    digitalWrite(LA, 1);
    delay(1);
    digitalWrite(LA, 0);
    digitalWrite(EU, 0);
    PC++;
  }

  if (digitalRead(RETURNIN)) {
    PC = valor;
  }

  if (digitalRead(GOTOIN)) {
    valor = PC + 1;
    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    PC++;
  }

  if (digitalRead(LDAIN)) {
    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    digitalWrite(ER, 1);
    digitalWrite(LB, 1);
    delay(1);
    digitalWrite(ER, 0);
    digitalWrite(LB, 0);
    digitalWrite(EU, 1);
    digitalWrite(LA, 1);
    delay(1);
    digitalWrite(LA, 0);
    digitalWrite(EU, 0);
    PC++;
  }

  if (digitalRead(ADDIN)) {

    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    digitalWrite(ER, 1);
    digitalWrite(LB, 1);
    delay(1);
    digitalWrite(ER, 0);
    digitalWrite(LB, 0);
    digitalWrite(ADD, 1);
    digitalWrite(EU, 1);
    delay(1);
    digitalWrite(ADD, 0);
    digitalWrite(LA, 1);
    delay(1);
    digitalWrite(LA, 0);
    digitalWrite(EU, 0);
    PC++;
  }

  if (digitalRead(SUBIN)) {
    digitalWrite(EI, 1);
    digitalWrite(LM, 1);
    delay(1);
    digitalWrite(LM, 0);
    digitalWrite(EI, 0);
    digitalWrite(ER, 1);
    digitalWrite(LB, 1);
    delay(1);
    digitalWrite(ER, 0);
    digitalWrite(LB, 0);
    digitalWrite(SUB, 1);
    digitalWrite(EU, 1);
    delay(1);
    digitalWrite(SUB, 0);
    digitalWrite(LA, 1);
    delay(1);
    digitalWrite(EU, 0);
    digitalWrite(LA, 0);
    PC++;

  }

  if (digitalRead(OUTIN)) {
    digitalWrite(EU, 1);
    digitalWrite(LO, 1);
    delay(1);
    digitalWrite(LO, 0);
    digitalWrite(EU, 0);
    PC++;
  }

  if (digitalRead(HALTIN)) {
    while (!digitalRead(BHLT)) {
      PC = 0;
    }
  }
  if (digitalRead(POT2IN)) {
    digitalWrite(EA, 1);
    digitalWrite(LB, 1);
    delay(1);
    digitalWrite(ER, 0);
    digitalWrite(LB, 0);
    digitalWrite(EA, 0);
    digitalWrite(MUL, 1);
    digitalWrite(EU, 1);
    delay(1);
    digitalWrite(MUL, 0);
    digitalWrite(LA, 1);
    delay(1);
    digitalWrite(EU, 0);
    digitalWrite(LA, 0);
    PC++;
  }
}
