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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.zmpp.base.Memory;
import static org.zmpp.base.MemoryUtil.readUnsigned32;
import org.zmpp.blorb.BlorbImage.Ratio;
import org.zmpp.blorb.BlorbImage.ResolutionInfo;
import org.zmpp.blorb.BlorbImage.ScaleInfo;
import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.Resolution;

/**
 * This class implements the Image collection.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class BlorbImages extends BlorbMediaCollection<BlorbImage> {

  /**
   * This map implements the image database.
   */
  private Map<Integer, BlorbImage> images;

  /**
   * Constructor.
   * @param imageFactory the NativeImageFactory object
   * @param formchunk the form chunk
   */
  public BlorbImages(NativeImageFactory imageFactory, FormChunk formchunk) {
    super(imageFactory, null, formchunk);
    handleResoChunk();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    super.clear();
    images.clear();
  }

  /**
   * {@inheritDoc}
   */
  protected void initDatabase() {
    images = new HashMap<Integer, BlorbImage>();
  }

  /**
   * {@inheritDoc}
   */
  protected boolean isHandledResource(final byte[] usageId) {
    return usageId[0] == 'P' && usageId[1] == 'i' && usageId[2] == 'c'
           && usageId[3] == 't';
  }

  /**
   * {@inheritDoc}
   */
  public BlorbImage getResource(final int resourcenumber) {
    return images.get(resourcenumber);
  }

  /**
   * {@inheritDoc}
   */
  protected boolean putToDatabase(final Chunk chunk, final int resnum) {
    if (!handlePlaceholder(chunk, resnum)) {
      return handlePicture(chunk, resnum);
    }
    return true;
  }

  /**
   * Handles a placeholder image.
   * @param chunk the Chunk object
   * @param resnum the resource number
   * @return true if successful, false otherwise
   */
  private boolean handlePlaceholder(final Chunk chunk, final int resnum) {
    if ("Rect".equals(chunk.getId())) {
      // Place holder
      Memory memory = chunk.getMemory();
      int width = (int) readUnsigned32(memory, Chunk.CHUNK_HEADER_LENGTH);
      int height = (int) readUnsigned32(memory, Chunk.CHUNK_HEADER_LENGTH + 4);
      images.put(resnum, new BlorbImage(width, height));

      return true;
    }
    return false;
  }

  /**
   * Processes the picture contained in the specified chunk.
   * @param chunk the Chunk
   * @param resnum the resource number
   * @return true if successful, false otherwise
   */
  private boolean handlePicture(final Chunk chunk, final int resnum) {
    final InputStream is = new MemoryInputStream(chunk.getMemory(),
        Chunk.CHUNK_HEADER_LENGTH, chunk.getSize() + Chunk.CHUNK_HEADER_LENGTH);
    try {
      images.put(resnum, new BlorbImage(imageFactory.createImage(is)));
      return true;
    } catch (IOException ex) {

      ex.printStackTrace();
    }
    return false;
  }

  /**
   * Process the Reso chunk.
   */
  private void handleResoChunk() {
    Chunk resochunk = getFormChunk().getSubChunk("Reso");
    if (resochunk != null) {
      adjustResolution(resochunk);
    }
  }

  /**
   * Adjusts the resolution of the image.
   * @param resochunk the Reso chunk
   */
  private void adjustResolution(Chunk resochunk) {
    Memory memory = resochunk.getMemory();
    int offset = Chunk.CHUNK_ID_LENGTH;
    int size = (int) readUnsigned32(memory, offset);
    offset += Chunk.CHUNK_SIZEWORD_LENGTH;
    int px = (int) readUnsigned32(memory, offset);
    offset += 4;
    int py = (int) readUnsigned32(memory, offset);
    offset += 4;
    int minx = (int) readUnsigned32(memory, offset);
    offset += 4;
    int miny = (int) readUnsigned32(memory, offset);
    offset += 4;
    int maxx = (int) readUnsigned32(memory, offset);
    offset += 4;
    int maxy = (int) readUnsigned32(memory, offset);
    offset += 4;

    ResolutionInfo resinfo = new ResolutionInfo(new Resolution(px, py),
        new Resolution(minx, miny), new Resolution(maxx, maxy));

    for (int i = 0; i < getNumResources(); i++) {
      if (offset >= size) break;
      int imgnum = (int) readUnsigned32(memory, offset);
      offset += 4;
      int ratnum = (int) readUnsigned32(memory, offset);
      offset += 4;
      int ratden = (int) readUnsigned32(memory, offset);
      offset += 4;
      int minnum = (int) readUnsigned32(memory, offset);
      offset += 4;
      int minden = (int) readUnsigned32(memory, offset);
      offset += 4;
      int maxnum = (int) readUnsigned32(memory, offset);
      offset += 4;
      int maxden = (int) readUnsigned32(memory, offset);
      offset += 4;
      ScaleInfo scaleinfo = new ScaleInfo(resinfo, new Ratio(ratnum, ratden),
          new Ratio(minnum, minden), new Ratio(maxnum, maxden));
      BlorbImage img = images.get(imgnum);

      if (img != null) {
        img.setScaleInfo(scaleinfo);
      }
    }
  }
}
