/*
 * $Id: StatusLine.java,v 1.3 2006/01/07 02:51:31 weiju Exp $
 * 
 * Created on 10/20/2005
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
package org.zmpp.vm;

/**
 * This interface defines the Z machine's status line. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface StatusLine {

  /**
   * Updates the status of a score game.
   * 
   * @param objectName the object name
   * @param score the score
   * @param steps the number of steps
   */
  void updateStatusScore(String objectName, int score, int steps);
  
  /**
   * Updates the status of a time game.
   * 
   * @param objectName the object name
   * @param hours the hours
   * @param minutes the minutes
   */
  void updateStatusTime(String objectName, int hours, int minutes);
}
