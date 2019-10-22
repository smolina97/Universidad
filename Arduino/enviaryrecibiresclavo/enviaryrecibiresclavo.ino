#include <Wire.h>


void setup() {
  Wire.begin(1);
  pinMode(33, OUTPUT);
  pinMode(37, INPUT);
  Serial.begin(9600);
  Wire.onRequest(enviar);
}

void loop() {
Wire.onReceive(led);
}

void enviar(){
   if (digitalRead(37)) {
    Wire.write(1);
  } else {
    Wire.write(0);
  }

}
void led() {

  while (Wire.available()) {

    if (Wire.read() == 1) {
      digitalWrite(33, 1);
    }else{
      digitalWrite(33, 0);
    }
  }
}
