/*
 * $Id: TranscriptOutputStream.java,v 1.9 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 11/08/2005
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.zmpp.encoding.ZsciiEncoding;

/**
 * This class defines an output stream for transcript output (Stream 2).
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class TranscriptOutputStream implements OutputStream {

  private IOSystem iosys;
  private BufferedWriter output;
  private Writer transcriptWriter;
  private boolean enabled;
  private StringBuilder linebuffer;
  private ZsciiEncoding encoding;

  /**
   * Constructor.
   * 
   * @param iosys the I/O system
   */
  public TranscriptOutputStream(final IOSystem iosys,
      final ZsciiEncoding encoding) {
  
    super();
    this.iosys = iosys;
    this.encoding = encoding;
    linebuffer = new StringBuilder();
  }
  
  /**
   * {@inheritDoc}
   */
  private void initFile() {
    
    if (transcriptWriter == null) {
      
      transcriptWriter = iosys.getTranscriptWriter();
      output = new BufferedWriter(transcriptWriter);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void print(final short zsciiChar, final boolean isInput) {
    
    initFile();
    if (output != null) {
      
      if (zsciiChar == ZsciiEncoding.NEWLINE) {
        
        flush();
        
      } else if (zsciiChar == ZsciiEncoding.DELETE) {
        
        linebuffer.deleteCharAt(linebuffer.length() - 1);
        
      } else {
        
        linebuffer.append(encoding.getUnicodeChar(zsciiChar));
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void select(final boolean flag) {
  
    enabled = flag;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSelected() {
    
    return enabled;
  }
  
  /**
   * {@inheritDoc}
   */
  public void flush() {
    
    try {

      if (output != null) {
        
        output.write(linebuffer.toString());
        output.write("\n");
        linebuffer = new StringBuilder();
      }
      
    } catch (IOException ex) {
      
      ex.printStackTrace(System.err);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void close() {

    if (output != null) {
      
      try {
        
        output.close();
        output = null;
        
      } catch (Exception ex) {
        
        ex.printStackTrace(System.err);
      }      
    }
    
    if (transcriptWriter != null) {
      
      try {
        
        transcriptWriter.close();
        transcriptWriter = null;
        
      } catch (Exception ex) {
        
        ex.printStackTrace(System.err);
      }      
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void deletePrevious(final short zchar) {
    
    // transcript does not support deleting
  }
}
