package Controllers;

import Models.Inventory;
import Models.Warehouses;
import Repositories.IWarehousesRepository;
import Repositories.WarehousesRepository;
import java.sql.SQLException;
import java.util.List;

public class AlmacenesController {
    private final IWarehousesRepository repo;

    public AlmacenesController() {
        this.repo = new WarehousesRepository();
    }

    public void registrarAlmacen(String name, String address, String city, String country, String phone) throws Exception {
        if(name.isBlank() || address.isBlank() || city.isBlank() || country.isBlank()) {
            throw new Exception("Todos los campos obligatorios deben estar completos.");
        }
        try {
            Warehouses w = new Warehouses(0, name.trim(), address.trim(), city.trim(), country.trim(), phone.trim(), "ACTIVE");
            repo.save(w);
        } catch (SQLException e) {
            throw new Exception("Error en BD al guardar almacén: " + e.getMessage());
        }
    }

    public void modificarAlmacen(int id, String name, String address, String city, String country, String phone) throws Exception {
        if(name.isBlank() || address.isBlank() || city.isBlank() || country.isBlank()) {
            throw new Exception("Los campos de edición no pueden quedar vacíos.");
        }
        try {
            Warehouses w = new Warehouses(id, name.trim(), address.trim(), city.trim(), country.trim(), phone.trim(), "ACTIVE");
            repo.update(w);
        } catch (SQLException e) {
            throw new Exception("Error al actualizar almacén: " + e.getMessage());
        }
    }

    public void eliminarLogico(int id) throws Exception {
        try { repo.changeStatus(id, "INACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public void restaurarAlmacen(int id) throws Exception {
        try { repo.changeStatus(id, "ACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public void eliminarDefinitivo(int id) throws Exception {
        try { repo.deletePermanent(id); } catch (SQLException e) { 
            throw new Exception("No se puede eliminar de forma definitiva. Asegúrate de que no existan inventarios vinculados.");
        }
    }

    public List<Warehouses> listarActivos() throws Exception {
        try { return repo.findByStatus("ACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public List<Warehouses> listarActivosConResumen() throws Exception {
        try { return repo.findByStatusWithSummary("ACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public List<Warehouses> listarInactivos() throws Exception {
        try { return repo.findByStatus("INACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public List<Warehouses> listarInactivosConResumen() throws Exception {
        try { return repo.findByStatusWithSummary("INACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public List<Warehouses> buscarAlmacenes(String query, boolean deInactivos) throws Exception {
        try { return repo.search(query.trim(), deInactivos ? "INACTIVE" : "ACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public List<Warehouses> buscarAlmacenesConResumen(String query, boolean deInactivos) throws Exception {
        try { return repo.searchWithSummary(query == null ? "" : query.trim(), deInactivos ? "INACTIVE" : "ACTIVE"); } catch (SQLException e) { throw new Exception(e.getMessage()); }
    }

    public List<Inventory> listarInventarioDeAlmacen(int idWarehouse) throws Exception {
        if (idWarehouse <= 0) {
            throw new Exception("Seleccione un almacen valido.");
        }

        try {
            return repo.findInventoryDetail(idWarehouse);
        } catch (SQLException e) {
            throw new Exception("No se pudo cargar el inventario del almacen: " + e.getMessage());
        }
    }
}
