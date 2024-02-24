/*
 * Created on 10/03/2005
 * Copyright (c) 2005-2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.vm;

import java.io.Serializable;

import org.zmpp.base.Memory;
import org.zmpp.base.StoryFileHeader;
import org.zmpp.encoding.IZsciiEncoding;
import org.zmpp.media.PictureManager;
import org.zmpp.media.Resources;
import org.zmpp.media.SoundSystem;
import org.zmpp.windowing.ScreenModel;
import org.zmpp.windowing.ScreenModel6;
import org.zmpp.windowing.StatusLine;

/**
 * This interface acts as a central access point to the Z-Machine's components.
 * It is mainly provided as a service point for the instructions to manipulate
 * and read the VM's internal state.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface Machine
extends ObjectTree, Input, Output, Cpu, Memory, IZsciiEncoding, Serializable {

  /**
   * Initialization function.
   *
   * @param data the story data
   * @param resources Blorb resources
   */
  void initialize(byte[] data, Resources resources);

  /**
   * Returns the story file version.
   * @return the story file version
   */
  int getVersion();

  /**
   * Returns the release.
   * @return the release
   */
  int getRelease();

  /**
   * Checks the check sum.
   * @return true if valid, false, otherwise
   */
  boolean hasValidChecksum();

  // **********************************************************************
  // **** Main machine objects
  // *******************************

  /**
   * Returns the story file header.
   * @return file header
   */
  StoryFileHeader getFileHeader();

  /**
   * Returns story resources.
   * @return story resources
   */
  Resources getResources();
  // **********************************************************************
  // **** Tokenizing functions
  // **** We could refine this by exposing the tokenizers
  // **** instead of dictionary functionality
  // **********************************************************

  /**
   * Looks up token in dictionary.
   * @param dictionaryAddress address of dictionary
   * @param token token to look up
   * @return index in dictionary
   */
  int lookupToken(int dictionaryAddress, String token);

  /**
   * Returns the dictionary delimiters.
   * @return dictionary delimiters
   */
  String getDictionaryDelimiters();

  // **********************************************************************
  // **** Encoding functions
  // **********************************************************

  /**
   * Encode memory location to ZSCII.
   * @param source source position
   * @param length memory length in byte
   * @param destination destination position
   */
  void encode(int source, int length, int destination);

  /**
   * Decode memory address to ZSCII.
   * @param address memory address
   * @param length length in bytes
   * @return ZSCII string
   */
  String decode2Zscii(int address, int length);

  /**
   * Returns the number of Z-encoded bytes at the specified address.
   * @param address the string address
   * @return number of z-encoded bytes
   */
  int getNumZEncodedBytes(int address);

  // ************************************************************************
  // ****** Control functions
  // ************************************************

  /**
   * Returns the current run state of the machine
   * @return the run state
   */
  MachineRunState getRunState();

  /**
   * Sets the current run state of the machine
   * @param runstate the run state
   */
  void setRunState(MachineRunState runstate);

  /**
   * Halts the machine with the specified error message.
   * @param errormsg the error message
   */
  void halt(String errormsg);

  /**
   * Restarts the virtual machine.
   */
  void restart();

  /**
   * Starts the virtual machine.
   */
  void start();

  /**
   * Exists the virtual machine.
   */
  void quit();

  /**
   * Outputs a warning message.
   * @param msg the message
   */
  void warn(String msg);

  // **********************************************************************
  // **** Services
  // *******************************

  /**
   * Tokenizes the text in the text buffer using the specified parse buffer.
   * @param textbuffer the text buffer
   * @param parsebuffer the parse buffer
   * @param dictionaryAddress the dictionary address or 0 for the default
   * dictionary
   * @param flag if set, unrecognized words are not written into the parse
   * buffer and their slots are left unchanged
   */
  void tokenize(int textbuffer, int parsebuffer, int dictionaryAddress,
                boolean flag);

  /**
   * Reads a string from the currently selected input stream into
   * the text buffer address.
   * @param textbuffer the text buffer address
   * @return the terminator character
   */
  char readLine(int textbuffer);

  /**
   * Reads a ZSCII char from the selected input stream.
   * @return the selected ZSCII char
   */
  char readChar();

  /**
   * Returns the sound system.
   * @return the sound system
   */
  SoundSystem getSoundSystem();

  /**
   * Returns the picture manager.
   * @return the picture manager
   */
  PictureManager getPictureManager();

  /**
   * Generates a number in the range between 1 and <i>range</i>. If range is
   * negative, the random generator will be seeded to abs(range), if
   * range is 0, the random generator will be initialized to a new
   * random seed. In both latter cases, the result will be 0.
   * @param range the range
   * @return a random number
   */
  char random(short range);

  /**
   * Updates the status line.
   */
  void updateStatusLine();

  /**
   * Sets the Z-machine's status line.
   * @param statusline the status line
   */
  void setStatusLine(StatusLine statusline);

  /**
   * Sets the game screen.
   * @param screen the screen model
   */
  void setScreen(ScreenModel screen);

  /**
   * Gets the game screen.
   * @return the game screen
   */
  ScreenModel getScreen();

  /**
   * Returns screen model 6.
   * @return screen model 6
   */
  ScreenModel6 getScreen6();

  /**
   * Sets the save game data store.
   * @param datastore the data store
   */
  void setSaveGameDataStore(SaveGameDataStore datastore);

  /**
   * Saves the current state.
   * @param savepc the save pc
   * @return true on success, false otherwise
   */
  boolean save(int savepc);

  /**
   * Saves the current state in memory.
   * @param savepc the save pc
   * @return true on success, false otherwise
   */
  boolean save_undo(int savepc);

  /**
   * Restores a previously saved state.
   * @return the portable game state
   */
  PortableGameState restore();

  /**
   * Restores a previously saved state from memory.
   * @return the portable game state
   */
  PortableGameState restore_undo();
}
