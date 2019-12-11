/*
 * $Id: InputStream.java,v 1.5 2006/05/12 21:53:57 weiju Exp $
 * 
 * Created on 10/07/2005
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
package org.zmpp.io;


/**
 * This interface defines a Z-machine input stream.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface InputStream {

  /**
   * Cancels a wait on the input stream.
   */
  void cancelInput();
  
  /**
   * Reads only one ZSCII character from the stream. This is somewhat
   * immediate.
   * 
   * @param flushBeforeGet flushes the stream before the get operation
   * @return the ZSCII character
   */
  short getZsciiChar(boolean flushBeforeGet);
  
  /**
   * Release underlying resources.
   */
  void close();  
}
