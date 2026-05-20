package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la restauracion generica de registros inactivos.
public interface IRepositoryRestore<ID> {

    // Interfaces: restaura un registro previamente desactivado.
    void restore(ID id) throws SQLException;
}
