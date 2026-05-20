package Repositories;

import DAO.ReferenceDataDAO;
import Models.ReferenceItem;
import java.sql.SQLException;
import java.util.List;

public class ReferenceDataRepository implements IReferenceDataRepository {

    private final ReferenceDataDAO dao;

    public ReferenceDataRepository() {
        this.dao = new ReferenceDataDAO();
    }

    public ReferenceDataRepository(ReferenceDataDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<ReferenceItem> listWarehouses() throws SQLException {
        return dao.listWarehouses();
    }

    @Override
    public List<ReferenceItem> listProducts() throws SQLException {
        return dao.listProducts();
    }

    @Override
    public List<ReferenceItem> listShipments() throws SQLException {
        return dao.listShipments();
    }

    @Override
    public List<ReferenceItem> listUsers() throws SQLException {
        return dao.listUsers();
    }

    @Override
    public List<ReferenceItem> listBoxes() throws SQLException {
        return dao.listBoxes();
    }

    @Override
    public List<ReferenceItem> listBoxesByShipment(int idShipment) throws SQLException {
        return dao.listBoxesByShipment(idShipment);
    }
}
