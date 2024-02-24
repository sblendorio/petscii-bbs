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

/**
 * ZsciiEncoding interface.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface IZsciiEncoding {

  char NULL          = 0;
  char DELETE        = 8;
  char NEWLINE_10    = 10;
  char NEWLINE       = 13;
  char ESCAPE        = 27;
  char CURSOR_UP     = 129;
  char CURSOR_DOWN   = 130;
  char CURSOR_LEFT   = 131;
  char CURSOR_RIGHT  = 132;
  char ASCII_START   = 32;
  char ASCII_END     = 126;

  /** The start of the accent range. */
  char ACCENT_START = 155;

  /** End of the accent range. */
  char ACCENT_END   = 251;

  char MOUSE_DOUBLE_CLICK = 253;
  char MOUSE_SINGLE_CLICK = 254;

  /**
   * Converts the specified string into its ZSCII representation.
   * @param str the input string
   * @return the ZSCII representation
   */
  String convertToZscii(String str);

  /**
   * Converts a ZSCII character to a unicode character. Will return
   * '?' if the given character is not known.
   * @param zsciiChar a ZSCII character.
   * @return the unicode representation
   */
  char getUnicodeChar(char zsciiChar);
}
