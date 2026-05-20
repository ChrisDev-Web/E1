package Repositories;

import Models.Box;
import java.sql.SQLException;
import java.util.List;

// Interfaces + Repository: define las operaciones necesarias para trabajar con cajas.
public interface IBoxRepository {

    void create(Box box) throws SQLException;

    void update(Box box) throws SQLException;

    void delete(int idBox) throws SQLException;

    Box findById(int idBox) throws SQLException;

    List<Box> list() throws SQLException;

    List<Box> listByShipment(int idShipment) throws SQLException;
}