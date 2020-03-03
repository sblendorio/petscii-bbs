/*
 * $Id: Cpu.java,v 1.5 2006/05/12 21:59:31 weiju Exp $
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

import java.util.List;

public interface Cpu {

  /**
   * The possible variable types.
   */
  enum VariableType { STACK, LOCAL, GLOBAL }

  /**
   * Resets this object to initial state.
   */
  void reset();

  /**
   * Indicates if the virtual machine is running.
   * 
   * @return true if the machine is running, false, otherwise
   */
  boolean isRunning();
  
  /**
   * Sets the running status.
   * 
   * @param flag the running status
   */
  void setRunning(boolean flag);
  
  /**
   * Halts the machine with the specified error message.
   * 
   * @param errormsg the error message
   */
  void halt(String errormsg);
  
  /**
   * Returns the next instruction.
   * 
   * @return the next instruction
   */
  Instruction nextStep();
  
  /**
   * Translates a packed address into a byte address.
   * 
   * @param packedAddress the packed address
   * @param isCall if true then this is a call address, if false, this is
   * a string address
   * @return the translated byte address
   */
  int translatePackedAddress(int packedAddress, boolean isCall);
  
  /**
   * Computes a branch target from an offset.
   * 
   * @return the resulting branch target
   */
  int computeBranchTarget(short offset, int instructionLength);
  
  /**
   * Returns the current program counter.
   * 
   * @return the current program counter
   */
  int getProgramCounter();
  
  /**
   * Sets the program counter to a new address.
   * 
   * @param address the new address
   */
  void setProgramCounter(int address);
  
  /**
   * Increments the program counter by the specified offset.
   * 
   * @param offset the offset
   */
  void incrementProgramCounter(int offset);

  // ********************************************************************
  // ***** Stack operations
  // ***************************************
  /**
   * Returns the global stack pointer. Equals the stack size.
   * 
   * @return the stack pointer
   */
  int getStackPointer();
  
  /**
   * Returns the value at the top of the stack without removing it.
   * 
   * @return the stack top element
   */
  short getStackTopElement();
  
  /**
   * Sets the value of the element at the top of the stack without
   * incrementing the stack pointer.
   * 
   * @param value the value to set
   */
  void setStackTopElement(short value);
  
  /**
   * Returns the evaluation stack element at the specified index.
   * 
   * @param index an index
   * @return the stack value at the specified index
   */
  short getStackElement(int index);
  
  /**
   * Pushes the specified value on the user stack.
   * 
   * @param userstackAddress the address of the user stack
   * @param value the value to push
   * @return true if operation was ok, false if overflow
   */
  boolean pushUserStack(int userstackAddress, short value);
  
  /**
   * Pops the specified value from the user stack.
   * 
   * @param userstackAddress the address of the user stack
   * @return the popped value
   */
  short popUserStack(int userstackAddress);
  
  // ********************************************************************
  // ***** Variable access
  // ***************************************
  /**
   * Returns the value of the specified variable. 0 is the stack pointer,
   * 0x01-0x0f are local variables, and 0x10-0xff are global variables.
   * If the stack pointer is read from, its top value will be popped off.
   * 
   * @param variableNumber the variable number
   * @return the value of the variable
   * @throws IllegalStateException if a local variable is accessed without
   * a subroutine context or if a non-existent local variable is accessed
   */
  short getVariable(int variableNumber);
  
  /**
   * Sets the value of the specified variable. If the stack pointer is written
   * to, the stack will contain one more value.
   * 
   * @param variableNumber the variable number
   * @param value the value to write
   * @throws IllegalStateException if a local variable is accessed without
   * a subroutine context or if a non-existent local variable is accessed
   */
  void setVariable(int variableNumber, short value);

  // ********************************************************************
  // ***** Routine stack frames
  // ***************************************
  /**
   * Pushes a new routine context onto the routine context stack.
   * 
   * @param routineContext the routine context object
   */
  void pushRoutineContext(RoutineContext routineContext);
  
  /**
   * Pops the current routine context from the stack. It will also
   * restore the state before the invocation of the routine, i.e. it
   * will restore the program counter and the stack pointers and set
   * the specfied return value to the return variable.
   * 
   * @param returnValue the return value
   * @throws IllegalStateException if no RoutineContext exists
   */
  void popRoutineContext(short returnValue);
  
  /**
   * Returns the state of the current routine context stack as a non-
   * modifiable List. This is exposed to PortableGameState to take a
   * machine state snapshot.
   * 
   * @return the list of routine contexts
   */
  List<RoutineContext> getRoutineContexts();
  
  /**
   * Copies the list of routine contexts into this machine's routine context
   * stack. This is a consequence of a restore operation.
   * 
   * @param contexts a list of routine contexts
   */
  void setRoutineContexts(List<RoutineContext> contexts);
  
  /**
   * Returns the current routine context without affecting the state
   * of the machine.
   * 
   * @return the current routine context
   */
  RoutineContext getCurrentRoutineContext();
  
  /**
   * Performs a routine call.
   * 
   * @param routineAddress the packed routine address
   * @param returnAddress the return address
   * @param args the argument list
   * @param returnVariable the return variable or DISCARD_RESULT
   * @return the routine context created
   */
  RoutineContext call(int routineAddress, int returnAddress, short[] args, short returnVariable);

  // ***********************************************************************
  // **** Interrupt routines
  // ********************************
  
  /**
   * Indicates if the last interrupt routine performed any output.
   * 
   * @return true if the routine performed output, false otherwise
   */
  boolean interruptDidOutput();  
  
  /**
   * Calls the specified interrupt routine.
   * 
   * @param routineAddress the routine address
   * @return the return value
   */
  short callInterrupt(int routineAddress);
}
