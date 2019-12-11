/*
 * $Id: InputFunctions.java,v 1.13 2006/05/31 23:14:48 weiju Exp $
 * 
 * Created on 12/22/2005
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.ZCharDecoder;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.encoding.ZsciiString;
import org.zmpp.encoding.ZsciiStringBuilder;
import org.zmpp.encoding.ZsciiStringTokenizer;


/**
 * This class contains functions that deal with user input.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class InputFunctions implements InputLine {

  private CommandHistory history;  
  private Machine machine;
  private static final ZsciiString WHITESPACE =
    new ZsciiString(new short[] { ' ', '\n', '\t', '\r' });

  /**
   * This class represents the interrupt method in timed input.
   */
  public class InterruptThread extends Thread {
  
    /**
     * The interval in milliseconds.
     */
    private int time;
    
    /**
     * The packed routine address.
     */
    private int routineAddress;
    
    /**
     * The input buffer.
     */
    private List<Short> inputbuffer;
    
    /**
     * Status variable.
     */ 
    public boolean running;
    
    public InterruptThread(int time, int routineAddress,
                           List<Short> inputbuffer) {
      
      this.time = time;
      this.routineAddress = routineAddress;
      this.inputbuffer = inputbuffer;
      this.running = true;
    }
    
    public synchronized boolean isRunning() {
      
      return running;
    }
    
    public synchronized void terminate() {
      
      running = false;
      
      // note: interrupt() can only be executed in a signed applet
      interrupt();
      try { join(); } catch (Exception ex) {
        
        ex.printStackTrace(System.err);
      }
    }
    
    public void run() {
      
      final Output output = machine.getOutput();
      
      while (isRunning()) {
        
        // We sleep for the given time
        try { Thread.sleep(time); } catch (InterruptedException ex) { }
        
        if (isRunning()) {
          displayCursor(false);
          final short retval = machine.getCpu().callInterrupt(routineAddress);
          if (retval == 1) {
          
            machine.getInput().getSelectedInputStream().cancelInput();
            break;
          }
        
          // REDISPLAY INPUT HERE
          // We need to find out if the routine has printed anything to
          // the screen if yes, the input needs to be redisplayed
          if (inputbuffer != null
              && machine.getCpu().interruptDidOutput()) {
          
            for (short zsciiChar : inputbuffer) {
            
              output.printZsciiChar(zsciiChar, false);
            }
          }
          output.flushOutput();
          displayCursor(true);
        }
      }
    }    
  }
  
  /**
   * Constructor.
   * 
   * @param machine the machine object
   */
  public InputFunctions(Machine machine) {
    
    this.machine = machine;
    this.history = new CommandHistory(this);
  }

  // *********************************************************************
  // ****** SREAD/AREAD - the most complex and flexible function within the
  // ****** Z-machine. This function takes input from the user and
  // ****** calls the tokenizer for lexical analysis. It also recognizes
  // ****** terminator characters and controls the output as well as
  // ****** calling an optional interrupt routine.
  // *********************************************************************
  
  /**
   * {@inheritDoc}
   */
  public short readLine(final int textbuffer, final int time,
      final int routineAddress) {

    machine.getOutput().flushOutput();
    displayCursor(true);
    
    // Using a synchronized list
    final List<Short> inputbuffer =
      Collections.synchronizedList(new ArrayList<Short>());
    history.reset();
    
    final int pointer = checkForPreviousInput(textbuffer, inputbuffer);    
    
    // Timed input
    final InterruptThread thread = startInterruptThread(routineAddress, time,
        inputbuffer);
    
    final short terminateChar = doInputLoop(textbuffer, pointer, inputbuffer);
    storeInput(inputbuffer, terminateChar);
    terminateInterruptThread(thread);    
    displayCursor(false);
    
    return handleTerminateChar(terminateChar);
  }
  
  /**
   * {@inheritDoc}
   */
  public int deletePreviousChar(final List<Short> inputbuffer,
      final int pointer) {
    
    // Decrement the buffer pointer
    int newpointer = pointer;
    if (inputbuffer.size() > 0) {
    
      final short deleteChar = inputbuffer.remove(inputbuffer.size() - 1);
      newpointer--;
      machine.getOutput().deletePreviousZsciiChar(deleteChar);
    }
    return newpointer;
  }
  
  /**
   * {@inheritDoc}
   */
  public int addChar(final List<Short> inputbuffer, final int textbuffer,
      final int pointer, final short zsciiChar) {
    
    // Do not include the terminator in the buffer
    // Note: we convert ASCII characters to lower case to allow the
    // transkription of umlauts
    int newpointer = pointer;
    final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();    
    final ZsciiEncoding encoding = machine.getGameData().getZsciiEncoding();
    memaccess.writeUnsignedByte(textbuffer + newpointer,
                                encoding.toLower(zsciiChar));
    inputbuffer.add(zsciiChar);
    newpointer++;
    machine.getOutput().printZsciiChar(zsciiChar, true);
    return newpointer;
  }
  
  /**
   * This method checks the buffer for previous input.
   * 
   * @param textbuffer the text buffer
   * @param inputbuffer the input buffer
   * @return
   */
  public int checkForPreviousInput(final int textbuffer,
      final List<Short> inputbuffer) {
    
    final int version = machine.getGameData().getStoryFileHeader().getVersion();

    // We determine the start of the input here
    // From V5, the first byte contains the number of characters typed
    // so we skip that first byte      
    final int textbufferstart = determineTextBufferStart(version);
    int pointer = textbufferstart;
    
    if (version >= 5) {
      
      final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
      
      // The clunky feature to include previous input into the current input
      // Simply adjust the pointer, the differencing at the end of the
      // function will then calculate the total
      int numCharactersTyped = memaccess.readByte(textbuffer + 1);
      if (numCharactersTyped < 0) {
        
        numCharactersTyped = 0;
      }
      if (numCharactersTyped > 0) {
        
        for (int i = 0; i < numCharactersTyped; i++) {
          
          final short zsciichar = memaccess.readUnsignedByte(
              textbuffer + textbufferstart + i);
          inputbuffer.add(zsciichar);
        }
      }
      pointer += numCharactersTyped;
    }
    return pointer;
  }
  
  /**
   * Depending on the terminating character and the story file version,
   * either write a 0 to the end of the text buffer or write the length
   * of to the text buffer's first byte.
   * 
   * @param terminateChar the terminating character
   * @param textbuffer the text buffer
   * @param textpointer points at the position behind the last input char
   */
  public void checkTermination(final short terminateChar, final int textbuffer,
                               final int textpointer) {
    
    final int version = machine.getGameData().getStoryFileHeader().getVersion();
    final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
    
    if (version >= 5) {
      
      // Check if was cancelled
      final byte numCharsTyped = (terminateChar == ZsciiEncoding.NULL) ?
          0 : (byte) (textpointer - 2);
          
      // Write the number of characters typed in byte 1
      memaccess.writeUnsignedByte(textbuffer + 1, numCharsTyped);
      
    } else {
      
      // Terminate with 0 byte in versions < 5
      // Check if input was cancelled
      int terminatepos = textpointer;
      if (terminateChar == ZsciiEncoding.NULL) {
        terminatepos = 0;
      }
      memaccess.writeByte(textbuffer + terminatepos, (byte) 0);
    }    
  }
  
  public InterruptThread startInterruptThread(final int routineAddress,
      final int time, final List<Short> inputbuffer) {
    
    InterruptThread thread = null;
    final int version = machine.getGameData().getStoryFileHeader().getVersion();
    
    if (version >= 4 && time > 0 && routineAddress != 0) {
      
      final double dtime = ((double) time) / 10.0 * 1000.0;
      thread = new InterruptThread((int) dtime, routineAddress, inputbuffer);
      thread.start();
    }
    return thread;
  }
  
  public void terminateInterruptThread(final InterruptThread thread) {
    
    // Synchronize with timed input thread    
    if (thread != null) {
      
      thread.terminate();
    }    
  }

  /**
   * This is the main input loop.
   * 
   * @param textbuffer the text buffer address
   * @param pointerstart the offset of the text pointer start, either 0 or 1
   * @param pointer the starting pointer including previous input
   * @param inputbuffer the input buffer
   * @return the terminating character
   */
  public short doInputLoop(final int textbuffer, final int pointer,
                           final List<Short> inputbuffer) {
    
    short zsciiChar;
    final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
    final int bufferlen = memaccess.readUnsignedByte(textbuffer);
    int newpointer = pointer;
    boolean flushBeforeGet = true;

    do {
      
      zsciiChar = machine.getInput().getSelectedInputStream().getZsciiChar(flushBeforeGet);
      flushBeforeGet = false; // all subsequent input should not flush the buffer
      displayCursor(false);
      
      if (zsciiChar == ZsciiEncoding.DELETE) {
   
        newpointer = deletePreviousChar(inputbuffer, newpointer);
        
      } else if (!isTerminatingCharacter(zsciiChar)) {
        
        if (history.isHistoryChar(zsciiChar)) {
         
          newpointer = history.switchHistoryEntry(inputbuffer, textbuffer,
                                                  newpointer, zsciiChar);
                    
        } else {
    
          newpointer = addChar(inputbuffer, textbuffer, newpointer,
                               zsciiChar);
        }
      }
      displayCursor(true);
      
    } while (!isTerminatingCharacter(zsciiChar) && newpointer < bufferlen - 1);
    
    checkTermination(zsciiChar, textbuffer, newpointer);    
    return zsciiChar;
  }
  
  private boolean isTerminatingCharacter(final short zsciiChar) {
    
    return isFileHeaderTerminator(zsciiChar) 
           || zsciiChar == ZsciiEncoding.NEWLINE
           || zsciiChar == ZsciiEncoding.NULL;
  }
  
  private boolean isFileHeaderTerminator(final short zsciiChar) {
    
    final StoryFileHeader fileheader = machine.getGameData().getStoryFileHeader();
    if (fileheader.getVersion() >= 5) {
   
      final int terminatorTable = fileheader.getTerminatorsAddress();
      if (terminatorTable == 0) {
        return false;
      }
    
      // Check the terminator table
      final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
      short terminator;
    
      for (int i = 0; ; i++) {
      
        terminator = memaccess.readUnsignedByte(terminatorTable + i);
        if (terminator == 0) {
          break;
        }
        if (terminator == 255) {
          return ZsciiEncoding.isFunctionKey(zsciiChar);
        }
        if (terminator == zsciiChar) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Depending on the terminating character, return the terminator to
   * the caller. We need this since aread stores the terminating character
   * as a result. If a newline was typed as the terminator, a newline
   * will be echoed, in all other cases, the terminator is simply returned.
   * 
   * @param terminateChar the terminating character
   * @return a terminating character that can be stored as a result
   */
  public short handleTerminateChar(final short terminateChar) {
    
    if (terminateChar == ZsciiEncoding.NEWLINE) {
      
      // Echo a newline into the streams
      // must be called with isInput == false since we are not
      // in input mode anymore when we receive NEWLINE
      machine.getOutput().printZsciiChar(ZsciiEncoding.NEWLINE, false);      
    }      
    return terminateChar;
  }
  
  // **********************************************************************
  // ****** READ_CHAR
  // *******************************
  /**
   * {@inheritDoc}
   */
  public short readChar(final int time, final int routineAddress) {
    
    machine.getOutput().flushOutput();
    displayCursor(true);
    
    InterruptThread thread = startInterruptThread(routineAddress, time, null);
    final short result =
      machine.getInput().getSelectedInputStream().getZsciiChar(true);
    //System.out.println("readChar(): " + result);
    
    terminateInterruptThread(thread);
    displayCursor(false);
    
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  public void tokenize(final int textbuffer, final int parsebuffer,
                       final int dictionaryAddress, final boolean flag) {
    
    final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
    //System.out.printf("tokenize(), textbuffer: %x, parsebuffer: %x, " +
    //    "avail: %d, typed: %d\n", textbuffer, parsebuffer,
    //    memaccess.readUnsignedByte(textbuffer + 0),
    //    memaccess.readUnsignedByte(textbuffer + 1));
    Dictionary dictionary = machine.getGameData().getDictionary();
    if (dictionaryAddress > 0) {
      
      dictionary = new UserDictionary(memaccess, dictionaryAddress,
                         machine.getGameData().getZCharDecoder());
    }
    
    final int version = machine.getGameData().getStoryFileHeader().getVersion();
    final int bufferlen = memaccess.readUnsignedByte(textbuffer);
    final int textbufferstart = determineTextBufferStart(version);
    final int charsTyped = (version >= 5) ?
                      memaccess.readUnsignedByte(textbuffer + 1) :
                      0;
    
    // from version 5, text starts at position 2
    final ZsciiString input = bufferToZscii(textbuffer + textbufferstart, bufferlen,
                                            charsTyped);
    final List<ZsciiString> tokens = tokenize(input);
    
    final Map<ZsciiString, Integer> parsedTokens =
      new HashMap<ZsciiString, Integer>();
    
    // Write the number of tokens in byte 1 of the parse buffer
    final int maxwords = memaccess.readUnsignedByte(parsebuffer);
    
    // Do not go beyond the limit of maxwords
    final int numParsedTokens = Math.min(maxwords, tokens.size());
    
    // Write the number of parsed tokens into byte 1 of the parse buffer
    memaccess.writeUnsignedByte(parsebuffer + 1, (short) numParsedTokens);
    
    int parseaddr = parsebuffer + 2;
    
    for (int i = 0; i < numParsedTokens; i++) {
      
      final ZsciiString token = tokens.get(i);      
      final int entryAddress = dictionary.lookup(token);
      //System.out.println("token: '" + token + "' entryAddress: " + entryAddress);
      
      int startIndex = 0;
      if (parsedTokens.containsKey(token)) {
          
        final int timesContained = parsedTokens.get(token);
        parsedTokens.put(token, timesContained + 1);
          
        for (int j = 0; j < timesContained; j++) {
          
          final int found = input.indexOf(token, startIndex);
          startIndex = found + token.length();
        }
          
      } else {
          
        parsedTokens.put(token, 1);          
      }
      
      int tokenIndex = input.indexOf(token, startIndex);    
      
      tokenIndex++; // adjust by the buffer length byte
      
      if (version >= 5) {
        
        // if version >= 5, there is also numbers typed byte
        tokenIndex++;
      }
      
      // if the tokenize flag is not set, write out the entry to the
      // parse buffer, if it is set then, only write the token position
      // if the token was recognized
      if (!flag || flag && entryAddress > 0) {
        
        // This is one slot
        memaccess.writeUnsignedShort(parseaddr, entryAddress);     
        memaccess.writeUnsignedByte(parseaddr + 2, (short) token.length());
        memaccess.writeUnsignedByte(parseaddr + 3, (short) tokenIndex);
      }
      parseaddr += 4;
    }
  }  

  /**
   * Turns the buffer into a ZSCII string. This function reads at most
   * |bufferlen| bytes and treats each byte as an ASCII character.
   * The characters will be concatenated to the result string.
   * 
   * @param address the buffer address
   * @param bufferlen the buffer length
   * @param charsTyped from version 5, this is the number of characters
   * to include in the input
   * @return the string contained in the buffer
   */
  private ZsciiString bufferToZscii(final int address, final int bufferlen,
      final int charsTyped) {
    
    final MemoryAccess memaccess = machine.getGameData().getMemoryAccess();
    
    // If charsTyped is set, use that value as the limit
    final int numChars = (charsTyped > 0) ? charsTyped : bufferlen;
    
    // read input from text buffer
    final ZsciiStringBuilder buffer = new ZsciiStringBuilder();
    for (int i = 0; i < numChars; i++) {
      
      final short charByte = memaccess.readUnsignedByte(address + i);
      if (charByte == 0) {
        break;
      }
      buffer.append(charByte);
    }    
    return buffer.toZsciiString();
  }
  
  /**
   * Turns the specified input string into tokens. It will take whitespace
   * implicitly and dictionary separators explicitly to tokenize the
   * stream, dictionary specified separators are included in the result list.
   * 
   * @param input the input string
   * @return the tokens
   */
  private List<ZsciiString> tokenize(final ZsciiString input) {
    
    final List<ZsciiString> result = new ArrayList<ZsciiString>();
    
    // Retrieve the defined separators
    final ZsciiStringBuilder separators = new ZsciiStringBuilder();
    separators.append(WHITESPACE);
    
    final Dictionary dictionary = machine.getGameData().getDictionary();
    final ZCharDecoder decoder =
      machine.getGameData().getZCharDecoder();
    for (int i = 0, n = dictionary.getNumberOfSeparators(); i < n; i++) {
      
      final byte delim = dictionary.getSeparator(i);
      separators.append(decoder.decodeZChar(delim));
    }
    
    // The tokenizer will also return the delimiters
    final ZsciiString delim = separators.toZsciiString();
    final ZsciiStringTokenizer tok = new ZsciiStringTokenizer(input, delim);
    
    while (tok.hasMoreTokens()) {
      
      final ZsciiString token = tok.nextToken();
      if (!Character.isWhitespace(token.charAt(0))) {
        
        result.add(token);
      }
    }
    return result;
  }
  
  /**
   * Depending on the version, this returns the offset where text starts in
   * the text buffer. In versions up to 4 this is 1, since we have the
   * buffer size in the first byte, from versions 5, we also have the
   * number of typed characters in the second byte.
   * 
   * @param version the story file version
   * @return 1 if version &lt; 4, 2, otherwise
   */
  private int determineTextBufferStart(final int version) {
    
    return (version < 5) ? 1 : 2;
  }
  
  /**
   * Draws the cursor and refreshes the screen.
   * 
   * @param flag true for display, false for clear
   */
  private synchronized void displayCursor(final boolean flag) {
    
    machine.getScreen().displayCursor(flag);
    machine.getScreen().redraw();
  }
  
  // *********************************************************************
  // ***** History methods
  // ***********************************

  private void storeInput(final List<Short> inputbuffer,
      final short terminateChar) {
   
    if (terminateChar != ZsciiEncoding.NULL) {
  
      history.addInputLine(inputbuffer);
    }
  }
  
}
