/*
 * $Id: ZObject.java,v 1.5 2006/01/07 02:51:31 weiju Exp $
 * 
 * Created on 10/14/2005
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

/**
 * This is the interface definition for an object in the object tree.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface ZObject {

  /**
   * Tests if the specified attribute is set.
   * 
   * @param attributeNum the attribute number, starting with 0
   * @return true if the attribute is set
   */
  boolean isAttributeSet(int attributeNum);

  /**
   * Sets the specified attribute.
   * 
   * @param attributeNum the attribute number, starting with 0
   */
  void setAttribute(int attributeNum);


  /**
   * Clears the specified attribute.
   * 
   * @param attributeNum the attribute number, starting with 0
   */
  void clearAttribute(int attributeNum);
  
  /**
   * Returns the number of this object's parent object.
   * 
   * @return the parent object's number
   */
  int getParent();

  /**
   * Assigns a new parent object.
   * 
   * @param parent the new parent object
   */
  void setParent(int parent);
  
  /**
   * Returns the object number of this object's sibling object.
   * 
   * @return the sibling object's object number
   */
  int getSibling();
  
  /**
   * Assigns a new sibling to this object.
   * 
   * @param sibling the new sibling's object number
   */
  void setSibling(int sibling);
  
  /**
   * Returns the object number of this object's child object.
   * 
   * @return the child object's object number
   */
  int getChild();
  
  /**
   * Assigns a new child to this object.
   * 
   * @param child the new child
   */
  void setChild(int child);
    
  /**
   * Returns this object's property table address. Might be made private
   * in the future.
   * 
   * @return the address of this object's property table
   */
  int getPropertyTableAddress();
  
  /**
   * Returns the properties description address.
   * 
   * @return the description address
   */
  int getPropertiesDescriptionAddress();
  
  /**
   * Returns the number of properties.
   * 
   * @return the number of properties
   */
  int getNumProperties();
  
  /**
   * The number of bytes in the specified property.
   * 
   * @param property the property number
   * @return the specified property's number of bytes
   */
  int getPropertySize(int property);

  /**
   * Returns the the specified property byte.
   *  
   * @param property the property number
   * @param bytenum the byte number, starting with 0
   * @return the value of the specified property byte
   */
  byte getPropertyByte(int property, int bytenum);
  
  /**
   * Returns the address of the specified property. Note that this will not
   * include the length byte.
   * 
   * @param property the property
   * @return the specified property's address
   */
  int getPropertyAddress(int property);
  
  /**
   * Returns true if the specified property is available.
   * 
   * @param property the property number
   * @return true if the property is available, false otherwise
   */
  boolean isPropertyAvailable(int property);
  
  /**
   * Returns the next property in the list. If property is 0, this
   * will return the first property number, if property is the last
   * element in the list, it will return 0.
   * 
   * @param property the property number
   * @return the next property in the list or 0
   */
  int getNextProperty(int property);  

  /**
   * Sets the specified property byte to the given value.
   * 
   * @param property the property
   * @param bytenum the byte number
   * @param value the value
   */
  void setPropertyByte(int property, int bytenum, byte value);
}
