/*
 * Created on 2006/02/14
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
package org.zmpp.vm;

import org.zmpp.base.StoryFileHeader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.logging.Logger;
import org.zmpp.vmutil.FastShortStack;
import static org.zmpp.base.MemoryUtil.toUnsigned16;

/**
 * Cpu interface implementation.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class CpuImpl implements Cpu {

  private static final Logger LOG = Logger.getLogger("org.zmpp");

  /** The stack size is now 64 K. */
  private static final char STACKSIZE = 32768;

  /** The machine object. */
  private Machine machine;

  /** This machine's current program counter. */
  private int programCounter;

  /** This machine's global stack. */
  private FastShortStack stack;

  /** The routine info. */
  private List<RoutineContext> routineContextStack;

  /** The start of global variables. */
  private int globalsAddress;

  /**
   * Constructor.
   * @param machine the Machine object
   */
  public CpuImpl(final Machine machine) {
    this.machine = machine;
  }

  /** @{inheritDoc} */
  public void reset() {
    stack = new FastShortStack(STACKSIZE);
    routineContextStack = new ArrayList<RoutineContext>();
    globalsAddress = machine.readUnsigned16(StoryFileHeader.GLOBALS);

    if (machine.getVersion() == 6) {
      // Call main function in version 6
      call(getProgramStart(), (char) 0,
           new char[0], (char) 0);
    } else {
      programCounter = getProgramStart();
    }
  }

  /**
   * Returns the story's start address.
   * @return the start address
   */
  private char getProgramStart() {
    return machine.readUnsigned16(StoryFileHeader.PROGRAM_START);
  }

  /** {@inheritDoc} */
  public int getPC() { return programCounter; }

  /** {@inheritDoc} */
  public void setPC(final int address) { programCounter = address; }

  /**
   * Increments the program counter.
   * @param offset the increment value.
   */
  public void incrementPC(final int offset) { programCounter += offset; }

  /** {@inheritDoc} */
  public int unpackStringAddress(char packedAddress) {
    int version = machine.getVersion();
    return version == 6 || version == 7 ?
      packedAddress * 4 + 8 * getStaticStringOffset()
      : unpackAddress(packedAddress);
  }

  /**
   * Unpacks a routine address, exposed for testing.
   * @param packedAddress the packed address
   * @return the unpacked address
   */
  public int unpackRoutineAddress(char packedAddress) {
    int version = machine.getVersion();
    return version == 6 || version == 7 ?
      packedAddress * 4 + 8 * getRoutineOffset()
      : unpackAddress(packedAddress);
  }

  /**
   * Only for V6 and V7 games: the routine offset.
   * @return the routine offset
   */
  private char getRoutineOffset() {
    return machine.readUnsigned16(StoryFileHeader.ROUTINE_OFFSET);
  }

  /**
   * Only in V6 and V7: the static string offset.
   * @return the static string offset
   */
  private char getStaticStringOffset() {
    return machine.readUnsigned16(StoryFileHeader.STATIC_STRING_OFFSET);
  }

  /**
   * Version specific unpacking.
   * @param packedAddress the packed address
   * @return the unpacked address
   */
  private int unpackAddress(final char packedAddress) {
    switch (machine.getVersion()) {
      case 1: case 2: case 3:
        return packedAddress * 2;
      case 4: case 5:
        return packedAddress * 4;
      case 8:
      default:
        return packedAddress * 8;
    }
  }

  /** {@inheritDoc} */
  public void doBranch(short branchOffset, int instructionLength) {
    if (branchOffset >= 2 || branchOffset < 0) {
      setPC(computeBranchTarget(branchOffset, instructionLength));
    } else {
      // FALSE is defined as 0, TRUE as 1, so simply return the offset
      // since we do not have negative offsets
      returnWith((char) branchOffset);
    }
  }

  /**
   * Computes the branch target.
   * @param offset offset value
   * @param instructionLength instruction length
   * @return branch target value
   */
  private int computeBranchTarget(final short offset,
      final int instructionLength) {
    return getPC() + instructionLength + offset - 2;
  }

  // ********************************************************************
  // ***** Stack operations
  // ***************************************
  /** {@inheritDoc} */
  public char getSP() { return stack.getStackPointer(); }

  /**
   * Sets the global stack pointer to the specified value. This might pop off
   * several values from the stack.
   * @param stackpointer the new stack pointer value
   */
  private void setSP(final char stackpointer) {
    // remove the last diff elements
    final int diff = stack.getStackPointer() - stackpointer;
    for (int i = 0; i < diff; i++) { stack.pop(); }
  }

  /** {@inheritDoc} */
  public char getStackTop() {
    if (stack.size() > 0) { return stack.top(); }
    throw new java.lang.ArrayIndexOutOfBoundsException("Stack underflow error");
  }

  /** {@inheritDoc} */
  public void setStackTop(final char value) {
    stack.replaceTopElement(value);
  }

  /** {@inheritDoc} */
  public char getStackElement(final int index) {
    return stack.getValueAt(index);
  }

  /** {@inheritDoc} */
  public char popStack(char userstackAddress) {
    return userstackAddress == 0 ? getVariable((char) 0) :
      popUserStack(userstackAddress);
  }

  /**
   * Pops the user stack.
   * @param userstackAddress address of user stack
   * @return popped value
   */
  private char popUserStack(char userstackAddress) {
    int numFreeSlots = machine.readUnsigned16(userstackAddress);
    numFreeSlots++;
    machine.writeUnsigned16(userstackAddress, toUnsigned16(numFreeSlots));
    return machine.readUnsigned16(userstackAddress + (numFreeSlots * 2));
  }

  /** {@inheritDoc} */
  public boolean pushStack(char userstackAddress, char value) {
    if (userstackAddress == 0) {
      setVariable((char) 0, value);
      return true;
    } else {
      return pushUserStack(userstackAddress, value);
    }
  }

  /**
   * Push user stack.
   * @param userstackAddress address of user stack
   * @param value value to push
   * @return true if successful, false on overflow
   */
  private boolean pushUserStack(char userstackAddress, char value) {
    int numFreeSlots = machine.readUnsigned16(userstackAddress);
    if (numFreeSlots > 0) {
      machine.writeUnsigned16(userstackAddress + (numFreeSlots * 2), value);
      machine.writeUnsigned16(userstackAddress, toUnsigned16(numFreeSlots - 1));
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  public char getVariable(final char variableNumber) {
    final Cpu.VariableType varType = getVariableType(variableNumber);
    if (varType == Cpu.VariableType.STACK) {
      if (stack.size() == getInvocationStackPointer()) {
        //throw new IllegalStateException("stack underflow error");
        LOG.severe("stack underflow error");
        return 0;
      } else {
        return stack.pop();
      }
    } else if (varType == Cpu.VariableType.LOCAL) {
      final char localVarNumber = getLocalVariableNumber(variableNumber);
      checkLocalVariableAccess(localVarNumber);
      return getCurrentRoutineContext().getLocalVariable(localVarNumber);
    } else { // GLOBAL
      return machine.readUnsigned16(globalsAddress +
          (getGlobalVariableNumber(variableNumber) * 2));
    }
  }

  /**
   * Returns the current invocation stack pointer.
   * @return the invocation stack pointer
   */
  private char getInvocationStackPointer() {
    return (char) (getCurrentRoutineContext() == null ? 0 :
      getCurrentRoutineContext().getInvocationStackPointer());
  }

  /** {@inheritDoc} */
  public void setVariable(final char variableNumber, final char value) {
    final Cpu.VariableType varType = getVariableType(variableNumber);
    if (varType == Cpu.VariableType.STACK) {
      stack.push(value);
    } else if (varType == Cpu.VariableType.LOCAL) {
      final char localVarNumber = getLocalVariableNumber(variableNumber);
      checkLocalVariableAccess(localVarNumber);
      getCurrentRoutineContext().setLocalVariable(localVarNumber, value);
    } else {
      machine.writeUnsigned16(globalsAddress +
          (getGlobalVariableNumber(variableNumber) * 2), value);
    }
  }

  /**
   * Returns the variable type for the given variable number.
   *
   * @param variableNumber the variable number
   * @return STACK if stack variable, LOCAL if local variable, GLOBAL if global
   */
  public static Cpu.VariableType getVariableType(final int variableNumber) {
    if (variableNumber == 0) {
      return Cpu.VariableType.STACK;
    } else if (variableNumber < 0x10) {
      return Cpu.VariableType.LOCAL;
    } else {
      return Cpu.VariableType.GLOBAL;
    }
  }

  /**
   * {@inheritDoc}
   */
  public void pushRoutineContext(final RoutineContext routineContext) {
    routineContext.setInvocationStackPointer(getSP());
    routineContextStack.add(routineContext);
  }

  /**
   * {@inheritDoc}
   */
  public void returnWith(final char returnValue) {
    if (routineContextStack.size() > 0) {
      final RoutineContext popped =
        routineContextStack.remove(routineContextStack.size() - 1);
      popped.setReturnValue(returnValue);

      // Restore stack pointer and pc
      setSP(popped.getInvocationStackPointer());
      setPC(popped.getReturnAddress());
      final char returnVariable = popped.getReturnVariable();
      if (returnVariable != RoutineContext.DISCARD_RESULT) {
        setVariable(returnVariable, returnValue);
      }
    } else {
      throw new IllegalStateException("no routine context active");
    }
  }

  /**
   * {@inheritDoc}
   */
  public RoutineContext getCurrentRoutineContext() {
    if (routineContextStack.size() == 0) {
      return null;
    }
    return routineContextStack.get(routineContextStack.size() - 1);
  }

  /**
   * {@inheritDoc}
   */
  public List<RoutineContext> getRoutineContexts() {
    return Collections.unmodifiableList(routineContextStack);
  }

  /**
   * {@inheritDoc}
   */
  public void setRoutineContexts(final List<RoutineContext> contexts) {
    routineContextStack.clear();
    for (RoutineContext context : contexts) {
      routineContextStack.add(context);
    }
  }

  /**
   * This function is basically exposed to the debug application.
   * @return the current routine stack pointer
   */
  public char getRoutineStackPointer() {
    return (char) routineContextStack.size();
  }

  /** {@inheritDoc} */
  public RoutineContext call(final char packedRoutineAddress,
      final int returnAddress, final char[] args, final char returnVariable) {
    final int routineAddress = unpackRoutineAddress(packedRoutineAddress);
    final int numArgs = args == null ? 0 : args.length;
    final RoutineContext routineContext = decodeRoutine(routineAddress);

    // Sets the number of arguments
    routineContext.setNumArguments(numArgs);

    // Save return parameters
    routineContext.setReturnAddress(returnAddress);

    // Only if this instruction stores a result
    if (returnVariable == RoutineContext.DISCARD_RESULT) {
      routineContext.setReturnVariable(RoutineContext.DISCARD_RESULT);
    } else {
      routineContext.setReturnVariable(returnVariable);
    }

    // Set call parameters into the local variables
    // if there are more parameters than local variables,
    // those are thrown away
    final int numToCopy = Math.min(routineContext.getNumLocalVariables(),
                                   numArgs);

    for (int i = 0; i < numToCopy; i++) {
      routineContext.setLocalVariable((char) i, args[i]);
    }

    // save invocation stack pointer
    routineContext.setInvocationStackPointer(getSP());

    // Pushes the routine context onto the routine stack
    pushRoutineContext(routineContext);

    // Jump to the address
    setPC(machine.getVersion() >= 5 ? routineAddress + 1 :
      routineAddress + 1 + 2 * routineContext.getNumLocalVariables());
    return routineContext;
  }

  // ************************************************************************
  // ****** Private functions
  // ************************************************

  /**
   * Decodes the routine at the specified address.
   * @param routineAddress the routine address
   * @return a RoutineContext object
   */
  private RoutineContext decodeRoutine(final int routineAddress) {
    final int numLocals = machine.readUnsigned8(routineAddress);
    final char[] locals = new char[numLocals];

    if (machine.getVersion() <= 4) {
      // Only story files <= 4 actually store default values here,
      // after V5 they are assumed as being 0 (standard document 1.0, S.5.2.1)
      for (int i = 0; i < numLocals; i++) {
        locals[i] = machine.readUnsigned16(routineAddress + 1 + 2 * i);
      }
    }
    final RoutineContext info = new RoutineContext(numLocals);
    for (int i = 0; i < numLocals; i++) {
      info.setLocalVariable((char) i, locals[i]);
    }
    return info;
  }

  /**
   * Returns the local variable number for a specified variable number.
   * @param variableNumber the variable number in an operand (0x01-0x0f)
   * @return the local variable number
   */
  private char getLocalVariableNumber(final char variableNumber) {
    return (char) (variableNumber - 1);
  }

  /**
   * Returns the global variable for the specified variable number.
   * @param variableNumber a variable number (0x10-0xff)
   * @return the global variable number
   */
  private char getGlobalVariableNumber(final char variableNumber) {
    return (char) (variableNumber - 0x10);
  }

  /**
   * This function throws an exception if a non-existing local variable
   * is accessed on the current routine context or no current routine context
   * is set.
   *
   * @param localVariableNumber the local variable number
   */
  private void checkLocalVariableAccess(final char localVariableNumber) {
    if (routineContextStack.size() == 0) {
      throw new IllegalStateException("no routine context set");
    }

    if (localVariableNumber >= getCurrentRoutineContext()
        .getNumLocalVariables()) {
      throw new IllegalStateException(
          "access to non-existent local variable: " +
          (int) localVariableNumber);
    }
  }
}
