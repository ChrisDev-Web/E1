package Views;

import Models.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// Swing + Herencia: vista principal que se abre luego del login.
public class MenuJFrame extends BaseFrame {

    // POO: modelo recibido desde la autenticacion para personalizar la vista.
    private final User user;

    // POO: constructor principal que recibe el usuario autenticado.
    public MenuJFrame(User user) {
        this.user = user;
        initUI();
        configureWindow();
    }

    // Swing + Interfaces: construye los componentes de la ventana principal.
    @Override
    public void initUI() {
        // Swing: panel principal del menu.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        mainPanel.setPreferredSize(new Dimension(720, 420));

        JLabel lblTitle = new JLabel("Bienvenido, " + resolveDisplayName(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Inicio de sesion correcto", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        mainPanel.add(lblTitle, BorderLayout.CENTER);
        mainPanel.add(lblSubtitle, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Herencia + Swing: configura propiedades comunes de la ventana.
    @Override
    public void configureWindow() {
        configureDefaultWindow("Menu Principal", false);
    }

    // POO: obtiene el nombre a mostrar en el saludo de bienvenida.
    private String resolveDisplayName() {
        if (user == null) {
            return "Usuario";
        }

        return user.getDisplayName();
    }
}
