/*
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
 * This class manages read and write access to the byte array which contains
 * the story file data. It is the only means to read and manipulate the
 * memory map.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Memory {

  // ************************************************************************
  // ****
  // **** Read access
  // ****
  // *****************************
  /**
   * Reads the unsigned 16 bit word at the specified address.
   * @param address the address
   * @return the 16 bit unsigned value as int
   */
  char readUnsigned16(int address);

  /**
   * Returns the unsigned 8 bit value at the specified address.
   * @param address the address
   * @return the 8 bit unsigned value
   */
  char readUnsigned8(int address);

  // ************************************************************************
  // ****
  // **** Write access
  // ****
  // *****************************
  /**
   * Writes an unsigned 16 bit value to the specified address.
   * @param address the address to write to
   * @param value the value to write
   */
  void writeUnsigned16(int address, char value);

  /**
   * Writes an unsigned byte value to the specified address.
   * @param address the address to write to
   * @param value the value to write
   */
  void writeUnsigned8(int address, char value);

  /**
   * A rather common operation: copy the specified number of bytes from
   * the offset to a taret array.
   * @param dstData the destination array
   * @param dstOffset the offset in the destinations array
   * @param srcOffset the offset in the source
   * @param numBytes the number of bytes to copy
   */
  void copyBytesToArray(byte[] dstData, int dstOffset,
                        int srcOffset, int numBytes);

  /**
   * Copy the specified number of bytes from the source array to this
   * Memory object
   * @param srcData the source array
   * @param srcOffset the source offset
   * @param dstOffset the destination offset
   * @param numBytes the number of bytes to copy
   */
  void copyBytesFromArray(byte[] srcData, int srcOffset,
                          int dstOffset, int numBytes);

  /**
   * Copy the specified number of bytes from the specified source Memory object.
   * @param srcMem the source Memory object
   * @param srcOffset the source offset
   * @param dstOffset the destination offset
   * @param numBytes the number of bytes to copy
   */
  void copyBytesFromMemory(Memory srcMem, int srcOffset, int dstOffset,
                           int numBytes);

  /**
   * Copy an area of bytes efficiently. Since the System.arraycopy() is used,
   * we do not have to worry about overlapping areas and can take advantage
   * of the performance gain.
   * @param src the source address
   * @param dst the destination address
   * @param numBytes the number of bytes
   */
  void copyArea(int src, int dst, int numBytes);
}
