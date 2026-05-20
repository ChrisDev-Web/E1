package Repositories;

import Models.Shipment;
import java.sql.SQLException;
import java.util.List;

// Interfaces + Repository: contrato completo del modulo de envios.
public interface IShipmentRepository extends
        IRepositoryRegister<Shipment>,
        IRepositoryUpdate<Shipment>,
        IRepositoryFindById<Shipment, Integer>,
        IRepositorySearch<Shipment.PaginatedResult, Shipment.Filter> {

    // Interfaces: obtiene un envio por su codigo de tracking.
    Shipment findByTrackingCode(String trackingCode) throws SQLException;

    // Interfaces: obtiene el detalle completo de un envio.
    Shipment.Detail getDetail(int idShipment) throws SQLException;

    // Interfaces: actualiza solo el estado del envio y registra seguimiento.
    void updateStatus(int idShipment, Shipment.Status status, Integer changedByUserId, String location, String comments) throws SQLException;

    // Interfaces: marca un envio como entregado y registra seguimiento.
    void markAsDelivered(int idShipment, Integer changedByUserId, String location, String comments) throws SQLException;

    // Interfaces: cancela un envio y registra seguimiento.
    void cancelShipment(int idShipment, Integer changedByUserId, String location, String comments) throws SQLException;

    // Interfaces: registra manualmente un seguimiento adicional.
    void registerTracking(int idShipment, Shipment.Status status, String location, String comments, Integer changedByUserId) throws SQLException;

    // Interfaces: lista almacenes activos para combos.
    List<Shipment.ReferenceItem> listWarehouseOptions() throws SQLException;

    // Interfaces: lista usuarios activos para combos.
    List<Shipment.ReferenceItem> listUserOptions() throws SQLException;
}
