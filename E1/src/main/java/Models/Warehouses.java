
package Models;

import java.time.LocalDateTime;

public class Warehouses {
    private int idWarehouse;
    private String warehouseName;
    private String address;
    private String city;
    private String country;
    private String phone;
    private String status; // 'ACTIVE' o 'INACTIVE'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int productCount;
    private int totalStock;
    private int totalReservedStock;
    private int availableStock;

    public Warehouses() {}

    public Warehouses(int idWarehouse, String warehouseName, String address, String city, String country, String phone, String status) {
        this.idWarehouse = idWarehouse;
        this.warehouseName = warehouseName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.status = status;
    }

    // Getters y Setters
    public int getIdWarehouse() { return idWarehouse; }
    public void setIdWarehouse(int idWarehouse) { this.idWarehouse = idWarehouse; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }
    public int getTotalReservedStock() { return totalReservedStock; }
    public void setTotalReservedStock(int totalReservedStock) { this.totalReservedStock = totalReservedStock; }
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }

    @Override
    public String toString() { return warehouseName; }
}
