/*
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
 * This class represents an alphabet element which is an alphabet and
 * an index to that alphabet. We need this to determine what kind of
 * encoding we need.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class AlphabetElement {

  /** The zchar code or the ZSCII code, if alphabet is null. */
  private char zcharCode;

  /** The alphabet or null, if index is a ZSCII code. */
  private Alphabet alphabet;

  /**
   * Constructor.
   * @param alphabet the alphabet (can be null)
   * @param zcharCode the zcharCode in the alphabet or the ZSCII code
   */
  public AlphabetElement(Alphabet alphabet, char zcharCode) {
    this.alphabet = alphabet;
    this.zcharCode = zcharCode;
  }

  /**
   * Returns the alphabet. Can be null, in that case index represents the
   * ZSCII code.
   * @return the alphabet
   */
  public Alphabet getAlphabet() { return alphabet; }

  /**
   * Returns the index to the table. If the alphabet is null, this is the
   * plain ZSCII code and should be turned into a 10-bit code by the
   * encoder.
   * @return the z char code in the specified alphabet or the ZSCII code
   */
  public char getZCharCode() { return zcharCode; }
}
