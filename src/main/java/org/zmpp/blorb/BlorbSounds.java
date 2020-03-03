/*
 * $Id: BlorbSounds.java,v 1.6 2006/04/12 02:04:30 weiju Exp $
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
package org.zmpp.blorb;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.DefaultSoundEffect;
import org.zmpp.media.SoundEffect;

/**
 * This class implements the Blorb sound collection.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BlorbSounds extends BlorbMediaCollection<SoundEffect> {

  /**
   * This map implements the database.
   */
  private Map<Integer, SoundEffect> sounds;
  
  /**
   * Constructor.
   * 
   * @param formchunk the form chunk
   */
  public BlorbSounds(FormChunk formchunk) {

    super(formchunk);
  }
  
  /**
   * {@inheritDoc}
   */
  public void clear() {
    
    super.clear();
    sounds.clear();
  }
  
  /**
   * {@inheritDoc}
   */
  protected void initDatabase() {
    
    sounds = new HashMap<Integer, SoundEffect>();
  }

  /**
   * {@inheritDoc}
   */
  protected boolean isHandledResource(final byte[] usageId) {
    
    return usageId[0] == 'S' && usageId[1] == 'n' && usageId[2] == 'd'
           && usageId[3] == ' ';
  }
  
  /**
   * {@inheritDoc}
   */
  public SoundEffect getResource(final int resourcenumber) {

    return sounds.get(resourcenumber);
  }

  /**
   * {@inheritDoc}
   */
  protected boolean putToDatabase(final Chunk chunk, final int resnum) {

    final InputStream aiffStream =
      new  MemoryAccessInputStream(chunk.getMemoryAccess(), 0,
          chunk.getSize() + Chunk.CHUNK_HEADER_LENGTH);
    try {

      final AudioFileFormat aiffFormat =
        AudioSystem.getAudioFileFormat(aiffStream);
      final AudioInputStream stream = new AudioInputStream(aiffStream,
        aiffFormat.getFormat(), (long) chunk.getSize());
      final Clip clip = AudioSystem.getClip();
      clip.open(stream);      
      sounds.put(resnum, new DefaultSoundEffect(clip));
      return true;

    } catch (Exception ex) {

      ex.printStackTrace();
    }
    return false;
  }
}
