package Controllers;

import Models.Category;
import Repositories.CategoryRepository;
import Repositories.ICategoryRepository;
import Views.CategoriasJPanel;
import java.util.List;

public class CategoryController {

    private final ICategoryRepository categoryRepository;

    public CategoryController() {
        this.categoryRepository = new CategoryRepository();
    }

    public CategoryController(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryController(CategoriasJPanel ignoredView) {
        this();
    }

    public List<Category> listCategories() {
        return categoryRepository.listAll();
    }

    public List<Category> searchCategories(String query) {
        return categoryRepository.search(query);
    }

    public void createCategory(Category category) throws Exception {
        validateCategory(category, false);

        if (!categoryRepository.save(category)) {
            throw new Exception("No se pudo registrar la categoria.");
        }
    }

    public void updateCategory(Category category) throws Exception {
        validateCategory(category, true);

        if (!categoryRepository.update(category)) {
            throw new Exception("No se pudo actualizar la categoria.");
        }
    }

    public void deleteCategory(int idCategory) throws Exception {
        if (idCategory <= 0) {
            throw new Exception("Seleccione una categoria valida para eliminar.");
        }

        if (!categoryRepository.deleteById(idCategory)) {
            throw new Exception("No se pudo eliminar la categoria. Verifique si tiene productos asociados.");
        }
    }

    private void validateCategory(Category category, boolean requireId) throws Exception {
        if (category == null) {
            throw new Exception("Ingrese los datos de la categoria.");
        }

        if (requireId && category.getIdCategory() <= 0) {
            throw new Exception("Seleccione una categoria valida para actualizar.");
        }

        if (isBlank(category.getCategoryName())) {
            throw new Exception("El nombre de la categoria es obligatorio.");
        }

        category.setCategoryName(category.getCategoryName().trim());
        category.setDescription(defaultText(category.getDescription()));
        category.setImagePath(defaultText(category.getImagePath()));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultText(String value) {
        return value == null ? "" : value.trim();
    }
}
