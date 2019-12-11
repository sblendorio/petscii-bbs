/*
 * $Id: CursorWindow.java,v 1.4 2006/05/12 21:38:32 weiju Exp $
 * 
 * Created on 2006/02/17
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

/**
 * This interface reduces the dependency between cursor and window and
 * facilitates testing.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface CursorWindow {

  /**
   * Clears the current window.
   */
  void clear();

  /**
   * This method is called after a set_cursor instruction to give the  window
   * the opportunity to update its internal coordinates.
   */
  void updateCursorCoordinates();

  /**
   * A backspace needs to be rendered. The previousChar specifies the previous
   * character, which is necessary if the window does not buffer the input.
   * The character needs to be encoded in unicode.
   * 
   * @param previousChar the previous character.
   */
  void backspace(char previousChar);
  
  /**
   * Prints the character. The delegation point for an output stream.
   * 
   * @param c the character
   * @param isInput true if input stream
   */
  void printChar(char c, boolean isInput);
  
  /**
   * Flushes a buffer if there is one. Delegation point for an output stream.
   */
  void flushBuffer();
}
