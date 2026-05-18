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