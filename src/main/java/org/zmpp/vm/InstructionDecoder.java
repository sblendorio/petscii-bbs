/*
 * $Id: InstructionDecoder.java,v 1.22 2006/02/14 22:49:32 weiju Exp $
 * 
 * Created on 09/24/2005
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

import org.zmpp.base.MemoryReadAccess;

/**
 * The instruction decoder decodes an instruction at a specified address.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface InstructionDecoder {
    
  public void initialize(Machine machine, MemoryReadAccess memaccess);
  
  /**
   * Decode the instruction at the specified address.
   * 
   * @param instructionAddress the current instruction's address
   * @return the instruction at the specified address
   */
  public Instruction decodeInstruction(int instructionAddress);
}
