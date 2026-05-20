package Controllers;

import Models.Inventory;
import Repositories.IInventoryRepository;
import Repositories.InventoryRepository;
import java.sql.SQLException;
import java.util.List;

public class InventarioController {

    private final IInventoryRepository repo = new InventoryRepository();

    public void registrarInventario(int idWarehouse, int idProduct, int stock, int reserved, int min) throws Exception {
        if (idWarehouse <= 0 || idProduct <= 0 || stock < 0 || reserved < 0 || min < 0) {
            throw new Exception("Valores numéricos inválidos o llaves no seleccionadas.");
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
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                throw new Exception("Este producto ya está registrado en este almacén. Utilice la opción de edición para modificar sus existencias.");
            }
            throw new Exception("Error de consistencia de llaves: " + e.getMessage());
        }
    }

    public void modificarInventario(int idInventory, int stock, int reserved, int min) throws Exception {
        if (stock < 0 || reserved < 0 || min < 0) {
            throw new Exception("Las cantidades físicas no pueden adoptar valores negativos.");
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

    // Método que requiere tu "cargarDatos()" de la Vista para listar todo al inicio
    public List<Inventory> listarActivos() throws Exception {
        try {
            return repo.search(""); // Un string vacío trae todo el inventario sin filtrar
        } catch (SQLException e) {
            throw new Exception("Error al listar existencias: " + e.getMessage());
        }
    }

    // Método que requiere tu función "buscar()" al presionar el botón Filtrar
    public List<Inventory> buscarInventario(String query) throws Exception {
        try {
            return repo.search(query);
        } catch (SQLException e) {
            throw new Exception("Error al buscar en el inventario: " + e.getMessage());
        }
    }
    

    public void transferirExistencias(int idSourceInv, int idTargetWh, int cantidad) throws Exception {
        if (idSourceInv <= 0 || idTargetWh <= 0) {
            throw new Exception("Debe seleccionar un origen y un almacén de destino válidos.");
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

    

}
