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

import org.zmpp.vm.Machine;
import org.zmpp.windowing.ScreenModel;

/**
 * Implementation for 2OP operand count instructions.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class C2OpInstruction extends AbstractInstruction {

  /**
   * Constructor.
   * @param machine Machine object
   * @param opcodeNum opcode number
   * @param operands operands
   * @param storeVar store variable number
   * @param branchInfo branch information
   * @param opcodeLength opcode length
   */
  public C2OpInstruction(Machine machine, int opcodeNum,
                         Operand[] operands, char storeVar,
                         BranchInfo branchInfo, int opcodeLength) {
    super(machine, opcodeNum, operands, storeVar, branchInfo, opcodeLength);
  }

  /** {@inheritDoc} */
  @Override
  protected OperandCount getOperandCount() { return OperandCount.C2OP; }

  /** {@inheritDoc} */
  public void execute() {
    switch (getOpcodeNum()) {
      case C2OP_JE:
        je();
        break;
      case C2OP_JL:
        jl();
        break;
      case C2OP_JG:
        jg();
        break;
      case C2OP_JIN:
        jin();
        break;
      case C2OP_DEC_CHK:
        dec_chk();
        break;
      case C2OP_INC_CHK:
        inc_chk();
        break;
      case C2OP_TEST:
        test();
        break;
      case C2OP_OR:
        or();
        break;
      case C2OP_AND:
        and();
        break;
      case C2OP_TEST_ATTR:
        test_attr();
        break;
      case C2OP_SET_ATTR:
        set_attr();
        break;
      case C2OP_CLEAR_ATTR:
        clear_attr();
        break;
      case C2OP_STORE:
        store();
        break;
      case C2OP_INSERT_OBJ:
        insert_obj();
        break;
      case C2OP_LOADW:
        loadw();
        break;
      case C2OP_LOADB:
        loadb();
        break;
      case C2OP_GET_PROP:
        get_prop();
        break;
      case C2OP_GET_PROP_ADDR:
        get_prop_addr();
        break;
      case C2OP_GET_NEXT_PROP:
        get_next_prop();
        break;
      case C2OP_ADD:
        add();
        break;
      case C2OP_SUB:
        sub();
        break;
      case C2OP_MUL:
        mul();
        break;
      case C2OP_DIV:
        div();
        break;
      case C2OP_MOD:
        mod();
        break;
      case C2OP_CALL_2S:
        call(1);
        break;
      case C2OP_CALL_2N:
        call(1);
        break;
      case C2OP_SET_COLOUR:
        set_colour();
        break;
      case C2OP_THROW:
        z_throw();
        break;
      default:
        throwInvalidOpcode();
    }
  }

  /** JE instruction. */
  private void je() {
    boolean equalsFollowing = false;
    final char op1 = getUnsignedValue(0);
    if (getNumOperands() <= 1) {
      getMachine().halt("je expects at least two operands, only " +
                        "one provided");
    } else {
      for (int i = 1; i < getNumOperands(); i++) {
        char value = getUnsignedValue(i);
        if (op1 == value) {
          equalsFollowing = true;
          break;
        }
      }
      branchOnTest(equalsFollowing);
    }
  }

  /** JL instruction. */
  private void jl() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    //System.out.printf("Debugging jl op1: %d op2: %d\n", op1, op2);
    branchOnTest(op1 < op2);
  }

  /** JG instruction. */
  private void jg() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    branchOnTest(op1 > op2);
  }

  /** JIN instruction. */
  private void jin() {
    final int obj1 = getUnsignedValue(0);
    final int obj2 = getUnsignedValue(1);
    int parentOfObj1 = 0;

    if (obj1 > 0) {
      parentOfObj1 = getMachine().getParent(obj1);
    } else {
      getMachine().warn("@jin illegal access to object " + obj1);
    }
    branchOnTest(parentOfObj1 == obj2);
  }

  /** DEC_CHK instruction. */
  private void dec_chk() {
    final char varnum = getUnsignedValue(0);
    final short value = getSignedValue(1);
    final short varValue = (short) (getSignedVarValue(varnum) - 1);
    setSignedVarValue(varnum, varValue);
    branchOnTest(varValue < value);
  }

  /** INC_CHK instruction. */
  private void inc_chk() {
    final char varnum = getUnsignedValue(0);
    final short value = getSignedValue(1);
    final short varValue = (short) (getSignedVarValue(varnum) + 1);
    setSignedVarValue(varnum, varValue);
    branchOnTest(varValue > value);
  }

  /** TEST instruction. */
  private void test() {
    final int op1 = getUnsignedValue(0);
    final int op2 = getUnsignedValue(1);
    branchOnTest((op1 & op2) == op2);
  }

  /** OR instruction. */
  private void or() {
    final int op1 = getUnsignedValue(0);
    final int op2 = getUnsignedValue(1);
    storeUnsignedResult((char) ((op1 | op2) & 0xffff));
    nextInstruction();
  }

  /** AND instruction. */
  private void and() {
    final int op1 = getUnsignedValue(0);
    final int op2 = getUnsignedValue(1);
    storeUnsignedResult((char) ((op1 & op2) & 0xffff));
    nextInstruction();
  }

  /** ADD instruction. */
  private void add() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    storeSignedResult((short) (op1 + op2));
    nextInstruction();
  }

  /** SUB instruction. */
  private void sub() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    storeSignedResult((short) (op1 - op2));
    nextInstruction();
  }

  /** MUL instruction. */
  private void mul() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    storeSignedResult((short)(op1 * op2));
    nextInstruction();
  }

  /** DIV instruction. */
  private void div() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    if (op2 == 0) {
      getMachine().halt("@div division by zero");
    } else {
      storeSignedResult((short) (op1 / op2));
      nextInstruction();
    }
  }

  /** MOD instruction. */
  private void mod() {
    final short op1 = getSignedValue(0);
    final short op2 = getSignedValue(1);
    if (op2 == 0) {
      getMachine().halt("@mod division by zero");
    } else {
      storeSignedResult((short) (op1 % op2));
      nextInstruction();
    }
  }

  /** TEST_ATTR instruction. */
  private void test_attr() {
    final int obj = getUnsignedValue(0);
    final int attr = getUnsignedValue(1);
    if (obj > 0 && isValidAttribute(attr)) {
      branchOnTest(getMachine().isAttributeSet(obj, attr));
    } else {
      getMachine().warn("@test_attr illegal access to object " + obj);
      branchOnTest(false);
    }
  }

  /** SET_ATTR instruction. */
  private void set_attr() {
    final int obj = getUnsignedValue(0);
    final int attr = getUnsignedValue(1);
    if (obj > 0 && isValidAttribute(attr)) {
      getMachine().setAttribute(obj, attr);
    } else {
      getMachine().warn("@set_attr illegal access to object " + obj +
                        " attr: " + attr);
    }
    nextInstruction();
  }

  /** CLEAR_ATTR instruction. */
  private void clear_attr() {
    final int obj = getUnsignedValue(0);
    final int attr = getUnsignedValue(1);
    if (obj > 0 && isValidAttribute(attr)) {
      getMachine().clearAttribute(obj, attr);
    } else {
      getMachine().warn("@clear_attr illegal access to object " + obj +
                        " attr: " + attr);
    }
    nextInstruction();
  }

  /** STORE instruction. */
  private void store() {
    final char varnum = getUnsignedValue(0);
    final char value = getUnsignedValue(1);
    // Handle stack variable as a special case (standard 1.1)
    if (varnum == 0) {
      getMachine().setStackTop(value);
    } else {
      getMachine().setVariable(varnum, value);
    }
    nextInstruction();
  }

  /** INSERT_OBJ instruction. */
  private void insert_obj() {
    final int obj = getUnsignedValue(0);
    final int dest = getUnsignedValue(1);
    if (obj > 0 && dest > 0) {
      getMachine().insertObject(dest, obj);
    } else {
      getMachine().warn("@insert_obj with object 0 called, obj: " + obj +
                        ", dest: " + dest);
    }
    nextInstruction();
  }

  /** LOADB instruction. */
  private void loadb() {
    final int arrayAddress = getUnsignedValue(0);
    final int index = getUnsignedValue(1);
    final int memAddress = (arrayAddress + index) & 0xffff;
    storeUnsignedResult((char) getMachine().readUnsigned8(memAddress));
    nextInstruction();
  }

  /** LOADW instruction. */
  private void loadw() {
    final int arrayAddress = getUnsignedValue(0);
    final int index = getUnsignedValue(1);
    final int memAddress = (arrayAddress + 2 * index) & 0xffff;
    storeUnsignedResult(getMachine().readUnsigned16(memAddress));
    nextInstruction();
  }

  /** GET_PROP instruction. */
  private void get_prop() {
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);

    if (obj > 0) {
      char value = (char) getMachine().getProperty(obj, property);
      storeUnsignedResult(value);
    } else {
      getMachine().warn("@get_prop illegal access to object " + obj);
    }
    nextInstruction();
  }

  /** GET_PROP_ADDR instruction. */
  private void get_prop_addr() {
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);
    if (obj > 0) {
      char value = (char)
        (getMachine().getPropertyAddress(obj, property) & 0xffff);
      storeUnsignedResult(value);
    } else {
      getMachine().warn("@get_prop_addr illegal access to object " + obj);
    }
    nextInstruction();
  }

  /** GET_NEXT_PROP instruction. */
  private void get_next_prop() {
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);
    char value = 0;
    if (obj > 0) {
      value = (char) (getMachine().getNextProperty(obj, property) & 0xffff);
      storeUnsignedResult(value);
      nextInstruction();
    } else {
      // issue warning and continue
      getMachine().warn("@get_next_prop illegal access to object " + obj);
      nextInstruction();
    }
  }

  /** SET_COLOUR instruction. */
  private void set_colour() {
    int window = ScreenModel.CURRENT_WINDOW;
    if (getNumOperands() == 3) {
      window = getSignedValue(2);
    }
    getMachine().getScreen().setForeground(getSignedValue(0), window);
    getMachine().getScreen().setBackground(getSignedValue(1), window);
    nextInstruction();
  }

  /** THROW instruction. */
  private void z_throw() {
    final char returnValue = getUnsignedValue(0);
    final int stackFrame = getUnsignedValue(1);

    // Unwind the stack
    final int currentStackFrame = getMachine().getRoutineContexts().size() - 1;
    if (currentStackFrame < stackFrame) {
      getMachine().halt("@throw from an invalid stack frame state");
    } else {

      // Pop off the routine contexts until the specified stack frame is
      // reached
      final int diff = currentStackFrame - stackFrame;
      for (int i = 0; i < diff; i++) {
        getMachine().returnWith((char) 0);
      }

      // and return with the return value
      returnFromRoutine(returnValue);
    }
  }

  /**
   * Checks if the specified attribute is valid
   * @param attribute attribute number
   * @return true if valid, false otherwise
   */
  private boolean isValidAttribute(final int attribute) {
    final int numAttr = getStoryVersion() <= 3 ? 32 : 48;
    return attribute >= 0 && attribute < numAttr;
  }
}
