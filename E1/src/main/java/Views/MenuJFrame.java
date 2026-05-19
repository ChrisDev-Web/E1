package Views;

import Models.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

// Swing + Herencia: vista principal que se abre luego del login.
public class MenuJFrame extends BaseFrame {

    private static final DateTimeFormatter CLOCK_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // POO: modelo recibido desde la autenticacion para personalizar la vista.
    private final User user;
    // Singleton + Swing: administra la navegacion interna por paneles.
    private final MenuNavigator navigator;

    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();

    private JLabel lblWelcome;
    private JLabel lblClock;
    private JLabel lblCurrentSection;
    private Timer clockTimer;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color headerColor = new Color(27, 94, 157);
    private final Color headerTextColor = Color.WHITE;
    private final Color navButtonColor = new Color(227, 238, 247);
    private final Color navButtonTextColor = new Color(27, 94, 157);
    private final Color navButtonActiveColor = new Color(21, 101, 192);
    private final Color navButtonActiveTextColor = Color.WHITE;

    // POO: constructor principal que recibe el usuario autenticado.
    public MenuJFrame(User user) {
        this.user = user;
        this.navigator = MenuNavigator.getInstance();
        UserSession.getInstance().startSession(user);
        initUI();
        configureWindow();
    }

    // Swing + Interfaces: construye los componentes de la ventana principal.
    @Override
    public void initUI() {
        navigator.reset();
        registerViews();

        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        mainPanel.setPreferredSize(new Dimension(1280, 760));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        setContentPane(mainPanel);
        showSection("usuarios");
    }

    // Herencia + Swing: configura propiedades comunes de la ventana.
    @Override
    public void configureWindow() {
        startClock();
        configureAdaptiveWindow("Menu Principal", new Dimension(1100, 700), true);
    }

    // Swing: crea el encabezado principal con bienvenida, hora y navegacion.
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 14));
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel topBar = new JPanel(new BorderLayout(20, 0));
        topBar.setOpaque(false);

        JPanel brandPanel = new JPanel(new BorderLayout(0, 4));
        brandPanel.setOpaque(false);

        JLabel lblBrand = new JLabel("Logistica");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBrand.setForeground(headerTextColor);

        JLabel lblSubtitle = new JLabel("Menu principal del sistema");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(225, 235, 245));

        brandPanel.add(lblBrand, BorderLayout.NORTH);
        brandPanel.add(lblSubtitle, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout(0, 4));
        infoPanel.setOpaque(false);

        lblWelcome = new JLabel("Bienvenido, " + resolveDisplayName(), SwingConstants.RIGHT);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(headerTextColor);

        lblCurrentSection = new JLabel("Seccion: Usuarios", SwingConstants.RIGHT);
        lblCurrentSection.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCurrentSection.setForeground(new Color(225, 235, 245));

        lblClock = new JLabel("", SwingConstants.RIGHT);
        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblClock.setForeground(new Color(225, 235, 245));

        JPanel textInfoPanel = new JPanel(new BorderLayout(0, 2));
        textInfoPanel.setOpaque(false);
        textInfoPanel.add(lblWelcome, BorderLayout.NORTH);
        textInfoPanel.add(lblCurrentSection, BorderLayout.CENTER);
        textInfoPanel.add(lblClock, BorderLayout.SOUTH);

        infoPanel.add(textInfoPanel, BorderLayout.EAST);

        topBar.add(brandPanel, BorderLayout.WEST);
        topBar.add(infoPanel, BorderLayout.EAST);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navigationPanel.setOpaque(false);

        for (IViewPanel view : navigator.getViews()) {
            JButton button = createNavigationButton(view);
            navigationButtons.put(view.getViewKey(), button);
            navigationPanel.add(button);
        }

        headerPanel.add(topBar, BorderLayout.NORTH);
        headerPanel.add(navigationPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    // Swing: crea el contenedor central donde se cargan los paneles de cada modulo.
    private JPanel createContentPanel() {
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);

        JPanel contentPanel = navigator.getContentPanel();
        contentPanel.setOpaque(false);

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        return contentWrapper;
    }

    // Swing: crea un boton de navegacion con icono y titulo.
    private JButton createNavigationButton(IViewPanel view) {
        JButton button = new JButton(view.getViewTitle(), view.getViewIcon());
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setBackground(navButtonColor);
        button.setForeground(navButtonTextColor);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(8);
        button.addActionListener(e -> showSection(view.getViewKey()));
        return button;
    }

    // Swing + Singleton: registra todas las vistas vacias que usara el menu.
    private void registerViews() {
        navigator.registerView(new UsuariosJPanel());
        navigator.registerView(new CategoriasJPanel());
        navigator.registerView(new ProductosJPanel());
        navigator.registerView(new AlmacenesJPanel());
        navigator.registerView(new InventarioJPanel());
        navigator.registerView(new ClientesJPanel());
        navigator.registerView(new EnviosJPanel());
        navigator.registerView(new CajasJPanel());
        navigator.registerView(new DetallesEnviosJPanel());
        navigator.registerView(new SeguimientoEnviosJPanel());
    }

    // Swing: cambia la seccion visible y actualiza el estado visual de la cabecera.
    private void showSection(String viewKey) {
        IViewPanel currentView = navigator.showView(viewKey);

        if (currentView == null) {
            return;
        }

        updateSelectedButton(viewKey);
        lblCurrentSection.setText("Seccion: " + currentView.getViewTitle());
        setTitle("Menu Principal - " + currentView.getViewTitle());
    }

    // Swing: resalta el boton activo de la navegacion.
    private void updateSelectedButton(String selectedKey) {
        for (Map.Entry<String, JButton> entry : navigationButtons.entrySet()) {
            JButton button = entry.getValue();
            boolean selected = entry.getKey().equals(selectedKey);

            button.setBackground(selected ? navButtonActiveColor : navButtonColor);
            button.setForeground(selected ? navButtonActiveTextColor : navButtonTextColor);
        }
    }

    // Swing: inicia el reloj visible en la esquina superior del menu.
    private void startClock() {
        updateClockLabel();

        clockTimer = new Timer(1000, e -> updateClockLabel());
        clockTimer.start();
    }

    // Swing: actualiza el texto visible del reloj.
    private void updateClockLabel() {
        lblClock.setText("Hora: " + LocalDateTime.now().format(CLOCK_FORMATTER));
    }

    // POO: obtiene el nombre a mostrar en el saludo de bienvenida.
    private String resolveDisplayName() {
        String displayName = UserSession.getInstance().getDisplayName();

        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }

        if (user == null) {
            return "Usuario";
        }

        return user.getDisplayName();
    }
}
