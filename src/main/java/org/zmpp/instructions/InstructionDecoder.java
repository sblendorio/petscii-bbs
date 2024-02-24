/*
 * Created on 2008/07/23
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

import java.io.Serializable;

import static org.zmpp.vm.Instruction.*;
import static org.zmpp.vm.Instruction.InstructionForm.*;
import static org.zmpp.vm.Instruction.OperandCount.*;

import org.zmpp.instructions.AbstractInstruction.BranchInfo;
import org.zmpp.instructions.InstructionInfoDb.InstructionInfo;
import org.zmpp.vm.Instruction;
import org.zmpp.vm.Machine;

/**
 * The revised instruction decoder, a direct port from the Erlang implementation
 * of ZMPP (Schmalz). This decoding scheme is considerably simpler and stores
 * more useful information than the previous one.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class InstructionDecoder implements Serializable {
  private static final char EXTENDED_MASK     = 0xbe;
  private static final char VAR_MASK          = 0xc0; // 2#11000000
  private static final char SHORT_MASK        = 0x80; // 2#10000000
  private static final char LOWER_4_BITS      = 0x0f; // 2#00001111
  private static final char LOWER_5_BITS      = 0x1f; // 2#00011111
  private static final char LOWER_6_BITS      = 0x3f; // 2#00111111
  private static final char BITS_4_5          = 0x30; // 2#00110000
  private static final char BIT_7             = 0x80; // 2#10000000
  private static final char BIT_6             = 0x40; // 2#01000000
  private static final char BIT_5             = 0x20; // 2#00100000
  private static final int LEN_OPCODE         = 1;
  private static final int LEN_LONG_OPERANDS  = 2;
  private static final int LEN_STORE_VARIABLE = 1;
  private static final InstructionInfoDb INFO_DB =
      InstructionInfoDb.getInstance();
  private static final BranchInfo DUMMY_BRANCH_INFO =
      new BranchInfo(false, 0, 0, (short) 0);
  private static final int[] NO_OPERAND_TYPES = new int[0];
  private static final char[] NO_OPERANDS     = new char[0];

  private Machine machine;

  /**
   * Initialize decoder with a valid machine object.
   * @param aMachine a Machine object
   */
  public void initialize(Machine aMachine) {
    this.machine = aMachine;
  }

  /**
   * Decode the instruction at the specified address.
   * @param instructionAddress the current instruction's address
   * @return the instruction at the specified address
   */
  public Instruction decodeInstruction(final int instructionAddress) {
    Instruction instr = null;
    char byte1 = machine.readUnsigned8(instructionAddress);
    InstructionForm form = getForm(byte1);
    switch (form) {
      case SHORT:
        instr = decodeShort(instructionAddress, byte1);
        break;
      case LONG:
        instr = decodeLong(instructionAddress, byte1);
        break;
      case VARIABLE:
        instr = decodeVariable(instructionAddress, byte1);
        break;
      case EXTENDED:
        instr = decodeExtended(instructionAddress);
        break;
      default:
        System.out.println("unrecognized form: " + form);
        break;
    }
    return instr;
  }

  /**
   * Decodes an instruction in short form.
   * @param instrAddress the instruction address
   * @param byte1 the first instruction byte
   * @return the decoded instruction
   */
  private Instruction decodeShort(int instrAddress, char byte1) {
    OperandCount opCount = (byte1 & BITS_4_5) == BITS_4_5 ? C0OP : C1OP;
    char opcodeNum = (char) (byte1 & LOWER_4_BITS);
    InstructionInfo info = INFO_DB.getInfo(opCount, opcodeNum,
                                          machine.getVersion());
    if (info == null) {
      System.out.printf("ILLEGAL SHORT operation, instrAddr: $%04x, OC: %s, " +
                        "opcode: #$%02x, Version: %d\n",
                        instrAddress, opCount.toString(), (int) opcodeNum,
                        machine.getVersion());
      //infoDb.printKeys();
      throw new java.lang.UnsupportedOperationException("Exit !!");
    }
    int zsciiLength = 0;

    // extract operand
    String str = null;
    char operand = 0;
    int[] operandTypes = NO_OPERAND_TYPES;
    char[] operands = NO_OPERANDS;
    int operandType = getOperandType(byte1, 1);
    if (info.isPrint()) {
      str = machine.decode2Zscii(instrAddress + 1, 0);
      zsciiLength = machine.getNumZEncodedBytes(instrAddress + 1);
    } else {
      operand = getOperandAt(instrAddress + 1, operandType);
      operandTypes = new int[] {operandType};
      operands = new char[] {operand};
    }
    int numOperandBytes = getOperandLength(operandType);
    int currentAddr = instrAddress + LEN_OPCODE + numOperandBytes;
    return createInstruction(opCount, instrAddress,
                             opcodeNum, currentAddr, numOperandBytes,
                             zsciiLength, operandTypes, operands, str);
  }

  /**
   * Decodes a long op count instruction.
   * @param instrAddress instruction address
   * @param byte1 first instruction byte
   * @return instruction object
   */
  private Instruction decodeLong(int instrAddress, char byte1) {
    char opcodeNum = (char) (byte1 & LOWER_5_BITS);

    // extract long operands
    int operandType1 = (byte1 & BIT_6) != 0 ? Operand.TYPENUM_VARIABLE :
      Operand.TYPENUM_SMALL_CONSTANT;
    int operandType2 = (byte1 & BIT_5) != 0 ? Operand.TYPENUM_VARIABLE :
      Operand.TYPENUM_SMALL_CONSTANT;
    char operand1 = machine.readUnsigned8(instrAddress + 1);
    char operand2 = machine.readUnsigned8(instrAddress + 2);
    int numOperandBytes = LEN_LONG_OPERANDS;
    int currentAddr = instrAddress + LEN_OPCODE + LEN_LONG_OPERANDS;
    //System.out.printf("LONG 2OP, opnum: %d, byte1: %d, addr: $%04x\n",
    //        (int) opcodeNum, (int) byte1, instrAddress);
    return createInstruction(C2OP, instrAddress, opcodeNum, currentAddr,
                             numOperandBytes, 0,
      new int[] {operandType1, operandType2}, new char[] {operand1, operand2},
      null);
  }

  /**
   * Decodes an instruction in variable form.
   * @param instrAddress the instruction address
   * @param byte1 the first opcode byte
   * @return the instruction
   */
  private Instruction decodeVariable(int instrAddress, char byte1) {
    OperandCount opCount = (byte1 & BIT_5) != 0 ? VAR : C2OP;
    char opcodeNum = (char) (byte1 & LOWER_5_BITS);
    int opTypesOffset;
    int[] operandTypes;
    // The only instruction taking up to 8 parameters is CALL_VS2
    if (isVx2(opCount, opcodeNum)) {
      operandTypes = joinArrays(
          extractOperandTypes(machine.readUnsigned8(instrAddress + 1)),
          extractOperandTypes(machine.readUnsigned8(instrAddress + 2)));
      opTypesOffset = 3;
    } else {
      operandTypes =
          extractOperandTypes(machine.readUnsigned8(instrAddress + 1));
      opTypesOffset = 2;
    }
    return decodeVarInstruction(instrAddress, opCount, opcodeNum, operandTypes,
                                opTypesOffset - 1, opTypesOffset, false);
  }

  /**
   * Determines whether the instruction is a CALL_VS2 or CALL_VN2.
   * @param opCount operand count
   * @param opcodeNum opcode number
   * @return true if it CALL_VS2/CALL_VN2, false otherwise
   */
  private boolean isVx2(OperandCount opCount, char opcodeNum) {
    return opCount == VAR &&
        (opcodeNum == VAR_CALL_VN2 || opcodeNum == VAR_CALL_VS2);

  }

  /**
   * Join two int arrays which are not null.
   * @param arr1 the first int array
   * @param arr2 the second int array
   * @return the concatenation of the two input arrays
   */
  private int[] joinArrays(int[] arr1, int[] arr2) {
    int[] result = new int[arr1.length + arr2.length];
    System.arraycopy(arr1, 0, result, 0, arr1.length);
    System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
    return result;
  }

  /**
   * Decodes an instruction in extended form. Is really just a variation of
   * variable form and delegates to decodeVarInstruction.
   * @param instrAddress instruction address
   * @return the decoded instruction
   */
  private Instruction decodeExtended(int instrAddress) {
    return decodeVarInstruction(instrAddress, EXT,
        machine.readUnsigned8(instrAddress + 1),
        extractOperandTypes(machine.readUnsigned8(instrAddress + 2)), 1, 3,
                            true);
  }

  /**
   * Decode VAR form instruction.
   * @param instrAddress instruction address
   * @param opCount operand count
   * @param opcodeNum opcode number
   * @param operandTypes operand types
   * @param numOperandTypeBytes number of operand type bytes
   * @param opTypesOffset operand types offset
   * @param isExtended indicator of extended instruction
   * @return instruction object
   */
  private Instruction decodeVarInstruction(int instrAddress,
                                           OperandCount opCount,
                                           char opcodeNum,
                                           int[] operandTypes,
                                           int numOperandTypeBytes,
                                           int opTypesOffset,
                                           boolean isExtended) {
    char[] operands = extractOperands(instrAddress + opTypesOffset,
                                      operandTypes);
    int numOperandBytes = getNumOperandBytes(operandTypes);
    // it is important to note that extended instructions have an extra byte
    // since the first byte is always $be
    int numExtraOpcodeBytes = isExtended ? 1 : 0;
    int currentAddr = instrAddress + opTypesOffset + numOperandBytes;
    return createInstruction(opCount, instrAddress, opcodeNum, currentAddr,
                             numExtraOpcodeBytes + numOperandBytes +
                               numOperandTypeBytes,
                             0, operandTypes, operands, null);
  }

  /**
   * The generic part of instruction decoding, extracting store variable
   * and branch offset is always the same for all instruction forms.
   * @param opCount the OperandCount
   * @param instrAddress the instruction address
   * @param opcodeNum the opcode number
   * @param addrAfterOperands the address after the operands
   * @param numOperandBytes the number of operand bytes
   * @param zsciiLength the length of the ZSCII in bytes if a print instruction
   * @param operandTypes the operand types
   * @param operands the operand values
   * @param str the ZSCII string or null
   * @return the instruction
   */
  private Instruction createInstruction(OperandCount opCount,
                                        int instrAddress,
                                        char opcodeNum,
                                        int addrAfterOperands,
                                        int numOperandBytes,
                                        int zsciiLength,
                                        int[] operandTypes, char[] operands,
                                        String str) {
    int currentAddr = addrAfterOperands;
    int storeVarLen = 0;
    char storeVar = 0;
    Operand[] instrOperands = createOperands(operandTypes, operands);
    InstructionInfo info = INFO_DB.getInfo(opCount, opcodeNum,
                                          machine.getVersion());
    if (info == null) {
      System.out.printf("ILLEGAL operation, instrAddr: $%04x OC: %s, " +
                        "opcode: #$%02x, Version: %d\n",
                        instrAddress, opCount.toString(), (int) opcodeNum,
                        machine.getVersion());
      throw new java.lang.UnsupportedOperationException("Exit !!");
    }
    if (info.isStore()) {
      storeVar = machine.readUnsigned8(currentAddr);
      currentAddr++;
      storeVarLen = LEN_STORE_VARIABLE;
    }
    BranchInfo branchInfo = DUMMY_BRANCH_INFO;
    if (info.isBranch()) {
      branchInfo = getBranchInfo(currentAddr);
    }
    int opcodeLength = LEN_OPCODE + numOperandBytes + storeVarLen +
            branchInfo.numOffsetBytes + zsciiLength;
    //System.out.printf("OPCODELEN: %d, len opcode: %d, # operand bytes: %d, " +
    //                  "len storevar: %d, broffsetbytes: %d, zsciilen: %d\n",
    //                  opcodeLength, LEN_OPCODE, numOperandBytes, storeVarLen,
    //                  branchInfo.numOffsetBytes, zsciiLength);
    switch (opCount) {
      case C0OP:
        return new C0OpInstruction(machine, opcodeNum, instrOperands, str,
            storeVar, branchInfo, opcodeLength);
      case C1OP:
        return new C1OpInstruction(machine, opcodeNum, instrOperands,
            storeVar, branchInfo, opcodeLength);
      case C2OP:
        return new C2OpInstruction(machine, opcodeNum, instrOperands,
            storeVar, branchInfo, opcodeLength);
      case VAR:
        return new VarInstruction(machine, opcodeNum, instrOperands,
            storeVar, branchInfo, opcodeLength);
      case EXT:
        return new ExtInstruction(machine, opcodeNum, instrOperands,
            storeVar, branchInfo, opcodeLength);
      default:
        break;
    }
    return null;
  }

  /**
   * Create operands objects.
   * @param operandTypes operand types.
   * @param operands operand values
   * @return array of operand objects
   */
  private Operand[] createOperands(int[] operandTypes, char[] operands) {
    Operand[] result = new Operand[operandTypes.length];
    for (int i = 0; i < operandTypes.length; i++) {
      result[i] = new Operand(operandTypes[i], operands[i]);
    }
    return result;
  }

  // ************************************************************************
  // ***** Helper functions
  // ********************************
  private static final int NUM_OPERAND_TYPES_PER_BYTE = 4;

  /**
   * Extracts operand types.
   * @param opTypeByte operand type byte
   * @return operand types
   */
  private int[] extractOperandTypes(char opTypeByte) {
    int[] opTypes = new int[NUM_OPERAND_TYPES_PER_BYTE];
    int numTypes;
    for (numTypes = 0; numTypes < NUM_OPERAND_TYPES_PER_BYTE; numTypes++) {
      int opType = getOperandType(opTypeByte, numTypes);
      if (opType == Operand.TYPENUM_OMITTED) break;
      opTypes[numTypes] = opType;
    }
    int[] result = new int[numTypes];
    for (int i = 0; i < numTypes; i++) {
      result[i] = opTypes[i];
    }
    return result;
  }

  /**
   * Extract operands.
   * @param operandAddr operand address
   * @param operandTypes operand types
   * @return operands
   */
  private char[] extractOperands(int operandAddr, int[] operandTypes) {
    char[] result = new char[operandTypes.length];
    int currentAddr = operandAddr;
    for (int i = 0; i < operandTypes.length; i++) {
      if (operandTypes[i] == Operand.TYPENUM_LARGE_CONSTANT) {
        result[i] = machine.readUnsigned16(currentAddr);
        currentAddr += 2;
      } else {
        result[i] = machine.readUnsigned8(currentAddr);
        currentAddr++;
      }
    }
    return result;
  }

  /**
   * Returns total number of operand bytes.
   * @param operandTypes operand types
   * @return total operand bytes
   */
  private int getNumOperandBytes(int[] operandTypes) {
    int result = 0;
    for (int i = 0; i < operandTypes.length; i++) {
      result += operandTypes[i] == Operand.TYPENUM_LARGE_CONSTANT ? 2 : 1;
    }
    return result;
  }

  /**
   * Extracts the operand type at the specified position of the op type byte.
   * @param opTypeByte the op type byte
   * @param pos the position
   * @return the operand type
   */
  private int getOperandType(char opTypeByte, int pos) {
    return ((opTypeByte >>> (6 - pos * 2)) & 0x03);
  }

  /**
   * Extract the branch information at the specified address
   * @param branchInfoAddr the branch info address
   * @return the BranchInfo object
   */
  private BranchInfo getBranchInfo(int branchInfoAddr) {
    char branchByte1 = machine.readUnsigned8(branchInfoAddr);
    boolean branchOnTrue = (branchByte1 & BIT_7) != 0;
    int numOffsetBytes, branchOffset;
    if (isSimpleOffset(branchByte1)) {
      numOffsetBytes = 1;
      branchOffset = branchByte1 & LOWER_6_BITS;
    } else {
      numOffsetBytes = 2;
      char branchByte2 = machine.readUnsigned8(branchInfoAddr + 1);
      //System.out.printf("14 Bit offset, bracnh byte1: %02x byte2: %02x\n",
      //                  (int) branchByte1, (int) branchByte2);
      branchOffset =
          toSigned14((char) (((branchByte1 << 8) | branchByte2) & 0x3fff));
    }
    return new BranchInfo(branchOnTrue, numOffsetBytes,
                          branchInfoAddr + numOffsetBytes,
                          (short) branchOffset);
  }

  /**
   * Determines whether the branch is a simple or compound (2 byte) offset.
   * @param branchByte1 the first branch byte
   * @return true if simple offset, false if compound
   */
  private boolean isSimpleOffset(char branchByte1) {
    return (branchByte1 & BIT_6) != 0;
  }

  private static final short WORD_14_UNSIGNED_MAX = 16383;
  private static final short WORD_14_SIGNED_MAX   = 8191;

  /**
   * Helper function to extract a 14 bit signed branch offset.
   * @param value the value to convert
   * @return the signed offset
   */
  private short toSigned14(char value) {
    return (short) (value > WORD_14_SIGNED_MAX ?
      -(WORD_14_UNSIGNED_MAX - (value - 1)) : value);
  }

  /**
   * Returns the operand at the specified address.
   * @param operandAddress operand address
   * @param operandType operand type
   * @return operand value
   */
  private char getOperandAt(int operandAddress, int operandType) {
    return operandType == Operand.TYPENUM_LARGE_CONSTANT ?
      machine.readUnsigned16(operandAddress) :
      machine.readUnsigned8(operandAddress);
  }

  /**
   * Determines the operand length of a specified type in bytes.
   * @param operandType the operand type
   * @return the number of bytes for the type
   */
  private int getOperandLength(int operandType) {
    switch (operandType) {
      case Operand.TYPENUM_SMALL_CONSTANT: return 1;
      case Operand.TYPENUM_LARGE_CONSTANT: return 2;
      case Operand.TYPENUM_VARIABLE:       return 1;
      default: return 0;
    }
  }

  /**
   * Determine the instruction form from the first instruction byte.
   * @param byte1 the first instruction byte
   * @return the InstructionForm
   */
  private InstructionForm getForm(char byte1) {
    if (byte1 == EXTENDED_MASK) return EXTENDED;
    if ((byte1 & VAR_MASK) == VAR_MASK) return VARIABLE;
    if ((byte1 & SHORT_MASK) == SHORT_MASK) return SHORT;
    return LONG;
  }
}
