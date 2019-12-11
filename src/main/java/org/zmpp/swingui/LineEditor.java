/*
 * $Id: LineEditor.java,v 1.16 2006/05/30 17:26:40 weiju Exp $
 * 
 * Created on 2005/11/07
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

import java.awt.event.MouseEvent;

/**
 * An interface to define line editing functionality.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface LineEditor {

  /**
   * Sets the input mode flag.
   * 
   * @param flag the input mode
   * @param flushbuffer if true, the input buffer is flushed
   */
  void setInputMode(boolean flag, boolean flushbuffer);
  
  /**
   * Cancels the input.
   */
  void cancelInput();
  
  /**
   * Retrieves the next character from the editor.
   * 
   * @return the next character
   */
  short nextZsciiChar();
  
  /**
   * Returns the input mode.
   * 
   * @return the input mode
   */
  boolean isInputMode();
  
  /**
   * Returns the last mouse event.
   * 
   * @return the last mouse event0
   */
  MouseEvent getLastMouseEvent();
}
