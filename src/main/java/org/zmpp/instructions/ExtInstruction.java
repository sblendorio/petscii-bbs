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

import org.zmpp.media.Resolution;
import static org.zmpp.base.MemoryUtil.*;
import org.zmpp.vm.Machine;
import org.zmpp.vm.PortableGameState;
import org.zmpp.windowing.ScreenModel;

/**
 * Implementation of instructions with EXT operand count.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class ExtInstruction extends AbstractInstruction {

  /**
   * Constructor.
   * @param machine Machine object
   * @param opcodeNum opcode number
   * @param operands operands
   * @param storeVar store variable
   * @param branchInfo branch information
   * @param opcodeLength opcode length
   */
  public ExtInstruction(Machine machine, int opcodeNum,
                        Operand[] operands, char storeVar,
                        BranchInfo branchInfo, int opcodeLength) {
    super(machine, opcodeNum, operands, storeVar, branchInfo, opcodeLength);
  }

  /** {@inheritDoc} */
  @Override
  protected OperandCount getOperandCount() { return OperandCount.EXT; }

  /** {@inheritDoc} */
  public void execute() {
    switch (getOpcodeNum()) {
    case EXT_SAVE:
      save();
      break;
    case EXT_RESTORE:
      restore();
      break;
    case EXT_LOG_SHIFT:
      log_shift();
      break;
    case EXT_ART_SHIFT:
      art_shift();
      break;
    case EXT_SET_FONT:
      set_font();
      break;
    case EXT_SAVE_UNDO:
      save_undo();
      break;
    case EXT_RESTORE_UNDO:
      restore_undo();
      break;
    case EXT_PRINT_UNICODE:
      print_unicode();
      break;
    case EXT_CHECK_UNICODE:
      check_unicode();
      break;
    case EXT_MOUSE_WINDOW:
      mouse_window();
      break;
    case EXT_PICTURE_DATA:
      picture_data();
      break;
    case EXT_DRAW_PICTURE:
      draw_picture();
      break;
    case EXT_ERASE_PICTURE:
      erase_picture();
      break;
    case EXT_MOVE_WINDOW:
      move_window();
      break;
    case EXT_WINDOW_SIZE:
      window_size();
      break;
    case EXT_WINDOW_STYLE:
      window_style();
      break;
    case EXT_SET_MARGINS:
      set_margins();
      break;
    case EXT_GET_WIND_PROP:
      get_wind_prop();
      break;
    case EXT_PICTURE_TABLE:
      picture_table();
      break;
    case EXT_PUT_WIND_PROP:
      put_wind_prop();
      break;
    case EXT_PUSH_STACK:
      push_stack();
      break;
    case EXT_POP_STACK:
      pop_stack();
      break;
    case EXT_READ_MOUSE:
      read_mouse();
      break;
    case EXT_SCROLL_WINDOW:
      scroll_window();
      break;
    default:
      throwInvalidOpcode();
      break;
    }
  }

  /** SAVE_UNDO instruction. */
  private void save_undo() {
    // Target PC offset is two because of the extra opcode byte and
    // operand type byte compared to the 0OP instruction
    final int pc = getMachine().getPC() + 3;
    final boolean success = getMachine().save_undo(pc);
    storeUnsignedResult(success ? TRUE : FALSE);
    nextInstruction();
  }

  /** RESTORE_UNDO instruction. */
  private void restore_undo() {
    final PortableGameState gamestate = getMachine().restore_undo();
    if (gamestate == null) {
      storeUnsignedResult(FALSE);
      nextInstruction();
    } else {
      final char storevar = gamestate.getStoreVariable(getMachine());
      getMachine().setVariable(storevar, RESTORE_TRUE);
    }
  }

  /** ART_SHIFT instruction. */
  private void art_shift() {
    short number = getSignedValue(0);
    final short places = getSignedValue(1);
    number = (short) ((places >= 0) ? number << places : number >> (-places));
    storeUnsignedResult(signedToUnsigned16(number));
    nextInstruction();
  }

  /** LOG_SHIFT instruction. */
  private void log_shift() {
    char number = getUnsignedValue(0);
    final short places = getSignedValue(1);
    number = (char) ((places >= 0) ? number << places : number >>> (-places));
    storeUnsignedResult(number);
    nextInstruction();
  }

  /** SET_FONT instruction. */
  private void set_font() {
    final char previousFont =
      getMachine().getScreen().setFont(getUnsignedValue(0));
    storeUnsignedResult(previousFont);
    nextInstruction();
  }

  /** SAVE instruction. */
  private void save() {
    // Saving to tables is not supported yet, this is the standard save feature
    // Offset is 3 because there are two opcode bytes + 1 optype byte before
    // the actual store var byte
    saveToStorage(getMachine().getPC() + 3);
  }

  /** RESTORE instruction. */
  private void restore() {
    // Reading from tables is not supported yet, this is the standard
    // restore feature
    restoreFromStorage();
  }

  /** PRINT_UNICODE instruction. */
  private void print_unicode() {
    final char zchar = (char) getUnsignedValue(0);
    getMachine().printZsciiChar(zchar);
    nextInstruction();
  }

  /** CHECK_UNICODE instruction. */
  private void check_unicode() {
    // always return true, set bit 0 for can print and bit 1 for
    // can read
    storeUnsignedResult((char) 3);
    nextInstruction();
  }

  /** MOUSE_WINDOW instruction. */
  private void mouse_window() {
    getMachine().getScreen6().setMouseWindow(getSignedValue(0));
    nextInstruction();
  }

  /** PICTURE_DATA instruction. */
  private void picture_data() {
    final int picnum = getUnsignedValue(0);
    final int array = getUnsignedValue(1);
    boolean result = false;

    if (picnum == 0) {
      writePictureFileInfo(array);
      // branch if any pictures are available: this information is only
      // available in the 1.1 spec
      result = getMachine().getPictureManager().getNumPictures() > 0;
    } else {
      final Resolution picdim =
        getMachine().getPictureManager().getPictureSize(picnum);
      if (picdim != null) {
        getMachine().writeUnsigned16(array, toUnsigned16(picdim.getHeight()));
        getMachine().writeUnsigned16(array + 2,
                                     toUnsigned16(picdim.getWidth()));
        result = true;
      }
    }
    branchOnTest(result);
  }

  /**
   * Writes the information of the picture file into the specified array.
   * @param array an array address
   */
  private void writePictureFileInfo(final int array) {
    getMachine().writeUnsigned16(array,
        toUnsigned16(getMachine().getPictureManager().getNumPictures()));
    getMachine().writeUnsigned16(array + 2,
        toUnsigned16(getMachine().getPictureManager().getRelease()));
  }

  /** DRAW_PICTURE instruction. */
  private void draw_picture() {
    final int picnum = getUnsignedValue(0);
    int x = 0, y = 0;

    if (getNumOperands() > 1) {
      y = getUnsignedValue(1);
    }

    if (getNumOperands() > 2) {
      x = getUnsignedValue(2);
    }
    getMachine().getScreen6().getSelectedWindow().drawPicture(
        getMachine().getPictureManager().getPicture(picnum), y, x);
    nextInstruction();
  }

  /** ERASE_PICTURE instruction. */
  private void erase_picture() {
    final int picnum = getUnsignedValue(0);
    int x = 1, y = 1;

    if (getNumOperands() > 1) {
      y = getUnsignedValue(1);
    }

    if (getNumOperands() > 2) {
      x = getUnsignedValue(2);
    }
    getMachine().getScreen6().getSelectedWindow().erasePicture(
        getMachine().getPictureManager().getPicture(picnum), y, x);
    nextInstruction();
  }

  /** MOVE_WINDOW instruction. */
  private void move_window() {
    getMachine().getScreen6().getWindow(getUnsignedValue(0)).move(
        getUnsignedValue(1), getUnsignedValue(2));
    nextInstruction();
  }

  /** WINDOW_SIZE instruction. */
  private void window_size() {
    final short window = getSignedValue(0);
    final char height = getUnsignedValue(1);
    final char width = getUnsignedValue(2);
    getMachine().getScreen6().getWindow(window).setSize(height, width);
    nextInstruction();
  }

  /** WINDOW_STYLE instruction. */
  private void window_style() {
    int operation = 0;
    if (getNumOperands() > 2) {
      operation = getUnsignedValue(2);
    }
    getWindow(getSignedValue(0)).setStyle(getUnsignedValue(1), operation);
    nextInstruction();
  }

  /** SET_MARGINS instruction. */
  private void set_margins() {
    int window = ScreenModel.CURRENT_WINDOW;
    if (getNumOperands() == 3) {
      window = getSignedValue(2);
    }
    getWindow(window).setMargins(getUnsignedValue(0), getUnsignedValue(1));
    nextInstruction();
  }

  /** GET_WIND_PROP instruction. */
  private void get_wind_prop() {
    int window = getSignedValue(0);
    int propnum = getUnsignedValue(1);
    char result;
    result = (char) getWindow(window).getProperty(propnum);
    storeUnsignedResult(result);
    nextInstruction();
  }

  /** PUT_WIND_PROP instruction. */
  private void put_wind_prop() {
    short window = getSignedValue(0);
    char propnum = getUnsignedValue(1);
    short value = getSignedValue(2);
    getWindow(window).putProperty(propnum, value);
    nextInstruction();
  }

  /** PICTURE_TABLE instruction. */
  private void picture_table() {
    // @picture_table is a no-op, because all pictures are held in memory
    // anyways
    nextInstruction();
  }

  /** POP_STACK instruction. */
  private void pop_stack() {
    int numItems = getUnsignedValue(0);
    char stack = 0;
    if (getNumOperands() == 2) {
      stack = getUnsignedValue(1);
    }
    for (int i = 0; i < numItems; i++) {
      getMachine().popStack(stack);
    }
    nextInstruction();
  }

  /** PUSH_STACK instruction. */
  private void push_stack() {
    char value = getUnsignedValue(0);
    char stack = 0;
    if (getNumOperands() == 2) {
      stack = getUnsignedValue(1);
    }
    branchOnTest(getMachine().pushStack(stack, value));
  }

  /** SCROLL_WINDOW instruction. */
  private void scroll_window() {
    getWindow(getSignedValue(0)).scroll(getSignedValue(1));
    nextInstruction();
  }

  /** READ_MOUSE instruction. */
  private void read_mouse() {
    int array = getUnsignedValue(0);
    getMachine().getScreen6().readMouse(array);
    nextInstruction();
  }
}
