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

import org.zmpp.base.DefaultMemory;
import org.zmpp.base.Memory;
import static org.zmpp.base.MemoryUtil.readUnsigned32;
import static org.zmpp.base.MemoryUtil.writeUnsigned32;

/**
 * This is the default implementation of the Chunk interface.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class DefaultChunk implements Chunk {

  /** The memory access object. */
  protected Memory memory;

  /** The chunk id. */
  private byte[] id;

  /** The chunk size. */
  private int chunkSize;

  /** The start address within the form chunk. */
  private int address;

  /**
   * Constructor. Used for reading files.
   * @param memory a Memory object to the chunk data
   * @param address the address within the form chunk
   */
  public DefaultChunk(final Memory memory, final int address) {
    this.memory = memory;
    this.address = address;
    id = new byte[CHUNK_ID_LENGTH];
    memory.copyBytesToArray(id, 0, 0, CHUNK_ID_LENGTH);
    chunkSize = (int) readUnsigned32(memory, CHUNK_ID_LENGTH);
  }

  /**
   * Constructor. Initialize from byte data. This constructor is used
   * when writing a file, in that case chunks really are separate
   * memory areas.
   * @param id the id
   * @param chunkdata the data without header information, number of bytes
   * needs to be even
   */
  public DefaultChunk(final byte[] id, final byte[] chunkdata) {
    this.id = id;
    this.chunkSize = chunkdata.length;
    final byte[] chunkDataWithHeader =
      new byte[chunkSize + Chunk.CHUNK_HEADER_LENGTH];
    this.memory = new DefaultMemory(chunkDataWithHeader);
    memory.copyBytesFromArray(id, 0, 0, id.length);
    writeUnsigned32(memory, id.length, chunkSize);
    memory.copyBytesFromArray(chunkdata, 0, id.length + 4,
                              chunkdata.length);
  }

  /** {@inheritDoc} */
  public boolean isValid() { return true; }

  /** {@inheritDoc} */
  public String getId() { return new String(id); }

  /** {@inheritDoc} */
  public int getSize() { return chunkSize; }

  /** {@inheritDoc} */
  public Memory getMemory() { return memory; }

  /** {@inheritDoc} */
  public int getAddress() { return address; }
}
