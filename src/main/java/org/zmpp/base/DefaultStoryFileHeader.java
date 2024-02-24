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

import static org.zmpp.base.MemoryUtil.*;

/**
 * This is the default implementation of the StoryFileHeader interface.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class DefaultStoryFileHeader implements StoryFileHeader {

  /** The memory map. */
  private Memory memory;

  /**
   * Constructor.
   * @param memory a Memory object
   */
  public DefaultStoryFileHeader(final Memory memory) {
    this.memory = memory;
  }

  /** {@inheritDoc} */
  public int getVersion() { return memory.readUnsigned8(0x00); }

  /** {@inheritDoc} */
  public String getSerialNumber() { return extractAscii(0x12, 6); }

  /** {@inheritDoc} */
  public int getFileLength() {
    // depending on the story file version we have to multiply the
    // file length in the header by a constant
    int fileLength = memory.readUnsigned16(0x1a);
    if (getVersion() <= 3) {
      fileLength *= 2;
    } else if (getVersion() <= 5) {
      fileLength *= 4;
    } else {
      fileLength *= 8;
    }
    return fileLength;
  }

  /**
   * {@inheritDoc}
   */
  public void setInterpreterVersion(final int version) {
    if (getVersion() == 4 || getVersion() == 5) {
      memory.writeUnsigned8(0x1f, String.valueOf(version).charAt(0));
    } else {
      memory.writeUnsigned8(0x1f, (char) version);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setFontWidth(final int units) {
    if (getVersion() == 6) {
      memory.writeUnsigned8(0x27, (char) units);
    } else {
      memory.writeUnsigned8(0x26, (char) units);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setFontHeight(final int units) {
    if (getVersion() == 6) {
      memory.writeUnsigned8(0x26, (char) units);
    } else {
      memory.writeUnsigned8(0x27, (char) units);
    }
  }

  /** {@inheritDoc} */
  public void setMouseCoordinates(final int x, final int y) {
    // check the extension table
    final int extTable = memory.readUnsigned16(0x36);
    if (extTable > 0) {
      final int numwords = memory.readUnsigned16(extTable);
      if (numwords >= 1) {
        memory.writeUnsigned16(extTable + 2, toUnsigned16(x));
      }
      if (numwords >= 2) {
        memory.writeUnsigned16(extTable + 4, toUnsigned16(y));
      }
    }
  }

  /** {@inheritDoc} */
  public char getCustomAccentTable() {
    // check the extension table
    char result = 0;
    final int extTable = memory.readUnsigned16(0x36);
    if (extTable > 0) {
      final int numwords = memory.readUnsigned16(extTable);
      if (numwords >= 3) {
        result = memory.readUnsigned16(extTable + 6);
      }
    }
    return result;
  }

  // ***********************************************************************
  // ****** Attributes
  // **********************************

  /**
   * {@inheritDoc}
   */
  public void setEnabled(final Attribute attribute, final boolean flag) {
    switch (attribute) {

    case DEFAULT_FONT_IS_VARIABLE:
      setDefaultFontIsVariablePitch(flag);
      break;
    case TRANSCRIPTING:
      setTranscripting(flag);
      break;
    case FORCE_FIXED_FONT:
      setForceFixedFont(flag);
      break;
    case SUPPORTS_TIMED_INPUT:
      setTimedInputAvailable(flag);
      break;
    case SUPPORTS_FIXED_FONT:
      setFixedFontAvailable(flag);
      break;
    case SUPPORTS_BOLD:
      setBoldFaceAvailable(flag);
      break;
    case SUPPORTS_ITALIC:
      setItalicAvailable(flag);
      break;
    case SUPPORTS_SCREEN_SPLITTING:
      setScreenSplittingAvailable(flag);
      break;
    case SUPPORTS_STATUSLINE:
      setStatusLineAvailable(flag);
      break;
    case SUPPORTS_COLOURS:
      setSupportsColours(flag);
    default:
      break;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isEnabled(final Attribute attribute) {
    switch (attribute) {
    case TRANSCRIPTING:
      return isTranscriptingOn();
    case FORCE_FIXED_FONT:
      return forceFixedFont();
    case SCORE_GAME:
      return isScoreGame();
    case DEFAULT_FONT_IS_VARIABLE:
      return defaultFontIsVariablePitch();
    case USE_MOUSE:
      return useMouse();
    default:
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 55; i++) {
      builder.append(String.format("Addr: %02x Byte: %02x\n", i,
                                   (int) memory.readUnsigned8(i)));
    }
    return builder.toString();
  }

  // ************************************************************************
  // ****** Private section
  // *******************************

  /**
   * Extract an ASCII string of the specified length starting at the specified
   * address.
   *
   * @param address the start address
   * @param length the length of the ASCII string
   * @return the ASCII string at the specified position
   */
  private String extractAscii(final int address, final int length) {
    final StringBuilder builder = new StringBuilder();
    for (int i = address; i < address + length; i++) {

      builder.append((char) memory.readUnsigned8(i));
    }
    return builder.toString();
  }

  /**
   * Sets the state of the transcript stream.
   * @param flag new transcript state
   */
  private void setTranscripting(final boolean flag) {
    char flags = memory.readUnsigned16(0x10);
    flags = (char) (flag ? (flags | 1) : (flags & 0xfe));
    memory.writeUnsigned16(0x10, (char) flags);
  }

  /**
   * Returns the state of the transcript stream.
   * @return transcript state
   */
  private boolean isTranscriptingOn() {
    return (memory.readUnsigned16(0x10) & 1) > 0;
  }

  /**
   * Returns state of the force fixed font flag.
   * @return true if force fixed font, false otherwise
   */
  private boolean forceFixedFont() {
    return (memory.readUnsigned16(0x10) & 2) > 0;
  }

  /**
   * Sets the force fixed font flag.
   * @param flag true if fixed font forced, false otherwise
   */
  private void setForceFixedFont(final boolean flag) {
    char flags = memory.readUnsigned16(0x10);
    flags = (char) (flag ? (flags | 2) : (flags & 0xfd));
    memory.writeUnsigned16(0x10, (char) flags);
  }

  /**
   * Sets the timed input availability flag.
   * @param flag true if timed input available, false otherwise
   */
  private void setTimedInputAvailable(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 128) : (flags & 0x7f);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Determine whether this game is a "score" game or a "time" game.
   * @return true if score game, false if time game
   */
  private boolean isScoreGame() {
    return (memory.readUnsigned8(0x01) & 2) == 0;
  }

  /**
   * Sets the fixed font availability flag.
   * @param flag true if fixed font available, false otherwise
   */
  private void setFixedFontAvailable(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 16) : (flags & 0xef);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Sets the bold supported flag.
   * @param flag true if bold supported, false otherwise
   */
  private void setBoldFaceAvailable(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 4) : (flags & 0xfb);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Sets the italic supported flag.
   * @param flag true if italic supported, false otherwise
   */
  private void setItalicAvailable(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 8) : (flags & 0xf7);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Sets the screen splitting availability flag.
   * @param flag true if splitting supported, false otherwise
   */
  private void setScreenSplittingAvailable(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 32) : (flags & 0xdf);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Sets the flag whether a status line is available or not.
   * @param flag true if status line available, false otherwise
   */
  private void setStatusLineAvailable(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 16) : (flags & 0xef);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Sets the state whether the default font is variable or not.
   * @param flag true if default font is variable, false otherwise
   */
  private void setDefaultFontIsVariablePitch(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 64) : (flags & 0xbf);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Returns whether default font is variable pitch.
   * @return true if variable pitch, false otherwise
   */
  private boolean defaultFontIsVariablePitch() {
    return (memory.readUnsigned8(0x01) & 64) > 0;
  }

  /**
   * Returns the status of the supports color flag.
   * @param flag state of supports color flag
   */
  private void setSupportsColours(final boolean flag) {
    int flags = memory.readUnsigned8(0x01);
    flags = flag ? (flags | 1) : (flags & 0xfe);
    memory.writeUnsigned8(0x01, (char) flags);
  }

  /**
   * Returns the status of the use mouse flag.
   * @return the use mouse flag
   */
  private boolean useMouse() {
    return (memory.readUnsigned8(0x10) & 32) > 0;
  }
}
