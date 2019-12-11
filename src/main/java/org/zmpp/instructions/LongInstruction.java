/*
 * $Id: LongInstruction.java,v 1.14 2006/05/30 17:54:28 weiju Exp $
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

import org.zmpp.base.MemoryReadAccess;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ObjectTree;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.ZObject;


/**
 * This class represents instructions of type LONG, 2OP.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class LongInstruction extends AbstractInstruction {

  /**
   * The operand count.
   */
  private OperandCount operandCount;
  
  /**
   * Constructor.
   * 
   * @param machineState a reference to a MachineState object
   * @param opcode the instruction's opcode
   */
  public LongInstruction(Machine machineState, int opcode) {
    
    super(machineState, opcode);
    this.operandCount = OperandCount.C2OP;
  }
  
  /**
   * Constructor.
   * 
   * @param machineState the machine state
   * @param operandCount the operand count
   * @param opcode the opcode
   */
  public LongInstruction(Machine machineState,
      OperandCount operandCount, int opcode) {
    
    super(machineState, opcode);
    this.operandCount = operandCount;
  }
  
  /**
   * {@inheritDoc}
   */
  public void doInstruction() {
   
    switch (getOpcode()) {
    
      case LongStaticInfo.OP_JE:
        je();
        break;
      case LongStaticInfo.OP_JL:
        jl();
        break;
      case LongStaticInfo.OP_JG:
        jg();
        break;
      case LongStaticInfo.OP_JIN:
        jin();
        break;
      case LongStaticInfo.OP_DEC_CHK:
        dec_chk();
        break;
      case LongStaticInfo.OP_INC_CHK:
        inc_chk();
        break;
      case LongStaticInfo.OP_TEST:
        test();
        break;
      case LongStaticInfo.OP_OR:
        or();
        break;
      case LongStaticInfo.OP_AND:
        and();
        break;
      case LongStaticInfo.OP_TEST_ATTR:
        test_attr();
        break;
      case LongStaticInfo.OP_SET_ATTR:
        set_attr();
        break;
      case LongStaticInfo.OP_CLEAR_ATTR:
        clear_attr();
        break;        
      case LongStaticInfo.OP_STORE:
        store();
        break;
      case LongStaticInfo.OP_INSERT_OBJ:
        insert_obj();
        break;
      case LongStaticInfo.OP_LOADW:
        loadw();
        break;        
      case LongStaticInfo.OP_LOADB:
        loadb();
        break;        
      case LongStaticInfo.OP_GET_PROP:
        get_prop();
        break;        
      case LongStaticInfo.OP_GET_PROP_ADDR:
        get_prop_addr();
        break;        
      case LongStaticInfo.OP_GET_NEXT_PROP:
        get_next_prop();
        break;        
      case LongStaticInfo.OP_ADD:
        add();
        break;
      case LongStaticInfo.OP_SUB:
        sub();
        break;
      case LongStaticInfo.OP_MUL:
        mul();
        break;
      case LongStaticInfo.OP_DIV:
        div();
        break;
      case LongStaticInfo.OP_MOD:
        mod();
        break;
      case LongStaticInfo.OP_CALL_2S:
        call(1);
        break;
      case LongStaticInfo.OP_CALL_2N:
        call(1);
        break;
      case LongStaticInfo.OP_SET_COLOUR:
        set_colour();
        break;
      case LongStaticInfo.OP_THROW:
        z_throw();
        break;
      default:
        throwInvalidOpcode();
    }    
  }
  
  /**
   * {@inheritDoc}
   */
  public InstructionForm getInstructionForm() { return InstructionForm.LONG; }
  
  /**
   * {@inheritDoc}
   */
  public OperandCount getOperandCount() { return operandCount; }
  
  /**
   * {@inheritDoc}
   */
  protected InstructionStaticInfo getStaticInfo() {
    
    return LongStaticInfo.getInstance();
  }
  
  private void je() {
    
    boolean equalsFollowing = false;
    final short op1 = getValue(0);
    if (getNumOperands() <= 1) {

      getMachine().getCpu().halt("je expects at least two operands, only " +
                        "one provided");
    } else {
      
      for (int i = 1; i < getNumOperands(); i++) {
        
        if (op1 == getValue(i)) {
          
          equalsFollowing = true;
          break;
        }
      }
      branchOnTest(equalsFollowing);
    }
  }
    
  private void jl() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    branchOnTest(op1 < op2);
  }
  
  private void jg() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    branchOnTest(op1 > op2);
  }
  
  private void jin() {
    
    final int obj1 = getUnsignedValue(0);
    final int obj2 = getUnsignedValue(1);
    int parentOfObj1 = 0;
    
    if (obj1 > 0) {
      
      parentOfObj1 = getObjectTree().getObject(obj1).getParent();

    } else {
      
      getMachine().warn("@jin illegal access to object " + obj1);
    }
    branchOnTest(parentOfObj1 == obj2);
  }
  
  private void dec_chk() {
    
    final int varnum = getUnsignedValue(0);
    final short value = getValue(1);
    final short varValue = (short) (getCpu().getVariable(varnum) - 1);
    
    getCpu().setVariable(varnum, varValue);
    branchOnTest(varValue < value);
  }
  
  private void inc_chk() {
    
    final int varnum = getUnsignedValue(0);
    final short value = getValue(1);
    final short varValue = (short) (getCpu().getVariable(varnum) + 1);
    
    getCpu().setVariable(varnum, varValue);
    branchOnTest(varValue > value);
  }
  
  private void test() {
    
    final int op1 = getUnsignedValue(0);
    final int op2 = getUnsignedValue(1);
    branchOnTest((op1 & op2) == op2);
  }
  
  private void or() {
    
    final int op1 = getUnsignedValue(0);
    final int op2 = getUnsignedValue(1);
    storeResult((short) ((op1 | op2) & 0xffff));
    nextInstruction();
  }
  
  private void and() {
    
    final int op1 = getUnsignedValue(0);
    final int op2 = getUnsignedValue(1);
    storeResult((short) ((op1 & op2) & 0xffff));
    nextInstruction();
  }
  
  private void add() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    storeResult((short) (op1 + op2));
    nextInstruction();
  }
  
  private void sub() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    storeResult((short) (op1 - op2));
    nextInstruction();
  }
  
  private void mul() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    storeResult((short)(op1 * op2));
    nextInstruction();
  }
  
  private void div() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    
    if (op2 == 0) {
    
      getMachine().getCpu().halt("@div division by zero");
      
    } else {
    
      storeResult((short) (op1 / op2));
      nextInstruction();
    }
  }
  
  private void mod() {
    
    final short op1 = getValue(0);
    final short op2 = getValue(1);
    
    if (op2 == 0) {
      
      getMachine().getCpu().halt("@mod division by zero");
    } else {
    
      storeResult((short) (op1 % op2));
      nextInstruction();
    }
  }
  
  private void test_attr() {
    
    final int obj = getUnsignedValue(0);
    final int attr = getUnsignedValue(1);
    
    if (obj > 0 && isValidAttribute(attr)) {
      
      final ZObject zobj = getObjectTree().getObject(obj);
      branchOnTest(zobj.isAttributeSet(attr));
      
    } else {
      
      getMachine().warn("@test_attr illegal access to object " + obj);
      branchOnTest(false);
    }
  }
  
  private void set_attr() {
    
    final int obj = getUnsignedValue(0);
    final int attr = getUnsignedValue(1);
    if (obj > 0 && isValidAttribute(attr)) {
      
      final ZObject zobj = getObjectTree().getObject(obj);
      zobj.setAttribute(attr);
      
    } else {
      
      getMachine().warn("@set_attr illegal access to object " + obj
                        + " attr: " + attr);
    }
    nextInstruction();
  }
  
  private void clear_attr() {
    
    final int obj = getUnsignedValue(0);
    final int attr = getUnsignedValue(1);
    if (obj > 0 && isValidAttribute(attr)) {
      
      final ZObject zobj = getObjectTree().getObject(obj);
      zobj.clearAttribute(attr);
      
    } else {
      
      getMachine().warn("@clear_attr illegal access to object " + obj
                        + " attr: " + attr);
    }
    nextInstruction();
  }
  
  private void store() {
    
    final int varnum = getUnsignedValue(0);
    final short value = getValue(1);
    
    // Handle stack variable as a special case (standard 1.1)
    if (varnum == 0) {
      
      getCpu().setStackTopElement(value);
      
    } else {
      
      getCpu().setVariable(varnum, value);
    }
    nextInstruction();
  }
  
  private void insert_obj() {
    
    final int obj = getUnsignedValue(0);
    final int dest = getUnsignedValue(1);
    if (obj > 0 && dest > 0) {
      
      final ObjectTree objectTree = getObjectTree();
      objectTree.insertObject(dest, obj);
      
    } else {
      
      getMachine().warn("@insert_obj with object 0 called, obj: " + obj
                        + ", dest: " + dest);
    }
    nextInstruction();
  }
  
  private void loadw() {
    
    final int arrayAddress = getUnsignedValue(0);
    final int index = getUnsignedValue(1);
    final MemoryReadAccess memaccess =
      getMachine().getGameData().getMemoryAccess();
    storeResult(memaccess.readShort(arrayAddress + 2 * index));
    nextInstruction();
  }
  
  private void loadb() {
    
    final int arrayAddress = getUnsignedValue(0);
    final int index = getUnsignedValue(1);
    final MemoryReadAccess memaccess =
      getMachine().getGameData().getMemoryAccess();
    storeResult((short) memaccess.readUnsignedByte(arrayAddress + index));
    nextInstruction();
  }
  
  private void get_prop() {
    
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);
    
    if (obj > 0) {
      
      final ZObject zobj = getObjectTree().getObject(obj);
      final int numBytes = zobj.getPropertySize(property);
      short value;
    
      if (!zobj.isPropertyAvailable(property)) {
     
        // Retrieve and store default
        value = getObjectTree().getPropertyDefault(property);
      
      } else if (numBytes == 1) {
      
        value = zobj.getPropertyByte(property, 0);
      
      } else {
      
        final byte byte1 = zobj.getPropertyByte(property, 0);
        final byte byte2 = zobj.getPropertyByte(property, 1);      
        value = (short) (byte1 << 8 | (byte2 & 0xff));
      }
      storeResult(value);
      
    } else {
      
      getMachine().warn("@get_prop illegal access to object " + obj);
    }
    nextInstruction();
  }
  
  private void get_prop_addr() {
    
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);    
    
    if (obj > 0) {
      
      short value = 0;
      final ZObject zobj = getObjectTree().getObject(obj);
    
      if (zobj.isPropertyAvailable(property)) {
      
        value = (short) (zobj.getPropertyAddress(property) & 0xffff);
      }
      storeResult(value);

    } else {
      
      getMachine().warn("@get_prop_addr illegal access to object " + obj);
    }
    
    nextInstruction();
  }
  
  private void get_next_prop() {
    
    final int obj = getUnsignedValue(0);
    final int property = getUnsignedValue(1);
    short value = 0;
    
    if (obj > 0) {
      
      final ZObject zobj = getObjectTree().getObject(obj);
    
      if (property == 0 || zobj.isPropertyAvailable(property)) {
      
        value = (short) (zobj.getNextProperty(property) & 0xffff);
        storeResult(value);
        nextInstruction();
      
      } else {
      
        getMachine().getCpu().halt("the property [" + property + "] of object [" + obj
                          + "] does not exist");
      }
      
    } else {
      
      // issue warning and continue
      getMachine().warn("@get_next_prop illegal access to object " + obj);
      nextInstruction();
    }
  }
  
  private void set_colour() {
    
    int window = ScreenModel.CURRENT_WINDOW;
    if (getNumOperands() == 3) {
      
      window = getValue(2);
    }
    //System.out.printf("@set_colour, foreground: %d, background: %d\n",
    //    getValue(0), getValue(1));
    getMachine().getScreen().setForegroundColor(getValue(0), window);
    getMachine().getScreen().setBackgroundColor(getValue(1), window);
    nextInstruction();
  }
  
  private void z_throw() {
    
    final short returnValue = getValue(0);
    final int stackFrame = getUnsignedValue(1);
    
    // Unwind the stack
    final int currentStackFrame = getCpu().getRoutineContexts().size() - 1;
    if (currentStackFrame < stackFrame) {
      
      getMachine().getCpu().halt("@throw from an invalid stack frame state");
    } else {
     
      // Pop off the routine contexts until the specified stack frame is
      // reached
      final int diff = currentStackFrame - stackFrame;
      for (int i = 0; i < diff; i++) {
        
        getCpu().popRoutineContext((short) 0);
      }
      
      // and return with the return value
      returnFromRoutine(returnValue);
    }
  }
  
  private boolean isValidAttribute(final int attribute) {
    
    final int numAttr = getStoryFileVersion() <= 3 ? 32 : 48;
    return attribute >= 0 && attribute < numAttr;    
  }
}
