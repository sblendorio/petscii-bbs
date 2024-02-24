/*
 * Created on 2006/03/05
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

import org.zmpp.base.Memory;

/**
 * This class implements the object tree for story file version <= 3.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class ClassicObjectTree extends AbstractObjectTree {

  private static final int OFFSET_PARENT        = 4;
  private static final int OFFSET_SIBLING       = 5;
  private static final int OFFSET_CHILD         = 6;
  private static final int OFFSET_PROPERTYTABLE = 7;

  /**
   * Object entries in version <= 3 have a size of 9 bytes.
   */
  private static final int OBJECTENTRY_SIZE = 9;

  /**
   * Property defaults entries in versions <= 3 have a size of 31 words.
   */
  private static final int PROPERTYDEFAULTS_SIZE = 31 * 2;

  /**
   * Constructor.
   * @param memory the Memory object
   * @param address the address
   */
  public ClassicObjectTree(Memory memory, int address) {
    super(memory, address);
  }

  /**
   * {@inheritDoc}
   */
  protected int getObjectAddress(int objectNum) {
    return getObjectTreeStart() + (objectNum - 1) * getObjectEntrySize();
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
    return getPropertyLengthAtData(getMemory(), propertyAddress);
  }

  /**
   * {@inheritDoc}
   */
  public int getChild(final int objectNum) {
  return getMemory().readUnsigned8(getObjectAddress(objectNum) +
                                OFFSET_CHILD);
  }

  /**
   * {@inheritDoc}
   */
  public void setChild(final int objectNum, final int child) {
  getMemory().writeUnsigned8(getObjectAddress(objectNum) + OFFSET_CHILD,
      (char) (child & 0xff));
  }

  /**
   * {@inheritDoc}
   */
  public int getParent(final int objectNum) {
  return getMemory().readUnsigned8(getObjectAddress(objectNum) +
                                  OFFSET_PARENT);
  }

  /**
   * {@inheritDoc}
   */
  public void setParent(final int objectNum, final int parent) {
  getMemory().writeUnsigned8(getObjectAddress(objectNum) + OFFSET_PARENT,
      (char) (parent & 0xff));
  }

  /**
   * {@inheritDoc}
   */
  public int getSibling(final int objectNum) {
  return getMemory().readUnsigned8(getObjectAddress(objectNum) +
                    OFFSET_SIBLING);
  }

  /**
   * {@inheritDoc}
   */
  public void setSibling(final int objectNum, final int sibling) {
  getMemory().writeUnsigned8(getObjectAddress(objectNum) + OFFSET_SIBLING,
      (char) (sibling & 0xff));
  }

  /**
   * {@inheritDoc}
   */
  protected int getPropertyTableAddress(final int objectNum) {
    return getMemory().readUnsigned16(getObjectAddress(objectNum) +
                                     OFFSET_PROPERTYTABLE);
  }

  /**
   * {@inheritDoc}
   */
  protected int getNumPropertySizeBytes(final int propertyDataAddress) {
    return 1;
  }
  /**
   * {@inheritDoc}
   */
  protected int getNumPropSizeBytesAtData(int propertyDataAddress) {
    return 1;
  }

  /**
   * {@inheritDoc}
   */
  protected int getPropertyNum(final int propertyAddress) {
    final int sizeByte = getMemory().readUnsigned8(propertyAddress);
    return sizeByte - 32 * (getPropertyLength(propertyAddress + 1) - 1);
  }

  /**
   * This function represents the universal formula to calculate the length
   * of a property given the address of its data (as opposed to the address
   * of the property itself).
   * @param memaccess the memory access object
   * @param addressOfPropertyData the address of the property data
   * @return the length of the property
   */
  private static int getPropertyLengthAtData(final Memory memaccess,
                                             final int addressOfPropertyData) {
    if (addressOfPropertyData == 0) {
      return 0; // see standard 1.1
    }

    // The size byte is always the byte before the property data in any
    // version, so this is consistent
    final char sizebyte =
      memaccess.readUnsigned8(addressOfPropertyData - 1);

    return sizebyte / 32 + 1;
  }
}
