package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de envios.
public class EnviosJPanel extends BasePanel {

    public EnviosJPanel() {
        super(
                "envios",
                "Envios",
                "Administre la salida y coordinacion de envios.",
                ViewIcons.build(FontAwesome.TRUCK, 28)
        );
    }
}
