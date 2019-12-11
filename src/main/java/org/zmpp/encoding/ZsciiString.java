/*
 * $Id: ZsciiString.java,v 1.5 2006/04/12 18:00:08 weiju Exp $
 * 
 * Created on 2006/02/01
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
 * This class represents ZSCII strings. These are especially important to
 * the input system since the dictionaries store their entries in ZSCII
 * and the input will be converted into this encoding.
 * 
 * ZSCII strings are represented as a sequence of 16-bit characters.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ZsciiString {

  /**
   * The encoding object.
   */
  private static ZsciiEncoding encoding;

  /**
   * The string data.
   */
  private short[] data;
  
  /**
   * Global initialization method.
   * 
   * @param encoding the encoding
   */
  public static void initialize(final ZsciiEncoding encoding) {
 
    ZsciiString.encoding = encoding;
  }

  /**
   * Constructor.
   * 
   * @param data the source array
   */
  public ZsciiString(final short[] data) {
    
    super();
    this.data = data;
  }
  
  /**
   * Creates a ZSCII string from a string.
   * 
   * @param str the string
   */
  public ZsciiString(final String str) {
    
    super();
    this.data = encoding.convertToZscii(str);
  }

  /**
   * Returns the ZSCII character at the specified position.
   * 
   * @param pos the position
   * @return the character
   */
  public short charAt(final int pos) {
    
    return data[pos];
  }
  
  /**
   * Returns the length of this string.
   * 
   * @return the length
   */
  public int length() {
    
    return data.length;
  }
  
  /**
   * Returns the index of the first occurrence of the specified sub string.
   *  
   * @param str the string
   * @param startIndex the start index
   * @return the first index
   */
  public int indexOf(final ZsciiString str, final int startIndex) {

    int current = startIndex;
    final int length = length(); 
    final int n = str.length();
    
    while (current < length) {
      
      if (charAt(current) == str.charAt(0)) {
        
        // Compare all characters of str from the index on
        int i = 1;
        for (; i < n; i++) {

          if (charAt(current + i) != str.charAt(i)) {
            break;
          }
        }
        if (i == n) {
          return current;
        }
      }
      current++;
    }
    return -1;
  }
  
  /**
   * Returns a new string that is a substring of this string. The substring
   * begins at the specified startindex and extends to the character at
   * index endindex - 1. Thus the length of the substring is
   * endindex-startindex.
   * 
   * @param startindex start index
   * @param endindex end index
   * @return the sub string
   */
  public ZsciiString substring(final int startindex, final int endindex) {

    final int n = endindex - startindex;
    final short[] dat = new short[n];
    for (int i = 0; i < n; i++) {
      
      dat[i] = charAt(startindex + i);
    }
    return new ZsciiString(dat);
  }
  
  
  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    
    int hashvalue = 0;
    for (int i = 0; i < data.length; i++) {
      
      hashvalue = 31 * hashvalue + data[i];
    }
    return hashvalue;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean equals(final Object o) {
    
    if (o == this) {
      return true;
    }
    if (o instanceof ZsciiString) {
      
      final short[] data2 = ((ZsciiString) o).data;
      if (data.length == data2.length) {
        
        for (int i = 0; i < data.length; i++) {
        
          if (data[i] != data2[i]) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  /**
   * {@inheritDoc}
   */
  public String toString() {
   
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      
      builder.append(encoding.getUnicodeChar(data[i]));
    }
    return builder.toString();
  }
}
