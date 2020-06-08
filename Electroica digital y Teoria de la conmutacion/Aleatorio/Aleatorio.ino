#define startButton 37
#define mayor 36
#define menor 35
#define igual 34

int record = 0;
int randNumber;
int estadoIni = 0;
int estadoFin = 0;
int estado = 0;
int tDelay = 50;
int tCambio = 0;
int i = 0;
int numero1 = 0;
int numero2 = 0;
int resultado = 0;
int numero3 = 0;
int puntuacion = 0;

void setup()
{
  Serial.begin(9600);
  pinMode(startButton, INPUT);
  pinMode(mayor, INPUT);
  pinMode(menor, INPUT);
  pinMode(igual, INPUT);
  Serial.println("Pulsa el boton para comenzar");
  randomSeed(analogRead(0));
}

void loop()
{
  estado = digitalRead(startButton);
  if (estado != estadoFin)
  {
    tCambio = millis();
  }
  if ((millis() - tCambio) > tDelay)
  {
    if (estado != estadoIni)
    {
      estadoIni = estado;
      if (estadoIni == LOW)
      {
        Ale();
      }
    }
  }
  estadoFin = estado;
}

void Ale()
{
  if (i < 4)
  {
    randNumber = random(1, 9);
    digitalWrite(randNumber, HIGH);
    delay(300);
    digitalWrite(randNumber, LOW);
    delay(600);
    if (i == 0)
    {
      numero1 = randNumber;
      Serial.print("numero1:  ");
      Serial.println(numero1);
    }

    if (i == 1)
    {
      numero2 = randNumber;
      Serial.print("numero2: ");
      Serial.println(numero2);
    }

    if (i == 2)
    {

      if (randNumber >= 0 && randNumber <= 3)
      {
        resultado = numero1 + numero2;
        Serial.print("suma: ");
        Serial.println(resultado);
        Serial.println("Pulsa el boton 36 si el crees que el siguente numero es MAYOR al resultado");
        Serial.println("O 35 si crees que es MENOR al resultado");
        Serial.println("O 34 si crees que esIGUAL al resultado");
      }

      if (randNumber >= 4 && randNumber <= 6)
      {
        resultado = numero1 - numero2;
        Serial.print("resta: ");
        Serial.println(resultado);
        Serial.println("Pulsa el boton 36 si el crees que el siguente numero es MAYOR al resultado");
        Serial.println("O 35 si crees que es MENOR al resultado");
        Serial.println("O 34 si crees que esIGUAL al resultado");
      }

      if (randNumber == 7)
      {
        resultado = numero1 / numero2;
        Serial.print("division: ");
        Serial.println(resultado);
        Serial.println("Pulsa el boton 36 si el crees que el siguente numero es MAYOR al resultado");
        Serial.println("O 35 si crees que es MENOR al resultado");
        Serial.println("O 34 si crees que esIGUAL al resultado");
      }

      if (randNumber >= 8 && randNumber <= 9)
      {
        resultado = numero1 * numero2;
        Serial.print("multiplicacion: ");
        Serial.println(resultado);
        Serial.println("Pulsa el boton 36 si el crees que el siguente numero es MAYOR al resultado");
        Serial.println("O 35 si crees que es MENOR al resultado");
        Serial.println("O 34 si crees que esIGUAL al resultado");
      }
    }

    if (i == 3)
    {

      randNumber = random(-9, 81);
      digitalWrite(randNumber, HIGH);
      delay(300);
      digitalWrite(randNumber, LOW);
      delay(600);
      numero3 = randNumber;
      resul();
    }
  }
  i++;
  if (i >= 4)
  {
    i = 0;
  }
}

void resul()
{
  Serial.print("numero3: ");
  Serial.println(numero3);
  if (digitalRead(mayor) == 1)
  {
    if (resultado < numero3)
    {
      Serial.print("Ganaste, Puntuacion = ");
      puntuacion++;
      Serial.println(puntuacion);
    }
    else
    {
      Serial.print("Perdiste, Puntuacion = ");
      Serial.println(puntuacion);
      if (puntuacion > record)
      {
        record = puntuacion;
        Serial.print("Nuevo Record");
      }
      Serial.print("Record:  ");
      Serial.println(record);
      puntuacion = 0;
    }
  }
  if (digitalRead(menor) == 1 && digitalRead(mayor) == 0 && digitalRead(igual) == 0)
  {
    if (resultado > numero3)
    {
      Serial.print("Ganaste, Puntuacion = ");
      puntuacion++;
      Serial.println(puntuacion);
    }
    else
    {
      Serial.print("Perdiste, Puntuacion = ");
      Serial.println(puntuacion);
      if (puntuacion > record)
      {
        record = puntuacion;
        Serial.print("Nuevo Record");
      }
      Serial.print("Record:  ");
      Serial.println(record);
      puntuacion = 0;
    }
  }
  if (digitalRead(igual) == 1 && digitalRead(mayor) == 0 && digitalRead(menor) == 0)
  {
    if (resultado == numero3)
    {
      Serial.print("Ganaste, Puntuacion = ");
      puntuacion++;
      Serial.println(puntuacion);
    }
    else
    {
      Serial.print("Perdiste, Puntuacion = ");
      Serial.println(puntuacion);
      if (puntuacion > record)
      {
        record = puntuacion;
        Serial.print("Nuevo Record");
      }
      Serial.print("Record:  ");
      Serial.println(record);
      puntuacion = 0;
    }
  }

  if (digitalRead(mayor) == 1 && digitalRead(menor) == 1 && (digitalRead(igual) == 1))
  {
    Serial.print("Sacas 0 por Tranposo");
    puntuacion = 0;
    Serial.println(puntuacion);
  }
}
