/*
 * $Id: DefaultSoundEffect.java,v 1.4 2006/04/12 18:00:33 weiju Exp $
 * 
 * Created on 2006/02/06
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

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;


/**
 * This class implements the SoundEffect interface and encapsulates an
 * audio clip. By this means we provide more abstractness and flexibility.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DefaultSoundEffect implements SoundEffect, LineListener {

  /**
   * The volume range is between 0 and 64 for AIFF sounds.
   */
  public static final int MAX_VOLUME = 64;
  
  /**
   * The audio clip.
   */
  private Clip clip;
  
  /**
   * The listeners.
   */
  private List<SoundStopListener> listeners;

  /**
   * Constructor.
   * 
   * @param clip an audio clip
   */
  public DefaultSoundEffect(final Clip clip) {
    
    super();
    this.clip = clip;
    listeners = new ArrayList<SoundStopListener>();
    clip.addLineListener(this);
  }

  /**
   * {@inheritDoc}
   */
  public void play(final int number, final int volume) {
    
    setVolume(volume);
    
    if (number == 1) {
      
      clip.start();
      
    } else {
      
      clip.loop(number - 1);
    }
    // Reset to full volume
    //setVolume(255);
  }
  
  /**
   * Sets the volume.
   * 
   * @param vol the volume
   */
  private void setVolume(final int vol) {
    
    int volume = vol;
    if (volume < 0) {
      
      volume = MAX_VOLUME;
    }
    float gainDb = 0.0f;
    final FloatControl gain = (FloatControl)
        clip.getControl(FloatControl.Type.MASTER_GAIN);
    
    if (volume == 0) {
      
      gainDb = gain.getMinimum();
      
    } else if (volume < MAX_VOLUME) {
      
      // The volume algorithm is subtractive: The implementation assumes that
      // the sound is already at maximum volume, so we avoid distortion by
      // making the amplitude values
      // The scaling factor for the volume would be 20 normally, but
      // it seems that 13 is better
      gainDb = (float) (Math.log10(MAX_VOLUME - volume) * 13.0);
    }    
    gain.setValue(-gainDb);
  }

  /**
   * {@inheritDoc}
   */
  public void stop() {

    clip.stop();
  }
  
  /**
   * {@inheritDoc}
   */
  public void addSoundStopListener(final SoundStopListener l) {
    
    listeners.add(l);
  }
  
  /**
   * {@inheritDoc}
   */
  public void removeSoundStopListener(final SoundStopListener l) {
    
    listeners.remove(l);
  }

  /**
   * {@inheritDoc}
   */
  public void update(final LineEvent e) {
    
    if (e.getType() == LineEvent.Type.STOP) {

      notifySoundStopped();
    }
  }
  
  /**
   * Notify all listeners that the sound has stopped.
   */
  private void notifySoundStopped() {
    
    for (SoundStopListener l : listeners) {
      
      l.soundStopped(this);
    }
  }
}
