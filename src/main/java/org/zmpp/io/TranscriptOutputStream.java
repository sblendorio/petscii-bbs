/*
 * Created on 11/08/2005
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.logging.Logger;
import org.zmpp.encoding.IZsciiEncoding;

/**
 * This class defines an output stream for transcript output (Stream 2).
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class TranscriptOutputStream implements OutputStream {

  private static final Logger LOG = Logger.getLogger("org.zmpp");
  private IOSystem iosys;
  private BufferedWriter output;
  private Writer transcriptWriter;
  private boolean enabled;
  private StringBuilder linebuffer;
  private IZsciiEncoding encoding;
  private boolean initialized;

  /**
   * Constructor.
   * @param iosys the I/O system
   * @param encoding IZsciiEncoding object
   */
  public TranscriptOutputStream(final IOSystem iosys,
                                final IZsciiEncoding encoding) {
    this.iosys = iosys;
    this.encoding = encoding;
    linebuffer = new StringBuilder();
  }

  /** Initializes the output file. */
  private void initFile() {
    if (!initialized && transcriptWriter == null) {
      transcriptWriter = iosys.getTranscriptWriter();
      if (transcriptWriter != null) {
        output = new BufferedWriter(transcriptWriter);
      }
      initialized = true;
    }
  }

  /** {@inheritDoc} */
  public void print(final char zsciiChar) {
    initFile();
    if (output != null) {
      if (zsciiChar == IZsciiEncoding.NEWLINE) {
        flush();
      } else if (zsciiChar == IZsciiEncoding.DELETE) {
        linebuffer.deleteCharAt(linebuffer.length() - 1);
      } else {
        linebuffer.append(encoding.getUnicodeChar(zsciiChar));
      }
      flush();
    }
  }

  /** {@inheritDoc} */
  public void select(final boolean flag) { enabled = flag; }

  /** {@inheritDoc} */
  public boolean isSelected() { return enabled; }

  /** {@inheritDoc} */
  public void flush() {
    try {
      if (output != null) {
        output.write(linebuffer.toString());
        linebuffer = new StringBuilder();
      }
    } catch (IOException ex) {
        LOG.throwing("TranscriptOutputStream", "flush", ex);
    }
  }

  /** {@inheritDoc} */
  public void close() {
    if (output != null) {
      try {
        output.close();
        output = null;
      } catch (Exception ex) {
        LOG.throwing("TranscriptOutputStream", "close", ex);
      }
    }

    if (transcriptWriter != null) {
      try {
        transcriptWriter.close();
        transcriptWriter = null;
      } catch (Exception ex) {
        LOG.throwing("TranscriptOutputStream", "close", ex);
      }
    }
    initialized = false;
  }
}
