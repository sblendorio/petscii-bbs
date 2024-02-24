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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zmpp.base.DefaultMemory;
import org.zmpp.base.Memory;
import static org.zmpp.base.MemoryUtil.writeUnsigned32;

/**
 * A writable FormChunk class.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class WritableFormChunk implements FormChunk {

  private byte[] subId;
  private static final String FORM_ID = "FORM";
  private List<Chunk> subChunks;

  /**
   * Constructor.
   * @param subId the sub id
   */
  public WritableFormChunk(final byte[] subId) {
    this.subId = subId;
    this.subChunks = new ArrayList<Chunk>();
  }

  /**
   * Adds a sub chunk.
   * @param chunk the sub chunk to add
   */
  public void addChunk(final Chunk chunk) {
    subChunks.add(chunk);
  }

  /** {@inheritDoc} */
  public String getSubId() {
    return new String(subId);
  }

  /** {@inheritDoc} */
  public Iterator<Chunk> getSubChunks() {
    return subChunks.iterator();
  }

  /** {@inheritDoc} */
  public Chunk getSubChunk(final String id) {
    for (Chunk chunk : subChunks) {
      if (chunk.getId().equals(id)) return chunk;
    }
    return null;
  }

  /** {@inheritDoc} */
  public Chunk getSubChunk(final int address) {
    // We do not need to implement this
    return null;
  }

  /** {@inheritDoc} */
  public String getId() { return FORM_ID; }

  /** {@inheritDoc} */
  public int getSize() {
    int size = subId.length;

    for (Chunk chunk : subChunks) {
      int chunkSize = chunk.getSize();
      if ((chunkSize % 2) != 0) {
        chunkSize++; // pad if necessary
      }
      size += (Chunk.CHUNK_HEADER_LENGTH + chunkSize);
    }
    return size;
  }

  /** {@inheritDoc} */
  public boolean isValid() { return true; }

  /** {@inheritDoc} */
  public Memory getMemory() { return new DefaultMemory(getBytes()); }

  /**
   * Returns the data of this chunk.
   * @return the chunk data
   */
  public byte[] getBytes() {
    final int datasize = Chunk.CHUNK_HEADER_LENGTH + getSize();
    final byte[] data = new byte[datasize];
    final Memory memory = new DefaultMemory(data);
    memory.writeUnsigned8(0, 'F');
    memory.writeUnsigned8(1, 'O');
    memory.writeUnsigned8(2, 'R');
    memory.writeUnsigned8(3, 'M');
    writeUnsigned32(memory, 4, getSize());

    int offset = Chunk.CHUNK_HEADER_LENGTH;

    // Write sub id
    memory.copyBytesFromArray(subId, 0, offset, subId.length);
    offset += subId.length;

    // Write sub chunk data
    for (Chunk chunk : subChunks) {
      final byte[] chunkId = chunk.getId().getBytes();
      final int chunkSize = chunk.getSize();

      // Write id
      memory.copyBytesFromArray(chunkId, 0, offset, chunkId.length);
      offset += chunkId.length;

      // Write chunk size
      writeUnsigned32(memory, offset, chunkSize);
      offset += 4; // add the size word length

      // Write chunk data
      final Memory chunkMem = chunk.getMemory();
      memory.copyBytesFromMemory(chunkMem, Chunk.CHUNK_HEADER_LENGTH, offset,
                                 chunkSize);
      offset += chunkSize;
      // Pad if necessary
      if ((chunkSize % 2) != 0) {
        memory.writeUnsigned8(offset++, (char) 0);
      }
    }
    return data;
  }

  /** {@inheritDoc} */
  public int getAddress() { return 0; }
}
