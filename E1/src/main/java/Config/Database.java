package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// JDBC: centraliza la configuracion y apertura de conexiones a MySQL.
public class Database {

    // JDBC: driver del proveedor MySQL.
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    // JDBC: URL de conexion a la base de datos del proyecto.
    private static final String URL = "jdbc:mysql://localhost:3306/Logistics?useSSL=false&serverTimezone=America/Lima";
    // JDBC: usuario con el que se abre la conexion.
    private static final String USER = "root";
    // JDBC: contrasena del usuario de base de datos.
    private static final String PASSWORD = "123456";

    static {
        try {
            // JDBC: carga el driver una sola vez cuando la clase se inicializa.
            Class.forName(DRIVER);
            System.out.println("Driver de MySQL cargado correctamente.");
        } catch (ClassNotFoundException ex) {
            System.out.println("No se pudo cargar el driver de MySQL: " + ex.getMessage());
        }
    }

    private Database() {
    }

    // JDBC + DAO: devuelve una conexion lista para usar desde la capa DAO.
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            // Si falla la conexion, informa el motivo y devuelve null.
            System.out.println("No se pudo conectar a la base de datos: " + ex.getMessage());
            return null;
        }
    }
}
