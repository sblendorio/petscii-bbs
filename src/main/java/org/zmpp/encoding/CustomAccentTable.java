/*
 * $Id: CustomAccentTable.java,v 1.5 2006/04/12 18:00:08 weiju Exp $
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

import org.zmpp.base.MemoryReadAccess;

/**
 * This accent table is used in case that there is an extension header
 * that specifies that accent table.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CustomAccentTable implements AccentTable {

  /**
   * The memory access object.
   */
  private MemoryReadAccess memaccess;
  
  /**
   * The table adddress.
   */
  private int tableAddress;

  /**
   * Constructor.
   * 
   * @param memaccess a memory access object
   * @param address the table address
   */
  public CustomAccentTable(final MemoryReadAccess memaccess,
      final int address) {
  
    super();
    this.memaccess = memaccess;
    this.tableAddress = address;
  }
  
  /**
   * {@inheritDoc}
   */
  public int getLength() {
    
    int result = 0;
    if (tableAddress > 0) {
      
      result = memaccess.readUnsignedByte(tableAddress);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public short getAccent(final int index) {
    
    short result = '?';
    
    if (tableAddress > 0) {
      
      result = memaccess.readShort(tableAddress + (index * 2) + 1);
    }
    return result;
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
