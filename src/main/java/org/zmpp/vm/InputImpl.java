/*
 * $Id: InputImpl.java,v 1.2 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 2006/02/14
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

import org.zmpp.io.InputStream;

public class InputImpl implements Input {

  /**
   * This is the array of input streams.
   */
  private InputStream[] inputStream;
  
  /**
   * The selected input stream.
   */
  private int selectedInputStreamIndex;
  
  /**
   * The machine object.
   */
  private Machine machine;
  
  public InputImpl(Machine machine) {
    
    this.inputStream = new InputStream[2];    
    this.selectedInputStreamIndex = 0;
    this.machine = machine;
  }
  
  public void close() {
    
    if (inputStream != null) {

      for (int i = 0; i < inputStream.length; i++) {
        
        if (inputStream[i] != null) {
          
          inputStream[i].close();
        }
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void setInputStream(final int streamnumber, final InputStream stream) {
    
    inputStream[streamnumber] = stream;
  }
  
  /**
   * {@inheritDoc}
   */
  public void selectInputStream(final int streamnumber) {
    
    selectedInputStreamIndex = streamnumber;    
    machine.getScreen().setPaging(streamnumber != Input.INPUTSTREAM_FILE);
  }
  
  /**
   * {@inheritDoc}
   */
  public InputStream getSelectedInputStream() {
    
    return inputStream[selectedInputStreamIndex];
  }
    
}
