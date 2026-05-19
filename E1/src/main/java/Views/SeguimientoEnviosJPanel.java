package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de seguimiento de envios.
public class SeguimientoEnviosJPanel extends BasePanel {

    public SeguimientoEnviosJPanel() {
        super(
                "seguimientoEnvios",
                "Seguimiento de Envios",
                "Monitoree el estado y trazabilidad de los envios.",
                ViewIcons.build(FontAwesome.MAP_MARKER, 28)
        );
    }
}
