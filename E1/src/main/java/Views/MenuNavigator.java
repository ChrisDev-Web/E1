package Views;

import java.awt.CardLayout;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

// Singleton + Swing: administra las vistas internas del menu principal con CardLayout.
public final class MenuNavigator {

    private static final MenuNavigator INSTANCE = new MenuNavigator();

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, IViewPanel> views;

    private String currentViewKey;

    private MenuNavigator() {
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.views = new LinkedHashMap<>();
    }

    // Singleton: devuelve la unica instancia del navegador interno.
    public static MenuNavigator getInstance() {
        return INSTANCE;
    }

    // Swing: reinicia el contenedor para volver a registrar secciones limpias.
    public void reset() {
        views.clear();
        contentPanel.removeAll();
        currentViewKey = null;
    }

    // Swing + POO: registra un panel para que pueda mostrarse desde el menu.
    public void registerView(IViewPanel view) {
        views.put(view.getViewKey(), view);
        contentPanel.add((JPanel) view, view.getViewKey());

        if (currentViewKey == null) {
            currentViewKey = view.getViewKey();
        }
    }

    // Swing: devuelve el panel contenedor usado por el CardLayout.
    public JPanel getContentPanel() {
        return contentPanel;
    }

    // POO: devuelve todas las vistas registradas respetando el orden de navegacion.
    public Collection<IViewPanel> getViews() {
        return views.values();
    }

    // Swing: muestra la vista pedida y devuelve su referencia.
    public IViewPanel showView(String viewKey) {
        IViewPanel view = views.get(viewKey);

        if (view != null) {
            currentViewKey = viewKey;
            cardLayout.show(contentPanel, viewKey);
            view.onViewShown();
            contentPanel.revalidate();
            contentPanel.repaint();
        }

        return view;
    }

    // POO: devuelve la vista actualmente visible.
    public IViewPanel getCurrentView() {
        return views.get(currentViewKey);
    }
}
