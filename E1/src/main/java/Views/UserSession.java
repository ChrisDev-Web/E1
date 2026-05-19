package Views;

import Models.User;

// Singleton + POO: conserva el usuario autenticado mientras la aplicacion esta abierta.
public final class UserSession {

    private static final UserSession INSTANCE = new UserSession();

    private User currentUser;

    private UserSession() {
    }

    // Singleton: devuelve la unica instancia compartida de la sesion.
    public static UserSession getInstance() {
        return INSTANCE;
    }

    // POO: inicia o reemplaza la sesion actual con el usuario autenticado.
    public void startSession(User user) {
        this.currentUser = user;
    }

    // POO: limpia el usuario en memoria cuando sea necesario.
    public void clearSession() {
        this.currentUser = null;
    }

    // POO: devuelve el usuario autenticado actual.
    public User getCurrentUser() {
        return currentUser;
    }

    // POO: devuelve un nombre amigable listo para la interfaz.
    public String getDisplayName() {
        if (currentUser == null) {
            return "Usuario";
        }

        return currentUser.getDisplayName();
    }
}
