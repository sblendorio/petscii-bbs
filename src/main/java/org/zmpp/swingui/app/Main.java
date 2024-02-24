package org.zmpp.swingui.app;

import org.zmpp.blorb.NativeImage;
import org.zmpp.blorb.NativeImageFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    public static final boolean DEBUG = true;
    private static final PropertyResourceBundle MESSAGE_BUNDLE = (PropertyResourceBundle) PropertyResourceBundle.getBundle("zmpp_messages");
    public static final String APP_NAME = getMessage("app.name");

    public static String getMessage(String property) {
        return MESSAGE_BUNDLE.getString(property);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LogManager.getLogManager().readConfiguration();
            Logger.getLogger("org.zmpp").setLevel(Level.SEVERE);
            Logger.getLogger("org.zmpp.screen").setLevel(Level.SEVERE);
            Logger.getLogger("org.zmpp.ui").setLevel(Level.SEVERE);
            Logger.getLogger("org.zmpp.control").setLevel(Level.SEVERE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ZmppFrame.openStoryFile();
    }

    static class AwtImage implements NativeImage {
        private final BufferedImage image;

        public AwtImage(BufferedImage image) {
            this.image = image;
        }

        public BufferedImage getImage() {
            return this.image;
        }

        public int getWidth() {
            return this.image.getWidth();
        }

        public int getHeight() {
            return this.image.getHeight();
        }
    }

    static class AwtImageFactory implements NativeImageFactory {
        public NativeImage createImage(InputStream inputStream) throws IOException {
            return new AwtImage(ImageIO.read(inputStream));
        }
    }
}
