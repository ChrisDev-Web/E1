package Controllers;

import DAO.ShipmentDetailDAO;
import Models.ReferenceItem;
import Models.ShipmentDetail;
import Repositories.IReferenceDataRepository;
import Repositories.IShipmentDetailRepository;
import Repositories.ReferenceDataRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ShipmentDetailController {

    private final IShipmentDetailRepository detailRepository;
    private final IReferenceDataRepository referenceDataRepository;

    public ShipmentDetailController() {
        this(new ShipmentDetailDAO(), new ReferenceDataRepository());
    }

    public ShipmentDetailController(IShipmentDetailRepository detailRepository, IReferenceDataRepository referenceDataRepository) {
        this.detailRepository = detailRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    public void createDetail(ShipmentDetail detail) throws Exception {
        validateDetail(detail);

        try {
            detailRepository.create(detail);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void updateDetail(ShipmentDetail detail) throws Exception {
        if (detail == null || detail.getIdShipmentDetail() <= 0) {
            throw new Exception("Seleccione un detalle valido para actualizar.");
        }

        validateDetail(detail);

        try {
            detailRepository.update(detail);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteDetail(int idShipmentDetail) throws Exception {
        if (idShipmentDetail <= 0) {
            throw new Exception("Seleccione un detalle valido para eliminar.");
        }

        try {
            detailRepository.delete(idShipmentDetail);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ShipmentDetail> listDetails() throws Exception {
        try {
            return detailRepository.list();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ShipmentDetail> listDetailsByShipment(int idShipment) throws Exception {
        if (idShipment <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        try {
            return detailRepository.listByShipment(idShipment);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ShipmentDetail> listDetailsByBox(int idBox) throws Exception {
        if (idBox <= 0) {
            throw new Exception("Seleccione una caja valida.");
        }

        try {
            return detailRepository.listByBox(idBox);
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

    public List<ReferenceItem> listBoxOptions() throws Exception {
        try {
            return referenceDataRepository.listBoxes();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ReferenceItem> listBoxOptionsByShipment(int idShipment) throws Exception {
        if (idShipment <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        try {
            return referenceDataRepository.listBoxesByShipment(idShipment);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ReferenceItem> listProductOptions() throws Exception {
        try {
            return referenceDataRepository.listProducts();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<ReferenceItem> listProductOptionsByShipment(int idShipment) throws Exception {
        if (idShipment <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        try {
            return referenceDataRepository.listProductsByShipment(idShipment);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateDetail(ShipmentDetail detail) throws Exception {
        if (detail == null) {
            throw new Exception("Ingrese los datos del detalle de envio.");
        }

        if (detail.getIdShipment() <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        if (detail.getIdBox() <= 0) {
            throw new Exception("Seleccione una caja valida.");
        }

        if (detail.getIdProduct() <= 0) {
            throw new Exception("Seleccione un producto valido.");
        }

        if (detail.getQuantity() <= 0) {
            throw new Exception("La cantidad debe ser mayor que cero.");
        }

        if (detail.getUnitWeightKg() == null || detail.getUnitWeightKg().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El peso unitario no puede ser negativo.");
        }
    }

    private String getSqlMessage(SQLException e) {
        String detail = e.getMessage();

        if (detail != null && !detail.isBlank()) {
            return detail;
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
