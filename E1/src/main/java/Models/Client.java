package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// POO + Modelo: representa la tabla Clients y sus datos de apoyo para la interfaz.
public class Client {

    private int idClient;
    private String name;
    private String lastNamePaternal;
    private String lastNameMaternal;
    private int idDocumentType;
    private String documentTypeName;
    private Integer documentNumber;
    private String companyName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Client() {
    }

    public Client(
            int idClient,
            String name,
            String lastNamePaternal,
            String lastNameMaternal,
            int idDocumentType,
            Integer documentNumber,
            String companyName,
            String phone,
            String email,
            String address,
            String city,
            String country
    ) {
        this.idClient = idClient;
        this.name = name;
        this.lastNamePaternal = lastNamePaternal;
        this.lastNameMaternal = lastNameMaternal;
        this.idDocumentType = idDocumentType;
        this.documentNumber = documentNumber;
        this.companyName = companyName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public Client(
            String name,
            String lastNamePaternal,
            String lastNameMaternal,
            int idDocumentType,
            Integer documentNumber,
            String companyName,
            String phone,
            String email,
            String address,
            String city,
            String country
    ) {
        this(
                0,
                name,
                lastNamePaternal,
                lastNameMaternal,
                idDocumentType,
                documentNumber,
                companyName,
                phone,
                email,
                address,
                city,
                country
        );
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastNamePaternal() {
        return lastNamePaternal;
    }

    public void setLastNamePaternal(String lastNamePaternal) {
        this.lastNamePaternal = lastNamePaternal;
    }

    public String getLastNameMaternal() {
        return lastNameMaternal;
    }

    public void setLastNameMaternal(String lastNameMaternal) {
        this.lastNameMaternal = lastNameMaternal;
    }

    public int getIdDocumentType() {
        return idDocumentType;
    }

    public void setIdDocumentType(int idDocumentType) {
        this.idDocumentType = idDocumentType;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getFullName() {
        return String.format(
                "%s %s %s",
                valueOrEmpty(name),
                valueOrEmpty(lastNamePaternal),
                valueOrEmpty(lastNameMaternal)
        ).trim();
    }

    public boolean isActive() {
        return deletedAt == null;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    // POO + Modelo: filtro de busqueda y paginacion del modulo de clientes.
    public static class Filter {

        private String searchText;
        private int page = 1;
        private int pageSize = 10;

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
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

    // POO + Modelo: opcion simple para combos del modulo de clientes.
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

    // POO + Modelo: resultado paginado del modulo de clientes.
    public static class PaginatedResult {

        private final List<Client> items;
        private final int totalRecords;
        private final int page;
        private final int pageSize;

        public PaginatedResult(List<Client> items, int totalRecords, int page, int pageSize) {
            this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
            this.totalRecords = Math.max(totalRecords, 0);
            this.page = Math.max(page, 1);
            this.pageSize = Math.max(pageSize, 1);
        }

        public List<Client> getItems() {
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
}
