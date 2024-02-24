/*
 * Created on 10/14/2005
 * Copyright (c) 2005-2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.vm;

/**
 * This is the interface definition of the object tree.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface ObjectTree {

  /**
   * Removes an object from its parent.
   * @param objectNum the object number
   */
  void removeObject(int objectNum);

  /**
   * Inserts an object to a new parent.
   * @param parentNum the parent number
   * @param objectNum the object number
   */
  void insertObject(int parentNum, int objectNum);

  /**
   * Determines the length of the property at the specified address.
   * The address is an address returned by ZObject.getPropertyAddress,
   * i.e. it is starting after the length byte.
   * @param propertyAddress the property address
   * @return the length
   */
  int getPropertyLength(int propertyAddress);

  // ********************************************************************
  // ***** Methods on objects
  // ***********************************
  /**
   * Tests if the specified attribute is set.
   * @param objectNum the object number
   * @param attributeNum the attribute number, starting with 0
   * @return true if the attribute is set
   */
  boolean isAttributeSet(int objectNum, int attributeNum);

  /**
   * Sets the specified attribute.
   * @param objectNum the object number
   * @param attributeNum the attribute number, starting with 0
   */
  void setAttribute(int objectNum, int attributeNum);

  /**
   * Clears the specified attribute.
   * @param objectNum the object number
   * @param attributeNum the attribute number, starting with 0
   */
  void clearAttribute(int objectNum, int attributeNum);

  /**
   * Returns the number of this object's parent object.
   * @param objectNum object number
   * @return the parent object's number
   */
  int getParent(int objectNum);

  /**
   * Assigns a new parent object.
   * @param objectNum the object number
   * @param parent the new parent object
   */
  void setParent(int objectNum, int parent);

  /**
   * Returns the object number of this object's sibling object.
   * @param objectNum the object number
   * @return the sibling object's object number
   */
  int getSibling(int objectNum);

  /**
   * Assigns a new sibling to this object.
   * @param objectNum the object number
   * @param sibling the new sibling's object number
   */
  void setSibling(int objectNum, int sibling);

  /**
   * Returns the object number of this object's child object.
   * @param objectNum the object number
   * @return the child object's object number
   */
  int getChild(int objectNum);

  /**
   * Assigns a new child to this object.
   * @param objectNum the object number
   * @param child the new child
   */
  void setChild(int objectNum, int child);

  /**
   * Returns the properties description address.
   * @param objectNum the object number
   * @return the description address
   */
  int getPropertiesDescriptionAddress(int objectNum);

  /**
   * Returns the address of the specified property. Note that this will not
   * include the length byte.
   * @param objectNum the object number
   * @param property the property
   * @return the specified property's address
   */
  int getPropertyAddress(int objectNum, int property);

  /**
   * Returns the next property in the list. If property is 0, this
   * will return the first property number, if property is the last
   * element in the list, it will return 0.
   * @param objectNum the object number
   * @param property the property number
   * @return the next property in the list or 0
   */
  int getNextProperty(int objectNum, int property);

  /**
   * Returns the the specified property.
   * @param objectNum the object number
   * @param property the property number
   * @return the value of the specified property
   */
  char getProperty(int objectNum, int property);

  /**
   * Sets the specified property byte to the given value.
   * @param objectNum the object number
   * @param property the property
   * @param value the value
   */
  void setProperty(int objectNum, int property, char value);
}
