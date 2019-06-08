import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class main {

    public static void main(String[] args) throws IOException {
        try{
            long TInicio, TFin, tiempo;
            TInicio = System.currentTimeMillis();

            Lector nuevoLector = new Lector();
            String dataset = "dataset-ejemplo-U=205-p=1.2.txt";
            int[][] matrAux = nuevoLector.leerArchivo(dataset);
            double p = nuevoLector.p;
            Dijkstra est = new Dijkstra(matrAux, p);
            vertices[] arrVertAux = (est.ordenarLinkedList(est.ordenarVertices(est.asignarEnObjeto(est.asignarEnArreglo()))));

            String str = est.direccionHashMap(arrVertAux);
            FileWriter fw;
            fw = new FileWriter("Respuesta-" + dataset);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str);
            bw.close();

            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            System.out.println("Tiempo de ejecucion: " + tiempo+" milisegundos");
        }
        catch(Exception e){
            System.out.println("Proceso no realizado");
        }
    }
}
