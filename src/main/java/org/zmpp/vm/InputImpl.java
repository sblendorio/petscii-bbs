/*
 * Created on 2006/02/14
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

import java.io.Closeable;
import org.zmpp.io.InputStream;

/**
 * Input interface implementation.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class InputImpl implements Input, Closeable {

  /**
   * This is the array of input streams.
   */
  private InputStream[] inputStream = new InputStream[2];

  /**
   * The selected input stream.
   */
  private int selectedInputStreamIndex = 0;

  /** {@inheritDoc} */
  public void close() {
    if (inputStream != null) {
      for (int i = 0; i < inputStream.length; i++) {
        if (inputStream[i] != null) {
          inputStream[i].close();
        }
      }
    }
  }

  /**
   * Sets an input stream to the specified number.
   * @param streamnumber the input stream number
   * @param stream the input stream to set
   */
  public void setInputStream(final int streamnumber, final InputStream stream) {
    inputStream[streamnumber] = stream;
  }

  /**
   * {@inheritDoc}
   */
  public void selectInputStream(final int streamnumber) {
    selectedInputStreamIndex = streamnumber;
  }

  /**
   * {@inheritDoc}
   */
  public InputStream getSelectedInputStream() {

    return inputStream[selectedInputStreamIndex];
  }

}
