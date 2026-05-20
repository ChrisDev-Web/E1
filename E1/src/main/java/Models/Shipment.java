package Models;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// POO + Modelo: representa la tabla Shipments junto con datos extendidos para la vista.
public class Shipment {

    private int idShipment;
    private String trackingCode;
    private int idClient;
    private String clientName;
    private int idWarehouseOrigin;
    private String warehouseName;
    private int idUser;
    private String userName;
    private Integer changedByUserId;
    private LocalDateTime shipmentDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveredAt;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Shipment() {
    }

    public Shipment(
            int idShipment,
            String trackingCode,
            int idClient,
            int idWarehouseOrigin,
            int idUser,
            Integer changedByUserId,
            LocalDateTime estimatedDeliveryDate,
            Status status,
            String notes
    ) {
        this.idShipment = idShipment;
        this.trackingCode = trackingCode;
        this.idClient = idClient;
        this.idWarehouseOrigin = idWarehouseOrigin;
        this.idUser = idUser;
        this.changedByUserId = changedByUserId;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status = status;
        this.notes = notes;
    }

    public Shipment(
            String trackingCode,
            int idClient,
            int idWarehouseOrigin,
            int idUser,
            Integer changedByUserId,
            LocalDateTime estimatedDeliveryDate,
            Status status,
            String notes
    ) {
        this(0, trackingCode, idClient, idWarehouseOrigin, idUser, changedByUserId, estimatedDeliveryDate, status, notes);
    }

    public int getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(int idShipment) {
        this.idShipment = idShipment;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getIdWarehouseOrigin() {
        return idWarehouseOrigin;
    }

    public void setIdWarehouseOrigin(int idWarehouseOrigin) {
        this.idWarehouseOrigin = idWarehouseOrigin;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Integer changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Enum + POO: estados permitidos del modulo de envios.
    public enum Status {
        PENDING,
        PREPARING,
        SHIPPED,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED
    }

    // POO + Modelo: filtro de busqueda y paginacion del modulo de envios.
    public static class Filter {

        private String searchText;
        private Status status;
        private LocalDate shipmentDate;
        private int page = 1;
        private int pageSize = 10;

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public LocalDate getShipmentDate() {
            return shipmentDate;
        }

        public void setShipmentDate(LocalDate shipmentDate) {
            this.shipmentDate = shipmentDate;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    // POO + Modelo: opcion simple para combos del modulo de envios.
    public static class ReferenceItem {

        private int id;
        private String label;

        public ReferenceItem() {
        }

        public ReferenceItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // POO + Modelo: resultado paginado del modulo de envios.
    public static class PaginatedResult {

        private final List<Shipment> items;
        private final int totalRecords;
        private final int page;
        private final int pageSize;

        public PaginatedResult(List<Shipment> items, int totalRecords, int page, int pageSize) {
            this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
            this.totalRecords = Math.max(totalRecords, 0);
            this.page = Math.max(page, 1);
            this.pageSize = Math.max(pageSize, 1);
        }

        public List<Shipment> getItems() {
            return Collections.unmodifiableList(items);
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotalPages() {
            if (totalRecords == 0) {
                return 1;
            }

            return (int) Math.ceil((double) totalRecords / pageSize);
        }
    }

    // POO + Modelo: registro de seguimiento de un envio.
    public static class Tracking {

        private int idTracking;
        private int idShipment;
        private Integer idUser;
        private String userName;
        private LocalDateTime trackingDate;
        private String location;
        private Status status;
        private String comments;

        public Tracking() {
        }

        public Tracking(int idTracking, int idShipment, Integer idUser, LocalDateTime trackingDate, String location, Status status, String comments) {
            this.idTracking = idTracking;
            this.idShipment = idShipment;
            this.idUser = idUser;
            this.trackingDate = trackingDate;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

    // POO + Modelo: detalle completo de un envio.
    public static class Detail {

        private Shipment shipment;
        private final List<String> boxDetails = new ArrayList<>();
        private final List<String> productDetails = new ArrayList<>();
        private final List<Tracking> trackingHistory = new ArrayList<>();

        public Shipment getShipment() {
            return shipment;
        }

        public void setShipment(Shipment shipment) {
            this.shipment = shipment;
        }

        public List<String> getBoxDetails() {
            return Collections.unmodifiableList(boxDetails);
        }

        public void setBoxDetails(List<String> items) {
            boxDetails.clear();

            if (items != null) {
                boxDetails.addAll(items);
            }
        }

        public List<String> getProductDetails() {
            return Collections.unmodifiableList(productDetails);
        }

        public void setProductDetails(List<String> items) {
            productDetails.clear();

            if (items != null) {
                productDetails.addAll(items);
            }
        }

        public List<Tracking> getTrackingHistory() {
            return Collections.unmodifiableList(trackingHistory);
        }

        public void setTrackingHistory(List<Tracking> items) {
            trackingHistory.clear();

            if (items != null) {
                trackingHistory.addAll(items);
            }
        }
    }
}
