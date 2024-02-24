package org.zmpp.swingui.app;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PictureLabel extends JLabel {
    private static final long serialVersionUID = 1L;
    private final BufferedImage image;

    public PictureLabel(BufferedImage image) {
        this.image = image;
    }

    public void paint(Graphics g) {
        int imageHeight = this.image.getHeight();
        int imageWidth = this.image.getWidth();
        double scalefactor = (imageHeight > imageWidth) ? (getHeight() / imageHeight) : (getWidth() / imageWidth);


        g.drawImage(this.image, 0, 0, (int) (imageWidth * scalefactor), (int) (imageHeight * scalefactor), this);
    }
}
