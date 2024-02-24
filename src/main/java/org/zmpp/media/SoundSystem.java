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

/**
 * This interface defines the sound system of the Z-machine preservation
 * project.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface SoundSystem {

  /** High pitched bleep. */
  int BLEEP_HIGH  = 1;

  /** Low pitched bleep. */
  int BLEEP_LOW   = 2;

  /** Prepares a sound. */
  int EFFECT_PREPARE  = 1;

  /** Starts a sound. */
  int EFFECT_START    = 2;

  /** Stops a sound. */
  int EFFECT_STOP     = 3;

  /** Finishes a sound. */
  int EFFECT_FINISH   = 4;

  /** The maximum value for volume. */
  int VOLUME_MAX = 0;

  /** The minimum value for volume. */
  int VOLUME_MIN = 255;

  /** Sets the volume to default. */
  int VOLUME_DEFAULT = -1;

  /**
   * Plays a sound.
   *
   * @param number the number of the resource, 1 and 2 are bleeps
   * @param effect the effect
   * @param volume the volume
   * @param repeats how often should the sound be played
   * @param routine the interrupt routine (can be 0)
   */
  void play(int number, int effect, int volume, int repeats, int routine);

  /** Resets the sound system. */
  void reset();
}
