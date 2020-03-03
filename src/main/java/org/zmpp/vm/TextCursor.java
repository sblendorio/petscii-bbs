/*
 * $Id: TextCursor.java,v 1.3 2006/01/23 22:49:59 weiju Exp $
 * 
 * Created on 11/22/2005
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

/**
 * This defines the operations that can be performed on a text cursor.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface TextCursor {

  /**
   * Returns the current line.
   * 
   * @return the current line
   */
  int getLine();
  
  /**
   * Returns the current column.
   * 
   * @return the current column
   */
  int getColumn();
  
  /**
   * Sets the current line. A value <= 0 will set the line to 1.
   * 
   * @param line the new current line
   */
  void setLine(int line);
  
  /**
   * Sets the current column. A value <= 0 will set the column to 1.
   * 
   * @param column the new current column
   */
  void setColumn(int column);
  
  /**
   * Sets the new position. Values <= 0 will set the corresponding values
   * to 1.
   * 
   * @param line the new line
   * @param column the new column
   */
  void setPosition(int line, int column);
}
