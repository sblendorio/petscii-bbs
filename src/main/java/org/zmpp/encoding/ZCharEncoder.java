/*
 * Created on 2006/01/10
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
package org.zmpp.encoding;

import static org.zmpp.base.MemoryUtil.toUnsigned16;

import org.zmpp.base.Memory;
import org.zmpp.encoding.AlphabetTable.Alphabet;

/**
 * This class encodes ZSCII strings into dictionary encoded strings.
 * Encoding is pretty difficult since there are several variables to
 * remember during the encoding process. We use the State pattern passing
 * around the encoding state for a target word until encoding is complete.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class ZCharEncoder {

  private static final char PAD_CHAR = 5;
  private static final int SLOTS_PER_WORD16 = 3;
  private ZCharTranslator translator;
  private DictionarySizes dictionarySizes;

  /**
   * Constructor.
   * @param aTranslator ZCharTranslator object
   * @param dictSizes DictionarySizes object
   */
  public ZCharEncoder(final ZCharTranslator aTranslator,
                      final DictionarySizes dictSizes) {
    this.translator = aTranslator;
    this.dictionarySizes = dictSizes;
  }

  /**
   * Encodes the Z word at the specified memory address and writes the encoded
   * for to the target address, using the specified word length.
   * @param memory Memory object
   * @param sourceAddress source address
   * @param length Z-word length
   * @param targetAddress target address
   */
  public void encode(final Memory memory,
      final int sourceAddress, final int length, final int targetAddress) {
    final int maxlen = Math.min(length, dictionarySizes.getMaxEntryChars());
    final EncodingState state = new EncodingState();
    state.init(memory, sourceAddress, targetAddress,
               dictionarySizes.getNumEntryBytes(), maxlen);
    encode(state, translator);
  }

  /**
   * Encodes the specified Z-word contained in the String and writes it to the
   * specified target address.
   * @param str input string
   * @param memory Memory object
   * @param targetAddress target address
   */
  public void encode(final String str, final Memory memory,
                     final int targetAddress) {
    final StringEncodingState state = new StringEncodingState();
    state.init(str, memory, targetAddress, dictionarySizes);
    encode(state, translator);
  }

  /**
   * Encodes the string at the specified address and writes it to the target
   * address.
   * @param state EncodingState
   * @param translator ZCharTranslator
   */
  private static void encode(EncodingState state, ZCharTranslator translator) {
    while (state.hasMoreInput()) {
      processChar(translator, state);
    }
    // Padding
    // This pads the incomplete currently encoded word
    if (!state.currentWordWasProcessed() && !state.atLastWord16()) {
      int resultword = state.currentWord;
      for (int i = state.wordPosition; i < SLOTS_PER_WORD16; i++) {
        resultword = writeZcharToWord(resultword, PAD_CHAR, i);
      }
      state.writeUnsigned16(toUnsigned16(resultword));
    }

    // If we did not encode 3 16-bit words, fill the remaining ones with
    // 0x14a5's (= 0-{5,5,5})
    while (state.getTargetOffset() < state.getNumEntryBytes()) {
      state.writeUnsigned16(toUnsigned16(0x14a5));
    }

    // Always mark the last word as such
    state.markLastWord();
  }

  /**
   * Processes the current character.
   * @param translator ZCharTranslator object
   * @param state the EncodingState
   */
  private static void processChar(ZCharTranslator translator,
                                  final EncodingState state) {
    final char zsciiChar = state.nextChar();
    final AlphabetElement element = translator.getAlphabetElementFor(zsciiChar);
    if (element.getAlphabet() == null) {
      final char zcharCode = element.getZCharCode();
      // This is a ZMPP specialty, we do not want to end the string
      // in the middle of encoding, so we only encode if there is
      // enough space in the target (4 5-bit slots are needed to do an
      // A2-escape).
      // We might want to reconsider this, let's see, if there are problems
      // with different dictionaries
      final int numRemainingSlots = getNumRemainingSlots(state);
      if (numRemainingSlots >= 4) {
        // Escape A2
        processWord(state, AlphabetTable.SHIFT_5);
        processWord(state, AlphabetTable.A2_ESCAPE);
        processWord(state, getUpper5Bit(zcharCode));
        processWord(state, getLower5Bit(zcharCode));
      } else {
        // pad remaining slots with SHIFT_5's
        for (int i = 0; i < numRemainingSlots; i++) {
          processWord(state, AlphabetTable.SHIFT_5);
        }
      }
    } else {
      final Alphabet alphabet = element.getAlphabet();
      final char zcharCode = element.getZCharCode();
      if (alphabet == Alphabet.A1) {
        processWord(state, AlphabetTable.SHIFT_4);
      } else if (alphabet == Alphabet.A2) {
        processWord(state, AlphabetTable.SHIFT_5);
      }
      processWord(state, zcharCode);
    }
  }

  /**
   * Returns the number of remaining slots.
   * @param state the EncodingState
   * @return number of remaining slots
   */
  private static int getNumRemainingSlots(final EncodingState state) {
    final int currentWord = state.getTargetOffset() / 2;
    return ((2 - currentWord) * 3) + (3 - state.wordPosition);
  }

  /**
   * Processes the current word.
   * @param state the EncodingState
   * @param value the char value
   */
  private static void processWord(final EncodingState state, final char value) {
    state.currentWord = writeZcharToWord(state.currentWord, value,
                                         state.wordPosition++);
    writeWordIfNeeded(state);
  }

  /**
   * Writes the current word if needed.
   * @param state the EncodingState
   */
  private static void writeWordIfNeeded(final EncodingState state) {
    if (state.currentWordWasProcessed() && !state.atLastWord16()) {
      // Write the result and increment the target position
      state.writeUnsigned16(toUnsigned16(state.currentWord));
      state.currentWord = 0;
      state.wordPosition = 0;
    }
  }

  /**
   * Retrieves the upper 5 bit of the specified ZSCII character.
   * @param zsciiChar the ZSCII character
   * @return the upper 5 bit
   */
  private static char getUpper5Bit(final char zsciiChar) {
    return (char) ((zsciiChar >>> 5) & 0x1f);
  }

  /**
   * Retrieves the lower 5 bit of the specified ZSCII character.
   * @param zsciiChar the ZSCII character
   * @return the lower 5 bit
   */
  private static char getLower5Bit(final char zsciiChar) {
    return (char) (zsciiChar & 0x1f);
  }

  /**
   * This function sets a zchar value to the specified position within
   * a word. There are three positions within a 16 bit word and the bytes
   * are truncated such that only the lower 5 bit are taken as values.
   *
   * @param dataword the word to set
   * @param zchar the character to set
   * @param pos a value between 0 and 2
   * @return the new word with the databyte set in the position
   */
  private static char writeZcharToWord(final int dataword,
      final char zchar, final int pos) {
    final int shiftwidth = (2 - pos) * 5;
    return (char) (dataword | ((zchar & 0x1f) << shiftwidth));
  }
}

