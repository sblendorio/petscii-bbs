/*
 * $Id: ZsciiEncoding.java,v 1.8 2006/04/12 18:00:08 weiju Exp $
 * 
 * Created on 2005/11/10
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
 * @version 1.0
 */
public class ZsciiEncoding {

  public static final short NULL          = 0;
  public static final short DELETE        = 8;
  public static final short NEWLINE_10    = 10;
  public static final short NEWLINE       = 13;
  public static final short ESCAPE        = 27;
  public static final short CURSOR_UP     = 129;
  public static final short CURSOR_DOWN   = 130;
  public static final short CURSOR_LEFT   = 131;
  public static final short CURSOR_RIGHT  = 132;
  public static final short ASCII_START   = 32;
  public static final short ASCII_END     = 126;
  
  /**
   * The start of the accent range. 
   */
  public static final short ACCENT_START = 155;
  
  /**
   * End of the accent range.
   */
  public static final short ACCENT_END   = 251;
  
  
  public static final short MOUSE_DOUBLE_CLICK = 253;
  public static final short MOUSE_SINGLE_CLICK = 254;
  
  private AccentTable accentTable;

  /**
   * Constructor.
   * 
   * @param accentTable the accent table.
   */
  public ZsciiEncoding(final AccentTable accentTable) {
  
    super();
    this.accentTable = accentTable;
  }
  
  /**
   * Returns true if the input is a valid ZSCII character, false otherwise.
   * 
   * @param zchar a ZSCII character
   * @return true if valid, false otherwise
   */
  public boolean isZsciiCharacter(final short zchar) {
   
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
   * 
   * @param c a unicode character
   * @return true if c can be converted, false, otherwise
   */
  public boolean isConvertableToZscii(final char c) {
   
    return isAscii((short) c) || isInTranslationTable(c) || c == '\n'
           || c == 0 || isUnicodeCharacter((short) c);
  }
  
  /**
   * Converts a ZSCII character to a unicode character. Will return
   * '?' if the given character is not known.
   * 
   * @param zchar a ZSCII character.
   * @return the unicode representation
   */
  public char getUnicodeChar(final short zchar) {
    
    if (isAscii(zchar)) {
      return (char) zchar;
    }
    if (isAccent(zchar)) {
      
      final int index = zchar - ACCENT_START;
      if (index < accentTable.getLength()) {
       
        return (char) accentTable.getAccent(index);
      }
    }
    if (zchar == NULL) {
      return '\0';
    }
    if (zchar == NEWLINE || zchar == NEWLINE_10) {
      return '\n';
    }
    if (isUnicodeCharacter(zchar)) {
      return (char) zchar;
    }
    return '?';
  }
  
  /**
   * Converts the specified string into its ZSCII representation.
   * 
   * @param str the input string
   * @return the ZSCII representation
   */
  public short[] convertToZscii(final String str) {
    
    final short[] result = new short[str.length()];
    for (int i = 0; i < str.length(); i++) {
      
      result[i] = getZsciiChar(str.charAt(i));
    }
    return result;
  }
  
  /**
   * Converts the specified unicode character to a ZSCII character.
   * Will return 0 if the character can not be converted.
   * 
   * @param c the unicode character to convert
   * @return the ZSCII character
   */
  public short getZsciiChar(final char c) {
    
    if (isAscii((short) c)) {
      
      return (short) c;
      
    } else if (isInTranslationTable(c)) {
      
      return (short) (getIndexInTranslationTable(c) + ACCENT_START);
      
    } else if (c == '\n') {
      
      return NEWLINE;
    }
    return 0;
  }
  
  private boolean isInTranslationTable(final char c) {
    
    for (int i = 0; i < accentTable.getLength(); i++) {
      
      if (accentTable.getAccent(i) == c) {
        return true;
      }
    }
    return false;
  }

  private int getIndexInTranslationTable(final char c) {
    
    for (int i = 0; i < accentTable.getLength(); i++) {
      
      if (accentTable.getAccent(i) == c) {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * Tests the given ZSCII character if it falls in the ASCII range.
   * 
   * @param zchar the input character
   * @return true if in the ASCII range, false, otherwise
   */
  public static boolean isAscii(final short zchar) {
    
    return zchar >= ASCII_START && zchar <= ASCII_END;
  }
  
  /**
   * Tests the given ZSCII character for whether it is in the special range.
   * 
   * @param zchar the input character
   * @return true if in special range, false, otherwise
   */
  public static boolean isAccent(final short zchar) {
    
    return zchar >= ACCENT_START && zchar <= ACCENT_END;
  }
  
  /**
   * Returns true if zsciiChar is a cursor key.
   * 
   * @param zsciiChar a cursor key
   * @return true if cursor key, false, otherwise
   */
  public static boolean isCursorKey(final short zsciiChar) {
    
    return zsciiChar >= CURSOR_UP && zsciiChar <= CURSOR_RIGHT; 
  }
  
  /**
   * Returns true if zchar is in the unicode range.
   * 
   * @param zchar a zscii character
   * @return the unicode character
   */
  private static boolean isUnicodeCharacter(final short zchar) {
    
    return zchar >= 256;
  }
  
  /**
   * Returns true if zsciiChar is a function key.
   * 
   * @param zsciiChar the zscii char
   * @return true if function key, false, otherwise
   */
  public static boolean isFunctionKey(final short zsciiChar) {
    
    return (zsciiChar >= 129 && zsciiChar <= 154)
           || (zsciiChar >= 252 && zsciiChar <= 254);
  }
  
  /**
   * Converts the character to lower case.
   * 
   * @param zsciiChar the ZSCII character to convert
   * @return the lower case character
   */
  public short toLower(final short zsciiChar) {
  
    if (isAscii(zsciiChar)) {
      
      return (short) Character.toLowerCase(zsciiChar);
    }
    if (isAccent(zsciiChar)) {
      
      return (short) (accentTable.getIndexOfLowerCase(zsciiChar - ACCENT_START)
                      + ACCENT_START);
    }
    return zsciiChar;
  }
}
