/*
 * $Id: ScreenModel.java,v 1.15 2006/03/03 16:48:09 weiju Exp $
 * 
 * Created on 11/07/2005
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
package org.zmpp.vm;

import org.zmpp.io.OutputStream;


/**
 * This interface defines the access to the screen model.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface ScreenModel {

  static final int CURRENT_WINDOW = -3;
  
  /**
   * Font number for the standard font.
   */
  static final int FONT_NORMAL  = 1;
  
  /**
   * Font number for the character graphics font.
   */
  static final int FONT_CHARACTER_GRAPHICS  = 3;
  
  /**
   * Font number for the fixed pitch font.
   */
  static final int FONT_FIXED   = 4;
  
  static final int TEXTSTYLE_ROMAN          = 0;
  static final int TEXTSTYLE_REVERSE_VIDEO  = 1;
  static final int TEXTSTYLE_BOLD           = 2;
  static final int TEXTSTYLE_ITALIC         = 4;
  static final int TEXTSTYLE_FIXED          = 8;
  
  /**
   * Resets the screen model.
   */
  void reset();
  
  /**
   * Splits the screen into two windows, the upper window will contain
   * linesUpperWindow lines. If linesUpperWindow is 0, the window will
   * be unsplit.
   * 
   * @param linesUpperWindow the number of lines the upper window will have
   */
  void splitWindow(int linesUpperWindow);
  
  /**
   * Sets the active window.
   * 
   * @param window the active window
   */
  void setWindow(int window);
  
  /**
   * Sets the text style.
   * 
   * @param style the text style
   */
  void setTextStyle(int style);
  
  /**
   * Sets the buffer mode.
   * 
   * @param flag true if should be buffered, false otherwise
   */
  void setBufferMode(boolean flag);

  /**
   * Version 4/5: If value is 1, erase from current cursor position to the
   * end of the line.
   *  
   * @param value the parameter
   */
  void eraseLine(int value);
  
  /**
   * Clears the window with the specified number to the background color.
   * If window is -1, the screen is unsplit and the area is cleared.
   * If window is -2, the whole screen is cleared, but the splitting status
   * is retained.
   * 
   * @param window the window number
   */
  void eraseWindow(int window);
  
  /**
   * Moves the cursor in the current window to the specified position.
   * 
   * @param line the line
   * @param column the column
   * @param the window
   */
  void setTextCursor(int line, int column, int window);
  
  /**
   * Retrieves the active window's cursor.
   * 
   * @return the current window's cursor
   */
  TextCursor getTextCursor();
  
  /**
   * Sets the paging mode. This is useful if the input stream is a file.
   * 
   * @param flag true to enable paging, false to disable
   */
  void setPaging(boolean flag);
  
  /**
   * Sets the font in the current window.
   * 
   * @param fontnumber the font number
   * @boolean the previous font number
   */
  int setFont(int fontnumber);
  
  /**
   * Sets the background color.
   * 
   * @param colornumber the color number
   * @param window the window
   */
  void setBackgroundColor(int colornumber, int window);
  
  /**
   * Sets the foreground color.
   * 
   * @param colornumber a color number
   * @param window the window
   */
  void setForegroundColor(int colornumber, int window);
  
  /**
   * Forces a redraw.
   */
  void redraw();
  
  /**
   * Displays the cursor.
   * 
   * @param flag true if display, false for clear
   */
  void displayCursor(boolean flag);
  
  /**
   * Returns the output stream associated with the screen.
   * 
   * @return the output stream
   */
  OutputStream getOutputStream();
  
  /**
   * Wait until this object is initialized.
   */
  void waitInitialized();
  
  /**
   * Reset the internal pagers.
   */
  void resetPagers();
}
