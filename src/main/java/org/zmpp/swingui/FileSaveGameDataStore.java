/*
 * $Id: FileSaveGameDataStore.java,v 1.1 2006/03/22 21:41:24 weiju Exp $
 * 
 * Created on 2006/03/22
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
package org.zmpp.swingui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JFileChooser;

import org.zmpp.base.DefaultMemoryAccess;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.vm.SaveGameDataStore;

/**
 * This class saves game states into the file system.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class FileSaveGameDataStore implements SaveGameDataStore {

  private Component parent;
  
  /**
   * Constructor.
   * 
   * @param parent the parent component for the file dialog
   */
  public FileSaveGameDataStore(Component parent) {
    
    this.parent = parent;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean saveFormChunk(WritableFormChunk formchunk) {
    
    File currentdir = new File(System.getProperty("user.dir"));    
    JFileChooser fileChooser = new JFileChooser(currentdir);
    fileChooser.setDialogTitle("Save game ...");
    
    if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
      
      File savefile = fileChooser.getSelectedFile();
      RandomAccessFile raf = null;
      try {
        
        raf = new RandomAccessFile(savefile, "rw");
        byte[] data = formchunk.getBytes();
        raf.write(data);
        return true;
        
      } catch (IOException ex) {
       
        ex.printStackTrace();
        
      } finally {
        
        if (raf != null) try { raf.close(); } catch (Exception ex) { }
      }
    }
    return false;
  }  

  /**
   * {@inheritDoc}
   */
  public FormChunk retrieveFormChunk() {
    
    File currentdir = new File(System.getProperty("user.dir"));    
    JFileChooser fileChooser = new JFileChooser(currentdir);
    fileChooser.setDialogTitle("Restore game...");
    if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      
      File savefile = fileChooser.getSelectedFile();
      RandomAccessFile raf = null;
      try {
        
        raf = new RandomAccessFile(savefile, "r");
        byte[] data = new byte[(int) raf.length()];
        raf.readFully(data);
        return new DefaultFormChunk(new DefaultMemoryAccess(data));
        
      } catch (IOException ex) {
       
        ex.printStackTrace();
        
      } finally {
        
        if (raf != null) try { raf.close(); } catch (Exception ex) { }
      }
    }
    return null;
  }
}
