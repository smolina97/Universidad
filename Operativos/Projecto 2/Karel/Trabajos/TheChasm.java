package Trabajos;

import kareltherobot.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.awt.Color;
import java.util.ArrayList;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.locks.Condition;

public class TheChasm implements Directions {
    //Variables para el entendimiento del mundo
    public static int totalBeepersLeftInMine;
    public static int totalBeepersLeftToBeTransported;
    public static int totalBeepersLeftToBeExtracted;
    public static int beepersWaitingForTrain;
    public static int beepersInExtractionPoint;
    public static boolean trainInterception = false;
    public static boolean extractionPointInterception = false;

    //Se crea una lista para almacenar los robots creados de cualquier tipo
    private static final ArrayList<Robot> robots = new ArrayList<>();

    //Semaforos necesarios para el correcto funcionamiento
    private static final Semaphore onlyOneMines = new Semaphore(1);
    private static final Semaphore onlyOneTrainExtract = new Semaphore(1);
    private static final Semaphore onlyOneExtractorExtract = new Semaphore(1);
    private static final Semaphore onlyOneTrainInterception = new Semaphore(1);
    private static final Semaphore onlyOneCarInExtractionPoint = new Semaphore(1);

    //Condiciones para evitar que los hilos corran sin hacer nada
    //Para evitar que los trenes ejecuten el while constatemente cuando esperan que los mineros terminen
    private static final Lock lock = new ReentrantLock();
    private static final Condition nuevosBeepersDeLosMineros = lock.newCondition();
    //Para evitar que los extractores ejecuten el while constatemente cuando esperan que los trenes terminen
    private static final Lock lock2 = new ReentrantLock();
    private static final Condition nuevosBeepersDeLosTrenes = lock2.newCondition();

