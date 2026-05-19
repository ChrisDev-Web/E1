package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de inventario.
public class InventarioJPanel extends BasePanel {

    public InventarioJPanel() {
        super(
                "inventario",
                "Inventario",
                "Revise existencias, movimientos y control de stock.",
                ViewIcons.build(FontAwesome.CLIPBOARD, 28)
        );
    }
}
