-- =========================================================
-- NEIL.SQL
-- SQL base para los modulos Clients y Shipments
-- Incluye:
-- - tabla ShipmentTracking
-- - trigger para generar tracking_code
-- - procedimientos almacenados para Clients
-- - procedimientos almacenados para Shipments
-- =========================================================

CREATE TABLE IF NOT EXISTS ShipmentTracking (
    id_tracking INT AUTO_INCREMENT PRIMARY KEY,
    id_shipment INT NOT NULL,
    id_user INT NULL,
    tracking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location VARCHAR(120) NOT NULL,
    status ENUM('PENDING','PREPARING','SHIPPED','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL,
    comments VARCHAR(255),
    CONSTRAINT fk_shipment_tracking_shipment
        FOREIGN KEY (id_shipment) REFERENCES Shipments(id_shipment)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_shipment_tracking_user
        FOREIGN KEY (id_user) REFERENCES Users(id_user)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

DELIMITER $$

DROP TRIGGER IF EXISTS trg_shipments_generate_tracking_code $$
CREATE TRIGGER trg_shipments_generate_tracking_code
BEFORE INSERT ON Shipments
FOR EACH ROW
BEGIN
    IF NEW.tracking_code IS NULL OR TRIM(NEW.tracking_code) = '' THEN
        SET NEW.tracking_code = CONCAT('TRK-', UUID_SHORT());
    END IF;
END $$

DROP PROCEDURE IF EXISTS sp_document_type_list $$
CREATE PROCEDURE sp_document_type_list()
BEGIN
    SELECT
        dt.id_document_type AS id,
        dt.name AS label
    FROM DocumentTypes dt
    ORDER BY dt.name;
END $$

DROP PROCEDURE IF EXISTS sp_client_list_for_combo $$
CREATE PROCEDURE sp_client_list_for_combo()
BEGIN
    SELECT
        c.id_client AS id,
        CONCAT(c.name, ' ', c.last_name_paternal, ' ', c.last_name_maternal) AS label
    FROM Clients c
    WHERE c.deleted_at IS NULL
    ORDER BY c.name, c.last_name_paternal, c.last_name_maternal;
END $$

DROP PROCEDURE IF EXISTS sp_warehouse_list_for_combo $$
CREATE PROCEDURE sp_warehouse_list_for_combo()
BEGIN
    SELECT
        w.id_warehouse AS id,
        CONCAT('Almacen ', w.id_warehouse) AS label
    FROM Warehouses w
    ORDER BY label;
END $$

DROP PROCEDURE IF EXISTS sp_user_list_for_combo $$
CREATE PROCEDURE sp_user_list_for_combo()
BEGIN
    SELECT
        u.id_user AS id,
        u.username AS label
    FROM Users u
    ORDER BY u.username;
END $$

DROP PROCEDURE IF EXISTS sp_client_create $$
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

DROP PROCEDURE IF EXISTS sp_client_update $$
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

DROP PROCEDURE IF EXISTS sp_client_soft_delete $$
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

DROP PROCEDURE IF EXISTS sp_client_delete $$
CREATE PROCEDURE sp_client_delete(IN p_id_client INT)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Clients WHERE id_client = p_id_client) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cliente indicado no existe.';
    END IF;

    DELETE FROM Clients
    WHERE id_client = p_id_client;
END $$

DROP PROCEDURE IF EXISTS sp_client_restore $$
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

DROP PROCEDURE IF EXISTS sp_client_get_by_id $$
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

DROP PROCEDURE IF EXISTS sp_client_search_active $$
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

DROP PROCEDURE IF EXISTS sp_client_search_inactive $$
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

DROP PROCEDURE IF EXISTS sp_shipment_tracking_register $$
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

DROP PROCEDURE IF EXISTS sp_shipment_create $$
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

DROP PROCEDURE IF EXISTS sp_shipment_update $$
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

DROP PROCEDURE IF EXISTS sp_shipment_update_status $$
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

DROP PROCEDURE IF EXISTS sp_shipment_mark_delivered $$
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

DROP PROCEDURE IF EXISTS sp_shipment_cancel $$
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

DROP PROCEDURE IF EXISTS sp_shipment_get_by_id $$
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

DROP PROCEDURE IF EXISTS sp_shipment_get_by_tracking $$
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

DROP PROCEDURE IF EXISTS sp_shipment_search $$
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

DROP PROCEDURE IF EXISTS sp_shipment_get_detail $$
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
