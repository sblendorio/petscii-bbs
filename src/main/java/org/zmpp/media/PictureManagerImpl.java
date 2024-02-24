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
 * PictureManager implementation.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class PictureManagerImpl implements PictureManager {

  private int release;
  private MediaCollection<? extends ZmppImage> pictures;
  private DrawingArea drawingArea;

  /**
   * Constructor.
   * @param release release number
   * @param drawingArea DrawingArea object
   * @param pictures pictures collection
   */
  public PictureManagerImpl(int release, DrawingArea drawingArea,
                            MediaCollection<? extends ZmppImage> pictures) {
    this.release = release;
    this.pictures = pictures;
    this.drawingArea = drawingArea;
  }

  /** {@inheritDoc} */
  public Resolution getPictureSize(final int picturenum) {
    final ZmppImage img = pictures.getResource(picturenum);
    if (img != null) {
      Resolution reso = drawingArea.getResolution();
      return img.getSize(reso.getWidth(), reso.getHeight());
    }
    return null;
  }

  /** {@inheritDoc} */
  public ZmppImage getPicture(final int picturenum) {
    return pictures.getResource(picturenum);
  }

  /** {@inheritDoc} */
  public int getNumPictures() {
    return pictures.getNumResources();
  }

  /** {@inheritDoc} */
  public void preload(final int[] picnumbers) {
    // no preloading at the moment
  }

  /** {@inheritDoc} */
  public int getRelease() { return release; }

  /** {@inheritDoc} */
  public void reset() {
    // no resetting supported
  }
}
