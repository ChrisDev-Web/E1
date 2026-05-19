package Views;

import Controllers.UserController;
import Models.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

// Swing + Herencia + MVC: vista de inicio de sesion.
public class LoginJFrame extends BaseFrame {

    // Swing: controles principales del formulario.
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JButton btnTogglePassword;
    private JButton btnLogin;
    private JButton btnRegister;

    // MVC: controlador que atiende la logica del login.
    private final UserController userController;
    // Swing: guarda el caracter original del password field.
    private char defaultEchoChar;
    // Swing: indica si la contrasena se muestra o se oculta.
    private boolean passwordVisible = false;

    // Swing: colores reutilizados en la interfaz.
    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color iconColor = new Color(90, 90, 90);
    private final Color borderColor = new Color(210, 210, 210);

    static {
        // Swing + libreria externa: registra FontAwesome para usar iconos.
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    // POO: constructor principal de la vista de login.
    public LoginJFrame() {
        userController = new UserController();
        initUI();
        configureWindow();
    }

    // Swing + Interfaces: construye todos los componentes visuales.
    @Override
    public void initUI() {
        // Swing: panel principal contenedor de toda la vista.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        // Swing: usa GridBagLayout para alinear los controles del formulario.
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);

        // Swing: restricciones base para organizar los componentes.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 0, 6, 0);

        JLabel lblTitle = new JLabel("Ventas y Facturacion");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Inicia sesion para continuar");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        JLabel lblUserName = new JLabel("Usuario");
        lblUserName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtUserName = new PlaceholderTextField("Nombre de Usuario");
        txtUserName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel userPanel = createTextFieldWithIcon(
                createIcon(FontAwesome.USER, 18),
                txtUserName,
                320
        );

        JLabel lblPassword = new JLabel("Contrasena");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtPassword = new PlaceholderPasswordField("Contrasena");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnTogglePassword = createEyeButton();

        JPanel passwordPanel = createPasswordFieldWithIcon(
                createIcon(FontAwesome.LOCK, 18),
                txtPassword,
                btnTogglePassword,
                320
        );

        btnLogin = new JButton("Ingresar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(320, 42));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBackground(new Color(30, 136, 229));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);

        btnRegister = new JButton("Crear cuenta");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnRegister.setPreferredSize(new Dimension(320, 38));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setFocusPainted(false);

        gbc.gridy = 0;
        formPanel.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        formPanel.add(lblSubtitle, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(6, 0, 6, 0);
        formPanel.add(lblUserName, gbc);

        gbc.gridy = 3;
        formPanel.add(userPanel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 6, 0);
        formPanel.add(lblPassword, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(6, 0, 6, 0);
        formPanel.add(passwordPanel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(22, 0, 6, 0);
        formPanel.add(btnLogin, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 6, 0);
        formPanel.add(btnRegister, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Swing: conecta eventos de botones con sus acciones.
        btnLogin.addActionListener(e -> loginAction());
        btnRegister.addActionListener(e -> openRegisterAction());
        btnTogglePassword.addActionListener(e -> togglePasswordVisibility());

        // Swing: activa el boton Ingresar al presionar Enter.
        getRootPane().setDefaultButton(btnLogin);
    }

    // Herencia + Swing: configura propiedades comunes de la ventana.
    @Override
    public void configureWindow() {
        defaultEchoChar = txtPassword.getEchoChar();
        configureDefaultWindow("Iniciar Sesion", false);
    }

    // Swing: fabrica visual para un campo de texto con icono.
    private JPanel createTextFieldWithIcon(Icon icon, JTextField textField, int width) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(width, 40));
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                )
        );

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setPreferredSize(new Dimension(24, 40));

        // Swing: limpia el borde interno para integrarlo al panel contenedor.
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setOpaque(false);

        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    // Swing: fabrica visual para un campo password con icono y boton ojo.
    private JPanel createPasswordFieldWithIcon(Icon icon, JPasswordField passwordField, JButton eyeButton, int width) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(width, 40));
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 5)
                )
        );

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setPreferredSize(new Dimension(24, 40));

        passwordField.setBorder(BorderFactory.createEmptyBorder());
        passwordField.setOpaque(false);

        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(eyeButton, BorderLayout.EAST);

        return panel;
    }

    // Swing + UX: crea el boton para mostrar u ocultar la contrasena.
    private JButton createEyeButton() {
        JButton button = new JButton();
        button.setIcon(createIcon(FontAwesome.EYE, 18));
        button.setToolTipText("Mostrar contrasena");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(32, 32));
        return button;
    }

    // Swing + libreria externa: construye un icono de FontAwesome.
    private Icon createIcon(FontAwesome icon, int size) {
        return IconFontSwing.buildIcon(icon, size, iconColor);
    }

    // Swing: alterna visualmente el contenido del campo de contrasena.
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            txtPassword.setEchoChar((char) 0);
            btnTogglePassword.setIcon(createIcon(FontAwesome.EYE_SLASH, 18));
            btnTogglePassword.setToolTipText("Ocultar contrasena");
        } else {
            txtPassword.setEchoChar(defaultEchoChar);
            btnTogglePassword.setIcon(createIcon(FontAwesome.EYE, 18));
            btnTogglePassword.setToolTipText("Mostrar contrasena");
        }
    }

    // MVC: recoge los datos del formulario y delega el login al controlador.
    private void loginAction() {
        String userName = txtUserName.getText().trim();
        char[] password = txtPassword.getPassword();

        // Swing: evita varios clics mientras se procesa el inicio de sesion.
        btnLogin.setEnabled(false);

        try {
            // MVC: recibe el usuario autenticado desde la capa controlador.
            User user = userController.loginUser(userName, password);
            UserSession.getInstance().startSession(user);
            // Herencia + Swing: navega a la siguiente vista al iniciar sesion.
            openFrame(new MenuJFrame(user));
        } catch (Exception e) {
            // Swing: muestra un mensaje mucho mas claro y visible al usuario.
            AppMessageDialog.showError(
                    this,
                    "No se pudo iniciar sesion",
                    e.getMessage()
            );
        } finally {
            btnLogin.setEnabled(true);
        }
    }

    // Swing + Herencia: abre la vista de registro.
    private void openRegisterAction() {
        openFrame(new RegisterJFrame());
    }

    // Swing + POO: campo de texto personalizado con placeholder.
    private static class PlaceholderTextField extends JTextField {

        private final String placeholder;
        private final Color placeholderColor = new Color(150, 150, 150);

        // POO: constructor que recibe el texto placeholder.
        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        // Swing: pinta el placeholder cuando el campo esta vacio.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty()) {
                paintPlaceholder(g);
            }
        }

        // Swing 2D: dibuja el placeholder manualmente.
        private void paintPlaceholder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            g2.setColor(placeholderColor);
            g2.setFont(getFont());

            Insets insets = getInsets();
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2
                    + g2.getFontMetrics().getAscent();

            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }

    // Swing + POO: campo password personalizado con placeholder.
    private static class PlaceholderPasswordField extends JPasswordField {

        private final String placeholder;
        private final Color placeholderColor = new Color(150, 150, 150);

        // POO: constructor que recibe el texto placeholder.
        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
        }

        // Swing: pinta el placeholder cuando no hay contrasena escrita.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getPassword().length == 0) {
                paintPlaceholder(g);
            }
        }

        // Swing 2D: dibuja el placeholder manualmente.
        private void paintPlaceholder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            g2.setColor(placeholderColor);
            g2.setFont(getFont());

            Insets insets = getInsets();
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2
                    + g2.getFontMetrics().getAscent();

            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }
}
