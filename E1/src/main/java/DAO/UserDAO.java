package DAO;

import Config.Database;
import Models.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void register(User user) throws SQLException {
        String sql = "{CALL sp_user_register(?, ?, ?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getStatus() == null ? "ACTIVE" : user.getStatus());
            statement.execute();
        }
    }

    public void update(User user) throws SQLException {
        String sql = "{CALL sp_user_update(?, ?, ?, ?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, user.getIdUser());
            statement.setString(2, user.getUserName());

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, user.getPassword());
            }

            statement.setString(4, user.getStatus());
            statement.execute();
        }
    }

    public void deleteById(int idUser) throws SQLException {
        String sql = "{CALL sp_user_delete(?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idUser);
            statement.execute();
        }
    }

    public User findById(int idUser) throws SQLException {
        String sql = "{CALL sp_user_find_by_id(?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idUser);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    public List<User> search(String query, String status) throws SQLException {
        String sql = "{CALL sp_user_search(?, ?)}";
        List<User> users = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, query == null ? "" : query.trim());
            statement.setString(2, status == null ? "ALL" : status.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }
        }

        return users;
    }

    public User login(String userName) throws SQLException {
        String sql = "{CALL sp_user_login(?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setIdUser(resultSet.getInt("id_user"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password_hash"));
        user.setStatus(readColumn(resultSet, "status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }

    private String readColumn(ResultSet resultSet, String column) throws SQLException {
        try {
            String value = resultSet.getString(column);
            return value == null ? null : value.trim();
        } catch (SQLException ex) {
            return null;
        }
    }
}
