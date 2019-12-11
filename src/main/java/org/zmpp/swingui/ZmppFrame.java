/*
 * $Id: ZmppFrame.java,v 1.18 2006/05/16 18:35:23 weiju Exp $
 * 
 * Created on 2005/10/19
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.zmpp.io.IOSystem;
import org.zmpp.io.InputStream;
import org.zmpp.media.Resources;
import org.zmpp.media.StoryMetadata;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.StatusLine;

/**
 * This class is the main frame for ZMPP run as an application. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ZmppFrame extends JFrame
implements InputStream, StatusLine, IOSystem {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;

  private JLabel global1ObjectLabel;
  private JLabel statusLabel;
  private ScreenModel screen;
  private Machine machine;
  private LineEditorImpl lineEditor;
  private GameThread currentGame;
  private boolean isMacOs;
  private DisplaySettings settings;
  private Preferences preferences;

  /**
   * Constructor.
   * 
   * @param machine a Machine object
   */
  public ZmppFrame(final Machine machine) {
    
    super(Main.APPNAME);
    
    this.machine = machine;
    lineEditor = new LineEditorImpl(machine.getGameData().getStoryFileHeader(),
        machine.getGameData().getZsciiEncoding());
    
    isMacOs = (System.getProperty("mrj.version") != null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    JComponent view = null;
    
    preferences = Preferences.userNodeForPackage(ZmppFrame.class);
    settings = createDisplaySettings(preferences);
    
    if (machine.getGameData().getStoryFileHeader().getVersion() ==  6) {
      
      view = new Viewport6(machine, lineEditor, settings);
      screen = (ScreenModel) view;
      
    } else {

      view = new TextViewport(machine, lineEditor, settings);
      screen = (ScreenModel) view;
    }
    view.setPreferredSize(new Dimension(640, 476));
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
    
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    
    // Menus need to be slightly different on MacOS X, they do not have
    // an explicit File menu
    if (!isMacOs) { 
      
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic('F');
      menubar.add(fileMenu);

      // Quit is already in the application menu
      JMenuItem exitItem = new JMenuItem("Exit");
      exitItem.setMnemonic('x');
      fileMenu.add(exitItem);
      exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        
          System.exit(0);
        }
      });      
    }
    JMenu editMenu = new JMenu("Edit");
    menubar.add(editMenu);
    editMenu.setMnemonic('E');
    JMenuItem preferencesItem = new JMenuItem("Preferences...");
    preferencesItem.setMnemonic('P');
    editMenu.add(preferencesItem);
    preferencesItem.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        
        editPreferences();
      }
    });
    
    JMenu helpMenu = new JMenu("Help");
    menubar.add(helpMenu);
    helpMenu.setMnemonic('H');
    
    JMenuItem aboutItem = new JMenuItem("About ZMPP...");
    aboutItem.setMnemonic('A');
    helpMenu.add(aboutItem);
    aboutItem.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {

        about();
      }
    });
    
    //addKeyListener(lineEditor);
    view.addKeyListener(lineEditor);
    view.addMouseListener(lineEditor);
    
    // just for debugging
    view.addMouseMotionListener(new MouseMotionAdapter() {
      
      public void mouseMoved(MouseEvent e) {
        
        //System.out.printf("mouse pos: %d %d\n", e.getX(), e.getY());
      }
    });
    
    // Add an info dialog and a title if metadata exists
    Resources resources = machine.getGameData().getResources();
    if (resources != null && resources.getMetadata() != null) {
      
      StoryMetadata storyinfo = resources.getMetadata().getStoryInfo();
      setTitle(Main.APPNAME + " - " + storyinfo.getTitle()
          + " (" + storyinfo.getAuthor() + ")");
      
      JMenuItem aboutGameItem = new JMenuItem("About this Game ...");
      helpMenu.add(aboutGameItem);
      aboutGameItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {

          aboutGame();
        }
      });
    }
  }

  /**
   * Access to screen model.
   * 
   * @return the screen model
   */
  public ScreenModel getScreenModel() {
    
    return screen;
  }
  
  public void startMachine() {
    
    currentGame = new GameThread(machine, screen);
    currentGame.start();
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

  // *************************************************************************
  // ******** IOSystem interface
  // ******************************************

  public Writer getTranscriptWriter() {
    
    File currentdir = new File(System.getProperty("user.dir"));    
    JFileChooser fileChooser = new JFileChooser(currentdir);
    fileChooser.setDialogTitle("Set transcript file ...");
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      
      try {
        
        return new FileWriter(fileChooser.getSelectedFile());
        
      } catch (IOException ex) {
      
        ex.printStackTrace();
      }
    }
    return null;
  }
  
  public Reader getInputStreamReader() {
    
    File currentdir = new File(System.getProperty("user.dir"));    
    JFileChooser fileChooser = new JFileChooser(currentdir);
    fileChooser.setDialogTitle("Set input stream file ...");
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      
      try {
        
        return new FileReader(fileChooser.getSelectedFile());
        
      } catch (IOException ex) {
        
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
  
  /**
   * {@inheritDoc}
   */
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
  
  private void about() {
    
    JOptionPane.showMessageDialog(this,
        Main.APPNAME + "\n\u00a9 2005-2006 by Wei-ju Wu\n" +
        "This software is released under the GNU public license.",
        "About...",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void aboutGame() {
    
    GameInfoDialog dialog = new GameInfoDialog(this,
        machine.getGameData().getResources());
    dialog.setVisible(true);
  }
  
  private void editPreferences() {
    
    PreferencesDialog dialog = new PreferencesDialog(this, preferences,
                                                     settings);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }
  
  private DisplaySettings createDisplaySettings(Preferences preferences) {
    
    int stdfontsize = preferences.getInt("stdfontsize", 12);
    int fixedfontsize = preferences.getInt("fixedfontsize", 12);
    int defaultforeground = preferences.getInt("defaultforeground",
        ColorTranslator.UNDEFINED);
    int defaultbackground = preferences.getInt("defaultbackground",
        ColorTranslator.UNDEFINED);
    boolean antialias = preferences.getBoolean("antialias", true);
    
    return new DisplaySettings(stdfontsize, fixedfontsize, defaultbackground,
                               defaultforeground, antialias);    
  }
}
