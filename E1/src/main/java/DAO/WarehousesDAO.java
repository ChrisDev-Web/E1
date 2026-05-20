package DAO;

import Config.Database;
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
    
}
