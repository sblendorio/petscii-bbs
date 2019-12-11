/*
 * $Id: Main.java,v 1.45 2007/03/25 04:19:08 weiju Exp $
 *
 * Created on 2005/10/17
 * Copyright 2005-2006 by Wei-ju Wu
 *
 * This file is part of The Z-machine Preservation Project (ZMPP).
 *
 * ZMPP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZMPP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZMPP; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.zmpp.swingui;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class starts the ZMPP swing interface.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class Main {

    /**
     * The application name.
     */
    public static final String APPNAME =
            "Z-Machine Preservation Project Version 0.92_02";

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final byte[] story = readBinaryFile("zmpp/minizork.z3");
        runStoryFile(story);
    }

    public static byte[] readBinaryFile(String filename) throws IOException {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
            return os.toByteArray();
        }
    }

    /**
     * This method opens a frame and runs the specified story file.
     *
     * @param story the story file
     */
    public static void runStoryFile(byte[] story) {

        if (System.getProperty("mrj.version") != null) {

            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode",
                    "false");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    "ZMPP");
        }
        // Read in the story file

        ApplicationMachineFactory factory;
        factory = new ApplicationMachineFactory(story);

        try {

            factory.buildMachine();
            ZmppFrame frame = factory.getUI();
            frame.startMachine();
            frame.pack();
            frame.setVisible(true);

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(null,
                    String.format("Could not read game.\nReason: '%s'", ex.getMessage()),
                    "Story file error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static boolean isZblorbSuffix(String filename) {

        return filename.endsWith("zblorb") || filename.endsWith("zlb");
    }


}
