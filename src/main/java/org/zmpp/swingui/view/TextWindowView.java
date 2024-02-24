package org.zmpp.swingui.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import org.zmpp.windowing.AnnotatedText;
import org.zmpp.windowing.TextAnnotation;

public class TextWindowView
        extends JTextPane {
    private ScreenModelSplitView parent;

    public TextWindowView(ScreenModelSplitView parent) {
        this.parent = parent;
    }


    public void setBackground(Color color) {
        super.setBackground(color);
        if (this.parent != null) this.parent.setBackground(color);
    }


    public void append(AnnotatedText segment) {
        Document doc = getDocument();
        try {
            doc.insertString(doc.getLength(), zsciiToUnicode(segment.getText()), setStyleAttributes(segment.getAnnotation()));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }


    private String zsciiToUnicode(String zsciiString) {
        return zsciiString.replace("\r", "\n");
    }

    public void clear(int background, int foreground) {
        setComponentColors(background, foreground);
        try {
            TextAnnotation annotation = new TextAnnotation('\001', 0, background, foreground);

            append(new AnnotatedText(annotation, getFormFeed()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getFormFeed() {
        StringBuilder formFeed = new StringBuilder();
        for (int i = 0; i < this.parent.getNumUpperRows(); i++) {
            formFeed.append("\n");
        }
        return formFeed.toString();
    }

    private void setComponentColors(int background, int foreground) {
        ColorTranslator translator = ColorTranslator.getInstance();
        setBackground(translator.translate(background, this.parent.getDefaultBackground()));

        setForeground(translator.translate(foreground, this.parent.getDefaultForeground()));
    }

    public void setCurrentStyle(TextAnnotation annotation) {
        setStyleAttributes(annotation);
    }

    private MutableAttributeSet setStyleAttributes(TextAnnotation annotation) {
        MutableAttributeSet attributes = getInputAttributes();
        Font font = this.parent.getFont(annotation);
        StyleConstants.setFontFamily(attributes, font.getFamily());
        StyleConstants.setFontSize(attributes, font.getSize());
        StyleConstants.setBold(attributes, annotation.isBold());
        StyleConstants.setItalic(attributes, annotation.isItalic());
        ColorTranslator colorTranslator = ColorTranslator.getInstance();
        Color background = colorTranslator.translate(annotation.getBackground(), this.parent.getDefaultBackground());

        Color foreground = colorTranslator.translate(annotation.getForeground(), this.parent.getDefaultForeground());

        if (annotation.isReverseVideo()) {
            StyleConstants.setBackground(attributes, foreground);
            StyleConstants.setForeground(attributes, background.brighter());
        } else {
            StyleConstants.setBackground(attributes, background);
            StyleConstants.setForeground(attributes, foreground.brighter());
        }
        return attributes;
    }
}
