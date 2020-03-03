/*
 * $Id: ZCharEncoder.java,v 1.2 2006/04/12 02:04:29 weiju Exp $
 * 
 * Created on 2006/01/10
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
package org.zmpp.encoding;

import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.AlphabetTable.Alphabet;

/**
 * This class encodes ZSCII strings into dictionary encoded strings.
 * Since encoding is only needed from version 5, we can always assume
 * a target entry size of 6 bytes containing a maximum of nine characters.
 * Encoding is pretty difficult since there are several variables to
 * remember during the encoding process which would result in ugly code if
 * stored in member variables. We use the strategy of having an encoding
 * state for a target word which is changed and passed around until the
 * word can written out.
 * 
 * The encoding has some restrictions defined in the specification:
 * The target string is restricted to 6 bytes and 9 characters, which is
 * the length of dictionary entries and no abbreviations need to be taken
 * into consideration.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ZCharEncoder {

  /**
   * The alphabet table.
   */
  private ZCharTranslator translator;
  
  /**
   * The maximum entry length.
   */
  private static final int MAX_ENTRY_LENGTH = 9;
  
  private static final int NUM_TARGET_BYTES = 6;
  private static final int TARGET_LAST_WORD = 4;
  
  class EncodingState {
    
    public MemoryAccess memaccess;
    public int source;
    public int target;
    public int targetStart;
    public int currentWord;
    public int wordPosition;
  }
  
  public ZCharEncoder(final ZCharTranslator translator) {
 
    super();
    this.translator = translator;
  }
  
  public void encode(final MemoryAccess memaccess,
      final int sourceAddress, final int length, final int targetAddress) {

    final int maxlen = Math.min(length, MAX_ENTRY_LENGTH);
  
    final EncodingState state = new EncodingState();
    state.source = sourceAddress;
    state.target = targetAddress;
    state.targetStart = targetAddress;
    state.memaccess = memaccess;
    
    while (state.source < (sourceAddress + maxlen)) {
    
      processChar(state);     
    }
    
    // Padding
    // This pads the incomplete last encoded word
    if (state.wordPosition <= 2 && state.target <= (state.targetStart + 4)) {
      
      int resultword = state.currentWord;
      for (int i = state.wordPosition; i < 3; i++) {
    
        resultword = writeByteToWord(resultword, (short) 5, i);
      }
      state.memaccess.writeUnsignedShort(state.target, resultword);
      state.target += 2;
    }
    
    // If we did not encode 3 shorts, fill the rest with 0x14a5's
    final int targetOffset = state.target - targetAddress;
    for (int i = targetOffset; i < NUM_TARGET_BYTES; i+= 2) {
      
      //System.out.println("write padword: " + i);
      state.memaccess.writeUnsignedShort(targetAddress + i, 0x14a5);
    }
    
    // Always mark the last word as such, the last word is always
    // starting at the fifth byte
    final int lastword =
      memaccess.readUnsignedShort(targetAddress + TARGET_LAST_WORD);
    memaccess.writeUnsignedShort(targetAddress + TARGET_LAST_WORD,
                                 lastword | 0x8000);
  }
  
  private void processChar(final EncodingState state) {
    
    final short zsciiChar = state.memaccess.readUnsignedByte(state.source++);
    final AlphabetElement element = translator.getAlphabetElementFor(zsciiChar);
    if (element.getAlphabet() == null) {
     
      final short zcharCode = element.getZCharCode();
      
      // This is a ZMPP speciality, we do not want to end the string
      // in the middle of encoding, so we only encode if there is
      // enough space
      // how many slots left ?
      final int slotsleft = getSlotsLeft(state);
      if (slotsleft >= 4) {
      
        // Escape A2
        processWord(state, (short) 5);
        processWord(state, (short) 6);
        processWord(state, getUpper5Bit(zcharCode));
        processWord(state, getLower5Bit(zcharCode));
      
      } else {
              
        for (int i = 0; i < slotsleft; i++) {
          
          processWord(state, (short) 5);
        }
      }
      
    } else {
        
      final Alphabet alphabet = element.getAlphabet();
      final short zcharCode = element.getZCharCode();
      
      if (alphabet == Alphabet.A1) {
        
        processWord(state, (short) 4);
        
      } else if (alphabet == Alphabet.A2) {

        processWord(state, (short) 5);
      }
      
      processWord(state, zcharCode);      
    }    
  }
  
  private int getSlotsLeft(final EncodingState state) {
    
    final int currentWord = (state.target - state.targetStart) / 2;
    return ((2 - currentWord) * 3) + (3 - state.wordPosition);
  }
  
  private void processWord(final EncodingState state, final short value) {
    
    state.currentWord = writeByteToWord(state.currentWord, value,
                                        state.wordPosition++);
    writeWordIfNeeded(state);
  }
  
  private void writeWordIfNeeded(final EncodingState state) {
    
    if (state.wordPosition > 2 && state.target <= (state.targetStart + 4)) {
      
      // Write the result and increment the target position
      state.memaccess.writeUnsignedShort(state.target, state.currentWord);
      state.target += 2;
      state.currentWord = 0;
      state.wordPosition = 0;
    }    
  }
  
  private short getUpper5Bit(final short zsciiChar) {
    
    return (short) ((zsciiChar >>> 5) & 0x1f);
  }
  
  private short getLower5Bit(final short zsciiChar) {
   
    return (short) (zsciiChar & 0x1f);
  }
  
  /**
   * This function sets a byte value to the specified position within
   * a word. There are three positions within a 16 bit word and the bytes
   * are truncated such that only the lower 5 bit are taken as values.
   * 
   * @param dataword the word to set
   * @param databyte the byte to set
   * @param pos a value between 0 and 2
   * @return the new word with the databyte set in the position
   */
  private static short writeByteToWord(final int dataword,
      final short databyte, final int pos) {
    
    final int shiftwidth = (2 - pos) * 5;
    return (short) (dataword | ((databyte & 0x1f) << shiftwidth));
  }
}
