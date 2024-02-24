/*
 * Created on 2006/01/09
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

import org.zmpp.base.Memory;
import org.zmpp.encoding.ZCharDecoder;
import org.zmpp.encoding.ZCharEncoder;

/**
 * This class implements a user dictionary. The specification suggests that
 * lookup is implemented using linear search in case the user dictionary
 * is specified as unordered (negative number of entries) and in case of
 * ordered a binary search will be performed.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class UserDictionary extends AbstractDictionary {

  /**
   * Constructor.
   * @param memory the Memory object
   * @param address the start address of the dictionary
   * @param decoder a ZCharDecoder object
   * @param encoder a ZCharEncoder object
   */
  public UserDictionary(Memory memory, int address,
                        ZCharDecoder decoder, ZCharEncoder encoder) {
    super(memory, address, decoder, encoder, new DictionarySizesV4ToV8());
  }

  /** {@inheritDoc} */
  public int lookup(final String token) {
    // We only implement linear search for user dictionaries
    final int n = Math.abs(getNumberOfEntries());
    final byte[] tokenBytes = truncateTokenToBytes(token);
    for (int i = 0; i < n; i++) {
      final int entryAddress = getEntryAddress(i);
      if (tokenMatch(tokenBytes, entryAddress) == 0) { return entryAddress; }
    }
    return 0;
  }
}
