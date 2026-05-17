# Documentatio

## 1. Descripcion general del proyecto

Este proyecto es una aplicacion de escritorio construida con **Java Swing** que implementa un flujo basico de:

- registro de usuario
- inicio de sesion
- navegacion a un menu principal despues del login

Actualmente el sistema trabaja con la tabla `Users` y con los procedimientos almacenados:

- `sp_user_register`
- `sp_user_login`

La informacion principal del usuario se basa en estos campos:

- `id_user`
- `username`
- `password_hash`
- `created_at`
- `updated_at`

## 2. Que realiza actualmente el sistema

### 2.1 Registro

La vista de registro permite:

- ingresar `username`
- ingresar `password`
- confirmar `password`
- registrar el usuario en la base de datos

Cuando el registro es correcto:

- se muestra un mensaje de exito
- el sistema regresa a la vista de login

### 2.2 Login

La vista de login permite:

- ingresar `username`
- ingresar `password`
- validar credenciales contra la base de datos

Cuando el login es correcto:

- se obtiene el usuario desde la base de datos
- se valida el `password_hash` con `PasswordUtil`
- se abre `MenuJFrame`

### 2.3 Menu principal

Despues del login:

- se muestra `MenuJFrame`
- se presenta un saludo con el `username`

## 3. Validaciones que realiza el sistema

Las validaciones se encuentran principalmente en `UserController`.

### 3.1 Validaciones de username

- no puede ser `null`
- no puede estar vacio
- debe tener minimo 4 caracteres

### 3.2 Validaciones de password en registro

- no puede ser `null`
- no puede estar vacio
- debe tener minimo 6 caracteres
- la confirmacion es obligatoria
- ambas contrasenas deben coincidir

### 3.3 Validaciones de password en login

- no puede ser `null`
- no puede estar vacio

### 3.4 Validaciones de autenticacion

En el login el sistema:

- busca el usuario por `username`
- recupera el `password_hash`
- compara la contrasena escrita con el hash usando `PBKDF2`
- si no coincide, lanza error de credenciales

### 3.5 Validaciones en base de datos

La base de datos tambien protege la integridad con:

- `username` unico
- `password_hash` obligatorio

Y el SP de registro puede lanzar errores si:

- el `username` esta vacio
- el `password_hash` esta vacio
- el `username` ya existe

## 4. Tecnologias usadas, como y donde

### 4.1 Java

Se usa como lenguaje principal de todo el proyecto.

Se encuentra en:

- `src/main/java`

### 4.2 Java Swing

Se usa para construir la interfaz grafica de escritorio.

Se aplica en:

- `Views/LoginJFrame.java`
- `Views/RegisterJFrame.java`
- `Views/MenuJFrame.java`
- `Views/BaseFrame.java`

Se usa para:

- ventanas `JFrame`
- paneles `JPanel`
- etiquetas `JLabel`
- botones `JButton`
- campos `JTextField`
- campos `JPasswordField`
- cuadros de mensaje `JOptionPane`

### 4.3 POO

Se aplica mediante:

- clases
- encapsulamiento con atributos privados
- getters y setters
- constructores
- separacion por responsabilidades

Ejemplos:

- `Models/User.java`
- `Controllers/UserController.java`
- `Repositories/UserRepository.java`

### 4.4 MVC

El proyecto sigue una organizacion cercana a **MVC**:

- `Models` representa los datos
- `Views` representa la interfaz
- `Controllers` gestiona la logica entre vista y persistencia

Se refleja en:

- `Models/User.java`
- `Views/LoginJFrame.java`
- `Views/RegisterJFrame.java`
- `Views/MenuJFrame.java`
- `Controllers/UserController.java`

### 4.5 DAO

Se usa el patron **DAO** para encapsular el acceso a base de datos.

Se encuentra en:

- `DAO/UserDAO.java`

Su responsabilidad es:

- ejecutar SP
- abrir consultas
- mapear `ResultSet` a `User`

### 4.6 Repository

Se usa una capa **Repository** sin eliminar el DAO original.

Se encuentra en:

- `Repositories/IRepositoryRegister.java`
- `Repositories/IRepositoryLogin.java`
- `Repositories/IUserRepository.java`
- `Repositories/UserRepository.java`

Su objetivo es:

- trabajar con contratos usando interfaces
- desacoplar el controlador del DAO concreto
- dejar una estructura mas academica y escalable

### 4.7 Interfaces

Se usan interfaces para definir contratos.

Se encuentran en:

- `Repositories/IRepositoryRegister.java`
- `Repositories/IRepositoryLogin.java`
- `Repositories/IUserRepository.java`
- `Views/IViewFrame.java`

Se usan para:

- definir operaciones de repositorio
- obligar a las vistas a implementar metodos comunes

### 4.8 Herencia

Se usa herencia en las vistas para compartir comportamiento.

Se aplica en:

- `Views/BaseFrame.java`
- `Views/LoginJFrame.java`
- `Views/RegisterJFrame.java`
- `Views/MenuJFrame.java`

