/*
 * Created on 2005/09/25
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
import org.zmpp.encoding.ZCharDecoder.AbbreviationsTable;

/**
 * This class represents a view to the abbreviations table. The table
 * starts at the predefined address within the header and contains pointers
 * to ZSCII strings in the memory map. These pointers are word addresses
 * as opposed to all other addresses in the memory map, therefore the
 * actual value has to multiplied by two to get the real address.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class Abbreviations implements AbbreviationsTable {

  /** The memory object. */
  private Memory memory;

  /** The start address of the abbreviations table. */
  private int address;

  /**
   * Constructor.
   * @param memory the memory map
   * @param address the start address of the abbreviations table
   */
  public Abbreviations(final Memory memory, final int address) {
    super();
    this.memory = memory;
    this.address = address;
  }

  /**
   * The abbreviation table contains word addresses, so read out the pointer
   * and multiply by two
   *
   * @param entryNum the entry index in the abbreviations table
   * @return the word address
   */
  public int getWordAddress(final int entryNum) {
    return memory.readUnsigned16(address + entryNum * 2) * 2;
  }
}
