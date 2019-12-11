/*
 * $Id: BottomWindow.java,v 1.16 2007/03/25 04:19:09 weiju Exp $
 * 
 * Created on 2006/01/23
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

/**
 * This class implements the lower window of the standard Z-machine screen
 * model. It extends on the base functionality defined in its super class,
 * and is much more complex than TopWindow, because it supports paging,
 * buffering and proportional font. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BottomWindow extends SubWindow {

  private boolean isBuffered;
  private boolean isPaged;
  private int verticalTextPixelsPrinted;
  private int currentX;
  private int currentY;
  private int lineHeight;
  private StringBuilder streambuffer;
  
  /**
   * Constructor.
   * 
   * @param screen the screen model
   * @param editor the line editor
   * @param canvas the canvas to draw to
   */
  public BottomWindow(Viewport viewport) {
    
    super(viewport, "BOTTOM");    
    setBufferMode(true);
    setPagingEnabled(true);
    streambuffer = new StringBuilder();
  }
  
  /**
   * {@inheritDoc}
   */
  public void flushBuffer() {

    // save some unnecessary flushes
    if (streambuffer.length() > 0) {
      
      printString(streambuffer.toString());
      streambuffer = new StringBuilder();
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isBuffered() { return isBuffered; }
  
  /**
   * {@inheritDoc}
   */
  public void setPagingEnabled(boolean flag) { isPaged = flag; }
  
  /**
   * {@inheritDoc}
   */
  public boolean isPagingEnabled() { return isPaged; }
  
  /**
   * {@inheritDoc}
   */
  public void setBufferMode(boolean flag) { isBuffered = flag; }
    
  /**
   * {@inheritDoc}
   */
  public void resetCursorToHome() {
    
    //System.out.println("resetCursorToHome()");
    // We calulate an available height with a correction amount
    // of fm.getMaxDescent() to reserve enough scrolling space
    setCursorPosition(getAvailableLines(), 1);
    
    currentY = getTop() + getHeight() - getCanvas().getFontDescent(getFont());
  }

  /**
   * Returns the available lines.
   * 
   * @return the available lines
   */
  private int getAvailableLines() {
  
    int descent = getCanvas().getFontDescent(getFont());
    int fontHeight = getCanvas().getFontHeight(getFont());
    return (getHeight() - descent) / fontHeight;
  }
  
  /**
   * Check if paging should be done.
   */
  private void handlePaging() {
    
    if (isPaged && pageMinusOneLinePrinted()) {
      
      doMeMore();
    }
  }
  
  private boolean pageMinusOneLinePrinted() {
    
    return (verticalTextPixelsPrinted
           + getCanvas().getFontDescent(getFont())
           + getCanvas().getFontHeight(getFont()))
           >= getHeight();
  }

  /**
   * Wait for key press.
   */
  private void doMeMore() {
    
    // Invoke the super method, which does not handle paging
    printLineNonPaged("\n<MORE> (Press key to continue)", getTextBackground(),
                      getTextColor());
    
    getScreen().redraw();
    
    // Do this exclusively to have better thread control, we need to stay
    // in the application thread
    getEditor().setInputMode(true, true);
    getEditor().nextZsciiChar();
    resetCursorToHome();
    eraseLine();
    getEditor().setInputMode(false, true);
    resetPager();
  }
  
  /**
   * Updates the page size.
   */
  protected void sizeUpdated() { }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void newline() {
    
    //System.out.println("newline()");
    super.newline();
    scrollIfNeeded();
    currentX = 0;
    
    // We need to remember the line height to calculate the next y position
    verticalTextPixelsPrinted += lineHeight;
    currentY += lineHeight;
    lineHeight = 0;
  }

  /**
   * {@inheritDoc}
   */
  public void printChar(char c, boolean isInput) {

    if (isInput || !isBuffered()) {
      
      printString(String.valueOf(c));
      
    } else {
      
      streambuffer.append(c);
    }
  } 
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void printLine(String line, Color textbackColor,
      Color textColor) {
    
    //System.out.printf("printLine(): '%s' current x: %d -> ", line, currentX);
    handlePaging();
    scrollIfNeeded();
    super.printLine(line, textbackColor, textColor);
    
    // Every elementary print instruction adds to the current line
    currentX += getCanvas().getStringWidth(getFont(), line);
    //System.out.printf("current x: %d\n", currentX);

    // Adjust the maximum line height
    lineHeight = Math.max(lineHeight, getCanvas().getFontHeight(getFont()));
  }

  private void printLineNonPaged(String line, Color textbackColor,
                                 Color textColor) {
    
    scrollIfNeeded();
    super.printLine(line, textbackColor, textColor);
    
    // Every elementary print instruction adds to the current line
    currentX += getCanvas().getStringWidth(getFont(), line);

    // Adjust the maximum line height
    lineHeight = Math.max(lineHeight, getCanvas().getFontHeight(getFont()));
  }
  
  /**
   * {@inheritDoc}
   */
  public void resetPager() {  
    verticalTextPixelsPrinted = 0;
  }

  /**
   * {@inheritDoc}
   */
  protected int getCurrentX() { return currentX; }

  /**
   * {@inheritDoc}
   */
  protected int getCurrentY() { return currentY; }

  /**
   * {@inheritDoc}
   */
  private void setCurrentY(int value) { currentY = value; }

  /**
   * {@inheritDoc}
   */
  @Override
  public void backspace(char c) {
    super.backspace(c);   
    currentX -= getCanvas().getCharWidth(getFont(), c);
    if (currentX < 0) currentX = 0;
  }
  
  /**
   * {@inheritDoc}
   */
  public void updateCursorCoordinates() {
    
    Canvas canvas = getCanvas();
    Font font = getFont();
    int currentLine = getCursor().getLine();
    int currentColumn = getCursor().getColumn();
    
    currentX = (currentColumn - 1) * canvas.getCharWidth(font, '0');
    currentY = getTop() + (currentLine - 1) * canvas.getFontHeight(font)
               + (canvas.getFontHeight(font) - canvas.getFontDescent(font));
  }
  
  /**
   * {@inheritDoc}
   */
  protected void scrollIfNeeded() {
    //System.out.printf("scrollIfNeeded(), current y: %d, window bottom: %d" +
    //    ", font descent: %d, font height: %d\n", getCurrentY(),
    //    (getTop() + getHeight()), getCanvas().getFontDescent(getFont()),
    //    getCanvas().getFontHeight(getFont()));
    int fontDescent = getCanvas().getFontDescent(getFont());
    int fontHeight = getCanvas().getFontHeight(getFont());
    clipToCurrentBounds();
    
    // We calulate an available height with a correction amount
    // of fontDescent to reserve enough scrolling space
    while (getCurrentY() > (getTop() + getHeight() - fontDescent)) {
      
      getCanvas().scrollUp(getBackgroundColor(), getFont(),
                           getTop(), getHeight());
      getCursor().setLine(getCursor().getLine() - 1);
      setCurrentY(getCurrentY() - fontHeight);
    }
  }
}
