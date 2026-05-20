package Controllers;

import Models.Client;
import Repositories.ClientRepository;
import Repositories.IClientRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

// MVC + POO: coordina validaciones y persistencia del modulo de clientes.
public class ClientController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final IClientRepository clientRepository;

    public ClientController() {
        this.clientRepository = new ClientRepository();
    }

    public ClientController(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(Client client) throws Exception {
        validateClient(client);

        try {
            clientRepository.register(client);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void updateClient(Client client) throws Exception {
        if (client == null || client.getIdClient() <= 0) {
            throw new Exception("Seleccione un cliente valido para editar.");
        }

        validateClient(client);

        try {
            clientRepository.update(client);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void softDeleteClient(int idClient) throws Exception {
        validateId(idClient, "Seleccione un cliente valido para desactivar.");

        try {
            clientRepository.softDelete(idClient);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void deleteClient(int idClient) throws Exception {
        validateId(idClient, "Seleccione un cliente valido para eliminar.");

        try {
            clientRepository.deleteById(idClient);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public void restoreClient(int idClient) throws Exception {
        validateId(idClient, "Seleccione un cliente inactivo valido para restaurar.");

        try {
            clientRepository.restore(idClient);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Client findClientById(int idClient) throws Exception {
        validateId(idClient, "Seleccione un cliente valido.");

        try {
            return clientRepository.findById(idClient);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Client.PaginatedResult searchActiveClients(Client.Filter filter) throws Exception {
        validateFilter(filter);

        try {
            return clientRepository.search(filter);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public Client.PaginatedResult searchInactiveClients(Client.Filter filter) throws Exception {
        validateFilter(filter);

        try {
            return clientRepository.searchInactive(filter);
        } catch (SQLException e) {
            throw new Exception(resolveSqlMessage(e));
        }
    }

    public List<Client.ReferenceItem> listDocumentTypes() throws Exception {
        try {
            return clientRepository.listDocumentTypes();
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

    private void validateClient(Client client) throws Exception {
        if (client == null) {
            throw new Exception("No se recibieron los datos del cliente.");
        }

        if (isBlank(client.getName())) {
            throw new Exception("Escriba el nombre del cliente.");
        }

        if (isBlank(client.getLastNamePaternal())) {
            throw new Exception("Escriba el apellido paterno del cliente.");
        }

        if (isBlank(client.getLastNameMaternal())) {
            throw new Exception("Escriba el apellido materno del cliente.");
        }

        if (client.getIdDocumentType() <= 0) {
            throw new Exception("Seleccione un tipo de documento.");
        }

        if (client.getDocumentNumber() == null || client.getDocumentNumber() <= 0) {
            throw new Exception("Escriba un numero de documento valido.");
        }

        if (isBlank(client.getAddress())) {
            throw new Exception("Escriba la direccion del cliente.");
        }

        if (isBlank(client.getCity())) {
            throw new Exception("Escriba la ciudad del cliente.");
        }

        if (isBlank(client.getCountry())) {
            throw new Exception("Escriba el pais del cliente.");
        }

        if (!isBlank(client.getEmail()) && !EMAIL_PATTERN.matcher(client.getEmail().trim()).matches()) {
            throw new Exception("Escriba un correo electronico valido.");
        }
    }

    private void validateFilter(Client.Filter filter) throws Exception {
        if (filter == null) {
            throw new Exception("No se recibio el filtro de busqueda.");
        }

        if (filter.getPage() <= 0) {
            filter.setPage(1);
        }

        if (filter.getPageSize() <= 0) {
            filter.setPageSize(10);
        }
    }

    private void validateId(int id, String message) throws Exception {
        if (id <= 0) {
            throw new Exception(message);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String resolveSqlMessage(SQLException e) {
        String message = e.getMessage();

        if (message == null || message.isBlank()) {
            return "Ocurrio un problema al procesar la operacion de clientes.";
        }

        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("duplicate") || lowerMessage.contains("duplicada")) {
            return "El correo electronico o el documento ya estan registrados.";
        }

        return message;
    }
}
