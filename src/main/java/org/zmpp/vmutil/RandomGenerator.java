/*
 * $Id: RandomGenerator.java,v 1.4 2006/04/05 03:45:17 weiju Exp $
 * 
 * Created on 2005/09/23
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
package org.zmpp.vmutil;

/**
 * This interface defines the functions of a random number generator within
 * the Z machine.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface RandomGenerator {

  /**
   * The maximum generated value.
   */
  int MAX_VALUE = 32767;
  
  /**
   * Returns the next random value between 1 and MAX_VALUE.
   * 
   * @return a random int value
   */
  int next();
}
