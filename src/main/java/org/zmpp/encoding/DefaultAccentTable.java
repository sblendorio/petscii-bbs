/*
 * $Id: DefaultAccentTable.java,v 1.4 2006/04/12 18:00:08 weiju Exp $
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


public class DefaultAccentTable implements AccentTable {

  public static short[] STANDARD_TRANSLATION_TABLE = {
    
    '\u00e4', '\u00f6', '\u00fc', '\u00c4', '\u00d6', '\u00dc', '\u00df',
    '\u00bb', '\u00ab',
    '\u00eb', '\u00ef', '\u00ff', '\u00cb', '\u00cf',
    '\u00e1', '\u00e9', '\u00ed', '\u00f3', '\u00fa', '\u00fd',
    '\u00c1', '\u00c9', '\u00cd', '\u00d3', '\u00da', '\u00dd',
    '\u00e0', '\u00e8', '\u00ec', '\u00f2', '\u00f9',
    '\u00c0', '\u00c8', '\u00cc', '\u00d2', '\u00d9',
    '\u00e2', '\u00ea', '\u00ee', '\u00f4', '\u00fb',
    '\u00c2', '\u00ca', '\u00ce', '\u00d4', '\u00db',
    '\u00e5', '\u00c5', '\u00f8', '\u00d8',
    '\u00e3', '\u00f1', '\u00f5', '\u00c3', '\u00d1', '\u00d5',
    '\u00e6', '\u00c6', '\u00e7', '\u00c7',
    '\u00fe', '\u00fd', '\u00f0', '\u00d0',
    '\u00a3', '\u0153', '\u0152', '\u00a1', '\u00bf'    
  };
  
  /**
   * {@inheritDoc}
   */
  public int getLength() {
  
    return STANDARD_TRANSLATION_TABLE.length;
  }
  
  /**
   * {@inheritDoc}
   */
  public short getAccent(final int index) {
    
    return STANDARD_TRANSLATION_TABLE[index];
  }
  
  /**
   * {@inheritDoc}
   */
  public int getIndexOfLowerCase(final int index) {

    final char c = (char) getAccent(index);
    final char lower = Character.toLowerCase(c);
    final int length = getLength();
    for (int i = 0; i < length; i++) {
      
      if (getAccent(i) == lower) {
        return i;
      }
    }
    return index;
  }
}
