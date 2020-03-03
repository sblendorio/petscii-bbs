/*
 * $Id: PrintLiteralInstruction.java,v 1.11 2006/05/12 21:56:37 weiju Exp $
 * 
 * Created on 10/06/2005
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
package org.zmpp.instructions;

import org.zmpp.base.MemoryReadAccess;
import org.zmpp.vm.Machine;

/**
 * This class implements the print and print_ret instructions.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class PrintLiteralInstruction extends AbstractInstruction {

  /**
   * The instruction address.
   */
  private int instructionAddress;
  
  /**
   * Memory access object.
   */
  private MemoryReadAccess memaccess;
  
  /**
   * Constructor.
   * 
   * @param machineState the machine state reference
   * @param opcode the opcode
   * @param memaccess the memory access object
   * @param instrAddress the instruction address
   */
  public PrintLiteralInstruction(Machine machineState, int opcode,
      MemoryReadAccess memaccess, int instrAddress) {
    
    super(machineState, opcode);
    
    this.memaccess = memaccess;
    instructionAddress = instrAddress;
  }
  
  /**
   * {@inheritDoc}
   */
  protected String getOperandString() {

    final String str =
      getMachine().getGameData().getZCharDecoder().decode2Zscii(memaccess,
        instructionAddress + 1, 0).toString();
    return "\"" + str + "\"";
  }
  
  /**
   * {@inheritDoc}
   */
  public InstructionForm getInstructionForm() {
    
    return InstructionForm.SHORT;
  }

  /**
   * {@inheritDoc}
   */
  public OperandCount getOperandCount() {
    
    return OperandCount.C0OP;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setLength(final int length) {

    // overridden to do nothing
  }
  
  /**
   * {@inheritDoc}
   */
  public int getLength() {
    
    return getLiteralLengthInBytes() + 1;
  }

  /**
   * {@inheritDoc}
   */
  protected InstructionStaticInfo getStaticInfo() {
    
    return PrintLiteralStaticInfo.getInstance();
  }

  /**
   * {@inheritDoc}
   */
  public void doInstruction() {

    if (getOpcode() == PrintLiteralStaticInfo.OP_PRINT_RET) {
      
      getMachine().getOutput().printZString(instructionAddress + 1);
      getMachine().getOutput().newline();
      returnFromRoutine(TRUE);
      
    } else if (getOpcode() == PrintLiteralStaticInfo.OP_PRINT) {
      
      getMachine().getOutput().printZString(instructionAddress + 1);
      nextInstruction();
      
    } else {
     
      throwInvalidOpcode();
    }
  }
  
  /**
   * Returns the address of the literal.
   * 
   * @return the address of the literal
   */
  public int getLiteralAddress() {
    
    return instructionAddress + 1;
  }

  /**
   * Determines the byte length of the ZSCII string literal.
   *  
   * @return the length of the literal in bytes
   */
  private int getLiteralLengthInBytes() {
    
    int currentAddress = instructionAddress + 1;
    int zword = 0;
    
    do {
      
      zword = memaccess.readUnsignedShort(currentAddress);
      currentAddress += 2;
      
    } while ((zword & 0x8000) == 0);
    
    return currentAddress - (instructionAddress + 1);
  }  
}
