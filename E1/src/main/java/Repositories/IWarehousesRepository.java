package Repositories;

import Models.Inventory;
import Models.Warehouses;
import java.sql.SQLException;
import java.util.List;

public interface IWarehousesRepository {
    void save(Warehouses warehouse) throws SQLException;
    void update(Warehouses warehouse) throws SQLException;
    void changeStatus(int id, String status) throws SQLException;
    void deletePermanent(int id) throws SQLException;
    List<Warehouses> findByStatus(String status) throws SQLException;
    List<Warehouses> search(String query, String status) throws SQLException;
    List<Warehouses> findByStatusWithSummary(String status) throws SQLException;
    List<Warehouses> searchWithSummary(String query, String status) throws SQLException;
    List<Inventory> findInventoryDetail(int idWarehouse) throws SQLException;
}
