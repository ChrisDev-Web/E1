package App;

import Views.LoginJFrame;
import javax.swing.SwingUtilities;

// POO: clase principal que actua como punto de entrada de la aplicacion.
public class Main {

    // Swing: inicia la interfaz en el hilo seguro de eventos.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Swing + MVC: abre la vista de inicio de sesion al arrancar el sistema.
            LoginJFrame menuJFrame = new LoginJFrame();
            menuJFrame.setVisible(true);
        });
    }
}
