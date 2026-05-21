package Repositories;

import Models.Product;
import java.util.List;

// Interfaces + Repository: contrato simple para el modulo de productos.
public interface IProductRepository {

    List<Product> listAll();

    List<Product> search(String query);

    boolean save(Product product);

    boolean update(Product product);

    boolean deleteById(int idProduct);
}
