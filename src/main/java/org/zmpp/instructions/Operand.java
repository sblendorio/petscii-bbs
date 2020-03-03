/*
 * $Id: Operand.java,v 1.3 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 09/24/2005
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

/**
 * This is the definition of an instruction's operand. Each operand has
 * an operand type, and a value which is to be interpreted according to
 * the type.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class Operand {

  /**
   * Type number for a large constant.
   */
  public static final byte TYPENUM_LARGE_CONSTANT = 0x00;
  
  /**
   * Type number for a small constant.
   */
  public static final byte TYPENUM_SMALL_CONSTANT = 0x01;
  
  /**
   * Type number for a variable.
   */
  public static final byte TYPENUM_VARIABLE       = 0x02;
  
  /**
   * Type number for omitted.
   */
  public static final byte TYPENUM_OMITTED        = 0x03;
  
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
  private short value;

  /**
   * Constructor.
   * 
   * @param typenum the type number, must be < 4
   * @param value the operand value
   */
  public Operand(int typenum, short value) {
    
    type = getOperandType(typenum);
    this.value = value;
  }

  /**
   * Determines the operand type from a two-bit value.
   * 
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
   * 
   * @return the operand type
   */
  public OperandType getType() { return type; }
  
  /**
   * The operand value.
   * 
   * @return the value
   */
  public short getValue() { return value; }
}
