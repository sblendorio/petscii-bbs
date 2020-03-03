/*
 * $Id: BlorbCoverArt.java,v 1.3 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 2006/03/04
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
 * This class extracts the Frontispiece chunk.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BlorbCoverArt {

  private int coverartnum;

  /**
   * Constructor.
   * 
   * @param formchunk the form chunk
   */
  public BlorbCoverArt(FormChunk formchunk) {
    
    readFrontispiece(formchunk);
  }
  
  private void readFrontispiece(final FormChunk formchunk) {
    
    //System.out.println("SEARCHIN FRONTISPIECE");
    final Chunk fspcchunk = formchunk.getSubChunk("Fspc".getBytes());
    if (fspcchunk != null) {
      //System.out.println("FOUND FRONTISPIECE");
      coverartnum = (int)
        fspcchunk.getMemoryAccess().readUnsigned32(Chunk.CHUNK_HEADER_LENGTH);
    }
  }
  
  /**
   * Returns the number of the cover art.
   * 
   * @return the cover art
   */
  public int getCoverArtNum() { return coverartnum; }
}