    public static void main(String[] args) {
        //Leer el mundo y hacerlo visible
        World.readWorld("MundoProyectoV1.kwld");
        World.setDelay(10);
        World.setVisible(true);

        //Encontrar beepers en la mina:
        totalBeepersLeftInMine = contarBeepersEnLaMina();
        totalBeepersLeftToBeTransported = totalBeepersLeftInMine;
        totalBeepersLeftToBeExtracted = totalBeepersLeftInMine;

        class Minero extends Car {
            public Minero(int Street, int Avenue, Direction direction, int beeps, Color robotColor) {
                super(Street, Avenue, direction, beeps, robotColor);
            }

            public void mine() {
                moveRobot(this);
                turnLeft();
                turnLeft();
                turnLeft();
                while (beepersInBag < 5 && totalBeepersLeftInMine > 0) {
                    if (nextToABeeper()) {
                        pickBeeper();
                        beepersInBag++;
                        totalBeepersLeftInMine--;
                    } else {
                        moveRobot(this);
                    }
                }
                turnLeft();
                turnLeft();
                while (avenidaActual != 13) {
                    moveRobot(this);
                }
            }

            public void putBeepersAndWait() {
                while (beepersInBag > 0) {
                    putBeeper();
                    beepersInBag--;
                    beepersWaitingForTrain++;
                }

                //---
                //Después de que los mineros pongan beepers notificamos a los trenes para que comprueben
                // si ya hay los suficientes beepers para recoger
                lock.lock();
                try {
                    nuevosBeepersDeLosMineros.signalAll();
                } finally {
                    lock.unlock();
                }
                //---

                turnLeft();
                moveRobot(this);
                turnLeft();
                moveRobot(this);
                turnLeft();
            }
        }

        class Tren extends Car {
            public Tren(int Street, int Avenue, Direction direction, int beeps, Color robotColor) {
                super(Street, Avenue, direction, beeps, robotColor);
            }
        }

        class Extractor extends Car {
            public Extractor(int Street, int Avenue, Direction direction, int beeps, Color robotColor) {
                super(Street, Avenue, direction, beeps, robotColor);
            }

            public void extractAndReturn() {
                while (beepersInExtractionPoint < 5) {
                    System.out.println("WAITING FOR TRENES");

                    //--
                    //con el .await() ponemos a dormir el hilo hasta que se le notifique con .signalAll()
                    lock2.lock();
                    try {
                        nuevosBeepersDeLosTrenes.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock2.unlock();
                    }
                    //--
                }
                moveRobot(this);
                turnLeft();
                turnLeft();
                turnLeft();
                moveRobot(this);
                turnLeft();
                turnLeft();
                turnLeft();
                moveRobot(this);
                turnLeft();
                while (this.calleActual != 1) {
                    moveRobot(this);
                }
                turnLeft();
                try {
                    onlyOneCarInExtractionPoint.acquire();

                    while (extractionPointInterception) {
                        System.out.println("WAITING FOR EXTRACTORS");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    onlyOneCarInExtractionPoint.release();
                }
                moveRobot(this);

                while (this.beepersInBag < 5 && totalBeepersLeftToBeExtracted > 0) {
                    this.pickBeeper();
                    this.beepersInBag++;
                    totalBeepersLeftToBeExtracted--;
                    beepersInExtractionPoint--;
                }

                turnLeft();
                turnLeft();
                moveRobot(this);
                turnLeft();
                turnLeft();
                turnLeft();
                while (this.calleActual != 7) {
                    moveRobot(this);
                }
                turnLeft();
                turnLeft();
                turnLeft();
                moveRobot(this);
                turnLeft();
                moveRobot(this);
                turnLeft();
                turnLeft();
                turnLeft();
                moveRobot(this);
                turnLeft();
                turnLeft();
                turnLeft();
                moveRobot(this);
            }

            public void putAndReturn() {
                while (this.beepersInBag > 0) {
                    this.putBeeper();
                    this.beepersInBag--;
                }
                this.turnLeft();
                while (this.avenidaActual != 7) {
                    moveRobot(this);
                }
                this.turnLeft();
                while (this.calleActual != 15) {
                    moveRobot(this);
                }
                this.turnLeft();
                while (this.avenidaActual != 1) {
                    moveRobot(this);
                }
                this.turnLeft();
                while (this.calleActual != 8) {
                    moveRobot(this);
                }
                this.turnLeft();
            }
        }
        // Definir la cantidad de Mineros a crear y las coordenadas iniciales de los mineros
        int cantidadMineros = 2;
        int minerosCalleInicial = 12;
        int minerosAvenidaInicial = 8;

        int cantidadTren = 3;
        int TrenCalleInicial = 13;
        int TrenAvenidaInicial = 8;

        int cantidadExtractor = 5;
        int ExtractorCalleInicial = 14;
        int ExtractorAvenidaInicial = 8;

        // Crear los robots mineros
        for (int i = 0; i < cantidadMineros; i++) {
            // Calcular la avenida para el nuevo minero
            int nuevaAvenida = minerosAvenidaInicial + i;

            Minero minero = new Minero(minerosCalleInicial, nuevaAvenida, West, 0, Color.black);
            robots.add(minero); // Agregar el robot al ArrayList
        }

        for (int i = 0; i < cantidadTren; i++) {
            // Calcular la avenida para el nuevo minero
            int nuevaAvenida = TrenAvenidaInicial + i;

            Tren tren = new Tren(TrenCalleInicial, nuevaAvenida, West, 0, Color.blue);
            robots.add(tren); // Agregar el robot al ArrayList
        }

        for (int i = 0; i < cantidadExtractor; i++) {
            // Calcular la avenida para el nuevo minero
            int nuevaAvenida = ExtractorAvenidaInicial + i;

            Extractor karel = new Extractor(ExtractorCalleInicial, nuevaAvenida, West, 0, Color.red);
            robots.add(karel); // Agregar el robot al ArrayList
        }

        // Iniciar un hilo para cada carro
        for (Robot robot : robots) {

            if (robot instanceof Minero) {
                new Thread(() -> {
                    Minero minero = (Minero) robot;
                    gotoMineVein(minero);
                    while (totalBeepersLeftInMine > 0) {
                        try {
                            onlyOneMines.acquire();
                            ((Minero) robot).mine();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            onlyOneMines.release();
                        }
                        ((Minero) robot).putBeepersAndWait();
                    }
                    if (!siguientePosicionOcupada((Minero) robot)) {
                        moveRobot((Minero) robot);

                    }
                }).start();
            } else if (robot instanceof Tren) {
                new Thread(() -> {
                    Tren tren = (Tren) robot;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    enterMine(tren);
                    while (totalBeepersLeftToBeTransported > 0) {
                        trainInterceptionWithTrain(tren);
                    }
                }).start();
            } else if (robot instanceof Extractor) {
                new Thread(() -> {
                    Extractor karel = (Extractor) robot;
                    gotoExtractionPoint(karel);
                    while (totalBeepersLeftToBeExtracted > 0) {
                        try {
                            onlyOneExtractorExtract.acquire();
                            ((Extractor) robot).extractAndReturn();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            onlyOneExtractorExtract.release();
                        }
                        ((Extractor) robot).putAndReturn();
                    }
                }).start();
            }
        }
    }

    private static void enterMine(Car juanitoAlimana) {
        while (juanitoAlimana.avenidaActual != 2) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 7) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        moveRobot(juanitoAlimana);

        juanitoAlimana.turnLeft();
        while (juanitoAlimana.calleActual != 1) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
    }


