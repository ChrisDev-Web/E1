package DAO;

import Config.Database;
import Models.Shipment;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// DAO + JDBC: ejecuta los procedimientos almacenados del modulo de envios.
public class ShipmentDAO {

    public void register(Shipment shipment) throws SQLException {
        String sql = "{CALL sp_shipment_create(?, ?, ?, ?, ?, ?, ?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, normalizeText(shipment.getTrackingCode()));
            statement.setInt(2, shipment.getIdClient());
            statement.setInt(3, shipment.getIdWarehouseOrigin());
            statement.setInt(4, shipment.getIdUser());
            setTimestamp(statement, 5, shipment.getEstimatedDeliveryDate());
            statement.setString(6, normalizeText(shipment.getNotes()));

            if (shipment.getChangedByUserId() == null) {
                statement.setNull(7, Types.INTEGER);
            } else {
                statement.setInt(7, shipment.getChangedByUserId());
            }

            statement.execute();
        }
    }

    public void update(Shipment shipment) throws SQLException {
        String sql = "{CALL sp_shipment_update(?, ?, ?, ?, ?, ?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, shipment.getIdShipment());
            setTimestamp(statement, 2, shipment.getEstimatedDeliveryDate());
            statement.setString(3, normalizeText(shipment.getNotes()));
            statement.setInt(4, shipment.getIdWarehouseOrigin());
            statement.setString(5, shipment.getStatus() == null ? null : shipment.getStatus().name());

            if (shipment.getChangedByUserId() == null) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, shipment.getChangedByUserId());
            }

            statement.execute();
        }
    }

    public Shipment findById(int idShipment) throws SQLException {
        String sql = "{CALL sp_shipment_get_by_id(?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipment);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapShipment(resultSet);
                }
            }
        }

        return null;
    }

    public Shipment findByTrackingCode(String trackingCode) throws SQLException {
        String sql = "{CALL sp_shipment_get_by_tracking(?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, normalizeText(trackingCode));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapShipment(resultSet);
                }
            }
        }

        return null;
    }

    public Shipment.PaginatedResult search(Shipment.Filter filter) throws SQLException {
        List<Shipment> items = new ArrayList<>();
        int totalRecords = 0;

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall("{CALL sp_shipment_search(?, ?, ?, ?, ?)}")
        ) {
            statement.setString(1, normalizeText(filter.getSearchText()));
            statement.setString(2, filter.getStatus() == null ? null : filter.getStatus().name());

            if (filter.getShipmentDate() == null) {
                statement.setNull(3, Types.DATE);
            } else {
                statement.setDate(3, Date.valueOf(filter.getShipmentDate()));
            }

            statement.setInt(4, resolveOffset(filter.getPage(), filter.getPageSize()));
            statement.setInt(5, filter.getPageSize());

            boolean hasResult = statement.execute();

            if (hasResult) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    while (resultSet.next()) {
                        items.add(mapShipment(resultSet));
                    }
                }
            }

            if (statement.getMoreResults()) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    if (resultSet.next()) {
                        totalRecords = resultSet.getInt("total_records");
                    }
                }
            }
        }

        return new Shipment.PaginatedResult(items, totalRecords, filter.getPage(), filter.getPageSize());
    }

    public Shipment.Detail getDetail(int idShipment) throws SQLException {
        Shipment.Detail detail = new Shipment.Detail();

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall("{CALL sp_shipment_get_detail(?)}")
        ) {
            statement.setInt(1, idShipment);

            boolean hasResult = statement.execute();
            int resultIndex = 0;

            while (true) {
                if (hasResult) {
                    try (ResultSet resultSet = statement.getResultSet()) {
                        if (resultIndex == 0) {
                            if (resultSet.next()) {
                                detail.setShipment(mapShipment(resultSet));
                            }
                        } else if (resultIndex == 1) {
                            List<Shipment.Tracking> trackingItems = new ArrayList<>();

                            while (resultSet.next()) {
                                trackingItems.add(mapTracking(resultSet));
                            }

                            detail.setTrackingHistory(trackingItems);
                        } else if (resultIndex == 2) {
                            List<String> boxItems = new ArrayList<>();

                            while (resultSet.next()) {
                                String description = readColumn(resultSet, "box_description");

                                if (description != null && !description.isBlank()) {
                                    boxItems.add(description);
                                }
                            }

                            detail.setBoxDetails(boxItems);
                        } else if (resultIndex == 3) {
                            List<String> productItems = new ArrayList<>();

                            while (resultSet.next()) {
                                String description = readColumn(resultSet, "product_description");

                                if (description != null && !description.isBlank()) {
                                    productItems.add(description);
                                }
                            }

                            detail.setProductDetails(productItems);
                        }
                    }
                    resultIndex++;
                }

                if (!statement.getMoreResults()) {
                    if (statement.getUpdateCount() == -1) {
                        break;
                    }

                    hasResult = false;
                    continue;
                }

                hasResult = true;
            }

            if (detail.getBoxDetails().isEmpty()) {
                detail.setBoxDetails(loadBoxDetails(connection, idShipment));
            }

            if (detail.getProductDetails().isEmpty()) {
                detail.setProductDetails(loadProductDetails(connection, idShipment));
            }
        }

        return detail;
    }

    public void updateStatus(int idShipment, Shipment.Status status, Integer changedByUserId, String location, String comments) throws SQLException {
        executeStatusProcedure("{CALL sp_shipment_update_status(?, ?, ?, ?, ?)}", idShipment, status, changedByUserId, location, comments);
    }

    public void markAsDelivered(int idShipment, Integer changedByUserId, String location, String comments) throws SQLException {
        executeStatusProcedure("{CALL sp_shipment_mark_delivered(?, ?, ?, ?)}", idShipment, null, changedByUserId, location, comments);
    }

    public void cancelShipment(int idShipment, Integer changedByUserId, String location, String comments) throws SQLException {
        executeStatusProcedure("{CALL sp_shipment_cancel(?, ?, ?, ?)}", idShipment, null, changedByUserId, location, comments);
    }

    public void registerTracking(int idShipment, Shipment.Status status, String location, String comments, Integer changedByUserId) throws SQLException {
        executeStatusProcedure("{CALL sp_shipment_tracking_register(?, ?, ?, ?, ?)}", idShipment, status, changedByUserId, location, comments);
    }

    public List<Shipment.ReferenceItem> listWarehouseOptions() throws SQLException {
        return listReferenceItems("{CALL sp_warehouse_list_for_combo()}");
    }

    public List<Shipment.ReferenceItem> listUserOptions() throws SQLException {
        return listReferenceItems("{CALL sp_user_list_for_combo()}");
    }

    private void executeStatusProcedure(
            String sql,
            int idShipment,
            Shipment.Status status,
            Integer changedByUserId,
            String location,
            String comments
    ) throws SQLException {
        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idShipment);

            int nextIndex = 2;

            if (status != null) {
                statement.setString(nextIndex++, status.name());
            }

            if (changedByUserId == null) {
                statement.setNull(nextIndex++, Types.INTEGER);
            } else {
                statement.setInt(nextIndex++, changedByUserId);
            }

            statement.setString(nextIndex++, normalizeText(location));
            statement.setString(nextIndex, normalizeText(comments));
            statement.execute();
        }
    }

    private List<Shipment.ReferenceItem> listReferenceItems(String sql) throws SQLException {
        List<Shipment.ReferenceItem> items = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                items.add(new Shipment.ReferenceItem(
                        resultSet.getInt("id"),
                        resultSet.getString("label")
                ));
            }
        }

        return items;
    }

    private Shipment mapShipment(ResultSet resultSet) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setIdShipment(resultSet.getInt("id_shipment"));
        shipment.setTrackingCode(resultSet.getString("tracking_code"));
        shipment.setIdClient(resultSet.getInt("id_client"));
        shipment.setClientName(readColumn(resultSet, "client_name"));
        shipment.setIdWarehouseOrigin(resultSet.getInt("id_warehouse_origin"));
        shipment.setWarehouseName(readColumn(resultSet, "warehouse_name"));
        shipment.setIdUser(resultSet.getInt("id_user"));
        shipment.setUserName(readColumn(resultSet, "user_name"));
        shipment.setShipmentDate(toLocalDateTime(resultSet.getTimestamp("shipment_date")));
        shipment.setEstimatedDeliveryDate(toLocalDateTime(resultSet.getTimestamp("estimated_delivery_date")));
        shipment.setDeliveredAt(toLocalDateTime(resultSet.getTimestamp("delivered_at")));

        String statusValue = resultSet.getString("status");
        if (statusValue != null) {
            shipment.setStatus(Shipment.Status.valueOf(statusValue));
        }

        shipment.setNotes(readColumn(resultSet, "notes"));
        shipment.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        shipment.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        return shipment;
    }

    private Shipment.Tracking mapTracking(ResultSet resultSet) throws SQLException {
        Shipment.Tracking tracking = new Shipment.Tracking();
        tracking.setIdTracking(resultSet.getInt("id_tracking"));
        tracking.setIdShipment(resultSet.getInt("id_shipment"));
        tracking.setIdUser((Integer) resultSet.getObject("id_user"));
        tracking.setUserName(readColumn(resultSet, "user_name"));
        tracking.setTrackingDate(toLocalDateTime(resultSet.getTimestamp("tracking_date")));
        tracking.setLocation(readColumn(resultSet, "location"));
        tracking.setComments(readColumn(resultSet, "comments"));

        String statusValue = resultSet.getString("status");
        if (statusValue != null) {
            tracking.setStatus(Shipment.Status.valueOf(statusValue));
        }

        return tracking;
    }

    private List<String> loadBoxDetails(Connection connection, int idShipment) throws SQLException {
        List<String> items = new ArrayList<>();
        String sql = "SELECT b.box_code, b.length_cm, b.width_cm, b.height_cm, b.weight_kg, b.declared_value, b.status "
                + "FROM Boxes b "
                + "WHERE b.id_shipment = ? "
                + "ORDER BY b.id_box";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idShipment);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String boxCode = readColumn(resultSet, "box_code");
                    String status = readColumn(resultSet, "status");
                    double length = resultSet.getDouble("length_cm");
                    double width = resultSet.getDouble("width_cm");
                    double height = resultSet.getDouble("height_cm");
                    double weight = resultSet.getDouble("weight_kg");
                    double declaredValue = resultSet.getDouble("declared_value");

                    items.add(String.format(
                            Locale.US,
                            "%s | %.2f x %.2f x %.2f cm | %.2f kg | Valor declarado: %.2f | %s",
                            boxCode == null || boxCode.isBlank() ? "Caja sin codigo" : boxCode,
                            length,
                            width,
                            height,
                            weight,
                            declaredValue,
                            status == null || status.isBlank() ? "-" : status
                    ));
                }
            }
        }

        return items;
    }

    private List<String> loadProductDetails(Connection connection, int idShipment) throws SQLException {
        List<String> items = new ArrayList<>();
        String sql = "SELECT p.product_name, sd.quantity, sd.unit_weight_kg, b.box_code "
                + "FROM ShipmentDetails sd "
                + "INNER JOIN Products p ON p.id_product = sd.id_product "
                + "LEFT JOIN Boxes b ON b.id_box = sd.id_box "
                + "WHERE sd.id_shipment = ? "
                + "ORDER BY sd.id_box, p.product_name";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idShipment);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String boxCode = readColumn(resultSet, "box_code");
                    String productName = readColumn(resultSet, "product_name");
                    int quantity = resultSet.getInt("quantity");
                    double unitWeight = resultSet.getDouble("unit_weight_kg");

                    items.add(String.format(
                            Locale.US,
                            "%s | %s | Cantidad: %d | Peso unit.: %.3f kg",
                            boxCode == null || boxCode.isBlank() ? "Sin caja" : boxCode,
                            productName == null || productName.isBlank() ? "Producto sin nombre" : productName,
                            quantity,
                            unitWeight
                    ));
                }
            }
        }

        return items;
    }

    private void setTimestamp(CallableStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private int resolveOffset(int page, int pageSize) {
        return Math.max(page - 1, 0) * Math.max(pageSize, 1);
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }

    private String readColumn(ResultSet resultSet, String column) throws SQLException {
        String value = resultSet.getString(column);
        return value == null ? null : value.trim();
    }
}
