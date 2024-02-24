/*
 * Created on 2008/07/19
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
 * Utility functions for address conversion.
 * @author Wei-ju Wu
 * @version 1.5
 */
public final class MemoryUtil {

  /** Private constructor. */
  private MemoryUtil() { }
  /**
   * Convert an integer value to a char, which is an unsigned 16 bit value.
   * @param value the value to convert
   * @return the converted value
   */
  public static char toUnsigned16(int value) {
    return (char) (value & 0xffff);
  }

  /**
   * Reads the unsigned 32 bit word at the specified address.
   * @param memory the Memory object
   * @param address the address
   * @return the 32 bit unsigned value as long
   */
  public static long readUnsigned32(Memory memory, int address) {
      final long a24 = (memory.readUnsigned8(address) & 0xffL) << 24;
      final long a16 = (memory.readUnsigned8(address + 1) & 0xffL) << 16;
      final long a8  = (memory.readUnsigned8(address + 2) & 0xffL) << 8;
      final long a0  = (memory.readUnsigned8(address + 3) & 0xffL);
      return a24 | a16 | a8 | a0;
  }

  /**
   * Writes an unsigned 32 bit value to the specified address.
   * @param memory the Memory object
   * @param address the address to write to
   * @param value the value to write
   */
  public static void writeUnsigned32(Memory memory, final int address,
                                     final long value) {
    memory.writeUnsigned8(address, (char) ((value & 0xff000000) >> 24));
    memory.writeUnsigned8(address + 1, (char) ((value & 0x00ff0000) >> 16));
    memory.writeUnsigned8(address + 2, (char) ((value & 0x0000ff00) >> 8));
    memory.writeUnsigned8(address + 3, (char) (value & 0x000000ff));
  }

  /**
   * Converts the specified signed 16 bit value to an unsigned 16 bit value.
   * @param value the signed value
   * @return the unsigned value
   */
  public static char signedToUnsigned16(short value) {
    return (char) (value >= 0 ? value : Character.MAX_VALUE + (value + 1));
  }

  /**
   * Converts the specified unsigned 16 bit value to a signed 16 bit value.
   * @param value the unsigned value
   * @return the signed value
   */
  public static short unsignedToSigned16(char value) {
    return (short) (value > Short.MAX_VALUE ?
      -(Character.MAX_VALUE - (value - 1)) : value);
  }

  /**
   * Converts the specified unsigned 8 bit value to a signed 8 bit value.
   * If the value specified is actually a 16 bit value, only the lower 8 bit
   * will be used.
   * @param value the unsigned value
   * @return the signed value
   */
  public static short unsignedToSigned8(char value) {
    char workvalue = (char) (value & 0xff);
    return (short) (workvalue > Byte.MAX_VALUE ?
      -(255 - (workvalue - 1)) : workvalue);
  }
}
