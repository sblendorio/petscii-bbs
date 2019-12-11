/*
 * $Id: ZsciiStringTokenizer.java,v 1.3 2006/04/12 18:00:08 weiju Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is similar to StringTokenizer, but more specialized. In
 * particular, it operates on ZsciiString objects and always returns the
 * delimiters in the token stream.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ZsciiStringTokenizer {

  /**
   * An iterator to implement the tokenizer.
   */
  private Iterator<ZsciiString> iterator;
  
  /**
   * Constructor.
   * 
   * @param input
   * @param delim
   */
  public ZsciiStringTokenizer(final ZsciiString input,
      final ZsciiString delim) {
  
    tokenize(input, delim);
  }
  
  /**
   * Returns true, if there are more tokens to process.
   * 
   * @return true, if there are more tokens, false, otherwise
   */
  public boolean hasMoreTokens() {
    
    return iterator.hasNext();
  }
  
  /**
   * Returns the next token.
   * 
   * @return the next token
   */
  public ZsciiString nextToken() {
    
    return iterator.next();
  }
  
  /**
   * This is the main algorithm. It sequentially processes the input and
   * checks if the character is a delimiter. Its not a very fast algorithm,
   * but its simple.
   * 
   * @param input the input string
   * @param delim the delimiter string
   */
  private void tokenize(final ZsciiString input, final ZsciiString delim) {
   
    final List<ZsciiString> tokens = new ArrayList<ZsciiString>();
    int currentIndex = 0;
    final int inputlength = input.length();
    ZsciiStringBuilder currentTokenBuilder = new ZsciiStringBuilder();
    
    while (currentIndex < inputlength) {
      
      final short currentChar = input.charAt(currentIndex);
      if (isDelimiter(currentChar, delim)) {

        if (currentTokenBuilder.length() > 0) {
                    
          tokens.add(currentTokenBuilder.toZsciiString());
          currentTokenBuilder = new ZsciiStringBuilder();
        }
        tokens.add(new ZsciiString(new short[] { currentChar }));
        
      } else {
        
        currentTokenBuilder.append(currentChar);
      }
      currentIndex++;
    }
    
    // If there is still a token in the builder
    if (currentTokenBuilder.length() > 0) {
      
      tokens.add(currentTokenBuilder.toZsciiString());
    }
    
    iterator = tokens.iterator();
  }

  /**
   * Returns true if the specified character is in the delimiter string.
   * 
   * @param zsciiChar the ZSCII character
   * @param delim the delimiter string
   * @return true, delimiter, false, otherwise
   */
  private static boolean isDelimiter(final short zsciiChar,
      final ZsciiString delim) {
    
    for (int i = 0, n = delim.length(); i < n; i++) {
      
      if (zsciiChar == delim.charAt(i)) {
        return true;
      }
    }
    return false;
  }
}
