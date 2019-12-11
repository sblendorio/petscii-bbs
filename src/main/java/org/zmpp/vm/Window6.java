/*
 * $Id: Window6.java,v 1.5 2006/05/16 18:05:16 weiju Exp $
 * 
 * Created on 2006/02/23
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

import org.zmpp.blorb.BlorbImage;

public interface Window6 {

  static final int PROPERTY_Y_COORD           = 0;
  static final int PROPERTY_X_COORD           = 1;
  static final int PROPERTY_Y_SIZE            = 2;
  static final int PROPERTY_X_SIZE            = 3;
  static final int PROPERTY_Y_CURSOR          = 4;
  static final int PROPERTY_X_CURSOR          = 5;
  static final int PROPERTY_LEFT_MARGIN       = 6;
  static final int PROPERTY_RIGHT_MARGIN      = 7;
  static final int PROPERTY_INTERRUPT_ROUTINE = 8;
  static final int PROPERTY_INTERRUPT_COUNT   = 9;
  static final int PROPERTY_TEXTSTYLE         = 10;
  static final int PROPERTY_COLOURDATA        = 11;
  static final int PROPERTY_FONT_NUMBER       = 12;
  static final int PROPERTY_FONT_SIZE         = 13;
  static final int PROPERTY_ATTRIBUTES        = 14;
  static final int PROPERTY_LINE_COUNT        = 15;
  
  /**
   * Draws the specified picture at the given position.
   * 
   * @param picture the picture data
   * @param y the y coordinate
   * @param x the x coordinate
   */
  void drawPicture(BlorbImage picture, int y, int x);
  
  /**
   * Clears the area of the specified picture at the given position.
   * 
   * @param picture the picture
   * @param y the y coordinate
   * @param x the x coordinate
   */
  void erasePicture(BlorbImage picture, int y, int x);
  
  /**
   * Moves the window to the specified coordinates in pixels, (1, 1)
   * being the top left.
   * 
   * @param y the y coordinate
   * @param x the x coordinate
   */
  void move(int y, int x);
  
  /**
   * Sets window size in pixels.
   * 
   * @param height the height in pixels
   * @param width the width in pixels
   */
  void setSize(int height, int width);
  
  /**
   * Sets the window style.
   * The <i>styleflags</i> parameter is a bitmask specified as follows:
   * - Bit 0: keep text within margins
   * - Bit 1: scroll when at bottom
   * - Bit 2: copy text to transcript stream (stream 2)
   * - Bit 3: word wrapping
   * 
   * The <i>operation</i> parameter is specified as this:
   * - 0: set style flags to the specified mask
   * - 1: set the bits supplied
   * - 2: clear the bits supplied
   * - 3: reverse the bits supplied 
   * 
   * @param styleflags the style flags
   * @param op the operations
   */
  void setStyle(int styleflags, int operation);
  
  /**
   * Sets the window margins in pixels. If the cursor is overtaken by the
   * new margins, set it to the new left margin.
   * 
   * @param left the left margin
   * @param right the right margin
   */
  void setMargins(int left, int right);

  /**
   * Returns the specified window property.
   * 0  y coordinate    6   left margin size            12  font number
   * 1  x coordinate    7   right margin size           13  font size
   * 2  y size          8   newline interrupt routine   14  attributes
   * 3  x size          9   interrupt countdown         15  line count
   * 4  y cursor        10  text style
   * 5  x cursor        11  colour data
   * 
   * @param propertynum the property number
   * @return the property value
   */
  int getProperty(int propertynum);
  
  /**
   * Sets the specified window property.
   * 
   * @param propertynum the property number
   * @param value the value
   */
  void putProperty(int propertynum, short value);
  
  /**
   * Scrolls the window by the specified amount of pixels, negative values
   * scroll down, positive scroll up.
   * 
   * @param pixels the number of pixels
   */
  void scroll(int pixels);
}
