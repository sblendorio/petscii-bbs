/*
 * Created on 2006/02/06
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
package org.zmpp.blorb;

import java.io.IOException;
import java.io.InputStream;

import org.zmpp.base.Memory;

/**
 * This class encapsulates the a memory object within an input stream.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class MemoryInputStream extends InputStream {

  /** The memory object this stream is based on. */
  private Memory memory;

  /** The position in the stream. */
  private int position;

  /** Supports a mark. */
  private int mark;

  /** The size of the memory. */
  private int size;

  /**
   * Constructor.
   * @param memory a memory object
   * @param offset the byte offset
   * @param size the memory size
   */
  public MemoryInputStream(final Memory memory, final int offset,
                           final int size) {
    this.memory = memory;
    position += offset;
    this.size = size;
  }

  /** {@inheritDoc} */
  public int read() throws IOException {
    if (position >= size) return -1;
    return memory.readUnsigned8(position++);
  }

  /** {@inheritDoc} */
  public void mark(final int readLimit) { mark = position; }

  /** {@inheritDoc} */
  public void reset() { position = mark; }
}
