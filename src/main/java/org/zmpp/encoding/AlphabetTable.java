/*
 * $Id: AlphabetTable.java,v 1.3 2006/01/20 00:59:32 weiju Exp $
 * 
 * Created on 2006/01/12
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
package org.zmpp.encoding;


/**
 * The alphabet table is a central part of the Z encoding system. It stores
 * the characters that are mapped to each alphabet and provides information
 * about shift and escape situations.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface AlphabetTable {

  /**
   * Defines the possible alphabets here.
   */
  public enum Alphabet {  A0, A1, A2 }
  
  public static final int ALPHABET_START  = 6;
  public static final int ALPHABET_END    = 31;
  
  public static final byte SHIFT_2 = 0x02; // Shift 1
  public static final byte SHIFT_3 = 0x03; // Shift 2
  public static final byte SHIFT_4 = 0x04; // Shift lock 1
  public static final byte SHIFT_5 = 0x05; // Shift lock 2
  
  /**
   * This character code, used from A2, denotes that a 10 bit value
   * follows. 
   */
  public static final byte A2_ESCAPE = 0x06; // escape character

  /**
   * Returns the ZSCII character from alphabet 0 at the specified index.
   * 
   * @param zchar a Z encoded character
   * @return the specified character from alphabet 0
   */
  short getA0Char(byte zchar);
  
  /**
   * Returns the ZSCII character from alphabet 1 at the specified index.
   * 
   * @param zchar a Z encoded character
   * @return the specified character from alphabet 1
   */
  short getA1Char(byte zchar);
  
  /**
   * Returns the ZSCII character from alphabet 2 at the specified index.
   * 
   * @param zchar a Z encoded character
   * @return the specified character from alphabet 2
   */
  short getA2Char(byte zchar);
  
  /**
   * Returns the index of the specified ZSCII character in alphabet 0.
   * 
   * @param zsciiChar a ZSCII chararacter
   * @return the index of the character in this alphabet or -1
   */
  byte getA0CharCode(short zsciiChar);

  /**
   * Returns the index of the specified ZSCII character in alphabet 2.
   * 
   * @param zsciiChar a ZSCII chararacter
   * @return the index of the character in this alphabet or -1
   */
  byte getA1CharCode(short zsciiChar);

  /**
   * Returns the index of the specified ZSCII character in alphabet 2.
   * 
   * @param zsciiChar a ZSCII chararacter
   * @return the index of the character in this alphabet or -1
   */
  byte getA2CharCode(short zsciiChar);
  
  /**
   * Determines if the specified character marks a abbreviation. 
   * 
   * @param zchar the zchar
   * @return true if abbreviation, false, otherwise
   */
  boolean isAbbreviation(short zchar);

  /**
   * Returns true if the specified character is a shift level 1 character.
   * 
   * @param zchar a Z encoded character
   * @return true if shift, false, otherwise
   */
  boolean isShift1(short zchar);

  /**
   * Returns true if the specified character is a shift level 2 character.
   * 
   * @param zchar a Z encoded character
   * @return true if shift, false, otherwise
   */
  boolean isShift2(short zchar);
  
  /**
   * Returns true if the specified character is a shift lock character.
   * 
   * @param zchar a Z encoded character
   * @return true if shift lock, false otherwise
   */
  boolean isShiftLock(short zchar);  
  
  /**
   * Returns true if the specified character is a shift character. Includes
   * shift lock.
   * 
   * @param zchar a Z encoded character
   * @return true if either shift or shift lock
   */
  boolean isShift(short zchar);
}
