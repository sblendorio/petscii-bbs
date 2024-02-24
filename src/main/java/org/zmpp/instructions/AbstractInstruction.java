/*
 * Created on 2008/07/23
 * Copyright (c) 2005-2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.instructions;

import org.zmpp.base.MemoryUtil;
import org.zmpp.vm.Instruction;
import org.zmpp.vm.Machine;
import org.zmpp.vm.PortableGameState;
import org.zmpp.vm.RoutineContext;
import org.zmpp.windowing.ScreenModel6;
import org.zmpp.windowing.Window6;

/**
 * An abstract instruction to replace the old instruction scheme.
 * Goes with the NewInstructionDecoder.
 * @author Wei-ju Wu
 * @version 1.5
 */
public abstract class AbstractInstruction implements Instruction {
  /**
   * Branch information.
   */
  public static class BranchInfo {
    public boolean branchOnTrue;
    public int numOffsetBytes;
    public int addressAfterBranchData;
    public short branchOffset;
    /**
     * Constructor.
     * @param branchOnTrue branch on true flag
     * @param numOffsetBytes number of offset bytes
     * @param addressAfterBranchData address after branch data
     * @param branchOffset branch offset
     */
    public BranchInfo(boolean branchOnTrue, int numOffsetBytes,
                      int addressAfterBranchData, short branchOffset) {
      this.branchOnTrue = branchOnTrue;
      this.numOffsetBytes = numOffsetBytes;
      this.addressAfterBranchData = addressAfterBranchData;
      this.branchOffset = branchOffset;
    }
  }

  private Machine machine;
  private int opcodeNum;
  private Operand[] operands;
  private char storeVariable;
  private BranchInfo branchInfo;
  private int opcodeLength;

  /**
   * Constructor.
   * @param machine Machine object
   * @param opcodeNum opcode number
   * @param operands operands
   * @param storeVar store variable
   * @param branchInfo branch information
   * @param opcodeLength opcode length
   */
  public AbstractInstruction(Machine machine, int opcodeNum,
                             Operand[] operands,
                             char storeVar,
                             BranchInfo branchInfo,
                             int opcodeLength) {
    this.machine = machine;
    this.opcodeNum = opcodeNum;
    this.operands = operands;
    this.storeVariable = storeVar;
    this.branchInfo = branchInfo;
    this.opcodeLength = opcodeLength;
  }

  /**
   * Returns the machine object.
   * @return the Machine object
   */
  protected Machine getMachine() { return machine; }

  /**
   * Returns the story version.
   * @return story version
   */
  protected int getStoryVersion() { return machine.getVersion(); }
  /**
   * Returns the operand count.
   * @return operand count
   */
  protected abstract OperandCount getOperandCount();
  /**
   * The opcode length is a crucial attribute for program control, expose it
   * for testing.
   * @return the length of the instruction in memory
   */
  public int getLength() { return opcodeLength; }

  /**
   * Returns the instruction's opcode.
   * @return the opcode
   */
  protected int getOpcodeNum() { return opcodeNum; }

  /**
   * Determines whether this instruction stores a result.
   * @return true if stores result, false otherwise
   */
  protected boolean storesResult() {
    return InstructionInfoDb.getInstance().getInfo(getOperandCount(),
            opcodeNum, machine.getVersion()).isStore();
  }

  // *********************************************************************
  // ******** Variable access
  // ***********************************

  /**
   * Returns the number of operands.
   * @return the number of operands
   */
  protected int getNumOperands() { return operands.length; }
  /**
   * Converts the specified value into a signed value, depending on the
   * type of the operand. If the operand is LARGE_CONSTANT or VARIABLE,
   * the value is treated as a 16 bit signed integer, if it is SMALL_CONSTANT,
   * it is treated as an 8 bit signed integer.
   * @param operandNum the operand number
   * @return a signed value
   */
  protected short getSignedValue(final int operandNum) {
    /*
    // I am not sure if this is ever applicable....
    if (operands[operandNum].getType() == OperandType.SMALL_CONSTANT) {
      return MemoryUtil.unsignedToSigned8(getUnsignedValue(operandNum));
    }*/
    return MemoryUtil.unsignedToSigned16(getUnsignedValue(operandNum));
  }

