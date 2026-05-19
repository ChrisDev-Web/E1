package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de usuarios.
public class UsuariosJPanel extends BasePanel {

    public UsuariosJPanel() {
        super(
                "usuarios",
                "Usuarios",
                "Administre los usuarios registrados del sistema.",
                ViewIcons.build(FontAwesome.USER, 28)
        );
    }
}
