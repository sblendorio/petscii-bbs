/*
 * $Id: VariableStaticInfo.java,v 1.7 2006/05/30 17:23:52 weiju Exp $
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


public class VariableStaticInfo implements InstructionStaticInfo {

  private static final int[][] VALID_VERSIONS = {
    
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // CALL
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // STOREW
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // STOREB
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PUT_PROP
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // SREAD/AREAD
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PRINT_CHAR
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PRINT_NUM
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // RANDOM
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PUSH
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // PULL
    { 3, 4, 5, 6, 7, 8 },       // SPLIT_WINDOW
    { 3, 4, 5, 6, 7, 8 },       // SET_WINDOW
    { 4, 5, 6, 7, 8 },          // CALL_VS2
    { 4, 5, 6, 7, 8 },          // ERASE_WINDOW
    { 4, 5, 6, 7, 8 },          // ERASE_LINE
    { 4, 5, 6, 7, 8 },          // SET_CURSOR
    { 4, 5, 6, 7, 8 },          // GET_CURSOR
    { 4, 5, 6, 7, 8 },          // SET_TEXT_STYLE
    { 4, 5, 6, 7, 8 },          // BUFFER_MODE
    { 3, 4, 5, 6, 7, 8 },       // OUTPUT_STREAM
    { 3, 4, 5, 6, 7, 8 },       // INPUT_STREAM
    { 3, 4, 5, 6, 7, 8 },       // SOUND_EFFECT
    { 4, 5, 6, 7, 8 },          // READ_CHAR
    { 4, 5, 6, 7, 8 },          // SCAN_TABLE
    { 5, 6, 7, 8 },             // NOT
    { 5, 6, 7, 8 },             // CALL_VN
    { 5, 6, 7, 8 },             // CALL_VN2
    { 5, 6, 7, 8 },             // TOKENISE
    { 5, 6, 7, 8 },             // ENCODE_TEXT
    { 5, 6, 7, 8 },             // COPY_TABLE
    { 5, 6, 7, 8 },             // PRINT_TABLE
    { 5, 6, 7, 8 },             // CHECK_ARG_COUNT
  };
  
  private static final VariableStaticInfo instance = new VariableStaticInfo();
  
  public static VariableStaticInfo getInstance() {
    
    return instance;
  }

  /**
   * List of opcodes. See Z-Machine Standards document 1.0 for
   * explanations.
   */
  public static final int OP_CALL               = 0x00; // Versions 1-3
  public static final int OP_STOREW             = 0x01;
  public static final int OP_STOREB             = 0x02;
  public static final int OP_PUT_PROP           = 0x03;
  public static final int OP_SREAD              = 0x04; // Versions 1-4
  public static final int OP_AREAD              = 0x04; // Versions >= 5
  public static final int OP_PRINT_CHAR         = 0x05;
  public static final int OP_PRINT_NUM          = 0x06;
  public static final int OP_RANDOM             = 0x07;
  public static final int OP_PUSH               = 0x08;
  public static final int OP_PULL               = 0x09;
  public static final int OP_SPLIT_WINDOW       = 0x0a;
  public static final int OP_SET_WINDOW         = 0x0b;
  public static final int OP_CALL_VS2           = 0x0c;
  public static final int OP_ERASE_WINDOW       = 0x0d;
  public static final int OP_ERASE_LINE         = 0x0e;
  public static final int OP_SET_CURSOR         = 0x0f;
  public static final int OP_GET_CURSOR         = 0x10;
  public static final int OP_SET_TEXT_STYLE     = 0x11;
  public static final int OP_BUFFER_MODE        = 0x12;
  public static final int OP_OUTPUTSTREAM       = 0x13;
  public static final int OP_INPUTSTREAM        = 0x14;
  public static final int OP_SOUND_EFFECT       = 0x15;
  public static final int OP_READ_CHAR          = 0x16;
  public static final int OP_SCAN_TABLE         = 0x17;
  public static final int OP_NOT                = 0x18; // Versions >= 5
  public static final int OP_CALL_VN            = 0x19; // Versions >= 5
  public static final int OP_CALL_VN2           = 0x1a; // Versions >= 5
  public static final int OP_TOKENISE           = 0x1b; // Versions >= 5
  public static final int OP_ENCODE_TEXT        = 0x1c; // Versions >= 5
  public static final int OP_COPY_TABLE         = 0x1d;
  public static final int OP_PRINT_TABLE        = 0x1e;
  public static final int OP_CHECK_ARG_COUNT    = 0x1f; // Versions >= 5
  
  /**
   * {@inheritDoc}
   */
  public int[] getValidVersions(final int opcode) {
    
    return (opcode < VALID_VERSIONS.length) ? VALID_VERSIONS[opcode] :
                                              new int[0];
  }

  /**
   * {@inheritDoc}
   */
  public boolean storesResult(final int opcode, final int version) {

    if (version >= 5) {
      
      // new codes and meanings
      switch (opcode) {
      
        case VariableStaticInfo.OP_AREAD:
        case VariableStaticInfo.OP_NOT:
          return true;
        default:
          break;
      }
    }
    
    if (version == 6 && opcode == VariableStaticInfo.OP_PULL) {
      
      return true;
    }
    
    switch (opcode) {
    
      case VariableStaticInfo.OP_CALL:
      case VariableStaticInfo.OP_RANDOM:
      case VariableStaticInfo.OP_CALL_VS2:
      case VariableStaticInfo.OP_READ_CHAR:
      case VariableStaticInfo.OP_SCAN_TABLE:
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
    
      case VariableStaticInfo.OP_SCAN_TABLE:
      case VariableStaticInfo.OP_CHECK_ARG_COUNT:
        return true;
      default:
        return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isOutput(final int opcode, final int version) {
    
    switch (opcode) {
    
    case OP_PRINT_CHAR:
    case OP_PRINT_NUM:
    case OP_PRINT_TABLE:
    case OP_ERASE_WINDOW:
    case OP_ERASE_LINE:
      return true;
    default:
      return false;
    }
  }
  
  public String getOpName(final int opcode, final int version) {
    
    switch (opcode) {
    case VariableStaticInfo.OP_CALL: return "CALL";
    case VariableStaticInfo.OP_INPUTSTREAM: return "INPUTSTREAM";
    case VariableStaticInfo.OP_OUTPUTSTREAM: return "OUTPUTSTREAM";
    case VariableStaticInfo.OP_PRINT_CHAR: return "PRINT_CHAR";
    case VariableStaticInfo.OP_PRINT_NUM: return "PRINT_NUM";
    case VariableStaticInfo.OP_PULL: return "PULL";
    case VariableStaticInfo.OP_PUSH: return "PUSH";
    case VariableStaticInfo.OP_PUT_PROP: return "PUT_PROP";
    case VariableStaticInfo.OP_RANDOM: return "RANDOM";
    case VariableStaticInfo.OP_SREAD:
      if (version <= 4) {
        
        return "SREAD";
        
      } else {
        
        return "AREAD";
      }
    case VariableStaticInfo.OP_STOREB: return "STOREB";
    case VariableStaticInfo.OP_STOREW: return "STOREW";
    case VariableStaticInfo.OP_SPLIT_WINDOW: return "SPLIT_WINDOW";
    case VariableStaticInfo.OP_SET_WINDOW: return "SET_WINDOW";
    case VariableStaticInfo.OP_SET_TEXT_STYLE: return "SET_TEXT_STYLE";
    case VariableStaticInfo.OP_BUFFER_MODE: return "BUFFER_MODE";
    case VariableStaticInfo.OP_ERASE_WINDOW: return "ERASE_WINDOW";
    case VariableStaticInfo.OP_ERASE_LINE: return "ERASE_LINE";
    case VariableStaticInfo.OP_SET_CURSOR: return "SET_CURSOR";
    case VariableStaticInfo.OP_GET_CURSOR: return "GET_CURSOR";
    case VariableStaticInfo.OP_CALL_VS2: return "CALL_VS2";
    case VariableStaticInfo.OP_READ_CHAR: return "READ_CHAR";
    case VariableStaticInfo.OP_SCAN_TABLE: return "SCAN_TABLE";
    case VariableStaticInfo.OP_NOT: return "NOT";
    case VariableStaticInfo.OP_CALL_VN: return "CALL_VN";
    case VariableStaticInfo.OP_CALL_VN2: return "CALL_VN2";
    case VariableStaticInfo.OP_TOKENISE: return "TOKENISE";
    case VariableStaticInfo.OP_ENCODE_TEXT: return "ENCODE_TEXT";
    case VariableStaticInfo.OP_COPY_TABLE: return "COPY_TABLE";
    case VariableStaticInfo.OP_PRINT_TABLE: return "PRINT_TABLE";
    case VariableStaticInfo.OP_CHECK_ARG_COUNT: return "CHECK_ARG_COUNT";
    case VariableStaticInfo.OP_SOUND_EFFECT: return "SOUND_EFFECT";
    default: return "unknown";
    }
  }
}
