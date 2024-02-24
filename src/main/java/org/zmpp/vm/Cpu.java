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

import java.util.List;

/**
 * Cpu interface.
 * @author Wei-ju Wu
 * @version 1.5
 */
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
   * Translates a packed string address into a byte address.
   * @param packedAddress the packed address
   * @return the translated byte address
   */
  int unpackStringAddress(char packedAddress);

  /**
   * Computes a branch target from an offset.
   * @param offset the offset
   * @param instructionLength the instruction length
   */
  void doBranch(short offset, int instructionLength);

  /**
   * Returns the current program counter.
   * @return the current program counter
   */
  int getPC();

  /**
   * Sets the program counter to a new address.
   * @param address the new address
   */
  void setPC(int address);

  /**
   * Increments the program counter by the specified offset.
   * @param offset the offset
   */
  void incrementPC(int offset);

  // ********************************************************************
  // ***** Stack operations
  // ***************************************
  /**
   * Returns the global stack pointer. Equals the stack size.
   * @return the stack pointer
   */
  char getSP();

  /**
   * Returns the value at the top of the stack without removing it.
   * @return the stack top element
   */
  char getStackTop();

  /**
   * Sets the value of the element at the top of the stack without
   * incrementing the stack pointer.
   * @param value the value to set
   */
  void setStackTop(char value);

  /**
   * Returns the evaluation stack element at the specified index.
   * @param index an index
   * @return the stack value at the specified index
   */
  char getStackElement(int index);

  /**
   * Pushes the specified value on the user stack.
   * @param userstackAddress the address of the user stack
   * @param value the value to push
   * @return true if operation was ok, false if overflow
   */
  boolean pushStack(char userstackAddress, char value);

  /**
   * Pops the specified value from the user stack.
   * @param userstackAddress the address of the user stack
   * @return the popped value
   */
  char popStack(char userstackAddress);

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
  char getVariable(char variableNumber);

  /**
   * Sets the value of the specified variable. If the stack pointer is written
   * to, the stack will contain one more value.
   *
   * @param variableNumber the variable number
   * @param value the value to write
   * @throws IllegalStateException if a local variable is accessed without
   * a subroutine context or if a non-existent local variable is accessed
   */
  void setVariable(char variableNumber, char value);

  // ********************************************************************
  // ***** Routine stack frames
  // ***************************************
  /**
   * Pops the current routine context from the stack. It will also
   * restore the state before the invocation of the routine, i.e. it
   * will restore the program counter and the stack pointers and set
   * the specfied return value to the return variable.
   *
   * @param returnValue the return value
   * @throws IllegalStateException if no RoutineContext exists
   */
  void returnWith(char returnValue);

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
  RoutineContext call(char routineAddress, int returnAddress, char[] args,
                      char returnVariable);
}
