package Views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// Herencia + Swing: base comun para todas las secciones mostradas dentro del menu principal.
public abstract class BasePanel extends JPanel implements IViewPanel {

    private final String viewKey;
    private final String viewTitle;
    private final String viewDescription;
    private final Icon viewIcon;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color titleColor = new Color(33, 33, 33);
    private final Color textColor = new Color(90, 90, 90);
    private final Color cardBorderColor = new Color(214, 223, 233);

    // POO: constructor base que inicializa la metadata comun de cada seccion.
    protected BasePanel(String viewKey, String viewTitle, String viewDescription, Icon viewIcon) {
        this.viewKey = viewKey;
        this.viewTitle = viewTitle;
        this.viewDescription = viewDescription;
        this.viewIcon = viewIcon;
        initPanel();
    }

    // Swing + Interfaces: construye una vista base reutilizable para secciones vacias.
    @Override
    public void initPanel() {
        removeAll();
        setLayout(new BorderLayout(0, 24));
        setOpaque(true);
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createPlaceholderPanel(), BorderLayout.CENTER);
    }

    // Interfaces: devuelve la clave interna de la seccion.
    @Override
    public String getViewKey() {
        return viewKey;
    }

    // Interfaces: devuelve el titulo visible de la seccion.
    @Override
    public String getViewTitle() {
        return viewTitle;
    }

    // Interfaces: devuelve el icono de la seccion.
    @Override
    public Icon getViewIcon() {
        return viewIcon;
    }

    // Swing: crea el encabezado superior de la vista actual.
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(14, 0));
        headerPanel.setOpaque(false);

        JLabel lblIcon = new JLabel(viewIcon);
        lblIcon.setVerticalAlignment(SwingConstants.TOP);

        JLabel lblTitle = new JLabel(viewTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(titleColor);

        JLabel lblDescription = new JLabel(viewDescription);
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDescription.setForeground(textColor);

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblDescription, BorderLayout.CENTER);

        headerPanel.add(lblIcon, BorderLayout.WEST);
        headerPanel.add(textPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    // Swing: crea un contenedor visual simple para dejar la vista lista y funcional.
    private JPanel createPlaceholderPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setPreferredSize(new Dimension(430, 230));
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(cardBorderColor, 1),
                        BorderFactory.createEmptyBorder(30, 30, 30, 30)
                )
        );

        JLabel lblIcon = new JLabel(viewIcon);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitle = new JLabel(viewTitle, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(titleColor);

        JLabel lblText = new JLabel(
                "<html><div style='text-align:center; width:280px;'>"
                + "Vista base activa para la seccion de "
                + viewTitle
                + ".<br><br>Desde aqui podras continuar con el desarrollo del modulo."
                + "</div></html>",
                SwingConstants.CENTER
        );
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblText.setForeground(textColor);

        card.add(lblIcon, BorderLayout.NORTH);
        card.add(lblTitle, BorderLayout.CENTER);
        card.add(lblText, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(card, gbc);

        return wrapper;
    }
}
