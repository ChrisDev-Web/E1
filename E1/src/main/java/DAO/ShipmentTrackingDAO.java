package DAO;

import Config.Database;
import Models.ShipmentTracking;
import Repositories.IShipmentTrackingRepository;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// DAO + JDBC: ejecuta procedimientos almacenados relacionados al seguimiento de envios.
public class ShipmentTrackingDAO implements IShipmentTrackingRepository {

    @Override
    public void create(ShipmentTracking tracking) throws SQLException {
        String sql = "{CALL sp_shipment_tracking_create(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, tracking.getIdShipment());

            if (tracking.getIdUser() != null) {
                statement.setInt(2, tracking.getIdUser());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            statement.setString(3, tracking.getLocation());
            statement.setString(4, tracking.getStatus());
            statement.setString(5, tracking.getComments());
            statement.execute();
        }
    }

    @Override
    public void update(ShipmentTracking tracking) throws SQLException {
        String sql = "{CALL sp_shipment_tracking_update(?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, tracking.getIdTracking());
            statement.setInt(2, tracking.getIdShipment());

            if (tracking.getIdUser() != null) {
                statement.setInt(3, tracking.getIdUser());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            statement.setString(4, tracking.getLocation());
            statement.setString(5, tracking.getStatus());
            statement.setString(6, tracking.getComments());
            statement.execute();
        }
    }

    @Override
    public void delete(int idTracking) throws SQLException {
        String sql = "{CALL sp_shipment_tracking_delete(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idTracking);
            statement.execute();
        }
    }

    @Override
    public ShipmentTracking findById(int idTracking) throws SQLException {
        String sql = "{CALL sp_shipment_tracking_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idTracking);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapShipmentTracking(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<ShipmentTracking> list() throws SQLException {
        String sql = "{CALL sp_shipment_tracking_list()}";
        List<ShipmentTracking> trackingList = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                trackingList.add(mapShipmentTracking(resultSet));
            }
        }

        return trackingList;
    }

    @Override
    public List<ShipmentTracking> listByShipment(int idShipment) throws SQLException {
        String sql = "{CALL sp_shipment_tracking_list_by_shipment(?)}";
        List<ShipmentTracking> trackingList = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipment);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trackingList.add(mapShipmentTracking(resultSet));
                }
            }
        }

        return trackingList;
    }

    private ShipmentTracking mapShipmentTracking(ResultSet resultSet) throws SQLException {
        ShipmentTracking tracking = new ShipmentTracking();

        tracking.setIdTracking(resultSet.getInt("id_tracking"));
        tracking.setIdShipment(resultSet.getInt("id_shipment"));

        int idUser = resultSet.getInt("id_user");
        if (resultSet.wasNull()) {
            tracking.setIdUser(null);
        } else {
            tracking.setIdUser(idUser);
        }

        Timestamp trackingDate = resultSet.getTimestamp("tracking_date");

        if (trackingDate != null) {
            tracking.setTrackingDate(trackingDate.toLocalDateTime());
        }

        tracking.setLocation(resultSet.getString("location"));
        tracking.setStatus(resultSet.getString("status"));
        tracking.setComments(resultSet.getString("comments"));

        return tracking;
    }
}