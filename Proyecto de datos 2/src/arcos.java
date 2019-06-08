public class arcos{

    int tiempoDestino;
    int idlleg;
    int tiempo;

    public arcos(int idlleg, int tiempo, int tiempoDestino){
        this.idlleg=idlleg;
        this.tiempo=tiempo;
        this.tiempoDestino=tiempoDestino;
    }

    public int getIdlleg(){
        return this.idlleg;
    }

    public int getTiempo(){
        return this.tiempo;
    }
}
