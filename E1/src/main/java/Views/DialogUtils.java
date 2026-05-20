package Views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

// Swing + Utilidad: centraliza configuracion comun para dialogos estaticos y centrados.
public final class DialogUtils {

    private static final Color SURFACE_COLOR = Color.WHITE;
    private static final Color PAGE_COLOR = new Color(243, 247, 251);
    private static final Color BORDER_COLOR = new Color(214, 223, 233);
    private static final Color MUTED_TEXT_COLOR = new Color(98, 110, 123);
    private static final Color PRIMARY_TEXT_COLOR = new Color(33, 33, 33);

    private DialogUtils() {
    }

    // Swing: centra el dialogo dentro de su ventana padre y evita que sobresalga.
    public static void centerAndLock(JDialog dialog, Component relativeTo) {
        if (dialog == null) {
            return;
        }

        dialog.setResizable(false);

        Window ownerWindow = resolveOwnerWindow(relativeTo);

        if (ownerWindow == null || !ownerWindow.isShowing()) {
            dialog.setLocationRelativeTo(relativeTo);
            return;
        }

        Rectangle ownerBounds = ownerWindow.getBounds();
        int margin = 28;
        int maxWidth = Math.max(360, ownerBounds.width - (margin * 2));
        int maxHeight = Math.max(260, ownerBounds.height - (margin * 2));

        Dimension currentSize = dialog.getSize();
        if (currentSize.width <= 0 || currentSize.height <= 0) {
            currentSize = dialog.getPreferredSize();
        }

        int fittedWidth = Math.min(currentSize.width, maxWidth);
        int fittedHeight = Math.min(currentSize.height, maxHeight);

        Dimension minimumSize = dialog.getMinimumSize();
        if (minimumSize != null) {
            dialog.setMinimumSize(new Dimension(
                    Math.min(minimumSize.width, maxWidth),
                    Math.min(minimumSize.height, maxHeight)
            ));
        }

        dialog.setSize(new Dimension(fittedWidth, fittedHeight));

        int x = ownerBounds.x + ((ownerBounds.width - fittedWidth) / 2);
        int y = ownerBounds.y + ((ownerBounds.height - fittedHeight) / 2);

        x = Math.max(ownerBounds.x + margin / 2, x);
        y = Math.max(ownerBounds.y + margin / 2, y);

        int maxX = ownerBounds.x + ownerBounds.width - fittedWidth - (margin / 2);
        int maxY = ownerBounds.y + ownerBounds.height - fittedHeight - (margin / 2);

        dialog.setLocation(Math.min(x, maxX), Math.min(y, maxY));
    }

    // Swing: aplica un fondo general coherente para modales del sistema.
    public static void applyDialogTheme(JDialog dialog) {
        if (dialog == null) {
            return;
        }

        dialog.getContentPane().setBackground(PAGE_COLOR);
    }

    // Swing: crea una cabecera visual para modales con identidad de seccion.
    public static JPanel createHeader(Icon icon, String title, String subtitle, Color accentColor) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(accentColor);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel iconLabel = new JLabel(icon);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(232, 240, 247));

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.CENTER);

        header.add(iconLabel, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    // Swing: crea una tarjeta blanca reutilizable para formularios y detalles.
    public static JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(SURFACE_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        if (title != null && !title.isBlank()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(PRIMARY_TEXT_COLOR);
            card.add(titleLabel, BorderLayout.NORTH);
        }

        return card;
    }

    // Swing: crea un panel blanco con borde, usando el layout indicado.
    public static JPanel createSurfacePanel(LayoutManager layout, int padding) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        ));
        return panel;
    }

    // Swing: etiqueta secundaria para formularios y tiles informativos.
    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(MUTED_TEXT_COLOR);
        return label;
    }

    // Swing: valor visual para datos de solo lectura.
    public static JLabel createValueLabel(String text) {
        JLabel valueLabel = new JLabel(normalizeValue(text));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(PRIMARY_TEXT_COLOR);
        valueLabel.setBorder(createInputBorder());
        valueLabel.setOpaque(true);
        valueLabel.setBackground(new Color(249, 251, 253));
        return valueLabel;
    }

    // Swing: tile vertical para destacar informacion.
    public static JPanel createInfoTile(String label, String value) {
        JPanel tile = new JPanel(new BorderLayout(0, 8));
        tile.setBackground(new Color(249, 251, 253));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 238)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLabel = createFieldLabel(label);
        JLabel valueLabel = new JLabel(normalizeValue(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(PRIMARY_TEXT_COLOR);

        tile.add(titleLabel, BorderLayout.NORTH);
        tile.add(valueLabel, BorderLayout.CENTER);
        return tile;
    }

    // Swing: aplica estilo visual uniforme a campos editables.
    public static <T extends JComponent> T styleInput(T component) {
        component.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        component.setBorder(createInputBorder());
        component.setOpaque(true);
        component.setBackground(Color.WHITE);
        component.setPreferredSize(new Dimension(Math.max(component.getPreferredSize().width, 120), 38));
        return component;
    }

    // Swing: crea un scroll neutro para areas de texto o listas.
    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    // Swing: estiliza un area de texto multilínea para formularios.
    public static JTextArea styleTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(PRIMARY_TEXT_COLOR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    private static Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }

    private static String normalizeValue(String text) {
        return text == null || text.trim().isEmpty() ? "-" : text.trim();
    }

    private static Window resolveOwnerWindow(Component relativeTo) {
        if (relativeTo instanceof Window) {
            return (Window) relativeTo;
        }

        return relativeTo == null ? null : SwingUtilities.getWindowAncestor(relativeTo);
    }
}
