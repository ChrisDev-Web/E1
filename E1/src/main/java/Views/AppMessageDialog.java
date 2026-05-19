package Views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

// Swing + Utilidad: muestra mensajes mas claros, grandes y visibles para el usuario.
public final class AppMessageDialog {

    private AppMessageDialog() {
    }

    // Swing: muestra un mensaje de error con mejor visibilidad.
    public static void showError(Component parent, String title, String message) {
        showDialog(
                parent,
                title,
                message,
                new Color(198, 40, 40),
                UIManager.getIcon("OptionPane.errorIcon"),
                "Entendido"
        );
    }

    // Swing: muestra un mensaje informativo con mejor visibilidad.
    public static void showInfo(Component parent, String title, String message) {
        showDialog(
                parent,
                title,
                message,
                new Color(46, 125, 50),
                UIManager.getIcon("OptionPane.informationIcon"),
                "Aceptar"
        );
    }

    // Swing: construye y abre el dialogo personalizado.
    private static void showDialog(
            Component parent,
            String title,
            String message,
            Color accentColor,
            Icon icon,
            String buttonText
    ) {
        JPanel contentPanel = new JPanel(new BorderLayout(14, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accentColor, 2),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18)
                )
        );

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setVerticalAlignment(JLabel.TOP);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(33, 33, 33));

        JTextArea txtMessage = new JTextArea(normalizeMessage(message));
        txtMessage.setEditable(false);
        txtMessage.setFocusable(false);
        txtMessage.setOpaque(false);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setLineWrap(true);
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtMessage.setForeground(new Color(70, 70, 70));

        JPanel textPanel = new JPanel(new BorderLayout(0, 8));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(txtMessage, BorderLayout.CENTER);

        contentPanel.add(lblIcon, BorderLayout.WEST);
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.setPreferredSize(new Dimension(420, 150));

        JOptionPane optionPane = new JOptionPane(
                contentPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{buttonText},
                buttonText
        );

        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    // Utilidad: evita que se muestren mensajes vacios al usuario.
    private static String normalizeMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Ocurrio un problema inesperado. Revise los datos e intente nuevamente.";
        }

        return message.trim();
    }
}
