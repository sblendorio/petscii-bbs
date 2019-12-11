/*
 * $Id: Abbreviations.java,v 1.9 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 2005/09/25
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

import org.zmpp.base.MemoryReadAccess;
import org.zmpp.encoding.ZCharDecoder.AbbreviationsTable;

/**
 * This class represents a view to the abbreviations table. The table
 * starts at the predefined address within the header and contains pointers
 * to ZSCII strings in the memory map. These pointers are word addresses
 * as opposed to all other addresses in the memory map, therefore the
 * actual value has to multiplied by two to get the real address.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class Abbreviations implements AbbreviationsTable {

  /**
   * The memory map.
   */
  private MemoryReadAccess map;
  
  /**
   * The start address of the abbreviations table.
   */
  private int address;
  
  /**
   * Constructor.
   * 
   * @param map the memory map
   * @param address the start address of the abbreviations table
   */
  public Abbreviations(final MemoryReadAccess map, final int address) {
    
    super();
    this.map = map;
    this.address = address;
  }
  
  /**
   * The abbreviation table contains word addresses, so read out the pointer
   * and multiply by two
   * 
   * @param entryNum the entry index in the abbreviations table
   * @return the word address
   */
  public int getWordAddress(final int entryNum) {
    
    return map.readUnsignedShort(address + entryNum * 2) * 2;
  }  
}
