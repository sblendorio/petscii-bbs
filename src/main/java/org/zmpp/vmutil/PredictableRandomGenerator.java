/*
 * $Id: PredictableRandomGenerator.java,v 1.2 2006/01/07 02:51:31 weiju Exp $
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

import java.util.Random;

/**
 * This class implements the predictable random number generator mentioned
 * in the Z machine standards document. It takes a seed and generates the
 * same sequence of numbers for equal seeds.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class PredictableRandomGenerator implements RandomGenerator {

  /**
   * The random generator.
   */
  private Random rand;
  
  /**
   * Constructor.
   * 
   * @param seed the start seed.
   */
  public PredictableRandomGenerator(long seed) {
    
    rand = new Random(seed);
  }

  /**
   * {@inheritDoc}
   */
  public int next() {
    
    return rand.nextInt(MAX_VALUE - 1) + 1;
  }
}
