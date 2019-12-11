/*
 * $Id: BlorbMediaCollection.java,v 1.8 2006/05/12 21:58:57 weiju Exp $
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

import java.util.ArrayList;
import java.util.List;

import org.zmpp.base.MemoryReadAccess;
import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.MediaCollection;

/**
 * This class defines an abstract media collection based on the Blorb
 * format.
 * It also defines the common read process for resources. The collection
 * is represented by a database and an index to the database, which maps
 * index numbers to resource numbers. The implementation of the database
 * is left to the sub classes.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class BlorbMediaCollection<T> implements MediaCollection<T> {

  /**
   * The list of resource numbers in the file.
   */
  private List<Integer> resourceNumbers;
  
  /**
   * Access to the form chunk.
   */
  private FormChunk formchunk;

  /**
   * Constructor.
   * 
   * @param formchunk the Blorb file form chunk
   */
  public BlorbMediaCollection(FormChunk formchunk) {
    
    resourceNumbers = new ArrayList<Integer>();
    this.formchunk = formchunk;
    initDatabase();
    
    // Ridx chunk
    Chunk ridxChunk = formchunk.getSubChunk("RIdx".getBytes());
    //System.out.println("ridxChunk: " + ridxChunk);
    MemoryReadAccess chunkmem = ridxChunk.getMemoryAccess();
    int numresources = (int) chunkmem.readUnsigned32(8);
    int offset = 12;
    byte[] usage = new byte[4];
    //System.out.println("num resources: " + numresources);
    
    for (int i = 0; i < numresources; i++) {

      for (int j = 0; j < 4; j++) {
        
        usage[j] = chunkmem.readByte(offset + j);
      }
      
      if (isHandledResource(usage)) {

        int resnum = (int) chunkmem.readUnsigned32(offset + 4);        
        int address = (int) chunkmem.readUnsigned32(offset + 8);
        Chunk chunk = formchunk.getSubChunk(address);
        
        if (putToDatabase(chunk, resnum)) {

          resourceNumbers.add(resnum);
        }
      }
      offset += 12;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void clear() {
    
    resourceNumbers.clear();
  }
  
  /**
   * {@inheritDoc}
   */
  public int getNumResources() {
    
    return resourceNumbers.size();
  }
  
  /**
   * Returns the resource number at the given index.
   * 
   * @param index the index
   * @return the resource number
   */
  public int getResourceNumber(final int index) {
    
    return resourceNumbers.get(index);
  }
  
  /**
   * {@inheritDoc}
   */
  public void loadResource(final int resourcenumber) {
    
    // intentionally left empty for possible future use
  }

  /**
   * {@inheritDoc}
   */
  public void unloadResource(final int resourcenumber) {
    
    // intentionally left empty for possible future use
  }
  
  /**
   * Access to the form chunk.
   * 
   * @return the form chunk
   */
  protected FormChunk getFormChunk() {
    
    return formchunk;
  }
  
  /**
   * Initialize the database.
   */
  abstract protected void initDatabase();      

  /**
   * This method is invoked by the constructor to indicate if the
   * class handles the given resource.
   * 
   * @param usageId the usage id
   * @return true if the current class handles this resource, false, otherwise
   */
  abstract protected boolean isHandledResource(byte[] usageId);
  
  /**
   * Puts the media object based on this sub chunk into the database.
   * 
   * @param chunk the blorb sub chunk
   * @param resnum the resource number
   * @return true if successful, false otherwise
   */
  abstract protected boolean putToDatabase(Chunk chunk, int resnum);
}