    public static void trainInterceptionWithTrain(Car juanitoAlimana) {
        while (juanitoAlimana.avenidaActual != 8) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 5) {
            moveRobot(juanitoAlimana);
        }

        try {
            onlyOneTrainInterception.acquire();

            while (trainInterception) {

                System.out.println("WAITING FOR TRAIN");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            onlyOneTrainInterception.release();
        }


        goToMineTrenRaw(juanitoAlimana);

    }

    public static void goToMineTrenRaw(Car juanitoAlimana) {

        while (juanitoAlimana.calleActual != 11) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.avenidaActual != 12) {
            moveRobot(juanitoAlimana);
        }
        try {
            onlyOneTrainExtract.acquire();

            while (beepersWaitingForTrain < 10) {
                System.out.println("WAITING FOR MINERS");

                //--
                //con el .await() ponemos a dormir el hilo hasta que se le notifique con .signalAll()
                lock.lock();
                try {
                    // Esperar hasta que el procesamiento esté completo
                    nuevosBeepersDeLosMineros.await();
                } finally {
                    lock.unlock();
                }
                //--
            }

            moveRobot(juanitoAlimana);

            while (juanitoAlimana.beepersInBag < 10) {
                juanitoAlimana.pickBeeper();
                juanitoAlimana.beepersInBag++;
                beepersWaitingForTrain--;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            onlyOneTrainExtract.release();
        }
        trainGotoExtractionPoint(juanitoAlimana);
    }

