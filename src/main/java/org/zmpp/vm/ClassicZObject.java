/*
 * $Id: ClassicZObject.java,v 1.6 2006/04/12 18:00:17 weiju Exp $
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
 * This class implements Z-machine objects of the story file versions 1-3.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ClassicZObject extends AbstractZObject {

  private static final int OFFSET_PARENT        = 4;
  private static final int OFFSET_SIBLING       = 5;
  private static final int OFFSET_CHILD         = 6;
  private static final int OFFSET_PROPERTYTABLE = 7;
  
  class ClassicPropertyTable extends PropertyTable {
    
    /**
     * {@inheritDoc}
     */
    protected int getNumPropertySizeBytes(final int address) { return 1; }
    
    /**
     * {@inheritDoc}
     */
    public short getPropertyNum(final int index) {
      
      final int addr = getPropertyAddressAt(index);
      final int sizeByte = getMemoryAccess().readUnsignedByte(addr);
      return (short) (sizeByte - 32 * (getPropertySizeAtAddress(addr) - 1));
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
  public ClassicZObject(MemoryAccess memaccess, int address,
                        ZCharDecoder decoder) {
    
    super(memaccess, address, decoder);
  }

  /**
   * {@inheritDoc}
   */
  protected PropertyTable createPropertyTable() {
    
    return new ClassicPropertyTable();
  }
  
  /**
   * {@inheritDoc}
   */
  public int getParent() {
    
    return getMemoryAccess().readUnsignedByte(getObjectAddress() + OFFSET_PARENT);
  }
  
  /**
   * {@inheritDoc}
   */
  public void setParent(final int parent) {
    
    getMemoryAccess().writeUnsignedByte(getObjectAddress() + OFFSET_PARENT,
                                        (short) (parent & 0xff));
  }
  
  /**
   * {@inheritDoc}
   */
  public int getSibling() {
    
    return getMemoryAccess().readUnsignedByte(
        getObjectAddress() + OFFSET_SIBLING);
  }
  
  /**
   * {@inheritDoc}
   */
  public void setSibling(final int sibling) {

    getMemoryAccess().writeUnsignedByte(getObjectAddress() + OFFSET_SIBLING,
                                        (short) (sibling & 0xff));
  }
  
  /**
   * {@inheritDoc}
   */
  public int getChild() {

    return getMemoryAccess().readUnsignedByte(
        getObjectAddress() + OFFSET_CHILD);
  }
  
  /**
   * {@inheritDoc}
   */
  public void setChild(final int child) {
    
    getMemoryAccess().writeUnsignedByte(getObjectAddress() + OFFSET_CHILD,
                                        (short) (child & 0xff));
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
    
    return sizebyte / 32 + 1;
  }  
}
