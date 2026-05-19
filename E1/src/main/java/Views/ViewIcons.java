package Views;

import java.awt.Color;
import javax.swing.Icon;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

// Swing + Utilidad: centraliza la creacion de iconos para las vistas.
public final class ViewIcons {

    private static final Color DEFAULT_COLOR = new Color(27, 94, 157);

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    private ViewIcons() {
    }

    // Swing + Libreria externa: crea un icono usando el color por defecto del sistema.
    public static Icon build(FontAwesome icon, int size) {
        return IconFontSwing.buildIcon(icon, size, DEFAULT_COLOR);
    }

    // Swing + Libreria externa: crea un icono usando un color personalizado.
    public static Icon build(FontAwesome icon, int size, Color color) {
        return IconFontSwing.buildIcon(icon, size, color);
    }
}
