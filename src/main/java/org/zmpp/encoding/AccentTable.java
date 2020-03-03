/*
 * $Id: AccentTable.java,v 1.3 2006/02/02 01:03:24 weiju Exp $
 * 
 * Created on 2005/01/15
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
package org.zmpp.encoding;

/**
 * Accent tables are used by ZsciiEncoding objects to translate encoded
 * Z characters to unicode characters.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface AccentTable {

  /**
   * Returns the length of the table.
   * 
   * @return the length of the table
   */
  int getLength();
  
  /**
   * Returns the accent at the specified index.
   * 
   * @param index the index
   * @return the accent
   */
  short getAccent(int index);
  
  /**
   * Converts the accent at the specified index to lower case and returns
   * the index of that character.
   * 
   * @param index the character
   * @return the index of the corresponding lower case
   */
  int getIndexOfLowerCase(int index);
}
