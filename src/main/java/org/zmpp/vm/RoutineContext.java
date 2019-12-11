/*
 * $Id: RoutineContext.java,v 1.10 2006/04/12 02:04:30 weiju Exp $
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
package org.zmpp.vm;

/**
 * This class holds information about a subroutine.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class RoutineContext {

  /**
   * Set as return variable value if the call is a call_nx.
   */
  public static final int DISCARD_RESULT = -1;
  
  /**
   * The start address of the routine's code.
   */
  private int startAddress;
  
  /**
   * The local variables.
   */
  private short[] locals;
  
  /**
   * The return address.
   */
  private int returnAddress;
  
  /**
   * The return variable number to store the return value to.
   */
  private int returnVarNum;
  
  /**
   * The stack pointer at invocation time.
   */
  private int invocationStackPointer;
  
  /**
   * The number of arguments.
   */
  private int numArgs;
  
  /**
   * The return value.
   */
  private short returnValue;
  
  /**
   * Constructor.
   * 
   * @param startAddress the routine's start address
   * @param numLocalVariables the number of local variables
   */
  public RoutineContext(int startAddress, int numLocalVariables) {
    
    this.startAddress = startAddress;
    locals = new short[numLocalVariables];
  }
  
  /**
   * Sets the number of arguments.
   * 
   * @param numArgs the number of arguments
   */
  public void setNumArguments(final int numArgs) {
    
    this.numArgs = numArgs;
  }
  
  /**
   * Returns the number of arguments.
   * 
   * @return the number of arguments
   */
  public int getNumArguments() {
    
    return numArgs;
  }
  
  /**
   * Returns the number of local variables.
   * 
   * @return the number of local variables
   */
  public int getNumLocalVariables() {
    
    return (locals == null) ? 0 : locals.length;
  }
  
  /**
   * Sets a value to the specified local variable.
   *  
   * @param localNum the local variable number, starting with 0
   * @param value the value
   */
  public void setLocalVariable(final int localNum, final short value) {
    
    locals[localNum] = value;
  }

  /**
   * Retrieves the value of the specified local variable.
   * 
   * @param localNum the local variable number, starting at 0
   * @return the value of the specified variable
   */
  public short getLocalVariable(final int localNum) {
    
    return locals[localNum];
  }
  
  /**
   * Returns this routine's start address.
   * 
   * @return the start address
   */
  public int getStartAddress() {
    
    return startAddress;
  }
  
  /**
   * Returns the routine's return address.
   * 
   * @return the routine's return address
   */
  public int getReturnAddress() {
    
    return returnAddress;
  }
  
  /**
   * Sets the return address.
   * 
   * @param address the return address
   */
  public void setReturnAddress(final int address) {
    
    this.returnAddress = address;
  }
  
  /**
   * Returns the routine's return variable number.
   * 
   * @return the return variable number or DISCARD_RESULT
   */
  public int getReturnVariable() {
    
    return returnVarNum;
  }
  
  /**
   * Sets the routine's return variable number.
   * 
   * @param varnum the return variable number or DISCARD_RESULT
   */
  public void setReturnVariable(final int varnum) {
    
    returnVarNum = varnum;
  }
  
  /**
   * Returns the stack pointer at invocation time.
   * 
   * @return the stack pointer at invocation time
   */
  public int getInvocationStackPointer() {
    
    return invocationStackPointer;
  }
  
  /**
   * Sets the stack pointer at invocation time.
   * 
   * @param stackpointer the stack pointer at invocation time.
   */
  public void setInvocationStackPointer(final int stackpointer) {
    
    invocationStackPointer = stackpointer;
  }
  
  /**
   * Returns the return value.
   * 
   * @return the return value
   */
  public short getReturnValue() {
    
    return returnValue;
  }
  
  /**
   * Sets the return value.
   * 
   * @param value the return value
   */
  public void setReturnValue(final short value) {
    
    returnValue = value;
  }
}
