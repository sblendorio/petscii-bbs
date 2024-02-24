/*
 * Created on 2006/02/23
 * Copyright (c) 2005-2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.windowing;

import org.zmpp.media.ZmppImage;

/**
 * Window 6 interface. V6 windows in the Z-machine are probably the hackiest
 * and trickiest challenge in a Z-machine.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Window6 {

  int PROPERTY_Y_COORD           = 0;
  int PROPERTY_X_COORD           = 1;
  int PROPERTY_Y_SIZE            = 2;
  int PROPERTY_X_SIZE            = 3;
  int PROPERTY_Y_CURSOR          = 4;
  int PROPERTY_X_CURSOR          = 5;
  int PROPERTY_LEFT_MARGIN       = 6;
  int PROPERTY_RIGHT_MARGIN      = 7;
  int PROPERTY_INTERRUPT_ROUTINE = 8;
  int PROPERTY_INTERRUPT_COUNT   = 9;
  int PROPERTY_TEXTSTYLE         = 10;
  int PROPERTY_COLOURDATA        = 11;
  int PROPERTY_FONT_NUMBER       = 12;
  int PROPERTY_FONT_SIZE         = 13;
  int PROPERTY_ATTRIBUTES        = 14;
  int PROPERTY_LINE_COUNT        = 15;

  /**
   * Draws the specified picture at the given position.
   *
   * @param picture the picture data
   * @param y the y coordinate
   * @param x the x coordinate
   */
  void drawPicture(ZmppImage picture, int y, int x);

  /**
   * Clears the area of the specified picture at the given position.
   *
   * @param picture the picture
   * @param y the y coordinate
   * @param x the x coordinate
   */
  void erasePicture(ZmppImage picture, int y, int x);

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
   * @param operation the operation
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
