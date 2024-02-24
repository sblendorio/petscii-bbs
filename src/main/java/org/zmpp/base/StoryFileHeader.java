/*
 * Created on 2005/09/23
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
package org.zmpp.base;

/**
 * This interface defines the structure of a story file header in the Z-machine.
 * It is designed as a read only view to the byte array containing the
 * story file data.
 * By this means, changes in the memory map will be implicitly change
 * the header structure.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface StoryFileHeader {

  int RELEASE              = 0x02;
  int PROGRAM_START        = 0x06;
  int DICTIONARY           = 0x08;
  int OBJECT_TABLE         = 0x0a;
  int GLOBALS              = 0x0c;
  int STATIC_MEM           = 0x0e;
  int ABBREVIATIONS        = 0x18;
  int CHECKSUM             = 0x1c;
  int INTERPRETER_NUMBER   = 0x1e;
  int SCREEN_HEIGHT        = 0x20;
  int SCREEN_WIDTH         = 0x21;
  int SCREEN_WIDTH_UNITS   = 0x22;
  int SCREEN_HEIGHT_UNITS  = 0x24;
  int ROUTINE_OFFSET       = 0x28;
  int STATIC_STRING_OFFSET = 0x2a;
  int DEFAULT_BACKGROUND   = 0x2c;
  int DEFAULT_FOREGROUND   = 0x2d;
  int TERMINATORS          = 0x2e;
  int OUTPUT_STREAM3_WIDTH = 0x30; // 16 bit
  int STD_REVISION_MAJOR   = 0x32;
  int STD_REVISION_MINOR   = 0x33;
  int CUSTOM_ALPHABET      = 0x34;

  /**
   * Attributes for the file header flags.
   */
  enum Attribute {
    DEFAULT_FONT_IS_VARIABLE,
    SCORE_GAME, SUPPORTS_STATUSLINE, SUPPORTS_SCREEN_SPLITTING, // V3 only
    TRANSCRIPTING, FORCE_FIXED_FONT, SUPPORTS_TIMED_INPUT,
    SUPPORTS_FIXED_FONT, SUPPORTS_ITALIC, SUPPORTS_BOLD,
    SUPPORTS_COLOURS, USE_MOUSE
  };
  /**
   * Returns the story file version.
   * @return the story file version
   */
  int getVersion();

  /**
   * Returns this game's serial number.
   * @return the serial number
   */
  String getSerialNumber();

  /**
   * Returns this story file's length.
   * @return the file length
   */
  int getFileLength();

  /**
   * Sets the interpreter version.
   * @param version the version
   */
  void setInterpreterVersion(int version);

  /**
   * Sets the font width in width of a '0'.
   * @param units the number of units in widths of a '0'
   */
  void setFontWidth(int units);

  /**
   * Sets the font height in width of a '0'.
   * @param units the number of units in heights of a '0'
   */
  void setFontHeight(int units);

  /**
   * Sets the mouse coordinates.
   * @param x the x coordinate
   * @param y the y coordinate
   */
  void setMouseCoordinates(int x, int y);

  /**
   * Returns the address of the cutom unicode translation table.
   * @return the address of the custom unicode translation table
   */
  char getCustomAccentTable();

  // ********************************************************************
  // ****** Attributes
  // **********************************

  /**
   * Enables the specified attribute.
   * @param attribute the attribute to set
   * @param flag the value
   */
  void setEnabled(Attribute attribute, boolean flag);

  /**
   * Checks the enabled status of the specified attribute
   * @param attribute the attribute name
   * @return true if enabled, false otherwise
   */
  boolean isEnabled(Attribute attribute);
}
