/*
 * $Id: ModernZObject.java,v 1.5 2006/04/12 18:00:17 weiju Exp $
 * 
 * Created on 2006/03/05
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

import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.ZCharDecoder;

/**
 * This class implements Z machine objects for story file versions starting
 * from version 4.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ModernZObject extends AbstractZObject {

  private static final int OFFSET_PARENT        = 6;
  private static final int OFFSET_SIBLING       = 8;
  private static final int OFFSET_CHILD         = 10;
  private static final int OFFSET_PROPERTYTABLE = 12;
  
  class ModernPropertyTable extends PropertyTable {
  
    /**
     * {@inheritDoc}
     */
    protected int getNumPropertySizeBytes(final int address) {
      
      // if bit 7 is set, there are two size bytes, one otherwise
      final short first = getMemoryAccess().readUnsignedByte(address);
      return ((first & 0x80) > 0) ? 2 : 1;
    }

    /**
     * {@inheritDoc}
     */
    public short getPropertyNum(final int index) {
      
      final int addr = getPropertyAddressAt(index);
      // Version >= 4 - take the lower 5 bit of the first size byte
      return (short) (getMemoryAccess().readUnsignedByte(addr) & 0x3f);
    }
    
    /**
     * {@inheritDoc}
     */
    protected int getPropertySizeAtAddress(final int address) {
      
      return getPropertyLengthAtData(getMemoryAccess(),
          address + getNumPropertySizeBytes(address));
    }
  }

  /**
   * Constructor.
   * 
   * @param memaccess the memory object
   * @param address the object address
   * @param decoder the decoder object
   */
  public ModernZObject(MemoryAccess memaccess, int address,
                       ZCharDecoder decoder) {
    
    super(memaccess, address, decoder);
  }
  
  /**
   * {@inheritDoc}
   */
  protected PropertyTable createPropertyTable() {
    
    return new ModernPropertyTable();
  }
  
  /**
   * {@inheritDoc}
   */
  public int getParent() {
    
    return getMemoryAccess().readUnsignedShort(
        getObjectAddress() + OFFSET_PARENT);
  }
  
  /**
   * {@inheritDoc}
   */
  public void setParent(final int parent) {
    
    getMemoryAccess().writeUnsignedShort(getObjectAddress() + OFFSET_PARENT,
                                         parent);
  }
  
  /**
   * {@inheritDoc}
   */
  public int getSibling() {
    
    return getMemoryAccess().readUnsignedShort(
        getObjectAddress() + OFFSET_SIBLING);
  }
  
  /**
   * {@inheritDoc}
   */
  public void setSibling(final int sibling) {

    getMemoryAccess().writeUnsignedShort(getObjectAddress() + OFFSET_SIBLING,
                                         sibling);
  }
  
  /**
   * {@inheritDoc}
   */
  public int getChild() {
      
    return getMemoryAccess().readUnsignedShort(
        getObjectAddress() + OFFSET_CHILD);
  }
  
  /**
   * {@inheritDoc}
   */
  public void setChild(final int child) {
    
    getMemoryAccess().writeUnsignedShort(getObjectAddress() + OFFSET_CHILD,
                                         child);
  }
 
  /**
   * {@inheritDoc}
   */
  public int getPropertyTableAddress() {

    return getMemoryAccess().readUnsignedShort(
        getObjectAddress() + OFFSET_PROPERTYTABLE);
  }

  /**
   * This function represents the universal formula to calculate the length
   * of a property given the address of its data (as opposed to the address
   * of the property itself).
   * 
   * @param memaccess the memory access object
   * @param addressOfPropertyData the address of the property data
   * @return the length of the property
   */
  public static int getPropertyLengthAtData(final MemoryAccess memaccess,
                                            final int addressOfPropertyData) {
    
    if (addressOfPropertyData == 0) {
      
      return 0; // see standard 1.1
    }

    // The size byte is always the byte before the property data in any
    // version, so this is consistent
    final short sizebyte =
      memaccess.readUnsignedByte(addressOfPropertyData - 1);
    
      
    // Bit 7 set => this is the second size byte
    if ((sizebyte & 0x80) > 0) {
        
      int proplen = sizebyte & 0x3f;
      if (proplen == 0) {
        proplen = 64; // Std. doc. 1.0, S 12.4.2.1.1
      }
      return proplen;
        
    } else {

      // Bit 7 clear => there is only one size byte, so if bit 6 is set,
      // the size is 2, else it is 1
      return (sizebyte & 0x40) > 0 ? 2 : 1;
    }
  }  
}
