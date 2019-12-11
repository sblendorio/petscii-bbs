/*
 * $Id: Short1StaticInfo.java,v 1.4 2006/04/12 02:04:30 weiju Exp $
 * 
 * Created on 2005/12/19
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


public class Short1StaticInfo implements InstructionStaticInfo {

  private static final int[][] VALID_VERSIONS = {
    
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // JZ
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_SIBLING
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_CHILD
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_PARENT
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_PROP_LEN
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // INC
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // DEC
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PRINT_ADDR
    { 4, 5, 6, 7, 8 },          // CALL_1S
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // REMOVE_OBJ
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PRINT_OBJ
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // RET
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // JUMP
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PRINT_PADDR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // LOAD
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // NOT/CALL_1N
  };
  
  private static final Short1StaticInfo instance = new Short1StaticInfo();
  
  public static Short1StaticInfo getInstance() {
    
    return instance;
  }

  /**
   * List of opcodes. See Z-Machine Standards document 1.0 for
   * explanations.
   */
  public static final int OP_JZ                 = 0x00;

  public static final int OP_GET_SIBLING        = 0x01;

  public static final int OP_GET_CHILD          = 0x02;

  public static final int OP_GET_PARENT         = 0x03;

  public static final int OP_GET_PROP_LEN       = 0x04;

  public static final int OP_INC                = 0x05;

  public static final int OP_DEC                = 0x06;

  public static final int OP_PRINT_ADDR         = 0x07;

  public static final int OP_CALL_1S            = 0x08;

  public static final int OP_REMOVE_OBJ         = 0x09;

  public static final int OP_PRINT_OBJ          = 0x0a;

  public static final int OP_RET                = 0x0b;

  public static final int OP_JUMP               = 0x0c;

  public static final int OP_PRINT_PADDR        = 0x0d;

  public static final int OP_LOAD               = 0x0e;

  public static final int OP_NOT                = 0x0f; // Versions 1-4

  public static final int OP_CALL_1N            = 0x0f; // Versions >= 5

  public int[] getValidVersions(final int opcode) {

    return (opcode < VALID_VERSIONS.length) ? VALID_VERSIONS[opcode] :
      new int[0];
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean storesResult(final int opcode, final int version) {
    
    if (version >= 5
        && opcode == Short1StaticInfo.OP_CALL_1N) {
    
      return false;
    }
    
    switch (opcode) {
    
      case Short1StaticInfo.OP_GET_SIBLING:
      case Short1StaticInfo.OP_GET_CHILD:
      case Short1StaticInfo.OP_GET_PARENT:
      case Short1StaticInfo.OP_GET_PROP_LEN:
      case Short1StaticInfo.OP_LOAD:
      case Short1StaticInfo.OP_NOT:
      case Short1StaticInfo.OP_CALL_1S:
        return true;
      default:
        return false;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isBranch(final int opcode, final int version) {
    
    switch (opcode) {
      case Short1StaticInfo.OP_JZ:
      case Short1StaticInfo.OP_GET_SIBLING:
      case Short1StaticInfo.OP_GET_CHILD:
        return true;
      default:
        return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isOutput(final int opcode, final int version) {
    
    return opcode == OP_PRINT_ADDR || opcode == OP_PRINT_PADDR;
  }
  
  public String getOpName(final int opcode, final int version) {

    switch (opcode) {
    
    case Short1StaticInfo.OP_DEC: return "DEC";
    case Short1StaticInfo.OP_GET_CHILD: return "GET_CHILD";
    case Short1StaticInfo.OP_GET_PARENT: return "GET_PARENT";
    case Short1StaticInfo.OP_GET_PROP_LEN: return "GET_PROP_LEN";
    case Short1StaticInfo.OP_GET_SIBLING: return "GET_SIBLING";
    case Short1StaticInfo.OP_INC: return "INC";
    case Short1StaticInfo.OP_JUMP: return "JUMP";
    case Short1StaticInfo.OP_JZ: return "JZ";
    case Short1StaticInfo.OP_LOAD: return "LOAD";
    case Short1StaticInfo.OP_NOT:
      if (version <= 4) {

        return "NOT";
        
      } else {
        
        return "CALL_1N";
      }
    case Short1StaticInfo.OP_PRINT_ADDR: return "PRINT_ADDR";
    case Short1StaticInfo.OP_PRINT_OBJ: return "PRINT_OBJ";
    case Short1StaticInfo.OP_PRINT_PADDR: return "PRINT_PADDR";
    case Short1StaticInfo.OP_REMOVE_OBJ: return "REMOVE_OBJ";
    case Short1StaticInfo.OP_RET: return "RET";
    case Short1StaticInfo.OP_CALL_1S: return "CALL_1S";
    default: return "unknown";
    }
  }
}
