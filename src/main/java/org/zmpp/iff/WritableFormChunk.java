/*
 * $Id: WritableFormChunk.java,v 1.7 2006/04/12 18:00:33 weiju Exp $
 * 
 * Created on 2005/09/23
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
package org.zmpp.iff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.zmpp.base.DefaultMemoryAccess;
import org.zmpp.base.MemoryAccess;

public class WritableFormChunk implements FormChunk {

  private byte[] subId;
  private static final byte[] FORM_ID = "FORM".getBytes();
  
  private List<Chunk> subChunks;
  
  /**
   * Constructor.
   * 
   * @param subId the sub id
   */
  public WritableFormChunk(final byte[] subId) {
  
    super();
    this.subId = subId;
    this.subChunks = new ArrayList<Chunk>();
  }
  
  /**
   * Adds a sub chunk.
   * 
   * @param chunk the sub chunk to add
   */
  public void addChunk(final Chunk chunk) {
    
    subChunks.add(chunk);
  }
  
  /**
   * {@inheritDoc}
   */
  public byte[] getSubId() {
    
    return subId;
  }

  /**
   * {@inheritDoc}
   */
  public Iterator<Chunk> getSubChunks() {
    
    return subChunks.iterator();
  }

  /**
   * {@inheritDoc}
   */
  public Chunk getSubChunk(final byte[] id) {
    
    for (Chunk chunk : subChunks) {
    
      if (Arrays.equals(chunk.getId(), id)) {
        
        return chunk;
      }
    }
    return null;
  }
  
  /**
   * {@inheritDoc}
   */
  public Chunk getSubChunk(final int address) {
    
    // We do not need to implement this
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getId() {
    
    return FORM_ID;
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() {
    
    int size = subId.length;
    
    for (Chunk chunk : subChunks) {
      
      int chunkSize = chunk.getSize();
      if ((chunkSize % 2) == 1) {
        chunkSize++; // pad if necessary
      }
      size += (Chunk.CHUNK_HEADER_LENGTH + chunkSize);
    }
    return size;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isValid() {
    
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public MemoryAccess getMemoryAccess() {
    
    return new DefaultMemoryAccess(getBytes());
  }

  /**
   * Returns the data of this chunk.
   * 
   * @return the chunk data
   */
  public byte[] getBytes() {
    
    final int datasize = Chunk.CHUNK_HEADER_LENGTH + getSize();    
    final byte[] data = new byte[datasize];
    final MemoryAccess memaccess = new DefaultMemoryAccess(data);
    memaccess.writeByte(0, (byte) 'F');
    memaccess.writeByte(1, (byte) 'O');
    memaccess.writeByte(2, (byte) 'R');
    memaccess.writeByte(3, (byte) 'M');
    memaccess.writeUnsigned32(4, getSize());
    
    int offset = Chunk.CHUNK_HEADER_LENGTH;
    
    // Write sub id
    for (int i = 0; i < subId.length; i++) {
     
      memaccess.writeByte(offset++, subId[i]);
    }
    
    // Write sub chunk data
    for (Chunk chunk : subChunks) {
     
      //System.out.println("Chunk: " + (new String(chunk.getId())));
      final byte[] chunkId = chunk.getId();
      final int chunkSize = chunk.getSize();
      
      // Write id
      for (int i = 0; i < chunkId.length; i++) {
        
        memaccess.writeByte(offset++, chunkId[i]);
      }
      
      // Write chunk size
      memaccess.writeUnsigned32(offset, chunkSize);
      offset += 4; // add the size word length
      
      // Write chunk data
      final MemoryAccess chunkMem = chunk.getMemoryAccess();      
      for (int i = 0; i < chunkSize; i++) {
        
        memaccess.writeByte(offset++,
            chunkMem.readByte(Chunk.CHUNK_HEADER_LENGTH + i));
      }
      
      // Pad if necessary
      if ((chunkSize % 2) == 1) {
        memaccess.writeByte(offset++, (byte) 0);
      }
    }
    
    return data;
  }
  
  /**
   * {@inheritDoc}
   */
  public int getAddress() { return 0; }
  
}
