/*
 * $Id: BlorbImages.java,v 1.7 2006/05/12 21:58:57 weiju Exp $
 * 
 * Created on 2006/02/06
 * Copyright 2005-2006 by Wei-ju Wu
 *
 * This file is part of The Z-machine Preservation Project (ZMPP).
 *
 * ZMPP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZMPP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZMPP; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.zmpp.blorb;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.zmpp.base.MemoryAccess;
import org.zmpp.blorb.BlorbImage.Ratio;
import org.zmpp.blorb.BlorbImage.Resolution;
import org.zmpp.blorb.BlorbImage.ResolutionInfo;
import org.zmpp.blorb.BlorbImage.ScaleInfo;
import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;

/**
 * This class implements the Image collection.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BlorbImages extends BlorbMediaCollection<BlorbImage> {

  /**
   * This map implements the image database.
   */
  private Map<Integer, BlorbImage> images;
  
  /**
   * Constructor.
   * 
   * @param formchunk the form chunk
   */
  public BlorbImages(FormChunk formchunk) {
    
    super(formchunk);
    handleResoChunk();
  }
  
  /**
   * {@inheritDoc}
   */
  public void clear() {
    
    super.clear();
    images.clear();
  }
  
  /**
   * {@inheritDoc}
   */
  protected void initDatabase() {
    
    images = new HashMap<>();    
  }
  
  /**
   * {@inheritDoc}
   */
  protected boolean isHandledResource(final byte[] usageId) {
    
    //System.out.println("isHandled ? : " + (new String(usageId)));
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

    // TODO: This chunk can be a placeholder picture, check for this
    // condition first
    if (!handlePlaceholder(chunk, resnum)) {
      
      return handlePicture(chunk, resnum);
    }
    return true;
  }
  
  private boolean handlePlaceholder(final Chunk chunk, final int resnum) {
    
    if ("Rect".equals(new String(chunk.getId()))) {
      
      // Place holder
      MemoryAccess memaccess = chunk.getMemoryAccess();
      int width = (int) memaccess.readUnsigned32(Chunk.CHUNK_HEADER_LENGTH);
      int height = (int) memaccess.readUnsigned32(Chunk.CHUNK_HEADER_LENGTH + 4);      
      images.put(resnum, new BlorbImage(width, height));
      
      return true;
    }
    return false;
  }
  
  private boolean handlePicture(final Chunk chunk, final int resnum) {
    
    final InputStream is = new MemoryAccessInputStream(chunk.getMemoryAccess(),
        Chunk.CHUNK_HEADER_LENGTH, chunk.getSize() + Chunk.CHUNK_HEADER_LENGTH);
    try {

      final BufferedImage img = ImageIO.read(is);
      images.put(resnum, new BlorbImage(img));
      return true;

    } catch (IOException ex) {

      ex.printStackTrace();
    }
    return false;
  }
  
  private void handleResoChunk() {
 
    Chunk resochunk = getFormChunk().getSubChunk("Reso".getBytes());
    if (resochunk != null) {

      adjustResolution(resochunk);
    }
  }
  
  private void adjustResolution(Chunk resochunk) {
    
    MemoryAccess memaccess = resochunk.getMemoryAccess();
    int offset = Chunk.CHUNK_ID_LENGTH;
    int size = (int) memaccess.readUnsigned32(offset);
    offset += Chunk.CHUNK_SIZEWORD_LENGTH;
    int px = (int) memaccess.readUnsigned32(offset);
    offset += 4;
    int py = (int) memaccess.readUnsigned32(offset);
    offset += 4;
    int minx = (int) memaccess.readUnsigned32(offset);
    offset += 4;
    int miny = (int) memaccess.readUnsigned32(offset);
    offset += 4;
    int maxx = (int) memaccess.readUnsigned32(offset);
    offset += 4;
    int maxy = (int) memaccess.readUnsigned32(offset);
    offset += 4;
    
    ResolutionInfo resinfo = new ResolutionInfo(new Resolution(px, py),
        new Resolution(minx, miny), new Resolution(maxx, maxy));    
    
    for (int i = 0; i < getNumResources(); i++) {
      
      if (offset >= size) break;
      int imgnum = (int) memaccess.readUnsigned32(offset);
      offset += 4;
      int ratnum = (int) memaccess.readUnsigned32(offset);
      offset += 4;
      int ratden = (int) memaccess.readUnsigned32(offset);
      offset += 4;
      int minnum = (int) memaccess.readUnsigned32(offset);
      offset += 4;
      int minden = (int) memaccess.readUnsigned32(offset);
      offset += 4;
      int maxnum = (int) memaccess.readUnsigned32(offset);
      offset += 4;
      int maxden = (int) memaccess.readUnsigned32(offset);
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
