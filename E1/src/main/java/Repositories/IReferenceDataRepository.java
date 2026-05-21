package Repositories;

import Models.ReferenceItem;
import java.sql.SQLException;
import java.util.List;

public interface IReferenceDataRepository {

    List<ReferenceItem> listWarehouses() throws SQLException;

    List<ReferenceItem> listProducts() throws SQLException;

    List<ReferenceItem> listProductsByShipment(int idShipment) throws SQLException;

    List<ReferenceItem> listShipments() throws SQLException;

    List<ReferenceItem> listUsers() throws SQLException;

    List<ReferenceItem> listBoxes() throws SQLException;

    List<ReferenceItem> listBoxesByShipment(int idShipment) throws SQLException;
}
