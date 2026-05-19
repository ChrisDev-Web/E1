package Views;

import Controllers.UserController;
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

// Swing + Herencia + MVC: vista de registro de usuario.
public class RegisterJFrame extends BaseFrame {

    // Swing: controles principales del formulario.
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnTogglePassword;
    private JButton btnToggleConfirmPassword;
    private JButton btnRegister;
    private JButton btnBack;

    // MVC: controlador que atiende la logica del registro.
    private final UserController userController;

    // Swing: conserva el caracter de mascara original de los password fields.
    private char defaultPasswordEchoChar;
    private char defaultConfirmPasswordEchoChar;

    // Swing: controla el estado visible u oculto de las contrasenas.
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    // Swing: colores reutilizados en la interfaz.
    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color iconColor = new Color(90, 90, 90);
    private final Color borderColor = new Color(210, 210, 210);

    static {
        // Swing + libreria externa: registra FontAwesome para usar iconos.
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    // POO: constructor principal de la vista de registro.
    public RegisterJFrame() {
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

        JLabel lblTitle = new JLabel("Crear cuenta");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Registra un nuevo usuario del sistema");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        JLabel lblUserName = new JLabel("Usuario");
        lblUserName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtUserName = new PlaceholderTextField("Nombre de Usuario");
        txtUserName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel userPanel = createTextFieldWithIcon(
                createIcon(FontAwesome.USER, 18),
                txtUserName,
                340
        );

        JLabel lblPassword = new JLabel("Contrasena");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtPassword = new PlaceholderPasswordField("Contrasena");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnTogglePassword = createEyeButton("Mostrar contrasena");

        JPanel passwordPanel = createPasswordFieldWithIcon(
                createIcon(FontAwesome.LOCK, 18),
                txtPassword,
                btnTogglePassword,
                340
        );

        JLabel lblConfirmPassword = new JLabel("Confirmar contrasena");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtConfirmPassword = new PlaceholderPasswordField("Confirmar Contrasena");
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnToggleConfirmPassword = createEyeButton("Mostrar confirmacion");

        JPanel confirmPasswordPanel = createPasswordFieldWithIcon(
                createIcon(FontAwesome.LOCK, 18),
                txtConfirmPassword,
                btnToggleConfirmPassword,
                340
        );

        btnRegister = new JButton("Registrar");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setPreferredSize(new Dimension(340, 42));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setBackground(new Color(46, 125, 50));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setOpaque(true);

        btnBack = new JButton("Volver al login");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setPreferredSize(new Dimension(340, 38));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setFocusPainted(false);

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
        gbc.insets = new Insets(15, 0, 6, 0);
        formPanel.add(lblConfirmPassword, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(6, 0, 6, 0);
        formPanel.add(confirmPasswordPanel, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(22, 0, 6, 0);
        formPanel.add(btnRegister, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(8, 0, 6, 0);
        formPanel.add(btnBack, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Swing: conecta eventos de botones con sus acciones.
        btnRegister.addActionListener(e -> registerAction());
        btnBack.addActionListener(e -> backToLoginAction());
        btnTogglePassword.addActionListener(e -> togglePasswordVisibility());
        btnToggleConfirmPassword.addActionListener(e -> toggleConfirmPasswordVisibility());

        // Swing: activa el boton Registrar al presionar Enter.
        getRootPane().setDefaultButton(btnRegister);
    }

    // Herencia + Swing: configura propiedades comunes de la ventana.
    @Override
    public void configureWindow() {
        defaultPasswordEchoChar = txtPassword.getEchoChar();
        defaultConfirmPasswordEchoChar = txtConfirmPassword.getEchoChar();
        configureDefaultWindow("Registrar Usuario", false);
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
    private JButton createEyeButton(String tooltip) {
        JButton button = new JButton();
        button.setIcon(createIcon(FontAwesome.EYE, 18));
        button.setToolTipText(tooltip);
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

    // Swing: alterna visualmente la contrasena principal.
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            txtPassword.setEchoChar((char) 0);
            btnTogglePassword.setIcon(createIcon(FontAwesome.EYE_SLASH, 18));
            btnTogglePassword.setToolTipText("Ocultar contrasena");
        } else {
            txtPassword.setEchoChar(defaultPasswordEchoChar);
            btnTogglePassword.setIcon(createIcon(FontAwesome.EYE, 18));
            btnTogglePassword.setToolTipText("Mostrar contrasena");
        }
    }

    // Swing: alterna visualmente la contrasena de confirmacion.
    private void toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible;

        if (confirmPasswordVisible) {
            txtConfirmPassword.setEchoChar((char) 0);
            btnToggleConfirmPassword.setIcon(createIcon(FontAwesome.EYE_SLASH, 18));
            btnToggleConfirmPassword.setToolTipText("Ocultar confirmacion");
        } else {
            txtConfirmPassword.setEchoChar(defaultConfirmPasswordEchoChar);
            btnToggleConfirmPassword.setIcon(createIcon(FontAwesome.EYE, 18));
            btnToggleConfirmPassword.setToolTipText("Mostrar confirmacion");
        }
    }

    // MVC: recoge los datos del formulario y delega el registro al controlador.
    private void registerAction() {
        String userName = txtUserName.getText().trim();
        char[] password = txtPassword.getPassword();
        char[] confirmPassword = txtConfirmPassword.getPassword();

        // Swing: evita varios clics mientras se procesa el registro.
        btnRegister.setEnabled(false);

        try {
            // MVC: envia los datos al controlador para validar y registrar.
            userController.registerUser(userName, password, confirmPassword);

            // Swing: informa con mejor visibilidad cuando el registro fue exitoso.
            AppMessageDialog.showInfo(
                    this,
                    "Registro exitoso",
                    "El usuario fue registrado correctamente. Ya puede iniciar sesion."
            );

            // Herencia + Swing: vuelve al login despues del registro.
            openFrame(new LoginJFrame());
        } catch (Exception e) {
            // Swing: muestra un mensaje mucho mas claro y visible al usuario.
            AppMessageDialog.showError(
                    this,
                    "No se pudo completar el registro",
                    e.getMessage()
            );
        } finally {
            btnRegister.setEnabled(true);
        }
    }

    // Swing + Herencia: regresa a la vista de login.
    private void backToLoginAction() {
        openFrame(new LoginJFrame());
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
