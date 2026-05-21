package DAO;

import Config.Database;
import Models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> listar() {
        return buscar("");
    }

    public List<Category> buscar(String query) {
        List<Category> lista = new ArrayList<>();
        String sql = "{CALL sp_buscar_categorias(?)}";
        
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, query == null ? "" : query.trim());

            try (ResultSet rs = cs.executeQuery()) {
            
                while (rs.next()) {
                    Category c = new Category();
                    c.setIdCategory(rs.getInt("id_category"));
                    c.setCategoryName(rs.getString("category_name"));
                    c.setDescription(rs.getString("description"));
                    c.setImagePath(rs.getString("image_path"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean registrar(Category c) {
        String sql = "{CALL sp_registrar_categoria(?, ?, ?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, c.getCategoryName());
            cs.setString(2, c.getDescription());
            cs.setString(3, c.getImagePath());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificar(Category c) {
        String sql = "{CALL sp_modificar_categoria(?, ?, ?, ?)}";
        try (Connection con = Database.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, c.getIdCategory());
            cs.setString(2, c.getCategoryName());
            cs.setString(3, c.getDescription());
            cs.setString(4, c.getImagePath());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL sp_eliminar_categoria(?)}";
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
