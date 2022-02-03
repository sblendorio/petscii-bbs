/*
 * $Id: PreferencesDialog.java,v 1.2 2006/05/12 21:36:16 weiju Exp $
 * 
 * Created on 2006/03/27
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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.border.Border;

public class PreferencesDialog extends JDialog implements ActionListener {

  class ColorItem {
    
    int color;
    String name;
    
    public ColorItem(int colornum, String name) {
      
      this.color = colornum;
      this.name = name;
    }
    
    public String toString() { return name; }
  }
  
  private ColorItem[] colors = {
    
    new ColorItem(ColorTranslator.UNDEFINED, ""),
    new ColorItem(ColorTranslator.COLOR_BLACK, "Black"),
    new ColorItem(ColorTranslator.COLOR_RED, "Red"),
    new ColorItem(ColorTranslator.COLOR_GREEN, "Green"),
    new ColorItem(ColorTranslator.COLOR_YELLOW, "Yellow"),
    new ColorItem(ColorTranslator.COLOR_BLUE, "Blue"),
    new ColorItem(ColorTranslator.COLOR_MAGENTA, "Magenta"),
    new ColorItem(ColorTranslator.COLOR_CYAN, "Cyan"),
    new ColorItem(ColorTranslator.COLOR_WHITE, "White"),
    new ColorItem(ColorTranslator.COLOR_MS_DOS_DARKISH_GREY, "Dark Gray"),
  };
  
  /**
   * Serial version uid.
   */
  private static final long serialVersionUID = 1L;
  
  private JSpinner stdfontSpinner;
  private JSpinner fixedfontSpinner;
  private JComboBox foregroundCB;
  private JComboBox backgroundCB;
  private JCheckBox antialiasCB;
  private Preferences preferences;
  private DisplaySettings settings;
    
  public PreferencesDialog(JFrame parent, Preferences preferences,
                           DisplaySettings settings) {
    
    super(parent, "Preferences...", true);
    this.preferences = preferences;
    this.settings = settings;
    
    // Control panel
    GridLayout grid = new GridLayout(5, 2);
    grid.setVgap(3);
    grid.setHgap(3);
    
    JPanel mainpanel = new JPanel(grid);
    
    JLabel stdfontLabel = new JLabel("Size of standard font: ");
    mainpanel.add(stdfontLabel);
    stdfontSpinner = new JSpinner();
    stdfontSpinner.setValue(settings.getStdFontSize());
    mainpanel.add(stdfontSpinner);

    JLabel fixedfontLabel = new JLabel("Size of fixed font: ");
    mainpanel.add(fixedfontLabel);    
    fixedfontSpinner = new JSpinner();
    fixedfontSpinner.setValue(settings.getFixedFontSize());
    mainpanel.add(fixedfontSpinner);
    
    JLabel backgroundLabel = new JLabel("Default background: ");
    mainpanel.add(backgroundLabel);
    backgroundCB = new JComboBox(colors);
    mainpanel.add(backgroundCB);
    preselect(backgroundCB, settings.getDefaultBackground());
    
    JLabel foregroundLabel = new JLabel("Default foreground: ");
    mainpanel.add(foregroundLabel);
    foregroundCB = new JComboBox(colors);
    mainpanel.add(foregroundCB);
    preselect(foregroundCB, settings.getDefaultForeground());

    JLabel antialiasLabel = new JLabel("Antialiased text: ");
    mainpanel.add(antialiasLabel);
    antialiasCB = new JCheckBox();
    antialiasCB.setSelected(settings.getAntialias());
    mainpanel.add(antialiasCB);
    
    // Button panel
    Box lowpanel = new Box(BoxLayout.Y_AXIS);
    lowpanel.add(new JSeparator());
    JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    lowpanel.add(buttonpanel);
    
    JButton okbutton = new JButton("Ok");
    JButton cancelbutton = new JButton("Cancel");
    getRootPane().setDefaultButton(okbutton);
    buttonpanel.add(okbutton);
    buttonpanel.add(cancelbutton);
    okbutton.addActionListener(this);
    cancelbutton.addActionListener(this);
    
    getContentPane().add(mainpanel, BorderLayout.NORTH);
    getContentPane().add(
        new JLabel("<html><body><i>(Note: Changes only take effect after a " +
                   "restart)</i></body></html>"),
                   BorderLayout.CENTER);
    getContentPane().add(lowpanel, BorderLayout.SOUTH);
    
    Border border = BorderFactory.createEmptyBorder(5, 5, 5, 3);
    ((JPanel) getContentPane()).setBorder(border);
    ((BorderLayout) getContentPane().getLayout()).setVgap(5);

    pack();
    
  }
  
  public void actionPerformed(ActionEvent e) {
   
    if (e.getActionCommand().equals("Ok")) {
      
      // Transfer the settings to the user settings only, they will
      // only take effect on the next restart
      int stdfontsize = Integer.parseInt(stdfontSpinner.getValue().toString());
      int fixedfontsize = Integer.parseInt(fixedfontSpinner.getValue().toString());
      int bgcolor = ((ColorItem) backgroundCB.getSelectedItem()).color;
      int fgcolor = ((ColorItem) foregroundCB.getSelectedItem()).color;
      boolean antialias = antialiasCB.isSelected();
      
      preferences.put("stdfontsize", String.valueOf(stdfontsize));
      preferences.put("fixedfontsize", String.valueOf(fixedfontsize));
      preferences.put("defaultbackground", String.valueOf(bgcolor));
      preferences.put("defaultforeground", String.valueOf(fgcolor));
      preferences.put("antialias", antialias ? "on" : "off");
      settings.setSettings(stdfontsize, fixedfontsize, bgcolor, fgcolor, antialias);
      try {
        preferences.flush();
      } catch (BackingStoreException ex) {
        
        ex.printStackTrace();
      }
    }
    dispose();
  }
  
  private void preselect(JComboBox combobox, int value) {
   
    for (int i = 0; i < colors.length; i++) {
      
      if (colors[i].color == value) {
        
        combobox.setSelectedItem(colors[i]);
        break;
      }
    }
  }
}
