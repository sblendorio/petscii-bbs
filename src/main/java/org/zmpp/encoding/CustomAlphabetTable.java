/*
 * $Id: CustomAlphabetTable.java,v 1.5 2006/04/12 18:00:08 weiju Exp $
 * 
 * Created on 2006/01/16
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

import org.zmpp.base.MemoryReadAccess;

/**
 * If the story file header defines a custom alphabet table, instances
 * of this class are used to retrieve the alphabet characters.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CustomAlphabetTable implements AlphabetTable {

  private static final int ALPHABET_SIZE = 26;
  
  private MemoryReadAccess memaccess;
  private int tableAddress;
 
  /**
   * Constructor.
   * 
   * @param memaccess the memory access object
   * @param address the table address
   */
  public CustomAlphabetTable(final  MemoryReadAccess memaccess,
      final int address) {
    
    super();
    this.memaccess = memaccess;
    tableAddress = address;
  }
  
  /**
   * {@inheritDoc}
   */
  public short getA0Char(final byte zchar) {
    
    if (zchar == 0) {
      return ' ';
    }
    return memaccess.readUnsignedByte(tableAddress
                                      + (zchar - ALPHABET_START));
  }
  
  /**
   * {@inheritDoc}
   */
  public short getA1Char(final byte zchar) {
    
    if (zchar == 0) {
      return ' ';
    }
    return memaccess.readUnsignedByte(tableAddress
                                      + ALPHABET_SIZE
                                      + (zchar - ALPHABET_START));
  }
  
  /**
   * {@inheritDoc}
   */
  public short getA2Char(final byte zchar) {
    
    if (zchar == 0) {
      return ' ';
    }
    if (zchar == 7) {
      return (short) '\n';
    }
    return memaccess.readUnsignedByte(tableAddress + 2 * ALPHABET_SIZE
                                      + (zchar - ALPHABET_START));
  }
  
  /**
   * {@inheritDoc}
   */
  public final byte getA0CharCode(final short zsciiChar) {

    for (int i = ALPHABET_START; i < ALPHABET_START + ALPHABET_SIZE; i++) {
      
      if (getA0Char((byte) i) == zsciiChar) {
        return (byte) i;
      }
    }
    return -1;
  }
  
  /**
   * {@inheritDoc}
   */
  public final byte getA1CharCode(final short zsciiChar) {

    for (int i = ALPHABET_START; i < ALPHABET_START + ALPHABET_SIZE; i++) {
      
      if (getA1Char((byte) i) == zsciiChar) {
        return (byte) i;
      }
    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  public byte getA2CharCode(final short zsciiChar) {

    for (int i = ALPHABET_START; i < ALPHABET_START + ALPHABET_SIZE; i++) {
      
      if (getA2Char((byte) i) == zsciiChar) {
        return (byte) i;
      }
    }
    return -1;
  }
  
  
  /**
   * {@inheritDoc}
   */
  public boolean isAbbreviation(final short zchar) {
   
    return 1 <= zchar && zchar <= 3;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isShift1(final short zchar) {

    return zchar == AlphabetTable.SHIFT_4;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isShift2(final short zchar) {
    
    return zchar == AlphabetTable.SHIFT_5;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isShiftLock(final short zchar) {
    
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isShift(final short zchar) {
    
    return isShift1(zchar) || isShift2(zchar);
  }
}
