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

/**
 * The default alphabet table implementation.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class DefaultAlphabetTable implements AlphabetTable {

  private static final long serialVersionUID = 1L;
  private static final String A0CHARS = "abcdefghijklmnopqrstuvwxyz";
  private static final String A1CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String A2CHARS = " \n0123456789.,!?_#'\"/\\-:()";

  /** {@inheritDoc} */
  public char getA0Char(final byte zchar) {
    if (zchar == 0) return ' ';
    return A0CHARS.charAt(zchar - ALPHABET_START);
  }

  /** {@inheritDoc} */
  public char getA1Char(final byte zchar) {
    if (zchar == 0) return ' ';
    return A1CHARS.charAt(zchar - ALPHABET_START);
  }

  /** {@inheritDoc} */
  public char getA2Char(final byte zchar) {
    if (zchar == 0) return ' ';
    return A2CHARS.charAt(zchar - ALPHABET_START);
  }

  /** {@inheritDoc} */
  public final int getA0CharCode(final char zsciiChar) {
    return getCharCodeFor(A0CHARS, zsciiChar);
  }

  /** {@inheritDoc} */
  public final int getA1CharCode(final char zsciiChar) {
    return getCharCodeFor(A1CHARS, zsciiChar);
  }

  /** {@inheritDoc} */
  public int getA2CharCode(final char zsciiChar) {
    return getCharCodeFor(A2CHARS, zsciiChar);
  }

  /**
   * Returns the character code for the specified ZSCII character by searching
   * the index in the specified chars string.
   * @param chars the search string
   * @param zsciiChar the ZSCII character
   * @return the character code, which is the index of the character in chars
   *         or -1 if not found
   */
  protected static int getCharCodeFor(final String chars,
                                      final char zsciiChar) {
    int index = chars.indexOf(zsciiChar);
    if (index >= 0) index += ALPHABET_START;
    return index;
  }


  /** {@inheritDoc} */
  public boolean isShift1(final char zchar) { return zchar == SHIFT_4; }

  /** {@inheritDoc} */
  public boolean isShift2(final char zchar) { return zchar == SHIFT_5; }

  /** {@inheritDoc} */
  public boolean isShift(final char zchar) {
    return isShift1(zchar) || isShift2(zchar);
  }

  /** {@inheritDoc} */
  public boolean isShiftLock(final char zchar) { return false; }

  /** {@inheritDoc} */
  public boolean isAbbreviation(final char zchar) {
    return 1 <= zchar && zchar <= 3;
  }
}
