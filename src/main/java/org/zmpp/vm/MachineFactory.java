/*
 * $Id: MachineFactory.java,v 1.5 2006/05/01 23:04:18 weiju Exp $
 * 
 * Created on 2006/02/15
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

import java.io.IOException;

import org.zmpp.instructions.DefaultInstructionDecoder;
import org.zmpp.io.FileInputStream;
import org.zmpp.io.IOSystem;
import org.zmpp.io.InputStream;
import org.zmpp.io.TranscriptOutputStream;
import org.zmpp.media.Resources;

/**
 * Constructing a Machine object is a very complex task, the building process
 * deals with creating the game objects, the UI and the I/O system. 
 * This factory class offers a template for the building and leaves the
 * concrete implementation which are dependent on specific input sources
 * and UI technologies to sub classes.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class MachineFactory<T> {

  /**
   * This is the main creation function.
   * 
   * @return the machine
   */
  public Machine buildMachine() throws IOException {
    
    final GameData gamedata =
      new GameDataImpl(readStoryData(), readResources());
  
    if (isInvalidStory(gamedata.getStoryFileHeader().getVersion())) {
    
      reportInvalidStory();
    }
    final Machine machine = new MachineImpl();
    final InstructionDecoder decoder = new DefaultInstructionDecoder();
    machine.initialize(gamedata, decoder);
    initUI(machine);
    initIOSystem(machine);
    return machine;
  }

  // ***********************************************************************
  // ****** Protected methods to be overridden
  // ***************************************************
  
  /**
   * Initializes the user interface objects.
   * 
   * @param machine the machine object
   * @return the resulting top level user interface object
   */
  abstract protected T initUI(Machine machine);
  
  /**
   * Returns the top level user interface object.
   * 
   * @return the top level user interface object
   */
  abstract public T getUI();
  
  /**
   * Reads the story data.
   * 
   * @return the story data
   * @throws IOException if reading story file reveiled an error
   */
  abstract protected byte[] readStoryData() throws IOException;
  
  /**
   * Reads the resource data.
   * 
   * @return the resource data
   * @throws IOException if reading resources reveiled an error
   */
  abstract protected Resources readResources() throws IOException;
  
  /**
   * This function is called to report an invalid story file.
   */
  abstract protected void reportInvalidStory();
  
  /**
   * The IOSystem object.
   * 
   * @return the IOSystem object
   */
  abstract protected IOSystem getIOSystem();
  
  /**
   * The keyboard input stream object.
   * 
   * @return the keyboard input stream
   */
  abstract protected InputStream getKeyboardInputStream();
  
  /**
   * Returns the status line object.
   * 
   * @return the status line object
   */
  abstract protected StatusLine getStatusLine();
  
  /**
   * Returns the screen model object.
   * 
   * @return the screen model
   */
  abstract protected ScreenModel getScreenModel();
  
  /**
   * Returns the save game data store object.
   * 
   * @return the save game data store
   */
  abstract protected SaveGameDataStore getSaveGameDataStore();

  // ************************************************************************
  // ****** Private methods
  // ********************************
  /**
   * Checks the story file version.
   * 
   * @param version the story file version
   * @return true if not supported
   */
  private boolean isInvalidStory(final int version) {
    
    return version < 1 || version > 8;
  }
  
  /**
   * Initializes the I/O system.
   * 
   * @param machine the machine object
   */
  private void initIOSystem(final Machine machine) {
  
    initInputStreams(machine);
    initOutputStreams(machine);    
    machine.setStatusLine(getStatusLine());
    machine.setScreen(getScreenModel());
    machine.setSaveGameDataStore(getSaveGameDataStore());    
  }
  
  /**
   * Initializes the input streams.
   * 
   * @param machine the machine object
   */
  private void initInputStreams(final Machine machine) {
    
    machine.getInput().setInputStream(0, getKeyboardInputStream());
    machine.getInput().setInputStream(1, new FileInputStream(getIOSystem(),
        machine.getGameData().getZsciiEncoding()));
  }

  /**
   * Initializes the output streams.
   * 
   * @param machine the machine object
   */
  private void initOutputStreams(final Machine machine) {
    
    final Output output = machine.getOutput();
    output.setOutputStream(1, getScreenModel().getOutputStream());
    output.selectOutputStream(1, true);
    output.setOutputStream(2, new TranscriptOutputStream(
        getIOSystem(), machine.getGameData().getZsciiEncoding()));
    output.selectOutputStream(2, false);
    output.setOutputStream(3, new MemoryOutputStream(machine));
    output.selectOutputStream(3, false);
  }
}
