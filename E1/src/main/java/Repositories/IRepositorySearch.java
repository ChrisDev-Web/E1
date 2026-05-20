package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la busqueda paginada generica.
public interface IRepositorySearch<R, F> {

    // Interfaces: ejecuta una consulta paginada basada en un filtro.
    R search(F filter) throws SQLException;
}
