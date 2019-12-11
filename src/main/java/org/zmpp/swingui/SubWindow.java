/*
 * $Id: SubWindow.java,v 1.41 2007/03/25 04:19:09 weiju Exp $
 * 
 * Created on 2005/11/19
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

import java.awt.Color;
import java.awt.Font;

import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.TextCursor;

/**
 * The class SubWindow manages a sub window within the screen model.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class SubWindow implements CursorWindow {

  private Viewport viewport;
  private TextCursor cursor;
  
  private int top;
  private int height;
  private int foreground;
  private int background;
 
  private int fontnumber;
  private Font font;
  private boolean isReverseVideo;
  
  private String name;
 
  /**
   * Constructor.
   * 
   * @param parentComponent the parent component
   * @param editor the line editor
   * @param canvas the canvas
   * @param name the window name
   */
  public SubWindow(Viewport viewport, String name) {
    
    this.viewport = viewport;
    this.cursor = new TextCursorImpl(this);
    this.name = name;
  }
  
  public int getFontNumber() { return fontnumber; }
  
  public void setFontNumber(int fontnumber) { this.fontnumber = fontnumber; }
  
  /**
   * Sets the reverse video text mode.
   * 
   * @param flag true or false to activate or deactivate reverse video
   */
  public void setReverseVideo(boolean flag) { isReverseVideo = flag; }
  
  /**
   * Returns the reverse video status.
   * 
   * @return true if reverse video, false otherwise
   */
  public boolean isReverseVideo() { return isReverseVideo; }

  /**
   * Access to this window's cursor.
   * 
   * @return the cursor
   */
  public TextCursor getCursor() {
   
    return cursor;
  }
  
  public void setCursorPosition(int line, int column) {
    cursor.setPosition(line, column);
  }
  
  public int getHeight() { return height; }
  
  public int getTop() { return top; }
  
  public void setVerticalBounds(int top, int height) {
    this.top = top;
    this.height = height;
    sizeUpdated();
  }
  
  public void resize(int numLines) {
    height = getCanvas().getFontHeight(font) * numLines;
    sizeUpdated();
  }
  
  /**
   * Sets this window's current font.
   * @param aFont the current font
   */
  public void setFont(Font aFont) { font = aFont; }
  
  /**
   * Returns this window's current font.
   * @return the current font
   */
  public Font getFont() { return font; }
  
  /**
   * {@inheritDoc}
   */
  public void clear() {
    clipToCurrentBounds();
    getCanvas().fillRect(getBackgroundColor(), 0, getTop(),
                         getCanvas().getWidth(), height);
    resetCursorToHome();
  }
  
  public void eraseLine() {
    Canvas canvas = getCanvas();
    int currentX = getCurrentX();
    clipToCurrentBounds();
    canvas.fillRect(getBackgroundColor(), currentX,
                    getCurrentY() - canvas.getFontAscent(font),
                    canvas.getWidth() - currentX,
                    canvas.getFontHeight(font));
  }
  
  public void setBackground(int colornum) { background = colornum; }
  
  protected Color getBackgroundColor() {
  
    if (background == ColorTranslator.COLOR_UNDER_CURSOR) {
      
      return getCanvas().getColorAtPixel(getCurrentX(), getCurrentY());
    }
    return ColorTranslator.getInstance().translate(background,
        viewport.getDefaultBackground());
  }
  
  public void setForeground(int colornum) { foreground = colornum; }
  
  protected Color getForegroundColor() {
    
    if (foreground == ColorTranslator.COLOR_UNDER_CURSOR) {
      
      return getCanvas().getColorAtPixel(getCurrentX(), getCurrentY());
    }
    return ColorTranslator.getInstance().translate(foreground,
        viewport.getDefaultForeground());
  }
  
  /**
   * This is the function that does the actual printing to the screen.
   * 
   * @param str a string to pring
   */
  protected void printString(String str) {

    //System.out.printf("printString(), %s: '%s'\n", name, str);
    Canvas canvas = getCanvas();
    int width = canvas.getWidth();
    int lineLength = width;
    
    WordWrapper wordWrapper =
      new WordWrapper(lineLength, canvas, font, isBuffered());
    String[] lines = wordWrapper.wrap(getCurrentX(), str);
    printLines(lines);  
  }
 
  public String toString() {
    
    return "[" + name + "]";
  }

  public void drawCursor(boolean flag) {
    
    Canvas canvas = getCanvas();
    int meanCharWidth = canvas.getCharWidth(font, '0');    
    
    clipToCurrentBounds();
    canvas.fillRect(flag ? getForegroundColor() : getBackgroundColor(),
                    getCurrentX(), getCurrentY() - canvas.getFontAscent(font),
                    meanCharWidth, canvas.getFontHeight(font));
  }
  
  public void backspace(char c) {
    
    Canvas canvas = getCanvas();
    int charWidth = canvas.getCharWidth(font, c);
    
    // Clears the text under the cursor
    clipToCurrentBounds();
    canvas.fillRect(getBackgroundColor(), getCurrentX() - charWidth,
                    getCurrentY() - canvas.getFontAscent(font), charWidth,
                    canvas.getFontHeight(font));
    cursor.setColumn(cursor.getColumn() - 1);
  }

  /**
   * Scrolls the window if necessary.
   */
  abstract protected void scrollIfNeeded();

  // **********************************************************************
  // ***** Protected methods
  // **************************************
  
  protected void newline() {
    
    //System.out.println("newline()");
    cursor.setLine(cursor.getLine() + 1);
    cursor.setColumn(1);
  }
  
  protected Canvas getCanvas() {
    return viewport.getCanvas();
  }
  
  protected LineEditor getEditor() {
    return viewport.getLineEditor();
  }
  
  protected ScreenModel getScreen() {
    return (ScreenModel) viewport;
  }
  
  protected Viewport getViewport() {
    return viewport;
  }
  
  protected Color getTextBackground() {
    return isReverseVideo ? getForegroundColor() : getBackgroundColor();
  }
  
  protected Color getTextColor() {
    return isReverseVideo ? getBackgroundColor() : getForegroundColor();
  }
  
  protected void printLine(String line, Color textbackColor,
                           Color textColor) {

    clipToCurrentBounds();
    Canvas canvas = getCanvas();
    canvas.fillRect(textbackColor, getCurrentX(),
                    getCurrentY() - canvas.getFontHeight(font)
                    + canvas.getFontDescent(font),
                    canvas.getStringWidth(font, line),
                    canvas.getFontHeight(font));
    canvas.drawString(textColor, font, getCurrentX(), getCurrentY(), line);
    cursor.setColumn(cursor.getColumn() + line.length());
  }
  
  public void flushBuffer() {
    // default implementation is empty
  }
  
  // ************************************************************************
  // ******* Abstract methods
  // *************************************************
  
  /**
   * Sets the buffer mode.
   * 
   * @param flag the buffer mode flag
   */
  public abstract void setBufferMode(boolean flag);
  
  /**
   * Returns the buffer mode.
   * 
   * @return the buffer mode
   */
  public abstract boolean isBuffered();

  /**
   * Sets the paging flag. This feature must be available to be controlled
   * from within the core (file input for replaying recorded sessions).
   * 
   * @param flag true to enable paging, false otherwise
   */
  public abstract void setPagingEnabled(boolean flag);
  
  /**
   * Returns the status of the paging flag.
   * 
   * @return the paging status
   */
  public abstract boolean isPagingEnabled();
  
  /**
   * Resets the cursor to its home position.
   */
  public abstract void resetCursorToHome();  

  /**
   * Resets the internal pager.
   */
  public abstract void resetPager();

  protected abstract void sizeUpdated();
  
  protected abstract int getCurrentX();
  
  protected abstract int getCurrentY();
  
  // ************************************************************************
  // ******* Private methods
  // *************************************************
  
  private void printLines(String lines[]) {
    
    Color textColor = getTextColor();
    Color textbackColor = getTextBackground();    
    
    // This is a feature that is not specified, but it is supported by
    // DOS Frotz
    if ((getFont().getStyle() & Font.BOLD) > 0 && !isReverseVideo) {

      textColor = textColor.brighter();
    }
        
    for (int i = 0; i < lines.length; i++) {

      String line = lines[i];
      printLine(line, textbackColor, textColor);
      
      if (endsWithNewLine(line)) {
        newline();
      }
    }
    // Note: this is a patch for the issue that Fredrik revealed, the
    // cursor being out of the visible area
    scrollIfNeeded();
  }  
  
  private static boolean endsWithNewLine(String str) {
  
    return str.length() > 0 && str.charAt(str.length() - 1) == '\n';
  }
  
  protected void clipToCurrentBounds() {
    
    Canvas canvas = getCanvas();
    canvas.setClip(0, top, canvas.getWidth(), height);
  }
}
