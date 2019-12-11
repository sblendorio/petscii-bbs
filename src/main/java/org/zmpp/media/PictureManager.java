/*
 * $Id: PictureManager.java,v 1.2 2006/05/12 22:03:13 weiju Exp $
 * 
 * Created on 2006/02/22
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

import java.awt.Dimension;

import org.zmpp.blorb.BlorbImage;

public interface PictureManager {

  /**
   * Returns the size of the specified picture or null if the picture does not
   * exist.
   * 
   * @param picturenum the number of the picture
   * @return the size
   */
  Dimension getPictureSize(int picturenum);
  
  /**
   * Returns the data of the specified picture. If it is not available, this
   * method returns null.
   * 
   * @param picturenum the picture number
   * @return the data
   */
  BlorbImage getPicture(int picturenum);
  
  /**
   * Returns the number of pictures.
   * 
   * @return the number of pictures
   */
  int getNumPictures();

  /**
   * Preloads the specified picture numbers.
   * 
   * @param picnumbers the picture numbers to preload
   */
  void preload(int[] picnumbers);
  
  /**
   * Returns the release number of the picture file.
   * 
   * @return the release number
   */
  int getRelease();
  
  /**
   * Resets the picture manager.
   */
  void reset();
}
