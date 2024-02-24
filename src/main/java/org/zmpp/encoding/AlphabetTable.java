/*
 * Created on 2006/01/12
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

import java.io.Serializable;


/**
 * The alphabet table is a central part of the Z encoding system. It stores
 * the characters that are mapped to each alphabet and provides information
 * about shift and escape situations.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface AlphabetTable extends Serializable {

  /** Defines the possible alphabets here. */
  enum Alphabet {  A0, A1, A2 }

  int ALPHABET_START  = 6;
  int ALPHABET_END    = 31;

  char SHIFT_2 = 0x02; // Shift 1
  char SHIFT_3 = 0x03; // Shift 2
  char SHIFT_4 = 0x04; // Shift lock 1
  char SHIFT_5 = 0x05; // Shift lock 2

  /** This character code, used from A2, denotes that a 10 bit value follows. */
  char A2_ESCAPE = 0x06; // escape character

  /**
   * Returns the ZSCII character from alphabet 0 at the specified index.
   * @param zchar a Z encoded character
   * @return the specified character from alphabet 0
   */
  char getA0Char(byte zchar);

  /**
   * Returns the ZSCII character from alphabet 1 at the specified index.
   * @param zchar a Z encoded character
   * @return the specified character from alphabet 1
   */
  char getA1Char(byte zchar);

  /**
   * Returns the ZSCII character from alphabet 2 at the specified index.
   * @param zchar a Z encoded character
   * @return the specified character from alphabet 2
   */
  char getA2Char(byte zchar);

  /**
   * Returns the index of the specified ZSCII character in alphabet 0.
   * @param zsciiChar a ZSCII chararacter
   * @return the index of the character in this alphabet or -1
   */
  int getA0CharCode(char zsciiChar);

  /**
   * Returns the index of the specified ZSCII character in alphabet 2.
   * @param zsciiChar a ZSCII chararacter
   * @return the index of the character in this alphabet or -1
   */
  int getA1CharCode(char zsciiChar);

  /**
   * Returns the index of the specified ZSCII character in alphabet 2.
   * @param zsciiChar a ZSCII chararacter
   * @return the index of the character in this alphabet or -1
   */
  int getA2CharCode(char zsciiChar);

  /**
   * Determines if the specified character marks a abbreviation.
   * @param zchar the zchar
   * @return true if abbreviation, false, otherwise
   */
  boolean isAbbreviation(char zchar);

  /**
   * Returns true if the specified character is a shift level 1 character.
   * @param zchar a Z encoded character
   * @return true if shift, false, otherwise
   */
  boolean isShift1(char zchar);

  /**
   * Returns true if the specified character is a shift level 2 character.
   * @param zchar a Z encoded character
   * @return true if shift, false, otherwise
   */
  boolean isShift2(char zchar);

  /**
   * Returns true if the specified character is a shift lock character.
   * @param zchar a Z encoded character
   * @return true if shift lock, false otherwise
   */
  boolean isShiftLock(char zchar);

  /**
   * Returns true if the specified character is a shift character. Includes
   * shift lock.
   * @param zchar a Z encoded character
   * @return true if either shift or shift lock
   */
  boolean isShift(char zchar);
}
