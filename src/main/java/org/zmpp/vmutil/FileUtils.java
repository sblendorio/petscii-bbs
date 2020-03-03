/*
 * $Id: FileUtils.java,v 1.4 2006/04/12 18:00:33 weiju Exp $
 * 
 * Created on 2006/02/13
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
package org.zmpp.vmutil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.zmpp.base.DefaultMemoryAccess;
import org.zmpp.base.MemoryAccess;
import org.zmpp.blorb.BlorbResources;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.Resources;

/**
 * This utility class was introduced to avoid a code smell in data
 * initialization.
 * It offers methods to read data from streams and files.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class FileUtils {

  /**
   * This class only contains static methods.
   */
  private FileUtils() {
    
    // this of course, does nothing, since there are no instances
    super();
  }
  
  /**
   * Creates a resources object from a Blorb file.
   * 
   * @param blorbfile the file
   * @return the resources object or null (on failure)
   */
  public static Resources createResources(final File blorbfile) {
    
    RandomAccessFile raf = null;
    try {
      
      raf = new RandomAccessFile(blorbfile, "r");
      final byte[] data = new byte[(int) raf.length()];
      raf.readFully(data);
      final MemoryAccess memaccess = new DefaultMemoryAccess(data);
      final FormChunk formchunk = new DefaultFormChunk(memaccess);
      return new BlorbResources(formchunk);
      
    } catch (IOException ex) {
      
      ex.printStackTrace();
      
    } finally {
      
      if (raf != null) {
        
        try { raf.close(); } catch (Exception ex) {
          ex.printStackTrace(System.err);
        }
      }        
    }
    return null;
  }
  
  /**
   * Reads an array of bytes from the given input stream.
   * 
   * @param inputstream the input stream
   * @return the bytes or null if the inputstream is null
   */
  public static byte[] readFileBytes(final InputStream inputstream) {

    byte[] data = null;
    
    if (inputstream == null) {
      
      return null;
    }
    
    try {
      
      final List<Byte> buffer = new ArrayList<Byte>();
      
      int databyte = 0;
      do {
        
        databyte = inputstream.read();
        if (databyte != -1) {
          
          buffer.add((byte) databyte);
        }
        
      } while (databyte != -1);
      
      data = new byte[buffer.size()];
      for (int i = 0; i < buffer.size(); i++) {
        
        data[i] = buffer.get(i);
      }

    } catch (IOException ex) {

      ex.printStackTrace();
      
    } finally {
      
      try { inputstream.close(); } catch (Exception ex) {
        
        ex.printStackTrace(System.err);
      } 
    }
    return data;
  }

  /**
   * Reads the bytes from the given file if it is a file and it exists.
   * 
   * @param file the file object
   * @return a byte array
   */
  public static byte[] readFileBytes(final File file) {

    byte[] data = null;
    
    if (file != null && file.exists() && file.isFile()) {
            
      RandomAccessFile raf = null;
      
      try {
        
        raf = new RandomAccessFile(file, "r");
        data = new byte[(int) raf.length()];
        raf.readFully(data);
        
      } catch (IOException ex) {

        ex.printStackTrace();
      
      } finally {
      
        if (raf != null) {
          
          try { raf.close(); } catch (Exception ex) {
            
            ex.printStackTrace(System.err);
          } 
        } 
      }
    }
    return data;
  }
}
