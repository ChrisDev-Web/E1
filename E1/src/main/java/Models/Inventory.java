package Models;

import java.time.LocalDateTime;

public class Inventory {
    private int idInventory;
    private int idWarehouse;
    private int idProduct;
    private int stock;
    private int reservedStock;
    private int minStock;
    private String status;
    private LocalDateTime updatedAt;
    
    // Propiedades extendidas para facilitar la visualización directa en la tabla Swing
    private String warehouseName;
    private String productName;
    private String productSku;

    public Inventory() {}

    // Getters y Setters
    public int getIdInventory() { return idInventory; }
    public void setIdInventory(int idInventory) { this.idInventory = idInventory; }
    public int getIdWarehouse() { return idWarehouse; }
    public void setIdWarehouse(int idWarehouse) { this.idWarehouse = idWarehouse; }
    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getReservedStock() { return reservedStock; }
    public void setReservedStock(int reservedStock) { this.reservedStock = reservedStock; }
    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
}   