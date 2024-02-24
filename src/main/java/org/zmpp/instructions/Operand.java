/*
 * Created on 09/24/2005
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

/**
 * This is the definition of an instruction's operand. Each operand has
 * an operand type, and a value which is to be interpreted according to
 * the type.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class Operand {

  /** Type number for a large constant. */
  public static final int TYPENUM_LARGE_CONSTANT = 0x00;

  /** Type number for a small constant. */
  public static final int TYPENUM_SMALL_CONSTANT = 0x01;

  /** Type number for a variable. */
  public static final int TYPENUM_VARIABLE       = 0x02;

  /** Type number for omitted. */
  public static final int TYPENUM_OMITTED        = 0x03;

  /**
   * The available operand types.
   */
  public enum OperandType { SMALL_CONSTANT, LARGE_CONSTANT, VARIABLE, OMITTED }

  /**
   * This operand's type.
   */
  private OperandType type;

  /**
   * This operand's value.
   */
  private char value;

  /**
   * Constructor.
   * @param typenum the type number, must be < 4
   * @param value the operand value
   */
  public Operand(int typenum, char value) {
    type = getOperandType(typenum);
    this.value = value;
  }

  /**
   * Determines the operand type from a two-bit value.
   * @param typenum the type number
   * @return the operand type
   */
  private static OperandType getOperandType(final int typenum) {
    switch (typenum) {
    case 0x00:
      return OperandType.LARGE_CONSTANT;
    case 0x01:
      return OperandType.SMALL_CONSTANT;
    case 0x02:
      return OperandType.VARIABLE;
    default:
      return OperandType.OMITTED; // In fact, such a value should never exist..
    }
  }

  /**
   * Returns this operand's type.
   * @return the operand type
   */
  public OperandType getType() { return type; }

  /**
   * The operand value.
   * @return the value
   */
  public char getValue() { return value; }
}
