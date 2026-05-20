package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la eliminacion fisica generica.
public interface IRepositoryDelete<ID> {

    // Interfaces: elimina de forma fisica un registro segun su identificador.
    void deleteById(ID id) throws SQLException;
}
