package Repositories;

import java.sql.SQLException;

// Interfaces + Repository: define la operacion de cierre de sesion generica.
public interface IRepositoryLogout<ID> {

    // Interfaces: contrato para cerrar la sesion persistente o registrada de una entidad.
    void logout(ID id) throws SQLException;
}
