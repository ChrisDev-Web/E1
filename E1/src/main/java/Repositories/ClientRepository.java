package Repositories;

import DAO.ClientDAO;
import Models.Client;
import java.sql.SQLException;
import java.util.List;

// Repository: implementa el contrato de clientes apoyandose en la capa DAO.
public class ClientRepository implements IClientRepository {

    private final ClientDAO clientDAO;

    public ClientRepository() {
        this.clientDAO = new ClientDAO();
    }

    public ClientRepository(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public void register(Client entity) throws SQLException {
        clientDAO.register(entity);
    }

    @Override
    public void update(Client entity) throws SQLException {
        clientDAO.update(entity);
    }

    @Override
    public Client findById(Integer id) throws SQLException {
        return clientDAO.findById(id);
    }

    @Override
    public Client.PaginatedResult search(Client.Filter filter) throws SQLException {
        return clientDAO.search(filter);
    }

    @Override
    public void softDelete(Integer id) throws SQLException {
        clientDAO.softDelete(id);
    }

    @Override
    public void deleteById(Integer id) throws SQLException {
        clientDAO.deleteById(id);
    }

    @Override
    public void restore(Integer id) throws SQLException {
        clientDAO.restore(id);
    }

    @Override
    public Client.PaginatedResult searchInactive(Client.Filter filter) throws SQLException {
        return clientDAO.searchInactive(filter);
    }

    @Override
    public List<Client.ReferenceItem> listDocumentTypes() throws SQLException {
        return clientDAO.listDocumentTypes();
    }

    @Override
    public List<Client.ReferenceItem> listClientOptions() throws SQLException {
        return clientDAO.listClientOptions();
    }
}
