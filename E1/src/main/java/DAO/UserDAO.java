package DAO;

import Config.Database;
import Models.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// DAO + JDBC: ejecuta procedimientos almacenados relacionados al usuario.
public class UserDAO {

    // DAO + JDBC: ejecuta el SP de registro persistiendo username y password_hash.
    public void register(User user) throws SQLException {
        String sql = "{CALL sp_user_register(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            // JDBC: envia el username al procedimiento almacenado.
            statement.setString(1, user.getUserName());
            // JDBC: envia el hash de la contrasena al procedimiento almacenado.
            statement.setString(2, user.getPassword());
            statement.execute();
        }
    }

    // DAO + JDBC: ejecuta el SP de login buscando solo por username.
    public User login(String userName) throws SQLException {
        String sql = "{CALL sp_user_login(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            // JDBC: envia el username que se quiere autenticar.
            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                // DAO + POO: si el SP devuelve una fila, la convierte al modelo User.
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    // DAO + POO: convierte el resultado del SP al objeto User.
    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();

        // JDBC: lee el id del usuario desde la columna id_user.
        user.setIdUser(resultSet.getInt("id_user"));
        // JDBC: lee el username exacto segun la tabla Users.
        user.setUserName(resultSet.getString("username"));
        // JDBC + Seguridad: lee el hash de la contrasena segun la tabla Users.
        user.setPassword(resultSet.getString("password_hash"));

        // JDBC: recupera las marcas de tiempo del registro.
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        // POO: convierte created_at a LocalDateTime si existe.
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        // POO: convierte updated_at a LocalDateTime si existe.
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}
