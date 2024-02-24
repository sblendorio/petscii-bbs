/*
 * Created on 2005/09/23
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
package org.zmpp.base;

/**
 * A MemorySection object wraps a Memory object, a length and a start to
 * support subsections within memory.
 * All access functions will be relative to the initialized start offset
 * within the global memory.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class MemorySection implements Memory {

  private Memory memory;
  private int start;
  private int length;

  /**
   * Constructor.
   * @param memory the Memory objeci to wrap
   * @param start the start of the section
   * @param length the length of the section
   */
  public MemorySection(final Memory memory, final int start, final int length) {
    this.memory = memory;
    this.start = start;
    this.length = length;
  }

  /**
   * Returns the length of this object in bytes.
   * @return the length in bytes
   */
  public int getLength() { return length; }

  /** {@inheritDoc} */
  public void writeUnsigned16(final int address, final char value) {
    memory.writeUnsigned16(address + start, value);
  }

  /** {@inheritDoc} */
  public void writeUnsigned8(final int address, final char value) {
    memory.writeUnsigned8(address + start, value);
  }

  /** {@inheritDoc} */
  public char readUnsigned16(final int address) {
    return memory.readUnsigned16(address + start);
  }

  /** {@inheritDoc} */
  public char readUnsigned8(final int address) {
    return memory.readUnsigned8(address + start);
  }

  /** {@inheritDoc} */
  public void copyBytesToArray(byte[] dstData, int dstOffset,
                               int srcOffset, int numBytes) {
    memory.copyBytesToArray(dstData, dstOffset, srcOffset + start,
                            numBytes);
  }

  /** {@inheritDoc} */
  public void copyBytesFromArray(byte[] srcData, int srcOffset, int dstOffset,
                                 int numBytes) {
    memory.copyBytesFromArray(srcData, srcOffset, dstOffset + start, numBytes);
  }

  /** {@inheritDoc} */
  public void copyBytesFromMemory(Memory srcMem, int srcOffset, int dstOffset,
                                  int numBytes) {
    memory.copyBytesFromMemory(srcMem, srcOffset, dstOffset + start, numBytes);
  }

  /** {@inheritDoc} */
  public void copyArea(int src, int dst, int numBytes) {
    memory.copyArea(src + start, dst + start, numBytes);
  }
}
