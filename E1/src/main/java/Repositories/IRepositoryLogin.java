package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la operacion de login generica.
public interface IRepositoryLogin<T> {

    // Interfaces: contrato para buscar una entidad por username al autenticar.
    T login(String userName) throws SQLException;
}
