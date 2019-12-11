/*
 * $Id: CommandHistory.java,v 1.3 2006/04/12 18:00:17 weiju Exp $
 * 
 * Created on 2006/03/10
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

import java.util.ArrayList;
import java.util.List;

import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.encoding.ZsciiString;
import org.zmpp.vmutil.RingBuffer;

/**
 * This class implements a store for command lines. The history is a ring
 * buffer stored in an array. This is done to prevent that the history is
 * too big, resulting in a big inefficient thing just to maintain the list
 * of entries.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CommandHistory {

  private static final int NUM_ENTRIES = 5;
  
  /**
   * The class HistoryEntry maintains input entries, there is both an original
   * line and a
   */
  private class HistoryEntry {
  
    public List<Short> original;
    public List<Short> edited;
    public HistoryEntry() {
      
      original = new ArrayList<Short>();
      edited = new ArrayList<Short>();
    }
    
    public String toString() {
      
      short[] orig = new short[original.size()];
      short[] edit = new short[edited.size()];
      
      for (int i = 0; i < original.size(); i++) {
        orig[i] = original.get(i);
      }
      
      for (int i = 0; i < edited.size(); i++) {
        edit[i] = edited.get(i);
      }
      
      final StringBuilder buffer = new StringBuilder();
      buffer.append(" (" + new ZsciiString(orig));
      buffer.append(", ");
      buffer.append(new ZsciiString(edit) + " )");
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
  public boolean isHistoryChar(final short zsciiChar) {
    
    return zsciiChar == ZsciiEncoding.CURSOR_UP
           || zsciiChar == ZsciiEncoding.CURSOR_DOWN;
  }
  
  /**
   * Resets the index of the history to the last entry.
   */
  public void reset() {
    
    historySizeAtReset = historyIndex = history.size();
    for (int i = 0; i < history.size(); i++) {
      
      final HistoryEntry entry = history.get(i);
      entry.edited.clear();
      entry.edited.addAll(entry.original);
    }
    //System.out.println("reset(), History: " + history.toString());
  }
  
  /**
   * Adds an input line to the history.
   * 
   * @param inputbuffer the input buffer
   */
  public void addInputLine(final List<Short> inputbuffer) {
    
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
    //System.out.println("addInputLine(), History: " + history.toString());
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
  public int switchHistoryEntry(final List<Short> inputbuffer,
      final int textbuffer, final int pointer, final short zsciiChar) {

    if (zsciiChar == ZsciiEncoding.CURSOR_UP) {
      
      return processHistoryUp(inputbuffer, textbuffer, pointer);
      
    } else {
      
      return processHistoryDown(inputbuffer, textbuffer, pointer);
    }
  }
  
  private int processHistoryUp(final List<Short> inputbuffer,
      final int textbuffer, final int pointer) {
    
    int newpointer = pointer;
    if (historyIndex > 0) {
      
      storeCurrentInput(inputbuffer);
      //System.out.println("historyUp(), History: " + history.toString());
      historyIndex--;
      newpointer = fillInputLineFromHistory(inputbuffer, textbuffer, pointer);      
    }
    return newpointer;
  }
  
  private int processHistoryDown(final List<Short> inputbuffer, 
      final int textbuffer, final int pointer) {

    int newpointer = pointer;
    if (historyIndex < history.size() - 1) {
      
      storeCurrentInput(inputbuffer);
      //System.out.println("historyDown(), History: " + history.toString());
      historyIndex++;
      newpointer = fillInputLineFromHistory(inputbuffer, textbuffer,
                                            pointer);      
    }
    return newpointer;
  }
  
  private int fillInputLineFromHistory(final List<Short> inputbuffer,
      final int textbuffer, final int pointer) {
    
    int newpointer = deleteInputLine(inputbuffer, pointer);
    if (history.size() > historyIndex) {
      
      final List<Short> input = history.get(historyIndex).edited;
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
   * 
   * @param inputbuffer the input buffer
   */
  private void storeCurrentInput(final List<Short> inputbuffer) {
    
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
   * 
   * @param inputbuffer the input buffer
   * @param pointer the pointer in the text buffer
   * @return the new pointer in the text buffer
   */
  private int deleteInputLine(final List<Short> inputbuffer,
      final int pointer) {

    final int n = inputbuffer.size();
    int newpointer = pointer;
    
    for (int i = 0; i < n; i++) {
      
      newpointer = inputline.deletePreviousChar(inputbuffer, newpointer);
    }
    return newpointer;
  }  
}
