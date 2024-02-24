/*
 * Created on 2006/03/10
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
package org.zmpp.vm;

import java.util.ArrayList;
import java.util.List;

import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.vmutil.RingBuffer;

/**
 * This class implements a store for command lines. The history is a ring
 * buffer stored in an array. This is done to prevent that the history is
 * too big, resulting in a big inefficient thing just to maintain the list
 * of entries.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class CommandHistory {

  private static final int NUM_ENTRIES = 5;

  /**
   * The class HistoryEntry maintains input entries, there is both an original
   * line and a
   */
  private static class HistoryEntry {

    public List<Character> original;
    public List<Character> edited;

    /**
     * Constructor.
     */
    public HistoryEntry() {
      original = new ArrayList<Character>();
      edited = new ArrayList<Character>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

      char[] orig = new char[original.size()];
      char[] edit = new char[edited.size()];

      for (int i = 0; i < original.size(); i++) {
        orig[i] = original.get(i);
      }

      for (int i = 0; i < edited.size(); i++) {
        edit[i] = edited.get(i);
      }

      final StringBuilder buffer = new StringBuilder();
      buffer.append(" (" + new String(orig));
      buffer.append(", ");
      buffer.append(new String(edit) + " )");
      return buffer.toString();
    }
  }

  private RingBuffer<HistoryEntry> history =
      new RingBuffer<HistoryEntry>(NUM_ENTRIES);

  private int historyIndex;
  private int historySizeAtReset;
  private InputLine inputline;

  /**
   * Constructor.
   *
   * @param inputline the input line object
   */
  public CommandHistory(InputLine inputline) {

    this.inputline = inputline;
  }

  /**
   * Returns the current history index.
   *
   * @return the history index
   */
  public int getCurrentIndex() { return historyIndex; }

  /**
   * Returns true if history char, false otherwise.
   *
   * @param zsciiChar the character
   * @return true if history character
   */
  public boolean isHistoryChar(final char zsciiChar) {
    return zsciiChar == ZsciiEncoding.CURSOR_UP
           || zsciiChar == ZsciiEncoding.CURSOR_DOWN;
  }

  /**
   * Resets the index of the history to the last entry.
   */
  public void reset() {
    int historySize = history.size();
    historySizeAtReset = historySize;
    historyIndex = historySize;
    for (int i = 0; i < historySize; i++) {
      final HistoryEntry entry = history.get(i);
      entry.edited.clear();
      entry.edited.addAll(entry.original);
    }
  }

  /**
   * Adds an input line to the history.
   * @param inputbuffer the input buffer
   */
  public void addInputLine(final List<Character> inputbuffer) {
    final HistoryEntry entry = new HistoryEntry();
    entry.original.addAll(inputbuffer);
    entry.edited.addAll(inputbuffer);

    if (history.size() > historySizeAtReset) {
      // If the history was invoked, the last edit line is also included
      // in the input, in this case, replace it with the final input line
      history.set(history.size() - 1, entry);
    } else {
      // If the history was not invoked, simply add the input to the end of
      // the history list
      history.add(entry);
    }
  }

  /**
   * Deletes the current line and replaces it with a history entry, which
   * is determined depending on the input character.
   *
   * @param inputbuffer the input buffer
   * @param textbuffer the text buffer address
   * @param pointer the pointer in the text buffer
   * @param zsciiChar the character
   * @return the new pointer in the text buffer
   */
  public int switchHistoryEntry(final List<Character> inputbuffer,
      final int textbuffer, final int pointer, final char zsciiChar) {
    if (zsciiChar == ZsciiEncoding.CURSOR_UP) {
      return processHistoryUp(inputbuffer, textbuffer, pointer);
    } else {
      return processHistoryDown(inputbuffer, textbuffer, pointer);
    }
  }

  /**
   * Retrieve previous history entry.
   * @param inputbuffer input buffer
   * @param textbuffer text buffer
   * @param pointer memory pointer
   * @return new memory pointer
   */
  private int processHistoryUp(final List<Character> inputbuffer,
      final int textbuffer, final int pointer) {
    int newpointer = pointer;
    if (historyIndex > 0) {
      storeCurrentInput(inputbuffer);
      historyIndex--;
      newpointer = fillInputLineFromHistory(inputbuffer, textbuffer, pointer);
    }
    return newpointer;
  }

  /**
   * Retrieve next entry in the history.
   * @param inputbuffer input buffer
   * @param textbuffer text buffer
   * @param pointer memory pointer
   * @return new memory pointer
   */
  private int processHistoryDown(final List<Character> inputbuffer,
      final int textbuffer, final int pointer) {
    int newpointer = pointer;
    if (historyIndex < history.size() - 1) {
      storeCurrentInput(inputbuffer);
      historyIndex++;
      newpointer = fillInputLineFromHistory(inputbuffer, textbuffer,
                                            pointer);
    }
    return newpointer;
  }

  /**
   * Put history text into the input line.
   * @param inputbuffer input buffer
   * @param textbuffer text buffer
   * @param pointer memory pointer
   * @return new memory pointer
   */
  private int fillInputLineFromHistory(final List<Character> inputbuffer,
      final int textbuffer, final int pointer) {
    int newpointer = deleteInputLine(inputbuffer, pointer);
    if (history.size() > historyIndex) {
      final List<Character> input = history.get(historyIndex).edited;
      for (int i = 0; i < input.size(); i++) {
        newpointer = inputline.addChar(inputbuffer, textbuffer, newpointer,
                                       input.get(i));
      }
    }
    return newpointer;
  }

  /**
   * Replaces the current history entry with the content of the input
   * buffer.
   * @param inputbuffer the input buffer
   */
  private void storeCurrentInput(final List<Character> inputbuffer) {
    if (historyIndex < history.size()) {
      // Replace the edited thang
      history.get(historyIndex).edited.clear();
      history.get(historyIndex).edited.addAll(inputbuffer);
    } else {
      final HistoryEntry entry = new HistoryEntry();
      entry.original.addAll(inputbuffer);
      entry.edited.addAll(inputbuffer);
      history.add(entry);
    }
  }

  /**
   * Removes the text from the current input line.
   * @param inputbuffer the input buffer
   * @param pointer the pointer in the text buffer
   * @return the new pointer in the text buffer
   */
  private int deleteInputLine(final List<Character> inputbuffer,
      final int pointer) {
    final int n = inputbuffer.size();
    int newpointer = pointer;

    for (int i = 0; i < n; i++) {
      newpointer = inputline.deletePreviousChar(inputbuffer, newpointer);
    }
    return newpointer;
  }
}
