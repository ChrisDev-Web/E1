package Repositories;

import Config.Database;
import Models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements ICategoryRepository {

    @Override
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category cat = new Category(
                    rs.getInt("id_category"),
                    rs.getString("category_name"),
                    rs.getString("description"),
                    rs.getString("image_path")
                );
                list.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean add(Category category) {
        String sql = "INSERT INTO Categories (category_name, description, image_path) VALUES (?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getImagePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Category category) {
        String sql = "UPDATE Categories SET category_name = ?, description = ?, image_path = ? WHERE id_category = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getImagePath());
            ps.setInt(4, category.getIdCategory());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Categories WHERE id_category = ?";
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