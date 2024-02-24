/*
 * Created on 2006/03/10
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
package org.zmpp.vmutil;

/**
 * This ring buffer implementation is an efficient representation for a
 * dynamic list structure that should have a limited number of entries and
 * where the oldest n entries can be discarded.
 * This kind of container is particularly useful for undo and history buffers.
 *
 * @author Wei-ju Wu
 * @version 1.5
 * @param <T> type of contained objects
 */
public class RingBuffer<T> {

  private T[] elements;
  private int bufferstart;
  private int bufferend;
  private int size;

  /**
   * Constructor.
   * @param size the size of the buffer
   */
  @SuppressWarnings({"unchecked"})
  public RingBuffer(int size) {
    elements = (T[]) new Object[size];
  }

  /**
   * Adds an element to the buffer. If the capacity of the buffer is exceeded,
   * the oldest element is replaced.
   * @param elem the element
   */
  public void add(final T elem) {
    if (size == elements.length) {
      bufferstart = (bufferstart + 1) % elements.length;
    } else {
      size++;
    }
    elements[bufferend++] = elem;
    bufferend = bufferend % elements.length;
  }

  /**
   * Replaces the element at the specified index with the specified element.
   * @param index the replacement index
   * @param elem the replacement element
   */
  public void set(final int index, final T elem) {
    elements[mapIndex(index)] = elem;
  }

  /**
   * Returns the element at the specified index.
   * @param index the index
   * @return the object
   */
  public T get(final int index) { return elements[mapIndex(index)]; }

  /**
   * Returns the size of this ring buffer.
   * @return the size
   */
  public int size() { return size; }

  /**
   * Removes the object at the specified index.
   * @param index the index
   * @return the removed object
   */
  public T remove(final int index) {
    if (size > 0) {
      // remember the removed element
      final T elem = get(index);

      // move the following element by one to the front
      for (int i = index; i < (size - 1); i++) {
        final int idx1 = mapIndex(i);
        final int idx2 = mapIndex(i + 1);
        elements[idx1] = elements[idx2];
      }
      size--;
      bufferend = (bufferend - 1) % elements.length;
      if (bufferend < 0) bufferend = elements.length + bufferend;
      return elem;
    }
    return null;
  }

  /**
   * Maps a container index to a ring buffer index.
   * @param index the container index
   * @return the buffer index
   */
  private int mapIndex(final int index) {
    return (bufferstart + index) % elements.length;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final StringBuilder buffer =  new StringBuilder("{ ");
    for (int i = 0; i < size(); i++) {
      if (i > 0) {
        buffer.append(", ");
      }
      buffer.append(get(i));
    }
    buffer.append(" }");
    return buffer.toString();
  }
}
