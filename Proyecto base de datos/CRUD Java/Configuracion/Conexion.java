package Configuracion;

import java.sql.Connection;
import java.sql.DriverManager;


public class Conexion {
    Connection con;
    public Conexion(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb","smolin19","fui.NHW79");
        } catch (Exception e) {
            System.err.println("Error:" +e);
        }
    }
    
    public Connection getConnction(){
        return con;
    }
}