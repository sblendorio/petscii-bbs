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

import java.util.HashMap;
import java.util.Map;

import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.SoundEffect;

/**
 * This class implements the Blorb sound collection.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class BlorbSounds extends BlorbMediaCollection<SoundEffect> {

  /**
   * This map implements the database.
   */
  private Map<Integer, SoundEffect> sounds;

  /**
   * Constructor.
   * @param factory the SoundEffectFactory
   * @param formchunk the form chunk
   */
  public BlorbSounds(SoundEffectFactory factory, FormChunk formchunk) {
    super(null, factory, formchunk);
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    super.clear();
    sounds.clear();
  }

  /** {@inheritDoc} */
  protected void initDatabase() {
    sounds = new HashMap<Integer, SoundEffect>();
  }

  /** {@inheritDoc} */
  protected boolean isHandledResource(final byte[] usageId) {
    return usageId[0] == 'S' && usageId[1] == 'n' && usageId[2] == 'd'
           && usageId[3] == ' ';
  }

  /** {@inheritDoc} */
  public SoundEffect getResource(final int resourcenumber) {
    return sounds.get(resourcenumber);
  }

  /** {@inheritDoc} */
  protected boolean putToDatabase(final Chunk aiffChunk, final int resnum) {
    try {
      sounds.put(resnum, soundEffectFactory.createSoundEffect(aiffChunk));
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return false;
  }
}
