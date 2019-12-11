/*
 * $Id: AbstractObjectTree.java,v 1.5 2006/04/12 18:00:17 weiju Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.ZCharDecoder;

/**
 * This class is the abstract super class of object trees.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class AbstractObjectTree implements ObjectTree {
  
  /**
   * The object cache.
   */
  private Map<Integer, ZObject> objectCache;
  
  /**
   * Flags if illegal access was reported.
   */
  private boolean illegalAccessReported;
  
  /**
   * The memory access object.
   */
  private MemoryAccess memaccess;
  
  /**
   * The object table's start address.
   */
  private int address;
  
  /**
   * A decoder object.
   */
  private ZCharDecoder decoder;
  
  /**
   * Constructor.
   * 
   * @param memaccess the memory access object
   * @param address the object table's start address
   * @param decoder a ZCharDecoder object
   */
  public AbstractObjectTree(final MemoryAccess memaccess, final int address,
      final ZCharDecoder decoder) {

    super();
    this.objectCache = new HashMap<Integer, ZObject>();
    this.memaccess = memaccess;
    this.address = address;
    this.decoder = decoder;
  }
  
  /**
   * Returns the memory object.
   * 
   * @return the memory object
   */
  protected MemoryAccess getMemoryAccess() { return memaccess; }
  
  /**
   * Returns this tree's start address.
   * 
   * @return the address
   */
  protected int getAddress() { return address; }
  
  /**
   * Returns the decoder object.
   * 
   * @return the decoder object
   */
  protected ZCharDecoder getDecoder() { return decoder; }

  /**
   * {@inheritDoc}
   */
  public short getPropertyDefault(final int propertyNum) {
    
    final int index = propertyNum - 1;
    return memaccess.readShort(address + index * 2);
  }
  
  /**
   * {@inheritDoc}
   */
  public ZObject getObject(final int objectNum) {
    
    // We can not introduce a real check here since getNumObjects relies
    // on this functions and would result in an endless recursion.
    if (objectNum > 0) {
      
      // flags + (parent, sibling, child) + properties
      final Integer key = Integer.valueOf(objectNum);
      ZObject result = objectCache.get(key);
      if (result == null) {
        
        result = createObject(objectNum);
        objectCache.put(key, result);
      }
      return result;
    }
    
    if (!illegalAccessReported) {
      
      System.err.println("invalid access to object 0");
      illegalAccessReported = true;
    }
    return null;
  }
  
  protected abstract ZObject createObject(int objectNum);
  
  /**
   * {@inheritDoc}
   */
  public int getNumObjects() {
    
    // The information about the number of objects can not directly
    // read from the header.
    // We assume that the first object always contains the property table
    // with the lowest address
    // This is a function that is in fact never invoked directly from
    // the Z-machine and only made available for testing
    return (getObject((short) 1).getPropertyTableAddress()
            - getObjectTreeStart()) / getObjectEntrySize();
  }

  /**
   * {@inheritDoc}
   */
  public void removeObject(final int objectNum) {
    
    final ZObject obj = getObject(objectNum);    
    final ZObject parentObj = getObject(obj.getParent());
    obj.setParent((short) 0);
    
    if (parentObj != null) {
      
      if (parentObj.getChild() == objectNum) {
      
        parentObj.setChild(obj.getSibling());
      
      } else {
      
        // Find the child that comes directly before the removed
        // node and set the direct sibling of the removed node as
        // its new sibling
        ZObject currentChild = getObject(parentObj.getChild());
        int sibling = currentChild.getSibling();
      
        // We have to handle the case that in fact that object is a child
        // of its parent, but not directly (happens for some reasons).
        // We stop in this case and simply remove the object from its
        // parent, probably the object tree modification routines should
        // be reverified
        while (sibling != 0 && sibling != objectNum) {
        
          currentChild = getObject(sibling);
          sibling = currentChild.getSibling();
        }
        // sibling might be 0, in that case, the object is not
        // in the hierarchy
        if (sibling == objectNum) {
          currentChild.setSibling(obj.getSibling());
        }
      }
    }
    obj.setSibling((short) 0);
  }
  
  /**
   * {@inheritDoc}
   */
  public void insertObject(final int parentNum, final int objectNum) {
    
    final ZObject parent = getObject(parentNum);
    final ZObject child = getObject(objectNum);
    
    // we want to ensure, the child has no old parent relationships
    if (child.getParent() > 0) {
      
      removeObject(objectNum);
    }
    
    final int oldChild = parent.getChild();    
    child.setParent(parentNum);
    parent.setChild(objectNum);
    child.setSibling(oldChild);
  }
  
  /**
   * The size of the property defaults section.
   * 
   * @return the property defaults section
   */
  protected abstract int getPropertyDefaultsSize();
  
  /**
   * Returns the start address of the object tree section.
   * 
   * @return the object tree's start address 
   */
  protected int getObjectTreeStart() {
    
    return getAddress() + getPropertyDefaultsSize();
  }
    
  /**
   * Returns the story file version specific object entry size.
   * 
   * @return the size of an object entry
   */
  protected abstract int getObjectEntrySize();
}
