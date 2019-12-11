/*
 * $Id: MachineImpl.java,v 1.41 2006/05/12 22:02:46 weiju Exp $
 * 
 * Created on 2005/10/03
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

import org.zmpp.blorb.BlorbImage;
import org.zmpp.encoding.ZsciiString;
import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.media.MediaCollection;
import org.zmpp.media.PictureManager;
import org.zmpp.media.PictureManagerImpl;
import org.zmpp.media.SoundEffect;
import org.zmpp.media.SoundSystem;
import org.zmpp.media.SoundSystemImpl;
import org.zmpp.vm.StoryFileHeader.Attribute;
import org.zmpp.vmutil.PredictableRandomGenerator;
import org.zmpp.vmutil.RandomGenerator;
import org.zmpp.vmutil.RingBuffer;
import org.zmpp.vmutil.UnpredictableRandomGenerator;

/**
 * This class implements the state and some services of a Z-machine, version 3.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MachineImpl implements Machine {

  /**
   * Number of undo steps.
   */
  private static final int NUM_UNDO = 5;
  
  /**
   * The configuration object.
   */
  private GameData gamedata;
  
  /**
   * The random generator.
   */
  private RandomGenerator random;
  
  /**
   * The status line.
   */
  private StatusLine statusLine;
  
  /**
   * The screen model.
   */
  private ScreenModel screenModel;
  
  /**
   * The save game data store.
   */
  private SaveGameDataStore datastore;
  
  /**
   * The undo states.
   */
  private RingBuffer<PortableGameState> undostates;
  
  /**
   * The input functions object.
   */
  private InputFunctions inputFunctions;
  
  /**
   * The sound system.
   */
  private SoundSystem soundSystem;
  
  /**
   * The picture manager.
   */
  private PictureManager pictureManager;  
  
  /**
   * The CPU object.
   */
  private Cpu cpu;
  
  /**
   * The output streams.
   */
  private Output output;
  
  /**
   * The input streams.
   */
  private Input input;
  
  /**
   * Constructor.
   */
  public MachineImpl() {

    this.inputFunctions = new InputFunctions(this);
  }
  
  /**
   * {@inheritDoc}
   */
  public GameData getGameData() {
    
    return gamedata;
  }
  
  /**
   * {@inheritDoc}
   */
  public Cpu getCpu() {
    
    return cpu;
  }
  
  /**
   * {@inheritDoc}
   */
  public Output getOutput() {
    
    return output;
  }
  
  /**
   * {@inheritDoc}
   */
  public Input getInput() {
    
    return input;
  }
  
  /**
   * {@inheritDoc}
   */
  public void initialize(final GameData gamedata,
      final InstructionDecoder decoder) {
  
    this.gamedata = gamedata;
    this.random = new UnpredictableRandomGenerator();
    this.undostates = new RingBuffer<PortableGameState>(NUM_UNDO);
    
    cpu = new CpuImpl(this, decoder);
    output = new OutputImpl(gamedata, cpu);
    input = new InputImpl(this);
    
    MediaCollection<SoundEffect> sounds = null;
    MediaCollection<BlorbImage> pictures = null;
    int resourceRelease = 0;
    
    if (gamedata.getResources() != null) {
      
      sounds = gamedata.getResources().getSounds();
      pictures = gamedata.getResources().getImages();
      resourceRelease = gamedata.getResources().getRelease();
    }
    
    soundSystem = new SoundSystemImpl(sounds);
    pictureManager = new PictureManagerImpl(resourceRelease, this, pictures);
    
    resetState();
  }

  /**
   * {@inheritDoc}
   */
  public short random(final short range) {
    
    if (range < 0) {
      
      random = new PredictableRandomGenerator(-range);
      return 0;
      
    } else if (range == 0) {
      
      random = new UnpredictableRandomGenerator();
      return 0;
    }
    return (short) ((random.next() % range) + 1);
  }

  // ************************************************************************
  // ****** Control functions
  // ************************************************

  /**
   * {@inheritDoc}
   */
  public void warn(final String msg) {
    
    System.err.println("WARNING: " + msg);
  }
  
  /**
   * {@inheritDoc} 
   */
  public void restart() {

    restart(true);
  }
  
  /**
   * {@inheritDoc} 
   */
  public void quit() {
    
    cpu.setRunning(false);
    
    // On quit, close the streams
    output.print(new ZsciiString("*Game ended*"));
    closeStreams();
    screenModel.redraw();
  }
  
  /**
   * {@inheritDoc}
   */
  public void start() {
    
    cpu.setRunning(true);
  }
  
  // ************************************************************************
  // ****** Machine services
  // ************************************************

  /**
   * {@inheritDoc}
   */
  public void tokenize(final int textbuffer, final int parsebuffer,
      final int dictionaryAddress, final boolean flag) {
    
    inputFunctions.tokenize(textbuffer, parsebuffer, dictionaryAddress, flag);
  }
  
  /**
   * {@inheritDoc}
   */
  public short readLine(final int textbuffer, final int time,
      final int routineAddress) {
    
    return inputFunctions.readLine(textbuffer, time, routineAddress);
  }
  
  /**
   * {@inheritDoc}
   */
  public short readChar(final int time, final int routineAddress) {

    return inputFunctions.readChar(time, routineAddress);
  }
  
  /**
   * {@inheritDoc}
   */
  public SoundSystem getSoundSystem() {
    
    return soundSystem;
  }

  /**
   * {@inheritDoc}
   */
  public PictureManager getPictureManager() {
  
    return pictureManager;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setSaveGameDataStore(final SaveGameDataStore datastore) {
    
    this.datastore = datastore;
  }

  /**
   * {@inheritDoc}
   */
  public void updateStatusLine() {
  
    if (gamedata.getStoryFileHeader().getVersion() <= 3 && statusLine != null) {
      
      final int objNum = cpu.getVariable(0x10);    
      final ZObject obj = gamedata.getObjectTree().getObject(objNum);
      final String objectName = gamedata.getZCharDecoder().decode2Zscii(
          gamedata.getMemoryAccess(),
          obj.getPropertiesDescriptionAddress(), 0).toString();      
      final int global2 = cpu.getVariable(0x11);
      final int global3 = cpu.getVariable(0x12);
      if (gamedata.getStoryFileHeader().isEnabled(Attribute.SCORE_GAME)) {
        
        statusLine.updateStatusScore(objectName, global2, global3);
      } else {
        
        statusLine.updateStatusTime(objectName, global2, global3);
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void setStatusLine(final StatusLine statusLine) {
    
    this.statusLine = statusLine;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setScreen(final ScreenModel screen) {
   
    this.screenModel = screen;
  }
  
  /**
   * {@inheritDoc}
   */
  public ScreenModel getScreen() {
    
    return screenModel;
  }
  
  /**
   * {@inheritDoc}
   */
  public ScreenModel6 getScreen6() {
    
    return (ScreenModel6) screenModel;
  }
  
  /**
   * {@inheritDoc} 
   */
  public boolean save(final int savepc) {
    
    if (datastore != null) {
      
      final PortableGameState gamestate = new PortableGameState();
      gamestate.captureMachineState(this, savepc);
      final WritableFormChunk formChunk = gamestate.exportToFormChunk();
      return datastore.saveFormChunk(formChunk);
    }
    
    return false;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean save_undo(final int savepc) {
    
    final PortableGameState undoGameState = new PortableGameState();
    undoGameState.captureMachineState(this, savepc);
    undostates.add(undoGameState);
    return true;
  }

  /**
   * {@inheritDoc} 
   */
  public PortableGameState restore() {
    
    if (datastore != null) {
      
      final PortableGameState gamestate = new PortableGameState();
      final FormChunk formchunk = datastore.retrieveFormChunk();
      gamestate.readSaveGame(formchunk);
      
      // verification has to be here
      if (verifySaveGame(gamestate)) {
        
        // do not reset screen model, since e.g. AMFV simply picks up the
        // current window state
        restart(false);
        gamestate.transferStateToMachine(this);
        //System.out.printf("restore(), pc is: %4x running: %b\n", getProgramCounter(), isRunning());
        return gamestate;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public PortableGameState restore_undo() {
    
    // do not reset screen model, since e.g. AMFV simply picks up the
    // current window state
    if (undostates.size() > 0) {
      
      final PortableGameState undoGameState =
        undostates.remove(undostates.size() - 1);      
      restart(false);
      undoGameState.transferStateToMachine(this);
      System.out.printf("restore(), pc is: %4x\n", cpu.getProgramCounter());
      return undoGameState;
    }
    return null;
  }
  
  // ***********************************************************************
  // ***** Private methods
  // **************************************
  
  private boolean verifySaveGame(final PortableGameState gamestate) {
    
    // Verify the game according to the standard
    final StoryFileHeader fileHeader = gamedata.getStoryFileHeader();
    int checksum = fileHeader.getChecksum();
    if (checksum == 0) {
      checksum = gamedata.getCalculatedChecksum();
    }
    return gamestate.getRelease() == fileHeader.getRelease()
      && gamestate.getChecksum() == checksum
      && gamestate.getSerialNumber().equals(fileHeader.getSerialNumber());
  }  

  /**
   * Close the streams.
   */
  private void closeStreams() {

    input.close();
    output.close();
  }
  
  /**
   * Resets all state to initial values, using the configuration object.
   */
  private void resetState() {

    output.reset();
    soundSystem.reset();
    cpu.reset();
    //gamedata.getStoryFileHeader().setStandardRevision(1, 0);
    
    if (gamedata.getStoryFileHeader().getVersion() >= 4) {
            
      gamedata.getStoryFileHeader().setEnabled(Attribute.SUPPORTS_TIMED_INPUT, true);
      //gamedata.getStoryFileHeader().setInterpreterNumber(4); // Amiga
      gamedata.getStoryFileHeader().setInterpreterNumber(6); // IBM PC
      gamedata.getStoryFileHeader().setInterpreterVersion(1);
    }
  }
  
  private void restart(final boolean resetScreenModel) {
    
    // Transcripting and fixed font bits survive the restart
    final StoryFileHeader fileHeader = gamedata.getStoryFileHeader();
    final boolean fixedFontForced =
      fileHeader.isEnabled(Attribute.FORCE_FIXED_FONT);
    final boolean transcripting = fileHeader.isEnabled(Attribute.TRANSCRIPTING);
    
    gamedata.reset();
    resetState();
    
    if (resetScreenModel) {
      
      screenModel.reset();    
    }
    fileHeader.setEnabled(Attribute.TRANSCRIPTING, transcripting);
    fileHeader.setEnabled(Attribute.FORCE_FIXED_FONT, fixedFontForced);
  }  
}
