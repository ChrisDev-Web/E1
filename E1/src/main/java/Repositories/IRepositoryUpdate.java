package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la actualizacion generica de una entidad.
public interface IRepositoryUpdate<T> {

    // Interfaces: actualiza una entidad ya existente en persistencia.
    void update(T entity) throws SQLException;
}
