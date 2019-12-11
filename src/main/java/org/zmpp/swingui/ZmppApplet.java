/*
 * $Id: ZmppApplet.java,v 1.18 2006/05/12 21:30:04 weiju Exp $
 * 
 * Created on 2005/11/15
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.zmpp.io.IOSystem;
import org.zmpp.io.InputStream;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.StatusLine;

/**
 * This is the applet class for ZMPP.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ZmppApplet extends JApplet
implements InputStream, StatusLine, IOSystem {

  /**
   * The serial version.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The color map maps parameters to color ids.
   */
  private static final Map<String, Integer> colormap =
    new HashMap<String, Integer>();
  
  static {
    
    colormap.put("black",   2);
    colormap.put("red",     3);
    colormap.put("green",   4);
    colormap.put("yellow",  5);
    colormap.put("blue",    6);
    colormap.put("magenta", 7);
    colormap.put("cyan",    8);
    colormap.put("white",   9);
    colormap.put("gray",   10);
  }
    
  
  private JLabel global1ObjectLabel;
  private JLabel statusLabel;
  private ScreenModel screen;
  private Machine machine;
  private LineEditorImpl lineEditor;
  private GameThread currentGame;
  private DisplaySettings settings;
  private boolean savetofile;
  
  public void init() {
    
    requestFocusInWindow();
    String story = getParameter("storyfile");
    String blorb = getParameter("blorbfile");
    String saveto = getParameter("saveto");
    String fixedfontsize = getParameter("fixedfontsize");
    String stdfontsize = getParameter("stdfontsize");
    String defbg = getParameter("defaultbg");
    String deffg = getParameter("defaultfg");
    String antialiasparam = getParameter("antialias");
    
    int sizeStdFont = 12;
    int sizeFixedFont = 12;
    int defaultBackground = ColorTranslator.UNDEFINED;
    int defaultForeground = ColorTranslator.UNDEFINED;
    boolean antialias = true;
    
    savetofile = "file".equalsIgnoreCase(saveto);

    sizeFixedFont = parseInt(fixedfontsize, sizeFixedFont);
    sizeStdFont = parseInt(stdfontsize, sizeStdFont);
    defaultBackground = parseColor(defbg, defaultBackground);
    defaultForeground = parseColor(deffg, defaultForeground);
    antialias = parseBoolean(antialiasparam, antialias);
    
    settings = new DisplaySettings(sizeStdFont, sizeFixedFont,
        defaultBackground, defaultForeground, antialias);
    
    try {

      URL blorburl = null;
      if (blorb != null) blorburl = new URL(getDocumentBase(), blorb);
      
      AppletMachineFactory factory = null;

      if (story != null) {

        URL storyurl = new URL(getDocumentBase(), story);
        factory = new AppletMachineFactory(this, storyurl, blorburl,
                                           savetofile);

      } else {

        factory = new AppletMachineFactory(this, blorburl, savetofile);
      }
      machine = factory.buildMachine();
      
    } catch (Exception ex) {
      
      ex.printStackTrace();      
    }
  }
  
  /**
   * Parses the specified string into an integer and returns it, if str
   * is null or not an integer, the fallback value is returned.
   * 
   * @param str the string to parse
   * @param fallback the fallback value
   * @return the integer result
   */
  private int parseInt(String str, int fallback) {
    
    int result = fallback;
    if (str != null) {
      try {
        result = Integer.parseInt(str);
      } catch (NumberFormatException ignore) { }
    }
    return result;
  }

  /**
   * Retrieves the color id for the specified string.
   * 
   * @param str the color string
   * @param fallback the fallback value
   * @return the color id
   */
  private int parseColor(String str, int fallback) {
    
    return colormap.get(str) == null ? fallback : colormap.get(str);
  }
  
  /**
   * Retrievs the boolean value for the specified string. Values can
   * be true|false or on|off.
   * 
   * @param str the string
   * @param fallback the fallback value
   * @return the boolean value
   */
  private boolean parseBoolean(String str, boolean fallback) {
    
    if ("false".equals(str) || "off".equals(str)) return false;
    if ("true".equals(str) || "on".equals(str)) return true;
    return fallback;
  }
  
  private void createUI(Machine machine) {
    
    lineEditor = new LineEditorImpl(machine.getGameData().getStoryFileHeader(),
        machine.getGameData().getZsciiEncoding());
    
    JComponent view = null;
    
    if (machine.getGameData().getStoryFileHeader().getVersion() == 6) {
      
      view = new Viewport6(machine, lineEditor, settings);
      screen = (ScreenModel) view;
      
    } else {
      
      view = new TextViewport(machine, lineEditor, settings);
      screen = (ScreenModel) view;
    }
    view.setPreferredSize(new Dimension(640, 480));
    view.setMinimumSize(new Dimension(400, 300));
    
    if (machine.getGameData().getStoryFileHeader().getVersion() <= 3) {
      
      JPanel statusPanel = new JPanel(new GridLayout(1, 2));
      JPanel status1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JPanel status2Panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      statusPanel.add(status1Panel);
      statusPanel.add(status2Panel);        
      global1ObjectLabel = new JLabel(" ");
      statusLabel = new JLabel(" ");
      status1Panel.add(global1ObjectLabel);
      status2Panel.add(statusLabel);    
      getContentPane().add(statusPanel, BorderLayout.NORTH);
      getContentPane().add(view, BorderLayout.CENTER);
      
    } else {
            
      setContentPane(view);
    }
    
    addKeyListener(lineEditor);
    view.addKeyListener(lineEditor);
    view.addMouseListener(lineEditor);
  }
  
  public void initUI(final Machine machine) {
  
    try {
      EventQueue.invokeAndWait(new Runnable() {
        public void run() {
        
          createUI(machine);
        }
      });
    } catch (Exception ex) {
      
      ex.printStackTrace();
    }
  }
  
  public void start() {
   
    currentGame = new GameThread(machine, screen);
    currentGame.start();
  }
  
  public ScreenModel getScreenModel() {
    
    return screen;
  }
    
  // *************************************************************************
  // ******** StatusLine interface
  // ******************************************
  
  public void updateStatusScore(final String objectName, final int score,
      final int steps) {

    EventQueue.invokeLater(new Runnable() {
      
      public void run() {
        
        global1ObjectLabel.setText(objectName);
        statusLabel.setText(score + "/" + steps);
      }
    });
  }
  
  public void updateStatusTime(final String objectName, final int hours,
      final int minutes) {
        
    EventQueue.invokeLater(new Runnable() {
      
      public void run() {
        
        global1ObjectLabel.setText(objectName);
        statusLabel.setText(String.format("%02d:%02d", hours, minutes));
      }
    });
  }

  public Writer getTranscriptWriter() {
    
    return new OutputStreamWriter(System.out);
  }
  
  public Reader getInputStreamReader() {
    
    File currentdir = new File(System.getProperty("user.dir"));    
    JFileChooser fileChooser = new JFileChooser(currentdir);
    fileChooser.setDialogTitle("Set input stream file ...");
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      
      try {
        
        return new FileReader(fileChooser.getSelectedFile());
        
      } catch (IOException ex){
        
        ex.printStackTrace();
      }
    }
    return null;
  }
  
  // *************************************************************************
  // ******** InputStream interface
  // ******************************************
  
  public void close() { }

  public void cancelInput() {
    
    lineEditor.cancelInput();
  }
  
  public short getZsciiChar(boolean flushBeforeGet) {

    enterEditMode(flushBeforeGet);
    short zsciiChar = lineEditor.nextZsciiChar();
    leaveEditMode(flushBeforeGet);
    return zsciiChar;
  }
  
  private void enterEditMode(boolean flushbuffer) {
    
    if (!lineEditor.isInputMode()) {

      screen.resetPagers();
      lineEditor.setInputMode(true, flushbuffer);
    }
  }
  
  private void leaveEditMode(boolean flushbuffer) {
    
    lineEditor.setInputMode(false, flushbuffer);
  }  
}
