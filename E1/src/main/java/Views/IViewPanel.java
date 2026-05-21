package Views;

import javax.swing.Icon;

// Interfaces + Swing: define el contrato comun de las vistas basadas en JPanel.
public interface IViewPanel {

    // Interfaces: obliga a construir la interfaz del panel.
    void initPanel();

    // Interfaces: identifica el panel dentro de la navegacion.
    String getViewKey();

    // Interfaces: devuelve el titulo visible de la seccion.
    String getViewTitle();

    // Interfaces: expone el icono visual de la seccion.
    Icon getViewIcon();

    // Interfaces: permite refrescar datos cuando el panel vuelve a mostrarse.
    default void onViewShown() {
    }
}
