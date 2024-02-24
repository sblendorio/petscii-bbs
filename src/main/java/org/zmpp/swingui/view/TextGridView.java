package org.zmpp.swingui.view;

import org.zmpp.windowing.AnnotatedCharacter;
import org.zmpp.windowing.BufferedScreenModel;
import org.zmpp.windowing.TextAnnotation;
import org.zmpp.windowing.TextCursor;

import javax.swing.*;
import java.awt.*;

public class TextGridView
        extends JComponent {
    private static final char REF_CHAR = '0';
    private static final AnnotatedCharacter EMPTY_CHAR = null;
    private AnnotatedCharacter[][] grid = new AnnotatedCharacter[0][0];

    private final ScreenModelSplitView parent;

    private boolean cursorShown = false;

    public TextGridView(ScreenModelSplitView parent) {
        this.parent = parent;
    }

    private BufferedScreenModel getScreenModel() {
        return this.parent.getScreenModel();
    }

    public void setGridSize(int numrows, int numcols) {
        this.grid = new AnnotatedCharacter[numrows][numcols];
    }

    public int getNumRows() {
        return (this.grid == null) ? 0 : this.grid.length;
    }

    public int getNumColumns() {
        return (this.grid == null || this.grid.length == 0) ? 0 : (this.grid[0]).length;
    }

    public void clear(int color) {
        TextAnnotation annotation = new TextAnnotation('\004', 0, color, color);

        AnnotatedCharacter annchar = new AnnotatedCharacter(annotation, ' ');
        int row;
        for (row = 0; row < getScreenModel().getNumRowsUpper(); row++) {
            for (int col = 0; col < (this.grid[row]).length; col++) {
                this.grid[row][col] = annchar;
            }
        }

        for (row = getScreenModel().getNumRowsUpper(); row < this.grid.length; row++) {
            for (int col = 0; col < (this.grid[row]).length; col++) {
                this.grid[row][col] = null;
            }
        }
    }

    private void visualizeCursorPosition(Graphics g, int row, int col) {
        AnnotatedCharacter c = this.grid[row][col];
        if (c != null) {
            drawCharacter(g, row, col);
        }
    }

    private void drawCharacter(Graphics g, int row, int col) {
        AnnotatedCharacter c = this.grid[row][col];
        if (c != null) {
            TextAnnotation annotation = c.getAnnotation();
            Font drawfont = this.parent.getFont(annotation);
            g.setFont(drawfont);
            FontMetrics fontMetrics = g.getFontMetrics();
            int posy = row * fontMetrics.getHeight() + fontMetrics.getAscent();
            int posx = col * fontMetrics.charWidth('0');

            ColorTranslator colTranslator = ColorTranslator.getInstance();
            Color foreground = colTranslator.translate(annotation.getForeground(), this.parent.getDefaultForeground());

            Color background = colTranslator.translate(annotation.getBackground(), this.parent.getDefaultBackground());

            if (annotation.isReverseVideo()) {

                Color tmp = foreground;
                foreground = background;
                background = tmp;
            }
            g.setColor(background);
            g.fillRect(posx, row * fontMetrics.getHeight(), fontMetrics.charWidth('0'), fontMetrics.getHeight());


            g.setColor(foreground.brighter());
            g.drawString(String.valueOf(c.getCharacter()), posx, posy);
        }
    }

    public void setCharacter(int line, int column, AnnotatedCharacter c) {
        this.grid[line - 1][column - 1] = c;
    }

    public void paintComponent(Graphics g) {
        for (int row = 0; row < this.grid.length; row++) {
            for (int col = 0; col < (this.grid[row]).length; col++) {
                visualizeCursorPosition(g, row, col);
            }
        }
    }

    public void viewCursor(boolean flag) {
        TextCursor cursor = getScreenModel().getTextCursor();
        if (flag) {
            setCharacter(cursor.getLine(), cursor.getColumn(), getCursorChar());
            this.cursorShown = true;
        } else {
            if (this.cursorShown) {
                setCharacter(cursor.getLine(), cursor.getColumn(), EMPTY_CHAR);
            }
            this.cursorShown = false;
        }
    }

    private AnnotatedCharacter getCursorChar() {
        return new AnnotatedCharacter(new TextAnnotation('\004', 1, this.parent.getDefaultBackground(), this.parent.getDefaultForeground()), ' ');
    }
}
