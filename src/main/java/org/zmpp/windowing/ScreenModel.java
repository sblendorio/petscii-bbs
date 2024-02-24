/*
 * Created on 11/07/2005
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

import org.zmpp.io.OutputStream;

/**
 * This interface defines the access to the screen model.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface ScreenModel {

  int CURRENT_WINDOW = -3;
  int WINDOW_BOTTOM = 0;
  int WINDOW_TOP    = 1;

  /**
   * Font number for the standard font.
   */
  char FONT_NORMAL  = 1;

  /**
   * Font number for the character graphics font.
   */
  char FONT_CHARACTER_GRAPHICS  = 3;

  /**
   * Font number for the fixed pitch font.
   */
  char FONT_FIXED   = 4;

  int TEXTSTYLE_ROMAN          = 0;
  int TEXTSTYLE_REVERSE_VIDEO  = 1;
  int TEXTSTYLE_BOLD           = 2;
  int TEXTSTYLE_ITALIC         = 4;
  int TEXTSTYLE_FIXED          = 8;

  /**
   * Color definitions.
   */
  int UNDEFINED                   = -1000;
  int COLOR_UNDER_CURSOR          = -1;
  int COLOR_CURRENT               =  0;
  int COLOR_DEFAULT               =  1;
  int COLOR_BLACK                 =  2;
  int COLOR_RED                   =  3;
  int COLOR_GREEN                 =  4;
  int COLOR_YELLOW                =  5;
  int COLOR_BLUE                  =  6;
  int COLOR_MAGENTA               =  7;
  int COLOR_CYAN                  =  8;
  int COLOR_WHITE                 =  9;
  int COLOR_MS_DOS_DARKISH_GREY   =  10;

  /**
   * Returns the current annotation of the bottom window.
   * @return the annotation in the bottom window
   */
  TextAnnotation getBottomAnnotation();

  /**
   * Returns the current annotation of the top window.
   * @return the annotation in the top window
   */
  TextAnnotation getTopAnnotation();

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
   * @param window the active window
   */
  void setWindow(int window);

  /**
   * Returns the active window.
   * @return the active window
   */
  int getActiveWindow();

  /**
   * Sets the text style.
   * @param style the text style
   */
  void setTextStyle(int style);

  /**
   * Sets the buffer mode.
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
   * @param window the window
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
  //void setPaging(boolean flag);

  /**
   * Sets the font in the current window.
   *
   * @param fontnumber the font number
   * @return the previous font number
   */
  char setFont(char fontnumber);

  /**
   * Sets the background color.
   *
   * @param colornumber the color number
   * @param window the window
   */
  void setBackground(int colornumber, int window);

  /**
   * Sets the foreground color.
   *
   * @param colornumber a color number
   * @param window the window
   */
  void setForeground(int colornumber, int window);

  /**
   * Returns the output stream associated with the screen.
   *
   * @return the output stream
   */
  OutputStream getOutputStream();
}
