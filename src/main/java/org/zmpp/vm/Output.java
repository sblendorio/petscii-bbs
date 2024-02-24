/*
 * Created on 2006/02/14
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
 * The Output interface.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Output {

  /** The output stream number for the screen. */
  int OUTPUTSTREAM_SCREEN = 1;

  /** The output stream number for the transcript. */
  int OUTPUTSTREAM_TRANSCRIPT = 2;

  /** The output stream number for the memory stream. */
  int OUTPUTSTREAM_MEMORY = 3;

  /**
   * Selects/unselects the specified output stream. If the streamnumber
   * is negative, |streamnumber| is deselected, if positive, it is selected.
   * Stream 3 (the memory stream) can not be selected by this function,
   * but can be deselected here.
   * @param streamnumber the output stream number
   * @param flag true to enable, false to disable
   */
  void selectOutputStream(int streamnumber, boolean flag);

  /**
   * Selects the output stream 3 which writes to memory.
   * @param tableAddress the table address to write to
   * @param tableWidth the table width
   */
  void selectOutputStream3(int tableAddress, int tableWidth);

  /**
   * Prints the ZSCII string at the specified address to the active
   * output streams.
   * @param stringAddress the address of an ZSCII string
   */
  void printZString(int stringAddress);

  /**
   * Prints the specified string to the active output streams.
   * @param str the string to print, encoding is ZSCII
   */
  void print(String str);

  /**
   * Prints a newline to the active output streams.
   */
  void newline();

  /**
   * Prints the specified ZSCII character.
   * @param zchar the ZSCII character to print
   */
  void printZsciiChar(char zchar);

  /**
   * Prints the specified signed number.
   * @param num the number to print?
   */
  void printNumber(short num);

  /**
   * Flushes the active output streams.
   */
  void flushOutput();

  /**
   * Resets the output streams.
   */
  void reset();
}
