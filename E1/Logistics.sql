-- Tabla de Usuarios
CREATE TABLE Users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
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

-- SP Espinoza

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

CREATE PROCEDURE sp_warehouse_list_for_combo()
BEGIN
    SELECT
        w.id_warehouse AS id,
        CONCAT('Almacen ', w.id_warehouse) AS label
    FROM Warehouses w
    ORDER BY label;
END $$

CREATE PROCEDURE sp_user_list_for_combo()
BEGIN
    SELECT
        u.id_user AS id,
        u.username AS label
    FROM Users u
    ORDER BY u.username;
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
    IF NOT EXISTS (SELECT 1 FROM Shipments WHERE id_shipment = p_id_shipment) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
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
    IF NOT EXISTS (SELECT 1 FROM Shipments WHERE id_shipment = p_id_shipment) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El envio indicado no existe.';
    END IF;

    UPDATE Shipments
    SET status = p_status
    WHERE id_shipment = p_id_shipment;

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

    UPDATE Shipments
    SET
        status = 'DELIVERED',
        delivered_at = NOW()
    WHERE id_shipment = p_id_shipment;

    CALL sp_shipment_tracking_register(
        p_id_shipment,
        'DELIVERED',
        p_changed_by_user_id,
        COALESCE(NULLIF(TRIM(p_location), ''), 'Sistema'),
        COALESCE(NULLIF(TRIM(p_comments), ''), 'Envio marcado como entregado.')
    );
END $$

CREATE PROCEDURE sp_shipment_cancel(
    IN p_id_shipment INT,
    IN p_changed_by_user_id INT,
    IN p_location VARCHAR(120),
    IN p_comments VARCHAR(255)
)
BEGIN
    IF EXISTS (
        SELECT 1
        FROM Shipments
        WHERE id_shipment = p_id_shipment
          AND status = 'DELIVERED'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede cancelar un envio ya entregado.';
    END IF;

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
    INSERT INTO boxes (
        id_shipment, box_code, image_path,
        length_cm, width_cm, height_cm,
        weight_kg, declared_value, status
    ) VALUES (
        p_id_shipment, p_box_code, NULLIF(p_image_path, ''),
        p_length_cm, p_width_cm, p_height_cm,
        p_weight_kg, p_declared_value, p_status
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
    UPDATE boxes
    SET
        id_shipment = p_id_shipment,
        box_code = p_box_code,
        image_path = NULLIF(p_image_path, ''),
        length_cm = p_length_cm,
        width_cm = p_width_cm,
        height_cm = p_height_cm,
        weight_kg = p_weight_kg,
        declared_value = p_declared_value,
        status = p_status
    WHERE id_box = p_id_box;
END$$

CREATE PROCEDURE sp_box_delete(IN p_id_box INT)
BEGIN
    DELETE FROM boxes
    WHERE id_box = p_id_box;
END$$

CREATE PROCEDURE sp_shipment_detail_create(
    IN p_id_shipment INT,
    IN p_id_box INT,
    IN p_id_product INT,
    IN p_quantity INT,
    IN p_unit_weight_kg DECIMAL(10,2)
)
BEGIN
    INSERT INTO shipmentdetails (
        id_shipment, id_box, id_product, quantity, unit_weight_kg
    ) VALUES (
        p_id_shipment, p_id_box, p_id_product, p_quantity, p_unit_weight_kg
    );
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
    UPDATE shipmentdetails
    SET
        id_shipment = p_id_shipment,
        id_box = p_id_box,
        id_product = p_id_product,
        quantity = p_quantity,
        unit_weight_kg = p_unit_weight_kg
    WHERE id_shipment_detail = p_id_shipment_detail;
END$$

CREATE PROCEDURE sp_shipment_detail_delete(IN p_id_shipment_detail INT)
BEGIN
    DELETE FROM shipmentdetails
    WHERE id_shipment_detail = p_id_shipment_detail;
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
