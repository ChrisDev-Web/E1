package DAO;

import Config.Database;
import Models.Product;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 1. LISTAR PRODUCTOS CON STORED PROCEDURE
    public List<Product> listar() {
        List<Product> lista = new ArrayList<>();
        String sql = "{call sp_listar_productos()}";

        try (Connection con = Database.getConnection();
             CallableStatement cstmt = con.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            while (rs.next()) {
                // Aquí se crea el objeto usando los datos que devuelve tu base de datos
                Product prod = new Product(
                    rs.getInt("id_product"),
                    rs.getInt("id_category"),
                    rs.getString("sku"),
                    rs.getString("nombre"),       // Se pasa al constructor del modelo
                    rs.getString("descripcion"),
                    rs.getString("categoria_nombre"), 
                    rs.getBigDecimal("peso"),
                    rs.getBigDecimal("precio"),
                    rs.getString("status")
                );
                lista.add(prod);
            }
        } catch (SQLException e) {
            System.out.println("Error en SP Listar Productos: " + e.getMessage());
        }
        return lista;
    }

    // 2. REGISTRAR PRODUCTO CON STORED PROCEDURE
    public boolean registrar(Product prod) {
        String sql = "{call sp_registrar_producto(?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection con = Database.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {

            cstmt.setInt(1, prod.getIdCategory());
            cstmt.setString(2, prod.getSku());
            cstmt.setString(3, prod.getProductName());    // Sincronizado con tu controlador
            cstmt.setString(4, prod.getDescription());    // Cambiado para usar el estándar del grupo
            cstmt.setBigDecimal(5, prod.getUnitWeightKg()); // Sincronizado con tu controlador
            cstmt.setBigDecimal(6, prod.getUnitPrice());   // Sincronizado con tu controlador
            cstmt.setString(7, prod.getStatus());

            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error en SP Registrar Producto: " + e.getMessage());
            return false;
        }
    }

    // 3. MODIFICAR PRODUCTO CON STORED PROCEDURE
    public boolean modificar(Product prod) {
        String sql = "{call sp_modificar_producto(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection con = Database.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {

            cstmt.setInt(1, prod.getIdProduct());
            cstmt.setInt(2, prod.getIdCategory());
            cstmt.setString(3, prod.getSku());
            cstmt.setString(4, prod.getProductName());    // Sincronizado con tu controlador
            cstmt.setString(5, prod.getDescription());    // Cambiado para usar el estándar del grupo
            cstmt.setBigDecimal(6, prod.getUnitWeightKg()); // Sincronizado con tu controlador
            cstmt.setBigDecimal(7, prod.getUnitPrice());   // Sincronizado con tu controlador
            cstmt.setString(8, prod.getStatus());

            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error en SP Modificar Producto: " + e.getMessage());
            return false;
        }
    }

 // 4. ELIMINAR PRODUCTO CON STORED PROCEDURE
    public boolean eliminar(int id) {
        String sql = "{call sp_eliminar_producto(?)}";
        
        try (Connection con = Database.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {

            cstmt.setInt(1, id);
            return cstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error en SP Eliminar Producto: " + e.getMessage());
            return false;
        }
    }
}
                    