package Controllers;

import Models.Client;
import Models.Shipment;
import Repositories.ClientRepository;
import Repositories.IClientRepository;
import Repositories.IShipmentRepository;
import Repositories.ShipmentRepository;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

// MVC + POO: coordina validaciones y persistencia del modulo de envios.
public class ShipmentController {

    private final IShipmentRepository shipmentRepository;
    private final IClientRepository clientRepository;

    public ShipmentController() {
        this.shipmentRepository = new ShipmentRepository();
        this.clientRepository = new ClientRepository();
    }

    public ShipmentController(IShipmentRepository shipmentRepository, IClientRepository clientRepository) {
        this.shipmentRepository = shipmentRepository;
        this.clientRepository = clientRepository;
    }

    public void createShipment(Shipment shipment) throws Exception {
        validateShipment(shipment, true);

        try {
            shipmentRepository.register(shipment);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void updateShipment(Shipment shipment) throws Exception {
        if (shipment == null || shipment.getIdShipment() <= 0) {
            throw new Exception("Seleccione un envio valido para editar.");
        }

        validateShipment(shipment, false);

        try {
            shipmentRepository.update(shipment);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Shipment.PaginatedResult searchShipments(Shipment.Filter filter) throws Exception {
        if (filter == null) {
            throw new Exception("No se recibio el filtro de busqueda.");
        }

        if (filter.getPage() <= 0) {
            filter.setPage(1);
        }

        if (filter.getPageSize() <= 0) {
            filter.setPageSize(10);
        }

        try {
            return shipmentRepository.search(filter);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Shipment findShipmentById(int idShipment) throws Exception {
        validateId(idShipment, "Seleccione un envio valido.");

        try {
            return shipmentRepository.findById(idShipment);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Shipment findShipmentByTrackingCode(String trackingCode) throws Exception {
        if (trackingCode == null || trackingCode.trim().isEmpty()) {
            throw new Exception("Escriba un codigo de tracking para consultar.");
        }

        try {
            return shipmentRepository.findByTrackingCode(trackingCode.trim());
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Shipment.Detail getShipmentDetail(int idShipment) throws Exception {
        validateId(idShipment, "Seleccione un envio valido para ver el detalle.");

        try {
            return shipmentRepository.getDetail(idShipment);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void changeShipmentStatus(int idShipment, Shipment.Status status, Integer changedByUserId, String location, String comments) throws Exception {
        validateId(idShipment, "Seleccione un envio valido para cambiar el estado.");

        if (status == null) {
            throw new Exception("Seleccione un estado valido.");
        }

        try {
            shipmentRepository.updateStatus(idShipment, status, changedByUserId, location, comments);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void markShipmentAsDelivered(int idShipment, Integer changedByUserId, String location, String comments) throws Exception {
        validateId(idShipment, "Seleccione un envio valido para marcar como entregado.");

        if (location == null || location.trim().isEmpty()) {
            throw new Exception("Escriba la ubicacion de entrega.");
        }

        try {
            shipmentRepository.markAsDelivered(idShipment, changedByUserId, location.trim(), comments);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void cancelShipment(int idShipment, Integer changedByUserId, String location, String comments) throws Exception {
        validateId(idShipment, "Seleccione un envio valido para cancelar.");

        if (location == null || location.trim().isEmpty()) {
            throw new Exception("Escriba la ubicacion de cancelacion.");
        }

        try {
            shipmentRepository.cancelShipment(idShipment, changedByUserId, location.trim(), comments);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void registerTracking(int idShipment, Shipment.Status status, String location, String comments, Integer changedByUserId) throws Exception {
        validateId(idShipment, "Seleccione un envio valido para registrar seguimiento.");

        if (status == null) {
            throw new Exception("Seleccione un estado para el seguimiento.");
        }

        if (location == null || location.trim().isEmpty()) {
            throw new Exception("Escriba la ubicacion del seguimiento.");
        }

        try {
            shipmentRepository.registerTracking(idShipment, status, location.trim(), comments, changedByUserId);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public List<Client.ReferenceItem> listClientOptions() throws Exception {
        try {
            return clientRepository.listClientOptions();
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public List<Shipment.ReferenceItem> listWarehouseOptions() throws Exception {
        try {
            return shipmentRepository.listWarehouseOptions();
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public List<Shipment.ReferenceItem> listUserOptions() throws Exception {
        try {
            return shipmentRepository.listUserOptions();
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    private void validateShipment(Shipment shipment, boolean creationMode) throws Exception {
        if (shipment == null) {
            throw new Exception("No se recibieron los datos del envio.");
        }

        if (shipment.getIdClient() <= 0) {
            throw new Exception("Seleccione un cliente.");
        }

        if (shipment.getIdWarehouseOrigin() <= 0) {
            throw new Exception("Seleccione el almacen de origen.");
        }

        if (shipment.getIdUser() <= 0) {
            throw new Exception("Seleccione el usuario responsable.");
        }

        if (shipment.getEstimatedDeliveryDate() != null
                && shipment.getEstimatedDeliveryDate().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new Exception("La fecha estimada de entrega no es valida.");
        }

        if (!creationMode && shipment.getStatus() == null) {
            throw new Exception("Seleccione un estado valido para el envio.");
        }
    }

    private void validateId(int id, String message) throws Exception {
        if (id <= 0) {
            throw new Exception(message);
        }
    }

    private String resolveSqlMessage(SQLException e) {
        String message = e.getMessage();

        if (message == null || message.isBlank()) {
            return "Ocurrio un problema al procesar la operacion de envios.";
        }

        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("duplicate") || lowerMessage.contains("duplicada")) {
            return "El codigo de tracking ya existe. Intente nuevamente.";
        }

        return message;
    }
}
