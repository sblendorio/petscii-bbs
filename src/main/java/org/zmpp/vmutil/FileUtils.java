/*
 * Created on 2006/02/13
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;
import org.zmpp.base.DefaultMemory;
import org.zmpp.base.Memory;
import org.zmpp.blorb.BlorbResources;
import org.zmpp.blorb.NativeImageFactory;
import org.zmpp.blorb.SoundEffectFactory;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.Resources;

/**
 * This utility class was introduced to avoid a code smell in data
 * initialization.
 * It offers methods to read data from streams and files.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public final class FileUtils {

  private static final Logger LOG = Logger.getLogger("org.zmpp");

  /** This class only contains static methods. */
  private FileUtils() { }

  /**
   * Creates a resources object from a Blorb file.
   * @param imageFactory the NativeImageFactory
   * @param soundEffectFactory the SoundEffectFactory
   * @param blorbfile the file
   * @return the resources object or null (on failure)
   */
  public static Resources createResources(NativeImageFactory imageFactory,
    SoundEffectFactory soundEffectFactory,
    final File blorbfile) {
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(blorbfile, "r");
      final byte[] data = new byte[(int) raf.length()];
      raf.readFully(data);
      final Memory memory = new DefaultMemory(data);
      final FormChunk formchunk = new DefaultFormChunk(memory);
      return new BlorbResources(imageFactory, soundEffectFactory, formchunk);
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (raf != null) {
        try { raf.close(); } catch (Exception ex) {
          LOG.throwing("FileUtils", "createResources", ex);
        }
      }
    }
    return null;
  }

  /**
   * Reads an array of bytes from the given input stream.
   * @param inputstream the input stream
   * @return the bytes or null if the inputstream is null
   */
  public static byte[] readFileBytes(final InputStream inputstream) {
    if (inputstream == null) return null;

    // Start with a buffer size between 1K and 1M based on available memory.
    final int minBufferSize = (int)
      Math.max(1024,
               Math.min(Runtime.getRuntime().freeMemory()/10, 1024 * 1024));

    List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
    int totalBytesRead = 0;

    // Fill buffer lists
    try {
      final ReadableByteChannel rbc = Channels.newChannel(inputstream);
      ByteBuffer bb = ByteBuffer.allocate(minBufferSize);
      buffers.add(bb);

      int bytesRead;
      while ((bytesRead = rbc.read(bb)) != -1) {
        totalBytesRead += bytesRead;
        // if this buffer is mostly full, create another one and avoid small
        // read iterations
        if (bb.remaining() < 16) {
          bb.flip();
          bb = ByteBuffer.allocate(Math.max(minBufferSize, totalBytesRead/2));
          buffers.add(bb);
        }
      }
      bb.flip();
    } catch (IOException ex) {
      LOG.throwing("FileUtils", "readFileBytes", ex);
      throw new RuntimeException("Unable to read file bytes", ex);
    }

    // merge the buffers so we can convert to a byte array.
    final ByteBuffer data =  ByteBuffer.allocate(totalBytesRead);
    for (final ByteBuffer buf : buffers) {
      data.put(buf);
    }
    data.flip();

    // allow intermeditate buffers to be collected before we create a
    // possibly big array
    buffers = null;

    // is the data buffer backed by a byte array?
    if (data.hasArray()) {
      // just use the buffer's backing array since it's no longer needed
      // and avoid the copy.
      return data.array();
    } else {
      // we need to copy the bytes into a byte array.
      final byte[] bytes = new byte[data.remaining()];
      data.get(bytes);
      return bytes;
    }
  }

  /**
   * Reads the bytes from the given file if it is a file and it exists.
   * @param file the file object
   * @return a byte array
   */
  public static byte[] readFileBytes(final File file) {
    byte[] data = null;
    if (file != null && file.exists() && file.isFile()) {
      RandomAccessFile raf = null;
      try {
        raf = new RandomAccessFile(file, "r");
        data = new byte[(int) raf.length()];
        raf.readFully(data);
      } catch (IOException ex) {
        ex.printStackTrace();
      } finally {
        if (raf != null) {
          try { raf.close(); } catch (Exception ex) {
            LOG.throwing("FileUtils", "readFileBytes", ex);
          }
        }
      }
    }
    return data;
  }
}
