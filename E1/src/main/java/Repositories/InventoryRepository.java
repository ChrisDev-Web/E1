package Repositories;

import DAO.InventoryDAO;
import Models.Inventory;
import java.sql.SQLException;
import java.util.List;

public class InventoryRepository implements IInventoryRepository {
    private final InventoryDAO dao = new InventoryDAO();

    @Override
     public void save(Inventory i) throws SQLException { dao.save(i); }
    @Override
     public void update(Inventory i) throws SQLException { dao.update(i); }
    @Override
    public List<Inventory> search(String query) throws SQLException { 
        return dao.search(query); 
    }

    @Override
    public void transferStock(int idSourceInv, int idTargetWh, int qty) throws SQLException {
        dao.transferStock(idSourceInv, idTargetWh, qty);
    }  
}