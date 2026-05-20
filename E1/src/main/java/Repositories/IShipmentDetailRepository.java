package Repositories;

import Models.ShipmentDetail;
import java.sql.SQLException;
import java.util.List;

// Interfaces + Repository: define las operaciones para trabajar con detalles de envio.
public interface IShipmentDetailRepository {

    void create(ShipmentDetail detail) throws SQLException;

    void update(ShipmentDetail detail) throws SQLException;

    void delete(int idShipmentDetail) throws SQLException;

    ShipmentDetail findById(int idShipmentDetail) throws SQLException;

    List<ShipmentDetail> list() throws SQLException;

    List<ShipmentDetail> listByShipment(int idShipment) throws SQLException;

    List<ShipmentDetail> listByBox(int idBox) throws SQLException;
}