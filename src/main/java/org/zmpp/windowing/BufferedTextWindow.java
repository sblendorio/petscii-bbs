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

/**
 * BufferedTextWindow is part of the BufferedScreenModel, it represents a
 * buffer for continuously flowing text.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class BufferedTextWindow {

  private List<AnnotatedText> textBuffer;
  private TextAnnotation currentAnnotation = new TextAnnotation(
    TextAnnotation.FONT_NORMAL, TextAnnotation.TEXTSTYLE_ROMAN);
  private StringBuilder currentRun;
  private boolean isBuffered;

  /**
   * Constructor.
   */
  public BufferedTextWindow() {
    reset();
  }

  /**
   * Reset the window state.
   */
  public void reset() {
    textBuffer = new ArrayList<AnnotatedText>();
    currentRun = new StringBuilder();
    isBuffered = true;
  }
  /**
   * Retrieves the currently active annotation.
   * @return active annotation
   */
  public TextAnnotation getCurrentAnnotation() { return currentAnnotation; }
  /**
   * Determines whether this window is buffered.
   * @return true if buffered, false otherwise
   */
  public boolean isBuffered() { return isBuffered; }
  /**
   * Sets the buffered flag.
   * @param flag true to set to buffered, false to unbuffered
   */
  public void setBuffered(boolean flag) { isBuffered = flag; }
  /**
   * Sets the window's current font.
   * @param font font number
   * @return previous font number
   */
  public char setCurrentFont(char font) {
    char previousFont = currentAnnotation.getFont();
    // no need to start a new run if the font is the same
    if (previousFont != font) {
      startNewAnnotatedRun(currentAnnotation.deriveFont(font));
    }
    return previousFont;
  }
  /**
   * Sets the window's current text style.
   * @param style text style number
   */
  public void setCurrentTextStyle(int style) {
    startNewAnnotatedRun(currentAnnotation.deriveStyle(style));
  }
  /**
   * Sets this window's current background color.
   * @param color color number
   */
  public void setBackground(int color) {
    startNewAnnotatedRun(currentAnnotation.deriveBackground(color));
  }
  /**
   * Sets this window's current foreground color.
   * @param color color number
   */
  public void setForeground(int color) {
    startNewAnnotatedRun(currentAnnotation.deriveForeground(color));
  }
  /**
   * Retrieves this window's current background color.
   * @return current background color
   */
  public int getBackground() { return currentAnnotation.getBackground(); }
  /**
   * Retrieves this window's current foreground color.
   * @return current foreground color
   */
  public int getForeground() { return currentAnnotation.getForeground(); }
  /**
   * Begins a new text run with the specified annotation.
   * @param annotation the annotation for the text run
   */
  private void startNewAnnotatedRun(TextAnnotation annotation) {
    textBuffer.add(new AnnotatedText(currentAnnotation, currentRun.toString()));
    currentRun = new StringBuilder();
    currentAnnotation = annotation;
  }
  /**
   * Appends a character to the current text run.
   * @param zchar character to print
   */
  public void printChar(char zchar) {
    currentRun.append(zchar);
  }
  /**
   * Returns this window's buffer.
   * @return buffer
   */
  public List<AnnotatedText> getBuffer() {
    flush();
    List<AnnotatedText> result = textBuffer;
    textBuffer = new ArrayList<AnnotatedText>();
    return result;
  }

  /**
   * Flushes pending output into
   */
  private void flush() {
    if (currentRun.length() > 0) {
      textBuffer.add(new AnnotatedText(currentAnnotation,
                                       currentRun.toString()));
      currentRun = new StringBuilder();
    }
  }

  /**
   * Override toString().
   * @return the string representation
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (AnnotatedText str : textBuffer) {
      String line = str.getText().replace('\r', '\n');
      builder.append(line);
    }
    return builder.toString();
  }
}

