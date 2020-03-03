/*
 * $Id: Chunk.java,v 1.3 2006/02/01 17:50:49 weiju Exp $
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

import org.zmpp.base.MemoryAccess;

/**
 * The basic data structure for an IFF file, a chunk.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface Chunk {

  /**
   * The length of an IFF chunk id in bytes.
   */
  static final int CHUNK_ID_LENGTH = 4;
  
  /**
   * The length of an IFF chunk size word in bytes.
   */
  static final int CHUNK_SIZEWORD_LENGTH = 4;
  
  /**
   * The chunk header size.
   */
  static final int CHUNK_HEADER_LENGTH = CHUNK_ID_LENGTH
                                         + CHUNK_SIZEWORD_LENGTH;
  
  /**
   * Returns this IFF chunk's id. An id is a 4 byte array. 
   * 
   * @return the id
   */
  byte[] getId();
  
  /**
   * The chunk data size, excluding id and size word.
   * 
   * @return the size
   */
  int getSize();
  
  /**
   * Returns true if this is a valid chunk.
   * 
   * @return true if valid, false otherwise
   */
  boolean isValid();
  
  /**
   * Returns a memory access object to the chunk.
   * 
   * @return the MemoryAccess object
   */
  MemoryAccess getMemoryAccess();
  
  /**
   * Returns the address of the chunk within the global FORM chunk.
   * 
   * @return the address within the form chunk
   */
  int getAddress();
}
