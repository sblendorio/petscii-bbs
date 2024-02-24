/*
 * Created on 2006/01/29
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zmpp.base.Interruptable;

/**
 * This class implements the SoundSystem interface. The implementation
 * is using a Java 5 thread executor which makes it very easy to
 * assign a control task to each sound which can handle the stopping
 * of a sound easily.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class SoundSystemImpl implements SoundSystem {

  /** The resource database. */
  private MediaCollection<SoundEffect> sounds;

  /** The executor service. */
  private ExecutorService executor;

  /** The interruptable. */
  private Interruptable interruptable;

  /** The current sound task. */
  protected PlaySoundTask currentTask;

  /**
   * Constructor.
   * @param sounds the sound resources
   */
  public SoundSystemImpl(final MediaCollection<SoundEffect> sounds) {
    this.sounds = sounds;
    // That's pretty cool:
    // We can control the number of concurrent sounds to be played
    // simultaneously by the size of the thread pool.
    this.executor = Executors.newSingleThreadExecutor();
  }

  /**
   * This method handles the situation if a sound effect is going to
   * be played and a previous one is not finished.
   */
  protected void handlePreviousNotFinished() {
    // The default behaviour is to stop the previous sound
    currentTask.stop();
  }

  /** {@inheritDoc} */
  public void reset() {
    // no resetting supported
  }

  /** {@inheritDoc} */
  public void play(final int number, final int effect, final int volume,
      final int repeats, final int routine) {
    SoundEffect sound = null;
    // @sound_effect 0 3 followed by @sound_effect 0 4 is called
    // by "The Lurking Horror" and hints that all sound effects should
    // be stopped and unloaded. ZMPP's sound system implementation does
    // nothing at the moment (hey, we have plenty of memory and are
    // in a Java environment)
    if (number == 0) return;

    if (sounds != null) {
      sound = sounds.getResource(number);
    }
    if (sound == null) {
      System.out.println("*BEEP* (playing non-sound)");
    } else {
      if (effect == SoundSystem.EFFECT_START) {
        startSound(number, sound, volume, repeats, routine);
      } else if (effect == SoundSystem.EFFECT_STOP) {
        stopSound(number);
      } else if (effect == SoundSystem.EFFECT_PREPARE) {
        sounds.loadResource(number);
      } else if (effect == SoundSystem.EFFECT_FINISH) {
        stopSound(number);
        sounds.unloadResource(number);
      }
    }
  }

  /**
   * Starts the specified sound.
   * @param number the sound number
   * @param sound the sound object
   * @param volume the volume
   * @param repeats the number of repeats
   * @param routine the interrupt routine
   */
  private void startSound(final int number, final SoundEffect sound,
      final int volume, final int repeats, final int routine) {
    if (currentTask != null && !currentTask.wasPlayed()) {
      handlePreviousNotFinished();
    }
    currentTask = (routine <= 0) ?
      new PlaySoundTask(number, sound, volume, repeats) :
      new PlaySoundTask(number, sound, volume, repeats, interruptable, routine);
    executor.submit(currentTask);
  }

  /**
   * Stops the sound with the given number.
   * @param number the number
   */
  private void stopSound(final int number) {
    // only stop the sound if the numbers match
    if (currentTask != null && currentTask.getResourceNumber() == number) {
      currentTask.stop();
    }
  }
}
