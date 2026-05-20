CREATE TABLE Users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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




-- TABLA ALMACENES 

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

USE logistics;

DELIMITER $$

-- =========================================================================
-- 1. REGISTRAR ALMACÉN
-- =========================================================================
DROP PROCEDURE IF EXISTS sp_warehouse_save;
$$
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
DROP PROCEDURE IF EXISTS sp_warehouse_update;
$$
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
DROP PROCEDURE IF EXISTS sp_warehouse_change_status;
$$
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
DROP PROCEDURE IF EXISTS sp_warehouse_delete_permanent;
$$
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
DROP PROCEDURE IF EXISTS sp_warehouse_search;
$$
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

DELIMITER ;


-- Inventario

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



USE logistics;

DELIMITER $$

-- =========================================================================
-- 1. REGISTRAR O ASIGNAR STOCK INICIAL (CON VALIDACIONES)
-- =========================================================================
DROP PROCEDURE IF EXISTS sp_inventory_save;
$$
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
DROP PROCEDURE IF EXISTS sp_inventory_update;
$$
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
DROP PROCEDURE IF EXISTS sp_inventory_search;
$$
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
DROP PROCEDURE IF EXISTS sp_inventory_transfer;
$$
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
