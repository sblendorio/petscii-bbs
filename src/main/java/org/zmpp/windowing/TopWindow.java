/*
 * Created on 2008/07/16
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

/**
 * This class implements the virtual top window of the Z-machine screen model.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class TopWindow implements TextCursor {

  private int cursorx,  cursory;
  private int numCharsPerRow, numRows;
  // Note: It is assumed that this annotation will be overridden,
  // check if this is the case for Varicella
  private TextAnnotation annotation  =
    new TextAnnotation(ScreenModel.FONT_FIXED, ScreenModel.TEXTSTYLE_ROMAN,
                       ScreenModel.COLOR_BLACK, ScreenModel.COLOR_WHITE);

  /**
   * Default constructor.
   */
  public TopWindow() {
    resetCursor();
  }

  /** Resets the text cursor position. */
  public void resetCursor() {
    cursorx = 1;
    cursory = 1;
  }

  /**
   * Returns the current TextAnnotation used for this window.
   * @return the text annotation
   */
  public TextAnnotation getCurrentAnnotation() { return annotation; }

  /**
   * Sets the number of rows in this window.
   * @param numRows the number of rows
   */
  public void setNumRows(int numRows) { this.numRows = numRows; }

  /**
   * Returns the number of rows in this window.
   * @return the number of rows
   */
  public int getNumRows() { return numRows; }

  /**
   * Sets the new number of characters per row.
   * @param numChars the number of characters
   */
  public void setNumCharsPerRow(int numChars) {
    numCharsPerRow = numChars;
  }

  /**
   * Sets the font number for this window.
   * @param font the font number
   * @return returns the number of the previous font
   */
  public char setFont(char font) {
    char previousFont = this.annotation.getFont();
    annotation = annotation.deriveFont(font);
    return previousFont;
  }

  /**
   * Sets the current text style in this window.
   * @param style the text style
   */
  public void setCurrentTextStyle(int style) {
    annotation = annotation.deriveStyle(style);
  }

  /**
   * Sets the foreground color in this window.
   * @param color the new foreground color
   */
  public void setForeground(int color) {
    annotation = annotation.deriveForeground(color);
  }

  /**
   * Sets the new background color in this window.
   * @param color the new background color
   */
  public void setBackground(int color) {
    annotation = annotation.deriveBackground(color);
  }

  /**
   * Annotates the specified character with the current annotation.
   * @param zchar the character to annotate
   * @return the annotated character
   */
  public AnnotatedCharacter annotateCharacter(char zchar) {
    return new AnnotatedCharacter(annotation, zchar);
  }

  /**
   * Sets the new text cursor position.
   * @param line the line
   * @param column the column
   */
  public void setTextCursor(int line, int column) {
    if (outOfUpperBounds(line, column)) {
      // set to left margin of current line
      cursorx = 1;
    } else {
      this.cursorx = column;
      this.cursory = line;
    }
  }

  /**
   * Increments the current cursor position.
   */
  public void incrementCursorXPos() {
    this.cursorx++;
    // Make sure the cursor does not overrun the margin
    if (cursorx >= numCharsPerRow) {
      cursorx = numCharsPerRow - 1;
    }
  }

  /**
   * Notifies the ScreenModelListeners.
   * @param l listener
   * @param c character
   */
  public void notifyChange(ScreenModelListener l, char c) {
    if (c == '\n') {
      // handle newline differently
      cursorx = 0;
      cursory++;
    } else {
      l.topWindowUpdated(cursorx, cursory, annotateCharacter(c));
    }
  }

  /**
   * Determines whether the specified position is outside the upper window's
   * bounds.
   * @param line line number
   * @param column column number
   * @return true if out of bounds, false otherwise
   */
  private boolean outOfUpperBounds(int line, int column) {
    if (line < 1 || line > numRows) return true;
    if (column < 1 || column > numCharsPerRow) return true;
    return false;
  }

  /** {@inheritDoc} */
  public int getLine() { return cursory; }

  /** {@inheritDoc} */
  public int getColumn() { return cursorx; }

  /** {@inheritDoc} */
  public void setLine(int line) { cursory = line; }

  /** {@inheritDoc} */
  public void setColumn(int column) { cursorx = column; }

  /** {@inheritDoc} */
  public void setPosition(int line, int column) {
    cursorx = column;
    cursory = line;
  }
}
