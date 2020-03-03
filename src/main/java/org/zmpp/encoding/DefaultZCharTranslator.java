/*
 * $Id: DefaultZCharTranslator.java,v 1.7 2006/04/12 18:00:08 weiju Exp $
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
 * The default implementation of ZCharTranslator. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DefaultZCharTranslator implements Cloneable, ZCharTranslator {

  /**
   * The alphabet used for this game.
   */
  private AlphabetTable alphabetTable;
  
  /**
   * The current alphabet in this translator object.
   */
  private Alphabet currentAlphabet;
  
  /**
   * The shift lock flag.
   */
  private Alphabet lockAlphabet;
  
  /**
   * The lock flag.
   */
  private boolean shiftLock;
 
  /**
   * Constructor.
   * 
   * @param alphabetTable the alphabet table
   */
  public DefaultZCharTranslator(final AlphabetTable alphabetTable) {
    
    super();
    this.alphabetTable = alphabetTable;
    reset();
  }
  
  /**
   * {@inheritDoc}
   */
  public final void reset() {
    
    currentAlphabet = Alphabet.A0;
    lockAlphabet = null;
    shiftLock = false;
  }
  
  public void resetToLastAlphabet() {
    
    if (lockAlphabet == null) {
      
      currentAlphabet = Alphabet.A0;
      
    } else {
      
      currentAlphabet = lockAlphabet;
      shiftLock = true;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public Object clone() throws CloneNotSupportedException {
    
    DefaultZCharTranslator clone = null;
    clone = (DefaultZCharTranslator) super.clone();
    clone.reset();
    return clone;
  }
  
  /**
   * {@inheritDoc}
   */
  public Alphabet getCurrentAlphabet() {
    
    return currentAlphabet;
  }
  
  /**
   * {@inheritDoc}
   */
  public char translate(final short zchar) {
    
    // Handle the shift
    if (shift(zchar)) {
      return '\0';
    }

    char result;
    
    if (isInAlphabetRange(zchar)) {
      
      switch (currentAlphabet) {
    
        case A0:
          result = (char) alphabetTable.getA0Char((byte) zchar);
          break;
        case A1:
          result = (char) alphabetTable.getA1Char((byte) zchar);
          break;
        case A2:
        default:            
          result = (char) alphabetTable.getA2Char((byte) zchar);
          break;
      }
      
    } else {
      
      //System.out.printf("not handled : %d\n", zchar);
      result = '?';
    }
    
    // Only reset if the shift lock flag is not set
    if (!shiftLock) {
      
      resetToLastAlphabet();
    }
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean willEscapeA2(final short zchar) {
   
    return currentAlphabet == Alphabet.A2 && zchar == AlphabetTable.A2_ESCAPE;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isAbbreviation(final short zchar) {
    
    return alphabetTable.isAbbreviation(zchar);
  }

  /**
   * {@inheritDoc}
   */
  public AlphabetElement getAlphabetElementFor(final short zsciiChar) {
    
    // Special handling for newline !!
    if (zsciiChar == '\n') {
      
      return new AlphabetElement(Alphabet.A2, (short) 7);
    }
    
    Alphabet alphabet = null;
    int zcharCode = alphabetTable.getA0CharCode(zsciiChar);
    
    if (zcharCode >= 0) {
      
      alphabet = Alphabet.A0;
      
    } else {
      
      zcharCode = alphabetTable.getA1CharCode(zsciiChar);
      if (zcharCode >= 0) {
        
        alphabet = Alphabet.A1;
        
      } else {
        
        zcharCode = alphabetTable.getA2CharCode(zsciiChar);
        if (zcharCode >= 0) {
          alphabet = Alphabet.A2;
        }
      }
    }
    
    if (alphabet == null) {
      
      // It is not in any alphabet table, we are fine with taking the code
      // number for the moment
      zcharCode = zsciiChar;
    }
    
    return new AlphabetElement(alphabet, (short) zcharCode);
  }  

  /**
   * Determines if the given byte value falls within the alphabet range.
   * 
   * @param zchar the zchar value
   * @return true if the value is in the alphabet range, false, otherwise
   */
  private static boolean isInAlphabetRange(final short zchar) {
    
    return 0 <= zchar && zchar <= AlphabetTable.ALPHABET_END;
  }
  
  /**
   * Performs a shift.
   * 
   * @param zchar a z encoded character
   * @return true if a shift was performed, false, otherwise
   */
  private boolean shift(final short zchar) {
  
    if (alphabetTable.isShift(zchar)) {
      
      currentAlphabet = shiftFrom(currentAlphabet, zchar);
      
      // Sets the current lock alphabet
      if (alphabetTable.isShiftLock(zchar)) {
      
        lockAlphabet = currentAlphabet;
      }
      return true;
    }
    return false;
  }
  
  /**
   * This method contains the rules to shift the alphabets.
   * 
   * @param alphabet the source alphabet
   * @param shiftChar the shift character
   * @return the resulting alphabet
   */
  private Alphabet shiftFrom(final Alphabet alphabet, final short shiftChar) {

    Alphabet result = null;
    
    if (alphabetTable.isShift1(shiftChar)) {
      
      if (alphabet == Alphabet.A0) {
        
        result = Alphabet.A1;
      
      } else if (alphabet == Alphabet.A1) {
      
        result = Alphabet.A2;
      
      } else if (alphabet == Alphabet.A2) {
      
        result = Alphabet.A0;
      }
      
    } else if (alphabetTable.isShift2(shiftChar)) {
      
      if (alphabet == Alphabet.A0) {
        
        result = Alphabet.A2;
      
      } else if (alphabet == Alphabet.A1) {
      
        result = Alphabet.A0;
        
      } else if (alphabet == Alphabet.A2) {
      
        result = Alphabet.A1;
      }
      
    } else {
      
      result = alphabet;
    }
    
    shiftLock = alphabetTable.isShiftLock(shiftChar); 
    
    return result;
  }
}
