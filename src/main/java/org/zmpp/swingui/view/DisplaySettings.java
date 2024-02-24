package org.zmpp.swingui.view;

import java.awt.*;

public class DisplaySettings {
    private Font stdFont;
    private Font fixedFont;
    private int defaultForeground;
    private int defaultBackground;
    private boolean antialias;

    public DisplaySettings(Font stdFont, Font fixedFont, int defaultBackground, int defaultForeground, boolean antialias) {
        setSettings(stdFont, fixedFont, defaultBackground, defaultForeground, antialias);
    }

    public Font getStdFont() {
        return this.stdFont;
    }

    public Font getFixedFont() {
        return this.fixedFont;
    }

    public int getDefaultBackground() {
        return this.defaultBackground;
    }

    public int getDefaultForeground() {
        return this.defaultForeground;
    }

    public boolean getAntialias() {
        return this.antialias;
    }

    public void setSettings(Font stdFont, Font fixedFont, int defaultBackground, int defaultForeground, boolean antialias) {
        this.stdFont = stdFont;
        this.fixedFont = fixedFont;
        this.defaultBackground = defaultBackground;
        this.defaultForeground = defaultForeground;
        this.antialias = antialias;
    }
}
