/*
 * $Id: SoundSystemImpl.java,v 1.8 2006/04/12 18:00:33 weiju Exp $
 * 
 * Created on 2006/01/29
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

import java.awt.Toolkit;
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
 * @version 1.0
 */
public class SoundSystemImpl implements SoundSystem {

  /**
   * The resource database.
   */
  private MediaCollection<SoundEffect> sounds;
  
  /**
   * The executor service.
   */
  private ExecutorService executor;
  
  /**
   * The interruptable.
   */
  private Interruptable interruptable;
  
  /**
   * The current sound task.
   */
  protected PlaySoundTask currentTask;
  
  /**
   * Constructor.
   * 
   * @param sounds the sound resources
   */
  public SoundSystemImpl(final MediaCollection<SoundEffect> sounds) {
    
    super();
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

  /**
   * {@inheritDoc}
   */
  public void reset() {
  
    // no resetting supported
  }
  
  /**
   * {@inheritDoc}
   */
  public void play(final int number, final int effect, final int volume,
      final int repeats, final int routine) {

    SoundEffect sound = null;
    
    // @sound_effect 0 3 followed by @sound_effect 0 4 is called
    // by "The Lurking Horror" and hints that all sound effects should
    // be stopped and unloaded. ZMPP's sound system implementation does
    // nothing at the moment (hey, we have plenty of memory and are
    // in a Java environment)
    if (number == 0) {
      return;
    }
    
    if (sounds != null) {
      sound = sounds.getResource(number);
    }
    if (sound == null) {

      Toolkit.getDefaultToolkit().beep();
      
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
   * 
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
   * 
   * @param number the number
   */
  private void stopSound(final int number) {
    
    // only stop the sound if the numbers match
    if (currentTask != null && currentTask.getResourceNumber() == number) {
      
      currentTask.stop();
    }
  }  
}
