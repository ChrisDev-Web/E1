package Repositories;

import Models.Product;
import java.util.List;

public interface IProductRepository {
    List<Product> getAll();
    boolean add(Product product);
    boolean update(Product product);
    boolean delete(int id);
}