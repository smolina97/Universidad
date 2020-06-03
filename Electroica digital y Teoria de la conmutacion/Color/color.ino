#define B 4
#define R 3
#define G 2


void setup() {
  pinMode(R,INPUT);
  pinMode(B,INPUT);
  pinMode(G,INPUT);
}

void loop() {
 analogWrite(R,163);
 analogWrite(G,73);
 analogWrite(B,164);

}
