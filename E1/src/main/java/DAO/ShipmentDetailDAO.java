package DAO;

import Config.Database;
import Models.ShipmentDetail;
import Repositories.IShipmentDetailRepository;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// DAO + JDBC: ejecuta procedimientos almacenados relacionados al detalle de envio.
public class ShipmentDetailDAO implements IShipmentDetailRepository {

    @Override
    public void create(ShipmentDetail detail) throws SQLException {
        String sql = "{CALL sp_shipment_detail_create(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, detail.getIdShipment());
            statement.setInt(2, detail.getIdBox());
            statement.setInt(3, detail.getIdProduct());
            statement.setInt(4, detail.getQuantity());
            statement.setBigDecimal(5, detail.getUnitWeightKg());
            statement.execute();
        }
    }

    @Override
    public void update(ShipmentDetail detail) throws SQLException {
        String sql = "{CALL sp_shipment_detail_update(?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, detail.getIdShipmentDetail());
            statement.setInt(2, detail.getIdShipment());
            statement.setInt(3, detail.getIdBox());
            statement.setInt(4, detail.getIdProduct());
            statement.setInt(5, detail.getQuantity());
            statement.setBigDecimal(6, detail.getUnitWeightKg());
            statement.execute();
        }
    }

    @Override
    public void delete(int idShipmentDetail) throws SQLException {
        String sql = "{CALL sp_shipment_detail_delete(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipmentDetail);
            statement.execute();
        }
    }

    @Override
    public ShipmentDetail findById(int idShipmentDetail) throws SQLException {
        String sql = "{CALL sp_shipment_detail_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipmentDetail);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapShipmentDetail(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<ShipmentDetail> list() throws SQLException {
        String sql = "{CALL sp_shipment_detail_list()}";
        List<ShipmentDetail> details = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                details.add(mapShipmentDetail(resultSet));
            }
        }

        return details;
    }

    @Override
    public List<ShipmentDetail> listByShipment(int idShipment) throws SQLException {
        String sql = "{CALL sp_shipment_detail_list_by_shipment(?)}";
        List<ShipmentDetail> details = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipment);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    details.add(mapShipmentDetail(resultSet));
                }
            }
        }

        return details;
    }

    @Override
    public List<ShipmentDetail> listByBox(int idBox) throws SQLException {
        String sql = "{CALL sp_shipment_detail_list_by_box(?)}";
        List<ShipmentDetail> details = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBox);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    details.add(mapShipmentDetail(resultSet));
                }
            }
        }

        return details;
    }

    private ShipmentDetail mapShipmentDetail(ResultSet resultSet) throws SQLException {
        ShipmentDetail detail = new ShipmentDetail();

        detail.setIdShipmentDetail(resultSet.getInt("id_shipment_detail"));
        detail.setIdShipment(resultSet.getInt("id_shipment"));
        detail.setIdBox(resultSet.getInt("id_box"));
        detail.setIdProduct(resultSet.getInt("id_product"));
        detail.setQuantity(resultSet.getInt("quantity"));
        detail.setUnitWeightKg(resultSet.getBigDecimal("unit_weight_kg"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            detail.setCreatedAt(createdAt.toLocalDateTime());
        }

        return detail;
    }
}