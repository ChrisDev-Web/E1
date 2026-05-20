package Models;
import java.time.LocalDateTime;
public class ShipmentTracking {
    private int idTracking;
    private int idShipment;
    private Integer idUser;
    private LocalDateTime trackingDate;
    private String location;
    private String status;
    private String comments;
    public ShipmentTracking(){
        
    }

    public ShipmentTracking(int idTracking, int idShipment, Integer idUser, LocalDateTime trackingDate, String location, String status, String comments) {
        this.idTracking = idTracking;
        this.idShipment = idShipment;
        this.idUser = idUser;
        this.trackingDate = trackingDate;
        this.location = location;
        this.status = status;
        this.comments = comments;
    }
    public ShipmentTracking(int idShipment, Integer idUser,
            String location, String status, String comments) {
        this.idShipment = idShipment;
        this.idUser = idUser;
        this.location = location;
        this.status = status;
        this.comments = comments;
    }

    public int getIdTracking() {
        return idTracking;
    }

    public void setIdTracking(int idTracking) {
        this.idTracking = idTracking;
    }

    public int getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(int idShipment) {
        this.idShipment = idShipment;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public LocalDateTime getTrackingDate() {
        return trackingDate;
    }

    public void setTrackingDate(LocalDateTime trackingDate) {
        this.trackingDate = trackingDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
