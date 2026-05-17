package Views;

import javax.swing.JFrame;

// Herencia + Swing: base comun para reutilizar comportamiento en las vistas.
public abstract class BaseFrame extends JFrame implements IViewFrame {

    // Herencia + Swing: abre otra ventana y cierra la actual.
    protected void openFrame(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    // Herencia + Swing: aplica configuracion comun a cualquier JFrame hijo.
    protected void configureDefaultWindow(String title, boolean resizable) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(resizable);
        pack();
        setLocationRelativeTo(null);
    }
}
