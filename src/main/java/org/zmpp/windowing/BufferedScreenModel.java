/*
 * Created on 2008/04/23
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
package org.zmpp.windowing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.zmpp.base.DefaultStoryFileHeader;
import org.zmpp.base.Memory;
import org.zmpp.base.StoryFileHeader;
import org.zmpp.base.StoryFileHeader.Attribute;
import org.zmpp.encoding.IZsciiEncoding;
import org.zmpp.io.OutputStream;

/**
 * BufferedScreenModel is the attempt to provide a reusable screen model
 * that will be part of the core in later versions. It is mainly a
 * configurable virtual window management model, providing virtual windows
 * that the machine writes to. It is intended to provide interfaces to
 * both Glk and Z-machine and to combine the abilities of both.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class BufferedScreenModel implements ScreenModel, StatusLine,
  OutputStream {
  private static final Logger LOG = Logger.getLogger("org.zmpp.screen");

  private int current = WINDOW_BOTTOM;
  private BufferedTextWindow bottomWindow = new BufferedTextWindow();
  private TopWindow topWindow = new TopWindow();
  private List<ScreenModelListener> screenModelListeners =
    new ArrayList<ScreenModelListener>();
  private List<StatusLineListener> statusLineListeners =
    new ArrayList<StatusLineListener>();
  private IZsciiEncoding encoding;
  private Memory memory;
  private StoryFileHeader fileheader;

  /**
   * Status line listener.
   */
  public interface StatusLineListener {
    /**
     * Update the status line.
     * @param objectDescription object description
     * @param status status text
     */
    void statusLineUpdated(String objectDescription, String status);
  }

  /**
   * Adds a ScreenModelListener.
   * @param l the listener to add
   */
  public void addScreenModelListener(ScreenModelListener l) {
    screenModelListeners.add(l);
  }

  /**
   * Adds a StatusLineListener.
   * @param l the listener to add
   */
  public void addStatusLineListener(StatusLineListener l) {
    statusLineListeners.add(l);
  }

  /**
   * Initialize the model, an Encoding object is needed to retrieve
   * Unicode characters.
   * @param aMemory a Memory object
   * @param anEncoding the ZsciiEncoding object
   */
  public void init(Memory aMemory, IZsciiEncoding anEncoding) {
    this.memory = aMemory;
    this.fileheader = new DefaultStoryFileHeader(memory);
    this.encoding = anEncoding;
  }

  /** {@inheritDoc} */
  public TextAnnotation getTopAnnotation() {
    return topWindow.getCurrentAnnotation();
  }
  /** {@inheritDoc} */
  public TextAnnotation getBottomAnnotation() {
    return bottomWindow.getCurrentAnnotation();
  }

  /**
   * Sets the number of charactes per row, should be called if the size of
   * the output area or the size of the font changes.
   * @param num the number of characters in a row
   */
  public void setNumCharsPerRow(int num) {
    topWindow.setNumCharsPerRow(num);
  }

  /**
   * Resets the screen model.
   */
  public void reset() {
    topWindow.resetCursor();
    bottomWindow.reset();
    current = WINDOW_BOTTOM;
  }

  /**
   * Splits the window.
   * @param linesUpperWindow number of lines in upper window
   */
  public void splitWindow(int linesUpperWindow) {
    LOG.info("SPLIT_WINDOW: " + linesUpperWindow);
    topWindow.setNumRows(linesUpperWindow);
    for (ScreenModelListener l : screenModelListeners) {
      l.screenSplit(linesUpperWindow);
    }
  }
  /** {@inheritDoc} */
  public void setWindow(int window) {
    LOG.info("SET_WINDOW: " + window);
    current = window;
    if (current == ScreenModel.WINDOW_TOP) {
      topWindow.resetCursor();
    }
  }
  /** {@inheritDoc} */
  public int getActiveWindow() { return current; }

  /** {@inheritDoc} */
  public void setTextStyle(int style) {
    LOG.info("SET_TEXT_STYLE: " + style);
    topWindow.setCurrentTextStyle(style);
    bottomWindow.setCurrentTextStyle(style);
  }

  /** {@inheritDoc} */
  public void setBufferMode(boolean flag) {
    LOG.info("SET_BUFFER_MODE: " + flag);
    if (current == ScreenModel.WINDOW_BOTTOM) {
      bottomWindow.setBuffered(flag);
    }
  }

  /** {@inheritDoc} */
  public void eraseLine(int value) {
    LOG.info("ERASE_LINE: " + value);
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /** {@inheritDoc} */
  public void eraseWindow(int window) {
    LOG.info("ERASE_WINDOW: " + window);
    for (ScreenModelListener l : screenModelListeners) {
      l.windowErased(window);
    }
    if (window == -1) {
      splitWindow(0);
      setWindow(ScreenModel.WINDOW_BOTTOM);
      topWindow.resetCursor();
    }
    if (window == ScreenModel.WINDOW_TOP) {
      for (ScreenModelListener l : screenModelListeners) {
        l.windowErased(ScreenModel.WINDOW_TOP);
      }
      topWindow.resetCursor();
    }
  }

  /** {@inheritDoc} */
  public void setTextCursor(int line, int column, int window) {
    int targetWindow = getTargetWindow(window);
    //LOG.info(String.format("SET_TEXT_CURSOR, line: %d, column: %d, " +
    //                       "window: %d\n", line, column, targetWindow));
    if (targetWindow == WINDOW_TOP) {
      for (ScreenModelListener l : screenModelListeners) {
        l.topWindowCursorMoving(line, column);
      }
      topWindow.setTextCursor(line, column);
    }
  }

  /**
   * Returns the window number for the specified parameter.
   * @param window the window number
   * @return current window or specified
   */
  private int getTargetWindow(int window) {
    return window == ScreenModel.CURRENT_WINDOW ? current : window;
  }

  /** {@inheritDoc} */
  public TextCursor getTextCursor() {
    if (this.current != ScreenModel.WINDOW_TOP) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    return topWindow;
  }

  /** {@inheritDoc} */
  public char setFont(char fontnumber) {
    if (fontnumber != ScreenModel.FONT_FIXED &&
        fontnumber != ScreenModel.FONT_NORMAL) {
      setFont(ScreenModel.FONT_FIXED); // call yourself again with the fixed
      return 0;
    }
    if (current == WINDOW_TOP) {
      // For the top window, the normal font should not be used, instead,
      // we always assume the fixed font as the top window normal font
      // The character graphics font is a fixed font, so we want to set that
      return fontnumber == ScreenModel.FONT_NORMAL ? ScreenModel.FONT_FIXED :
              topWindow.setFont(fontnumber);
    } else {
      return bottomWindow.setCurrentFont(fontnumber);
    }
  }

  /** {@inheritDoc} */
  public void setBackground(int colornumber, int window) {
    LOG.info("setBackground, color: " + colornumber);
    topWindow.setBackground(colornumber);
    bottomWindow.setBackground(colornumber);
  }

  /** {@inheritDoc} */
  public void setForeground(int colornumber, int window) {
    LOG.info("setForeground, color: " + colornumber);
    topWindow.setForeground(colornumber);
    bottomWindow.setForeground(colornumber);
  }

  /** {@inheritDoc} */
  public OutputStream getOutputStream() { return this; }

  // OutputStream
  private boolean selected;

  /**
   * This checks the fixed font flag and adjust the font if necessary.
   */
  private void checkFixedFontFlag() {
    if (fileheader.isEnabled(Attribute.FORCE_FIXED_FONT) &&
        current == WINDOW_BOTTOM) {
      bottomWindow.setCurrentFont(ScreenModel.FONT_FIXED);
    } else if (!fileheader.isEnabled(Attribute.FORCE_FIXED_FONT) &&
               current == WINDOW_BOTTOM) {
      bottomWindow.setCurrentFont(ScreenModel.FONT_NORMAL);
    }
  }

  /** {@inheritDoc} */
  public void print(char zsciiChar) {
    checkFixedFontFlag();
    char unicodeChar = encoding.getUnicodeChar(zsciiChar);
    if (current == WINDOW_BOTTOM) {
      bottomWindow.printChar(unicodeChar);
      if (!bottomWindow.isBuffered()) {
        flush();
      }
    } else if (current == WINDOW_TOP) {
      for (ScreenModelListener l : screenModelListeners) {
        topWindow.notifyChange(l, unicodeChar);
        topWindow.incrementCursorXPos();
      }
    }
  }

  /** {@inheritDoc} */
  public void close() { }

  /**
   * Notify listeners that the screen has changed.
   */
  public void flush() {
    for (ScreenModelListener l : screenModelListeners) {
      l.screenModelUpdated(this);
    }
  }

  /** {@inheritDoc} */
  public void select(boolean flag) { selected = flag; }

  /** {@inheritDoc} */
  public boolean isSelected() { return selected; }

  // ***********************************************************************
  // ***** StatusLine implementation
  // ***************************************
  /** {@inheritDoc} */
  public void updateStatusScore(String objectName, int score, int steps) {
    for (StatusLineListener l : statusLineListeners) {
      l.statusLineUpdated(objectName, score + "/" + steps);
    }
  }

  /** {@inheritDoc} */
  public void updateStatusTime(String objectName, int hours, int minutes) {
    for (StatusLineListener l : statusLineListeners) {
      l.statusLineUpdated(objectName, hours + ":" + minutes);
    }
  }

  // ***********************************************************************
  // ***** Additional public interface
  // ***************************************

  /**
   * Returns number of rows in upper window.
   * @return number of rows
   */
  public int getNumRowsUpper() { return topWindow.getNumRows(); }

  /**
   * Returns current background color.
   * @return current background color
   */
  public int getBackground() {
    int background = bottomWindow.getBackground();
    return background == COLOR_DEFAULT ?
      getDefaultBackground() : background;
  }

  /**
   * Returns current foreground color.
   * @return current foreground color
   */
  public int getForeground() {
    int foreground = bottomWindow.getForeground();
    return foreground == COLOR_DEFAULT ?
      getDefaultForeground() : foreground;
  }

  /**
   * Returns default background color.
   * @return default background color
   */
  private int getDefaultBackground() {
    return memory.readUnsigned8(StoryFileHeader.DEFAULT_BACKGROUND);
  }

  /**
   * Returns default foreground color.
   * @return default foreground color
   */
  private int getDefaultForeground() {
    return memory.readUnsigned8(StoryFileHeader.DEFAULT_FOREGROUND);
  }

  /**
   * Returns buffer to lower window.
   * @return buffer to lower window
   */
  public List<AnnotatedText> getLowerBuffer() {
    return bottomWindow.getBuffer();
  }
}
