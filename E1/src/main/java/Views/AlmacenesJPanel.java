package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de almacenes.
public class AlmacenesJPanel extends BasePanel {

    public AlmacenesJPanel() {
        super(
                "almacenes",
                "Almacenes",
                "Visualice y gestione los almacenes del sistema.",
                ViewIcons.build(FontAwesome.BUILDING_O, 28)
        );
    }
}
