/*
 * $Id: AlphabetTableV2.java,v 1.3 2006/04/12 02:04:29 weiju Exp $
 * 
 * Created on 2006/01/18
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
 * An alphabet table in a V2 story file behaves "almost like" the default
 * alphabet table, in that they have the same characters in the alphabets.
 * There are however two differences: It only supports one abbreviation code
 * and it supports shift-lock. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AlphabetTableV2 extends DefaultAlphabetTable {
  
  /**
   * {@inheritDoc}
   */
  public boolean isAbbreviation(final short zchar) {

    return zchar == 1;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isShift1(final short zchar) {

    return zchar == SHIFT_2 || zchar == SHIFT_4;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isShift2(final short zchar) {
    
    return zchar == SHIFT_3 || zchar == SHIFT_5;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isShiftLock(final short zchar) {
    
    return zchar == SHIFT_4 || zchar == SHIFT_5;
  }
}
