/**
 * $Id: DisplaySettings.java,v 1.2 2006/05/12 21:37:38 weiju Exp $
 */
package org.zmpp.swingui;

public class DisplaySettings {

  private int stdFontSize;
  private int fixedFontSize;
  private int defaultForeground;
  private int defaultBackground;
  private boolean antialias;
  
  public DisplaySettings(int stdFontSize, int fixedFontSize,
      int defaultBackground, int defaultForeground, boolean antialias) {

    setSettings(stdFontSize, fixedFontSize, defaultBackground,
        defaultForeground, antialias);
  }
  
  public int getStdFontSize() { return stdFontSize; }
  public int getFixedFontSize() { return fixedFontSize; }
  public int getDefaultBackground() { return defaultBackground; }
  public int getDefaultForeground() { return defaultForeground; }
  public boolean getAntialias() { return antialias; }
  
  public void setSettings(int stdFontSize, int fixedFontSize,
      int defaultBackground, int defaultForeground, boolean antialias) {
    
    this.stdFontSize = stdFontSize;
    this.fixedFontSize = fixedFontSize;
    this.defaultBackground = defaultBackground;
    this.defaultForeground = defaultForeground;
    this.antialias = antialias;
  }
}
