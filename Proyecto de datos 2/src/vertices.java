import java.util.LinkedList;

public class vertices{

    LinkedList<arcos> lista;
    int id;
    int tiempoDestino;

    public vertices(int id, int tiempoDestino) {
        this.id = id;
        this.tiempoDestino = tiempoDestino;
        lista = new LinkedList<>();
    }

    public int getId(){
        return this.id;
    }

    public int getTiempoDestino(){
        return this.tiempoDestino;
    }
}
