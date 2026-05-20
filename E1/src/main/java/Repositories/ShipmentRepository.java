package Repositories;

import DAO.ShipmentDAO;
import Models.Shipment;
import java.sql.SQLException;
import java.util.List;

// Repository: implementa el contrato de envios apoyandose en la capa DAO.
public class ShipmentRepository implements IShipmentRepository {

    private final ShipmentDAO shipmentDAO;

    public ShipmentRepository() {
        this.shipmentDAO = new ShipmentDAO();
    }

    public ShipmentRepository(ShipmentDAO shipmentDAO) {
        this.shipmentDAO = shipmentDAO;
    }

    @Override
    public void register(Shipment entity) throws SQLException {
        shipmentDAO.register(entity);
    }

    @Override
    public void update(Shipment entity) throws SQLException {
        shipmentDAO.update(entity);
    }

    @Override
    public Shipment findById(Integer id) throws SQLException {
        return shipmentDAO.findById(id);
    }

    @Override
    public Shipment.PaginatedResult search(Shipment.Filter filter) throws SQLException {
        return shipmentDAO.search(filter);
    }

    @Override
    public Shipment findByTrackingCode(String trackingCode) throws SQLException {
        return shipmentDAO.findByTrackingCode(trackingCode);
    }

    @Override
    public Shipment.Detail getDetail(int idShipment) throws SQLException {
        return shipmentDAO.getDetail(idShipment);
    }

    @Override
    public void updateStatus(int idShipment, Shipment.Status status, Integer changedByUserId, String location, String comments) throws SQLException {
        shipmentDAO.updateStatus(idShipment, status, changedByUserId, location, comments);
    }

    @Override
    public void markAsDelivered(int idShipment, Integer changedByUserId, String location, String comments) throws SQLException {
        shipmentDAO.markAsDelivered(idShipment, changedByUserId, location, comments);
    }

    @Override
    public void cancelShipment(int idShipment, Integer changedByUserId, String location, String comments) throws SQLException {
        shipmentDAO.cancelShipment(idShipment, changedByUserId, location, comments);
    }

    @Override
    public void registerTracking(int idShipment, Shipment.Status status, String location, String comments, Integer changedByUserId) throws SQLException {
        shipmentDAO.registerTracking(idShipment, status, location, comments, changedByUserId);
    }

    @Override
    public List<Shipment.ReferenceItem> listWarehouseOptions() throws SQLException {
        return shipmentDAO.listWarehouseOptions();
    }

    @Override
    public List<Shipment.ReferenceItem> listUserOptions() throws SQLException {
        return shipmentDAO.listUserOptions();
    }
}
