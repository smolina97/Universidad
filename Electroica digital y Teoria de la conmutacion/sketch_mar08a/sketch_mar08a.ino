#define LR 3
#define LG 2
#define LB 4

const unsigned long TR = 3000;
const unsigned long TG = 2000;
const unsigned long TB = 1000;

void setup() {
  pinMode(LR, OUTPUT);
  pinMode(LG, OUTPUT);
  pinMode(LB, OUTPUT);

  digitalWrite(LR, LOW);
  digitalWrite(LG, LOW);
  digitalWrite(LB, LOW);
  Serial.begin(9600);
}
void loop() {
  for (int i = 0; i <= 255; i++) {
    analogWrite(LR, i);
    Serial.println("R: " + String(i) + " G: 0 B: 0");
    delay(TR / 255);
  }
  for (int i = 0; i <= 255; i++) {
    analogWrite(LG, i);
    Serial.println("R: 0 G: " + String(i) + " B: 0");
    delay(TG / 255);
  }
  for (int i = 0; i <= 255; i++) {
    analogWrite(LB, i);
    Serial.println("R: 0 G: 0 B: " + String(i));
    delay(TB / 255);
  }
}
