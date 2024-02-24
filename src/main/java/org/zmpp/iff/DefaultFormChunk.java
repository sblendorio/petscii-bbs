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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zmpp.base.Memory;
import org.zmpp.base.MemorySection;

/**
 * This class implements the FormChunk interface.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class DefaultFormChunk extends DefaultChunk implements FormChunk {

  /** The sub type id. */
  private byte[] subId;

  /** The list of sub chunks. */
  private List<Chunk> subChunks;

  /**
   * Constructor.
   * @param memory a MemoryAccess object
   * @throws IOException if i/o exception occurred
   */
  public DefaultFormChunk(final Memory memory) throws IOException {
    super(memory, 0);
    initBaseInfo();
    readSubChunks();
  }

  /**
   * Initialize the id field.
   * @throws IOException if i/o exception occurred
   */
  private void initBaseInfo() throws IOException {
    if (!"FORM".equals(getId())) {
      throw new IOException("not a valid IFF format");
    }
    // Determine the sub id
    subId = new byte[CHUNK_ID_LENGTH];
    memory.copyBytesToArray(subId, 0, CHUNK_HEADER_LENGTH,
                            Chunk.CHUNK_ID_LENGTH);
  }

  /**
   * Read this form chunk's sub chunks.
   */
  private void readSubChunks() {
    subChunks = new ArrayList<Chunk>();

    // skip the identifying information
    final int length = getSize();
    int offset = CHUNK_HEADER_LENGTH + CHUNK_ID_LENGTH;
    int chunkTotalSize = 0;

    while (offset < length) {
      final Memory memarray = new MemorySection(memory, offset,
                                                      length - offset);
      final Chunk subchunk = new DefaultChunk(memarray, offset);
      subChunks.add(subchunk);
      chunkTotalSize = subchunk.getSize() + CHUNK_HEADER_LENGTH;

      // Determine if padding is necessary
      chunkTotalSize = (chunkTotalSize % 2) == 0 ? chunkTotalSize :
                                                   chunkTotalSize + 1;
      offset += chunkTotalSize;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isValid() { return "FORM".equals(getId()); }

  /** {@inheritDoc} */
  public String getSubId() { return new String(subId); }

  /** {@inheritDoc} */
  public Iterator<Chunk> getSubChunks() {
    return subChunks.iterator();
  }

  /** {@inheritDoc} */
  public Chunk getSubChunk(final String id) {
    for (Chunk chunk : subChunks) {
      if (chunk.getId().equals(id)) {
        return chunk;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  public Chunk getSubChunk(final int address) {
    for (Chunk chunk : subChunks) {
      if (chunk.getAddress() == address) {
        return chunk;
      }
    }
    return null;
  }
}
