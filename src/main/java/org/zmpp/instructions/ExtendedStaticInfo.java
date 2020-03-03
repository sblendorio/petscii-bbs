/*
 * $Id: ExtendedStaticInfo.java,v 1.8 2006/05/16 18:05:16 weiju Exp $
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


public class ExtendedStaticInfo implements InstructionStaticInfo {

  private static final int[][] VALID_VERSIONS = {
    
    { 5, 6, 7, 8},  // SAVE
    { 5, 6, 7, 8},  // RESTORE
    { 5, 6, 7, 8 }, // LOG_SHIFT
    { 5, 6, 7, 8 }, // ART_SHIFT
    { 5, 6, 7, 8 }, // SET_FONT
    { 6 },          // DRAW_PICTURE
    { 6 },          // PICTURE_DATA
    { 6 },          // ERASE_PICTURE
    { 6 },          // SET_MARGINS
    { 5, 6, 7, 8 }, // SAVE_UNDO    
    { 5, 6, 7, 8 }, // RESTORE_UNDO
    { 5, 6, 7, 8 }, // PRINT_UNICODE
    { 5, 6, 7, 8 }, // CHECK_UNICODE
    { },            // 0x0d
    { },            // 0x0e
    { },            // 0x0f
    { 6 },          // MOVE_WINDOW
    { 6 },          // WINDOW_SIZE
    { 6 },          // WINDOW_STYLE
    { 6 },          // GET_WIND_PROP
    { 6 },          // SCROLL_WINDOW
    { 6 },          // POP_STACK
    { 6 },          // READ_MOUSE
    { 6 },          // MOUSE_WINDOW
    { 6 },          // PUSH_STACK
    { 6 },          // PUT_WIND_PROP
    { },          // PRINT_FORM
    { },          // MAKE_MENU
    { 6 },          // PICTURE_TABLE
  };

  private static final ExtendedStaticInfo instance = new ExtendedStaticInfo();
  
  public static ExtendedStaticInfo getInstance() {
    
    return instance;
  }

  /**
   * List of opcodes. See Z-Machine Standards document 1.0 for
   * explanations.
   */
  public static final int OP_SAVE                 = 0x00;
  public static final int OP_RESTORE              = 0x01;
  public static final int OP_LOG_SHIFT            = 0x02;
  public static final int OP_ART_SHIFT            = 0x03;
  public static final int OP_SET_FONT             = 0x04;
  public static final int OP_DRAW_PICTURE         = 0x05;
  public static final int OP_PICTURE_DATA         = 0x06;
  public static final int OP_ERASE_PICTURE        = 0x07;
  public static final int OP_SET_MARGINS          = 0x08;  
  public static final int OP_SAVE_UNDO            = 0x09;
  public static final int OP_RESTORE_UNDO         = 0x0a;
  public static final int OP_PRINT_UNICODE        = 0x0b;
  public static final int OP_CHECK_UNICODE        = 0x0c;
  public static final int OP_MOVE_WINDOW          = 0x10;
  public static final int OP_WINDOW_SIZE          = 0x11;
  public static final int OP_WINDOW_STYLE         = 0x12;
  public static final int OP_GET_WIND_PROP        = 0x13;
  public static final int OP_SCROLL_WINDOW        = 0x14;
  public static final int OP_POP_STACK            = 0x15;
  public static final int OP_READ_MOUSE           = 0x16;
  public static final int OP_MOUSE_WINDOW         = 0x17;
  public static final int OP_PUSH_STACK           = 0x18;
  public static final int OP_PUT_WIND_PROP        = 0x19;
  public static final int OP_PRINT_FORM           = 0x1a;
  public static final int OP_MAKE_MENU            = 0x1b;  
  public static final int OP_PICTURE_TABLE        = 0x1c;  
  
  public int[] getValidVersions(final int opcode) {

    return (opcode < VALID_VERSIONS.length) ? VALID_VERSIONS[opcode] :
      new int[0];
  }

  /**
   * {@inheritDoc}
   */
  public boolean storesResult(final int opcode, final int version) {
    
    switch (opcode) {
    
    case ExtendedStaticInfo.OP_SAVE:
    case ExtendedStaticInfo.OP_RESTORE:
    case ExtendedStaticInfo.OP_LOG_SHIFT:
    case ExtendedStaticInfo.OP_ART_SHIFT:
    case ExtendedStaticInfo.OP_SET_FONT:
    case ExtendedStaticInfo.OP_SAVE_UNDO:
    case ExtendedStaticInfo.OP_RESTORE_UNDO:
    case ExtendedStaticInfo.OP_CHECK_UNICODE:
    case ExtendedStaticInfo.OP_GET_WIND_PROP:
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
    
    case ExtendedStaticInfo.OP_PICTURE_DATA:
    case ExtendedStaticInfo.OP_PUSH_STACK:
    case ExtendedStaticInfo.OP_MAKE_MENU:
      return true;
    default:
      return false;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isOutput(final int opcode, final int version) {
    
    return opcode == OP_PRINT_UNICODE;
  }

  /**
   * {@inheritDoc}
   */
  public String getOpName(final int opcode, final int version) {

    switch (opcode) {
    
    case ExtendedStaticInfo.OP_SAVE:
      return "SAVE";
    case ExtendedStaticInfo.OP_RESTORE:
      return "RESTORE";
    case ExtendedStaticInfo.OP_LOG_SHIFT:
      return "LOG_SHIFT";
    case ExtendedStaticInfo.OP_ART_SHIFT:
      return "ART_SHIFT";
    case ExtendedStaticInfo.OP_SET_FONT:
      return "SET_FONT";
    case ExtendedStaticInfo.OP_SAVE_UNDO:
      return "SAVE_UNDO";
    case ExtendedStaticInfo.OP_RESTORE_UNDO:
      return "RESTORE_UNDO";
    case ExtendedStaticInfo.OP_PRINT_UNICODE:
      return "PRINT_UNICODE";
    case ExtendedStaticInfo.OP_CHECK_UNICODE:
      return "CHECK_UNICODE";
    case ExtendedStaticInfo.OP_MOUSE_WINDOW:
      return "MOUSE_WINDOW";
    case ExtendedStaticInfo.OP_PICTURE_DATA:
      return "PICTURE_DATA";
    case ExtendedStaticInfo.OP_DRAW_PICTURE:
      return "DRAW_PICTURE";
    case ExtendedStaticInfo.OP_ERASE_PICTURE:
      return "ERASE_PICTURE";
    case ExtendedStaticInfo.OP_MOVE_WINDOW:
      return "MOVE_WINDOW";
    case ExtendedStaticInfo.OP_WINDOW_SIZE:
      return "WINDOW_SIZE";
    case ExtendedStaticInfo.OP_WINDOW_STYLE:
      return "WINDOW_STYLE";
    case ExtendedStaticInfo.OP_SET_MARGINS:
      return "SET_MARGINS";
    case ExtendedStaticInfo.OP_GET_WIND_PROP:
      return "GET_WIND_PROP";
    case ExtendedStaticInfo.OP_PICTURE_TABLE:
      return "PICTURE_TABLE";
    case ExtendedStaticInfo.OP_PUT_WIND_PROP:
      return "PUT_WIND_PROP";
    case ExtendedStaticInfo.OP_PUSH_STACK:
      return "PUSH_STACK";
    case ExtendedStaticInfo.OP_POP_STACK:
      return "POP_STACK";
    case ExtendedStaticInfo.OP_READ_MOUSE:
      return "READ_MOUSE";
    case ExtendedStaticInfo.OP_SCROLL_WINDOW:
      return "SCROLL_WINDOW";
    default:
      return "unknown";
    }
  }
}
