/*
 * $Id: ZsciiStringBuilder.java,v 1.2 2006/04/12 02:04:29 weiju Exp $
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
import java.util.List;

/**
 * This class works similar to the StringBuilder class in that it is
 * not synchronized and supports appending characters.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ZsciiStringBuilder {

  /**
   * This list holds the data.
   */
  private List<Short> data = new ArrayList<Short>();

  /**
   * Adds a ZSCII character to the builder object.
   * 
   * @param zsciiChar the ZSCII char to add
   */
  public void append(final short zsciiChar) {
    
    data.add(zsciiChar);
  }
  
  /**
   * Appends the characters of the specified ZSCII string to this builder
   * object.
   * 
   * @param str a ZSCII string
   */
  public void append(final ZsciiString str) {
    
    for (int i = 0, n = str.length(); i < n; i++) {
      
      append(str.charAt(i));
    }
  }
  
  /**
   * Retrieves a ZSCII string.
   * 
   * @return the ZSCII string
   */
  public ZsciiString toZsciiString() {
  
    final short[] strdata = new short[data.size()];
    for (int i = 0; i < strdata.length; i++) {
      
      strdata[i] = data.get(i);
    }
    return new ZsciiString(strdata);
  }
  
  /**
   * Returns the current length of the buffer.
   *  
   * @return the length
   */
  public int length() {
    
    return data.size();
  }
  
  /**
   * {@inheritDoc}
   */
  public String toString() {
    
    return toZsciiString().toString();
  }  
}
