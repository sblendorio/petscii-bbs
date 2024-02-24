/*
 * Created on 2008/07/24
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
package org.zmpp.instructions;

import java.util.logging.Logger;
import org.zmpp.media.SoundSystem;
import org.zmpp.vm.Machine;
import org.zmpp.vm.MachineRunState;
import org.zmpp.vm.Output;
import org.zmpp.windowing.ScreenModel;
import org.zmpp.windowing.TextCursor;

/**
 * Implementation of instructions with operand count VAR.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class VarInstruction extends AbstractInstruction {

  private static final Logger LOG = Logger.getLogger("org.zmpp");

  /**
   * Constructor.
   * @param machine Machine object
   * @param opcodeNum opcode number
   * @param operands operands
   * @param storeVar store variable
   * @param branchInfo branch information
   * @param opcodeLength opcode length
   */
  public VarInstruction(Machine machine, int opcodeNum,
                         Operand[] operands, char storeVar,
                         BranchInfo branchInfo, int opcodeLength) {
    super(machine, opcodeNum, operands, storeVar, branchInfo, opcodeLength);
  }

  /** {@inheritDoc} */
  @Override
  protected OperandCount getOperandCount() { return OperandCount.VAR; }

  /** {@inheritDoc} */
  public void execute() {
    switch (getOpcodeNum()) {
      case VAR_CALL:
        call();
        break;
      case VAR_CALL_VS2:
        call();
        break;
      case VAR_STOREW:
        storew();
        break;
      case VAR_STOREB:
        storeb();
        break;
      case VAR_PUT_PROP:
        put_prop();
        break;
      case VAR_SREAD:
        sread();
        break;
      case VAR_PRINT_CHAR:
        print_char();
        break;
      case VAR_PRINT_NUM:
        print_num();
        break;
      case VAR_RANDOM:
        random();
        break;
      case VAR_PUSH:
        push();
        break;
      case VAR_PULL:
        pull();
        break;
      case VAR_SPLIT_WINDOW:
        split_window();
        break;
      case VAR_SET_TEXT_STYLE:
        set_text_style();
        break;
      case VAR_BUFFER_MODE:
        buffer_mode();
        break;
      case VAR_SET_WINDOW:
        set_window();
        break;
      case VAR_OUTPUT_STREAM:
        output_stream();
        break;
      case VAR_INPUT_STREAM:
        input_stream();
        break;
      case VAR_SOUND_EFFECT:
        sound_effect();
        break;
      case VAR_ERASE_WINDOW:
        erase_window();
        break;
      case VAR_ERASE_LINE:
        erase_line();
        break;
      case VAR_SET_CURSOR:
        set_cursor();
        break;
      case VAR_GET_CURSOR:
        get_cursor();
        break;
      case VAR_READ_CHAR:
        read_char();
        break;
      case VAR_SCAN_TABLE:
        scan_table();
        break;
      case VAR_NOT:
        not();
        break;
      case VAR_CALL_VN:
      case VAR_CALL_VN2:
        call();
        break;
      case VAR_TOKENISE:
        tokenise();
        break;
      case VAR_ENCODE_TEXT:
        encode_text();
        break;
      case VAR_COPY_TABLE:
        copy_table();
        break;
      case VAR_PRINT_TABLE:
        print_table();
        break;
      case VAR_CHECK_ARG_COUNT:
        check_arg_count();
        break;
      default:
        throwInvalidOpcode();
    }
  }

  /** CALL instruction. */
  private void call() {
    call(getNumOperands() - 1);
  }

  /** STOREW instruction. */
  private void storew() {
    final int array = getUnsignedValue(0);
    final int wordIndex = getSignedValue(1);
    final int memAddress = (array + 2 * wordIndex) & 0xffff;
    final char value = getUnsignedValue(2);
    getMachine().writeUnsigned16(memAddress, value);
    nextInstruction();
  }

  /** STOREB instruction. */
  private void storeb() {
    final int array = getUnsignedValue(0);
    final int byteIndex = getSignedValue(1);
    final int memAddress = (array + byteIndex) & 0xffff;
    final int value = getUnsignedValue(2);
    getMachine().writeUnsigned8(memAddress, (char) (value & 0xff));
    nextInstruction();
  }

  /** PUT_PROP instruction. */
  private void put_prop() {
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);
    final char value = getUnsignedValue(2);

    if (obj > 0) {
      getMachine().setProperty(obj, property, value);
      nextInstruction();
    } else {
      // Issue warning for non-existent object
      getMachine().warn("@put_prop illegal access to object " + obj);
      nextInstruction();
    }
  }

  /** PRINT_CHAR instruction. */
  private void print_char() {
    final char zchar = (char) getUnsignedValue(0);
    getMachine().printZsciiChar(zchar);
    nextInstruction();
  }

  /** PRINT_NUM instruction. */
  private void print_num() {
    final short number = getSignedValue(0);
    getMachine().printNumber(number);
    nextInstruction();
  }

  /** PUSH instruction. */
  private void push() {
    final char value = getUnsignedValue(0);
    getMachine().setVariable((char) 0, value);
    nextInstruction();
  }

  /** PULL instruction. */
  private void pull() {
    if (getStoryVersion() == 6) {
      pull_v6();
    } else {
      pull_std();
    }
    nextInstruction();
  }

  /** PULL instruction for version 6 stories. */
  private void pull_v6() {
    char stack = 0;
    if (getNumOperands() == 1) {
      stack = getUnsignedValue(0);
    }
    storeUnsignedResult(getMachine().popStack(stack));
  }

  /** PULL instruction for stories except V6. */
  private void pull_std() {
    final char varnum = getUnsignedValue(0);
    final char value = getMachine().getVariable((char) 0);

    // standard 1.1
    if (varnum == 0) {
      getMachine().setStackTop(value);
    } else {
      getMachine().setVariable(varnum, value);
    }
  }

  /** OUTPUT_STREAM instruction. */
  private void output_stream() {
    // Stream number should be a signed byte
    final short streamnumber = getSignedValue(0);

    if (streamnumber < 0 && streamnumber >= -3) {
      getMachine().selectOutputStream(-streamnumber, false);
    } else if (streamnumber > 0 && streamnumber <= 3) {
      if (streamnumber == Output.OUTPUTSTREAM_MEMORY) {
        final int tableAddress = getUnsignedValue(1);
        int tablewidth = 0;
        if (getNumOperands() == 3) {
          tablewidth = getUnsignedValue(2);
          LOG.info(String.format("@output_stream 3 %x %d\n", tableAddress,
                                 tablewidth));
        }
        getMachine().selectOutputStream3(tableAddress, tablewidth);
      } else {
        getMachine().selectOutputStream(streamnumber, true);
      }
    }
    nextInstruction();
  }

  /** INPUT_STREAM instruction. */
  private void input_stream() {
    getMachine().selectInputStream(getUnsignedValue(0));
    nextInstruction();
  }

  /** RANDOM instruction. */
  private void random() {
    final short range = getSignedValue(0);
    storeUnsignedResult(getMachine().random(range));
    nextInstruction();
  }

  /** SREAD instruction. */
  private void sread() {
    if (getMachine().getRunState() == MachineRunState.RUNNING) {
      sreadStage1();
    } else {
      sreadStage2();
    }
  }

  /** First stage of SREAD. */
  private void sreadStage1() {
    char textbuffer = getUnsignedValue(0);
    getMachine().setRunState(MachineRunState.createReadLine(
            getReadInterruptTime(), getReadInterruptRoutine(),
            getNumLeftOverChars(textbuffer), textbuffer));
  }

  /**
   * Returns the read interrupt time.
   * @return read interrupt time
   */
  private int getReadInterruptTime() {
    return getNumOperands() >= 3 ? getUnsignedValue(2) : 0;
  }

  /**
   * Returns the read interrupt routine address.
   * @return interrup routine address
   */
  private char getReadInterruptRoutine() {
    return getNumOperands() >= 4 ? getUnsignedValue(3) : 0;
  }

  /**
   * Returns the number of characters left in the text buffer when timed
   * input interrupt occurs.
   * @param textbuffer text buffer address
   * @return number of left over characters
   */
  private int getNumLeftOverChars(char textbuffer) {
    return getStoryVersion() >= 5 ?
      getMachine().readUnsigned8(textbuffer + 1) : 0;
  }

  /**
   * Second stage of SREAD.
   */
  private void sreadStage2() {
    getMachine().setRunState(MachineRunState.RUNNING);

    final int version = getStoryVersion();
    final char textbuffer = getUnsignedValue(0);
    char parsebuffer = 0;
    if (getNumOperands() >= 2) {
      parsebuffer = getUnsignedValue(1);
    }
    // Here the Z-machine needs to be paused and the user interface
    // handles the whole input
    final char terminal =
      getMachine().readLine(textbuffer);

    if (version < 5 || (version >= 5 && parsebuffer > 0)) {
      // Do not tokenise if parsebuffer is 0 (See specification of read)
      getMachine().tokenize(textbuffer, parsebuffer, 0, false);
    }

    if (storesResult()) {
      // The specification suggests that we store the terminating character
      // here, this can be NULL or NEWLINE at the moment
      storeUnsignedResult(terminal);
    }
    nextInstruction();
  }

  /** SOUND_EFFECT instruction. */
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
    LOG.info(String.format("@sound_effect n: %d, fx: %d, vol: %d, rep: %d, " +
                           "routine: $%04x\n", soundnum, effect, volume,
                           repeats, routine));
    // In version 3 repeats is always 1
    if (getStoryVersion() == 3) {
      repeats = 1;
    }

    final SoundSystem soundSystem = getMachine().getSoundSystem();
    soundSystem.play(soundnum, effect, volume, repeats, routine);
    nextInstruction();
  }

  /** SPLIT_WINDOW instruction. */
  private void split_window() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      screenModel.splitWindow(getUnsignedValue(0));
    }
    nextInstruction();
  }

  /** SET_WINDOW instruction. */
  private void set_window() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      screenModel.setWindow(getUnsignedValue(0));
    }
    nextInstruction();
  }

  /** SET_TEXT_STYLE instruction. */
  private void set_text_style() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      screenModel.setTextStyle(getUnsignedValue(0));
    }
    nextInstruction();
  }

  /** BUFFER_MODE instruction. */
  private void buffer_mode() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      screenModel.setBufferMode(getUnsignedValue(0) > 0);
    }
    nextInstruction();
  }

  /** ERASE_WINDOW instruction. */
  private void erase_window() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      screenModel.eraseWindow(getSignedValue(0));
    }
    nextInstruction();
  }

  /** ERASE_LINE instruction. */
  private void erase_line() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      screenModel.eraseLine(getUnsignedValue(0));
    }
    nextInstruction();
  }

  /** SET_CURSOR instruction. */
  private void set_cursor() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {

      final short line = getSignedValue(0);
      char column = 0;
      short window = ScreenModel.CURRENT_WINDOW;

      if (getNumOperands() >= 2) {
        column = getUnsignedValue(1);
      }
      if (getNumOperands() >= 3) {
        window = getSignedValue(2);
      }
      if (line > 0) {
        screenModel.setTextCursor(line, column, window);
      }
    }
    nextInstruction();
  }

  /** GET_CURSOR instruction. */
  private void get_cursor() {
    final ScreenModel screenModel = getMachine().getScreen();
    if (screenModel != null) {
      final TextCursor cursor = screenModel.getTextCursor();
      final int arrayAddr = getUnsignedValue(0);
      getMachine().writeUnsigned16(arrayAddr, (char) cursor.getLine());
      getMachine().writeUnsigned16(arrayAddr + 2, (char) cursor.getColumn());
    }
    nextInstruction();
  }

  /** SCAN_TABLE instruction. */
  private void scan_table() {
    int x = getUnsignedValue(0);
    final char table = getUnsignedValue(1);
    final int length = getUnsignedValue(2);
    int form  = 0x82; // default value
    if (getNumOperands() == 4) {
      form = getUnsignedValue(3);
    }
    final int fieldlen = form & 0x7f;
    final boolean isWordTable = (form & 0x80) > 0;
    char pointer = table;
    boolean found = false;

    for (int i = 0; i < length; i++) {
      int current;
      if (isWordTable) {
        current = getMachine().readUnsigned16(pointer);
        x &= 0xffff;
      } else {
        current = getMachine().readUnsigned8(pointer);
        x &= 0xff;
      }
      if (current == x) {
        storeUnsignedResult(pointer);
        found = true;
        break;
      }
      pointer += fieldlen;
    }
    // not found
    if (!found) {
      storeUnsignedResult((char) 0);
    }
    branchOnTest(found);
  }

  /** READ_CHAR instruction. */
  private void read_char() {
    if (getMachine().getRunState() == MachineRunState.RUNNING) {
      readCharStage1();
    } else {
      readCharStage2();
    }
  }

  /**
   * First stage of READ_CHAR.
   */
  private void readCharStage1() {
    getMachine().setRunState(MachineRunState.createReadChar(
      getReadCharInterruptTime(), getReadCharInterruptRoutine()));
  }

  /**
   * Returns the interrupt time for READ_CHAR timed input.
   * @return interrupt time
   */
  private int getReadCharInterruptTime() {
    return getNumOperands() >= 2 ? getUnsignedValue(1) : 0;
  }

  /**
   * Returns the address of the interrupt routine for READ_CHAR timed input.
   * @return interrupt routine address
   */
  private char getReadCharInterruptRoutine() {
    return getNumOperands() >= 3 ? getUnsignedValue(2) : 0;
  }

  /**
   * Second stage of READ_CHAR.
   */
  private void readCharStage2() {
    getMachine().setRunState(MachineRunState.RUNNING);
    storeUnsignedResult(getMachine().readChar());
    nextInstruction();
  }

  /**
   * not instruction. Actually a copy from Short1Instruction, probably we
   * can remove this duplication.
   */
  private void not()  {
    final int notvalue = ~getUnsignedValue(0);
    storeUnsignedResult((char) (notvalue & 0xffff));
    nextInstruction();
  }

  /** TOKENISE instruction. */
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

  /** CHECK_ARG_COUNT instruction. */
  private void check_arg_count() {
    final int argumentNumber = getUnsignedValue(0);
    final int currentNumArgs =
      getMachine().getCurrentRoutineContext().getNumArguments();
    branchOnTest(argumentNumber <= currentNumArgs);
  }

  /** COPY_TABLE instruction. */
  private void copy_table() {
    final int first = getUnsignedValue(0);
    final int second = getUnsignedValue(1);
    int size = Math.abs(getSignedValue(2));
    if (second == 0) {
      // Clear size bytes of first
      for (int i = 0; i < size; i++) {
        getMachine().writeUnsigned8(first + i, (char) 0);
      }
    } else {
      getMachine().copyArea(first, second, size);
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

    char zchar = 0;
    final TextCursor cursor = getMachine().getScreen().getTextCursor();
    final int column = cursor.getColumn();
    int row = cursor.getLine();

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        final int offset = (width * i) + j;
        zchar = (char) getMachine().readUnsigned8(zsciiText + offset);
        getMachine().printZsciiChar(zchar);
      }
      row += skip + 1;
      getMachine().getScreen().setTextCursor(row, column,
          ScreenModel.CURRENT_WINDOW);
    }
    nextInstruction();
  }

  /** ENCODE_TEXT instruction. */
  private void encode_text() {
    final int zsciiText = getUnsignedValue(0);
    final int length = getUnsignedValue(1);
    final int from = getUnsignedValue(2);
    final int codedText = getUnsignedValue(3);
    getMachine().encode(zsciiText + from, length, codedText);
    nextInstruction();
  }
}
