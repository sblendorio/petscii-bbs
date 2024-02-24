/*
 * Created on 2006/02/10
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
package org.zmpp.media;

import java.util.logging.Logger;
import org.zmpp.base.Interruptable;

/**
 * Class to play sounds.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class PlaySoundTask implements Runnable, SoundStopListener {

  private static final Logger LOG = Logger.getLogger("org.zmpp");
  private int resourceNum;
  private SoundEffect sound;
  private int repeats;
  private int volume;
  private boolean played;
  private Interruptable interruptable;
  private int routine;
  private boolean stopped;

  /**
   * Constructor.
   * @param resourceNum resource number
   * @param sound sound object
   * @param volume volume
   * @param repeats number of repeats
   */
  public PlaySoundTask(int resourceNum, SoundEffect sound, int volume,
                       int repeats) {
    this(resourceNum, sound, volume, repeats, null, 0);
  }

  /**
   * Constructor.
   * @param resourceNum resource number
   * @param sound sound object
   * @param volume playback volume
   * @param repeats number of repeats
   * @param interruptable interruptable object (should not be used anymore)
   * @param routine the interrupt routine
   * @deprecated interrupts should be implemented differently
   */
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
   * @return the resource number
   */
  public int getResourceNumber() { return resourceNum; }

  /** {@inheritDoc} */
  public void run() {
    sound.addSoundStopListener(this);
    sound.play(repeats, volume);

    synchronized (this) {
      while (!wasPlayed()) {
        try { wait(); } catch (Exception ex) {
          LOG.throwing("PlaySoundTask", "run", ex);
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
   * @return the played flag
   */
  public synchronized boolean wasPlayed() { return played; }

  /**
   * Sets the status of the played flag and notifies waiting threads.
   * @param flag the played flag
   */
  private synchronized void setPlayed(final boolean flag) {
    played = flag;
    notifyAll();
  }

  /**
   * Returns the status of the stopped flag.
   * @return the stopped flag
   */
  private synchronized boolean wasStopped() { return stopped; }

  /**
   * Sets the stopped flag and notifies waiting threads.
   * @param flag true to stop, false otherwise
   */
  private synchronized void setStopped(final boolean flag) {
    stopped = flag;
    notifyAll();
  }

  /** Stops the sound. */
  public synchronized void stop() {
    if (!wasPlayed()) {
      setStopped(true);
      sound.stop();
    }
  }

  /** This method waits until the sound was completely played or stopped. */
  public synchronized void waitUntilDone() {
    while (!wasPlayed()) {
      try { wait(); } catch (Exception ex) {
        LOG.throwing("PlaySoundTask", "waitUntilDone", ex);
      }
    }
  }

  /** {@inheritDoc} */
  public void soundStopped(final SoundEffect aSound) { setPlayed(true); }
}
