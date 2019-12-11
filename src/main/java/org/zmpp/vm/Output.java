/*
 * $Id: Output.java,v 1.3 2006/05/12 22:01:39 weiju Exp $
 * 
 * Created on 2006/02/14
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

import org.zmpp.encoding.ZsciiString;
import org.zmpp.io.OutputStream;

public interface Output {

  /**
   * The output stream number for the screen.
   */
  final static int OUTPUTSTREAM_SCREEN = 1;
  
  /**
   * The output stream number for the transcript.
   */
  final static int OUTPUTSTREAM_TRANSCRIPT = 2;
  
  /**
   * The output stream number for the memory stream.
   */
  final static int OUTPUTSTREAM_MEMORY = 3;
  
  /**
   * Closes the managed streams.
   */
  void close();
  
  /**  
   * Sets the output stream to the specified number.
   * 
   * @param streamnumber the stream number
   * @param stream the output stream
   */
  void setOutputStream(int streamnumber, OutputStream stream);
  
  /**
   * Selects/unselects the specified output stream. If the streamnumber
   * is negative, |streamnumber| is deselected, if positive, it is selected.
   * Stream 3 (the memory stream) can not be selected by this function,
   * but can be deselected here.
   * 
   * @param streamnumber the output stream number
   * @param flag true to enable, false to disable
   */
  void selectOutputStream(int streamnumber, boolean flag);
  
  /**
   * Selects the output stream 3 which writes to memory.
   * 
   * @param tableAddress the table address to write to
   * @param tableWidth the table width
   */
  void selectOutputStream3(int tableAddress, int tableWidth);
  
  /**
   * Prints the ZSCII string at the specified address to the active
   * output streams.
   * 
   * @param stringAddress the address of an ZSCII string
   */
  void printZString(int stringAddress);
  
  /**
   * Prints the specified string to the active output streams.
   * 
   * @param str the string to print
   */
  void print(ZsciiString str);
  
  /**
   * Prints a newline to the active output streams.
   */
  void newline();
  
  /**
   * Prints the specified ZSCII character.
   * 
   * @param zchar the ZSCII character to print
   * @param isInput true if this is echoing input
   */
  void printZsciiChar(short zchar, boolean isInput);
  
  /**
   * Deletes the specified ZSCII character. This implements a backspace.
   * 
   * @param zchar the character to delete
   */
  void deletePreviousZsciiChar(short zchar);
  
  /**
   * Prints the specified signed number.
   * 
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
