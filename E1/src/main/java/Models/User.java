package Models;

import java.time.LocalDateTime;

// POO + Modelo: representa un usuario segun la tabla Users.
public class User {

    // Identificador primario del usuario.
    private int idUser;
    // Nombre unico usado para iniciar sesion.
    private String userName;
    // Hash de la contrasena almacenada en la base de datos.
    private String password;
    // Fecha de creacion registrada por la base de datos.
    private LocalDateTime createdAt;
    // Fecha de ultima actualizacion registrada por la base de datos.
    private LocalDateTime updatedAt;

    // POO: constructor vacio para mapeo y carga parcial de datos.
    public User() {
    }

    // POO: constructor completo para inicializar todas las propiedades del modelo.
    public User(int idUser, String userName, String password, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.idUser = idUser;
        this.userName = userName;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // POO: constructor util para operaciones de registro o autenticacion.
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Encapsulamiento POO: devuelve el id del usuario.
    public int getIdUser() {
        return idUser;
    }

    // Encapsulamiento POO: asigna el id del usuario.
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    // Encapsulamiento POO: devuelve el username del usuario.
    public String getUserName() {
        return userName;
    }

    // Encapsulamiento POO: asigna el username del usuario.
    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Encapsulamiento POO: devuelve el hash de contrasena del usuario.
    public String getPassword() {
        return password;
    }

    // Seguridad + Encapsulamiento POO: aqui se guarda el hash, no la contrasena plana.
    public void setPassword(String password) {
        this.password = password;
    }

    // Encapsulamiento POO: devuelve la fecha de creacion del usuario.
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Encapsulamiento POO: asigna la fecha de creacion del usuario.
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Encapsulamiento POO: devuelve la fecha de actualizacion del usuario.
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Encapsulamiento POO: asigna la fecha de actualizacion del usuario.
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // POO: devuelve un texto amigable para mostrar en la interfaz.
    public String getDisplayName() {
        if (userName != null && !userName.trim().isEmpty()) {
            return userName.trim();
        }

        return "Usuario";
    }

    // POO: muestra el username al imprimir el objeto.
    @Override
    public String toString() {
        return userName;
    }
}
