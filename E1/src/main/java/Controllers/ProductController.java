package Controllers;

import DAO.CategoryDAO;
import DAO.ProductDAO;
import Models.Category;
import Models.Product;
import Views.ProductosJPanel;
import java.math.BigDecimal;
import java.util.List;

public class ProductController {

    private final ProductDAO productDao;
    private final CategoryDAO categoryDao;

    public ProductController() {
        this.productDao = new ProductDAO();
        this.categoryDao = new CategoryDAO();
    }

    public ProductController(ProductosJPanel ignoredView) {
        this();
    }

    public List<Product> listProducts() {
        return productDao.listar();
    }

    public List<Product> searchProducts(String query) {
        return productDao.buscar(query);
    }

    public List<Category> listCategories() {
        return categoryDao.listar();
    }

    public void createProduct(Product product) throws Exception {
        validateProduct(product, false);

        if (!productDao.registrar(product)) {
            throw new Exception("No se pudo registrar el producto. Verifique que el SKU no este duplicado.");
        }
    }

    public void updateProduct(Product product) throws Exception {
        validateProduct(product, true);

        if (!productDao.modificar(product)) {
            throw new Exception("No se pudo actualizar el producto.");
        }
    }

    public void deleteProduct(int idProduct) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto valido para eliminar.");
        }

        if (!productDao.eliminar(idProduct)) {
            throw new Exception("No se pudo eliminar el producto.");
        }
    }

    private void validateProduct(Product product, boolean requireId) throws Exception {
        if (product == null) {
            throw new Exception("Ingrese los datos del producto.");
        }

        if (requireId && product.getIdProduct() <= 0) {
            throw new Exception("Seleccione un producto valido para actualizar.");
        }

        if (product.getIdCategory() <= 0) {
            throw new Exception("Seleccione una categoria valida.");
        }

        if (isBlank(product.getSku())) {
            throw new Exception("El SKU es obligatorio.");
        }

        if (isBlank(product.getProductName())) {
            throw new Exception("El nombre del producto es obligatorio.");
        }

        validateZeroOrPositive(product.getUnitWeightKg(), "El peso no puede ser negativo.");
        validateZeroOrPositive(product.getUnitPrice(), "El precio no puede ser negativo.");

        if (isBlank(product.getStatus())) {
            product.setStatus("ACTIVE");
        }

        product.setSku(product.getSku().trim());
        product.setProductName(product.getProductName().trim());
        product.setDescription(defaultText(product.getDescription()));
        product.setImagePath(defaultText(product.getImagePath()));
        product.setStatus(product.getStatus().trim());
    }

    private void validateZeroOrPositive(BigDecimal value, String message) throws Exception {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception(message);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultText(String value) {
        return value == null ? "" : value.trim();
    }
}
