/*
 * Created on 2006/02/22
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
 * Interface for managing pictures.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface PictureManager {

  /**
   * Returns the size of the specified picture or null if the picture does not
   * exist.
   * @param picturenum the number of the picture
   * @return the size
   */
  Resolution getPictureSize(int picturenum);

  /**
   * Returns the data of the specified picture. If it is not available, this
   * method returns null.
   * @param picturenum the picture number
   * @return the data
   */
  ZmppImage getPicture(int picturenum);

  /**
   * Returns the number of pictures.
   * @return the number of pictures
   */
  int getNumPictures();

  /**
   * Preloads the specified picture numbers.
   * @param picnumbers the picture numbers to preload
   */
  void preload(int[] picnumbers);

  /**
   * Returns the release number of the picture file.
   * @return the release number
   */
  int getRelease();

  /** Resets the picture manager. */
  void reset();
}