    public static void gotoExtractionPoint(Car juanitoAlimana) {
        while (juanitoAlimana.avenidaActual != 1) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 8) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
    }

    public static void gotoMineVein(Car juanitoAlimana) {
        enterMine(juanitoAlimana);

        while (juanitoAlimana.avenidaActual != 8) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 6) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.avenidaActual != 13) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 10) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.avenidaActual != 14) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
    }

    public static void trainGotoExtractionPoint(Car juanitoAlimana) {
        while (juanitoAlimana.avenidaActual != 13) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 6) {
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.avenidaActual != 3) {
            moveRobot(juanitoAlimana);
            trainInterception = juanitoAlimana.avenidaActual == 9 || juanitoAlimana.avenidaActual == 10;
                System.out.println("Train Interception: " + trainInterception);
        }

        juanitoAlimana.turnLeft();

        while (juanitoAlimana.calleActual != 2) {
            moveRobot(juanitoAlimana);
            extractionPointInterception = juanitoAlimana.calleActual == 2;
        }

        trainToPutBipper(juanitoAlimana);
    }

    public static void trainToPutBipper(Car juanitoAlimana) {


        while (juanitoAlimana.calleActual != 1) {
            moveRobot(juanitoAlimana);

        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.avenidaActual != 2) {
            moveRobot(juanitoAlimana);

        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();

        while (juanitoAlimana.beepersInBag > 0) {
            juanitoAlimana.putBeeper();
            juanitoAlimana.beepersInBag--;
            beepersInExtractionPoint++;
            totalBeepersLeftToBeTransported--;
            System.out.println("Beepers in extraction point: " + beepersInExtractionPoint);
        }
        extractionPointInterception = false;
        //---
        //Después de que los trenes pongan beepers notificamos a los extractores para que comprueben
        // si ya hay los suficientes beepers para recoger
        lock2.lock();
        try {
            nuevosBeepersDeLosTrenes.signalAll();
        } finally {
            lock2.unlock();
        }
        //---

        while (juanitoAlimana.avenidaActual != 4) {
            moveRobot(juanitoAlimana);
        }
    }

/*
    public static void ExtractorExterior(Car juanitoAlimana){
        while(juanitoAlimana.beepersInBag < 5) {
            while (!juanitoAlimana.nextToABeeper() && beepersInExtractionPoint > 0) {
                moveRobot(juanitoAlimana);
            }
            while (juanitoAlimana.nextToABeeper() && juanitoAlimana.beepersInBag < 5) {
                juanitoAlimana.pickBeeper();
                juanitoAlimana.beepersInBag++;
                beepersInExtractionPoint--;
                System.out.println("Beepers in extraction point: " + beepersInExtractionPoint);
            }
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.calleActual != 1){
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.avenidaActual != 1){
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.calleActual != 7){
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.avenidaActual != 2){
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.calleActual != 8){
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.avenidaActual != 7){
            moveRobot(juanitoAlimana);
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.calleActual != 7){
            moveRobot(juanitoAlimana);
        }
        while (juanitoAlimana.beepersInBag > 0){
            juanitoAlimana.putBeeper();
            juanitoAlimana.beepersInBag--;
        }
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        juanitoAlimana.turnLeft();
        while (juanitoAlimana.avenidaActual != 3){
            moveRobot(juanitoAlimana);
        }
    }
*/

    public static void moveRobot(Car juanitoAlimana) {
        while (siguientePosicionOcupada(juanitoAlimana)) {
        }
        if (!siguientePosicionOcupada(juanitoAlimana)) {
            juanitoAlimana.move();
            switch (juanitoAlimana.orientation()) {
                case "north":
                    juanitoAlimana.calleActual++;
                    break;
                case "south":
                    juanitoAlimana.calleActual--;
                    break;
                case "east":
                    juanitoAlimana.avenidaActual++;
                    break;
                case "west":
                    juanitoAlimana.avenidaActual--;
                    break;
            }
            //System.out.println("Coordenadas Ocupadas: "+ juanitoAlimana.calleActual + " " + juanitoAlimana.avenidaActual);
        }
    }

    public static boolean siguientePosicionOcupada(Car juanitoAlimana) {
        int siguienteCalle = juanitoAlimana.getCalleActual();
        int siguienteAvenida = juanitoAlimana.getAvenidaActual();

        switch (juanitoAlimana.orientation()) {
            case "north":
                siguienteCalle++;
                break;
            case "south":
                siguienteCalle--;
                break;
            case "east":
                siguienteAvenida++;
                break;
            case "west":
                siguienteAvenida--;
                break;
        }

        // Verificar si la siguiente posición está ocupada por otro robot
        for (Robot robot : robots) { // Acceder a la lista de robots
            if (robot != juanitoAlimana && ((Car) robot).getCalleActual() == siguienteCalle && ((Car) robot).getAvenidaActual() == siguienteAvenida) {
                return true;
            }
        }

        return false;
    }

    public static int contarBeepersEnLaMina() {
        String fileName = "MundoProyectoV1.kwld";
        int suma = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("beepers")) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 4) {
                        int valor = Integer.parseInt(parts[3]);
                        suma += valor;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("La suma del tercer valor de las líneas beepers es: " + suma);
        return suma;
    }
}

