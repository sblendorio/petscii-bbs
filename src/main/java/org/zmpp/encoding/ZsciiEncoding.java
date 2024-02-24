/*
 * Created on 2005/11/10
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
 * The usage of ZSCII is a little confusing, within a story file it uses
 * alphabet tables to encode/decode it to an unreadable format, for input
 * and output it uses a more readable encoding which resembles iso-8859-n.
 * ZsciiEncoding therefore captures this input/output aspect of ZSCII
 * whereas ZsciiConverter and ZsciiString handle story file encoded strings.
 *
 * This class has a nonmodifiable state, so it can be shared throughout
 * the whole application.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class ZsciiEncoding implements IZsciiEncoding {
  private AccentTable accentTable;

  /**
   * Constructor.
   * @param accentTable the accent table.
   */
  public ZsciiEncoding(final AccentTable accentTable) {
    this.accentTable = accentTable;
  }

  /**
   * Returns true if the input is a valid ZSCII character, false otherwise.
   * @param zchar a ZSCII character
   * @return true if valid, false otherwise
   */
  public boolean isZsciiCharacter(final char zchar) {
    switch (zchar) {
    case NULL:
    case DELETE:
    case NEWLINE:
    case ESCAPE:
      return true;
    default:
      return isAscii(zchar) || isAccent(zchar)
             || isUnicodeCharacter(zchar);
    }
  }

  /**
   * Returns true if the specified character can be converted to a ZSCII
   * character, false otherwise.
   * @param c a unicode character
   * @return true if c can be converted, false, otherwise
   */
  public boolean isConvertableToZscii(final char c) {
    return isAscii(c) || isInTranslationTable(c) || c == '\n'
           || c == 0 || isUnicodeCharacter(c);
  }

  /**
   * Converts a ZSCII character to a unicode character. Will return
   * '?' if the given character is not known.
   * @param zchar a ZSCII character.
   * @return the unicode representation
   */
  public char getUnicodeChar(final char zchar) {
    if (isAscii(zchar)) return zchar;
    if (isAccent(zchar)) {
      final int index = zchar - ACCENT_START;
      if (index < accentTable.getLength()) {
        return (char) accentTable.getAccent(index);
      }
    }
    if (zchar == NULL) return '\0';
    if (zchar == NEWLINE || zchar == NEWLINE_10) return '\n';
    if (isUnicodeCharacter(zchar)) return zchar;
    return '?';
  }

  /**
   * Converts the specified string into its ZSCII representation.
   * @param str the input string
   * @return the ZSCII representation
   */
  public String convertToZscii(final String str) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      result.append(getZsciiChar(str.charAt(i)));
    }
    return result.toString();
  }

  /**
   * Converts the specified unicode character to a ZSCII character.
   * Will return 0 if the character can not be converted.
   * @param c the unicode character to convert
   * @return the ZSCII character
   */
  public char getZsciiChar(final char c) {
    if (isAscii(c)) {
      return c;
    } else if (isInTranslationTable(c)) {
      return (char) (getIndexInTranslationTable(c) + ACCENT_START);
    } else if (c == '\n') {
      return NEWLINE;
    }
    return 0;
  }

  /**
   * Determines whether the specified character is in the
   * translation table.
   * @param c character
   * @return true if in translation table, false otherwise
   */
  private boolean isInTranslationTable(final char c) {
    return getIndexInTranslationTable(c) >= 0;
  }

  /**
   * Determines the index of character c in the translation
   * table.
   * @param c character
   * @return index in translation table
   */
  private int getIndexInTranslationTable(final char c) {
    for (int i = 0; i < accentTable.getLength(); i++) {
      if (accentTable.getAccent(i) == c) return i;
    }
    return -1;
  }

  /**
   * Tests the given ZSCII character if it falls in the ASCII range.
   * @param zchar the input character
   * @return true if in the ASCII range, false, otherwise
   */
  public static boolean isAscii(final char zchar) {
    return zchar >= ASCII_START && zchar <= ASCII_END;
  }

  /**
   * Tests the given ZSCII character for whether it is in the special range.
   * @param zchar the input character
   * @return true if in special range, false, otherwise
   */
  public static boolean isAccent(final char zchar) {
    return zchar >= ACCENT_START && zchar <= ACCENT_END;
  }

  /**
   * Returns true if zsciiChar is a cursor key.
   * @param zsciiChar a cursor key
   * @return true if cursor key, false, otherwise
   */
  public static boolean isCursorKey(final char zsciiChar) {
    return zsciiChar >= CURSOR_UP && zsciiChar <= CURSOR_RIGHT;
  }

  /**
   * Returns true if zchar is in the unicode range.
   * @param zchar a zscii character
   * @return the unicode character
   */
  private static boolean isUnicodeCharacter(final char zchar) {
    return zchar >= 256;
  }

  /**
   * Returns true if zsciiChar is a function key.
   * @param zsciiChar the zscii char
   * @return true if function key, false, otherwise
   */
  public static boolean isFunctionKey(final char zsciiChar) {
    return (zsciiChar >= 129 && zsciiChar <= 154)
           || (zsciiChar >= 252 && zsciiChar <= 254);
  }

  /**
   * Converts the character to lower case.
   * @param zsciiChar the ZSCII character to convert
   * @return the lower case character
   */
  public char toLower(final char zsciiChar) {
    if (isAscii(zsciiChar)) {
      return Character.toLowerCase(zsciiChar);
    }
    if (isAccent(zsciiChar)) {
      return (char) (accentTable.getIndexOfLowerCase(zsciiChar - ACCENT_START) +
                     ACCENT_START);
    }
    return zsciiChar;
  }
}
