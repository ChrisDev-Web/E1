package DAO;

import Config.Database;
import Models.Client;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DAO + JDBC: ejecuta los procedimientos almacenados del modulo de clientes.
public class ClientDAO {

    public void register(Client client) throws SQLException {
        String sql = "{CALL sp_client_create(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillClientStatement(statement, client, false);
            statement.execute();
        }
    }

    public void update(Client client) throws SQLException {
        String sql = "{CALL sp_client_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, client.getIdClient());
            fillClientStatement(statement, client, true);
            statement.execute();
        }
    }

    public void softDelete(int idClient) throws SQLException {
        executeById("{CALL sp_client_soft_delete(?)}", idClient);
    }

    public void deleteById(int idClient) throws SQLException {
        executeById("{CALL sp_client_delete(?)}", idClient);
    }

    public void restore(int idClient) throws SQLException {
        executeById("{CALL sp_client_restore(?)}", idClient);
    }

    public Client findById(int idClient) throws SQLException {
        String sql = "{CALL sp_client_get_by_id(?)}";

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idClient);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapClient(resultSet);
                }
            }
        }

        return null;
    }

    public Client.PaginatedResult search(Client.Filter filter) throws SQLException {
        return searchInternal("{CALL sp_client_search_active(?, ?, ?)}", filter);
    }

    public Client.PaginatedResult searchInactive(Client.Filter filter) throws SQLException {
        return searchInternal("{CALL sp_client_search_inactive(?, ?, ?)}", filter);
    }

    public List<Client.ReferenceItem> listDocumentTypes() throws SQLException {
        return listReferenceItems("{CALL sp_document_type_list()}");
    }

    public List<Client.ReferenceItem> listClientOptions() throws SQLException {
        return listReferenceItems("{CALL sp_client_list_for_combo()}");
    }

    private void executeById(String sql, int id) throws SQLException {
        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, id);
            statement.execute();
        }
    }

    private Client.PaginatedResult searchInternal(String sql, Client.Filter filter) throws SQLException {
        List<Client> items = new ArrayList<>();
        int totalRecords = 0;

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, normalizeText(filter.getSearchText()));
            statement.setInt(2, resolveOffset(filter.getPage(), filter.getPageSize()));
            statement.setInt(3, filter.getPageSize());

            boolean hasResult = statement.execute();

            if (hasResult) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    while (resultSet.next()) {
                        items.add(mapClient(resultSet));
                    }
                }
            }

            if (statement.getMoreResults()) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    if (resultSet.next()) {
                        totalRecords = resultSet.getInt("total_records");
                    }
                }
            }
        }

        return new Client.PaginatedResult(items, totalRecords, filter.getPage(), filter.getPageSize());
    }

    private List<Client.ReferenceItem> listReferenceItems(String sql) throws SQLException {
        List<Client.ReferenceItem> items = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                CallableStatement statement = connection.prepareCall(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                items.add(new Client.ReferenceItem(
                        resultSet.getInt("id"),
                        resultSet.getString("label")
                ));
            }
        }

        return items;
    }

    private void fillClientStatement(CallableStatement statement, Client client, boolean hasIdPrefix) throws SQLException {
        int index = hasIdPrefix ? 2 : 1;

        statement.setString(index++, client.getName());
        statement.setString(index++, client.getLastNamePaternal());
        statement.setString(index++, client.getLastNameMaternal());
        statement.setInt(index++, client.getIdDocumentType());

        if (client.getDocumentNumber() == null) {
            statement.setNull(index++, Types.INTEGER);
        } else {
            statement.setInt(index++, client.getDocumentNumber());
        }

        statement.setString(index++, normalizeText(client.getCompanyName()));
        statement.setString(index++, normalizeText(client.getPhone()));
        statement.setString(index++, normalizeText(client.getEmail()));
        statement.setString(index++, client.getAddress());
        statement.setString(index++, client.getCity());
        statement.setString(index, client.getCountry());
    }

    private Client mapClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setIdClient(resultSet.getInt("id_client"));
        client.setName(resultSet.getString("name"));
        client.setLastNamePaternal(resultSet.getString("last_name_paternal"));
        client.setLastNameMaternal(resultSet.getString("last_name_maternal"));
        client.setIdDocumentType(resultSet.getInt("id_document_type"));
        client.setDocumentTypeName(readColumn(resultSet, "document_type_name"));
        client.setDocumentNumber((Integer) resultSet.getObject("document_number"));
        client.setCompanyName(readColumn(resultSet, "company_name"));
        client.setPhone(readColumn(resultSet, "phone"));
        client.setEmail(readColumn(resultSet, "email"));
        client.setAddress(resultSet.getString("address"));
        client.setCity(resultSet.getString("city"));
        client.setCountry(resultSet.getString("country"));
        client.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        client.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        client.setDeletedAt(toLocalDateTime(resultSet.getTimestamp("deleted_at")));
        return client;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private int resolveOffset(int page, int pageSize) {
        return Math.max(page - 1, 0) * Math.max(pageSize, 1);
    }

    private String readColumn(ResultSet resultSet, String column) throws SQLException {
        String value = resultSet.getString(column);
        return value == null ? null : value.trim();
    }
}
