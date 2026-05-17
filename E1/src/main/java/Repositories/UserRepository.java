package Repositories;

import DAO.UserDAO;
import Models.User;
import java.sql.SQLException;

// Repository: implementa el contrato de usuario reutilizando la capa DAO existente.
public class UserRepository implements IUserRepository {

    // DAO: dependencia concreta que ejecuta el acceso JDBC.
    private final UserDAO userDAO;

    // POO: constructor por defecto que crea su propio DAO.
    public UserRepository() {
        this.userDAO = new UserDAO();
    }

    // POO: constructor alterno para inyectar un DAO ya existente.
    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Repository: delega el registro del usuario al DAO.
    @Override
    public void register(User entity) throws SQLException {
        userDAO.register(entity);
    }

    // Repository: delega la busqueda del usuario al DAO.
    @Override
    public User login(String userName) throws SQLException {
        return userDAO.login(userName);
    }
}
