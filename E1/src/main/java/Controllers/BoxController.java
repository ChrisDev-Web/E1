package Controllers;

import DAO.BoxDAO;
import Models.Box;
import Repositories.IBoxRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
public class BoxController {
    
    private final IBoxRepository boxRepository;

    public BoxController() {
        this.boxRepository = new BoxDAO();
    }

    public BoxController(IBoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    public void createBox(Box box) throws Exception {
        validateBox(box);

        try {
            boxRepository.create(box);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void updateBox(Box box) throws Exception {
        if (box == null || box.getIdBox() <= 0) {
            throw new Exception("Seleccione una caja valida para actualizar.");
        }

        validateBox(box);

        try {
            boxRepository.update(box);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteBox(int idBox) throws Exception {
        if (idBox <= 0) {
            throw new Exception("Seleccione una caja valida para eliminar.");
        }

        try {
            boxRepository.delete(idBox);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Box findBoxById(int idBox) throws Exception {
        if (idBox <= 0) {
            throw new Exception("Seleccione una caja valida.");
        }

        try {
            return boxRepository.findById(idBox);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Box> listBoxes() throws Exception {
        try {
            return boxRepository.list();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Box> listBoxesByShipment(int idShipment) throws Exception {
        if (idShipment <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        try {
            return boxRepository.listByShipment(idShipment);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateBox(Box box) throws Exception {
        if (box == null) {
            throw new Exception("Ingrese los datos de la caja.");
        }

        if (box.getIdShipment() <= 0) {
            throw new Exception("Seleccione un envio valido.");
        }

        if (box.getBoxCode() == null || box.getBoxCode().trim().isEmpty()) {
            throw new Exception("Ingrese el codigo de la caja.");
        }

        validatePositive(box.getLengthCm(), "El largo debe ser mayor que cero.");
        validatePositive(box.getWidthCm(), "El ancho debe ser mayor que cero.");
        validatePositive(box.getHeightCm(), "El alto debe ser mayor que cero.");
        validateZeroOrPositive(box.getWeightKg(), "El peso no puede ser negativo.");
        validateZeroOrPositive(box.getDeclaredValue(), "El valor declarado no puede ser negativo.");

        if (!isValidBoxStatus(box.getStatus())) {
            throw new Exception("Seleccione un estado valido para la caja.");
        }
    }

    private void validatePositive(BigDecimal value, String message) throws Exception {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception(message);
        }
    }

    private void validateZeroOrPositive(BigDecimal value, String message) throws Exception {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception(message);
        }
    }

    private boolean isValidBoxStatus(String status) {
        return "PACKED".equals(status)
                || "SHIPPED".equals(status)
                || "IN_TRANSIT".equals(status)
                || "DELIVERED".equals(status)
                || "DAMAGED".equals(status);
    }

    private String getSqlMessage(SQLException e) {
        String detail = e.getMessage();

        if (detail != null) {
            String lowerDetail = detail.toLowerCase();

            if (lowerDetail.contains("duplicate")) {
                return "Ya existe una caja con ese codigo.";
            }

            if (lowerDetail.contains("foreign key")) {
                return "El envio seleccionado no existe o no es valido.";
            }

            if (!detail.isBlank()) {
                return detail;
            }
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
