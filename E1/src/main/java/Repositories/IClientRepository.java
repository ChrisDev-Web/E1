package Repositories;

import Models.Client;
import java.sql.SQLException;
import java.util.List;

// Interfaces + Repository: contrato completo del modulo de clientes.
public interface IClientRepository extends
        IRepositoryRegister<Client>,
        IRepositoryUpdate<Client>,
        IRepositoryFindById<Client, Integer>,
        IRepositorySearch<Client.PaginatedResult, Client.Filter>,
        IRepositorySoftDelete<Integer>,
        IRepositoryDelete<Integer>,
        IRepositoryRestore<Integer> {

    // Interfaces: busca clientes inactivos con paginacion.
    Client.PaginatedResult searchInactive(Client.Filter filter) throws SQLException;

    // Interfaces: lista tipos de documento para combos.
    List<Client.ReferenceItem> listDocumentTypes() throws SQLException;

    // Interfaces: lista clientes activos para combos de otros modulos.
    List<Client.ReferenceItem> listClientOptions() throws SQLException;
}
