package Models;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShipmentDetail {
    private int idShipmentDetail;
    private int idShipment;
    private int idBox;
    private int idProduct;
    private int quantity;
    private BigDecimal unitWeightKg;
    private LocalDateTime createdAt;
    public ShipmentDetail(){
        
    }

    public ShipmentDetail(int idShipmentDetail, int idShipment, int idBox, int idProduct, int quantity, BigDecimal unitWeightKg, LocalDateTime createdAt) {
        this.idShipmentDetail = idShipmentDetail;
        this.idShipment = idShipment;
        this.idBox = idBox;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.unitWeightKg = unitWeightKg;
        this.createdAt = createdAt;
    }
        public ShipmentDetail(int idShipment, int idBox, int idProduct,
            int quantity, BigDecimal unitWeightKg) {
        this.idShipment = idShipment;
        this.idBox = idBox;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.unitWeightKg = unitWeightKg;
    }

    public int getIdShipmentDetail() {
        return idShipmentDetail;
    }

    public void setIdShipmentDetail(int idShipmentDetail) {
        this.idShipmentDetail = idShipmentDetail;
    }

    public int getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(int idShipment) {
        this.idShipment = idShipment;
    }

    public int getIdBox() {
        return idBox;
    }

    public void setIdBox(int idBox) {
        this.idBox = idBox;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitWeightKg() {
        return unitWeightKg;
    }

    public void setUnitWeightKg(BigDecimal unitWeightKg) {
        this.unitWeightKg = unitWeightKg;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
