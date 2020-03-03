/*
 * $Id: Resources.java,v 1.5 2006/05/12 22:03:13 weiju Exp $
 * 
 * Created on 2006/02/13
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

import org.zmpp.blorb.BlorbImage;


/**
 * This interface defines access to the Z-machine's media resources.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface Resources {

  /**
   * The release number of the resource file.
   * 
   * @return the release number
   */
  int getRelease();
  
  /**
   * Returns the images of this file.
   * 
   * @return the images
   */
  MediaCollection<BlorbImage> getImages();

  /**
   * Returns the sounds of this file.
   * 
   * @return the sounds
   */
  MediaCollection<SoundEffect> getSounds();

  /**
   * Returns the number of the cover art picture.
   * 
   * @return the number of the cover art picture
   */  
  int getCoverArtNum();
  
  /**
   * Returns the inform meta data if available.
   * 
   * @return the meta data
   */
  InformMetadata getMetadata();
  
  /**
   * Returns true if the resource file has information.
   * 
   * @return true if information, false, otherwise
   */
  boolean hasInfo();
}
