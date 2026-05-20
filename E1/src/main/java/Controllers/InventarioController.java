package Controllers;

import Models.Inventory;
import Models.ReferenceItem;
import Repositories.IInventoryRepository;
import Repositories.IReferenceDataRepository;
import Repositories.InventoryRepository;
import Repositories.ReferenceDataRepository;
import java.sql.SQLException;
import java.util.List;

public class InventarioController {

    private final IInventoryRepository repo;
    private final IReferenceDataRepository referenceDataRepository;

    public InventarioController() {
        this(new InventoryRepository(), new ReferenceDataRepository());
    }

    public InventarioController(IInventoryRepository repo, IReferenceDataRepository referenceDataRepository) {
        this.repo = repo;
        this.referenceDataRepository = referenceDataRepository;
    }

    public void registrarInventario(int idWarehouse, int idProduct, int stock, int reserved, int min) throws Exception {
        if (idWarehouse <= 0 || idProduct <= 0 || stock < 0 || reserved < 0 || min < 0) {
            throw new Exception("Valores numericos invalidos o llaves no seleccionadas.");
        }

        try {
            Inventory i = new Inventory();
            i.setIdWarehouse(idWarehouse);
            i.setIdProduct(idProduct);
            i.setStock(stock);
            i.setReservedStock(reserved);
            i.setMinStock(min);
            repo.save(i);
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new Exception("Este producto ya esta registrado en este almacen. Utilice la opcion de edicion para modificar sus existencias.");
            }

            throw new Exception("Error de consistencia de llaves: " + e.getMessage());
        }
    }

    public void modificarInventario(int idInventory, int stock, int reserved, int min) throws Exception {
        if (idInventory <= 0) {
            throw new Exception("Seleccione un registro de inventario valido.");
        }

        if (stock < 0 || reserved < 0 || min < 0) {
            throw new Exception("Las cantidades fisicas no pueden adoptar valores negativos.");
        }

        try {
            Inventory i = new Inventory();
            i.setIdInventory(idInventory);
            i.setStock(stock);
            i.setReservedStock(reserved);
            i.setMinStock(min);
            repo.update(i);
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Inventory> listarActivos() throws Exception {
        try {
            return repo.search("");
        } catch (SQLException e) {
            throw new Exception("Error al listar existencias: " + e.getMessage());
        }
    }

    public List<Inventory> buscarInventario(String query) throws Exception {
        try {
            return repo.search(query == null ? "" : query.trim());
        } catch (SQLException e) {
            throw new Exception("Error al buscar en el inventario: " + e.getMessage());
        }
    }

    public void transferirExistencias(int idSourceInv, int idTargetWh, int cantidad) throws Exception {
        if (idSourceInv <= 0 || idTargetWh <= 0) {
            throw new Exception("Debe seleccionar un origen y un almacen de destino validos.");
        }

        if (cantidad <= 0) {
            throw new Exception("La cantidad a transferir debe ser mayor a cero.");
        }

        try {
            repo.transferStock(idSourceInv, idTargetWh, cantidad);
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<ReferenceItem> listarAlmacenes() throws Exception {
        try {
            return referenceDataRepository.listWarehouses();
        } catch (SQLException e) {
            throw new Exception("Error al cargar almacenes: " + e.getMessage());
        }
    }

    public List<ReferenceItem> listarProductos() throws Exception {
        try {
            return referenceDataRepository.listProducts();
        } catch (SQLException e) {
            throw new Exception("Error al cargar productos: " + e.getMessage());
        }
    }
}
