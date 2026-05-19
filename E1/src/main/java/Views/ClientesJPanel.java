package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de clientes.
public class ClientesJPanel extends BasePanel {

    public ClientesJPanel() {
        super(
                "clientes",
                "Clientes",
                "Gestione la informacion principal de sus clientes.",
                ViewIcons.build(FontAwesome.USERS, 28)
        );
    }
}
