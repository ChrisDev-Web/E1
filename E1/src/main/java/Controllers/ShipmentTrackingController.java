package Controllers;

import DAO.ShipmentTrackingDAO;
import Models.ReferenceItem;
import Models.ShipmentTracking;
import Repositories.IReferenceDataRepository;
import Repositories.IShipmentTrackingRepository;
import Repositories.ReferenceDataRepository;
import java.sql.SQLException;
import java.util.List;

public class ShipmentTrackingController {

    private final IShipmentTrackingRepository trackingRepository;
    private final IReferenceDataRepository referenceDataRepository;

    public ShipmentTrackingController() {
        this(new ShipmentTrackingDAO(), new ReferenceDataRepository());
    }

    public ShipmentTrackingController(IShipmentTrackingRepository trackingRepository, IReferenceDataRepository referenceDataRepository) {
        this.trackingRepository = trackingRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    public void createTracking(ShipmentTracking tracking) throws Exception {
        validateTracking(tracking);

        try {
            trackingRepository.create(tracking);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void updateTracking(ShipmentTracking tracking) throws Exception {
        if (tracking == null || tracking.getIdTracking() <= 0) {
            throw new Exception("Seleccione un seguimiento valido para actualizar.");
        }

        validateTracking(tracking);

        try {
            trackingRepository.update(tracking);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteTracking(int idTracking) throws Exception {
        if (idTracking <= 0) {
            throw new Exception("Seleccione un seguimiento valido para eliminar.");
        }

        try {
            trackingRepository.delete(idTracking);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ShipmentTracking> listTracking() throws Exception {
        try {
            return trackingRepository.list();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ShipmentTracking> listTrackingByShipment(int idShipment) throws Exception {
        if (idShipment <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        try {
            return trackingRepository.listByShipment(idShipment);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ReferenceItem> listShipmentOptions() throws Exception {
        try {
            return referenceDataRepository.listShipments();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ReferenceItem> listUserOptions() throws Exception {
        try {
            return referenceDataRepository.listUsers();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateTracking(ShipmentTracking tracking) throws Exception {
        if (tracking == null) {
            throw new Exception("Ingrese los datos del seguimiento.");
        }

        if (tracking.getIdShipment() <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        if (tracking.getLocation() == null || tracking.getLocation().trim().isEmpty()) {
            throw new Exception("Ingrese la ubicacion del seguimiento.");
        }

        if (!isValidTrackingStatus(tracking.getStatus())) {
            throw new Exception("Seleccione un estado valido para el seguimiento.");
        }
    }

    private boolean isValidTrackingStatus(String status) {
        return "PENDING".equals(status)
                || "PREPARING".equals(status)
                || "SHIPPED".equals(status)
                || "IN_TRANSIT".equals(status)
                || "DELIVERED".equals(status)
                || "CANCELLED".equals(status);
    }

    private String getSqlMessage(SQLException e) {
        String detail = e.getMessage();

        if (detail != null && !detail.isBlank()) {
            return detail;
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
