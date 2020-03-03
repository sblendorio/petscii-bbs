/*
 * $Id: DefaultMemoryAccess.java,v 1.4 2006/04/12 02:04:30 weiju Exp $
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
 * This class is the default implementation for MemoryAccess.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DefaultMemoryAccess implements MemoryAccess {

  /**
   * The data array containing the story file.
   */
  private byte[] data;  
  
  /**
   * Constructor.
   * 
   * @param data the story file data
   */
  public DefaultMemoryAccess(final byte[] data) {
    
    super();
    this.data = data;    
  }
  
  /**
   * {@inheritDoc}
   */
  public long readUnsigned32(final int address) {
  
    return (data[address] & 0xff) << 24 | (data[address + 1] & 0xff) << 16
           | (data[address + 2] & 0xff) << 8 | (data[address + 3] & 0xff);
  }
  
  /**
   * {@inheritDoc}
   */
  public long readUnsigned48(final int address) {
    
    return (data[address + 0] & 0xff) << 40
         | (data[address + 1] & 0xff) << 32
         | (data[address + 2] & 0xff) << 24
         | (data[address + 3] & 0xff) << 16
         | (data[address + 4] & 0xff) << 8
         | (data[address + 5] & 0xff)
         ;
  }
  
  /**
   * {@inheritDoc}
   */
  public void writeUnsigned48(final int address, final long value) {
    
    data[address + 0] = (byte) ((value & 0xff0000000000l) >> 40);
    data[address + 1] = (byte) ((value & 0x00ff00000000l) >> 32);
    data[address + 2] = (byte) ((value & 0x0000ff000000l) >> 24);
    data[address + 3] = (byte) ((value & 0x000000ff0000l) >> 16);
    data[address + 4] = (byte) ((value & 0x00000000ff00l) >> 8);
    data[address + 5] = (byte)  (value & 0x0000000000ffl);
  }
  
  /**
   * {@inheritDoc}
   */
  public int readUnsignedShort(final int address) {
    
    return (data[address] & 0xff) << 8 | (data[address + 1] & 0xff);
  }
  
  /**
   * {@inheritDoc}
   */
  public short readShort(final int address) {
    
    return (short) (data[address] << 8 | (data[address + 1] & 0xff));
  }
  
  /**
   * {@inheritDoc}
   */
  public short readUnsignedByte(final int address) {
    
    return (short) (data[address] & 0xff);
  }
  
  /**
   * {@inheritDoc}
   */
  public byte readByte(final int address) {
    
    return data[address];
  }
  
  /**
   * Writes an unsigned 16 bit value to the specified address.
   * 
   * @param address the address to write to
   * @param value the value to write
   */
  public void writeUnsignedShort(final int address, final int value) {
    
    data[address] = (byte) ((value & 0xff00) >> 8);
    data[address + 1] = (byte) (value & 0xff);
  }
  
  /**
   * Writes a short value to the memory.
   * 
   * @param address the address
   * @param value the value
   */
  public void writeShort(final int address, final short value) {
    
    data[address] = (byte) ((value & 0xff00) >>> 8);
    data[address + 1] = (byte) (value & 0xff);
  }
  
  /**
   * Writes an unsigned byte value to the specified address.
   * 
   * @param address the address to write to
   * @param value the value to write
   */
  public void writeUnsignedByte(final int address, final short value) {
    
    data[address] = (byte) (value & 0xff);
  }
  
  /**
   * Writes a byte value to the specified address.
   * 
   * @param address the address
   * @param value the value
   */
  public void writeByte(final int address, final byte value) {
    
    data[address] = value;
  }
  
  /**
   * Writes an unsigned 32 bit value to the specified address.
   * 
   * @param address the address to write to
   * @param value the value to write
   */
  public void writeUnsigned32(final int address, final long value) {
    
    data[address] = (byte) ((value & 0xff000000) >> 24);
    data[address + 1] = (byte) ((value & 0x00ff0000) >> 16);
    data[address + 2] = (byte) ((value & 0x0000ff00) >> 8);
    data[address + 3] = (byte) (value & 0x000000ff);
  }
  
}
