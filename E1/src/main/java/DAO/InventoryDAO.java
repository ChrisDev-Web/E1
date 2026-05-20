package DAO;

import Config.Database;
import Models.Inventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    // Llama al SP para guardar existencias
    public void save(Inventory i) throws SQLException {
        String sql = "{call sp_inventory_save(?, ?, ?, ?, ?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, i.getIdWarehouse());
            cs.setInt(2, i.getIdProduct());
            cs.setInt(3, i.getStock());
            cs.setInt(4, i.getReservedStock());
            cs.setInt(5, i.getMinStock());
            cs.executeUpdate();
        }
    }

    // Llama al SP para actualizar los datos numéricos de stock
    public void update(Inventory i) throws SQLException {
        String sql = "{call sp_inventory_update(?, ?, ?, ?)}";
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, i.getIdInventory());
            cs.setInt(2, i.getStock());
            cs.setInt(3, i.getReservedStock());
            cs.setInt(4, i.getMinStock());
            cs.executeUpdate();
        }
    }

    // Listar todo el inventario (Llamando al buscador con parámetro vacío)
    public List<Inventory> findAll() throws SQLException {
        return search("");
    }
    
    
    public void transferStock(int idSourceInv, int idTargetWh, int qty) throws SQLException {
    String sql = "{call sp_inventory_transfer(?, ?, ?)}";
    try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
        cs.setInt(1, idSourceInv);
        cs.setInt(2, idTargetWh);
        cs.setInt(3, qty);
        cs.executeUpdate();
    }
}

    // El buscador principal que hace el JOIN usando el SP optimizado
    public List<Inventory> search(String query) throws SQLException {
        List<Inventory> list = new ArrayList<>();
        String sql = "{call sp_inventory_search(?)}";
        
        try (Connection con = Database.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, query);
            
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Inventory i = new Inventory();
                    i.setIdInventory(rs.getInt("id_inventory"));
                    i.setIdWarehouse(rs.getInt("id_warehouse"));
                    i.setIdProduct(rs.getInt("id_product"));
                    i.setStock(rs.getInt("stock"));
                    i.setReservedStock(rs.getInt("reserved_stock"));
                    i.setMinStock(rs.getInt("min_stock"));
                    
                    // Columnas del JOIN mapeadas dinámicamente
                    i.setWarehouseName(rs.getString("warehouse_name"));
                    i.setProductName(rs.getString("product_name"));
                    i.setProductSku(rs.getString("sku"));
                    
                    list.add(i);
                }
            }
        }
        return list;
    }
}