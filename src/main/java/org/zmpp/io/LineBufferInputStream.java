/*
 * Created on 2008/04/25
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
package org.zmpp.io;

import java.util.LinkedList;
import java.util.Queue;
import java.io.Serializable;

/**
 * The LineBufferInputStream is the default implementation for the keyboard
 * input stream. It is simply a queue holding a number of input lines.
 * Normally this is only one, but it could be used for testing by simply
 * storing more lines and running the core on it.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class LineBufferInputStream implements InputStream, Serializable {

  private static final long serialVersionUID = 1L;
  /** The queue holding input lines. */
  private Queue<String> inputLines = new LinkedList<String>();

  /**
   * Adds an input line to the end of the buffer.
   * @param line the new input line
   */
  public void addInputLine(String line) { inputLines.add(line); }

  /** {@inheritDoc} */
  public String readLine() {
    return inputLines.remove();
  }

  /** {@inheritDoc} */
  public void close() { }
}
