/*
 * $Id: StoryFileHeader.java,v 1.20 2006/05/30 17:24:54 weiju Exp $
 * 
 * Created on 2005/09/23
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
 * This interface defines the structure of a story file header in the Z-machine.
 * It is designed as a read only view to the byte array containing the
 * story file data.
 * By this means, changes in the memory map will be implicitly change
 * the header structure.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface StoryFileHeader {

  enum Attribute {
    
    DEFAULT_FONT_IS_VARIABLE,
    SCORE_GAME, SUPPORTS_STATUSLINE, SUPPORTS_SCREEN_SPLITTING, // V3 only
    TRANSCRIPTING, FORCE_FIXED_FONT, SUPPORTS_TIMED_INPUT,
    SUPPORTS_FIXED_FONT, SUPPORTS_ITALIC, SUPPORTS_BOLD,
    SUPPORTS_COLOURS, USE_MOUSE
  };
  /**
   * Returns the story file version.
   * 
   * @return the story file version
   */
  int getVersion();
  
  /**
   * Returns the release number.
   * 
   * @return the release number
   */
  int getRelease();
  
  /**
   * Returns the high memory start address.
   * 
   * @return the start of the high memory
   */
  int getHighMemAddress();
  
  /**
   * Returns the program counter start address.
   * 
   * @return the PC start address
   */
  int getProgramStart();
  
  /**
   * Returns the dictionary's start address.
   * 
   * @return the dictionary start address
   */  
  int getDictionaryAddress();
  
  /**
   * Returns the object table's start address.
   * 
   * @return the object table's start address
   */
  int getObjectTableAddress();
  
  /**
   * Returns the address of the global variables.
   * 
   * @return the global variables section
   */
  int getGlobalsAddress();
  
  /**
   * Returns the static memory start address.
   * 
   * @return the start address of the static memory
   */
  int getStaticsAddress();
  
  /**
   * Returns this game's serial number.
   * 
   * @return the serial number
   */
  String getSerialNumber();
  
  /**
   * Returns the start address of the abbreviations section.
   * 
   * @return the abbreviations start address
   */
  int getAbbreviationsAddress();
  
  /**
   * Returns this story file's length.
   * 
   * @return the file length
   */
  int getFileLength();
  
  /**
   * Returns the checksum for the story file.
   * 
   * @return the checksum
   */
  int getChecksum();

  /**
   * Sets the interpreter number.
   * 
   * @param number the interpreter number
   */
  void setInterpreterNumber(int number);
  
  /**
   * Returns the interpreter number.
   * 
   * @return the interpreter number
   */
  int getInterpreterNumber();
  
  /**
   * Sets the interpreter version.
   * 
   * @param version the version
   */
  void setInterpreterVersion(int version);
  
  /**
   * Returns the screen width.
   * 
   * @return the screen width
   */
  int getScreenWidth();
  /**
   * Sets the screen width in number of characters.
   * 
   * @param numChars the number of characters
   */
  void setScreenWidth(int numChars);
  
  /**
   * Sets the screen width in units.
   * 
   * @param units the number of units
   */
  void setScreenWidthUnits(int units);
  
  /**
   * Returns the screen width in units.
   * 
   * @return the screen width in units
   * 
   */
  int getScreenWidthUnits();
  
  /**
   * Returns the screen height.
   * 
   * @return the screen height
   */
  int getScreenHeight();
  
  /**
   * Returns the screen height in units.
   * 
   * @return the screen height in units
   */
  int getScreenHeightUnits();
  
  /**
   * Sets the screen height in number of lines.
   * 
   * @param numLines the number of lines
   */
  void setScreenHeight(int numLines);
  
  /**
   * Sets the screen height in units.
   * 
   * @param units the number of units
   */
  void setScreenHeightUnits(int units);
  
  /**
   * Only for V6 and V7 games: the routine offset.
   * 
   * @return the routine offset
   */
  int getRoutineOffset();
  
  /**
   * Only in V6 and V7: the static string offset.
   * 
   * @return the static string offset
   */
  int getStaticStringOffset();
  
  /**
   * Returns the default background color.
   * 
   * @return the default background color
   */
  int getDefaultBackgroundColor();
  
  /**
   * Returns the default foreground color.
   * 
   * @return the default foreground color
   */
  int getDefaultForegroundColor();
  
  /**
   * Sets the default background color.
   * 
   * @param color the default background color.
   */
  void setDefaultBackgroundColor(int color);
  
  /**
   * Sets the default foreground color.
   * 
   * @param color the default foreground color
   */
  void setDefaultForegroundColor(int color);
  
  /**
   * Sets the standard revision number this machine supports to the
   * header.
   * 
   * @param major major revision number
   * @param minor minor revision number
   */
  void setStandardRevision(int major, int minor);
  
  /**
   * Returns the address of the terminating characters table.
   * 
   * @return the address of the terminating characters table
   */
  int getTerminatorsAddress();
  
  /**
   * Sets the font width in width of a '0'.
   * 
   * @param units the number of units in widths of a '0'
   */
  void setFontWidth(int units);
  
  /**
   * Returns the font width in width of a '0'.
   * 
   * @return the font width in units
   */
  int getFontWidth();
  
  /**
   * Sets the font height in width of a '0'.
   * 
   * @param units the number of units in heights of a '0'
   */
  void setFontHeight(int units);
  
  /**
   * Returns the font height in height of a '0'.
   * 
   * @return the font height in units
   */
  int getFontHeight();
  
  /**
   * Returns the address of the custom alphabet table.
   * 
   * @return the custom alphabet table
   */
  int getCustomAlphabetTable();
  
  /**
   * Sets the mouse coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   */
  void setMouseCoordinates(int x, int y);
  
  /**
   * Returns the address of the cutom unicode translation table.
   * 
   * @return the address of the custom unicode translation table
   */
  int getCustomAccentTable();
  
  /**
   * Sets the width of the text sent to output stream 3 in units.
   * 
   * @param units the text width in units
   */
  void setOutputStream3TextWidth(int units);
  
  // ********************************************************************
  // ****** Attributes
  // **********************************
  
  /**
   * Enables the specified attribute.
   * 
   * @param attribute the attribute to set
   * @param flag the value
   */
  void setEnabled(Attribute attribute, boolean flag);
  
  /**
   * Checks the enabled status of the specified attribute
   * 
   * @param attribute the attribute name
   * @return true if enabled, false otherwise
   */
  boolean isEnabled(Attribute attribute);
}
