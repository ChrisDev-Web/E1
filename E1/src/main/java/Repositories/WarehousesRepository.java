package Repositories;

import DAO.WarehousesDAO;
import Models.Warehouses;
import java.sql.SQLException;
import java.util.List;

public class WarehousesRepository implements IWarehousesRepository {
    private final WarehousesDAO dao = new WarehousesDAO();

    @Override public void save(Warehouses w) throws SQLException { dao.save(w); }
    @Override public void update(Warehouses w) throws SQLException { dao.update(w); }
    @Override public void changeStatus(int id, String status) throws SQLException { dao.changeStatus(id, status); }
    @Override public void deletePermanent(int id) throws SQLException { dao.deletePermanent(id); }
    @Override 
    public List<Warehouses> findByStatus(String status) throws SQLException { 
        return dao.search("", status); }
    @Override public List<Warehouses> search(String query, String status) throws SQLException { return dao.search(query, status); }
}