CREATE DATABASE Logistics2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Logistics2;

-- Tabla de Usuarios
CREATE TABLE Users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status ENUM("ACTIVE","INACTIVE") NOT NULL DEFAULT "ACTIVE",
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de Tipo de Documentos
CREATE TABLE DocumentTypes (
    id_document_type INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL
);

-- Tabla de Categorias
CREATE TABLE Categories (
    id_category INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de Productos
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

-- Tabla de Almacenes
CREATE TABLE Warehouses (
    id_warehouse INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    city VARCHAR(80) NOT NULL,
    country VARCHAR(80) NOT NULL,
    phone VARCHAR(20),
    status ENUM('active', 'inactive') DEFAULT 'active',
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de Inventario
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

-- Tabla de Clientes
CREATE TABLE Clients (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    last_name_paternal VARCHAR(120) NOT NULL,
    last_name_maternal VARCHAR(120) NOT NULL,
    id_document_type INT NOT NULL,
    document_number INT NOT NULL,
    company_name VARCHAR(120),
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address VARCHAR(200) NOT NULL,
    city VARCHAR(80) NOT NULL,
    country VARCHAR(80) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (id_document_type) REFERENCES DocumentTypes(id_document_type)
);

-- Tabla de Envios
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

-- Tabla de Cajas
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

-- Tabla de Detalle de Envios
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

-- Tabla de Seguimiento de Envíos
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

-- SP Neil

DELIMITER $$
CREATE TRIGGER trg_shipments_generate_tracking_code
BEFORE INSERT ON Shipments
FOR EACH ROW
BEGIN
    IF NEW.tracking_code IS NULL OR TRIM(NEW.tracking_code) = '' THEN
        SET NEW.tracking_code = CONCAT('TRK-', UUID_SHORT());
    END IF;
END $$

CREATE PROCEDURE sp_document_type_list()
BEGIN
    SELECT
        dt.id_document_type AS id,
        dt.name AS label
    FROM DocumentTypes dt
    ORDER BY dt.name;
END $$

CREATE PROCEDURE sp_client_list_for_combo()
BEGIN
    SELECT
        c.id_client AS id,
        CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) AS label
    FROM Clients c
    WHERE c.deleted_at IS NULL
    ORDER BY c.name, c.last_name_paternal, c.last_name_maternal;
END $$

CREATE PROCEDURE sp_client_create(
    IN p_name VARCHAR(120),
    IN p_last_name_paternal VARCHAR(120),
    IN p_last_name_maternal VARCHAR(120),
    IN p_id_document_type INT,
    IN p_document_number INT,
    IN p_company_name VARCHAR(120),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(100),
    IN p_address VARCHAR(200),
    IN p_city VARCHAR(80),
    IN p_country VARCHAR(80)
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre del cliente es obligatorio.';
    END IF;

    IF p_last_name_paternal IS NULL OR TRIM(p_last_name_paternal) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El apellido paterno es obligatorio.';
    END IF;

    IF p_last_name_maternal IS NULL OR TRIM(p_last_name_maternal) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El apellido materno es obligatorio.';
    END IF;

    IF p_id_document_type IS NULL OR p_id_document_type <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Debe seleccionar un tipo de documento valido.';
    END IF;

    IF p_document_number IS NULL OR p_document_number <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Debe ingresar un numero de documento valido.';
    END IF;

    IF p_address IS NULL OR TRIM(p_address) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La direccion es obligatoria.';
    END IF;

    IF p_city IS NULL OR TRIM(p_city) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La ciudad es obligatoria.';
    END IF;

    IF p_country IS NULL OR TRIM(p_country) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pais es obligatorio.';
    END IF;

    INSERT INTO Clients (
        name,
        last_name_paternal,
        last_name_maternal,
        id_document_type,
        document_number,
        company_name,
        phone,
        email,
        address,
        city,
        country
    ) VALUES (
        TRIM(p_name),
        TRIM(p_last_name_paternal),
        TRIM(p_last_name_maternal),
        p_id_document_type,
        p_document_number,
        NULLIF(TRIM(p_company_name), ''),
        NULLIF(TRIM(p_phone), ''),
        NULLIF(TRIM(p_email), ''),
        TRIM(p_address),
        TRIM(p_city),
        TRIM(p_country)
    );
END $$

CREATE PROCEDURE sp_client_update(
    IN p_id_client INT,
    IN p_name VARCHAR(120),
    IN p_last_name_paternal VARCHAR(120),
    IN p_last_name_maternal VARCHAR(120),
    IN p_id_document_type INT,
    IN p_document_number INT,
    IN p_company_name VARCHAR(120),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(100),
    IN p_address VARCHAR(200),
    IN p_city VARCHAR(80),
    IN p_country VARCHAR(80)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Clients WHERE id_client = p_id_client) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cliente indicado no existe.';
    END IF;

    UPDATE Clients
    SET
        name = TRIM(p_name),
        last_name_paternal = TRIM(p_last_name_paternal),
        last_name_maternal = TRIM(p_last_name_maternal),
        id_document_type = p_id_document_type,
        document_number = p_document_number,
        company_name = NULLIF(TRIM(p_company_name), ''),
        phone = NULLIF(TRIM(p_phone), ''),
        email = NULLIF(TRIM(p_email), ''),
        address = TRIM(p_address),
        city = TRIM(p_city),
        country = TRIM(p_country)
    WHERE id_client = p_id_client;
END $$

CREATE PROCEDURE sp_client_soft_delete(IN p_id_client INT)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Clients
        WHERE id_client = p_id_client
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cliente activo indicado no existe.';
    END IF;

    UPDATE Clients
    SET deleted_at = NOW()
    WHERE id_client = p_id_client;
END $$

CREATE PROCEDURE sp_client_delete(IN p_id_client INT)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Clients WHERE id_client = p_id_client) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cliente indicado no existe.';
    END IF;

    DELETE FROM Clients
    WHERE id_client = p_id_client;
END $$

CREATE PROCEDURE sp_client_restore(IN p_id_client INT)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Clients
        WHERE id_client = p_id_client
          AND deleted_at IS NOT NULL
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cliente inactivo indicado no existe.';
    END IF;

    UPDATE Clients
    SET deleted_at = NULL
    WHERE id_client = p_id_client;
END $$

CREATE PROCEDURE sp_client_get_by_id(IN p_id_client INT)
BEGIN
    SELECT
        c.id_client,
        c.name,
        c.last_name_paternal,
        c.last_name_maternal,
        c.id_document_type,
        dt.name AS document_type_name,
        c.document_number,
        c.company_name,
        c.phone,
        c.email,
        c.address,
        c.city,
        c.country,
        c.created_at,
        c.updated_at,
        c.deleted_at
    FROM Clients c
    INNER JOIN DocumentTypes dt ON dt.id_document_type = c.id_document_type
    WHERE c.id_client = p_id_client
    LIMIT 1;
END $$

CREATE PROCEDURE sp_client_search_active(
    IN p_search_text VARCHAR(120),
    IN p_offset INT,
    IN p_page_size INT
)
BEGIN
    DECLARE v_search_text VARCHAR(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    SET v_search_text = NULLIF(TRIM(CONVERT(p_search_text USING utf8mb4)), '');

    SELECT
        c.id_client,
        c.name,
        c.last_name_paternal,
        c.last_name_maternal,
        c.id_document_type,
        dt.name AS document_type_name,
        c.document_number,
        c.company_name,
        c.phone,
        c.email,
        c.address,
        c.city,
        c.country,
        c.created_at,
        c.updated_at,
        c.deleted_at
    FROM Clients c
    INNER JOIN DocumentTypes dt ON dt.id_document_type = c.id_document_type
    WHERE c.deleted_at IS NULL
      AND (
          v_search_text IS NULL
          OR CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR CAST(c.document_number AS CHAR) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR COALESCE(c.company_name, '') COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
      )
    ORDER BY c.id_client DESC
    LIMIT p_offset, p_page_size;

    SELECT
        COUNT(*) AS total_records
    FROM Clients c
    WHERE c.deleted_at IS NULL
      AND (
          v_search_text IS NULL
          OR CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR CAST(c.document_number AS CHAR) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR COALESCE(c.company_name, '') COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
      );
END $$

CREATE PROCEDURE sp_client_search_inactive(
    IN p_search_text VARCHAR(120),
    IN p_offset INT,
    IN p_page_size INT
)
BEGIN
    DECLARE v_search_text VARCHAR(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    SET v_search_text = NULLIF(TRIM(CONVERT(p_search_text USING utf8mb4)), '');

    SELECT
        c.id_client,
        c.name,
        c.last_name_paternal,
        c.last_name_maternal,
        c.id_document_type,
        dt.name AS document_type_name,
        c.document_number,
        c.company_name,
        c.phone,
        c.email,
        c.address,
        c.city,
        c.country,
        c.created_at,
        c.updated_at,
        c.deleted_at
    FROM Clients c
    INNER JOIN DocumentTypes dt ON dt.id_document_type = c.id_document_type
    WHERE c.deleted_at IS NOT NULL
      AND (
          v_search_text IS NULL
          OR CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR CAST(c.document_number AS CHAR) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR COALESCE(c.company_name, '') COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
      )
    ORDER BY c.deleted_at DESC, c.id_client DESC
    LIMIT p_offset, p_page_size;

    SELECT
        COUNT(*) AS total_records
    FROM Clients c
    WHERE c.deleted_at IS NOT NULL
      AND (
          v_search_text IS NULL
          OR CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR CAST(c.document_number AS CHAR) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
          OR COALESCE(c.company_name, '') COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
      );
END $$

CREATE PROCEDURE sp_shipment_tracking_register(
    IN p_id_shipment INT,
    IN p_status VARCHAR(20),
    IN p_id_user INT,
    IN p_location VARCHAR(120),
    IN p_comments VARCHAR(255)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Shipments WHERE id_shipment = p_id_shipment) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
    END IF;

    INSERT INTO ShipmentTracking (
        id_shipment,
        id_user,
        location,
        status,
        comments
    ) VALUES (
        p_id_shipment,
        p_id_user,
        COALESCE(NULLIF(TRIM(p_location), ''), 'Sistema'),
        p_status,
        NULLIF(TRIM(p_comments), '')
    );
END $$

CREATE PROCEDURE sp_shipment_create(
    IN p_tracking_code VARCHAR(50),
    IN p_id_client INT,
    IN p_id_warehouse_origin INT,
    IN p_id_user INT,
    IN p_estimated_delivery_date DATETIME,
    IN p_notes VARCHAR(255),
    IN p_changed_by_user_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Clients WHERE id_client = p_id_client) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un cliente valido para el envio.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Warehouses WHERE id_warehouse = p_id_warehouse_origin AND status IN ('ACTIVE', 'active')) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un almacen de origen activo.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Users WHERE id_user = p_id_user AND status = 'ACTIVE') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un usuario responsable activo.';
    END IF;

    INSERT INTO Shipments (
        tracking_code,
        id_client,
        id_warehouse_origin,
        id_user,
        estimated_delivery_date,
        notes
    ) VALUES (
        NULLIF(TRIM(p_tracking_code), ''),
        p_id_client,
        p_id_warehouse_origin,
        p_id_user,
        p_estimated_delivery_date,
        NULLIF(TRIM(p_notes), '')
    );

    CALL sp_shipment_tracking_register(
        LAST_INSERT_ID(),
        'PENDING',
        p_changed_by_user_id,
        'Sistema',
        'Envio creado.'
    );
END $$

CREATE PROCEDURE sp_shipment_update(
    IN p_id_shipment INT,
    IN p_estimated_delivery_date DATETIME,
    IN p_notes VARCHAR(255),
    IN p_id_warehouse_origin INT,
    IN p_status VARCHAR(20),
    IN p_changed_by_user_id INT
)
BEGIN
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_current_warehouse INT;
    DECLARE v_detail_count INT DEFAULT 0;

    SELECT status, id_warehouse_origin
      INTO v_current_status, v_current_warehouse
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1;

    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
    END IF;

    IF v_current_status IN ('DELIVERED', 'CANCELLED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede editar un envio entregado o cancelado.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Warehouses WHERE id_warehouse = p_id_warehouse_origin AND status IN ('ACTIVE', 'active')) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un almacen de origen activo.';
    END IF;

    SELECT COUNT(*)
      INTO v_detail_count
    FROM ShipmentDetails
    WHERE id_shipment = p_id_shipment;

    IF v_detail_count > 0 AND p_id_warehouse_origin <> v_current_warehouse THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede cambiar el almacen de origen porque el envio ya tiene productos reservados.';
    END IF;

    UPDATE Shipments
    SET
        estimated_delivery_date = p_estimated_delivery_date,
        notes = NULLIF(TRIM(p_notes), ''),
        id_warehouse_origin = p_id_warehouse_origin,
        status = p_status
    WHERE id_shipment = p_id_shipment;

    CALL sp_shipment_tracking_register(
        p_id_shipment,
        p_status,
        p_changed_by_user_id,
        'Sistema',
        'Datos del envio actualizados.'
    );
END $$

CREATE PROCEDURE sp_shipment_update_status(
    IN p_id_shipment INT,
    IN p_status VARCHAR(20),
    IN p_changed_by_user_id INT,
    IN p_location VARCHAR(120),
    IN p_comments VARCHAR(255)
)
BEGIN
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_detail_count INT DEFAULT 0;

    SELECT status
      INTO v_current_status
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1;

    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
    END IF;

    IF v_current_status IN ('DELIVERED', 'CANCELLED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede cambiar el estado de un envio finalizado.';
    END IF;

    IF p_status IS NULL OR p_status NOT IN ('PENDING', 'PREPARING', 'SHIPPED', 'IN_TRANSIT') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Use las operaciones especificas para entregar o cancelar el envio.';
    END IF;

    IF p_status IN ('SHIPPED', 'IN_TRANSIT') THEN
        SELECT COUNT(*)
          INTO v_detail_count
        FROM ShipmentDetails
        WHERE id_shipment = p_id_shipment;

        IF v_detail_count = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio debe tener al menos un producto antes de avanzar a SHIPPED o IN_TRANSIT.';
        END IF;
    END IF;

    UPDATE Shipments
    SET status = p_status
    WHERE id_shipment = p_id_shipment;

    IF p_status IN ('SHIPPED', 'IN_TRANSIT') THEN
        UPDATE Boxes
        SET status = p_status
        WHERE id_shipment = p_id_shipment;
    END IF;

    CALL sp_shipment_tracking_register(
        p_id_shipment,
        p_status,
        p_changed_by_user_id,
        COALESCE(NULLIF(TRIM(p_location), ''), 'Sistema'),
        COALESCE(NULLIF(TRIM(p_comments), ''), CONCAT('Estado cambiado a ', p_status, '.'))
    );
END $$

CREATE PROCEDURE sp_shipment_mark_delivered(
    IN p_id_shipment INT,
    IN p_changed_by_user_id INT,
    IN p_location VARCHAR(120),
    IN p_comments VARCHAR(255)
)
BEGIN
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_id_warehouse_origin INT;
    DECLARE v_total_details INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT status, id_warehouse_origin
      INTO v_current_status, v_id_warehouse_origin
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1;

    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM Shipments
        WHERE id_shipment = p_id_shipment
          AND status = 'DELIVERED'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio ya fue marcado como entregado.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM Shipments
        WHERE id_shipment = p_id_shipment
          AND status = 'CANCELLED'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede entregar un envio cancelado.';
    END IF;

    SELECT COUNT(*)
      INTO v_total_details
    FROM ShipmentDetails
    WHERE id_shipment = p_id_shipment;

    IF v_total_details = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede entregar un envio sin productos.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM (
            SELECT sd.id_product, SUM(sd.quantity) AS total_quantity
            FROM ShipmentDetails sd
            WHERE sd.id_shipment = p_id_shipment
            GROUP BY sd.id_product
        ) d
        INNER JOIN Inventory i
                ON i.id_warehouse = v_id_warehouse_origin
               AND i.id_product = d.id_product
        WHERE i.stock < d.total_quantity
           OR i.reserved_stock < d.total_quantity
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El inventario reservado del envio no es suficiente para completar la entrega.';
    END IF;

    START TRANSACTION;

    UPDATE Inventory i
    INNER JOIN (
        SELECT sd.id_product, SUM(sd.quantity) AS total_quantity
        FROM ShipmentDetails sd
        WHERE sd.id_shipment = p_id_shipment
        GROUP BY sd.id_product
    ) d
            ON i.id_warehouse = v_id_warehouse_origin
           AND i.id_product = d.id_product
    SET i.stock = i.stock - d.total_quantity,
        i.reserved_stock = GREATEST(i.reserved_stock - d.total_quantity, 0);

    UPDATE Shipments
    SET
        status = 'DELIVERED',
        delivered_at = NOW()
    WHERE id_shipment = p_id_shipment;

    UPDATE Boxes
    SET status = 'DELIVERED'
    WHERE id_shipment = p_id_shipment;

    CALL sp_shipment_tracking_register(
        p_id_shipment,
        'DELIVERED',
        p_changed_by_user_id,
        COALESCE(NULLIF(TRIM(p_location), ''), 'Sistema'),
        COALESCE(NULLIF(TRIM(p_comments), ''), 'Envio marcado como entregado.')
    );

    COMMIT;
END $$

CREATE PROCEDURE sp_shipment_cancel(
    IN p_id_shipment INT,
    IN p_changed_by_user_id INT,
    IN p_location VARCHAR(120),
    IN p_comments VARCHAR(255)
)
BEGIN
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_id_warehouse_origin INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT status, id_warehouse_origin
      INTO v_current_status, v_id_warehouse_origin
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1;

    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
    END IF;

    IF v_current_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio ya fue cancelado.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM Shipments
        WHERE id_shipment = p_id_shipment
          AND status = 'DELIVERED'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede cancelar un envio ya entregado.';
    END IF;

    START TRANSACTION;

    UPDATE Inventory i
    INNER JOIN (
        SELECT sd.id_product, SUM(sd.quantity) AS total_quantity
        FROM ShipmentDetails sd
        WHERE sd.id_shipment = p_id_shipment
        GROUP BY sd.id_product
    ) d
            ON i.id_warehouse = v_id_warehouse_origin
           AND i.id_product = d.id_product
    SET i.reserved_stock = GREATEST(i.reserved_stock - d.total_quantity, 0);

    UPDATE Shipments
    SET status = 'CANCELLED'
    WHERE id_shipment = p_id_shipment;

    CALL sp_shipment_tracking_register(
        p_id_shipment,
        'CANCELLED',
        p_changed_by_user_id,
        COALESCE(NULLIF(TRIM(p_location), ''), 'Sistema'),
        COALESCE(NULLIF(TRIM(p_comments), ''), 'Envio cancelado.')
    );

    COMMIT;
END $$

CREATE PROCEDURE sp_shipment_get_by_id(IN p_id_shipment INT)
BEGIN
    SELECT
        s.id_shipment,
        s.tracking_code,
        s.id_client,
        CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) AS client_name,
        s.id_warehouse_origin,
        CONCAT('Almacen ', w.id_warehouse) AS warehouse_name,
        s.id_user,
        u.username AS user_name,
        s.shipment_date,
        s.estimated_delivery_date,
        s.delivered_at,
        s.status,
        s.notes,
        s.created_at,
        s.updated_at
    FROM Shipments s
    INNER JOIN Clients c ON c.id_client = s.id_client
    INNER JOIN Warehouses w ON w.id_warehouse = s.id_warehouse_origin
    INNER JOIN Users u ON u.id_user = s.id_user
    WHERE s.id_shipment = p_id_shipment
    LIMIT 1;
END $$

CREATE PROCEDURE sp_shipment_get_by_tracking(IN p_tracking_code VARCHAR(50))
BEGIN
    SELECT
        s.id_shipment,
        s.tracking_code,
        s.id_client,
        CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) AS client_name,
        s.id_warehouse_origin,
        CONCAT('Almacen ', w.id_warehouse) AS warehouse_name,
        s.id_user,
        u.username AS user_name,
        s.shipment_date,
        s.estimated_delivery_date,
        s.delivered_at,
        s.status,
        s.notes,
        s.created_at,
        s.updated_at
    FROM Shipments s
    INNER JOIN Clients c ON c.id_client = s.id_client
    INNER JOIN Warehouses w ON w.id_warehouse = s.id_warehouse_origin
    INNER JOIN Users u ON u.id_user = s.id_user
    WHERE s.tracking_code = TRIM(p_tracking_code)
    LIMIT 1;
END $$

CREATE PROCEDURE sp_shipment_search(
    IN p_search_text VARCHAR(120),
    IN p_status VARCHAR(20),
    IN p_shipment_date DATE,
    IN p_offset INT,
    IN p_page_size INT
)
BEGIN
    DECLARE v_search_text VARCHAR(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    SET v_search_text = NULLIF(TRIM(CONVERT(p_search_text USING utf8mb4)), '');

    SELECT
        s.id_shipment,
        s.tracking_code,
        s.id_client,
        CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) AS client_name,
        s.id_warehouse_origin,
        CONCAT('Almacen ', w.id_warehouse) AS warehouse_name,
        s.id_user,
        u.username AS user_name,
        s.shipment_date,
        s.estimated_delivery_date,
        s.delivered_at,
        s.status,
        s.notes,
        s.created_at,
        s.updated_at
    FROM Shipments s
    INNER JOIN Clients c ON c.id_client = s.id_client
    INNER JOIN Warehouses w ON w.id_warehouse = s.id_warehouse_origin
    INNER JOIN Users u ON u.id_user = s.id_user
    WHERE (
            v_search_text IS NULL
            OR CAST(s.id_shipment AS CHAR) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
            OR s.tracking_code COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
            OR CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
        )
      AND (
            p_status IS NULL
            OR s.status = p_status
        )
      AND (
            p_shipment_date IS NULL
            OR DATE(s.shipment_date) = p_shipment_date
        )
    ORDER BY s.id_shipment DESC
    LIMIT p_offset, p_page_size;

    SELECT
        COUNT(*) AS total_records
    FROM Shipments s
    INNER JOIN Clients c ON c.id_client = s.id_client
    WHERE (
            v_search_text IS NULL
            OR CAST(s.id_shipment AS CHAR) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
            OR s.tracking_code COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
            OR CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) COLLATE utf8mb4_general_ci LIKE CONCAT('%', v_search_text, '%')
        )
      AND (
            p_status IS NULL
            OR s.status = p_status
        )
      AND (
            p_shipment_date IS NULL
            OR DATE(s.shipment_date) = p_shipment_date
        );
