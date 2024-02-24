package org.zmpp.swingui.view;

import org.zmpp.windowing.TextAnnotation;

import java.awt.*;

public class FontSelector {
    private Font fixedFont;
    private Font stdFont;

    public void setStandardFont(Font font) {
        this.stdFont = font;
    }

    public Font getFont(TextAnnotation annotation) {
        return getStyledFont(annotation.isFixed(), annotation.isBold(), annotation.isItalic());
    }

    public Font getFont(char fontnum, int style) {
        return getFont(new TextAnnotation(fontnum, style));
    }

    public Font getFixedFont() {
        return getStyledFont(true, false, false);
    }

    public void setFixedFont(Font font) {
        this.fixedFont = font;
    }

    private Font getStyledFont(boolean fixed, boolean bold, boolean italic) {
        Font font = fixed ? this.fixedFont : this.stdFont;
        if (bold) font = font.deriveFont(1);
        if (italic) font = font.deriveFont(2);
        return font;
    }
}
