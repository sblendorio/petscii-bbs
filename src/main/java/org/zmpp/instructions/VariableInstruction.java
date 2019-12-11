/*
 * $Id: VariableInstruction.java,v 1.32 2006/05/12 21:57:29 weiju Exp $
 * 
 * Created on 10/03/2005
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
package org.zmpp.instructions;

import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.ZCharEncoder;
import org.zmpp.media.SoundSystem;
import org.zmpp.vm.Machine;
import org.zmpp.vm.Output;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.TextCursor;
import org.zmpp.vm.ZObject;


/**
 * This class represents instructions of type VARIABLE.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class VariableInstruction extends AbstractInstruction {

  /**
   * The operand count.
   */
  private OperandCount operandCount;
  
  /**
   * Constructor.
   * 
   * @param machineState a reference to a MachineState object
   * @param operandCount the operand count
   * @param opcode the instruction's opcode
   */
  public VariableInstruction(Machine machineState, 
      OperandCount operandCount, int opcode) {
    
    super(machineState, opcode);
    this.operandCount = operandCount;
  }
  
  /**
   * {@inheritDoc}
   */
  public InstructionForm getInstructionForm() {
    
    return InstructionForm.VARIABLE;
  }
  
  /**
   * {@inheritDoc}
   */
  public OperandCount getOperandCount() {
    
    return operandCount;
  }
  
  /**
   * {@inheritDoc}
   */
  protected InstructionStaticInfo getStaticInfo() {

    return VariableStaticInfo.getInstance();
  }
  
  /**
   * Returns the memory access object.
   * 
   * @return the memory access object
   */
  private MemoryAccess getMemoryAccess() {
    
    return getMachine().getGameData().getMemoryAccess();
  }
  
  /**
   * {@inheritDoc}
   */
  protected void doInstruction() {
    
    switch (getOpcode()) {
      
      case VariableStaticInfo.OP_CALL:
        call();
        break;
      case VariableStaticInfo.OP_CALL_VS2:
        call();
        break;
      case VariableStaticInfo.OP_STOREW:
        storew();
        break;
      case VariableStaticInfo.OP_STOREB:
        storeb();
        break;
      case VariableStaticInfo.OP_PUT_PROP:
        put_prop();
        break;
      case VariableStaticInfo.OP_SREAD:
        sread();
        break;
      case VariableStaticInfo.OP_PRINT_CHAR:
        print_char();
        break;
      case VariableStaticInfo.OP_PRINT_NUM:
        print_num();
        break;
      case VariableStaticInfo.OP_RANDOM:
        random();
        break;
      case VariableStaticInfo.OP_PUSH:
        push();
        break;
      case VariableStaticInfo.OP_PULL:
        pull();
        break;
      case VariableStaticInfo.OP_SPLIT_WINDOW:
        split_window();
        break;
      case VariableStaticInfo.OP_SET_TEXT_STYLE:
        set_text_style();
        break;
      case VariableStaticInfo.OP_BUFFER_MODE:
        buffer_mode();
        break;
      case VariableStaticInfo.OP_SET_WINDOW:
        set_window();
        break;
      case VariableStaticInfo.OP_OUTPUTSTREAM:
        output_stream();
        break;
      case VariableStaticInfo.OP_INPUTSTREAM:
        input_stream();
        break;
      case VariableStaticInfo.OP_SOUND_EFFECT:
        sound_effect();
        break;
      case VariableStaticInfo.OP_ERASE_WINDOW:
        erase_window();
        break;
      case VariableStaticInfo.OP_ERASE_LINE:
        erase_line();
        break;
      case VariableStaticInfo.OP_SET_CURSOR:
        set_cursor();
        break;
      case VariableStaticInfo.OP_GET_CURSOR:
        get_cursor();
        break;
      case VariableStaticInfo.OP_READ_CHAR:
        read_char();
        break;
      case VariableStaticInfo.OP_SCAN_TABLE:
        scan_table();
        break;
      case VariableStaticInfo.OP_NOT:
        not();
        break;
      case VariableStaticInfo.OP_CALL_VN:
      case VariableStaticInfo.OP_CALL_VN2:
        call();
        break;
      case VariableStaticInfo.OP_TOKENISE:
        tokenise();
        break;
      case VariableStaticInfo.OP_ENCODE_TEXT:
        encode_text();
        break;
      case VariableStaticInfo.OP_COPY_TABLE:
        copy_table();
        break;
      case VariableStaticInfo.OP_PRINT_TABLE:
        print_table();
        break;
      case VariableStaticInfo.OP_CHECK_ARG_COUNT:
        check_arg_count();
        break;
      default:
        throwInvalidOpcode();
    }
  }
  
  private void call() {

    call(getNumOperands() - 1);
  }
    
  private void storew() {
    
    final MemoryAccess memaccess = getMemoryAccess();
    final int array = getUnsignedValue(0);
    final int wordIndex = getUnsignedValue(1);
    final short value = getValue(2);
    
    memaccess.writeShort(array + wordIndex * 2, value);
    nextInstruction();
  }
  
  private void storeb() {
    
    final MemoryAccess memaccess = getMemoryAccess();
    final int array = getUnsignedValue(0);
    final int byteIndex = getUnsignedValue(1);
    final byte value = (byte) getValue(2);
    
    memaccess.writeByte(array + byteIndex, value);
    nextInstruction();
  }
  
  private void put_prop() {
    
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);
    final short value = getValue(2);
    
    if (obj > 0) {
      
      final ZObject object = getObjectTree().getObject(obj);
    
      if (object.isPropertyAvailable(property)) {
      
        if (object.getPropertySize(property) == 1) {
        
          object.setPropertyByte(property, 0, (byte) (value & 0xff));
        
        } else {
        
          object.setPropertyByte(property, 0, (byte) ((value >> 8) & 0xff));
          object.setPropertyByte(property, 1, (byte) (value & 0xff));
        }
        nextInstruction();
      
      } else {
      
        getMachine().getCpu().halt("put_prop: the property [" + property
            + "] of object [" + obj + "] does not exist");
      }
      
    } else {
      
      // Issue warning for non-existent object
      getMachine().warn("@put_prop illegal access to object " + obj);
      nextInstruction();    
    } 
  }
  
  private void print_char() {
    
    final short zchar = getValue(0);
    getMachine().getOutput().printZsciiChar(zchar, false);
    nextInstruction();
  }
  
  private void print_num() {
    
    final short number = getValue(0);
    getMachine().getOutput().printNumber(number);
    nextInstruction();
  }
  
  private void push() {
    
    final short value = getValue(0);
    getCpu().setVariable(0, value);
    nextInstruction();
  }
  
  private void pull() {

    if (getStoryFileVersion() == 6) {
    
      pull_v6();
      
    } else {

      pull_std();
    }
    nextInstruction();
  }
  
  private void pull_v6() {
    
    int userstack = 0;
    if (getNumOperands() == 1) {

      userstack = getUnsignedValue(0);
    }
    
    if (userstack > 0) {
      
      storeResult(getCpu().popUserStack(userstack));
      
    } else {
      
      storeResult(getCpu().getVariable(0));
    }
  }
  
  private void pull_std() {
    
    final int varnum = getUnsignedValue(0);
    final short value = getCpu().getVariable(0);
    
    // standard 1.1
    if (varnum == 0) {
      
      getCpu().setStackTopElement(value);
      
    } else {
      
      getCpu().setVariable(varnum, value);
    }
  }
  
  private void output_stream() {
    
    // Stream number should be a signed byte
    final short streamnumber = getValue(0);
    
    if (streamnumber < 0 && streamnumber >= -3) {
      
      getMachine().getOutput().selectOutputStream(-streamnumber, false);
    
    } else if (streamnumber > 0 && streamnumber <= 3) {
      
      if (streamnumber == Output.OUTPUTSTREAM_MEMORY) {
       
        final int tableAddress = getUnsignedValue(1);
        int tablewidth = 0;
        if (getNumOperands() == 3) {
          
          tablewidth = getUnsignedValue(2);
          System.out.printf("@output_stream 3 %x %d\n", tableAddress, tablewidth);
        }
        //System.out.printf("Select stream 3 on table: %x\n", tableAddress);
        getMachine().getOutput().selectOutputStream3(tableAddress, tablewidth);
        
      } else {
      
        getMachine().getOutput().selectOutputStream(streamnumber, true);
      }
    }
    nextInstruction();
  }
  
  private void input_stream() {
    
    getMachine().getInput().selectInputStream(getUnsignedValue(0));
    nextInstruction();
  }
  
  private void random() {
    
    final short range = getValue(0);
    storeResult(getMachine().random(range));
    nextInstruction();
  }
  
  private void sread() {
    
    //System.out.println("@sread()");
    final int version = getStoryFileVersion();
    if (version <= 3) {
      
      getMachine().updateStatusLine();
    }
    
    final int textbuffer = getUnsignedValue(0);
    int parsebuffer = 0;
    int time = 0;
    short packedAddress = 0;
    
    if (getNumOperands() >= 2) {
      parsebuffer = getUnsignedValue(1);
    }
    if (getNumOperands() >= 3) {
      time = getUnsignedValue(2);
    }
    if (getNumOperands() >= 4) {
      packedAddress = getValue(3);
    }
    
    final short terminal =
      getMachine().readLine(textbuffer, time, packedAddress);
    
    if (version < 5 || (version >= 5 && parsebuffer > 0)) {
      
      // Do not tokenise if parsebuffer is 0 (See specification of read)
      getMachine().tokenize(textbuffer, parsebuffer, 0, false);
    }
    
    if (storesResult()) {

      // The specification suggests that we store the terminating character
      // here, this can be NULL or NEWLINE at the moment
      storeResult(terminal);
    }
    
    nextInstruction();
  }
  
  
  /**
   * Implements the sound_effect instruction.
   */
  private void sound_effect() {
    
    // Choose some default values
    int soundnum = SoundSystem.BLEEP_HIGH;
    int effect = SoundSystem.EFFECT_START;
    int volume = SoundSystem.VOLUME_DEFAULT;
    int repeats = 0;
    int routine = 0;
    
    // Truly variable
    // If no operands are set, this function will still try to send something
    if (getNumOperands() >= 1) {
      
      soundnum = getUnsignedValue(0);
    }
    
    if (getNumOperands() >= 2) {
      
      effect = getUnsignedValue(1);
    }
    
    if (getNumOperands() >= 3) {
      
      final int volumeRepeats = getUnsignedValue(2);
      volume = volumeRepeats & 0xff;
      repeats = (volumeRepeats >>> 8) & 0xff;      
      if (repeats <= 0) {
        repeats = 1;
      }
    }
    
    if (getNumOperands() == 4) {
      
      routine = getUnsignedValue(3);
    }
    System.out.printf("@sound_effect n: %d, fx: %d, vol: %d, rep: %d, routine: $%04x\n", soundnum, effect, volume, repeats, routine);
    // In version 3 repeats is always 1
    if (getStoryFileVersion() == 3) {
      repeats = 1;
    }
        
    final SoundSystem soundSystem = getMachine().getSoundSystem();
    soundSystem.play(soundnum, effect, volume, repeats, routine);
    nextInstruction();
  }
  
  private void split_window() {
    
    //System.out.printf("@split_window, window: %d\n", getUnsignedValue(0));
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      screenModel.splitWindow(getUnsignedValue(0));
    }
    nextInstruction();
  }
  
  private void set_window() {
    
    //System.out.printf("@set_window, window: %d\n", getUnsignedValue(0));    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      screenModel.setWindow(getUnsignedValue(0));
    }
    nextInstruction();
  }
  
  private void set_text_style() {
    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      screenModel.setTextStyle(getUnsignedValue(0));
    }
    nextInstruction();
  }
  
  private void buffer_mode() {
    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      screenModel.setBufferMode(getUnsignedValue(0) > 0);
    }
    nextInstruction();
  }
  
  private void erase_window() {
    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      screenModel.eraseWindow(getValue(0));
    }
    nextInstruction();    
  }
  
  private void erase_line() {
    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      screenModel.eraseLine(getValue(0));
    }
    nextInstruction();    
  }
  
  private void set_cursor() {
    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
    
      final int line = getValue(0);
      int column = 0;
      int window = ScreenModel.CURRENT_WINDOW;
      
      if (getNumOperands() >= 2) {
        column = getValue(1);
      }
      if (getNumOperands() >= 3) {
        window = getValue(2);
      }
      
      if (line > 0) {
        
        screenModel.setTextCursor(line, column, window);        
      }
    }
    nextInstruction();    
  }
  
  private void get_cursor() {
    
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      
      final TextCursor cursor = screenModel.getTextCursor();
      final MemoryAccess memaccess = getMemoryAccess();
      final int arrayAddr = getUnsignedValue(0);
      memaccess.writeShort(arrayAddr, (short) cursor.getLine());
      memaccess.writeShort(arrayAddr + 2, (short) cursor.getColumn());
    }
    nextInstruction();
  }
  
  private void scan_table() {
    
    final MemoryAccess memaccess = getMemoryAccess();
    final short x = getValue(0);
    final int table = getUnsignedValue(1);
    final int length = getUnsignedValue(2);
    int form  = 0x82; // default value
    if (getNumOperands() == 4) {
      
      form = getUnsignedValue(3);
    }
    final int fieldlen = form & 0x7f;
    final boolean isWordTable = (form & 0x80) > 0;
    int pointer = table;
    boolean found = false;
    
    for (int i = 0; i < length; i++) {
        
      final short current = isWordTable ? memaccess.readShort(pointer) :
                                          memaccess.readByte(pointer);
      if (current == x) {
        
        storeResult((short) pointer);
        found = true;
        break;
      }
      pointer += fieldlen;
    }
    
    // not found
    if (!found) {
      
      storeResult((short) 0);
    }
    branchOnTest(found);
  }

  private void read_char() {
    
    //System.out.println("@read_char()");    
    int time = 0;
    int routineAddress = 0;
    if (getNumOperands() >= 2) {
     
      time = getUnsignedValue(1);
    }
    if (getNumOperands() >= 3) {
      
      routineAddress = getValue(2);
    }
    storeResult(getMachine().readChar(time,
        routineAddress));
    nextInstruction();
  }
  
  /**
   * not instruction. Actually a copy from Short1Instruction, probably we
   * can remove this duplication.
   */
  private void not()  {
  
    final int notvalue = ~getUnsignedValue(0);
    storeResult((short) (notvalue & 0xffff));
    nextInstruction();
  }
  
  private void tokenise() {
    
    final int textbuffer = getUnsignedValue(0);
    final int parsebuffer = getUnsignedValue(1);
    int dictionary = 0;
    int flag = 0;
    if (getNumOperands() >= 3) {
      dictionary = getUnsignedValue(2);
    }
    if (getNumOperands() >= 4) {
      flag = getUnsignedValue(3);
    }
    getMachine().tokenize(textbuffer, parsebuffer, dictionary, (flag != 0));
    nextInstruction();
  }
  
  private void check_arg_count() {
    
    final int argumentNumber = getUnsignedValue(0);
    final int currentNumArgs =
      getCpu().getCurrentRoutineContext().getNumArguments();
    branchOnTest(argumentNumber <= currentNumArgs);
  }
  
  private void copy_table() {
    
    final int first = getUnsignedValue(0);
    final int second = getUnsignedValue(1);
    int size = getValue(2);
    final MemoryAccess memaccess = getMemoryAccess();

    if (second == 0) {
      
      // Clear size bytes of first
      size = Math.abs(size);
      for (int i = 0; i < size; i++) {
        
        memaccess.writeByte(first + i, (byte) 0);
      }
      
    } else {
      
      if (size < 0 || first > second) {
        
        // copy forward
        size = Math.abs(size);
        for (int i = 0; i < size; i++) {
                    
          memaccess.writeByte(second + i, memaccess.readByte(first + i));
        }
        
      } else {
          
        // backwards
        size = Math.abs(size);
        for (int i = size - 1; i >= 0; i--) {
          
          memaccess.writeByte(second + i, memaccess.readByte(first + i));
        }
      }
    }
    nextInstruction();
  }
  
  /**
   * Do the print_table instruction. This method takes a text and formats
   * it in a specified format. It requires access to the cursor position
   * in order to be implemented correctly, otherwise horizontal home
   * position would always be set to the left position of the window.
   * Interestingly, the text is not encoded, so the characters should be
   * accessed one by one in ZSCII format.
   */
  private void print_table() {
    
    final int zsciiText = getUnsignedValue(0);
    final int width = getUnsignedValue(1);
    int height = 1;
    int skip = 0;
    if (getNumOperands() >= 3) {
      height = getUnsignedValue(2);
    }
    if (getNumOperands() == 4) {
      skip = getUnsignedValue(3);
    }
    
    //System.out.printf("@print_table, zscii-text = %d, width = %d," +
    //    " height = %d, skip = %d\n", zsciiText, width, height, skip);
    short zchar = 0;
    final MemoryAccess memaccess = getMemoryAccess();
    final TextCursor cursor = getMachine().getScreen().getTextCursor();
    final int column = cursor.getColumn();
    int row = cursor.getLine();
    
    for (int i = 0; i < height; i++) {
      
      for (int j = 0; j < width; j++) {
        
        final int offset = (width * i) + j;
        zchar = memaccess.readUnsignedByte(zsciiText + offset);
        getMachine().getOutput().printZsciiChar(zchar, false);
      }
      row += skip + 1;
      getMachine().getScreen().setTextCursor(row, column,
          ScreenModel.CURRENT_WINDOW);
    }
    nextInstruction();
  }
  
  private void encode_text() {
    
    final int zsciiText = getUnsignedValue(0);
    final int length = getUnsignedValue(1);
    final int from = getUnsignedValue(2);
    final int codedText = getUnsignedValue(3);    
    final ZCharEncoder encoder = getMachine().getGameData().getZCharEncoder();
    
    encoder.encode(getMemoryAccess(), zsciiText + from,
        length, codedText);
    nextInstruction();
  }
}