END $$

CREATE PROCEDURE sp_shipment_get_detail(IN p_id_shipment INT)
BEGIN
    SELECT
        s.id_shipment,
        s.tracking_code,
        s.id_client,
        CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) AS client_name,
        s.id_warehouse_origin,
        CONCAT('Almacen ', w.id_warehouse) AS warehouse_name,
        s.id_user,
        u.username AS user_name,
        s.shipment_date,
        s.estimated_delivery_date,
        s.delivered_at,
        s.status,
        s.notes,
        s.created_at,
        s.updated_at
    FROM Shipments s
    INNER JOIN Clients c ON c.id_client = s.id_client
    INNER JOIN Warehouses w ON w.id_warehouse = s.id_warehouse_origin
    INNER JOIN Users u ON u.id_user = s.id_user
    WHERE s.id_shipment = p_id_shipment
    LIMIT 1;

    SELECT
        st.id_tracking,
        st.id_shipment,
        st.id_user,
        u.username AS user_name,
        st.location,
        st.status,
        st.comments,
        st.tracking_date
    FROM ShipmentTracking st
    LEFT JOIN Users u ON u.id_user = st.id_user
    WHERE st.id_shipment = p_id_shipment
    ORDER BY st.tracking_date DESC, st.id_tracking DESC;

    SELECT
        CONCAT(
            b.box_code,
            ' | ',
            ROUND(b.length_cm, 2), ' x ', ROUND(b.width_cm, 2), ' x ', ROUND(b.height_cm, 2), ' cm',
            ' | ',
            ROUND(b.weight_kg, 2), ' kg',
            ' | Valor declarado: ',
            ROUND(b.declared_value, 2),
            ' | ',
            b.status
        ) AS box_description
    FROM Boxes b
    WHERE b.id_shipment = p_id_shipment
    ORDER BY b.id_box;

    SELECT
        CONCAT(
            COALESCE(b.box_code, 'Sin caja'),
            ' | ',
            p.product_name,
            ' | Cantidad: ',
            sd.quantity,
            ' | Peso unit.: ',
            ROUND(sd.unit_weight_kg, 3),
            ' kg'
        ) AS product_description
    FROM ShipmentDetails sd
    INNER JOIN Products p ON p.id_product = sd.id_product
    LEFT JOIN Boxes b ON b.id_box = sd.id_box
    WHERE sd.id_shipment = p_id_shipment
    ORDER BY sd.id_box, p.product_name;
