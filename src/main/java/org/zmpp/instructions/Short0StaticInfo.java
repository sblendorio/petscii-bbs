/*
 * $Id: Short0StaticInfo.java,v 1.5 2006/04/12 02:04:30 weiju Exp $
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


public class Short0StaticInfo implements InstructionStaticInfo {

  private static final int[][] VALID_VERSIONS = {
    
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // RTRUE
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // RFALSE
    {},                         // 0x02
    {},                         // 0x03
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // NOP
    { 1, 2, 3, 4 },             // SAVE
    { 1, 2, 3, 4 },             // RESTORE
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // RESTART
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // RET_POPPED
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // POP/CATCH
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // QUIT
    { 1, 2, 3, 4, 5, 6, 7, 8 }, // NEW_LINE
    { 3 },                      // SHOW_STATUS
    { 3, 4, 5, 6, 7, 8 },       // VERIFY
    {},                         // 0x0e (EXTENDED)
    { 5, 6, 7, 8 },             // PIRACY
  };
  
  private static final Short0StaticInfo instance = new Short0StaticInfo();
  
  public static Short0StaticInfo getInstance() {
  
    return instance;
  }

  // Opcode numbers for short, 0OP
  public static final int OP_RTRUE              = 0x00;

  public static final int OP_RFALSE             = 0x01;

  public static final int OP_NOP                = 0x04;

  public static final int OP_SAVE               = 0x05;

  public static final int OP_RESTORE            = 0x06;

  public static final int OP_RESTART            = 0x07;

  public static final int OP_RET_POPPED         = 0x08;

  public static final int OP_POP                = 0x09; // Versions 1-4

  public static final int OP_QUIT               = 0x0a;

  public static final int OP_NEW_LINE           = 0x0b;

  public static final int OP_SHOW_STATUS        = 0x0c;

  public static final int OP_VERIFY             = 0x0d;

  public static final int OP_PIRACY             = 0x0f;
  
  public int[] getValidVersions(final int opcode) {

    return (opcode < VALID_VERSIONS.length) ? VALID_VERSIONS[opcode] :
      new int[0];
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isBranch(final int opcode, final int version) {
    
    switch (opcode) {
    
      case Short0StaticInfo.OP_SAVE:
      case Short0StaticInfo.OP_RESTORE:
        return version <= 3;
      case Short0StaticInfo.OP_VERIFY:
      case Short0StaticInfo.OP_PIRACY:
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
    
      case Short0StaticInfo.OP_SAVE:
      case Short0StaticInfo.OP_RESTORE:
        return version == 4;
      case Short0StaticInfo.OP_POP:
        return version >= 5;
      default:
        return false;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isOutput(final int opcode, final int version) {
    
    return opcode == OP_NEW_LINE;
  }
  
  public String getOpName(final int opcode, final int version) {
    
    switch (opcode) {
    
    case Short0StaticInfo.OP_NEW_LINE: return "NEW_LINE";
    case Short0StaticInfo.OP_NOP: return "NOP";
    case Short0StaticInfo.OP_POP: return "POP";
    case Short0StaticInfo.OP_QUIT: return "QUIT";
    case Short0StaticInfo.OP_RESTART: return "RESTART";
    case Short0StaticInfo.OP_RESTORE: return "RESTORE";
    case Short0StaticInfo.OP_RET_POPPED: return "RET_POPPED";
    case Short0StaticInfo.OP_RFALSE: return "RFALSE";
    case Short0StaticInfo.OP_RTRUE: return "RTRUE";
    case Short0StaticInfo.OP_SAVE: return "SAVE";
    case Short0StaticInfo.OP_PIRACY: return "PIRACY";
    default: return "unknown";
    }
  }  
}
