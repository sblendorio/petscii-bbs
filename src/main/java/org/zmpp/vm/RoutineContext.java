/*
 * Created on 10/03/2005
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

/**
 * This class holds information about a subroutine.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class RoutineContext {

  /**
   * Set as return variable value if the call is a call_nx.
   */
  public static final char DISCARD_RESULT = 0xffff;

  /** The local variables */
  private char[] locals;

  /** The return address. */
  private int returnAddress;

  /** The return variable number to store the return value to. */
  private char returnVarNum;

  /** The stack pointer at invocation time. */
  private char invocationStackPointer;

  /** The number of arguments. */
  private int numArgs;

  /** The return value. */
  private char returnValue;

  /**
   * Constructor.
   * @param numLocalVariables the number of local variables
   */
  public RoutineContext(int numLocalVariables) {
    locals = new char[numLocalVariables];
  }

  /**
   * Sets the number of arguments.
   * @param aNumArgs the number of arguments
   */
  public void setNumArguments(final int aNumArgs) {
    this.numArgs = aNumArgs;
  }

  /**
   * Returns the number of arguments.
   * @return the number of arguments
   */
  public int getNumArguments() { return numArgs; }

  /**
   * Returns the number of local variables.
   * @return the number of local variables
   */
  public int getNumLocalVariables() {
    return (locals == null) ? 0 : locals.length;
  }

  /**
   * Sets a value to the specified local variable.
   * @param localNum the local variable number, starting with 0
   * @param value the value
   */
  public void setLocalVariable(final char localNum, final char value) {
    locals[localNum] = value;
  }

  /**
   * Retrieves the value of the specified local variable.
   * @param localNum the local variable number, starting at 0
   * @return the value of the specified variable
   */
  public char getLocalVariable(final char localNum) {
    return locals[localNum];
  }

  /**
   * Returns the routine's return address.
   * @return the routine's return address
   */
  public int getReturnAddress() { return returnAddress; }

  /**
   * Sets the return address.
   * @param address the return address
   */
  public void setReturnAddress(final int address) {
    this.returnAddress = address;
  }

  /**
   * Returns the routine's return variable number.
   * @return the return variable number or DISCARD_RESULT
   */
  public char getReturnVariable() { return returnVarNum; }

  /**
   * Sets the routine's return variable number.
   * @param varnum the return variable number or DISCARD_RESULT
   */
  public void setReturnVariable(final char varnum) { returnVarNum = varnum; }

  /**
   * Returns the stack pointer at invocation time.
   * @return the stack pointer at invocation time
   */
  public char getInvocationStackPointer() { return invocationStackPointer; }

  /**
   * Sets the stack pointer at invocation time.
   * @param stackpointer the stack pointer at invocation time.
   */
  public void setInvocationStackPointer(final char stackpointer) {
    invocationStackPointer = stackpointer;
  }

  /**
   * Returns the return value.
   * @return the return value
   */
  public char getReturnValue() { return returnValue; }

  /**
   * Sets the return value.
   * @param value the return value
   */
  public void setReturnValue(final char value) { returnValue = value; }
}
