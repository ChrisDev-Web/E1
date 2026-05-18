-- 1. Usuarios del sistema
CREATE TABLE Users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Almacenes
CREATE TABLE Warehouses (
    id_warehouse INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    city VARCHAR(80) NOT NULL,
    country VARCHAR(80) NOT NULL,
    phone VARCHAR(20),
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Clientes / destinatarios
CREATE TABLE Clients (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    company_name VARCHAR(120),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(80) NOT NULL,
    country VARCHAR(80) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Categorias de productos
CREATE TABLE Categories (
    id_category INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. Productos
CREATE TABLE Products (
    id_product INT AUTO_INCREMENT PRIMARY KEY,
    id_category INT NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    image_path VARCHAR(255),
    unit_weight_kg DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category
        FOREIGN KEY (id_category) REFERENCES Categories(id_category)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 6. Inventario por almacen
CREATE TABLE Inventory (
    id_inventory INT AUTO_INCREMENT PRIMARY KEY,
    id_warehouse INT NOT NULL,
    id_product INT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    min_stock INT NOT NULL DEFAULT 0,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_inventory UNIQUE (id_warehouse, id_product),
    CONSTRAINT fk_inventory_warehouse
        FOREIGN KEY (id_warehouse) REFERENCES Warehouses(id_warehouse)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (id_product) REFERENCES Products(id_product)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 7. Envios
CREATE TABLE Shipments (
    id_shipment INT AUTO_INCREMENT PRIMARY KEY,
    tracking_code VARCHAR(50) NOT NULL UNIQUE,
    id_client INT NOT NULL,
    id_warehouse_origin INT NOT NULL,
    id_user INT NOT NULL,
    shipment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_delivery_date DATETIME,
    delivered_at DATETIME,
    status ENUM('PENDING','PREPARING','SHIPPED','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipments_client
        FOREIGN KEY (id_client) REFERENCES Clients(id_client)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_shipments_warehouse
        FOREIGN KEY (id_warehouse_origin) REFERENCES Warehouses(id_warehouse)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_shipments_user
        FOREIGN KEY (id_user) REFERENCES Users(id_user)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 8. Cajas de cada envio
CREATE TABLE Boxes (
    id_box INT AUTO_INCREMENT PRIMARY KEY,
    id_shipment INT NOT NULL,
    box_code VARCHAR(50) NOT NULL UNIQUE,
    image_path VARCHAR(255),
    length_cm DECIMAL(10,2) NOT NULL,
    width_cm DECIMAL(10,2) NOT NULL,
    height_cm DECIMAL(10,2) NOT NULL,
    weight_kg DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    declared_value DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('PACKED','SHIPPED','IN_TRANSIT','DELIVERED','DAMAGED') NOT NULL DEFAULT 'PACKED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_boxes_shipment
        FOREIGN KEY (id_shipment) REFERENCES Shipments(id_shipment)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- 9. Detalle de productos por envio y caja
CREATE TABLE ShipmentDetails (
    id_shipment_detail INT AUTO_INCREMENT PRIMARY KEY,
    id_shipment INT NOT NULL,
    id_box INT NOT NULL,
    id_product INT NOT NULL,
    quantity INT NOT NULL,
    unit_weight_kg DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipmentdetails_shipment
        FOREIGN KEY (id_shipment) REFERENCES Shipments(id_shipment)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_shipmentdetails_box
        FOREIGN KEY (id_box) REFERENCES Boxes(id_box)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_shipmentdetails_product
        FOREIGN KEY (id_product) REFERENCES Products(id_product)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 10. Historial / seguimiento del envio
CREATE TABLE ShipmentTracking (
    id_tracking INT AUTO_INCREMENT PRIMARY KEY,
    id_shipment INT NOT NULL,
    id_user INT,
    tracking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location VARCHAR(120) NOT NULL,
    status ENUM('PENDING','PREPARING','SHIPPED','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL,
    comments VARCHAR(255),
    CONSTRAINT fk_tracking_shipment
        FOREIGN KEY (id_shipment) REFERENCES Shipments(id_shipment)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_tracking_user
        FOREIGN KEY (id_user) REFERENCES Users(id_user)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- SP LOGIN AND REGISTER

DELIMITER $$

CREATE PROCEDURE sp_user_register(
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255)
)
BEGIN
    IF p_username IS NULL OR TRIM(p_username) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El username es obligatorio.';
    END IF;

    IF p_password_hash IS NULL OR TRIM(p_password_hash) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El password_hash es obligatorio.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM Users
        WHERE username = TRIM(p_username)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de usuario ya existe.';
    END IF;

    INSERT INTO Users (
        username,
        password_hash
    )
    VALUES (
        TRIM(p_username),
        TRIM(p_password_hash)
    );
END $$

CREATE PROCEDURE sp_user_login(
    IN p_username VARCHAR(50)
)
BEGIN
    SELECT
        id_user,
        username,
        password_hash,
        created_at,
        updated_at
    FROM Users
    WHERE username = TRIM(p_username)
    LIMIT 1;
END $$

DELIMITER ;