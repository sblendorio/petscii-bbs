/*
 * $Id: ScreenModel6.java,v 1.5 2006/05/30 17:25:33 weiju Exp $
 * 
 * Created on 2006/02/22
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


public interface ScreenModel6 extends ScreenModel {

  /**
   * Restricts the mouse pointer to the specified window.
   * 
   * @param window the window
   */
  void setMouseWindow(int window);

  /**
   * Returns the specified window.
   * 
   * @param window the window
   * @return the window
   */
  Window6 getWindow(int window);

  /**
   * Returns the currently selected window.
   * 
   * @return the currently selected window
   */
  Window6 getSelectedWindow();

  /**
   * Returns the total screen model width.
   * 
   * @return the screen width
   */
  int getWidth();
  
  /**
   * Returns the total screen model height.
   * 
   * @return the screen height
   */
  int getHeight();

  /**
   * Instructs the screen model to set the width of the current string
   * to the header.
   * 
   * @param zchars the z character array
   */
  void setTextWidthInUnits(short[] zchars);
  
  /**
   * Reads the current mouse data into the specified array.
   * 
   * @param array the array address
   */
  void readMouse(int array);
}
