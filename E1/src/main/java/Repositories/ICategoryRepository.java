package Repositories;

import Models.Category;
import java.util.List;

// Interfaces + Repository: contrato simple para el modulo de categorias.
public interface ICategoryRepository {

    List<Category> listAll();

    List<Category> search(String query);

    boolean save(Category category);

    boolean update(Category category);

    boolean deleteById(int idCategory);
}
