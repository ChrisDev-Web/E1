package DAO;

import Config.Database;
import Models.ReferenceItem;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReferenceDataDAO {

    public List<ReferenceItem> listWarehouses() throws SQLException {
        return callReferenceProcedure(
                "{CALL sp_warehouse_list_for_combo()}",
                "SELECT id_warehouse AS id, warehouse_name AS label FROM Warehouses ORDER BY warehouse_name"
        );
    }

    public List<ReferenceItem> listProducts() throws SQLException {
        return callReferenceProcedure(
                "{CALL sp_product_list_for_combo()}",
                "SELECT id_product AS id, CONCAT(sku, ' - ', product_name) AS label FROM Products WHERE status = 'ACTIVE' ORDER BY product_name"
        );
    }

    public List<ReferenceItem> listShipments() throws SQLException {
        return callReferenceProcedure(
                "{CALL sp_shipment_list_for_combo()}",
                "SELECT id_shipment AS id, CONCAT(tracking_code, ' - ', status) AS label FROM Shipments ORDER BY shipment_date DESC, id_shipment DESC"
        );
    }

    public List<ReferenceItem> listUsers() throws SQLException {
        return callReferenceProcedure(
                "{CALL sp_user_list_for_combo()}",
                "SELECT id_user AS id, username AS label FROM Users ORDER BY username"
        );
    }

    public List<ReferenceItem> listBoxes() throws SQLException {
        return callReferenceProcedure(
                "{CALL sp_box_list_for_combo()}",
                "SELECT id_box AS id, CONCAT(box_code, ' - Envio ', id_shipment) AS label FROM Boxes ORDER BY id_shipment DESC, box_code"
        );
    }

    public List<ReferenceItem> listBoxesByShipment(int idShipment) throws SQLException {
        try {
            return callReferenceProcedure("{CALL sp_box_list_for_combo_by_shipment(?)}", idShipment);
        } catch (SQLException ex) {
            String sql = "SELECT id_box AS id, box_code AS label FROM Boxes WHERE id_shipment = ? ORDER BY box_code";
            List<ReferenceItem> items = new ArrayList<>();

            try (
                    Connection connection = Database.getConnection();
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                statement.setInt(1, idShipment);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        items.add(mapReferenceItem(resultSet));
                    }
                }
            }

            return items;
        }
    }

    private List<ReferenceItem> callReferenceProcedure(String procedureSql, String fallbackSql) throws SQLException {
        try {
            return callReferenceProcedure(procedureSql);
        } catch (SQLException ex) {
            return queryReferenceItems(fallbackSql);
        }
    }

    private List<ReferenceItem> callReferenceProcedure(String procedureSql) throws SQLException {
        List<ReferenceItem> items = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(procedureSql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                items.add(mapReferenceItem(resultSet));
            }
        }

        return items;
    }

    private List<ReferenceItem> callReferenceProcedure(String procedureSql, int id) throws SQLException {
        List<ReferenceItem> items = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(procedureSql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapReferenceItem(resultSet));
                }
            }
        }

        return items;
    }

    private List<ReferenceItem> queryReferenceItems(String sql) throws SQLException {
        List<ReferenceItem> items = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                items.add(mapReferenceItem(resultSet));
            }
        }

        return items;
    }

    private ReferenceItem mapReferenceItem(ResultSet resultSet) throws SQLException {
        return new ReferenceItem(
                resultSet.getInt("id"),
                resultSet.getString("label")
        );
    }
}
