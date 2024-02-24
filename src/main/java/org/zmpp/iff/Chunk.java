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
package org.zmpp.iff;

import org.zmpp.base.Memory;

/**
 * The basic data structure for an IFF file, a chunk.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Chunk {

  /** The length of an IFF chunk id in bytes. */
  int CHUNK_ID_LENGTH = 4;

  /** The length of an IFF chunk size word in bytes. */
  int CHUNK_SIZEWORD_LENGTH = 4;

  /** The chunk header size. */
  int CHUNK_HEADER_LENGTH = CHUNK_ID_LENGTH + CHUNK_SIZEWORD_LENGTH;

  /**
   * Returns this IFF chunk's id. An id is a 4 byte array.
   * @return the id
   */
  String getId();

  /**
   * The chunk data size, excluding id and size word.
   * @return the size
   */
  int getSize();

  /**
   * Returns true if this is a valid chunk.
   * @return true if valid, false otherwise
   */
  boolean isValid();

  /**
   * Returns a memory object to access the chunk.
   * @return the Memory object
   */
  Memory getMemory();

  /**
   * Returns the address of the chunk within the global FORM chunk.
   * @return the address within the form chunk
   */
  int getAddress();
}
