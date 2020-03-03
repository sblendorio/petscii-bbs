/*
 * $Id: LurkingHorrorSoundSystem.java,v 1.2 2006/04/05 03:45:16 weiju Exp $
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


/**
 * The game "The Lurking Horror" serializes sound effect playing rather
 * than stopping previous ones.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class LurkingHorrorSoundSystem extends SoundSystemImpl {

  /**
   * Constructor.
   * 
   * @param sounds the sound resources
   */
  public LurkingHorrorSoundSystem(MediaCollection<SoundEffect> sounds) {
    
    super(sounds);
  }
  
  /**
   * {@inheritDoc}
   */
  protected void handlePreviousNotFinished() {
    
    currentTask.waitUntilDone();
  }
}
