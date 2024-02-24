package org.zmpp.swingui.view;

import javax.swing.*;
import java.awt.*;

public class ScreenModelLayout implements LayoutManager2 {
    private JComponent upper;
    private JComponent lower;
    private int numRowsUpper;
    private FontSelector fontSelector;
    private boolean valid;

    public void setNumRowsUpper(int numrows) {
        this.numRowsUpper = numrows;
    }

    public void setFontSelector(FontSelector selector) {
        this.fontSelector = selector;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Dimension minimumLayoutSize(Container parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void layoutContainer(Container parent) {
        if (!this.valid) {
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();
            int upperSize = getUpperSize();
            this.upper.setBounds(0, 0, parentWidth, parentHeight);
            this.lower.setBounds(0, upperSize, parentWidth, parentHeight - upperSize);
            this.valid = true;
        }
    }

    private int getUpperSize() {
        return getUpperFontMetrics().getHeight() * this.numRowsUpper;
    }

    private FontMetrics getUpperFontMetrics() {
        return this.upper.getFontMetrics(this.fontSelector.getFont('\004', 1));
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        Integer id = (Integer) constraints;
        if (id == JLayeredPane.DEFAULT_LAYER) {
            this.lower = (JComponent) comp;
        } else {
            this.upper = (JComponent) comp;
        }
    }

    public Dimension maximumLayoutSize(Container target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getLayoutAlignmentX(Container target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getLayoutAlignmentY(Container target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void invalidateLayout(Container target) {
        this.valid = false;
    }
}
