package DAO;

import Config.Database;
import Models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> listar() {
        List<Product> list = new ArrayList<>();
        String sql = "{CALL sp_listar_productos()}";
        
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            
            while (rs.next()) {
                Product prod = new Product(
                    rs.getInt("id_product"),
                    rs.getInt("id_category"),
                    rs.getString("sku"),
                    rs.getString("product_name"),
                    rs.getString("description"),
                    "", // image_path
                    rs.getBigDecimal("unit_weight_kg"),
                    rs.getBigDecimal("unit_price"),
                    rs.getString("status")
                );
                list.add(prod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean registrar(Product p) {
        String sql = "{CALL sp_registrar_producto(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, p.getIdCategory());
            cs.setString(2, p.getSku());
            cs.setString(3, p.getProductName());
            cs.setString(4, p.getDescription());
            cs.setBigDecimal(5, p.getUnitWeightKg());
            cs.setBigDecimal(6, p.getUnitPrice());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificar(Product p) {
        String sql = "{CALL sp_modificar_producto(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, p.getIdProduct());
            cs.setInt(2, p.getIdCategory());
            cs.setString(3, p.getSku());
            cs.setString(4, p.getProductName());
            cs.setString(5, p.getDescription());
            cs.setBigDecimal(6, p.getUnitWeightKg());
            cs.setBigDecimal(7, p.getUnitPrice());
            cs.setString(8, p.getStatus());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL sp_eliminar_producto(?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}