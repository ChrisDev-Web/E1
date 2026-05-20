package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la busqueda generica por identificador.
public interface IRepositoryFindById<T, ID> {

    // Interfaces: busca una entidad concreta por su identificador primario.
    T findById(ID id) throws SQLException;
}
