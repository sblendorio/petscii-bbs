/*
 * $Id: FormChunk.java,v 1.3 2006/02/01 17:50:49 weiju Exp $
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

import java.util.Iterator;

/**
 * FormChunk is the wrapper chunk for all other chunks.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface FormChunk extends Chunk {

  /**
   * Returns the sub id.
   * 
   * @return the sub id
   */
  byte[] getSubId();
  
  /**
   * Returns an iterator of chunks contained in this form chunk.
   * 
   * @return the enumeration of sub chunks
   */
  Iterator<Chunk> getSubChunks();
  
  /**
   * Returns the chunk with the specified id.
   * 
   * @param id the id
   * @return the chunk with the specified id or null if it does not exist
   */
  Chunk getSubChunk(byte[] id);
  
  /**
   * Returns the sub chunk at the specified address or null if it does
   * not exist.
   * 
   * @param address the address of the chunk
   * @return the chunk or null if it does not exist
   */
  Chunk getSubChunk(int address);
}
