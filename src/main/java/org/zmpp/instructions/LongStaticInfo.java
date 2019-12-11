/*
 * $Id: LongStaticInfo.java,v 1.6 2006/04/12 02:04:30 weiju Exp $
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


public class LongStaticInfo implements InstructionStaticInfo {

  private static final int[][] VALID_VERSIONS = {
    
    { },
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // JE
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // JL
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // JG
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // DEC_CHK
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // INC_CHK
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // JIN
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // TEST
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // OR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // AND
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // TEST_ATTR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // SET_ATTR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // CLEAR_ATTR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // STORE
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // INSERT_OBJ
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // LOADW
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // LOADB
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_PROP
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_PROP_ADDR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // GET_NEXT_PROP
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // ADD
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // SUB
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // MUL
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // DIV
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // MOD
    { 4, 5, 6, 7, 8 }, // CALL_2S
    { 5, 6, 7, 8 }, // CALL_2N
    { 5, 6, 7, 8 }, // SET_COLOUR
    { 5, 6, 7, 8 }, // THROW
  };
  
  private static final LongStaticInfo instance = new LongStaticInfo();
  
  public static LongStaticInfo getInstance() {
    
    return instance;
  }

  /**
   * List of opcodes. See Z-Machine Standards document 1.0 for
   * explanations.
   */
  public static final int OP_JE                 = 0x01;
  public static final int OP_JL                 = 0x02;
  public static final int OP_JG                 = 0x03;
  public static final int OP_DEC_CHK            = 0x04;
  public static final int OP_INC_CHK            = 0x05;
  public static final int OP_JIN                = 0x06;
  public static final int OP_TEST               = 0x07;
  public static final int OP_OR                 = 0x08;
  public static final int OP_AND                = 0x09;
  public static final int OP_TEST_ATTR          = 0x0a;
  public static final int OP_SET_ATTR           = 0x0b;
  public static final int OP_CLEAR_ATTR         = 0x0c;
  public static final int OP_STORE              = 0x0d;
  public static final int OP_INSERT_OBJ         = 0x0e;
  public static final int OP_LOADW              = 0x0f;
  public static final int OP_LOADB              = 0x10;
  public static final int OP_GET_PROP           = 0x11;
  public static final int OP_GET_PROP_ADDR      = 0x12;
  public static final int OP_GET_NEXT_PROP      = 0x13;
  public static final int OP_ADD                = 0x14;
  public static final int OP_SUB                = 0x15;
  public static final int OP_MUL                = 0x16;
  public static final int OP_DIV                = 0x17;
  public static final int OP_MOD                = 0x18;
  public static final int OP_CALL_2S            = 0x19;
  public static final int OP_CALL_2N            = 0x1a;
  public static final int OP_SET_COLOUR         = 0x1b;
  public static final int OP_THROW              = 0x1c;
  
  public int[] getValidVersions(final int opcode) {
    
    return (opcode < VALID_VERSIONS.length) ? VALID_VERSIONS[opcode] :
                                              new int[0];
  }

  public boolean isBranch(final int opcode, final int version) {
    
    switch (opcode) {
      case LongStaticInfo.OP_JE:
      case LongStaticInfo.OP_JL:
      case LongStaticInfo.OP_JG:
      case LongStaticInfo.OP_DEC_CHK:
      case LongStaticInfo.OP_INC_CHK:
      case LongStaticInfo.OP_JIN:
      case LongStaticInfo.OP_TEST:
      case LongStaticInfo.OP_TEST_ATTR:
        return true;
      default:
        return false;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean storesResult(final int opcode, final int version) {
    
    switch (opcode) {
      case LongStaticInfo.OP_OR:
      case LongStaticInfo.OP_AND:
      case LongStaticInfo.OP_LOADW:
      case LongStaticInfo.OP_LOADB:
      case LongStaticInfo.OP_GET_PROP:
      case LongStaticInfo.OP_GET_PROP_ADDR:
      case LongStaticInfo.OP_GET_NEXT_PROP:
      case LongStaticInfo.OP_ADD:
      case LongStaticInfo.OP_SUB:
      case LongStaticInfo.OP_MUL:
      case LongStaticInfo.OP_DIV:
      case LongStaticInfo.OP_MOD:
      case LongStaticInfo.OP_CALL_2S:
        return true;
      default:
        return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isOutput(final int opcode, final int version) {
    
    return false;
  }
  
  public String getOpName(final int opcode, final int version) {
    
    switch (opcode) {
    
    case LongStaticInfo.OP_ADD: return "ADD";
    case LongStaticInfo.OP_AND: return "AND";
    case LongStaticInfo.OP_CLEAR_ATTR: return "CLEAR_ATTR";
    case LongStaticInfo.OP_DEC_CHK: return "DEC_CHK";
    case LongStaticInfo.OP_DIV: return "DIV";
    case LongStaticInfo.OP_GET_NEXT_PROP: return "GET_NEXT_PROP";
    case LongStaticInfo.OP_GET_PROP: return "GET_PROP";
    case LongStaticInfo.OP_GET_PROP_ADDR: return "GET_PROP_ADDR";
    case LongStaticInfo.OP_INC_CHK: return "INC_CHK";
    case LongStaticInfo.OP_INSERT_OBJ: return "INSERT_OBJ";
    case LongStaticInfo.OP_JE: return "JE";
    case LongStaticInfo.OP_JG: return "JG";
    case LongStaticInfo.OP_JIN: return "JIN";
    case LongStaticInfo.OP_JL: return "JL";
    case LongStaticInfo.OP_LOADB: return "LOADB";
    case LongStaticInfo.OP_LOADW: return "LOADW";
    case LongStaticInfo.OP_MOD: return "MOD";
    case LongStaticInfo.OP_MUL: return "MUL";
    case LongStaticInfo.OP_OR: return "OR";
    case LongStaticInfo.OP_SET_ATTR: return "SET_ATTR";
    case LongStaticInfo.OP_STORE: return "STORE";
    case LongStaticInfo.OP_SUB: return "SUB";
    case LongStaticInfo.OP_TEST: return "TEST";
    case LongStaticInfo.OP_TEST_ATTR: return "TEST_ATTR";
    case LongStaticInfo.OP_CALL_2S: return "CALL_2S";
    case LongStaticInfo.OP_CALL_2N: return "CALL_2N";
    case LongStaticInfo.OP_SET_COLOUR: return "SET_COLOUR";
    default: return "unknown";
    }
  }
}
