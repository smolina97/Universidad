//Libreias
#include <SPI.h>
#include <WiFi101.h>
#include <UbidotsArduino.h>

//definicion de pines
#define LM35 A2
#define HUM A1
#define LED 6
#define BOM 7


//Ubidots
#define IDTEMP "5cdf17861d847268b3618891"
#define IDHUM "5cdf179d1d8472684641ccaf"
#define IDLED "5cf021fd1d847206f3144803"
#define IDBOM "5cf024c21d84720b42205dc2"
#define TOKEN "BBFF-lUZZcaoUjAIgtWebhJUkbAerIGONF9"

//variables
float temperatura;
float humedad;
char ssid[] = "IoT-B19";
char pass[] = "meca2017*";
int keyIndex;
int status = WL_IDLE_STATUS;
//UBIDOTS
Ubidots client(TOKEN);

void setup() {
  //configuracion de pine
  pinMode(LM35, INPUT);
  pinMode(HUM, INPUT);
  pinMode(LED, OUTPUT);
  pinMode(BOM, OUTPUT);
  digitalWrite(LED, LOW);
  digitalWrite(BOM, LOW);

  Serial.begin(9600);

  while ( status != WL_CONNECTED) {
    Serial.println("Attemting to connect to SSIDTEMP: ");
    Serial.println(ssid);
    //Connect to WPA/WPA2 network. change this line if using open or WEP network:
    status = WiFi.begin(ssid, pass);
    //wait 10 seconds for connection:
    delay(10000);
  }
}

void loop() {


  temperatura = (3.3 * analogRead(LM35) * 100) / 1023.0; //convertir el valor de temperatura a C

  humedad = 100 - (analogRead(HUM) / 1023.0) * 100;

  client.add(IDTEMP, temperatura);
  client.add(IDHUM, humedad);
  client.sendAll();
  delay(1000);


  int valorLED;
  float *responseArrayLED;
  responseArrayLED = client.getValue(IDLED);
  if (responseArrayLED[0] == 1) {
    valorLED = responseArrayLED[1];
  }
  if (valorLED == 1) {
    digitalWrite(LED, HIGH);
  } else {
    digitalWrite(LED, LOW);
  }

  int valorBOM;
  float *responseArrayBOM;
  responseArrayBOM = client.getValue(IDBOM);
  if (responseArrayBOM[0] == 1) {
    valorBOM = responseArrayBOM[1];
  }
  if (valorBOM == 1) {
    digitalWrite(BOM, HIGH);
  } else {
    digitalWrite(BOM, LOW);
  }

}
