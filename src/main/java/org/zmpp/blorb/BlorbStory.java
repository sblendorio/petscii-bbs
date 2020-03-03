/*
 * $Id: BlorbStory.java,v 1.2 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 2006/03/03
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

import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;

/**
 * This class extracts story data from a Blorb file.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BlorbStory {

  private byte[] storydata;
  
  public BlorbStory(final FormChunk formchunk) {
    
    super();
    storydata = readStoryFromZBlorb(formchunk);
  }
  
  public byte[] getStoryData() { return storydata; }
  
  private byte[] readStoryFromZBlorb(final FormChunk formchunk) {
    
    final Chunk chunk = formchunk.getSubChunk("ZCOD".getBytes());
    final int size = chunk.getSize();
    final byte[] data = new byte[size];
    for (int i = 0; i < size; i++) {
      
      data[i] = chunk.getMemoryAccess().readByte(i + Chunk.CHUNK_HEADER_LENGTH);
    }
    return data;
  }
}
