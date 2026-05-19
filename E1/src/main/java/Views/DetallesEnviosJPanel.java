package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de detalles de envios.
public class DetallesEnviosJPanel extends BasePanel {

    public DetallesEnviosJPanel() {
        super(
                "detallesEnvios",
                "Detalles de Envios",
                "Consulte el detalle operativo de cada envio.",
                ViewIcons.build(FontAwesome.FILE_TEXT_O, 28)
        );
    }
}
