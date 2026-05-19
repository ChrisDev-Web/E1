package Views;

import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Herencia: vista base para la seccion de cajas.
public class CajasJPanel extends BasePanel {

    public CajasJPanel() {
        super(
                "cajas",
                "Cajas",
                "Controle las cajas y unidades de embalaje.",
                ViewIcons.build(FontAwesome.ARCHIVE, 28)
        );
    }
}
