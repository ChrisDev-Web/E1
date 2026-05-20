package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la eliminacion logica generica.
public interface IRepositorySoftDelete<ID> {

    // Interfaces: marca un registro como inactivo sin borrarlo fisicamente.
    void softDelete(ID id) throws SQLException;
}
