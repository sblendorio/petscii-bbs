/*
 * $Id: WindowStyle.java,v 1.1 2006/02/27 18:55:52 weiju Exp $
 * 
 * Created on 2006/02/27
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
package org.zmpp.swingui;

/**
 * This class handles the window styles used in model 6.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class WindowStyle {

  private boolean isWrapped;
  private boolean isScrolled;
  private boolean isCopiedToStream2;
  private boolean isBuffered;
  
  public static final int FLAG_WORD_WRAP      = 1;
  public static final int FLAG_SCROLL         = 2;
  public static final int FLAG_COPYTO_STREAM2 = 4;
  public static final int FLAG_BUFFEROUTPUT   = 8;
  
  public static final int OP_SET_FLAGS    = 0;
  public static final int OP_SET_BITS     = 1;
  public static final int OP_CLEAR_BITS   = 2;
  public static final int OP_REVERSE_BITS = 3;
  
  
  public boolean isWrapped() { return isWrapped; }
  
  public boolean isScrolled() { return isScrolled; }
  
  public boolean isCopiedToStream2() { return isCopiedToStream2; }
  
  public boolean outputIsBuffered() { return isBuffered; }
  
  public void setIsWrapped(boolean flag) { isWrapped = flag; }
  public void setIsScrolled(boolean flag) { isScrolled = flag; }
  
  /**
   * Sets the window style.
   * The <i>styleflags</i> parameter is a bitmask specified as follows:
   * - Bit 0: keep text within margins
   * - Bit 1: scroll when at bottom
   * - Bit 2: copy text to transcript stream (stream 2)
   * - Bit 3: word wrapping
   * 
   * The <i>operation</i> parameter is specified as this:
   * - 0: set style flags to the specified mask
   * - 1: set the bits supplied
   * - 2: clear the bits supplied
   * - 3: reverse the bits supplied 
   * 
   * @param styleflags the style flags
   * @param op the operations
   */
  public void setFlags(int flags, int operation) {

    switch (operation) {
    
    case OP_SET_FLAGS:
      setFlags(flags);
      break;
    case OP_SET_BITS:
      setBits(flags);
      break;
    case OP_CLEAR_BITS:
      clearBits(flags);
      break;
    case OP_REVERSE_BITS:
      reverseBits(flags);
      break;
    default:
      break;
    }
  }
  
  public int getFlags() {
    
    int result = 0;
    if (isWrapped) result |= FLAG_WORD_WRAP;
    if (isScrolled) result |= FLAG_SCROLL;
    if (isCopiedToStream2) result |= FLAG_COPYTO_STREAM2;
    if (isBuffered) result |= FLAG_BUFFEROUTPUT;
    
    return result;
  }
  
  private void setFlags(int flags) {
 
    isWrapped = ((flags & FLAG_WORD_WRAP) > 0);
    isScrolled = ((flags & FLAG_SCROLL) > 0);
    isCopiedToStream2 = ((flags & FLAG_COPYTO_STREAM2) > 0);
    isBuffered = ((flags & FLAG_BUFFEROUTPUT) > 0);
  }

  private void setBits(int flags) {
    
    if ((flags & FLAG_WORD_WRAP) > 0) isWrapped = true;      
    if ((flags & FLAG_SCROLL) > 0) isScrolled = true;
    if ((flags & FLAG_COPYTO_STREAM2) > 0) isCopiedToStream2 = true;
    if ((flags & FLAG_BUFFEROUTPUT) > 0) isBuffered = true;
  }

  private void clearBits(int flags) {
    
    if ((flags & FLAG_WORD_WRAP) > 0) isWrapped = false;      
    if ((flags & FLAG_SCROLL) > 0) isScrolled = false;
    if ((flags & FLAG_COPYTO_STREAM2) > 0) isCopiedToStream2 = false;
    if ((flags & FLAG_BUFFEROUTPUT) > 0) isBuffered = false;
  }
  
  private void reverseBits(int flags) {
    
    if ((flags & FLAG_WORD_WRAP) > 0) isWrapped = !isWrapped;      
    if ((flags & FLAG_SCROLL) > 0) isScrolled = !isScrolled;
    if ((flags & FLAG_COPYTO_STREAM2) > 0) isCopiedToStream2 = !isCopiedToStream2;
    if ((flags & FLAG_BUFFEROUTPUT) > 0) isBuffered = !isBuffered;
  }
}
