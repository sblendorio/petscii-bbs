/*
 * $Id: ConsoleMachineFactory.java,v 1.0 2019/12/11 21:00 sblendorio Exp $
 * 
 * Created on 2019/12/11
 * Copyright 2019 Francesco Sblendorio, Roberto Manicardi
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
package org.zmpp.textui.bbs;


import eu.sblendorio.bbs.core.BbsThread;
import java.io.IOException;
import org.zmpp.io.IOSystem;
import org.zmpp.io.InputStream;
import org.zmpp.textui.VirtualConsole;
import org.zmpp.vm.Machine;
import org.zmpp.vm.MachineFactory;
import org.zmpp.vm.SaveGameDataStore;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.StatusLine;

/**
 * This class implements machine creation console application (stdin/stdout);
 * 
 * @author Francesco Sblendorio, Roberto Manicardi
 * @version 1.0
 */
public class BBSMachineFactory extends MachineFactory<VirtualConsole> {

  VirtualConsole console;
  IOSystem ioSystem;
  InputStream inputStream;
  StatusLine statusLine;
  ScreenModel screenModel;
  SaveGameDataStore saveGameDataStore;
  BbsThread bbsThread;
  int initialScrollLine = 0;

  private byte[] byteArrayStory;

  public BBSMachineFactory(byte[] byteArrayStory, BbsThread bbsThread, int initialScrollLine) {
    this.byteArrayStory = byteArrayStory;
    this.bbsThread = bbsThread;
    this.initialScrollLine = initialScrollLine;
  }

  public BBSMachineFactory(byte[] byteArrayStory, BbsThread bbsThread) {
    this(byteArrayStory, bbsThread, 0);
  }

  protected byte[] readStoryData() throws IOException {
      return byteArrayStory;
  }

  protected void reportInvalidStory() {
    console.reportInvalidStory();
  }

  protected VirtualConsole initUI(Machine machine) {
    BBSConsole bbsConsole  = new BBSConsole(machine,this.bbsThread,false);
    console = bbsConsole;
    saveGameDataStore = (SaveGameDataStore) bbsConsole;
    ioSystem = (IOSystem) bbsConsole;
    inputStream = new BBSInputStream(machine, bbsThread);
    screenModel = bbsConsole.getScreenModel();
    if (screenModel instanceof BBSScreenModel) ((BBSScreenModel) screenModel).nlines = initialScrollLine;
    statusLine = (StatusLine) bbsConsole.getScreenModel();
    return bbsConsole;
  }

  public VirtualConsole getUI() {
    return console;
  }

  protected IOSystem getIOSystem() {
    return ioSystem;
  }

  protected InputStream getKeyboardInputStream() {
    return inputStream;
  }

  protected StatusLine getStatusLine() {
    return statusLine;
  }

  protected ScreenModel getScreenModel() {
    return screenModel;
  }

  protected SaveGameDataStore getSaveGameDataStore() {
    return saveGameDataStore;
  }

}
