/*
 * Created on 2006/02/15
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

import org.zmpp.windowing.StatusLine;
import org.zmpp.windowing.ScreenModel;
import java.io.IOException;
import java.net.URL;

import org.zmpp.base.DefaultMemory;
import org.zmpp.blorb.BlorbResources;
import org.zmpp.blorb.BlorbFile;
import org.zmpp.blorb.NativeImageFactory;
import org.zmpp.blorb.SoundEffectFactory;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.io.FileInputStream;
import org.zmpp.io.InputStream;
import org.zmpp.io.IOSystem;
import org.zmpp.io.TranscriptOutputStream;
import org.zmpp.media.Resources;
import org.zmpp.vmutil.FileUtils;

/**
 * Constructing a Machine object is a very complex task, the building process
 * deals with creating the game objects, the UI and the I/O system.
 * Initialization was changed so it is not necessary to create a subclass
 * of MachineFactory. Instead, an init struct and a init callback object
 * should be provided.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class MachineFactory {

  /** Initialization structure. */
  public static class MachineInitStruct {
    public java.io.InputStream storyFile, blorbFile;
    public URL storyURL, blorbURL;
    public InputStream keyboardInputStream;
    public StatusLine statusLine;
    public ScreenModel screenModel;
    public IOSystem ioSystem;
    public SaveGameDataStore saveGameDataStore;
    public NativeImageFactory nativeImageFactory;
    public SoundEffectFactory soundEffectFactory;
  }

  private MachineInitStruct initStruct;
  private FormChunk blorbchunk;

  /**
   * Constructor.
   * @param initStruct an initialization structure
   */
  public MachineFactory(MachineInitStruct initStruct) {
    this.initStruct = initStruct;
  }

  /**
   * This is the main creation function.
   * @return the machine
   * @throws IOException if i/o error occurred
   * @throws InvalidStoryException invalid story file
   */
  public Machine buildMachine() throws IOException, InvalidStoryException {
    final MachineImpl machine = new MachineImpl();
    machine.initialize(readStoryData(), readResources());
    if (isInvalidStory(machine.getVersion())) {
      throw new InvalidStoryException();
    }
    initIOSystem(machine);
    return machine;
  }

  // ***********************************************************************
  // ****** Helpers
  // *****************************
  /**
   * Reads the story data.
   * @return the story data
   * @throws IOException if reading story file revealed an error
   */
  private byte[] readStoryData() throws IOException {
    if (initStruct.storyFile != null || initStruct.blorbFile != null)
      return readStoryDataFromFile();
    if (initStruct.storyURL != null || initStruct.blorbURL != null)
      return readStoryDataFromUrl();
    return null;
  }

  /**
   * Reads the story file from the specified URL.
   * @return byte data
   * @throws IOException if i/o error occurred
   */
  private byte[] readStoryDataFromUrl() throws IOException {
    java.io.InputStream storyis = null, blorbis = null;
    try {
      if (initStruct.storyURL != null) {
        storyis = initStruct.storyURL.openStream();
      }
      if (initStruct.blorbURL != null) {
        blorbis = initStruct.blorbURL.openStream();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    if (storyis != null) {
      return FileUtils.readFileBytes(storyis);
    } else {
      return new BlorbFile(readBlorb(blorbis)).getStoryData();
    }
  }

  /**
   * Reads story data from file.
   * @return byte data
   * @throws IOException if i/o error occurred
   */
  private byte[] readStoryDataFromFile() throws IOException {
    if (initStruct.storyFile != null) {
      return FileUtils.readFileBytes(initStruct.storyFile);
    } else {
      // Read from Z BLORB
      FormChunk formchunk = readBlorbFromFile();
      return formchunk != null ? new BlorbFile(formchunk).getStoryData() : null;
    }
  }

  /**
   * Reads the resource data.
   * @return the resource data
   * @throws IOException if reading resources revealed an error
   */
  protected Resources readResources() throws IOException {
    if (initStruct.blorbFile != null) return readResourcesFromFile();
    if (initStruct.blorbURL != null) return readResourcesFromUrl();
    return null;
  }

  /**
   * Reads Blorb data from file.
   * @return the data's form chunk
   * @throws IOException if i/o error occurred
   */
  private FormChunk readBlorbFromFile() throws IOException {
    if (blorbchunk == null) {
      byte[] data = FileUtils.readFileBytes(initStruct.blorbFile);
      if (data != null) {
        blorbchunk = new DefaultFormChunk(new DefaultMemory(data));
        if (!"IFRS".equals(blorbchunk.getSubId())) {
          throw new IOException("not a valid Blorb file");
        }
      }
    }
    return blorbchunk;
  }

  /**
   * Reads story resources from input blorb file.
   * @return resources object
   * @throws IOException if i/o error occurred
   */
  private Resources readResourcesFromFile() throws IOException {
    FormChunk formchunk = readBlorbFromFile();
    return (formchunk != null) ?
      new BlorbResources(initStruct.nativeImageFactory,
                         initStruct.soundEffectFactory, formchunk) : null;
  }

  /**
   * Reads Blorb's form chunk from the specified input stream object.
   * @param blorbis input stream
   * @return the form chunk
   * @throws IOException i/o error occurred
   */
  private FormChunk readBlorb(java.io.InputStream blorbis) throws IOException {
    if (blorbchunk == null) {
      byte[] data = FileUtils.readFileBytes(blorbis);
      if (data != null) {
        blorbchunk = new DefaultFormChunk(new DefaultMemory(data));
      }
    }
    return blorbchunk;
  }

  /**
   * Reads story resources from URL.
   * @return resources object
   * @throws IOException i/o error occurred
   */
  private Resources readResourcesFromUrl() throws IOException {
    FormChunk formchunk = readBlorb(initStruct.blorbURL.openStream());
    return (formchunk != null) ?
      new BlorbResources(initStruct.nativeImageFactory,
                         initStruct.soundEffectFactory, formchunk) : null;
  }

  // ************************************************************************
  // ****** Private methods
  // ********************************
  /**
   * Checks the story file version.
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
  private void initIOSystem(final MachineImpl machine) {
    initInputStreams(machine);
    initOutputStreams(machine);
    machine.setStatusLine(initStruct.statusLine);
    machine.setScreen(initStruct.screenModel);
    machine.setSaveGameDataStore(initStruct.saveGameDataStore);
  }

  /**
   * Initializes the input streams.
   *
   * @param machine the machine object
   */
  private void initInputStreams(final MachineImpl machine) {

    machine.setInputStream(0, initStruct.keyboardInputStream);
    machine.setInputStream(1, new FileInputStream(initStruct.ioSystem,
        machine));
  }

  /**
   * Initializes the output streams.
   *
   * @param machine the machine object
   */
  private void initOutputStreams(final MachineImpl machine) {
    machine.setOutputStream(1, initStruct.screenModel.getOutputStream());
    machine.selectOutputStream(1, true);
    machine.setOutputStream(2, new TranscriptOutputStream(
        initStruct.ioSystem, machine));
    machine.selectOutputStream(2, false);
    machine.setOutputStream(3, new MemoryOutputStream(machine));
    machine.selectOutputStream(3, false);
  }
}
