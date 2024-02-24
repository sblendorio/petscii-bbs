/*
 * Created on 09/24/2005
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
package org.zmpp.vm;

/**
 * This is the interface definition for a dictionary.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Dictionary {

  /**
   * Returns the number of separators.
   * @return the number of separators
   */
  int getNumberOfSeparators();

  /**
   * Returns the separator at position i as a ZSCII character.
   * @param i the separator number, zero-based
   * @return the separator
   */
  byte getSeparator(int i);

  /**
   * Returns the length of a dictionary entry.
   * @return the entry length
   */
  int getEntryLength();

  /**
   * Returns the number of dictionary entries.
   * @return the number of entries
   */
  short getNumberOfEntries();

  /**
   * Returns the entry address at the specified position.
   * @param entryNum entry number between (0 - getNumberOfEntries() - 1)
   * @return the entry address
   */
  int getEntryAddress(int entryNum);

  /**
   * Looks up a string in the dictionary. The word will be truncated to
   * the maximum word length and looked up. The result is the address
   * of the entry or 0 if it is not found.
   * @param token a token in ZSCII encoding
   * @return the address of the token or 0
   */
  int lookup(String token);
}
