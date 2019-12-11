/*
 * $Id: SaveGameDataStore.java,v 1.3 2006/01/07 02:51:31 weiju Exp $
 * 
 * Created on 11/03/2005
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
package org.zmpp.vm;

import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;

/**
 * This interface should be implemented by user interfaces that implement
 * game saving functionality. This keeps the game saving facilities independent
 * of implementation details.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface SaveGameDataStore {

  /**
   * Save the given form chunk to the storage.
   * 
   * @param formchunk a form chunk in Quetzal format
   * @return true if successful, false, otherwise
   */
  boolean saveFormChunk(WritableFormChunk formchunk);

  /**
   * Reads a form chunk from storage. Returns null if not successful.
   * 
   * @return a saved game or null
   */
  FormChunk retrieveFormChunk();
}
