package Repositories;

import Models.Inventory;
import java.sql.SQLException;
import java.util.List;

public interface IInventoryRepository {
    void save(Inventory inventory) throws SQLException;
    void update(Inventory inventory) throws SQLException;

    List<Inventory> search(String query) throws SQLException;
    
    public void transferStock(int idSourceInv, int idTargetWh, int qty) throws SQLException;;
}