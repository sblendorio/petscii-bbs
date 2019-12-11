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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

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
    
    File storyfile = null;
    try {
      
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      
      ex.printStackTrace();
    }
    /*
    if (args.length >= 1) {
      
      storyfile = new File(args[0]);      
      
    } else {
    
      JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
      fileChooser.setDialogTitle("Open story file...");
      if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      
        storyfile = fileChooser.getSelectedFile();
      }
    }
    */
//minizork
    List<Path> dir = getDirContent("zmpp");
    dir.forEach(System.out::println);

    ///storyfile =   dir.get(0).toFile();

    storyfile = new File(Main.class.getResource("/zmpp/minizork.z3").getFile());
    runStoryFile(storyfile);
  }
  private static final ClassLoader NULL_CLASSLOADER = null;

  public static List<Path> getDirContent(String path) throws URISyntaxException, IOException {
    List<Path> result = new ArrayList<>();
    URL jar = Main.class.getProtectionDomain().getCodeSource().getLocation();
    Path jarFile = Paths.get(jar.toURI());
    try (FileSystem fs = FileSystems.newFileSystem(jarFile, NULL_CLASSLOADER);
         DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(path))) {
      for (Path p : directoryStream) {
        result.add(p);
      }

      result.sort((o1, o2) -> o1 == null || o2 == null ? 0 :
              o1.getFileName().toString().compareTo(o2.getFileName().toString()));
      return result;
    }
  }
  /**
   * This method opens a frame and runs the specified story file.
   * 
   * @param storyfile the story file
   */
  public static void runStoryFile(File storyfile) {
    
    if (System.getProperty("mrj.version") != null) {
      
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode",
          "false");
      System.setProperty("com.apple.mrj.application.apple.menu.about.name",
          "ZMPP");
    }
    // Read in the story file

    //minizork
    System.out.println("storyfile="+storyfile);
    System.out.println("storyfile.exists="+storyfile.exists());
    System.out.println("storyfile.isFile="+storyfile.isFile());

    if (storyfile != null && storyfile.exists() && storyfile.isFile()) {
      
      ApplicationMachineFactory factory;
      
      if (isZblorbSuffix(storyfile.getName())) {
        
        factory = new ApplicationMachineFactory(storyfile);
        
      } else {
        
        File blorbfile = searchForResources(storyfile);
        factory = new ApplicationMachineFactory(storyfile, blorbfile);
      }
      
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
      
    } else {
      
      JOptionPane.showMessageDialog(null,
          String.format("The selected story file '%s' was not found",
          storyfile != null ? storyfile.getPath() : ""),
          "Story file not found", JOptionPane.ERROR_MESSAGE);
    }
  }
    
  private static boolean isZblorbSuffix(String filename) {
    
    return filename.endsWith("zblorb") || filename.endsWith("zlb");
  }
  
  /**
   * Trys to find a resource file in Blorb format.
   * 
   * @param storyfile the storyfile
   * @return the blorb file if one exists or null
   */
  private static File searchForResources(File storyfile) {
    
    StringTokenizer tok = new StringTokenizer(storyfile.getName(), ".");
    String prefix = tok.nextToken();
    String dir = storyfile.getParent();
    String blorbpath1 = ((dir != null) ? dir + System.getProperty("file.separator") : "")
                        + prefix + ".blb";
    String blorbpath2 = ((dir != null) ? dir + System.getProperty("file.separator") : "")
                        + prefix + ".blorb";

    File blorbfile1 = new File(blorbpath1);
    System.out.printf("does '%s' exist ? -> %b\n", blorbfile1.getPath(),
        blorbfile1.exists());
    if (blorbfile1.exists()) return blorbfile1;

    File blorbfile2 = new File(blorbpath2);
    System.out.printf("does '%s' exist ? -> %b\n", blorbfile2.getPath(),
        blorbfile2.exists());
    if (blorbfile2.exists()) return blorbfile2;
    
    return null;
  }  
}