END $$

DELIMITER ;

-- SP Leonardo

DELIMITER $$
CREATE PROCEDURE sp_box_create(
    IN p_id_shipment INT,
    IN p_box_code VARCHAR(50),
    IN p_image_path VARCHAR(255),
    IN p_length_cm DECIMAL(10,2),
    IN p_width_cm DECIMAL(10,2),
    IN p_height_cm DECIMAL(10,2),
    IN p_weight_kg DECIMAL(10,2),
    IN p_declared_value DECIMAL(10,2),
    IN p_status VARCHAR(20)
)
BEGIN
    DECLARE v_shipment_status VARCHAR(20);
    DECLARE v_box_code VARCHAR(50);
    DECLARE v_sequence INT DEFAULT 0;

    SELECT status
      INTO v_shipment_status
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1;

    IF v_shipment_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un envio valido para registrar la caja.';
    END IF;

    IF v_shipment_status IN ('DELIVERED', 'CANCELLED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se pueden registrar cajas en un envio entregado o cancelado.';
    END IF;

    IF p_length_cm IS NULL OR p_length_cm <= 0
       OR p_width_cm IS NULL OR p_width_cm <= 0
       OR p_height_cm IS NULL OR p_height_cm <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las dimensiones de la caja deben ser mayores que cero.';
    END IF;

    IF p_weight_kg IS NULL OR p_weight_kg < 0 OR p_declared_value IS NULL OR p_declared_value < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El peso y el valor declarado no pueden ser negativos.';
    END IF;

    SET v_box_code = NULLIF(TRIM(p_box_code), '');

    IF v_box_code IS NULL THEN
        SELECT COUNT(*) + 1
          INTO v_sequence
        FROM Boxes
        WHERE id_shipment = p_id_shipment;

        SET v_box_code = CONCAT('BOX-', LPAD(p_id_shipment, 6, '0'), '-', LPAD(v_sequence, 4, '0'));

        WHILE EXISTS (SELECT 1 FROM Boxes WHERE box_code = v_box_code) DO
            SET v_sequence = v_sequence + 1;
            SET v_box_code = CONCAT('BOX-', LPAD(p_id_shipment, 6, '0'), '-', LPAD(v_sequence, 4, '0'));
        END WHILE;
    END IF;

    INSERT INTO boxes (
        id_shipment, box_code, image_path,
        length_cm, width_cm, height_cm,
        weight_kg, declared_value, status
    ) VALUES (
        p_id_shipment, v_box_code, NULLIF(p_image_path, ''),
        p_length_cm, p_width_cm, p_height_cm,
        p_weight_kg, p_declared_value, COALESCE(NULLIF(TRIM(p_status), ''), 'PACKED')
    );
END$$

CREATE PROCEDURE sp_box_list()
BEGIN
    SELECT *
    FROM boxes
    ORDER BY id_box DESC;
END$$

CREATE PROCEDURE sp_box_find_by_id(IN p_id_box INT)
BEGIN
    SELECT *
    FROM boxes
    WHERE id_box = p_id_box;
END$$

CREATE PROCEDURE sp_box_list_by_shipment(IN p_id_shipment INT)
BEGIN
    SELECT *
    FROM boxes
    WHERE id_shipment = p_id_shipment
    ORDER BY id_box DESC;
END$$

CREATE PROCEDURE sp_box_update(
    IN p_id_box INT,
    IN p_id_shipment INT,
    IN p_box_code VARCHAR(50),
    IN p_image_path VARCHAR(255),
    IN p_length_cm DECIMAL(10,2),
    IN p_width_cm DECIMAL(10,2),
    IN p_height_cm DECIMAL(10,2),
    IN p_weight_kg DECIMAL(10,2),
    IN p_declared_value DECIMAL(10,2),
    IN p_status VARCHAR(20)
)
BEGIN
    DECLARE v_current_shipment INT;
    DECLARE v_current_code VARCHAR(50);
    DECLARE v_shipment_status VARCHAR(20);

    SELECT id_shipment, box_code
      INTO v_current_shipment, v_current_code
    FROM Boxes
    WHERE id_box = p_id_box
    LIMIT 1;

    IF v_current_shipment IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La caja indicada no existe.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM ShipmentDetails
        WHERE id_box = p_id_box
          AND p_id_shipment <> v_current_shipment
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede mover la caja a otro envio porque ya contiene productos.';
    END IF;

    SELECT status
      INTO v_shipment_status
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1;

    IF v_shipment_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un envio valido para la caja.';
    END IF;

    IF v_shipment_status IN ('DELIVERED', 'CANCELLED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede editar una caja de un envio entregado o cancelado.';
    END IF;

    UPDATE boxes
    SET
        id_shipment = p_id_shipment,
        box_code = COALESCE(NULLIF(TRIM(p_box_code), ''), v_current_code),
        image_path = NULLIF(p_image_path, ''),
        length_cm = p_length_cm,
        width_cm = p_width_cm,
        height_cm = p_height_cm,
        weight_kg = p_weight_kg,
        declared_value = p_declared_value,
        status = COALESCE(NULLIF(TRIM(p_status), ''), status)
    WHERE id_box = p_id_box;
END$$

CREATE PROCEDURE sp_box_delete(IN p_id_box INT)
BEGIN
    DECLARE v_id_shipment INT;
    DECLARE v_id_warehouse_origin INT;
    DECLARE v_shipment_status VARCHAR(20);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT b.id_shipment, s.id_warehouse_origin, s.status
      INTO v_id_shipment, v_id_warehouse_origin, v_shipment_status
    FROM Boxes b
    INNER JOIN Shipments s ON s.id_shipment = b.id_shipment
    WHERE b.id_box = p_id_box
    LIMIT 1;

    IF v_id_shipment IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La caja indicada no existe.';
    END IF;

    IF v_shipment_status NOT IN ('PENDING', 'PREPARING') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden eliminar cajas de envios en estado PENDING o PREPARING.';
    END IF;

    START TRANSACTION;

    UPDATE Inventory i
    INNER JOIN (
        SELECT sd.id_product, SUM(sd.quantity) AS total_quantity
        FROM ShipmentDetails sd
        WHERE sd.id_box = p_id_box
        GROUP BY sd.id_product
    ) d
            ON i.id_warehouse = v_id_warehouse_origin
           AND i.id_product = d.id_product
    SET i.reserved_stock = GREATEST(i.reserved_stock - d.total_quantity, 0);

    DELETE FROM boxes
    WHERE id_box = p_id_box;

    COMMIT;
END$$

CREATE PROCEDURE sp_shipment_detail_create(
    IN p_id_shipment INT,
    IN p_id_box INT,
    IN p_id_product INT,
    IN p_quantity INT,
    IN p_unit_weight_kg DECIMAL(10,2)
)
BEGIN
    DECLARE v_id_warehouse_origin INT;
    DECLARE v_shipment_status VARCHAR(20);
    DECLARE v_available_stock INT DEFAULT 0;
    DECLARE v_inventory_id INT;
    DECLARE v_existing_detail_id INT DEFAULT NULL;
    DECLARE v_default_unit_weight DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_quantity IS NULL OR p_quantity <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cantidad del detalle debe ser mayor que cero.';
    END IF;

    START TRANSACTION;

    SELECT id_warehouse_origin, status
      INTO v_id_warehouse_origin, v_shipment_status
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1
    FOR UPDATE;

    IF v_shipment_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un envio valido.';
    END IF;

    IF v_shipment_status NOT IN ('PENDING', 'PREPARING') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden agregar productos a envios en estado PENDING o PREPARING.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM Boxes
        WHERE id_box = p_id_box
          AND id_shipment = p_id_shipment
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La caja seleccionada no pertenece al envio indicado.';
    END IF;

    SELECT unit_weight_kg
      INTO v_default_unit_weight
    FROM Products
    WHERE id_product = p_id_product
      AND status = 'ACTIVE'
    LIMIT 1;

    IF v_default_unit_weight IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El producto seleccionado no existe o esta inactivo.';
    END IF;

    SELECT id_inventory, (stock - reserved_stock)
      INTO v_inventory_id, v_available_stock
    FROM Inventory
    WHERE id_warehouse = v_id_warehouse_origin
      AND id_product = p_id_product
    LIMIT 1
    FOR UPDATE;

    IF v_inventory_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El producto seleccionado no tiene inventario registrado en el almacen de origen del envio.';
    END IF;

    IF p_quantity > v_available_stock THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cantidad solicitada supera el stock disponible del producto en el almacen de origen.';
    END IF;

    SELECT id_shipment_detail
      INTO v_existing_detail_id
    FROM ShipmentDetails
    WHERE id_shipment = p_id_shipment
      AND id_box = p_id_box
      AND id_product = p_id_product
    LIMIT 1
    FOR UPDATE;

    IF v_existing_detail_id IS NULL THEN
        INSERT INTO shipmentdetails (
            id_shipment, id_box, id_product, quantity, unit_weight_kg
        ) VALUES (
            p_id_shipment, p_id_box, p_id_product, p_quantity, COALESCE(NULLIF(p_unit_weight_kg, 0), v_default_unit_weight)
        );
    ELSE
        UPDATE ShipmentDetails
        SET quantity = quantity + p_quantity,
            unit_weight_kg = COALESCE(NULLIF(p_unit_weight_kg, 0), v_default_unit_weight)
        WHERE id_shipment_detail = v_existing_detail_id;
    END IF;

    UPDATE Inventory
    SET reserved_stock = reserved_stock + p_quantity
    WHERE id_inventory = v_inventory_id;

    COMMIT;
END$$

CREATE PROCEDURE sp_shipment_detail_list()
BEGIN
    SELECT *
    FROM shipmentdetails
    ORDER BY id_shipment_detail DESC;
END$$

CREATE PROCEDURE sp_shipment_detail_find_by_id(IN p_id_shipment_detail INT)
BEGIN
    SELECT *
    FROM shipmentdetails
    WHERE id_shipment_detail = p_id_shipment_detail;
END$$

CREATE PROCEDURE sp_shipment_detail_list_by_shipment(IN p_id_shipment INT)
BEGIN
    SELECT *
    FROM shipmentdetails
    WHERE id_shipment = p_id_shipment
    ORDER BY id_shipment_detail DESC;
END$$

CREATE PROCEDURE sp_shipment_detail_list_by_box(IN p_id_box INT)
BEGIN
    SELECT *
    FROM shipmentdetails
    WHERE id_box = p_id_box
    ORDER BY id_shipment_detail DESC;
END$$

CREATE PROCEDURE sp_shipment_detail_update(
    IN p_id_shipment_detail INT,
    IN p_id_shipment INT,
    IN p_id_box INT,
    IN p_id_product INT,
    IN p_quantity INT,
    IN p_unit_weight_kg DECIMAL(10,2)
)
BEGIN
    DECLARE v_old_shipment_id INT;
    DECLARE v_old_box_id INT;
    DECLARE v_old_product_id INT;
    DECLARE v_old_quantity INT;
    DECLARE v_id_warehouse_origin INT;
    DECLARE v_shipment_status VARCHAR(20);
    DECLARE v_old_inventory_id INT;
    DECLARE v_new_inventory_id INT;
    DECLARE v_available_stock INT DEFAULT 0;
    DECLARE v_default_unit_weight DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_quantity IS NULL OR p_quantity <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cantidad del detalle debe ser mayor que cero.';
    END IF;

    START TRANSACTION;

    SELECT id_shipment, id_box, id_product, quantity
      INTO v_old_shipment_id, v_old_box_id, v_old_product_id, v_old_quantity
    FROM ShipmentDetails
    WHERE id_shipment_detail = p_id_shipment_detail
    LIMIT 1
    FOR UPDATE;

    IF v_old_shipment_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El detalle seleccionado no existe.';
    END IF;

    IF v_old_shipment_id <> p_id_shipment THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede mover el detalle a otro envio.';
    END IF;

    SELECT id_warehouse_origin, status
      INTO v_id_warehouse_origin, v_shipment_status
    FROM Shipments
    WHERE id_shipment = p_id_shipment
    LIMIT 1
    FOR UPDATE;

    IF v_shipment_status NOT IN ('PENDING', 'PREPARING') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden editar detalles en envios en estado PENDING o PREPARING.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM Boxes
        WHERE id_box = p_id_box
          AND id_shipment = p_id_shipment
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La caja seleccionada no pertenece al envio indicado.';
    END IF;

    SELECT unit_weight_kg
      INTO v_default_unit_weight
    FROM Products
    WHERE id_product = p_id_product
      AND status = 'ACTIVE'
    LIMIT 1;

    IF v_default_unit_weight IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El producto seleccionado no existe o esta inactivo.';
    END IF;

    SELECT id_inventory
      INTO v_old_inventory_id
    FROM Inventory
    WHERE id_warehouse = v_id_warehouse_origin
      AND id_product = v_old_product_id
    LIMIT 1
    FOR UPDATE;

    IF v_old_inventory_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El inventario original del detalle no existe o esta inconsistente.';
    END IF;

    IF v_old_product_id = p_id_product THEN
        SELECT id_inventory, (stock - reserved_stock + v_old_quantity)
          INTO v_new_inventory_id, v_available_stock
        FROM Inventory
        WHERE id_warehouse = v_id_warehouse_origin
          AND id_product = p_id_product
        LIMIT 1
        FOR UPDATE;

        IF v_new_inventory_id IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No existe inventario valido para el producto seleccionado.';
        END IF;

        IF p_quantity > v_available_stock THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La nueva cantidad supera el stock disponible del producto en el almacen de origen.';
        END IF;

        UPDATE ShipmentDetails
        SET id_box = p_id_box,
            id_product = p_id_product,
            quantity = p_quantity,
            unit_weight_kg = COALESCE(NULLIF(p_unit_weight_kg, 0), v_default_unit_weight)
        WHERE id_shipment_detail = p_id_shipment_detail;

        UPDATE Inventory
        SET reserved_stock = reserved_stock + (p_quantity - v_old_quantity)
        WHERE id_inventory = v_new_inventory_id;
    ELSE
        SELECT id_inventory, (stock - reserved_stock)
          INTO v_new_inventory_id, v_available_stock
        FROM Inventory
        WHERE id_warehouse = v_id_warehouse_origin
          AND id_product = p_id_product
        LIMIT 1
        FOR UPDATE;

        IF v_new_inventory_id IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El producto nuevo no tiene inventario registrado en el almacen de origen.';
        END IF;

        IF p_quantity > v_available_stock THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La nueva cantidad supera el stock disponible del producto seleccionado.';
        END IF;

        UPDATE Inventory
        SET reserved_stock = GREATEST(reserved_stock - v_old_quantity, 0)
        WHERE id_inventory = v_old_inventory_id;

        UPDATE Inventory
        SET reserved_stock = reserved_stock + p_quantity
        WHERE id_inventory = v_new_inventory_id;

        UPDATE ShipmentDetails
        SET id_box = p_id_box,
            id_product = p_id_product,
            quantity = p_quantity,
            unit_weight_kg = COALESCE(NULLIF(p_unit_weight_kg, 0), v_default_unit_weight)
        WHERE id_shipment_detail = p_id_shipment_detail;
    END IF;

    COMMIT;
END$$

CREATE PROCEDURE sp_shipment_detail_delete(IN p_id_shipment_detail INT)
BEGIN
    DECLARE v_id_shipment INT;
    DECLARE v_id_product INT;
    DECLARE v_quantity INT;
    DECLARE v_id_warehouse_origin INT;
    DECLARE v_shipment_status VARCHAR(20);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT sd.id_shipment, sd.id_product, sd.quantity, s.id_warehouse_origin, s.status
      INTO v_id_shipment, v_id_product, v_quantity, v_id_warehouse_origin, v_shipment_status
    FROM ShipmentDetails sd
    INNER JOIN Shipments s ON s.id_shipment = sd.id_shipment
    WHERE sd.id_shipment_detail = p_id_shipment_detail
    LIMIT 1
    FOR UPDATE;

    IF v_id_shipment IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El detalle seleccionado no existe.';
    END IF;

    IF v_shipment_status NOT IN ('PENDING', 'PREPARING') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden eliminar detalles de envios en estado PENDING o PREPARING.';
    END IF;

    UPDATE Inventory
    SET reserved_stock = GREATEST(reserved_stock - v_quantity, 0)
    WHERE id_warehouse = v_id_warehouse_origin
      AND id_product = v_id_product;

    DELETE FROM shipmentdetails
    WHERE id_shipment_detail = p_id_shipment_detail;

    COMMIT;
END$$

CREATE PROCEDURE sp_shipment_tracking_create(
    IN p_id_shipment INT,
    IN p_id_user INT,
    IN p_location VARCHAR(120),
    IN p_status VARCHAR(20),
    IN p_comments VARCHAR(255)
)
BEGIN
    INSERT INTO shipmenttracking (
        id_shipment, id_user, location, status, comments
    ) VALUES (
        p_id_shipment, p_id_user, p_location, p_status, p_comments
    );
END$$

CREATE PROCEDURE sp_shipment_tracking_list()
BEGIN
    SELECT *
    FROM shipmenttracking
    ORDER BY tracking_date DESC;
END$$

CREATE PROCEDURE sp_shipment_tracking_find_by_id(IN p_id_tracking INT)
BEGIN
    SELECT *
    FROM shipmenttracking
    WHERE id_tracking = p_id_tracking;
END$$

CREATE PROCEDURE sp_shipment_tracking_list_by_shipment(IN p_id_shipment INT)
BEGIN
    SELECT *
    FROM shipmenttracking
    WHERE id_shipment = p_id_shipment
    ORDER BY tracking_date DESC;
END$$

CREATE PROCEDURE sp_shipment_tracking_update(
    IN p_id_tracking INT,
    IN p_id_shipment INT,
    IN p_id_user INT,
    IN p_location VARCHAR(120),
    IN p_status VARCHAR(20),
    IN p_comments VARCHAR(255)
)
BEGIN
    UPDATE shipmenttracking
    SET
        id_shipment = p_id_shipment,
        id_user = p_id_user,
        location = p_location,
        status = p_status,
        comments = p_comments
    WHERE id_tracking = p_id_tracking;
END$$

CREATE PROCEDURE sp_shipment_tracking_delete(IN p_id_tracking INT)
BEGIN
    DELETE FROM shipmenttracking
    WHERE id_tracking = p_id_tracking;
END$$

DELIMITER ;

-- SP Jorge

DELIMITER $$
-- =========================================================================
-- 1. REGISTRAR ALMACÉN
-- =========================================================================
CREATE PROCEDURE sp_warehouse_save(
    IN p_warehouse_name VARCHAR(100),
    IN p_address VARCHAR(200),
    IN p_city VARCHAR(80),
    IN p_country VARCHAR(80),
    IN p_phone VARCHAR(20)
)
BEGIN
    -- Validaciones de nulidad o vacío
    IF p_warehouse_name IS NULL OR TRIM(p_warehouse_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre del almacén es obligatorio.';
    END IF;
    IF p_address IS NULL OR TRIM(p_address) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La dirección es obligatoria.';
    END IF;
    IF p_city IS NULL OR TRIM(p_city) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La ciudad es obligatoria.';
    END IF;
    IF p_country IS NULL OR TRIM(p_country) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El país es obligatorio.';
    END IF;

    INSERT INTO Warehouses (warehouse_name, address, city, country, phone, status)
    VALUES (TRIM(p_warehouse_name), TRIM(p_address), TRIM(p_city), TRIM(p_country), TRIM(p_phone), 'ACTIVE');
END $$

-- =========================================================================
-- 2. MODIFICAR ALMACÉN
-- =========================================================================
CREATE PROCEDURE sp_warehouse_update(
    IN p_id_warehouse INT,
    IN p_warehouse_name VARCHAR(100),
    IN p_address VARCHAR(200),
    IN p_city VARCHAR(80),
    IN p_country VARCHAR(80),
    IN p_phone VARCHAR(20)
)
BEGIN
    IF p_id_warehouse IS NULL OR p_id_warehouse <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ID de almacén no válido.';
    END IF;
    IF p_warehouse_name IS NULL OR TRIM(p_warehouse_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre del almacén es obligatorio.';
    END IF;

    UPDATE Warehouses 
    SET warehouse_name = TRIM(p_warehouse_name),
        address = TRIM(p_address),
        city = TRIM(p_city),
        country = TRIM(p_country),
        phone = TRIM(p_phone)
    WHERE id_warehouse = p_id_warehouse;
END $$

-- =========================================================================
-- 3. CAMBIO DE ESTADO (CON RESTRICCIÓN DE INTEGRIDAD LOGÍSTICA)
-- =========================================================================
CREATE PROCEDURE sp_warehouse_change_status(
    IN p_id_warehouse INT,
    IN p_status ENUM('ACTIVE', 'INACTIVE')
)
BEGIN
    -- Validar que no existan filas en inventario para este almacén antes de deshabilitarlo
    IF p_status = 'INACTIVE' THEN
        IF EXISTS (SELECT 1 FROM inventory WHERE id_warehouse = p_id_warehouse) THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Operación rechazada: No se puede inactivar el almacén porque tiene productos asignados en el inventario.';
        END IF;
    END IF;

    UPDATE Warehouses 
    SET status = p_status 
    WHERE id_warehouse = p_id_warehouse;
END $$

-- =========================================================================
-- 4. ELIMINACIÓN FÍSICA DEFINITIVA
-- =========================================================================
CREATE PROCEDURE sp_warehouse_delete_permanent(
    IN p_id_warehouse INT
)
BEGIN
    -- Validar si tiene dependencias estructurales
    IF EXISTS (SELECT 1 FROM inventory WHERE id_warehouse = p_id_warehouse) THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'No se puede eliminar: El almacén posee registros históricos en la tabla de Inventario.';
    END IF;

    DELETE FROM Warehouses WHERE id_warehouse = p_id_warehouse;
END $$

-- =========================================================================
-- 5. LISTAR O BUSCAR ALMACENES POR ESTADO
-- =========================================================================
CREATE PROCEDURE sp_warehouse_search(
    IN p_query VARCHAR(100),
    IN p_status ENUM('ACTIVE', 'INACTIVE')
)
BEGIN
    IF p_query IS NULL OR TRIM(p_query) = '' THEN
        SELECT id_warehouse, warehouse_name, address, city, country, phone, status, created_at, updated_at
        FROM Warehouses
        WHERE status = p_status;
    ELSE
        SELECT id_warehouse, warehouse_name, address, city, country, phone, status, created_at, updated_at
        FROM Warehouses
        WHERE status = p_status 
          AND (id_warehouse = CAST(p_query AS SIGNED) 
               OR warehouse_name LIKE CONCAT('%', TRIM(p_query), '%') 
               OR city LIKE CONCAT('%', TRIM(p_query), '%'));
    END IF;
END $$

-- =========================================================================
-- 1. REGISTRAR O ASIGNAR STOCK INICIAL (CON VALIDACIONES)
-- =========================================================================
CREATE PROCEDURE sp_inventory_save(
    IN p_id_warehouse INT,
    IN p_id_product INT,
    IN p_stock INT,
    IN p_reserved_stock INT,
    IN p_min_stock INT
)
BEGIN
    -- Validaciones de llaves relacionales existentes
    IF NOT EXISTS (SELECT 1 FROM warehouses WHERE id_warehouse = p_id_warehouse) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ID de almacén ingresado no existe.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM products WHERE id_product = p_id_product) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ID de producto ingresado no existe.';
    END IF;
    
    -- Validar restricciones lógicas de negocio
    IF p_stock < 0 OR p_reserved_stock < 0 OR p_min_stock < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las existencias físicas de stock no pueden ser negativas.';
    END IF;

    -- Validar restricción UNIQUE para evitar duplicados en el mismo almacén
    IF EXISTS (SELECT 1 FROM inventory WHERE id_warehouse = p_id_warehouse AND id_product = p_id_product) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Este producto ya cuenta con un registro en este almacén. Use la edición.';
    END IF;

    -- Inserción limpia sin columna 'status'
    INSERT INTO inventory (id_warehouse, id_product, stock, reserved_stock, min_stock)
    VALUES (p_id_warehouse, p_id_product, p_stock, p_reserved_stock, p_min_stock);
END $$

-- =========================================================================
-- 2. MODIFICAR CANTIDADES DE STOCK (AJUSTE)
-- =========================================================================
CREATE PROCEDURE sp_inventory_update(
    IN p_id_inventory INT,
    IN p_stock INT,
    IN p_reserved_stock INT,
    IN p_min_stock INT
)
BEGIN
    IF p_stock < 0 OR p_reserved_stock < 0 OR p_min_stock < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los valores numéricos de existencias no pueden ser menores a 0.';
    END IF;

    UPDATE inventory 
    SET stock = p_stock,
        reserved_stock = p_reserved_stock,
        min_stock = p_min_stock
    WHERE id_inventory = p_id_inventory;
END $$

-- =========================================================================
-- 3. BUSCAR O LISTAR CON CRUCE DE TABLAS (SIN COLUMNA STATUS)
-- =========================================================================
CREATE PROCEDURE sp_inventory_search(
    IN p_query VARCHAR(100)
)
BEGIN
    IF p_query IS NULL OR TRIM(p_query) = '' THEN
        SELECT i.id_inventory, i.id_warehouse, i.id_product, i.stock, i.reserved_stock, i.min_stock,
               w.warehouse_name, p.product_name, p.sku
        FROM inventory i
        JOIN warehouses w ON i.id_warehouse = w.id_warehouse
        JOIN products p ON i.id_product = p.id_product
        ORDER BY i.id_inventory DESC;
    ELSE
        SELECT i.id_inventory, i.id_warehouse, i.id_product, i.stock, i.reserved_stock, i.min_stock,
               w.warehouse_name, p.product_name, p.sku
        FROM inventory i
        JOIN warehouses w ON i.id_warehouse = w.id_warehouse
        JOIN products p ON i.id_product = p.id_product
        WHERE p.product_name LIKE CONCAT('%', TRIM(p_query), '%')
           OR w.warehouse_name LIKE CONCAT('%', TRIM(p_query), '%')
           OR p.sku LIKE CONCAT('%', TRIM(p_query), '%')
           -- Manejo seguro por si buscan por un ID numérico directo
           OR (p_query REGEXP '^[0-9]+$' AND i.id_inventory = CAST(p_query AS SIGNED))
        ORDER BY i.id_inventory DESC;
    END IF;
END $$

-- =========================================================================
-- 4. TRANSFERENCIA TRANSACCIONAL ENTRE ALMACENES
-- =========================================================================
CREATE PROCEDURE sp_inventory_transfer(
    IN p_id_source_inv INT,
    IN p_id_target_wh INT,
    IN p_qty INT
)
BEGIN
    DECLARE v_id_product INT;
    DECLARE v_current_stock INT;
    DECLARE v_target_inv_id INT;
    
    -- Manejo de excepciones transaccionales
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error transaccional: No se pudo completar la transferencia de mercadería.';
    END;

    START TRANSACTION;
    
        -- 1. Obtener los datos del origen y validar stock real disponible
        SELECT id_product, stock INTO v_id_product, v_current_stock 
        FROM inventory 
        WHERE id_inventory = p_id_source_inv;
        
        IF v_current_stock < p_qty THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Operación rechazada: Stock insuficiente en el almacén de origen.';
        END IF;
        
        -- 2. Decrementar el stock del origen
        UPDATE inventory 
        SET stock = stock - p_qty 
        WHERE id_inventory = p_id_source_inv;
        
        -- 3. Verificar si el producto ya existe en el almacén destino
        SELECT id_inventory INTO v_target_inv_id 
        FROM inventory 
        WHERE id_warehouse = p_id_target_wh AND id_product = v_id_product;
        
        IF v_target_inv_id IS NOT NULL THEN
            -- Si existe, incrementamos stock
            UPDATE inventory 
            SET stock = stock + p_qty 
            WHERE id_inventory = v_target_inv_id;
        ELSE
            -- Si no existe, creamos la fila inicial para ese almacén con un stock mínimo base de 5
            INSERT INTO inventory (id_warehouse, id_product, stock, reserved_stock, min_stock)
            VALUES (p_id_target_wh, v_id_product, p_qty, 0, 5);
        END IF;
        
    COMMIT;
END $$

DELIMITER ;

-- =========================================================================
-- PROCEDIMIENTOS DE REFERENCIA PARA COMBOS DE LA APLICACION
-- Estos SP evitan que el usuario escriba IDs manualmente en la interfaz.
-- =========================================================================

DELIMITER $$

CREATE PROCEDURE sp_warehouse_list_for_combo()
BEGIN
    SELECT
        w.id_warehouse AS id,
        CONCAT(w.warehouse_name, ' - ', w.city) AS label
    FROM Warehouses w
    ORDER BY w.warehouse_name, w.city;
END $$

CREATE PROCEDURE sp_user_list_for_combo()
BEGIN
    SELECT
        u.id_user AS id,
        u.username AS label
    FROM Users u
    ORDER BY u.username;
END $$

CREATE PROCEDURE sp_product_list_for_combo()
BEGIN
    SELECT
        p.id_product AS id,
        CONCAT(p.sku, ' - ', p.product_name) AS label
    FROM Products p
    WHERE p.status = 'ACTIVE'
    ORDER BY p.product_name, p.sku;
END $$

CREATE PROCEDURE sp_product_list_for_combo_by_shipment(IN p_id_shipment INT)
BEGIN
    SELECT
        p.id_product AS id,
        CONCAT(
            p.sku,
            ' - ',
            p.product_name,
            ' | Disponible: ',
            GREATEST(i.stock - i.reserved_stock, 0),
            ' | Peso: ',
            ROUND(p.unit_weight_kg, 2),
            ' kg'
        ) AS label,
        GREATEST(i.stock - i.reserved_stock, 0) AS available_stock,
        p.unit_weight_kg
    FROM Shipments s
    INNER JOIN Inventory i ON i.id_warehouse = s.id_warehouse_origin
    INNER JOIN Products p ON p.id_product = i.id_product
    WHERE s.id_shipment = p_id_shipment
      AND p.status = 'ACTIVE'
      AND (
            GREATEST(i.stock - i.reserved_stock, 0) > 0
            OR EXISTS (
                SELECT 1
                FROM ShipmentDetails sd
                WHERE sd.id_shipment = s.id_shipment
                  AND sd.id_product = p.id_product
            )
      )
    ORDER BY p.product_name, p.sku;
END $$

CREATE PROCEDURE sp_shipment_list_for_combo()
BEGIN
    SELECT
        s.id_shipment AS id,
        CONCAT(s.tracking_code, ' - ', s.status) AS label
    FROM Shipments s
    ORDER BY s.shipment_date DESC, s.id_shipment DESC;
END $$

CREATE PROCEDURE sp_box_list_for_combo()
BEGIN
    SELECT
        b.id_box AS id,
        CONCAT(b.box_code, ' - ', s.tracking_code) AS label
    FROM Boxes b
    INNER JOIN Shipments s ON s.id_shipment = b.id_shipment
    ORDER BY s.shipment_date DESC, b.box_code;
END $$

CREATE PROCEDURE sp_box_list_for_combo_by_shipment(IN p_id_shipment INT)
BEGIN
    SELECT
        b.id_box AS id,
        b.box_code AS label
    FROM Boxes b
    WHERE b.id_shipment = p_id_shipment
    ORDER BY b.box_code;
END $$

DELIMITER ;

-- SP Sergio
DELIMITER $$

CREATE PROCEDURE sp_buscar_categorias(IN p_search VARCHAR(120))
BEGIN
    SELECT
        c.id_category,
        c.category_name,
        c.description,
        c.image_path,
        c.created_at,
        c.updated_at
    FROM Categories c
    WHERE p_search IS NULL
       OR TRIM(p_search) = ''
       OR c.category_name LIKE CONCAT('%', TRIM(p_search), '%')
       OR c.description LIKE CONCAT('%', TRIM(p_search), '%')
    ORDER BY c.category_name;
END $$

CREATE PROCEDURE sp_listar_categorias()
BEGIN
    CALL sp_buscar_categorias('');
END $$

CREATE PROCEDURE sp_registrar_categoria(
    IN p_category_name VARCHAR(100),
    IN p_description VARCHAR(255),
    IN p_image_path VARCHAR(255)
)
BEGIN
    IF p_category_name IS NULL OR TRIM(p_category_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre de la categoria es obligatorio.';
    END IF;

    INSERT INTO Categories (category_name, description, image_path)
    VALUES (TRIM(p_category_name), NULLIF(TRIM(p_description), ''), NULLIF(TRIM(p_image_path), ''));
END $$

CREATE PROCEDURE sp_modificar_categoria(
    IN p_id_category INT,
    IN p_category_name VARCHAR(100),
    IN p_description VARCHAR(255),
    IN p_image_path VARCHAR(255)
)
BEGIN
    IF p_id_category IS NULL OR p_id_category <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione una categoria valida.';
    END IF;

    IF p_category_name IS NULL OR TRIM(p_category_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre de la categoria es obligatorio.';
    END IF;

    UPDATE Categories
    SET category_name = TRIM(p_category_name),
        description = NULLIF(TRIM(p_description), ''),
        image_path = NULLIF(TRIM(p_image_path), '')
    WHERE id_category = p_id_category;
END $$

CREATE PROCEDURE sp_eliminar_categoria(IN p_id_category INT)
BEGIN
    DELETE FROM Categories
    WHERE id_category = p_id_category;
END $$

CREATE PROCEDURE sp_buscar_productos(IN p_search VARCHAR(160))
BEGIN
    SELECT
        p.id_product,
        p.id_category,
        p.sku,
        p.product_name,
        p.description,
        p.image_path,
        p.unit_weight_kg,
        p.unit_price,
        p.status,
        p.created_at,
        p.updated_at
    FROM Products p
    INNER JOIN Categories c ON c.id_category = p.id_category
    WHERE p_search IS NULL
       OR TRIM(p_search) = ''
       OR p.sku LIKE CONCAT('%', TRIM(p_search), '%')
       OR p.product_name LIKE CONCAT('%', TRIM(p_search), '%')
       OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
       OR c.category_name LIKE CONCAT('%', TRIM(p_search), '%')
       OR p.status LIKE CONCAT('%', TRIM(p_search), '%')
    ORDER BY p.product_name, p.sku;
END $$

CREATE PROCEDURE sp_listar_productos()
BEGIN
    CALL sp_buscar_productos('');
END $$

CREATE PROCEDURE sp_registrar_producto(
    IN p_id_category INT,
    IN p_sku VARCHAR(50),
    IN p_product_name VARCHAR(120),
    IN p_description VARCHAR(255),
    IN p_image_path VARCHAR(255),
    IN p_unit_weight_kg DECIMAL(10,2),
    IN p_unit_price DECIMAL(10,2)
)
BEGIN
    IF p_id_category IS NULL OR p_id_category <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione una categoria valida.';
    END IF;

    IF p_sku IS NULL OR TRIM(p_sku) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El SKU es obligatorio.';
    END IF;

    IF p_product_name IS NULL OR TRIM(p_product_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre del producto es obligatorio.';
    END IF;

    INSERT INTO Products (
        id_category,
        sku,
        product_name,
        description,
        image_path,
        unit_weight_kg,
        unit_price,
        status
    )
    VALUES (
        p_id_category,
        TRIM(p_sku),
        TRIM(p_product_name),
        NULLIF(TRIM(p_description), ''),
        NULLIF(TRIM(p_image_path), ''),
        IFNULL(p_unit_weight_kg, 0),
        IFNULL(p_unit_price, 0),
        'ACTIVE'
    );
END $$

CREATE PROCEDURE sp_modificar_producto(
    IN p_id_product INT,
    IN p_id_category INT,
    IN p_sku VARCHAR(50),
    IN p_product_name VARCHAR(120),
    IN p_description VARCHAR(255),
    IN p_image_path VARCHAR(255),
    IN p_unit_weight_kg DECIMAL(10,2),
    IN p_unit_price DECIMAL(10,2),
    IN p_status VARCHAR(20)
)
BEGIN
    IF p_id_product IS NULL OR p_id_product <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un producto valido.';
    END IF;

    IF p_id_category IS NULL OR p_id_category <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione una categoria valida.';
    END IF;

    IF p_sku IS NULL OR TRIM(p_sku) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El SKU es obligatorio.';
    END IF;

    IF p_product_name IS NULL OR TRIM(p_product_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre del producto es obligatorio.';
    END IF;

    UPDATE Products
    SET id_category = p_id_category,
        sku = TRIM(p_sku),
        product_name = TRIM(p_product_name),
        description = NULLIF(TRIM(p_description), ''),
        image_path = NULLIF(TRIM(p_image_path), ''),
        unit_weight_kg = IFNULL(p_unit_weight_kg, 0),
        unit_price = IFNULL(p_unit_price, 0),
        status = IF(UPPER(TRIM(p_status)) = 'INACTIVE', 'INACTIVE', 'ACTIVE')
    WHERE id_product = p_id_product;
END $$

CREATE PROCEDURE sp_eliminar_producto(IN p_id_product INT)
BEGIN
    UPDATE Products
    SET status = 'INACTIVE'
    WHERE id_product = p_id_product;
END $$

DELIMITER ;

-- =========================================================================
-- PROCEDIMIENTOS DE REFERENCIA PARA COMBOS DE LA APLICACION
-- SP Espinoza

DELIMITER $$

CREATE PROCEDURE sp_user_register(
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255),
    IN p_status VARCHAR(20)
)
BEGIN
    IF p_username IS NULL OR TRIM(p_username) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El username es obligatorio.';
    END IF;

    IF CHAR_LENGTH(TRIM(p_username)) < 4 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El username debe tener al menos 4 caracteres.';
    END IF;

    IF p_password_hash IS NULL OR TRIM(p_password_hash) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El password_hash es obligatorio.';
    END IF;

    IF EXISTS (SELECT 1 FROM Users WHERE username = TRIM(p_username)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre de usuario ya existe.';
    END IF;

    INSERT INTO Users (username, password_hash, status)
    VALUES (
        TRIM(p_username),
        TRIM(p_password_hash),
        IF(UPPER(TRIM(IFNULL(p_status, 'ACTIVE'))) = 'INACTIVE', 'INACTIVE', 'ACTIVE')
    );
END $$

CREATE PROCEDURE sp_user_login(
    IN p_username VARCHAR(50)
        CHARACTER SET utf8mb4
            COLLATE utf8mb4_unicode_ci
)
BEGIN
    SELECT
        id_user,
        username,
        password_hash,
        status,
        created_at,
        updated_at
    FROM Users
    WHERE username = TRIM(p_username)
    LIMIT 1;
END $$
DELIMITER $$
CREATE PROCEDURE sp_user_search(
    IN p_query VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    IN p_status VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
)
BEGIN
    SELECT
        id_user,
        username,
        password_hash,
        status,
        created_at,
        updated_at
    FROM Users
    WHERE (
            p_query IS NULL
            OR TRIM(p_query) = ''
            OR (
                TRIM(p_query) REGEXP '^[0-9]+$'
                AND id_user = CAST(TRIM(p_query) AS UNSIGNED)
            )
            OR username LIKE CONCAT('%', TRIM(p_query), '%')
        )
      AND (
            p_status IS NULL
            OR TRIM(p_status) = ''
            OR UPPER(TRIM(p_status)) = 'ALL'
            OR status = UPPER(TRIM(p_status))
        )
    ORDER BY created_at DESC, id_user DESC;
END $$
DELIMITER ;
CREATE PROCEDURE sp_user_find_by_id(
    IN p_id_user INT
)
BEGIN
    SELECT
        id_user,
        username,
        password_hash,
        status,
        created_at,
        updated_at
    FROM Users
    WHERE id_user = p_id_user
    LIMIT 1;
END $$

CREATE PROCEDURE sp_user_update(
    IN p_id_user INT,
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255),
    IN p_status VARCHAR(20)
)
BEGIN
    IF p_id_user IS NULL OR p_id_user <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un usuario valido.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Users WHERE id_user = p_id_user) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario no existe.';
    END IF;

    IF p_username IS NULL OR TRIM(p_username) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El username es obligatorio.';
    END IF;

    IF CHAR_LENGTH(TRIM(p_username)) < 4 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El username debe tener al menos 4 caracteres.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM Users
        WHERE username = TRIM(p_username)
          AND id_user <> p_id_user
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre de usuario ya existe.';
    END IF;

    UPDATE Users
    SET username = TRIM(p_username),
        password_hash = IF(
            p_password_hash IS NULL OR TRIM(p_password_hash) = '',
            password_hash,
            TRIM(p_password_hash)
        ),
        status = IF(UPPER(TRIM(IFNULL(p_status, 'ACTIVE'))) = 'INACTIVE', 'INACTIVE', 'ACTIVE')
    WHERE id_user = p_id_user;
END $$

CREATE PROCEDURE sp_user_delete(
    IN p_id_user INT
)
BEGIN
    IF p_id_user IS NULL OR p_id_user <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seleccione un usuario valido.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Users WHERE id_user = p_id_user) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario no existe.';
    END IF;

    UPDATE Users
    SET status = 'INACTIVE'
    WHERE id_user = p_id_user;
END $$

DELIMITER ;

-- SP Logout

DELIMITER $$
CREATE PROCEDURE sp_user_logout(
    IN p_id_user INT
)
BEGIN
    IF p_id_user IS NULL OR p_id_user <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario para cerrar sesion no es valido.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM Users
        WHERE id_user = p_id_user
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario indicado no existe.';
    END IF;

    UPDATE Users
    SET updated_at = CURRENT_TIMESTAMP
    WHERE id_user = p_id_user;
END $$
DELIMITER ;

-- MEJORA

DELIMITER $$
-- =========================================================================
-- 5. BUSCAR ALMACENES CON RESUMEN DE PRODUCTOS Y STOCK
-- =========================================================================
CREATE PROCEDURE sp_warehouse_search_with_summary(
    IN p_query VARCHAR(100),
    IN p_status ENUM('ACTIVE', 'INACTIVE')
)
BEGIN
    SELECT
        w.id_warehouse,
        w.warehouse_name,
        w.address,
        w.city,
        w.country,
        w.phone,
        w.status,
        w.created_at,
        w.updated_at,
        COUNT(DISTINCT i.id_product) AS product_count,
        COALESCE(SUM(i.stock), 0) AS total_stock,
        COALESCE(SUM(i.reserved_stock), 0) AS reserved_stock,
        COALESCE(SUM(i.stock - i.reserved_stock), 0) AS available_stock
    FROM Warehouses w
    LEFT JOIN Inventory i ON i.id_warehouse = w.id_warehouse
    WHERE w.status = p_status
      AND (
            p_query IS NULL
            OR TRIM(p_query) = ''
            OR (p_query REGEXP '^[0-9]+$' AND w.id_warehouse = CAST(p_query AS SIGNED))
            OR w.warehouse_name LIKE CONCAT('%', TRIM(p_query), '%')
            OR w.city LIKE CONCAT('%', TRIM(p_query), '%')
            OR w.country LIKE CONCAT('%', TRIM(p_query), '%')
          )
    GROUP BY
        w.id_warehouse,
        w.warehouse_name,
        w.address,
        w.city,
        w.country,
        w.phone,
        w.status,
        w.created_at,
        w.updated_at
    ORDER BY w.warehouse_name, w.city;
END $$

-- =========================================================================
-- 6. DETALLE DE INVENTARIO POR ALMACEN
-- =========================================================================
CREATE PROCEDURE sp_warehouse_inventory_detail(
    IN p_id_warehouse INT
)
BEGIN
    IF p_id_warehouse IS NULL OR p_id_warehouse <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El almacen seleccionado no es valido.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM Warehouses
        WHERE id_warehouse = p_id_warehouse
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El almacen seleccionado no existe.';
    END IF;

    SELECT
        i.id_inventory,
        i.id_warehouse,
        i.id_product,
        i.stock,
        i.reserved_stock,
        i.min_stock,
        w.warehouse_name,
        p.sku,
        p.product_name
    FROM Inventory i
    INNER JOIN Warehouses w ON w.id_warehouse = i.id_warehouse
    INNER JOIN Products p ON p.id_product = i.id_product
    WHERE i.id_warehouse = p_id_warehouse
    ORDER BY p.product_name, p.sku;
END $$

DELIMITER ;