/**
 * EncodingState class.
 */
class EncodingState {
  private Memory memory;
  protected int source;
  private int sourceStart;
  private int maxLength;
  private int numEntryBytes;
  private int target;
  private int targetStart;

  // currently public
  // currentWord represents the state of the current word the encoder is
  // working on. The encoder attempts to fill the three slots contained in
  // this word and later writes it to the target memory address
  public int currentWord;
  // The current slot position within currentWord, can be 0, 1 or 2
  public int wordPosition;

  /**
   * Initialization.
   * @param mem memory object
   * @param src source position
   * @param trgt target position
   * @param maxEntryBytes maximum entry bytes
   * @param maxEntryChars maximum entry characters
   */
  public void init(Memory mem, int src, int trgt, int maxEntryBytes,
                   int maxEntryChars) {
    memory = mem;
    source = src;
    sourceStart = src;
    target = trgt;
    targetStart = trgt;
    numEntryBytes = maxEntryBytes;
    maxLength = maxEntryChars;
  }
  /**
   * Indicates whether the current word was already processed.
   * @return true if word was processed
   */
  public boolean currentWordWasProcessed() { return wordPosition > 2; }

  /**
   * Returns the target offset.
   * @return target offset
   */
  public int getTargetOffset() { return target - targetStart; }

  /**
   * Returns the number of entry bytes.
   * @return number of entry bytes
   */
  public int getNumEntryBytes() { return numEntryBytes; }

  /**
   * Determines whether we are already at the last 16-bit word.
   * @return true if at the end, false else
   */
  public boolean atLastWord16() {
    return target > targetStart + getLastWord16Offset();
  }
  /**
   * Returns the offset of the last 16 bit word.
   * @return offset of the last 16 bit word
   */
  private int getLastWord16Offset() { return numEntryBytes - 2; }
  /**
   * Returns the next character.
   * @return next character
   */
  public char nextChar() { return memory.readUnsigned8(source++); }
  /**
   * Marks the last word.
   */
  public void markLastWord() {
    final int lastword =
      memory.readUnsigned16(targetStart + getLastWord16Offset());
    memory.writeUnsigned16(targetStart + getLastWord16Offset(),
                           toUnsigned16(lastword | 0x8000));
  }
  /**
   * Writes the specified 16 bit value to the current memory address.
   * @param value the value to write
   */
  public void writeUnsigned16(char value) {
    memory.writeUnsigned16(target, value);
    target += 2;
  }
  /**
   * Determines whether there is more input.
   * @return true if more input, false otherwise
   */
  public boolean hasMoreInput() {
    return source < sourceStart + maxLength;
  }
}

/**
 * Representation of StringEncodingState.
 */
class StringEncodingState extends EncodingState {
  private String input;
  /**
   * Initialization.
   * @param inputStr input string
   * @param mem memory object
   * @param trgt target position
   * @param dictionarySizes DictionarySizes object
   */
  public void init(String inputStr, Memory mem, int trgt,
                   DictionarySizes dictionarySizes) {
    super.init(mem, 0, trgt, dictionarySizes.getNumEntryBytes(),
               Math.min(inputStr.length(), dictionarySizes.getMaxEntryChars()));
    input = inputStr;
  }
  /**
   * Retrieve to next character.
   * @return next character
   */
  public char nextChar() { return input.charAt(source++); }
}

