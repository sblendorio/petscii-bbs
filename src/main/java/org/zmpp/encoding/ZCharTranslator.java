/*
 * $Id: ZCharTranslator.java,v 1.5 2006/04/12 18:00:08 weiju Exp $
 * 
 * Created on 2006/01/15
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

import org.zmpp.encoding.AlphabetTable.Alphabet;


/**
 * The Z char translator is central for Z char encoding and decoding.
 * We provide an abstract interface, so the decoding and encoding algorithms
 * can be based on this.
 * 
 * It is basically an alphabet table combined with a current alphabet and
 * depending on this state, decides, whether to shift or translate.
 * We want to have alphabet tables as stateless information providers,
 * so we can keep them fairly simple.
 * 
 * Shift characters will move the object into another alphabet for the
 * duration of one character. If the current alphabet is A2, willEscapeA2()
 * indicates that the given character escapes to 10bit translation, the
 * client is responsible to join those characters and the translator will
 * not do anything about it, since it can only handle bytes.
 * 
 * Shift lock characters are a little special: The object will remember
 * the shift locked state until a reset() is called, if a regular shift
 * occurs, the alphabet will be changed for one translation and will
 * return to the last locked state. Since the translation process employs
 * abbreviations and ZSCII-Escape-Sequences which are external to this
 * class, the method resetToLastAlphabet() is provided to reset the state
 * from the client after an external translation has been performed.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface ZCharTranslator extends Cloneable {

  /**
   * Resets the state of the translator. This should be called before
   * a new decoding is started to reset this object to its initial state.
   */
  void reset();
  
  /**
   * This method should be invoked within the decoding of one single string.
   * In story file versions >= 3 this is the same as invoking reset(), in
   * V1 and V2, the object will reset to the last shift-locked alphabet.
   */
  void resetToLastAlphabet();
  
  /**
   * Clones this object. Needed, since this object has a modifiable state.
   * 
   * @return a copy of this object
   * @throws CloneNotSupportedException should not happen
   */
  Object clone() throws CloneNotSupportedException;
  
  /**
   * Returns the current alphabet this object works in.
   * 
   * @return the current alphabet
   */
  Alphabet getCurrentAlphabet();
  
  /**
   * Translates the given zchar to a Unicode character.
   * 
   * @param zchar a z encoded character
   * @return a Unicode character
   */
  char translate(short zchar);
  
  /**
   * If this object is in alphabet A2 now, this function determines if the
   * given character is an A2 escape.
   * 
   * @param zchar the character
   * @return true if A2 escape, false otherwise
   */
  public boolean willEscapeA2(short zchar);
  
  /**
   * Return true if this the specified character is an abbreviation in the
   * current alphabet table.
   * 
   * @param zchar a Z encoded character
   * @return true if abbreviation, false otherwise
   */
  public boolean isAbbreviation(short zchar);

  /**
   * Provides a reverse translation. Given a ZSCII character, determine
   * the alphabet and the index to this alphabet. If alphabet in the
   * result is null, this is a plain ZSCII character.
   * 
   * @param zsciiChar a ZSCII character
   * @return the reverse translation
   */
  public AlphabetElement getAlphabetElementFor(short zsciiChar);
}
