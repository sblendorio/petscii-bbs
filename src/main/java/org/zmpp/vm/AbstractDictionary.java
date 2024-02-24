/*
 * Created on 2006/09/25
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

import static org.zmpp.base.MemoryUtil.unsignedToSigned16;

import org.zmpp.base.DefaultMemory;
import org.zmpp.base.Memory;
import org.zmpp.encoding.DictionarySizes;
import org.zmpp.encoding.ZCharDecoder;
import org.zmpp.encoding.ZCharEncoder;

/**
 * Abstract super class of dictionaries.
 * @author Wei-ju Wu
 * @version 1.5
 */
public abstract class AbstractDictionary implements Dictionary {

  private Memory memory;

  /** The dictionary start address. */
  private int address;
  private ZCharDecoder decoder;
  private ZCharEncoder encoder;

  /** A sizes object. */
  private DictionarySizes sizes;

  /**
   * Constructor.
   * @param memory the memory object
   * @param address the start address of the dictionary
   * @param decoder a ZCharDecoder object
   * @param encoder a ZCharEncoder object
   * @param sizes an object specifying the sizes of the dictionary entries
   */
  public AbstractDictionary(final Memory memory, final int address,
                            final ZCharDecoder decoder,
                            final ZCharEncoder encoder,
                            final DictionarySizes sizes) {
    this.memory = memory;
    this.address = address;
    this.decoder = decoder;
    this.encoder = encoder;
    this.sizes = sizes;
  }

  /** {@inheritDoc} */
  public int getNumberOfSeparators() {
    return memory.readUnsigned8(address);
  }

  /** {@inheritDoc} */
  public byte getSeparator(final int i) {
    return (byte) memory.readUnsigned8(address + i + 1);
  }

  /** {@inheritDoc} */
  public int getEntryLength() {
    return memory.readUnsigned8(address + getNumberOfSeparators() + 1);
  }

  /** {@inheritDoc} */
  public short getNumberOfEntries() {
    // The number of entries is a signed value so that we can recognize
    // a negative number
    return unsignedToSigned16(memory.readUnsigned16(address +
        getNumberOfSeparators() + 2));
  }

  /** {@inheritDoc} */
  public int getEntryAddress(final int entryNum) {
    final int headerSize = getNumberOfSeparators() + 4;
    return address + headerSize + entryNum * getEntryLength();
  }

  /**
   * Access to the decoder object.
   * @return the decoder object
   */
  protected ZCharDecoder getDecoder() { return decoder; }

  /**
   * Access to the Memory object.
   * @return the Memory object
   */
  protected Memory getMemory() { return memory; }

  /**
   * Returns the DictionarySizes object for the current story file version.
   * @return the DictionarySizes object
   */
  protected DictionarySizes getSizes() { return sizes; }

  /**
   * Unfortunately it seems that the maximum size of an entry is not equal
   * to the size declared in the dictionary header, therefore we take
   * the maximum length of a token defined in the Z-machine specification.
   * The lookup token can only be 6 characters long in version 3
   * and 9 in versions >= 4
   * @param token the token to truncate
   * @return the truncated token
   */
  protected String truncateToken(final String token) {
    return token.length() > sizes.getMaxEntryChars() ?
        token.substring(0, sizes.getMaxEntryChars()) : token;
  }

  /**
   * Truncates the specified token and returns a dictionary encoded byte array.
   * @param token the token
   * @return the truncated token as a byte array
   */
  protected byte[] truncateTokenToBytes(final String token) {
    byte[] result = new byte[sizes.getNumEntryBytes()];
    Memory buffer = new DefaultMemory(result);
    encoder.encode(token, buffer, 0);
    return result;
  }

  /**
   * Lexicographical comparison of the input word and the dictionary entry
   * at the specified address.
   * @param tokenBytes input word bytes
   * @param entryAddress dictionary entry address
   * @return comparison value, 0 if match, &lt; 0 if lexicographical smaller,
   *         &lt; 0 if lexicographical greater
   */
  protected int tokenMatch(byte[] tokenBytes, int entryAddress) {
    for (int i = 0; i < tokenBytes.length; i++) {
      int tokenByte = tokenBytes[i] & 0xff;
      int c = (getMemory().readUnsigned8(entryAddress + i) & 0xff);
      if (tokenByte != c) {
        return tokenByte - c;
      }
    }
    return 0;
  }

  /**
   * Creates a string presentation of this dictionary.
   * @return the string presentation
   */
  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    int entryAddress;
    int i = 0;
    final int n = getNumberOfEntries();
    while (true) {
      entryAddress = getEntryAddress(i);
      final String str = getDecoder().decode2Zscii(getMemory(),
          entryAddress, sizes.getNumEntryBytes());
      buffer.append(String.format("[%4d] '%-9s' ", (i + 1), str));
      i++;
      if ((i % 4) == 0) { buffer.append("\n"); }
      if (i == n) { break; }
    }
    return buffer.toString();
  }
}
