package org.zmpp.swingui.view;

import java.awt.*;

public class ColorTranslator {
    private static final Color GREEN = new Color(0, 190, 0);
    private static final Color RED = new Color(190, 0, 0);
    private static final Color YELLOW = new Color(190, 190, 0);
    private static final Color BLUE = new Color(0, 0, 190);
    private static final Color MAGENTA = new Color(190, 0, 190);
    private static final Color CYAN = new Color(0, 190, 190);

    private static final ColorTranslator instance = new ColorTranslator();

    public static ColorTranslator getInstance() {
        return instance;
    }

    public Color translate(int colornum, int defaultColor) {
        switch (colornum) {
            case 1:
                return translate(defaultColor, -1000);
            case 2:
                return Color.BLACK;
            case 3:
                return RED;
            case 4:
                return GREEN;
            case 5:
                return YELLOW;
            case 6:
                return BLUE;
            case 7:
                return MAGENTA;
            case 8:
                return CYAN;
            case 9:
                return Color.WHITE;
            case 10:
                return Color.DARK_GRAY;
        }
        return Color.BLACK;
    }

    public Color translate(int colornum) {
        return translate(colornum, -1000);
    }
}
