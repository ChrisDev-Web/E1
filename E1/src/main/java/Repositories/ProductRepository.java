package Repositories;

import Config.Database;
import Models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository implements IProductRepository {

    @Override
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product prod = new Product(
                    rs.getInt("id_product"),
                    rs.getInt("id_category"),
                    rs.getString("sku"),
                    rs.getString("product_name"),
                    rs.getString("description"),
                    rs.getString("image_path"),
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

    @Override
    public boolean add(Product product) {
        String sql = "INSERT INTO Products (id_category, sku, product_name, description, image_path, unit_weight_kg, unit_price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, product.getIdCategory());
            ps.setString(2, product.getSku());
            ps.setString(3, product.getProductName());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getImagePath());
            ps.setBigDecimal(6, product.getUnitWeightKg());
            ps.setBigDecimal(7, product.getUnitPrice());
            ps.setString(8, product.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE Products SET id_category = ?, sku = ?, product_name = ?, description = ?, image_path = ?, unit_weight_kg = ?, unit_price = ?, status = ? WHERE id_product = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, product.getIdCategory());
            ps.setString(2, product.getSku());
            ps.setString(3, product.getProductName());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getImagePath());
            ps.setBigDecimal(6, product.getUnitWeightKg());
            ps.setBigDecimal(7, product.getUnitPrice());
            ps.setString(8, product.getStatus());
            ps.setInt(9, product.getIdProduct());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Products WHERE id_product = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}