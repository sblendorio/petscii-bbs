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

import org.zmpp.iff.FormChunk;
import org.zmpp.media.InformMetadata;
import org.zmpp.media.MediaCollection;
import org.zmpp.media.Resources;
import org.zmpp.media.SoundEffect;
import org.zmpp.media.ZmppImage;

/**
 * This class encapsulates a Blorb file and offers access to the sound
 * and graphics media collections.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class BlorbResources implements Resources {

  /** The file's images. */
  private MediaCollection<BlorbImage> images;

  /** The file's sounds. */
  private MediaCollection<SoundEffect> sounds;

  /** The cover art. */
  private BlorbCoverArt coverart;

  /** The meta data. */
  private BlorbMetadataHandler metadata;

  /** The release number. */
  private int release;

  /**
   * Constructor.
   * @param imageFactory a NativeImageFactory
   * @param soundEffectFactory a SoundEffectFactory
   * @param formchunk a form chunk in Blorb format
   */
  public BlorbResources(NativeImageFactory imageFactory,
      SoundEffectFactory soundEffectFactory,
      FormChunk formchunk) {
    images = new BlorbImages(imageFactory, formchunk);
    sounds = new BlorbSounds(soundEffectFactory, formchunk);
    coverart = new BlorbCoverArt(formchunk);
    metadata = new BlorbMetadataHandler(formchunk);
  }

  /**
   * {@inheritDoc}
   */
  public MediaCollection<? extends ZmppImage> getImages() { return images; }

  /**
   * {@inheritDoc}
   */
  public MediaCollection<SoundEffect> getSounds() { return sounds; }

  /**
   * {@inheritDoc}
   */
  public int getCoverArtNum() { return coverart.getCoverArtNum(); }


  /**
   * {@inheritDoc}
   */
  public InformMetadata getMetadata() { return metadata.getMetadata(); }

  /**
   * {@inheritDoc}
   */
  public int getRelease() { return release; }

  /**
   * {@inheritDoc}
   */
  public boolean hasInfo() { return metadata.getMetadata() != null; }
}
