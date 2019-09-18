/*
 * Titulo: 
 * Integrantes: Luis Fernando Posada Cano, Santiago Molina Mejía
 * Descripción: Un cultivo el cual se puede regar, iluminar, conocer su temperatura y su humedad -
 * desde una aplicación web.
 */

//Declaración de librerias
#include <ESP8266WiFi.h>      //Libreria de ESP8266 y su WIFI 
#include <Wire.h>             //Libreria adicional para el ADS1115
#include <Adafruit_ADS1015.h> //Libreria del ADS1115
#include <ThingSpeak.h>       //Libreria de ThingSpeak

//Definicion de pines
#define D4 2  //Defino el pin de salida 2 como D4
#define D6 12 //Defino el pin de salida 12 como D6

//Declaracion de objeto
Adafruit_ADS1115 ads(0x48); //Instancio el objeto Adafruit_ADS1115 como ads(0x48)
WiFiClient thin; //Instancio el objeto tipo WiFiClient como thin

//Declaracion de constantes
const char* ssid     = "IoT-B19";   //Declaro que ssid va a tener el String siguiente
const char* password = "meca2017*"; //Declaro password como el String siguiente

//Declaracion de variables
char thingSpeakAddress[] = "api.thingspeak.com"; //Variable para conectar al servidor
unsigned long channelID1 = 782170;               //ID del canal del thingSpeak
unsigned long channelID2 = 602090;               //ID del canal del thingSpeak
unsigned long channelID3 = 602092;               //ID del canal del thingSpeak
unsigned long channelID4 = 602093;               //ID del canal del thingSpeak
unsigned long channelID5 = 602094;               //ID del canal del thingSpeak
char* readAPIKey4 = "DRTE600B6RYWIFFE";          //Variable para leer datos del ThingSpeak
char* readAPIKey5 = "0BMSSSW1XAU8IY67";          //Variable para leer datos del ThingSpeak
char* writeAPIKey1 = "4AJ3NBYKYMR84KZF";         //Variable para escribir datos del ThingSpeak
char* writeAPIKey2 = "WQE2TH40I6CLDXVQ";         //Variable para escribir datos del ThingSpeak
char* writeAPIKey3 = "3AAJPM5UCOMH45IF";         //Variable para escribir datos del ThingSpeak
unsigned int aField = 1;                         //Variable para seleccionar el campo 1 del arreglo JSON             

//Subrutinas o Funciones
float readTSData(long TSChannel, unsigned int TSField, char* readAPIKey){  //Funcion para recibir los datos del ThingSpeak   
  float data =  ThingSpeak.readFloatField(TSChannel, TSField, readAPIKey); //Asigno data como float para recibir el valor llamado al servidor
  return data;  //Retorno data
}

int writeTSData(long TSChannel, unsigned int TSField, float data, char* writeAPIKey){ //Funcion para reescribir los valores del ThingSpeak
  int  writeSuccess = ThingSpeak.writeField(TSChannel, TSField, data, writeAPIKey);   //Asignacion de datos para enviar al ThingSpeak en una variable llamada writesucces
  if(writeSuccess){ //Se comprueba si writeSuccess es valida
    Serial.println(String(data) + " written to Thingspeak."); //Imprimimos que se ha enviado correctamente el dato al servidor
  }    
  return writeSuccess; //Se retorna writeSuccess
}

void leerDatos() {  //Funcion para leer datos
  float light = readTSData(channelID4, aField, readAPIKey4); //Se le asigna a light como float para recibir el valor del servidor
  float valve = readTSData(channelID5, aField, readAPIKey5); //Se le asigna a valve como float para recibir el valor del servidor
  if(light == 1) { //Se confirma si el valor de light es igual a 1
    digitalWrite(D4, HIGH); //Si se encuentra en 1 se prenden las luces
  } else { // si no
    digitalWrite(D4, LOW); //Si se encuentra en 0 se apagan las luces  
  }
  if(valve == 1) { //Se confirma si el valor de valve es igual a 1
    digitalWrite(D6, HIGH); //Si se encuentra en 1 se prende el motor
  } else { //si no
    digitalWrite(D6, LOW); //Si se encuentra en 0 se apaga el motor  
  }  
}

void enviDatos() { //Funcion para enviar datos
  int16_t adc0, adc1;  //Se asignan los ADC para recibir datos de 16bits como enteros

  adc0 = ads.readADC_SingleEnded(0); //Se lee el adc0 del A0
  adc1 = ads.readADC_SingleEnded(1); //Se lee el adc1 del A1

  float humFormula = (adc0 * 0.1875)/1000; //formula para convertir a porcentaje de humedad
  float readHumidity = map(humFormula, 1.40, 3.30, 100.0, 0.0); //Dependiendo del voltaje, hace un mapeo de 0 a 100% para cambiar el voltaje
  float humdc = readHumidity; //Paso la variable readHumidity a humdc

  float readTemp = (adc1 * ((6.144f * 2) / (65536 - 1))) / (10.0f / 1000); //Paso el valor de adc1 a readTemp
  readTemp = (readTemp * 2) / 1023; //Formula para transformar los datos de bits a temperatura
  float tempc = readTemp; //Paso el valor de readTemp a tempc
  
  writeTSData(channelID1, aField, humdc, writeAPIKey1); //Escribe los datos de humedad en el servidor
  delay(500); //Retraso para no enviar muy rapido los datos
  writeTSData(channelID2, aField, tempc, writeAPIKey2); //Escribe los datos de temperatura en el servidor
  delay(500); //Retraso para no enviar muy rapido los datos
}

//Configuracion de puertos y limpieza de pines
void setup() {
  Serial.println("Connecting to "); //Mensaje para seguimiento del programa
  Serial.println(ssid); // Nombre de la red wifi definido previamente
  WiFi.begin(ssid, password); //Iniciar el modo wifi con las variables definidas
  while(WiFi.status() != WL_CONNECTED) {      //Mientras el wifi no este conectado
    delay(500); //Retraso de 0.5 segundos 
    Serial.print("."); //Seguimiento del programa con "."
  }    
  Serial.println("Connected"); //Si se logro conectar al wifi se imprime connected
  ThingSpeak.begin(thin); //Coneccion con el servidor ThingSpeak

  //Definicion de pines como salida o entrada
  pinMode(D4, OUTPUT); //defino D4 como pin de salida
  pinMode(D6, OUTPUT); //defino D6 como pin de salida
  //Limpieza de pines por seguridad
  digitalWrite(D4, LOW); //Apago D4
  digitalWrite(D6, LOW); //Apago D6
  //Comunicaciones
  Serial.begin(115200); //Defino la velocidad de comunicacion a 115200 badios 
}
//Ciclo infinito
void loop() {
  leerDatos(); //se Llama la funcion leerDatos()
  enviDatos(); //Se llama la funcion enviDatos()
}
