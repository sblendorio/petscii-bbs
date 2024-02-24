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
package org.zmpp.media;

/**
 * This interface defines access to the Z-machine's media resources.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Resources {

  /**
   * The release number of the resource file.
   * @return the release number
   */
  int getRelease();

  /**
   * Returns the images of this file.
   * @return the images
   */
  MediaCollection<? extends ZmppImage> getImages();

  /**
   * Returns the sounds of this file.
   * @return the sounds
   */
  MediaCollection<SoundEffect> getSounds();

  /**
   * Returns the number of the cover art picture.
   * @return the number of the cover art picture
   */
  int getCoverArtNum();

  /**
   * Returns the inform meta data if available.
   * @return the meta data
   */
  InformMetadata getMetadata();

  /**
   * Returns true if the resource file has information.
   * @return true if information, false, otherwise
   */
  boolean hasInfo();
}
