/*
 * $Id: MemoryAccess.java,v 1.8 2006/01/07 02:51:31 weiju Exp $
 * 
 * Created on 2005/09/23
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
package org.zmpp.base;

/**
 * This class manages read and write access to the byte array which contains
 * the story file data. It is the only means to read and manipulate the
 * memory map.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface MemoryAccess extends MemoryReadAccess {

  /**
   * Writes an unsigned 16 bit value to the specified address.
   * 
   * @param address the address to write to
   * @param value the value to write
   */
  void writeUnsignedShort(int address, int value);
  
  /**
   * Writes a short value to the memory.
   * 
   * @param address the address
   * @param value the value
   */
  void writeShort(int address, short value);

  /**
   * Writes an unsigned byte value to the specified address.
   * 
   * @param address the address to write to
   * @param value the value to write
   */
  void writeUnsignedByte(int address, short value);
  
  /**
   * Writes a byte value to the specified address.
   * 
   * @param address the address
   * @param value the value
   */
  void writeByte(int address, byte value);
  
  /**
   * Writes an unsigned 32 bit value to the specified address.
   * 
   * @param address the address to write to
   * @param value the value to write
   */
  void writeUnsigned32(int address, long value);
  
  /**
   * Writes the specified unsigned 48 bit value to the specified address.
   * The value is written in 6 consecutive bytes.
   * 
   * @param address the address
   * @param value the value
   */
  void writeUnsigned48(int address, long value);
}