`BaseFrame` hereda de `JFrame` y las vistas heredan de `BaseFrame`.

Esto permite reutilizar:

- configuracion comun de ventanas
- apertura de nuevas vistas
- cierre de la vista actual

### 4.9 JDBC

Se usa **JDBC** para la conexion con MySQL.

Se aplica en:

- `Config/Database.java`
- `DAO/UserDAO.java`

Se usa para:

- cargar el driver
- abrir conexiones
- ejecutar `CallableStatement`
- leer resultados con `ResultSet`

### 4.10 MySQL y procedimientos almacenados

La persistencia actual esta pensada para MySQL.

Se usan:

- tabla `Users`
- procedimiento `sp_user_register`
- procedimiento `sp_user_login`

### 4.11 PBKDF2

Se usa **PBKDF2WithHmacSHA256** para proteger contrasenas.

Se encuentra en:

- `Config/PasswordUtil.java`

Se usa para:

- generar `password_hash`
- verificar contrasenas en login
- usar `salt`
- usar multiples iteraciones

### 4.12 Librerias externas activas

Dependencias activamente usadas por el codigo actual:

- `mysql-connector-j`
- `jiconfont-swing`
- `jiconfont-font_awesome`

### 4.13 Librerias configuradas pero no usadas aun en el flujo actual

Existen en `pom.xml`, pero no aparecen implementadas en las clases actuales del flujo de usuarios:

- `LGoodDatePicker`
- `ZXing core`

## 5. Flujo actual del sistema

### 5.1 Flujo de inicio

1. `Main` inicia la aplicacion.
2. Swing abre `LoginJFrame`.

### 5.2 Flujo de registro

1. El usuario abre `RegisterJFrame`.
2. Escribe `username`, `password` y confirmacion.
3. `RegisterJFrame` llama a `UserController.registerUser`.
4. `UserController` valida.
5. `PasswordUtil` genera el hash.
6. `UserRepository` delega en `UserDAO`.
7. `UserDAO` ejecuta `sp_user_register`.
8. Si todo sale bien, vuelve a `LoginJFrame`.

### 5.3 Flujo de login

1. El usuario escribe `username` y `password`.
2. `LoginJFrame` llama a `UserController.loginUser`.
3. `UserController` valida los datos.
4. `UserRepository` delega en `UserDAO`.
5. `UserDAO` ejecuta `sp_user_login`.
6. Se recupera el `password_hash`.
7. `PasswordUtil.verifyPassword` compara la contrasena con el hash.
8. Si coincide, se abre `MenuJFrame`.

## 6. Estructura actual del proyecto

```text
📁 E1
├── 📄 Documentatio.md
├── 📄 pom.xml
├── 📄 nb-configuration.xml
└── 📁 src
    └── 📁 main
        └── 📁 java
            ├── 📁 App
            │   └── 📄 Main.java
            ├── 📁 Config
            │   ├── 📄 Database.java
            │   └── 📄 PasswordUtil.java
            ├── 📁 Controllers
            │   └── 📄 UserController.java
            ├── 📁 DAO
            │   └── 📄 UserDAO.java
            ├── 📁 Models
            │   └── 📄 User.java
            ├── 📁 Repositories
            │   ├── 📄 IRepositoryLogin.java
            │   ├── 📄 IRepositoryRegister.java
            │   ├── 📄 IUserRepository.java
            │   └── 📄 UserRepository.java
            └── 📁 Views
                ├── 📄 BaseFrame.java
                ├── 📄 IViewFrame.java
                ├── 📄 LoginJFrame.java
                ├── 📄 MenuJFrame.java
                └── 📄 RegisterJFrame.java
```

## 7. Rol de cada paquete

### 📁 App

Contiene el arranque del sistema.

### 📁 Config

Contiene configuraciones tecnicas reutilizables:

- conexion a base de datos
- seguridad para contrasenas

### 📁 Models

Contiene las clases que representan entidades del sistema.

### 📁 DAO

Contiene el acceso directo a la base de datos.

### 📁 Repositories

Contiene contratos e implementaciones intermedias entre controlador y DAO.

### 📁 Controllers

Contiene la logica de validacion y coordinacion del flujo.

### 📁 Views

Contiene las pantallas de Swing y la base comun de herencia.

## 8. Archivo de base de datos esperado

El sistema actual esta alineado con una tabla como esta:

```sql
CREATE TABLE Users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

Y con estos procedimientos:

```sql
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
```

## 9. Estado actual del proyecto

Actualmente el proyecto:

- ya registra usuarios
- ya realiza login
- ya abre `MenuJFrame` al autenticarse
- ya usa hash seguro para contrasenas
- ya aplica `DAO`, `Repository`, `Interfaces` y `Herencia`
- ya tiene comentarios tecnicos en el codigo

## 10. Posibles mejoras futuras

El proyecto podria crecer facilmente con:

- CRUD completo de usuarios
- sesion activa de usuario
- logout
- mantenimiento de perfiles
- modulos extra como productos, ventas, clientes o reportes
- reutilizacion del mismo patron `Model -> DAO -> Repository -> Controller -> View`

