package Repositories;

import DAO.ProductDAO;
import Models.Product;
import java.util.List;

// Repository: implementacion del modulo de productos apoyada en ProductDAO.
public class ProductRepository implements IProductRepository {

    private final ProductDAO productDAO;

    public ProductRepository() {
        this.productDAO = new ProductDAO();
    }

    public ProductRepository(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public List<Product> listAll() {
        return productDAO.listar();
    }

    @Override
    public List<Product> search(String query) {
        return productDAO.buscar(query);
    }

    @Override
    public boolean save(Product product) {
        return productDAO.registrar(product);
    }

    @Override
    public boolean update(Product product) {
        return productDAO.modificar(product);
    }

    @Override
    public boolean deleteById(int idProduct) {
        return productDAO.eliminar(idProduct);
    }
}
