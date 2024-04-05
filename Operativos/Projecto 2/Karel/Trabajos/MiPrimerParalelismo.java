package Trabajos;

import kareltherobot.*;
import java.awt.Color;

public class MiPrimerParalelismo implements Directions
{
    public static void main(String[] args) {
        World.readWorld("MundoParalelismo.kwld");
        World.setVisible(true);

        // Crear y ejecutar los corredores en paralelo
        Thread firstThread = new Thread(new Runnable() {
            public void run() {
                Racer first = new Racer(1, 1, East, 0);
                first.race();
            }
        });

        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                Racer second = new Racer(2, 1, East, 0, Color.blue);
                second.race();
            }
        });

        // Iniciar los hilos
        firstThread.start();
        secondThread.start();
    }
}

class Racer extends Robot
{
    public Racer(int Street, int Avenue, Direction direction, int beeps)
    {
        super(Street, Avenue, direction, beeps);
        World.setupThread(this);
    }

    public Racer(int Street, int Avenue, Direction direction, int beeps, Color robotColor)
    {
        super(Street, Avenue, direction, beeps, robotColor);
        World.setupThread(this);
    }

    public void race()
    {
        while(! nextToABeeper())
            move();
        pickBeeper();
        turnOff();
    }

    public void run()
    {
        race();
    }
}