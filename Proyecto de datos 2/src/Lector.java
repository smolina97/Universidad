import java.io.*;

public class Lector{

    double p;

    public int[][] leerArchivo(String filename) throws IOException{

        int[][] matriz= new int[0][0];

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String linea = br.readLine();
            String lineap = linea;
            String[] splitLineap = lineap.split(" ");
            p = Double.parseDouble(splitLineap[splitLineap.length-1]);
            linea = br.readLine();
            linea = br.readLine();
            linea = br.readLine();
            linea = br.readLine();
            int contador = 0;
            while(!(linea.contains("Costo"))&& !linea.isEmpty()) {
                contador++;
                linea = br.readLine();
            }
            linea = br.readLine();
            linea = br.readLine();
            linea = br.readLine();
            matriz = new int[contador+1][contador+1];
            while(linea != null){
                if(!linea.isEmpty()){
                    String[] coord = linea.split(" ");
                    matriz[Integer.parseInt(coord[0])][Integer.parseInt(coord[1])] = Integer.parseInt(coord[2]);
                    linea = br.readLine();
                }
            }
        }catch(FileNotFoundException ex) {
            System.out.println("El archivo de lectura no existe");
            System.out.println(ex);
        }
        return matriz;
    }
}