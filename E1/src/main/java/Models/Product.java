package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private int idProduct;
    private int idCategory;
    private String sku;
    private String productName;
    private String description;
    private String imagePath;
    private BigDecimal unitWeightKg;
    private BigDecimal unitPrice;
    private String status; // 'ACTIVE' o 'INACTIVE'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {}

    // Constructor completo para recuperar de la BD
    public Product(int idProduct, int idCategory, String sku, String productName, String description, 
                   String imagePath, BigDecimal unitWeightKg, BigDecimal unitPrice, String status) {
        this.idProduct = idProduct;
        this.idCategory = idCategory;
        this.sku = sku;
        this.productName = productName;
        this.description = description;
        this.imagePath = imagePath;
        this.unitWeightKg = unitWeightKg;
        this.unitPrice = unitPrice;
        this.status = status;
    }

    // Getters y Setters
    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }
    public int getIdCategory() { return idCategory; }
    public void setIdCategory(int idCategory) { this.idCategory = idCategory; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public BigDecimal getUnitWeightKg() { return unitWeightKg; }
    public void setUnitWeightKg(BigDecimal unitWeightKg) { this.unitWeightKg = unitWeightKg; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}