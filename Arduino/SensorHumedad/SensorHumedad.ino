//Librerias
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

#define pinSM A0 // Sensor de humedad
#define ent D0  // Switch válvula
#define LED D1  // Led de intensidad
#define nivl D3  // Sensor de Nivel bajo
#define nivh D5  // Sensor de Nivel alto
#define luz D7  // Fotoresistencia

//Constantes
#define ADCWAT 414  //Medicion ADC con agua
#define ADCAIR 774  //Medicion ADC con aire
#define SMAIR 0 //Porcentaje de humedad minimo
#define SMWAT 100 //Porcentaje de humedad maximo

#define SSIDNAME "MoncadaGo"  //SSID Name
#define SSIDPASS "djmg71398002" //SSID Pass
#define TSKEY "AU7DXF1HXI8SO57T"  //Thingspeak API Key write  

//Variables
unsigned int ledstate = 0; //Variable para indicar el estado actual del led
unsigned int valvstate = 0;  //Estado de la válvula
unsigned int niv=0;  //Nivel del tanque 
unsigned int luminosidad=0;  //Luz que recibe la planta

//->Variables IoT
WiFiClient client;
HTTPClient http;
String thingSpeakAddress = "http://api.thingspeak.com/update?";
String writeAPIKey;
String tsfield1Name;
String request_string;

//Subrutinas y/o funciones
float flmap(float x, float in_min, float in_max, float out_min, float out_max) {
  return constrain((float)((x-in_min) * (out_max-out_min) / (in_max-in_min) + out_min), out_min, out_max);
}

void electVal(float humidity){
  valvstate= 0;
  if(humidity<75){
    digitalWrite(ent,HIGH);
    valvstate=1;
    delay(3000);
    float hum2 = flmap(analogRead(pinSM), ADCAIR, ADCWAT, SMAIR, SMWAT);
    if(hum2>75){
      digitalWrite(ent,LOW);
    }
  }else{
    digitalWrite(ent,LOW);
    delay(3000);
  }
  
}

void blink_led(int lumin){
  if(lumin==0){
    digitalWrite(LED, HIGH);
    ledstate = 1;
  }else{
    digitalWrite(LED, LOW);
    ledstate = 0;
  }
  luminosidad= lumin;
}

void nivel(){
  if(digitalRead(nivl)==1){
    if(digitalRead(nivh)==1){
      niv=100;
    }else{
      niv=50;
    }
  }else{
    niv=10;
  }
}

void printWifiStatus() {
  //Print SSID name
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  //Print ipv4 assigned to WiFi101 module
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  //Print signal strength for WiFi101 module
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

void WiFiInit() {
  delay(1000);  //Wait 1 sec for module initialization

  //Check if WiFi Shield is connected to Arduino
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    //Infinite loop if shield is not detected
    while (true);
  }

  //Attempt a WiFi connection to desired access point at ssid, password
  while ( WiFi.status() != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(SSIDNAME);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:
    WiFi.begin(SSIDNAME, SSIDPASS);
    delay(10000); //Wait 10 secs for establishing connection
  }
  //Print WiFi status
  printWifiStatus();
}

void setup() {
  //pinMode(LED_BUILTIN, OUTPUT);
  pinMode(LED, OUTPUT);
  pinMode(ent, OUTPUT);
  pinMode(pinSM, INPUT);
  pinMode(nivl, INPUT);
  pinMode(nivh, INPUT);
  pinMode(luz, INPUT);
  
  digitalWrite(LED, LOW);
  digitalWrite(ent,LOW);
  //Comunicaciones
  Serial.begin(9600);
  WiFiInit(); //WiFi communications initialization
}

void loop() {
  float sm = flmap(analogRead(pinSM), ADCAIR, ADCWAT, SMAIR, SMWAT);
  int lumin= digitalRead(luz);
  int bajo= digitalRead(nivl);
  int alto= digitalRead(nivh);
  Serial.println("Medición RAW: " + String(analogRead(pinSM)) + " Humedad Tierra(%): " + String(sm));
  Serial.println(String(digitalRead(nivl)));
  Serial.println(String(digitalRead(nivh)));
  Serial.println(String(digitalRead(luz)));
  delay(1000);
  electVal(sm);
  blink_led(lumin);
  nivel();
  Serial.println(niv);

  if (client.connect("api.thingspeak.com", 80)) {
      Serial.println("Connected successfully");
      request_string = thingSpeakAddress;
      request_string += "api_key=";
      request_string += TSKEY;
      request_string += "&";
      request_string += "field1";
      request_string += "=";
      request_string += sm;
      request_string += "&";
      request_string += "field2";
      request_string += "=";
      request_string += niv;
      request_string += "&";
      request_string += "field3";
      request_string += "=";
      request_string += luminosidad;
      request_string += "&";
      request_string += "field4";
      request_string += "=";
      request_string += valvstate;
      request_string += "&";
      request_string += "field5";
      request_string += "=";
      request_string += ledstate;
      http.begin(request_string);
      http.GET();
      http.end();
    }
    else {
      Serial.println("Error connecting");
    }
 delay(600000);
}
