package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de productos.
public class ProductosJPanel extends BasePanel {

    public ProductosJPanel() {
        super(
                "productos",
                "Productos",
                "Controle el catalogo general de productos.",
                ViewIcons.build(FontAwesome.CUBE, 28)
        );
    }
}
