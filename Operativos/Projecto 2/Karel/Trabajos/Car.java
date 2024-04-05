package Trabajos;

import kareltherobot.Robot;
import kareltherobot.World;

import java.awt.*;

public class Car extends Robot {
    public int calleActual;
    public int avenidaActual;
    public int beepersInBag;

    public Car(int Street, int Avenue, Direction direction, int beeps, Color robotColor) {
        super(Street, Avenue, direction, beeps, robotColor);
        this.calleActual = Street;
        this.avenidaActual = Avenue;
        this.beepersInBag = beeps;
        World.setupThread(this);
    }

    // Métodos para acceder y actualizar la calle actual y la avenida actual
    public int getCalleActual() {
        return calleActual;
    }

    public int getAvenidaActual() {
        return avenidaActual;
    }

    public int getBeepers(){ return beepersInBag;}

    public void setCalleActual(int calleActual) {
        this.calleActual = calleActual;
    }

    public void setAvenidaActual(int avenidaActual) {
        this.avenidaActual = avenidaActual;
    }

    public String orientation(){
        if(facingEast()){
            return "east";
        }
        else if (facingNorth()){
            return "north";
        }
        else if (facingSouth()){
            return "south";
        }
        else{
            return "west";
        }
    }
}
