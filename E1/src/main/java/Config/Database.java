package Config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// JDBC: centraliza la configuracion y apertura de conexiones a MySQL.
public class Database {

    private static final Properties PROPERTIES = loadProperties();

    private static final String DRIVER = resolveConfig("DB_DRIVER", "db.driver", "com.mysql.cj.jdbc.Driver");
    private static final String URL = resolveConfig("DB_URL", "db.url", "jdbc:mysql://localhost:3306/Logistics1?useSSL=false&serverTimezone=America/Lima");
    private static final String USER = resolveConfig("DB_USER", "db.user", "root");
    private static final String PASSWORD = resolveConfig("DB_PASSWORD", "db.password", "123456");

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
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            // JDBC: propaga un mensaje claro para evitar errores visuales vacios en la interfaz.
            throw new SQLException(
                    "No se pudo conectar a la base de datos. Revise que MySQL este iniciado y que la configuracion sea correcta.",
                    ex
            );
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = Database.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ex) {
            System.out.println("No se pudo leer database.properties: " + ex.getMessage());
        }

        return properties;
    }

    private static String resolveConfig(String envKey, String propertyKey, String defaultValue) {
        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        String propertyValue = PROPERTIES.getProperty(propertyKey);

        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }

        return defaultValue;
    }
}
