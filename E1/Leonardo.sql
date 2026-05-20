USE logistics;

DROP PROCEDURE IF EXISTS sp_box_create;
DROP PROCEDURE IF EXISTS sp_box_list;
DROP PROCEDURE IF EXISTS sp_box_find_by_id;
DROP PROCEDURE IF EXISTS sp_box_list_by_shipment;
DROP PROCEDURE IF EXISTS sp_box_update;
DROP PROCEDURE IF EXISTS sp_box_delete;

DROP PROCEDURE IF EXISTS sp_shipment_detail_create;
DROP PROCEDURE IF EXISTS sp_shipment_detail_list;
DROP PROCEDURE IF EXISTS sp_shipment_detail_find_by_id;
DROP PROCEDURE IF EXISTS sp_shipment_detail_list_by_shipment;
DROP PROCEDURE IF EXISTS sp_shipment_detail_list_by_box;
DROP PROCEDURE IF EXISTS sp_shipment_detail_update;
DROP PROCEDURE IF EXISTS sp_shipment_detail_delete;

DROP PROCEDURE IF EXISTS sp_shipment_tracking_create;
DROP PROCEDURE IF EXISTS sp_shipment_tracking_list;
DROP PROCEDURE IF EXISTS sp_shipment_tracking_find_by_id;
DROP PROCEDURE IF EXISTS sp_shipment_tracking_list_by_shipment;
DROP PROCEDURE IF EXISTS sp_shipment_tracking_update;
DROP PROCEDURE IF EXISTS sp_shipment_tracking_delete;

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