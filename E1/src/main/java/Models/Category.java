package Models;

public class Category {
    private int idCategory;
    private String categoryName;
    private String description;
    private String imagePath;

    // Constructor vacío (necesario para frameworks o manejo de datos)
    public Category() {}

    // Constructor completo
    public Category(int idCategory, String categoryName, String description, String imagePath) {
        this.idCategory = idCategory;
        this.categoryName = categoryName;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Getters y Setters
    public int getIdCategory() { return idCategory; }
    public void setIdCategory(int idCategory) { this.idCategory = idCategory; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}