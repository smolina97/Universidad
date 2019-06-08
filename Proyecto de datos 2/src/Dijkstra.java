import java.util.*;

public class Dijkstra {
    int [][] matriz;
    double p;

    public Dijkstra(int [][] matriz, double p){
        this.matriz = matriz;
        this.p = p;
    }

    public vertices[] asignarEnArreglo(){

        vertices[] arregloVertices = new vertices[matriz.length-2];

        for (int i = 2; i < matriz.length; i++){
            int tiempo = matriz[i][1];
            vertices nuevoVertice = new vertices(i, tiempo);
            arregloVertices[i-2] = nuevoVertice;
        }
        return arregloVertices;
    }

    public vertices[] asignarEnObjeto(vertices[] arregloVertices){
        for(int i = 2; i < matriz.length; i++){
            for (int j = 2; j<matriz[i-2].length; j++){
                if(i!=j){
                    arcos nuevoArco = new arcos(j, matriz[i][j], matriz[1][j]);
                    arregloVertices[i-2].lista.add(nuevoArco);
                }
            }
        }
        return arregloVertices;
    }

    public vertices[] ordenarVertices(vertices[] arregloVertices) {
        Arrays.sort(arregloVertices, Comparator.comparing(vertices::getTiempoDestino).reversed());
        return arregloVertices;
    }

    public vertices[] ordenarLinkedList(vertices[] arregloVertices){
        for (int i = 0; i<arregloVertices.length; i++) {
            Collections.sort(arregloVertices[i].lista, Comparator.comparingInt(arcos::getTiempo));
        }
        return arregloVertices;
    }


    public String direccionHashMap(vertices[] arregloVertices){
        HashMap<Integer, Integer> mapa = new HashMap<>();

        for (int i = 0; i < arregloVertices.length; i++) {
            mapa.put(arregloVertices[i].id, i);
        }
        return asignarCoches(mapa, arregloVertices);
    }

    public String asignarCoches(HashMap<Integer, Integer> mapa, vertices[] arregloVertices){
        String respuesta = "P= "+p+"\n";
        boolean[] visitados = new boolean[arregloVertices.length+2];
        int contador = 1;

        for (int i = 0; i<arregloVertices.length; i++){
            if (!visitados[arregloVertices[i].getId()]){
                LinkedList<Integer> verticesEnCoche = new LinkedList<>();
                int acumulado = 0;
                int k = i;
                double condicion = arregloVertices[k].tiempoDestino * p;
                int currentCoche = 0;
                respuesta += "\n" + "Coche #" + contador + "\n";
                while(acumulado + arregloVertices[k].tiempoDestino  <= condicion && verticesEnCoche.size() < 5 ) {
                    verticesEnCoche.add(arregloVertices[k].id);

                    if (verticesEnCoche.getLast() != currentCoche) {
                        currentCoche = verticesEnCoche.getLast();
                        respuesta += "Pasajero: " + arregloVertices[k].id + "\n";
                        visitados[arregloVertices[k].getId()] = true;
                        for (arcos lista : arregloVertices[k].lista) {
                            if (!visitados[lista.getIdlleg()]) {
                                if (verticesEnCoche.size() < 5) {
                                    acumulado += lista.tiempo;
                                } else if (verticesEnCoche.size() == 5) {
                                    acumulado += arregloVertices[k].tiempoDestino;
                                }
                                k = mapa.get(lista.getIdlleg());
                                break;
                            }
                        }
                    }
                }
                contador++;
            }
        }
        return respuesta;
    }
}