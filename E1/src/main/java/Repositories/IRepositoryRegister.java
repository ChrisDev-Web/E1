package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la operacion de registro generica.
public interface IRepositoryRegister<T> {

    // Interfaces: contrato para registrar una entidad en persistencia.
    void register(T entity) throws SQLException;
}
