/*
 * $Id: CpuImpl.java,v 1.12 2006/05/30 17:22:53 weiju Exp $
 * 
 * Created on 2006/02/14
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zmpp.base.Interruptable;
import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.ZsciiString;
import org.zmpp.vmutil.FastShortStack;

public class CpuImpl implements Cpu, Interruptable {

  /**
   * The stack size is now 64 K.
   */
  private static final int STACKSIZE = 32768;
  
  /**
   * The machine object.
   */
  private Machine machine;

  /**
   * This machine's current program counter.
   */
  private int programCounter;
  
  /**
   * This machine's global stack.
   */
  private FastShortStack stack;
  
  /**
   * The routine info.
   */
  private List<RoutineContext> routineContextStack;
  
  /**
   * The start of global variables.
   */
  private int globalsAddress;
  
  /**
   * The instruction decoder.
   */
  private InstructionDecoder decoder;  
  
  /**
   * This flag indicates the run status.
   */
  private boolean running;  
  
  public CpuImpl(final Machine machine, final InstructionDecoder decoder) {
  
    super();
    this.machine = machine;
    this.decoder = decoder;
    this.running = true;
  }
  
  public void reset() {

    final GameData gamedata = machine.getGameData();
    decoder.initialize(machine, gamedata.getMemoryAccess());
    stack = new FastShortStack(STACKSIZE);
    routineContextStack = new ArrayList<>();
    globalsAddress = gamedata.getStoryFileHeader().getGlobalsAddress();
    
    if (gamedata.getStoryFileHeader().getVersion() == 6) {
      
      // Call main function in version 6
      call(gamedata.getStoryFileHeader().getProgramStart(), 0, new short[0],
           (short) 0);
      
    } else {
      
      programCounter = gamedata.getStoryFileHeader().getProgramStart();
    }
  }
 
  /**
   * {@inheritDoc}
   */
  public int getProgramCounter() {
    
    return programCounter;
  }

  /**
   * {@inheritDoc}
   */
  public void setProgramCounter(final int address) {

    programCounter = address;
  }
  
  public void incrementProgramCounter(final int offset) {
    
    programCounter += offset;
  }

  /**
   * {@inheritDoc}
   */
  public Instruction nextStep() {
    
    return decoder.decodeInstruction(getProgramCounter());
  }
    
  /**
   * {@inheritDoc}
   */
  public int translatePackedAddress(final int packedAddress,
      final boolean isCall) {
  
    // Version specific packed address translation
    final GameData gamedata = machine.getGameData();
    
    switch (gamedata.getStoryFileHeader().getVersion()) {
    
      case 1: case 2: case 3:  
        return packedAddress * 2;
      case 4:
      case 5:
        return packedAddress * 4;
      case 6:
      case 7:
        return packedAddress * 4 + 8 *
          (isCall ? gamedata.getStoryFileHeader().getRoutineOffset() :
                    gamedata.getStoryFileHeader().getStaticStringOffset());
      case 8:
      default:
        return packedAddress * 8;
    }
  }
  
  /**
   * {@inheritDoc} 
   */
  public int computeBranchTarget(final short offset,
      final int instructionLength) {
        
    return getProgramCounter() + instructionLength + offset - 2;
  }
  
  /**
   * {@inheritDoc}
   */
  public void halt(final String errormsg) {
  
    machine.getOutput().print(new ZsciiString(errormsg));
    running = false;
  }  

  /**
   * {@inheritDoc}
   */
  public boolean isRunning() {
    
    return running;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setRunning(final boolean flag) {
    
    running = flag;
  }
  
  // ********************************************************************
  // ***** Stack operations
  // ***************************************
  /**
   * {@inheritDoc}
   */
  public int getStackPointer() {
    
    return stack.getStackPointer();
  }
  
  /**
   * Sets the global stack pointer to the specified value. This might pop off
   * several values from the stack.
   * 
   * @param stackpointer the new stack pointer value
   */
  private void setStackPointer(final int stackpointer) {

    // remove the last diff elements
    final int diff = stack.getStackPointer() - stackpointer;
    for (int i = 0; i < diff; i++) {
     
      stack.pop();
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public short getStackTopElement() {
    
    if (stack.size() > 0) {
      
      return stack.top();
    }
    return -1;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setStackTopElement(final short value) {
    
    stack.replaceTopElement(value);
  }
  
  /**
   * {@inheritDoc}
   */
  public short getStackElement(final int index) {
    
    return stack.getValueAt(index);
  }
  
  /**
   * {@inheritDoc}
   */
  public short popUserStack(int userstackAddress) {

    MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
    int numFreeSlots = memaccess.readUnsignedShort(userstackAddress);
    numFreeSlots++;
    memaccess.writeUnsignedShort(userstackAddress, numFreeSlots);
    return memaccess.readShort(userstackAddress + (numFreeSlots * 2));
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean pushUserStack(int userstackAddress, short value) {
    
    MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
    int numFreeSlots = memaccess.readUnsignedShort(userstackAddress);
    if (numFreeSlots > 0) {
      
      memaccess.writeShort(userstackAddress + (numFreeSlots * 2), value);
      memaccess.writeUnsignedShort(userstackAddress, numFreeSlots - 1);
      return true;
    }
    return false;
  }
  
  /**
   * {@inheritDoc}
   */
  public short getVariable(final int variableNumber) {

    final VariableType varType = getVariableType(variableNumber);
    if (varType == VariableType.STACK) {
      
      if (stack.size() == getInvocationStackPointer()) {
        
        //throw new IllegalStateException("stack underflow error");
        System.err.println("stack underflow error");
        return 0;
        
      } else {
   
        return stack.pop();
      }
      
    } else if (varType == VariableType.LOCAL) {
      
      final int localVarNumber = getLocalVariableNumber(variableNumber);
      checkLocalVariableAccess(localVarNumber);
      return getCurrentRoutineContext().getLocalVariable(localVarNumber);
      
    } else { // GLOBAL
      
      return machine.getGameData().getMemoryAccess().readShort(globalsAddress
          + (getGlobalVariableNumber(variableNumber) * 2));
    }
  }
  
  /**
   * Returns the current invocation stack pointer.
   * 
   * @return the invocation stack pointer
   */
  private int getInvocationStackPointer() {
    
    return getCurrentRoutineContext() == null ? 0 : 
      getCurrentRoutineContext().getInvocationStackPointer();
  }

  /**
   * {@inheritDoc}
   */
  public void setVariable(final int variableNumber, final short value) {

    final VariableType varType = getVariableType(variableNumber);
    if (varType == VariableType.STACK) {
      
      stack.push(value);
      
    } else if (varType == VariableType.LOCAL) {
      
      final int localVarNumber = getLocalVariableNumber(variableNumber);
      checkLocalVariableAccess(localVarNumber);
      getCurrentRoutineContext().setLocalVariable(localVarNumber, value);
      
    } else {
      
      machine.getGameData().getMemoryAccess().writeShort(globalsAddress
          + (getGlobalVariableNumber(variableNumber) * 2), value);
    }
  }
  
  /**
   * Returns the variable type for the given variable number.
   * 
   * @param variableNumber the variable number
   * @return STACK if stack variable, LOCAL if local variable, GLOBAL if global
   */
  public static VariableType getVariableType(final int variableNumber) {
    
    if (variableNumber == 0) {
      
      return VariableType.STACK;
      
    } else if (variableNumber < 0x10) {
      
      return VariableType.LOCAL;
      
    } else {
      
      return VariableType.GLOBAL;
    }
  }


  /**
   * {@inheritDoc}
   */
  public void pushRoutineContext(final RoutineContext routineContext) {

    routineContext.setInvocationStackPointer(getStackPointer());
    routineContextStack.add(routineContext);
  }
  
  /**
   * {@inheritDoc}
   */
  public void popRoutineContext(final short returnValue) {
    
    if (routineContextStack.size() > 0) {

      final RoutineContext popped =
        routineContextStack.remove(routineContextStack.size() - 1);
      popped.setReturnValue(returnValue);
    
      // Restore stack pointer and pc
      setStackPointer(popped.getInvocationStackPointer());
      setProgramCounter(popped.getReturnAddress());
      final int returnVariable = popped.getReturnVariable();
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
   * 
   * @return the current routine stack pointer
   */
  public int getRoutineStackPointer() {
    
    return routineContextStack.size();
  }
  
  public RoutineContext call(final int packedRoutineAddress,
      final int returnAddress, final short[] args, final short returnVariable) {
    
    final int routineAddress =
      translatePackedAddress(packedRoutineAddress, true);
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
      
      routineContext.setLocalVariable(i, args[i]);
    }
    
    // save invocation stack pointer
    routineContext.setInvocationStackPointer(getStackPointer());
    
    // Pushes the routine context onto the routine stack
    pushRoutineContext(routineContext);
    
    // Jump to the address
    setProgramCounter(routineContext.getStartAddress());
    return routineContext;
  }

  // ************************************************************************
  // ****** Private functions
  // ************************************************
  
  /**
   * Decodes the routine at the specified address.
   * 
   * @param routineAddress the routine address
   * @return a RoutineContext object
   */
  private RoutineContext decodeRoutine(final int routineAddress) {

    final GameData gamedata = machine.getGameData();
    final MemoryAccess memaccess = gamedata.getMemoryAccess();    
    final int numLocals = memaccess.readUnsignedByte(routineAddress);
    final short[] locals = new short[numLocals];
    int currentAddress = routineAddress + 1;
    
    if (gamedata.getStoryFileHeader().getVersion() <= 4) {
      
      // Only story files <= 4 actually store default values here,
      // after V5 they are assumed as being 0 (standard document 1.0, S.5.2.1) 
      for (int i = 0; i < numLocals; i++) {
      
        locals[i] = memaccess.readShort(currentAddress);
        currentAddress += 2;
      }
    }
    //System.out.printf("setting routine start to: %x\n", currentAddress);
    
    final RoutineContext info = new RoutineContext(currentAddress, numLocals);
    
    for (int i = 0; i < numLocals; i++) {
      
      info.setLocalVariable(i, locals[i]);
    }
    return info;
  }
    
  /**
   * Returns the local variable number for a specified variable number.
   * 
   * @param variableNumber the variable number in an operand (0x01-0x0f)
   * @return the local variable number
   */
  private int getLocalVariableNumber(final int variableNumber) {
    
    return variableNumber - 1;
  }
  
  /**
   * Returns the global variable for the specified variable number.
   * 
   * @param variableNumber a variable number (0x10-0xff)
   * @return the global variable number
   */
  private int getGlobalVariableNumber(final int variableNumber) {
    
    return variableNumber - 0x10;
  }
  
  /**
   * This function throws an exception if a non-existing local variable
   * is accessed on the current routine context or no current routine context
   * is set.
   * 
   * @param localVariableNumber the local variable number
   */
  private void checkLocalVariableAccess(final int localVariableNumber) {
    
    if (routineContextStack.size() == 0) {
      
      throw new IllegalStateException("no routine context set");
    }
    
    if (localVariableNumber >= getCurrentRoutineContext().getNumLocalVariables()) {
      
      throw new IllegalStateException("access to non-existent local variable: "
                                      + localVariableNumber);
    }
  }
  
  // ************************************************************************
  // ****** Interrupt functions
  // *************************************
  
  /**
   * The flag to indicate interrupt output.
   */
  private boolean interruptDidOutput;
  
  /**
   * The flag to indicate interrupt execution.
   */
  private boolean executeInterrupt;
  
  /**
   * {@inheritDoc}
   */
  public boolean interruptDidOutput() {
    
    return interruptDidOutput;
  }
  
  /**
   * {@inheritDoc}
   */
  public short callInterrupt(final int routineAddress) {
    
    interruptDidOutput = false;
    executeInterrupt = true;
    final int originalRoutineStackSize = getRoutineContexts().size();
    final RoutineContext routineContext = call(routineAddress,
        machine.getCpu().getProgramCounter(),
        new short[0], (short) RoutineContext.DISCARD_RESULT);
    
    for (;;) {
      
      final Instruction instr = nextStep();
      instr.execute();
      // check if something was printed
      if (instr.isOutput()) {
        interruptDidOutput = true;
      }
      if (getRoutineContexts().size() == originalRoutineStackSize) {
        
        break;
      }
    }
    executeInterrupt = false;
    return routineContext.getReturnValue();
  }
  
  public void setInterruptRoutine(final int routineAddress) {
    
    // TODO
  }
  
  /**
   * Returns the interrupt status of the cpu object.
   * 
   * @return the interrup status
   */
  public boolean isExecutingInterrupt() { return executeInterrupt; }
}
