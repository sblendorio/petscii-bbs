/*
 * Created on 2006/01/15
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

import org.zmpp.encoding.AlphabetTable.Alphabet;

/**
 * The default implementation of ZCharTranslator.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class DefaultZCharTranslator implements Cloneable, ZCharTranslator {

  private AlphabetTable alphabetTable;
  private Alphabet currentAlphabet;
  private Alphabet lockAlphabet;
  private boolean shiftLock;

  /**
   * Constructor.
   * @param alphabetTable the alphabet table
   */
  public DefaultZCharTranslator(final AlphabetTable alphabetTable) {
    this.alphabetTable = alphabetTable;
    reset();
  }

  /** {@inheritDoc} */
  public final void reset() {
    currentAlphabet = Alphabet.A0;
    lockAlphabet = null;
    shiftLock = false;
  }

  /**
   * Reset the translation to use the last alphabet used.
   */
  public void resetToLastAlphabet() {
    if (lockAlphabet == null) {
      currentAlphabet = Alphabet.A0;
    } else {
      currentAlphabet = lockAlphabet;
      shiftLock = true;
    }
  }

  /** {@inheritDoc} */
  public Object clone() throws CloneNotSupportedException {
    DefaultZCharTranslator clone = null;
    clone = (DefaultZCharTranslator) super.clone();
    clone.reset();
    return clone;
  }

  /** {@inheritDoc} */
  public Alphabet getCurrentAlphabet() { return currentAlphabet; }

  /** {@inheritDoc} */
  public char translate(final char zchar) {
    if (shift(zchar)) return '\0';

    char result;
    if (isInAlphabetRange(zchar)) {
      switch (currentAlphabet) {
        case A0:
          result = (char) alphabetTable.getA0Char((byte) zchar);
          break;
        case A1:
          result = (char) alphabetTable.getA1Char((byte) zchar);
          break;
        case A2:
        default:
          result = (char) alphabetTable.getA2Char((byte) zchar);
          break;
      }
    } else {
      result = '?';
    }
    // Only reset if the shift lock flag is not set
    if (!shiftLock) resetToLastAlphabet();
    return result;
  }

  /** {@inheritDoc} */
  public boolean willEscapeA2(final char zchar) {
    return currentAlphabet == Alphabet.A2 && zchar == AlphabetTable.A2_ESCAPE;
  }

  /** {@inheritDoc} */
  public boolean isAbbreviation(final char zchar) {
    return alphabetTable.isAbbreviation(zchar);
  }

  /** {@inheritDoc} */
  public AlphabetElement getAlphabetElementFor(final char zsciiChar) {
    // Special handling for newline !!
    if (zsciiChar == '\n') {
      return new AlphabetElement(Alphabet.A2, (char) 7);
    }

    Alphabet alphabet = null;
    int zcharCode = alphabetTable.getA0CharCode(zsciiChar);

    if (zcharCode >= 0) {
      alphabet = Alphabet.A0;
    } else {
      zcharCode = alphabetTable.getA1CharCode(zsciiChar);
      if (zcharCode >= 0) {
        alphabet = Alphabet.A1;
      } else {
        zcharCode = alphabetTable.getA2CharCode(zsciiChar);
        if (zcharCode >= 0) {
          alphabet = Alphabet.A2;
        }
      }
    }

    if (alphabet == null) {
      // It is not in any alphabet table, we are fine with taking the code
      // number for the moment
      zcharCode = zsciiChar;
    }
    return new AlphabetElement(alphabet, (char) zcharCode);
  }

  /**
   * Determines if the given byte value falls within the alphabet range.
   * @param zchar the zchar value
   * @return true if the value is in the alphabet range, false, otherwise
   */
  private static boolean isInAlphabetRange(final char zchar) {
    return 0 <= zchar && zchar <= AlphabetTable.ALPHABET_END;
  }

  /**
   * Performs a shift.
   * @param zchar a z encoded character
   * @return true if a shift was performed, false, otherwise
   */
  private boolean shift(final char zchar) {
    if (alphabetTable.isShift(zchar)) {
      currentAlphabet = shiftFrom(currentAlphabet, zchar);

      // Sets the current lock alphabet
      if (alphabetTable.isShiftLock(zchar)) {
        lockAlphabet = currentAlphabet;
      }
      return true;
    }
    return false;
  }

  /**
   * This method contains the rules to shift the alphabets.
   * @param alphabet the source alphabet
   * @param shiftChar the shift character
   * @return the resulting alphabet
   */
  private Alphabet shiftFrom(final Alphabet alphabet, final char shiftChar) {
    Alphabet result = null;

    if (alphabetTable.isShift1(shiftChar)) {
      if (alphabet == Alphabet.A0) {
        result = Alphabet.A1;
      } else if (alphabet == Alphabet.A1) {
        result = Alphabet.A2;
      } else if (alphabet == Alphabet.A2) {
        result = Alphabet.A0;
      }
    } else if (alphabetTable.isShift2(shiftChar)) {
      if (alphabet == Alphabet.A0) {
        result = Alphabet.A2;
      } else if (alphabet == Alphabet.A1) {
        result = Alphabet.A0;
      } else if (alphabet == Alphabet.A2) {
        result = Alphabet.A1;
      }
    } else {
      result = alphabet;
    }
    shiftLock = alphabetTable.isShiftLock(shiftChar);
    return result;
  }
}
