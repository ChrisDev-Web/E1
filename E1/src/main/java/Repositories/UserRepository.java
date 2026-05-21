package Repositories;

import DAO.UserDAO;
import Models.User;
import java.sql.SQLException;
import java.util.List;

public class UserRepository implements IUserRepository {

    private final UserDAO userDAO;

    public UserRepository() {
        this.userDAO = new UserDAO();
    }

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void register(User entity) throws SQLException {
        userDAO.register(entity);
    }

    @Override
    public User login(String userName) throws SQLException {
        return userDAO.login(userName);
    }

    @Override
    public void update(User user) throws SQLException {
        userDAO.update(user);
    }

    @Override
    public void deleteById(Integer id) throws SQLException {
        userDAO.deleteById(id);
    }

    @Override
    public User findById(Integer id) throws SQLException {
        return userDAO.findById(id);
    }

    @Override
    public List<User> search(String query, String status) throws SQLException {
        return userDAO.search(query, status);
    }
}
