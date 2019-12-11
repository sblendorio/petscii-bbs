/*
 * $Id: ClassicObjectTree.java,v 1.2 2006/04/12 02:04:30 weiju Exp $
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
 * This class implements the object tree for story file version <= 3.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ClassicObjectTree extends AbstractObjectTree {

  /**
   * Object entries in version <= 3 have a size of 9 bytes.
   */
  private static final int OBJECTENTRY_SIZE = 9;

  /**
   * Property defaults entries in versions <= 3 have a size of 31 words.
   */
  private static final int PROPERTYDEFAULTS_SIZE = 31 * 2;
  
  public ClassicObjectTree(MemoryAccess memaccess, int address,
                           ZCharDecoder decoder) {
    
    super(memaccess, address, decoder);
  }  

  /**
   * {@inheritDoc}
   */
  protected ZObject createObject(final int objectNum) {
    
    // flags + (parent, sibling, child) + properties
    return new ClassicZObject(getMemoryAccess(),
          getObjectTreeStart() + (objectNum - 1) * getObjectEntrySize(),
          getDecoder());        
  }

  /**
   * {@inheritDoc}
   */
  protected int getPropertyDefaultsSize() { return PROPERTYDEFAULTS_SIZE; }

  /**
   * {@inheritDoc}
   */
  protected int getObjectEntrySize() { return OBJECTENTRY_SIZE; }
  
  /**
   * {@inheritDoc}
   */
  public int getPropertyLength(final int propertyAddress) {

    return ClassicZObject.getPropertyLengthAtData(
        getMemoryAccess(), propertyAddress);
  }  
}

