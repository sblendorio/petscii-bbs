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
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.OutputStream;
import org.zmpp.base.StoryFileHeader.Attribute;

/**
 * Output implementation.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class OutputImpl implements Output, Closeable {

  private Machine machine;

  /**
   * This is the array of output streams.
   */
  private OutputStream[] outputStream;

  /**
   * Constructor.
   * @param machine Machine object
   */
  public OutputImpl(final Machine machine) {
    super();
    this.machine = machine;
    outputStream = new OutputStream[3];
  }

  /**
   * Sets the output stream to the specified number.
   * @param streamnumber the stream number
   * @param stream the output stream
   */
  public void setOutputStream(final int streamnumber,
      final OutputStream stream) {
    outputStream[streamnumber - 1] = stream;
  }

  /**
   * {@inheritDoc}
   */
  public void printZString(final int address) {
    print(machine.decode2Zscii(address, 0));
  }

  /**
   * {@inheritDoc}
   */
  public void print(final String str) { printZsciiChars(str); }

  /**
   * {@inheritDoc}
   */
  public void newline() { printZsciiChar(ZsciiEncoding.NEWLINE); }

  /**
   * {@inheritDoc}
   */
  public void printZsciiChar(final char zchar) {
    printZsciiChars(String.valueOf(zchar));
  }

  /**
   * Prints the specified array of ZSCII characters. This is the only function
   * that communicates with the output streams directly.
   *
   * @param zsciiString the array of ZSCII characters.
   */
  private void printZsciiChars(final String zsciiString) {
    checkTranscriptFlag();
    if (outputStream[OUTPUTSTREAM_MEMORY - 1].isSelected()) {
      for (int i = 0, n = zsciiString.length(); i < n; i++) {
        outputStream[OUTPUTSTREAM_MEMORY - 1].print(zsciiString.charAt(i));
      }
    } else {
      for (int i = 0; i < outputStream.length; i++) {
        if (outputStream[i] != null && outputStream[i].isSelected()) {
          for (int j = 0, n = zsciiString.length(); j < n; j++) {
            outputStream[i].print(zsciiString.charAt(j));
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  public void printNumber(final short number) {
    print(String.valueOf(number));
  }

  /** Flushes the output. */
  public void flushOutput() {
    // At the moment flushing only makes sense for screen
    if (!outputStream[OUTPUTSTREAM_MEMORY - 1].isSelected()) {
      for (int i = 0; i < outputStream.length; i++) {
        if (outputStream[i] != null && outputStream[i].isSelected()) {
          outputStream[i].flush();
        }
      }
    }
  }

  /**
   * Checks the fileheader if the transcript flag was set by the game
   * bypassing output_stream, e.g. with a storeb to the fileheader flags
   * address. Enable the transcript depending on the status of that flag.
   */
  private void checkTranscriptFlag() {
    if (outputStream[OUTPUTSTREAM_TRANSCRIPT - 1] != null) {
      outputStream[OUTPUTSTREAM_TRANSCRIPT - 1].select(
          machine.getFileHeader().isEnabled(Attribute.TRANSCRIPTING));
    }
  }

  /** {@inheritDoc} */
  public void selectOutputStream(final int streamnumber, final boolean flag) {
    outputStream[streamnumber - 1].select(flag);

    // Sets the tranxdQscript flag if the transcipt is specified
    if (streamnumber == OUTPUTSTREAM_TRANSCRIPT) {
      machine.getFileHeader().setEnabled(Attribute.TRANSCRIPTING, flag);
    } else if (streamnumber == OUTPUTSTREAM_MEMORY && flag) {
      machine.halt("invalid selection of memory stream");
    }
  }

  /** {@inheritDoc} */
  public void selectOutputStream3(final int tableAddress,
      final int tableWidth) {
    ((MemoryOutputStream) outputStream[OUTPUTSTREAM_MEMORY - 1]).select(
        tableAddress, tableWidth);
  }

  /** {@inheritDoc} */
  public void close() {
    if (outputStream != null) {
      for (int i = 0; i < outputStream.length; i++) {
        if (outputStream[i] != null) {
          outputStream[i].flush();
          outputStream[i].close();
        }
      }
    }
  }

  /** {@inheritDoc} */
  public void reset() {
    for (int i = 0; i < outputStream.length; i++) {
      if (outputStream[i] != null) {
        outputStream[i].flush();
      }
    }
  }
}
