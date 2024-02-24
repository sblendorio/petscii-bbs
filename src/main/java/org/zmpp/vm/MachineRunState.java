/*
 * Created on 05/27/2008
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

/**
 * This class models a machine run state that also stores data for timed
 * input, so a client application can call an interrupt method on the machine.
 * @author Wei-ju Wu
 * @version 1.5
 */
public final class MachineRunState implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Reading modes.
   */
  private enum ReadMode { NONE, READ_CHAR, READ_LINE };
  private int time, numLeftOverChars;
  private char routine, textbuffer;
  private ReadMode readMode = ReadMode.NONE;

  /**
   * Default constructor.
   */
  private MachineRunState() { }

  /**
   * Constructor for reading modes.
   * @param readMode the read mode
   * @param time the interrupt routine time interval
   * @param routine the packed interrupt routine address
   * @param numLeftOverChars the number of characters indicated as left over
   * @param textbuffer text buffer address
   */
  private MachineRunState(ReadMode readMode, int time, char routine,
    int numLeftOverChars, char textbuffer) {
    this.readMode = readMode;
    this.time = time;
    this.routine = routine;
    this.numLeftOverChars = numLeftOverChars;
    this.textbuffer = textbuffer;
  }

  /**
   * Returns the interrupt interval.
   * @return the interrupt interval
   */
  public int getTime() { return time; }

  /**
   * Returns the packed address of the interrupt address.
   * @return packed interrupt routine address
   */
  public char getRoutine() { return routine; }

  /**
   * Returns true if machine is waiting for input.
   * @return true if waiting for input, false otherwise
   */
  public boolean isWaitingForInput() { return readMode != ReadMode.NONE; }

  /**
   * Returns true if machine is in read character mode.
   * @return true if read character mode, false otherwise
   */
  public boolean isReadChar() { return readMode == ReadMode.READ_CHAR; }

  /**
   * Returns true if machine is in read line mode.
   * @return true if read line mode, false otherwise
   */
  public boolean isReadLine() { return readMode == ReadMode.READ_LINE; }

  /**
   * Returns the number of characters left over from previous input.
   * @return the number of left over characters
   */
  public int getNumLeftOverChars() { return numLeftOverChars; }

  /**
   * Returns the address of the text buffer.
   * @return the text buffer
   */
  public char getTextBuffer() { return textbuffer; }

  /**
   * Running state.
   */
  public static final MachineRunState RUNNING = new MachineRunState();

  /**
   * Stopped state.
   */
  public static final MachineRunState STOPPED = new MachineRunState();

  /**
   * Creates a read line mode object with the specified interrup data.
   * @param time interrupt interval
   * @param routine interrupt routine
   * @param numLeftOverChars the number of characters left over
   * @param textbuffer the address of the text buffer
   * @return machine run state object
   */
  public static MachineRunState createReadLine(int time, char routine,
    int numLeftOverChars, char textbuffer) {
    return new MachineRunState(ReadMode.READ_LINE, time, routine,
                               numLeftOverChars, textbuffer);
  }

  /**
   * Creates a read character mode object with the specified interrupt data.
   * @param time interrupt interval
   * @param routine interrupt routine
   * @return machine state
   */
  public static MachineRunState createReadChar(int time, char routine) {
    return new MachineRunState(ReadMode.READ_CHAR, time, routine, 0, (char) 0);
  }
}
