#include <Wire.h>

#define SLAVE_ADDRESS 0x06
#define Rojo 27
#define Amarillo 28
#define Verde 29
#define PEAR 31
#define PEAV 32
#define boton 37
#define stop 36

int estado = 0;
int tiempo = 0;
int tiempo2 = 0;
long Tactual = 0;
long Tinicial = 0;
int cambio = 15000;
int activo = 0;
int recive[6];
int botonEn;
int carroRojo;
int carroAmarillo;
int carroVerde;
int peatonRojo;
int peatonVerde;

void setup()
{

  Wire.begin(SLAVE_ADDRESS);
  Wire.onRequest(controlador);
  Wire.onReceive(Control);

  pinMode(Rojo, OUTPUT);
  pinMode(Amarillo, OUTPUT);
  pinMode(Verde, OUTPUT);
  pinMode(PEAR, OUTPUT);
  pinMode(PEAV, OUTPUT);
  pinMode(boton, INPUT);
  digitalWrite(Rojo, 0);
  digitalWrite(Amarillo, 0);
  digitalWrite(Verde, 0);
  digitalWrite(PEAR, 0);
  digitalWrite(PEAV, 0);

  Serial.begin(9600);
}

void loop()
{

  Tactual = millis();
  delay(600);
  digitalWrite(Rojo, 0);
  digitalWrite(Amarillo, 0);
  digitalWrite(Verde, 1);
  digitalWrite(PEAR, 1);
  digitalWrite(PEAV, 0);
  delay(500);

  while (recive[6] == 1)
  {
    mantenimiento();
  }

  if (digitalRead(boton) == 1)
  {
    activo = 1;

  }
  else
  {
    botonEn = 0;
  }

  if (activo == 1)
  {
    sem();
  }

  controlador();
}

void Control(int byteCount)
{
  for (int i = 0; i < byteCount; i++)
  {
    recive[i] = Wire.read();
  }
}

void mantenimiento()
{
  while (recive[1] != 1 && recive[2] != 1 && recive[3] != 1 && recive[4] != 1 && recive[5] != 1)
  {
    digitalWrite(Rojo, 0);
    digitalWrite(Amarillo, 0);
    digitalWrite(Verde, 0);
    digitalWrite(PEAR, 0);
    digitalWrite(PEAV, 0);
  }

  if (recive[1] == 1)
  {
    digitalWrite(Rojo, 1);
  }
  else
  {
    digitalWrite(Rojo, 0);
  }

  if (recive[2] == 1)
  {
    digitalWrite(Amarillo, 1);
  }
  else
  {
    digitalWrite(Amarillo, 0);
  }

  if (recive[3] == 1)
  {
    digitalWrite(Verde, 1);
  }
  else
  {
    digitalWrite(Verde, 0);
  }

  if (recive[4] == 1)
  {
    digitalWrite(PEAR, 1);
  }
  else
  {
    digitalWrite(PEAR, 0);
  }

  if (recive[5] == 1)
  {
    digitalWrite(PEAV, 1);
  }
  else
  {
    digitalWrite(PEAV, 0);
  }
}

void controlador()
{

  if (digitalRead(Rojo) == 1)
  {
    carroRojo = 1;
  }
  else
  {
    carroRojo = 0;
  }

  if (digitalRead(Amarillo) == 1)
  {
    carroAmarillo = 1;
  }
  else
  {
    carroAmarillo = 0;
  }

  if (digitalRead(Verde) == 1)
  {
    carroVerde = 1;
  }
  else
  {
    carroVerde = 0;
  }

  if (digitalRead(PEAR) == 1)
  {
    peatonRojo = 1;
  }
  else
  {
    peatonRojo = 0;
  }

  if (digitalRead(PEAV) == 1)
  {
    peatonVerde = 1;
  }
  else
  {
    peatonVerde = 0;
  }

  Wire.write(carroRojo);
  Wire.write(carroAmarillo);
  Wire.write(carroVerde);
  Wire.write(peatonRojo);
  Wire.write(peatonVerde);

}

void sem()
{

  if (Tactual - Tinicial >= cambio)
  {
    while (tiempo < 3)
    {
      controlador();
      delay(600);
      digitalWrite(Rojo, 0);
      digitalWrite(Amarillo, 0);
      digitalWrite(Verde, 1);
      digitalWrite(PEAR, 1);
      digitalWrite(PEAV, 0);
      delay(500);
      tiempo++;
    }

    tiempo = 0;
    while (tiempo < 3)
    {
      controlador();
      delay(500);
      digitalWrite(Rojo, 0);
      digitalWrite(Amarillo, 1);
      digitalWrite(Verde, 0);
      digitalWrite(PEAR, 1);
      digitalWrite(PEAV, 0);
      delay(500);
      tiempo++;
    }

    tiempo = 0;
    while (tiempo < 2)
    {

      controlador();
      digitalWrite(Rojo, 1);
      digitalWrite(Amarillo, 0);
      digitalWrite(Verde, 0);
      digitalWrite(PEAR, 1);
      digitalWrite(PEAV, 0);
      delay(500);
      tiempo++;
    }

    tiempo = 0;
    while (tiempo < 3)
    {

      controlador();
      delay(500);
      digitalWrite(Rojo, 1);
      digitalWrite(Amarillo, 0);
      digitalWrite(Verde, 0);
      digitalWrite(PEAR, 0);
      digitalWrite(PEAV, 1);
      delay(500);
      tiempo++;
    }

    tiempo = 0;
    while (tiempo2 < 3)
    {

      controlador();
      delay(500);
      digitalWrite(PEAV, 1);
      delay(500);
      digitalWrite(PEAV, 0);
      delay(500);
      tiempo2++;
    }

    controlador();
    digitalWrite(Rojo, 1);
    digitalWrite(PEAR, 1);
    delay(600);
    tiempo = 0;
    tiempo2 = 0;
    Tinicial = Tactual + 15401;
    activo = 0;
  }
}
