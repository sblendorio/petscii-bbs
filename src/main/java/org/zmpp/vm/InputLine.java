/*
 * $Id: InputLine.java,v 1.1 2006/03/16 19:14:27 weiju Exp $
 * 
 * Created on 2006/03/10
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

import java.util.List;

/**
 * This interface is used from CommandHistory to manipulate the input line.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface InputLine {

  /**
   * Deletes the previous character in the input line.
   * 
   * @param inputbuffer the input buffer
   * @param pointer the pointer
   * @return the new pointer after delete
   */
  int deletePreviousChar(List<Short> inputbuffer, int pointer);

  /**
   * Adds a character to the current input line.
   * 
   * @param inputbuffer the input buffer
   * @param textbuffer the textbuffer address
   * @param pointer the pointer address
   * @param zsciiChar the character to add
   * @return the new pointer
   */
  int addChar(List<Short> inputbuffer, int textbuffer, int pointer, short zsciiChar);
}