  /**
   * A method to return the signed representation of the contents of a variable
   * @param varnum the variable number
   * @return the signed value
   */
  protected short getSignedVarValue(char varnum) {
    return MemoryUtil.unsignedToSigned16(getMachine().getVariable(varnum));
  }

  /**
   * A method to set a signed 16 Bit integer to the specified variable.
   * @param varnum the variable number
   * @param value the signed value
   */
  protected void setSignedVarValue(char varnum, short value) {
    getMachine().setVariable(varnum, MemoryUtil.signedToUnsigned16(value));
  }

  /**
   * Retrieves the value of the specified operand as an unsigned 16 bit
   * integer.
   * @param operandNum the operand number
   * @return the value
   */
  protected char getUnsignedValue(final int operandNum) {
    final Operand operand = operands[operandNum];
    switch (operand.getType()) {
      case VARIABLE:
        return getMachine().getVariable(operand.getValue());
      case SMALL_CONSTANT:
      case LARGE_CONSTANT:
      default:
        return operand.getValue();
    }
  }

  /**
   * Stores the specified value in the result variable.
   * @param value the value to store
   */
  protected void storeUnsignedResult(final char value) {
    getMachine().setVariable(storeVariable, value);
  }

  /**
   * Stores a signed value in the result variable.
   * @param value the value to store
   */
  protected void storeSignedResult(final short value) {
    storeUnsignedResult(MemoryUtil.signedToUnsigned16(value));
  }

  // *********************************************************************
  // ******** Program flow control
  // ***********************************
  /**
   * Advances the program counter to the next instruction.
   */
  protected void nextInstruction() { machine.incrementPC(opcodeLength); }

  /**
   * Performs a branch, depending on the state of the condition flag.
   * If branchIfConditionTrue is true, the branch will be performed if
   * condition is true, if branchIfCondition is false, the branch will
   * be performed if condition is false.
   * @param condition the test condition
   */
  protected void branchOnTest(final boolean condition) {
    final boolean test = branchInfo.branchOnTrue ? condition : !condition;
    //System.out.printf("ApplyBranch, offset: %d, opcodeLength: %d,
    //                  branchIfTrue: %b, test: %b\n",
    //                  branchInfo.branchOffset, opcodeLength,
    //                  branchInfo.branchOnTrue, test);
    if (test) {
      applyBranch();
    } else {
      nextInstruction();
    }
  }

  /**
   * Applies a jump by applying the branch formula on the pc given the specified
   * offset.
   */
  private void applyBranch() {
    machine.doBranch(branchInfo.branchOffset, opcodeLength);
  }

  /**
   * This function returns from the current routine, setting the return value
   * into the specified return variable.
   * @param returnValue the return value
   */
  protected void returnFromRoutine(final char returnValue) {
    machine.returnWith(returnValue);
  }

  /**
   * Calls in the Z-machine are all very similar and only differ in the
   * number of arguments.
   * @param numArgs the number of arguments
   */
  protected void call(final int numArgs) {
    final char packedAddress = getUnsignedValue(0);
    final char[] args = new char[numArgs];
    for (int i = 0; i < numArgs; i++) {
      args[i] = getUnsignedValue(i + 1);
    }
    call(packedAddress, args);
  }

  /**
   * Perform a call to a packed address.
   * @param packedRoutineAddress routine address
   * @param args arguments
   */
  protected void call(final char packedRoutineAddress, final char[] args) {
    if (packedRoutineAddress == 0) {
      if (storesResult()) {
        // only if this instruction stores a result
        storeUnsignedResult(FALSE);
      }
      nextInstruction();
    } else {
      final int returnAddress = getMachine().getPC() + opcodeLength;
      final char returnVariable = storesResult() ? storeVariable :
        RoutineContext.DISCARD_RESULT;
      machine.call(packedRoutineAddress, returnAddress, args,
               returnVariable);
    }
  }

