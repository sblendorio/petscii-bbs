/*
 * $Id: PlaySoundTask.java,v 1.4 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 2006/02/10
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
package org.zmpp.media;

import org.zmpp.base.Interruptable;

public class PlaySoundTask implements Runnable, SoundStopListener {

  private int resourceNum;
  private SoundEffect sound;
  private int repeats;
  private int volume;
  private boolean played;
  private Interruptable interruptable;
  private int routine;
  private boolean stopped;
  
  public PlaySoundTask(int resourceNum, SoundEffect sound, int volume,
                       int repeats) {
  
    this(resourceNum, sound, volume, repeats, null, 0);
  }
  
  public PlaySoundTask(int resourceNum, SoundEffect sound, int volume,
      int repeats, Interruptable interruptable, int routine) {
    
    this.resourceNum = resourceNum;
    this.sound = sound;
    this.repeats = repeats;
    this.volume = volume;
    this.interruptable = interruptable;
    this.routine = routine;
  }
  
  /**
   * Returns the resource number.
   * 
   * @return the resource number
   */
  public int getResourceNumber() {
  
    return resourceNum;
  }
  
  /**
   * {@inheritDoc}
   */
  public void run() {
  
    sound.addSoundStopListener(this);
    sound.play(repeats, volume);
      
    synchronized (this) {
      
      while (!wasPlayed()) {
        
        try { wait(); } catch (Exception ex) {
          
          ex.printStackTrace(System.err);
        }
      }
    }
    sound.removeSoundStopListener(this);
    if (!wasStopped() && interruptable != null && routine > 0) {
      
      interruptable.setInterruptRoutine(routine);
    }
  }
  
  /**
   * Returns the status of the played flag.
   * 
   * @return the played flag
   */
  public synchronized boolean wasPlayed() {
    
    return played;
  }
  
  /**
   * Sets the status of the played flag and notifies waiting threads.
   * 
   * @param flag the played flag
   */
  private synchronized void setPlayed(final boolean flag) {
  
    played = flag;
    notifyAll();
  }
  
  /**
   * Returns the status of the stopped flag.
   * 
   * @return the stopped flag
   */
  private synchronized boolean wasStopped() {
    
    return stopped;
  }
  
  /**
   * Sets the stopped flag and notifies waiting threads.
   * 
   * @param flag
   */
  private synchronized void setStopped(final boolean flag) {
    
    stopped = flag;
    notifyAll();
  }
  
  /**
   * Stops the sound.
   */
  public synchronized void stop() {
    
    if (!wasPlayed()) {
      
      setStopped(true);
      sound.stop();
    }
  }

  /**
   * This method waits until the sound was completely played or stopped.
   */
  public synchronized void waitUntilDone() {
    
    while (!wasPlayed()) {
      
      try { wait(); } catch (Exception ex) {
        
        ex.printStackTrace(System.err);
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void soundStopped(final SoundEffect sound) {

    setPlayed(true);
  }  
}
