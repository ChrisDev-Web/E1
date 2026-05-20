
package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Box {
    private int idBox;
    private int idShipment;
    private String boxCode;
    private String imagePath;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal declaredValue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Box(){
        
    }

    public Box(int idBox, int idShipment, String boxCode, String imagePath, BigDecimal lengthCm, BigDecimal widthCm, BigDecimal heightCm, BigDecimal weightKg, BigDecimal declaredValue, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.idBox = idBox;
        this.idShipment = idShipment;
        this.boxCode = boxCode;
        this.imagePath = imagePath;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.declaredValue = declaredValue;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
        public Box(int idShipment, String boxCode, String imagePath,
            BigDecimal lengthCm, BigDecimal widthCm, BigDecimal heightCm,
            BigDecimal weightKg, BigDecimal declaredValue, String status) {
        this.idShipment = idShipment;
        this.boxCode = boxCode;
        this.imagePath = imagePath;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.declaredValue = declaredValue;
        this.status = status;

    }
        public int getIdbox(){
            return idBox;
        }

    public int getIdBox() {
        return idBox;
    }

    public void setIdBox(int idBox) {
        this.idBox = idBox;
    }

    public int getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(int idShipment) {
        this.idShipment = idShipment;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BigDecimal getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(BigDecimal lengthCm) {
        this.lengthCm = lengthCm;
    }

    public BigDecimal getWidthCm() {
        return widthCm;
    }

    public void setWidthCm(BigDecimal widthCm) {
        this.widthCm = widthCm;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(BigDecimal declaredValue) {
        this.declaredValue = declaredValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updateAt) {
        this.updatedAt = updatedAt;
    }
       public String getDisplayName(){
           if(boxCode !=null &&!boxCode.trim().isEmpty()){
               return boxCode.trim();
           }
           return "Caja";
       }
       @Override
       public String toString(){
           return getDisplayName();
       }
}
