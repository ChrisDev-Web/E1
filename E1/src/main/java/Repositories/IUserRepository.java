package Repositories;

import Models.User;
import java.sql.SQLException;
import java.util.List;

public interface IUserRepository extends IRepositoryRegister<User>, IRepositoryLogin<User> {

    void update(User user) throws SQLException;

    void deleteById(Integer id) throws SQLException;

    User findById(Integer id) throws SQLException;

    List<User> search(String query, String status) throws SQLException;
}