  /**
   * Halt the virtual machine with an error message about this instruction.
   */
  protected void throwInvalidOpcode() {
    machine.halt("illegal instruction, operand count: " + getOperandCount() +
        " opcode: " + opcodeNum);
  }

  /**
   * Save game state to persistent storage.
   * @param pc current pc
   */
  protected void saveToStorage(final int pc) {
    // This is a little tricky: In version 3, the program counter needs to
    // point to the branch offset, and not to an instruction position
    // In version 4, this points to the store variable. In both cases this
    // address is the instruction address + 1
    final boolean success = getMachine().save(pc);

    if (machine.getVersion() <= 3) {
      //int target = getMachine().getProgramCounter() + getLength();
      //target--; // point to the previous branch offset
      //boolean success = getMachine().save(target);
      branchOnTest(success);
    } else {
      // changed behaviour in version >= 4
      storeUnsignedResult(success ? TRUE : FALSE);
      nextInstruction();
    }
  }

  /**
   * Restore game from persistent storage.
   */
  protected void restoreFromStorage() {
    final PortableGameState gamestate = getMachine().restore();
    if (machine.getVersion() <= 3) {
      if (gamestate == null) {
        // If failure on restore, just continue
        nextInstruction();
      }
    } else {
      // changed behaviour in version >= 4
      if (gamestate == null) {
        storeUnsignedResult(FALSE);
        // If failure on restore, just continue
        nextInstruction();
      } else {
        final char storevar = gamestate.getStoreVariable(getMachine());
        getMachine().setVariable(storevar, RESTORE_TRUE);
      }
    }
  }

  /**
   * Returns the window for a given window number.
   * @param windownum the window number
   * @return the window
   */
  protected Window6 getWindow(final int windownum) {
    return (windownum == ScreenModel6.CURRENT_WINDOW) ?
            getMachine().getScreen6().getSelectedWindow() :
            getMachine().getScreen6().getWindow(windownum);
  }

  /**
   * Helper function
   * @return true if output, false otherwise
   */
  public boolean isOutput() {
    return InstructionInfoDb.getInstance().getInfo(getOperandCount(), opcodeNum,
        getStoryVersion()).isOutput();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    buffer.append(InstructionInfoDb.getInstance().getInfo(getOperandCount(),
                  opcodeNum, getStoryVersion()).getName());
    buffer.append(" ");
    buffer.append(getOperandString());
    if (storesResult()) {
      buffer.append(" -> ");
      buffer.append(getVarName(storeVariable));
    }
    return buffer.toString();
  }

  /**
   * Returns the string representation of the specified variable.
   * @param varnum variable number
   * @return variable name
   */
  private String getVarName(final int varnum) {
    if (varnum == 0) {
      return "(SP)";
    } else if (varnum <= 15) {
      return String.format("L%02x", (varnum - 1));
    } else {
      return String.format("G%02x", (varnum - 16));
    }
  }

  /**
   * Returns the value of the specified variable.
   * @param varnum the variable number
   * @return value of the specified variable
   */
  private String getVarValue(final char varnum) {
    char value = 0;
    if (varnum == 0) {
      value = machine.getStackTop();
    } else {
      value = machine.getVariable(varnum);
    }
    return String.format("$%04x", (int) value);
  }

  /**
   * Returns the string representation of the operands.
   * @return string representation of operands
   */
  protected String getOperandString() {
    final StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < getNumOperands(); i++) {
      if (i > 0) {
        buffer.append(", ");
      }
      final Operand operand = operands[i];
      switch (operand.getType()) {
        case SMALL_CONSTANT:
          buffer.append(String.format("$%02x", (int) operand.getValue()));
          break;
        case LARGE_CONSTANT:
          buffer.append(String.format("$%04x", (int) operand.getValue()));
          break;
        case VARIABLE:
          buffer.append(getVarName(operand.getValue()));
          buffer.append("[");
          buffer.append(getVarValue(operand.getValue()));
          buffer.append("]");
        default:
          break;
      }
    }
    return buffer.toString();
  }
}
