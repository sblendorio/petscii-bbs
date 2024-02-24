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
import static org.zmpp.base.MemoryUtil.*;

/**
 * This class implements the object tree for story file version >= 4.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class ModernObjectTree extends AbstractObjectTree {

  private static final int OFFSET_PARENT        = 6;
  private static final int OFFSET_SIBLING       = 8;
  private static final int OFFSET_CHILD         = 10;
  private static final int OFFSET_PROPERTYTABLE = 12;

  /**
   * Object entries in version >= 4 have a size of 14 bytes.
   */
  private static final int OBJECTENTRY_SIZE = 14;

  /**
   * Property defaults entries in versions >= 4 have a size of 63 words.
   */
  private static final int PROPERTYDEFAULTS_SIZE = 63 * 2;

  /**
   * Constructor.
   * @param memory Memory object
   * @param address address of tree
   */
  public ModernObjectTree(Memory memory, int address) {
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

  // ************************************************************************
  // ****** Object methods
  // ****************************
  /**
   * {@inheritDoc}
   */
  public int getParent(final int objectNum) {
    return getMemory().readUnsigned16(getObjectAddress(objectNum) +
                                     OFFSET_PARENT);
  }

  /**
   * {@inheritDoc}
   */
  public void setParent(final int objectNum, final int parent) {
    getMemory().writeUnsigned16(getObjectAddress(objectNum) + OFFSET_PARENT,
                                toUnsigned16(parent));
  }

  /**
   * {@inheritDoc}
   */
  public int getSibling(final int objectNum) {
    return getMemory().readUnsigned16(getObjectAddress(objectNum) +
                       OFFSET_SIBLING);
  }

  /**
   * {@inheritDoc}
   */
  public void setSibling(final int objectNum, final int sibling) {
    getMemory().writeUnsigned16(getObjectAddress(objectNum) + OFFSET_SIBLING,
                                toUnsigned16(sibling));
  }

  /**
   * {@inheritDoc}
   */
  public int getChild(final int objectNum) {
    return getMemory().readUnsigned16(getObjectAddress(objectNum) +
                       OFFSET_CHILD);
  }

  /**
   * {@inheritDoc}
   */
  public void setChild(final int objectNum, final int child) {
    getMemory().writeUnsigned16(getObjectAddress(objectNum) + OFFSET_CHILD,
                                toUnsigned16(child));
  }

  // ************************************************************************
  // ****** Property methods
  // ****************************

  /**
   * {@inheritDoc}
   */
  public int getPropertyLength(final int propertyAddress) {
    return getPropertyLengthAtData(getMemory(), propertyAddress);
  }

  /**
   * {@inheritDoc}
   */
  protected int getPropertyTableAddress(int objectNum) {
    return getMemory().readUnsigned16(getObjectAddress(objectNum) +
                       OFFSET_PROPERTYTABLE);
  }

  /**
   * {@inheritDoc}
   */
  protected int getNumPropertySizeBytes(final int propertyAddress) {
    // if bit 7 is set, there are two size bytes, one otherwise
    final char first = getMemory().readUnsigned8(propertyAddress);
    return ((first & 0x80) > 0) ? 2 : 1;
  }

  /**
   * {@inheritDoc}
   */
  protected int getNumPropSizeBytesAtData(int propertyDataAddress) {
    return getNumPropertySizeBytes(propertyDataAddress - 1);
  }

  /**
   * {@inheritDoc}
   */
  protected int getPropertyNum(final int propertyAddress) {
    // Version >= 4 - take the lower 5 bit of the first size byte
    return getMemory().readUnsigned8(propertyAddress) & 0x3f;
  }

  /**
   * This function represents the universal formula to calculate the length
   * of a property given the address of its data (as opposed to the address
   * of the property itself).
   *
   * @param memory the Memory object
   * @param addressOfPropertyData the address of the property data
   * @return the length of the property
   */
  private static int getPropertyLengthAtData(final Memory memory,
                                            final int addressOfPropertyData) {
    if (addressOfPropertyData == 0) {
      return 0; // see standard 1.1
    }
    // The size byte is always the byte before the property data in any
    // version, so this is consistent
    final char sizebyte =
      memory.readUnsigned8(addressOfPropertyData - 1);

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
