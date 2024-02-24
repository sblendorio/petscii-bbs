/*
 * Created on 2005/01/15
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
package org.zmpp.encoding;

import org.zmpp.base.Memory;

/**
 * This accent table is used in case that there is an extension header
 * that specifies that accent table.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class CustomAccentTable implements AccentTable {

  /** The Memory object. */
  private Memory memory;

  /** The table adddress. */
  private int tableAddress;

  /**
   * Constructor.
   * @param memory a Memory object
   * @param address the table address
   */
  public CustomAccentTable(final Memory memory, final int address) {
    this.memory = memory;
    this.tableAddress = address;
  }

  /** {@inheritDoc} */
  public int getLength() {
    int result = 0;
    if (tableAddress > 0) {
      result = memory.readUnsigned8(tableAddress);
    }
    return result;
  }

  /** {@inheritDoc} */
  public char getAccent(final int index) {
    char result = '?';
    if (tableAddress > 0) {
      result = memory.readUnsigned16(tableAddress + (index * 2) + 1);
    }
    return result;
  }

  /** {@inheritDoc} */
  public int getIndexOfLowerCase(final int index) {
    final char c = (char) getAccent(index);
    final char lower = Character.toLowerCase(c);
    final int length = getLength();

    for (int i = 0; i < length; i++) {
      if (getAccent(i) == lower) return i;
    }
    return index;
  }
}
