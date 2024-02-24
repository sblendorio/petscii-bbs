/*
 * Created on 2006/05/10
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
package org.zmpp.vmutil;

/**
 * This class implements a faster version of the Z-machin main stack.
 * This combines abstract access with the bypassing of unnecessary
 * object creation.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public final class FastShortStack {

  private char[] values;
  private char stackpointer;

  /**
   * Constructor.
   * @param size the stack size
   */
  public FastShortStack(final int size) {
    values = new char[size];
    stackpointer = 0;
  }

  /**
   * Returns the current stack pointer.
   * @return the stack pointer
   */
  public char getStackPointer() { return stackpointer; }

  /**
   * Pushes a value on the stack and increases the stack pointer.
   * @param value the value
   */
  public void push(final char value) { values[stackpointer++] = value; }

  /**
   * Returns the top value of the stack without modifying the stack pointer.
   * @return the top value
   */
  public char top() { return values[stackpointer - 1]; }

  /**
   * Replaces the top element with the specified value.
   * @param value the value to replace
   */
  public void replaceTopElement(final char value) {
    values[stackpointer - 1] = value;
  }

  /**
   * Returns the size of the stack. Is equal to stack pointer, but has a
   * different semantic meaning.
   * @return the size of the stack
   */
  public int size() { return stackpointer; }

  /**
   * Returns the top value of the stack and decreases the stack pointer.
   * @return the top value
   */
  public char pop() {
    return values[--stackpointer];
  }

  /**
   * Returns the value at index of the stack, here stack is treated as an array.
   * @param index the index
   * @return the value at the index
   */
  public char getValueAt(int index) { return values[index]; }
}
