/*
 * Created on 2005/09/23
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

import org.zmpp.base.Memory;

/**
 * This interface provides decoding for the Z character encoding into
 * the Java character system. It is important to point out that there
 * is a difference between Z characters and the ZCSII encoding. Where
 * ZSCII is a character set that is similar to ASCII/iso-8859-1, the
 * Z characters are a encoded form of characters in memory that provide
 * some degree of compression and encryption.
 *
 * ZCharConverter uses the alphabet tables specified in the Z machine
 * standards document 1.0, section 3.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface ZCharDecoder {

  /**
   * This interface defines the abstract access to an abbreviations
   * table in memory, this will be used for decoding if needed.
   */
  public interface AbbreviationsTable {
    /**
     * Returns the word address of the specified entry.
     * @param entryNum entry number
     * @return word address
     */
    int getWordAddress(int entryNum);
  }

  /**
   * Performs a ZSCII decoding at the specified position of
   * the given memory object, this method is exclusively designed to
   * deal with the problems of dictionary entries. These can be cropped,
   * leaving the string in a state, that can not be decoded properly
   * otherwise. If the provided length is 0, the semantics are
   * equal to the method without the length parameter.
   * @param memory a Memory object
   * @param address the address of the string
   * @param length the maximum length in bytes
   * @return the decoded string
   */
  String decode2Zscii(Memory memory, int address, int length);

  /**
   * Returns the number of Z encoded bytes at the specified position.
   * @param memory the Memory object
   * @param address the string address
   * @return the number Z encoded bytes
   */
  int getNumZEncodedBytes(Memory memory, int address);

  /**
   * Decodes the given byte value to the specified buffer using the working
   * alphabet.
   * @param zchar a z encoded character, needs to be a non-shift character
   * @return decoded character
   */
  char decodeZChar(char zchar);

  /**
   * Returns the ZStringTranslator.
   * @return the translator
   */
  ZCharTranslator getTranslator();
}
