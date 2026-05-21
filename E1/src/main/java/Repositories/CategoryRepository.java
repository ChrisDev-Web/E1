package Repositories;

import DAO.CategoryDAO;
import Models.Category;
import java.util.List;

// Repository: implementacion del modulo de categorias apoyada en CategoryDAO.
public class CategoryRepository implements ICategoryRepository {

    private final CategoryDAO categoryDAO;

    public CategoryRepository() {
        this.categoryDAO = new CategoryDAO();
    }

    public CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public List<Category> listAll() {
        return categoryDAO.listar();
    }

    @Override
    public List<Category> search(String query) {
        return categoryDAO.buscar(query);
    }

    @Override
    public boolean save(Category category) {
        return categoryDAO.registrar(category);
    }

    @Override
    public boolean update(Category category) {
        return categoryDAO.modificar(category);
    }

    @Override
    public boolean deleteById(int idCategory) {
        return categoryDAO.eliminar(idCategory);
    }
}
