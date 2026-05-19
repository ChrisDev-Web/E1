package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de categorias.
public class CategoriasJPanel extends BasePanel {

    public CategoriasJPanel() {
        super(
                "categorias",
                "Categorias",
                "Organice y clasifique las categorias disponibles.",
                ViewIcons.build(FontAwesome.TAGS, 28)
        );
    }
}
