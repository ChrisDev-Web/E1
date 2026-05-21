package Controllers;

import Config.PasswordUtil;
import Models.User;
import Repositories.IUserRepository;
import Repositories.UserRepository;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

// MVC + POO: controlador que coordina validaciones, repositorio y vistas.
public class UserController {

    // Interfaces + Repository: el controlador depende del contrato, no del DAO directo.
    private final IUserRepository userRepository;

    // POO: constructor por defecto que usa la implementacion concreta del repositorio.
    public UserController() {
        this.userRepository = new UserRepository();
    }

    // POO: constructor alterno para inyeccion de dependencias.
    public UserController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // MVC + Seguridad: valida, genera el hash y registra el usuario.
    public void registerUser(String userName, char[] password, char[] confirmPassword) throws Exception {
        createUser(userName, password, confirmPassword, "ACTIVE");
    }

    public void createUser(String userName, char[] password, char[] confirmPassword, String status) throws Exception {
        try {
            validateRegister(userName, password, confirmPassword);
            validateStatus(status);
            // PBKDF2: convierte la contrasena plana a un hash seguro antes de guardar.
            String hashedPassword = PasswordUtil.hashPassword(password);
            // POO: construye el modelo que sera enviado al repositorio.
            User user = new User(userName.trim(), hashedPassword);
            user.setStatus(status.trim().toUpperCase());
            // Repository: delega la persistencia al repositorio.
            userRepository.register(user);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            // Seguridad: limpia las contrasenas de memoria.
            clearPassword(password);
            clearPassword(confirmPassword);
        }
    }

    // MVC + Seguridad: autentica el usuario validando username y password.
    public User loginUser(String userName, char[] password) throws Exception {
        validateLogin(userName, password);

        try {
            // Repository: recupera el usuario desde la capa de persistencia.
            User user = userRepository.login(userName.trim());

            if (user == null) {
                throw new Exception("No se pudo iniciar sesion. Verifique su usuario y contrasena.");
            }

            if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new Exception("El usuario esta inactivo. Solicite la reactivacion de la cuenta.");
            }

            // PBKDF2: compara la contrasena plana con el hash almacenado.
            boolean validPassword = PasswordUtil.verifyPassword(password, user.getPassword());

            if (!validPassword) {
                throw new Exception("No se pudo iniciar sesion. Verifique su usuario y contrasena.");
            }

            return user;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            // Seguridad: limpia la contrasena usada en el login.
            clearPassword(password);
        }
    }

    public List<User> searchUsers(String query, String status) throws Exception {
        try {
            return userRepository.search(query, normalizeStatusFilter(status));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public User findUserById(int idUser) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Seleccione un usuario valido.");
        }

        try {
            return userRepository.findById(idUser);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void updateUser(int idUser, String userName, char[] password, char[] confirmPassword, String status) throws Exception {
        try {
            if (idUser <= 0) {
                throw new Exception("Seleccione un usuario valido para actualizar.");
            }

            validateUserName(userName);
            validateStatus(status);

            String hashedPassword = null;

            if (hasPassword(password) || hasPassword(confirmPassword)) {
                validatePasswordPair(password, confirmPassword);
                hashedPassword = PasswordUtil.hashPassword(password);
            }

            User user = new User();
            user.setIdUser(idUser);
            user.setUserName(userName.trim());
            user.setPassword(hashedPassword);
            user.setStatus(status.trim().toUpperCase());
            userRepository.update(user);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            clearPassword(password);
            clearPassword(confirmPassword);
        }
    }

    public void deleteUser(int idUser) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Seleccione un usuario valido para eliminar.");
        }

        try {
            userRepository.deleteById(idUser);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    // Validacion: revisa los datos necesarios para registrar.
    private void validateRegister(String userName, char[] password, char[] confirmPassword) throws Exception {
        validateUserName(userName);
        validatePasswordPair(password, confirmPassword);
    }

    // Validacion: revisa los datos minimos para iniciar sesion.
    private void validateLogin(String userName, char[] password) throws Exception {
        validateUserName(userName);

        if (password == null || password.length == 0) {
            throw new Exception("Escriba su contrasena para continuar.");
        }
    }

    // Validacion: controla reglas basicas del username.
    private void validateUserName(String userName) throws Exception {
        if (userName == null || userName.trim().isEmpty()) {
            throw new Exception("Escriba su nombre de usuario para continuar.");
        }

        if (userName.trim().length() < 4) {
            throw new Exception("El nombre de usuario debe tener al menos 4 caracteres.");
        }
    }

    // Validacion: comprueba longitud y confirmacion de contrasena.
    private void validatePasswordPair(char[] password, char[] confirmPassword) throws Exception {
        if (password == null || password.length == 0) {
            throw new Exception("Escriba una contrasena.");
        }

        if (password.length < 6) {
            throw new Exception("La contrasena debe tener al menos 6 caracteres.");
        }

        if (confirmPassword == null || confirmPassword.length == 0) {
            throw new Exception("Confirme la contrasena para continuar.");
        }

        if (!Arrays.equals(password, confirmPassword)) {
            throw new Exception("Las contrasenas no coinciden. Revise ambos campos.");
        }
    }

    private void validateStatus(String status) throws Exception {
        if (status == null || status.trim().isEmpty()) {
            throw new Exception("Seleccione el estado del usuario.");
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!"ACTIVE".equals(normalizedStatus) && !"INACTIVE".equals(normalizedStatus)) {
            throw new Exception("El estado del usuario no es valido.");
        }
    }

    private String normalizeStatusFilter(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "ALL";
        }

        String normalizedStatus = status.trim().toUpperCase();
        return "ACTIVE".equals(normalizedStatus) || "INACTIVE".equals(normalizedStatus) ? normalizedStatus : "ALL";
    }

    private boolean hasPassword(char[] password) {
        if (password == null || password.length == 0) {
            return false;
        }

        for (char character : password) {
            if (character != '\0' && !Character.isWhitespace(character)) {
                return true;
            }
        }

        return false;
    }

    // Seguridad: sobrescribe el contenido del arreglo para no dejar la contrasena en memoria.
    private void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }

    // JDBC: devuelve un mensaje amigable a partir del error SQL original.
    private String getSqlMessage(SQLException e) {
        String detail = e.getMessage();

        if (detail != null) {
            String lowerDetail = detail.toLowerCase();

            if (lowerDetail.contains("duplicate") || lowerDetail.contains("duplicada")) {
                return "El usuario ya existe. Elija otro nombre de usuario.";
            }

            if (lowerDetail.contains("connect")
                    || lowerDetail.contains("communications link failure")
                    || lowerDetail.contains("connection refused")
                    || lowerDetail.contains("access denied")) {
                return "No se pudo conectar a la base de datos. Revise que MySQL este iniciado y que la configuracion sea correcta.";
            }

            if (!detail.isBlank()) {
                return detail;
            }
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
