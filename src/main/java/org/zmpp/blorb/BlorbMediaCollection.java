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

import java.util.ArrayList;
import java.util.List;

import org.zmpp.base.Memory;
import static org.zmpp.base.MemoryUtil.readUnsigned32;
import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.MediaCollection;

/**
 * This class defines an abstract media collection based on the Blorb
 * format.
 * It also defines the common read process for resources. The collection
 * is represented by a database and an index to the database, which maps
 * index numbers to resource numbers. The implementation of the database
 * is left to the sub classes.
 *
 * @author Wei-ju Wu
 * @version 1.5
 * @param <T> collection type
 */
public abstract class BlorbMediaCollection<T> implements MediaCollection<T> {

  /**
   * The list of resource numbers in the file.
   */
  private List<Integer> resourceNumbers;

  /**
   * Access to the form chunk.
   */
  private FormChunk formchunk;

  protected NativeImageFactory imageFactory;
  protected SoundEffectFactory soundEffectFactory;

  /**
   * Constructor.
   * @param imageFactory the NativeImageFactory
   * @param soundEffectFactory the SoundEffectFactory
   * @param formchunk the Blorb file form chunk
   */
  public BlorbMediaCollection(NativeImageFactory imageFactory,
      SoundEffectFactory soundEffectFactory,
      FormChunk formchunk) {
    resourceNumbers = new ArrayList<Integer>();
    this.formchunk = formchunk;
    this.imageFactory = imageFactory;
    this.soundEffectFactory = soundEffectFactory;
    initDatabase();

    // Ridx chunk
    Chunk ridxChunk = formchunk.getSubChunk("RIdx");
    Memory chunkmem = ridxChunk.getMemory();
    int numresources = (int) readUnsigned32(chunkmem, 8);
    int offset = 12;
    byte[] usage = new byte[4];

    for (int i = 0; i < numresources; i++) {
      chunkmem.copyBytesToArray(usage, 0, offset, 4);
      if (isHandledResource(usage)) {
        int resnum = (int) readUnsigned32(chunkmem, offset + 4);
        int address = (int) readUnsigned32(chunkmem, offset + 8);
        Chunk chunk = formchunk.getSubChunk(address);

        if (putToDatabase(chunk, resnum)) {
          resourceNumbers.add(resnum);
        }
      }
      offset += 12;
    }
  }

  /**
   * {@inheritDoc}
   */
  public void clear() { resourceNumbers.clear(); }

  /**
   * {@inheritDoc}
   */
  public int getNumResources() { return resourceNumbers.size(); }

  /**
   * Returns the resource number at the given index.
   * @param index the index
   * @return the resource number
   */
  public int getResourceNumber(final int index) {
    return resourceNumbers.get(index);
  }

  /**
   * {@inheritDoc}
   */
  public void loadResource(final int resourcenumber) {
    // intentionally left empty for possible future use
  }

  /**
   * {@inheritDoc}
   */
  public void unloadResource(final int resourcenumber) {
    // intentionally left empty for possible future use
  }

  /**
   * Access to the form chunk.
   * @return the form chunk
   */
  protected FormChunk getFormChunk() {
    return formchunk;
  }

  /** Initialize the database. */
  protected abstract void initDatabase();

  /**
   * This method is invoked by the constructor to indicate if the
   * class handles the given resource.
   *
   * @param usageId the usage id
   * @return true if the current class handles this resource, false, otherwise
   */
  protected abstract boolean isHandledResource(byte[] usageId);

  /**
   * Puts the media object based on this sub chunk into the database.
   *
   * @param chunk the blorb sub chunk
   * @param resnum the resource number
   * @return true if successful, false otherwise
   */
  protected abstract boolean putToDatabase(Chunk chunk, int resnum);
}
