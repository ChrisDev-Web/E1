package Controllers;

import Config.PasswordUtil;
import Models.User;
import Repositories.IUserRepository;
import Repositories.UserRepository;
import java.sql.SQLException;
import java.util.Arrays;

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
        validateRegister(userName, password, confirmPassword);

        try {
            // PBKDF2: convierte la contrasena plana a un hash seguro antes de guardar.
            String hashedPassword = PasswordUtil.hashPassword(password);
            // POO: construye el modelo que sera enviado al repositorio.
            User user = new User(userName.trim(), hashedPassword);
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
                throw new Exception("Usuario o contrasena incorrectos.");
            }

            // PBKDF2: compara la contrasena plana con el hash almacenado.
            boolean validPassword = PasswordUtil.verifyPassword(password, user.getPassword());

            if (!validPassword) {
                throw new Exception("Usuario o contrasena incorrectos.");
            }

            return user;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            // Seguridad: limpia la contrasena usada en el login.
            clearPassword(password);
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
            throw new Exception("Ingrese su contrasena.");
        }
    }

    // Validacion: controla reglas basicas del username.
    private void validateUserName(String userName) throws Exception {
        if (userName == null || userName.trim().isEmpty()) {
            throw new Exception("Ingrese un nombre de usuario.");
        }

        if (userName.trim().length() < 4) {
            throw new Exception("El nombre de usuario debe tener minimo 4 caracteres.");
        }
    }

    // Validacion: comprueba longitud y confirmacion de contrasena.
    private void validatePasswordPair(char[] password, char[] confirmPassword) throws Exception {
        if (password == null || password.length == 0) {
            throw new Exception("Ingrese una contrasena.");
        }

        if (password.length < 6) {
            throw new Exception("La contrasena debe tener minimo 6 caracteres.");
        }

        if (confirmPassword == null || confirmPassword.length == 0) {
            throw new Exception("Confirme la contrasena.");
        }

        if (!Arrays.equals(password, confirmPassword)) {
            throw new Exception("Las contrasenas no coinciden.");
        }
    }

    // Seguridad: sobrescribe el contenido del arreglo para no dejar la contrasena en memoria.
    private void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }

    // JDBC: devuelve un mensaje amigable a partir del error SQL original.
    private String getSqlMessage(SQLException e) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            return e.getMessage();
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
