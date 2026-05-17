package Views;

// Interfaces + Swing: define operaciones comunes para las vistas JFrame.
public interface IViewFrame {

    // Interfaces: obliga a construir la interfaz visual.
    void initUI();

    // Interfaces: obliga a configurar propiedades de la ventana.
    void configureWindow();
}
