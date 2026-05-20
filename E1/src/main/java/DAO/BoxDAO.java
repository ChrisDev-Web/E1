package DAO;
import Repositories.IBoxRepository;

import Config.Database;
import Models.Box;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// DAO + JDBC: ejecuta procedimientos almacenados relacionados a cajas.
public class BoxDAO implements IBoxRepository {
@Override
    public void create(Box box) throws SQLException {
        String sql = "{CALL sp_box_create(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, box.getIdShipment());
            statement.setString(2, box.getBoxCode());
            statement.setString(3, box.getImagePath());
            statement.setBigDecimal(4, box.getLengthCm());
            statement.setBigDecimal(5, box.getWidthCm());
            statement.setBigDecimal(6, box.getHeightCm());
            statement.setBigDecimal(7, box.getWeightKg());
            statement.setBigDecimal(8, box.getDeclaredValue());
            statement.setString(9, box.getStatus());
            statement.execute();
        }
    }
@Override

    public List<Box> list() throws SQLException {
        String sql = "{CALL sp_box_list()}";
        List<Box> boxes = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                boxes.add(mapBox(resultSet));
            }
        }

        return boxes;
    }
@Override

    public List<Box> listByShipment(int idShipment) throws SQLException {
        String sql = "{CALL sp_box_list_by_shipment(?)}";
        List<Box> boxes = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipment);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    boxes.add(mapBox(resultSet));
                }
            }
        }

        return boxes;
    }
@Override

    public Box findById(int idBox) throws SQLException {
        String sql = "{CALL sp_box_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBox);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBox(resultSet);
                }
            }
        }

        return null;
    }
@Override

    public void update(Box box) throws SQLException {
        String sql = "{CALL sp_box_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, box.getIdBox());
            statement.setInt(2, box.getIdShipment());
            statement.setString(3, box.getBoxCode());
            statement.setString(4, box.getImagePath());
            statement.setBigDecimal(5, box.getLengthCm());
            statement.setBigDecimal(6, box.getWidthCm());
            statement.setBigDecimal(7, box.getHeightCm());
            statement.setBigDecimal(8, box.getWeightKg());
            statement.setBigDecimal(9, box.getDeclaredValue());
            statement.setString(10, box.getStatus());
            statement.execute();
        }
    }
@Override

    public void delete(int idBox) throws SQLException {
        String sql = "{CALL sp_box_delete(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBox);
            statement.execute();
        }
    }

    private Box mapBox(ResultSet resultSet) throws SQLException {
        Box box = new Box();

        box.setIdBox(resultSet.getInt("id_box"));
        box.setIdShipment(resultSet.getInt("id_shipment"));
        box.setBoxCode(resultSet.getString("box_code"));
        box.setImagePath(resultSet.getString("image_path"));
        box.setLengthCm(resultSet.getBigDecimal("length_cm"));
        box.setWidthCm(resultSet.getBigDecimal("width_cm"));
        box.setHeightCm(resultSet.getBigDecimal("height_cm"));
        box.setWeightKg(resultSet.getBigDecimal("weight_kg"));
        box.setDeclaredValue(resultSet.getBigDecimal("declared_value"));
        box.setStatus(resultSet.getString("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        if (createdAt != null) {
            box.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            box.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return box;
    }
}