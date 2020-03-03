/*
 * $Id: Short0Instruction.java,v 1.8 2006/05/12 21:56:37 weiju Exp $
 * 
 * Created on 10/03/2005
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

import org.zmpp.vm.Machine;



/**
 * This class represents instructions of type SHORT, 0OP.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class Short0Instruction extends AbstractInstruction {

  /**
   * Constructor.
   * 
   * @param machineState a reference to the MachineState object
   * @param opcode the instruction's opcode
   */
  public Short0Instruction(Machine machineState, int opcode) {
    
    super(machineState, opcode);
  }
  
  /**
   * {@inheritDoc}
   */
  public void doInstruction() {
    
    switch (getOpcode()) {

      case Short0StaticInfo.OP_RTRUE:
        returnFromRoutine(TRUE);
        break;
      case Short0StaticInfo.OP_RFALSE:
        returnFromRoutine(FALSE);
        break;
      case Short0StaticInfo.OP_NOP:
        nextInstruction();
        break;
      case Short0StaticInfo.OP_SAVE:
        saveToStorage(getMachine().getCpu().getProgramCounter() + 1);
        break;
      case Short0StaticInfo.OP_RESTORE:
        
        restoreFromStorage();
        break;
      case Short0StaticInfo.OP_RESTART:
        getMachine().restart();
        break;
      case Short0StaticInfo.OP_QUIT:
        getMachine().quit();
        break;
      case Short0StaticInfo.OP_RET_POPPED:        
        returnFromRoutine(getCpu().getVariable(0));
        break;
      case Short0StaticInfo.OP_POP:
        if (getStoryFileVersion() < 5) {
          pop();
        } else {
          z_catch();
        }
        break;
      case Short0StaticInfo.OP_NEW_LINE:
        getMachine().getOutput().newline();
        nextInstruction();
        break;
      case Short0StaticInfo.OP_SHOW_STATUS:
        
        getMachine().updateStatusLine();          
        nextInstruction();
        break;
      case Short0StaticInfo.OP_VERIFY:
        branchOnTest(getMachine().getGameData().hasValidChecksum());
        break;
      case Short0StaticInfo.OP_PIRACY:
        branchOnTest(true);
        break;
      default:        
        throwInvalidOpcode();
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public InstructionForm getInstructionForm() { return InstructionForm.SHORT; }
  
  /**
   * {@inheritDoc}
   */
  public OperandCount getOperandCount() { return OperandCount.C0OP; }
  
  /**
   * {@inheritDoc}
   */
  protected InstructionStaticInfo getStaticInfo() {

    return Short0StaticInfo.getInstance();
  }
  
  private void pop() {
    
    getCpu().getVariable(0);
    nextInstruction();    
  }
  
  private void z_catch() {
    
    // Stores the index of the current stack frame
    storeResult((short) (getCpu().getRoutineContexts().size() - 1));
    nextInstruction();
  }
}
