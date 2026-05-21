package DAO;

import Config.Database;
import Models.Inventory;
import Models.Warehouses;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehousesDAO {

    public void save(Warehouses w) throws SQLException {
        String sql = "{CALL sp_warehouse_save(?, ?, ?, ?, ?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, w.getWarehouseName());
            cs.setString(2, w.getAddress());
            cs.setString(3, w.getCity());
            cs.setString(4, w.getCountry());
            cs.setString(5, w.getPhone());
            cs.execute();
        }
    }

    public void update(Warehouses w) throws SQLException {
        String sql = "{CALL sp_warehouse_update(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, w.getIdWarehouse());
            cs.setString(2, w.getWarehouseName());
            cs.setString(3, w.getAddress());
            cs.setString(4, w.getCity());
            cs.setString(5, w.getCountry());
            cs.setString(6, w.getPhone());
            cs.execute();
        }
    }
    public void changeStatus(int id, String status) throws SQLException {
        String sql = "{CALL sp_warehouse_change_status(?, ?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, status);
            cs.execute();
        }
    }
    public void deletePermanent(int id) throws SQLException {
        String sql = "{CALL sp_warehouse_delete_permanent(?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
        }
    }
    public List<Warehouses> search(String query, String status) throws SQLException {
        List<Warehouses> list = new ArrayList<>();
        String sql = "{CALL sp_warehouse_search(?, ?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, query);
            cs.setString(2, status);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Warehouses w = new Warehouses(
                        rs.getInt("id_warehouse"),
                        rs.getString("warehouse_name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("phone"),
                        rs.getString("status")
                    );
                    list.add(w);
                }
            }
        }
        return list;
    }

    public List<Warehouses> searchWithSummary(String query, String status) throws SQLException {
        List<Warehouses> list = new ArrayList<>();
        String sql = "{CALL sp_warehouse_search_with_summary(?, ?)}";

        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, query == null ? "" : query);
            cs.setString(2, status);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Warehouses warehouse = new Warehouses(
                            rs.getInt("id_warehouse"),
                            rs.getString("warehouse_name"),
                            rs.getString("address"),
                            rs.getString("city"),
                            rs.getString("country"),
                            rs.getString("phone"),
                            rs.getString("status")
                    );
                    warehouse.setProductCount(rs.getInt("product_count"));
                    warehouse.setTotalStock(rs.getInt("total_stock"));
                    warehouse.setTotalReservedStock(rs.getInt("reserved_stock"));
                    warehouse.setAvailableStock(rs.getInt("available_stock"));
                    list.add(warehouse);
                }
            }
        }

        return list;
    }

    public List<Inventory> findInventoryDetail(int idWarehouse) throws SQLException {
        List<Inventory> list = new ArrayList<>();
        String sql = "{CALL sp_warehouse_inventory_detail(?)}";

        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idWarehouse);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setIdInventory(rs.getInt("id_inventory"));
                    inventory.setIdWarehouse(rs.getInt("id_warehouse"));
                    inventory.setIdProduct(rs.getInt("id_product"));
                    inventory.setStock(rs.getInt("stock"));
                    inventory.setReservedStock(rs.getInt("reserved_stock"));
                    inventory.setMinStock(rs.getInt("min_stock"));
                    inventory.setWarehouseName(rs.getString("warehouse_name"));
                    inventory.setProductSku(rs.getString("sku"));
                    inventory.setProductName(rs.getString("product_name"));
                    list.add(inventory);
                }
            }
        }

        return list;
    }
    
}
