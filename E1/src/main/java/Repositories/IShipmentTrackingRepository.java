package Repositories;

import Models.ShipmentTracking;
import java.sql.SQLException;
import java.util.List;

// Interfaces + Repository: define las operaciones para trabajar con seguimiento de envios.
public interface IShipmentTrackingRepository {

    void create(ShipmentTracking tracking) throws SQLException;

    void update(ShipmentTracking tracking) throws SQLException;

    void delete(int idTracking) throws SQLException;

    ShipmentTracking findById(int idTracking) throws SQLException;

    List<ShipmentTracking> list() throws SQLException;

    List<ShipmentTracking> listByShipment(int idShipment) throws SQLException;
}