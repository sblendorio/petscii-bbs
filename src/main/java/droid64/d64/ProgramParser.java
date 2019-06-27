package droid64.d64;

/**
 * <pre style='font-family:sans-serif;'>
 * Created on 9.01.2017
 *
 *   droiD64 - A graphical file manager for D64 files
 *   Copyright (C) 2004 Wolfram Heyer
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   http://droid64.sourceforge.net
 *
 * @author henrik
 * </pre>
 */
public class ProgramParser {

	/** Used for quick translation from byte to string. */
	private static final String[] HEX = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C",
			"0D", "0E", "0F", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E",
			"1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30",
			"31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41", "42",
			"43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53", "54",
			"55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65", "66",
			"67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74", "75", "76", "77", "78",
			"79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A",
			"8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C",
			"9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE",
			"AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0",
			"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1", "D2",
			"D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3", "E4",
			"E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6",
			"F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

	enum AddrMode {
		INDIRECT, XIND, INDY, ZEROPAGE, ZEROPAGEX, ZEROPAGEY, IMPLIED, IMMEDIATE, ACC, ABS, ABSX, ABSY, RELATIVE
	}

	private static final String OP_ADC = "ADC"; // Add with carry
	private static final String OP_AND = "AND"; // AND
	private static final String OP_ASL = "ASL"; // Arithmetic shift left
	private static final String OP_BCC = "BCC"; // Branch on carry clear
	private static final String OP_BCS = "BCS"; // Branch on carry set
	private static final String OP_BEQ = "BEQ"; // Branch on equal
	private static final String OP_BIT = "BIT"; // Bit test
	private static final String OP_BMI = "BMI"; // Branch on minus
	private static final String OP_BNE = "BNE"; // Branch on not equal
	private static final String OP_BPL = "BPL"; // Branch on plus
	private static final String OP_BRK = "BRK"; // Break
	private static final String OP_BVC = "BVC"; // Branch on overflow clear
	private static final String OP_BVS = "BVS"; // Branch on overflow set
	private static final String OP_CLC = "CLC"; // Clear carry
	private static final String OP_CLD = "CLD"; // Clear decimal
	private static final String OP_CLI = "CLI"; // Clear interrupt disable
	private static final String OP_CLV = "CLV"; // Clear overflow
	private static final String OP_CMP = "CMP"; // compare with accumulator
	private static final String OP_CPX = "CPX"; // Compare with X
	private static final String OP_CPY = "CPY"; // Compare with Y
	private static final String OP_DEC = "DEC"; // Decrement accumulator
	private static final String OP_DEX = "DEX"; // Decrement X
	private static final String OP_DEY = "DEY"; // Decrement Y
	private static final String OP_EOR = "EOR"; // XOR
	private static final String OP_INC = "INC"; // Increment accumulator
	private static final String OP_INX = "INX"; // Increment X
	private static final String OP_INY = "INY"; // Increment Y
	private static final String OP_JMP = "JMP"; // Jump
	private static final String OP_JSR = "JSR"; // Jump subroutine
	private static final String OP_LDA = "LDA"; // Load accumulator
	private static final String OP_LDX = "LDX"; // Load X
	private static final String OP_LDY = "LDY"; // Load Y
	private static final String OP_LSR = "LSR"; // Logic shift right
	private static final String OP_NOP = "NOP"; // No operation
	private static final String OP_ORA = "ORA"; // OR
	private static final String OP_PHA = "PHA"; // Push accumulator
	private static final String OP_PHP = "PHP"; // Push processor status
	private static final String OP_PLA = "PLA"; // Pull accumulator
	private static final String OP_PLP = "PLP"; // Pull processor status
	private static final String OP_ROL = "ROL"; // Rotate left
	private static final String OP_ROR = "ROR"; // Rotate right
	private static final String OP_RTI = "RTI"; // Return from interrupt
	private static final String OP_RTS = "RTS"; // Return from subroutine
	private static final String OP_SBC = "SBC"; // Subtract with carry
	private static final String OP_SEC = "SEC"; // Set carry
	private static final String OP_SED = "SED"; // Set decimal
	private static final String OP_SEI = "SEI"; // Set interrupt disable
	private static final String OP_STA = "STA"; // Store accumulator
	private static final String OP_STX = "STX"; // Store X
	private static final String OP_STY = "STY"; // Store Y
	private static final String OP_TAX = "TAX"; // Transfer accumulator to X
	private static final String OP_TAY = "TAY"; // Transfer accumulator to Y
	private static final String OP_TSX = "TSX"; // Transfer stack pointer to X
	private static final String OP_TXA = "TXA"; // Transfer X to accumulator
	private static final String OP_TXS = "TXS"; // Transfer X to stack pointer
	private static final String OP_TYA = "TYA"; // Transfer Y to accumulator

	// Illegal opcodes (from http://www.textfiles.com/programming/opcod6502.txt)
	private static final String OPX_ARR = "ARR"; // AND with accumulator, then rotate one bit right in accumulator and check bit 5 and 6
	private static final String OPX_ISB = "ISB"; // Increase memory by one, then subtract memory from accumulator (with borrow)
	private static final String OPX_DCP = "DCP"; // Subtract 1 from memory (without borrow).
	private static final String OPX_ASR = "ASR"; // AND byte with accumulator, then shift right one bit in accumulator
	private static final String OPX_ATX = "ATX"; // AND byte with accumulator, then transfer accumulator to X register
	private static final String OPX_AXA = "AXA"; // AND X register with accumulator then AND result with 7 and store in memory
	private static final String OPX_AXS = "AXS"; // AND X register with accumulator and store result in X register, then subtract byte from X register (without borrow)
	private static final String OPX_DOP = "DOP"; // Double no operation
	private static final String OPX_JAM = "JAM"; // Stop program counter (processor lock up)
	private static final String OPX_LAE = "LAE"; // AND memory with stack pointer, transfer result to accumulator, X register and stack pointer
	private static final String OPX_LAX = "LAX"; // Load accumulator and X register with memory
	private static final String OPX_NOP = "NOP"; // No operation
	private static final String OPX_RLA = "RLA"; // Rotate one bit left in memory, then AND accumulator with memory
	private static final String OPX_RRA = "RRA"; // Rotate one bit right in memory, then add memory to accumulator (with carry)
	private static final String OPX_SBC = "SBC"; // The same as the legal opcode $E9 (SBC #byte)
	private static final String OPX_SLO = "SLO"; // Shift left one bit in memory, then OR accumulator with memory
	private static final String OPX_SRE = "SRE"; // Shift right one bit in memory, then XOR accumulator with memory
	private static final String OPX_SHX = "SHX"; // AND X register with the high byte of the target address of the argument + 1. Store the result in memory.
	private static final String OPX_SHY = "SHY"; // AND Y register with the high byte of the target address of the argument + 1. Store the result in memory.
	private static final String OPX_TOP = "TOP"; // Triple no operation
	private static final String OPX_ANE = "ANE"; // Exact operation unknown
	private static final String OPX_SHS = "SHS"; // AND X register with accumulator and store result in stack pointer, then AND stack pointer with the high byte of the
	// target address of the argument + 1. Store result in memory.
	private static final String OPX_SAX = "SAX"; // AND X register with accumulator and store result in memory
	private static final String OPX_ANC = "ANC"; // AND byte with accumulator. If result is negative then carry is set

	private static final Op[] OPS = { new Op(OP_BRK, 0x00, AddrMode.IMPLIED), new Op(OP_ORA, 0x01, AddrMode.XIND),
			new Op(OPX_JAM, 0x02, AddrMode.IMPLIED), new Op(OPX_SLO, 0x03, AddrMode.XIND),
			new Op(OPX_DOP, 0x04, AddrMode.ZEROPAGE), new Op(OP_ORA, 0x05, AddrMode.ZEROPAGE),
			new Op(OP_ASL, 0x06, AddrMode.ZEROPAGE), new Op(OPX_SLO, 0x07, AddrMode.ZEROPAGE),
			new Op(OP_PHP, 0x08, AddrMode.IMPLIED), new Op(OP_ORA, 0x09, AddrMode.IMMEDIATE),
			new Op(OP_ASL, 0x0a, AddrMode.ACC), new Op(OPX_ANC, 0x0b, AddrMode.IMMEDIATE),
			new Op(OPX_TOP, 0x0c, AddrMode.ABS), new Op(OP_ORA, 0x0d, AddrMode.ABS), new Op(OP_ASL, 0x0e, AddrMode.ABS),
			new Op(OPX_SLO, 0x0f, AddrMode.ABS), new Op(OP_BPL, 0x10, AddrMode.RELATIVE),
			new Op(OP_ORA, 0x11, AddrMode.INDY), new Op(OPX_JAM, 0x12, AddrMode.IMPLIED),
			new Op(OPX_SLO, 0x13, AddrMode.INDY), new Op(OPX_DOP, 0x14, AddrMode.ZEROPAGEX),
			new Op(OP_ORA, 0x15, AddrMode.ZEROPAGEX), new Op(OP_ASL, 0x16, AddrMode.ZEROPAGEX),
			new Op(OPX_SLO, 0x17, AddrMode.ZEROPAGEX), new Op(OP_CLC, 0x18, AddrMode.IMPLIED),
			new Op(OP_ORA, 0x19, AddrMode.ABSY), new Op(OPX_NOP, 0x1a, AddrMode.IMPLIED),
			new Op(OPX_SLO, 0x1b, AddrMode.ABSY), new Op(OPX_TOP, 0x1c, AddrMode.ABSX),
			new Op(OP_ORA, 0x1d, AddrMode.ABSX), new Op(OP_ASL, 0x1e, AddrMode.ABSX),
			new Op(OPX_SLO, 0x1f, AddrMode.ABSX), new Op(OP_JSR, 0x20, AddrMode.ABS),
			new Op(OP_AND, 0x21, AddrMode.INDY), new Op(OPX_JAM, 0x22, AddrMode.IMPLIED),
			new Op(OPX_RLA, 0x23, AddrMode.XIND), new Op(OP_BIT, 0x24, AddrMode.ZEROPAGE),
			new Op(OP_AND, 0x25, AddrMode.ZEROPAGE), new Op(OP_ROL, 0x26, AddrMode.ZEROPAGE),
			new Op(OPX_RLA, 0x27, AddrMode.ZEROPAGE), new Op(OP_PLP, 0x28, AddrMode.IMPLIED),
			new Op(OP_AND, 0x29, AddrMode.IMMEDIATE), new Op(OP_ROL, 0x2a, AddrMode.ACC),
			new Op(OPX_ANC, 0x2b, AddrMode.IMMEDIATE), new Op(OP_BIT, 0x2c, AddrMode.ABS),
			new Op(OP_AND, 0x2d, AddrMode.ABS), new Op(OP_ROL, 0x2e, AddrMode.ABS), new Op(OPX_RLA, 0x2f, AddrMode.ABS),
			new Op(OP_BMI, 0x30, AddrMode.RELATIVE), new Op(OP_AND, 0x31, AddrMode.INDY),
			new Op(OPX_JAM, 0x32, AddrMode.IMPLIED), new Op(OPX_RLA, 0x33, AddrMode.INDY),
			new Op(OPX_DOP, 0x34, AddrMode.ZEROPAGEX), new Op(OP_AND, 0x35, AddrMode.ZEROPAGEX),
			new Op(OP_ROL, 0x36, AddrMode.ZEROPAGEX), new Op(OPX_RLA, 0x37, AddrMode.ZEROPAGEX),
			new Op(OP_SEC, 0x38, AddrMode.IMPLIED), new Op(OP_AND, 0x39, AddrMode.ABSY),
			new Op(OPX_NOP, 0x3a, AddrMode.IMPLIED), new Op(OPX_RLA, 0x3b, AddrMode.ABSY),
			new Op(OPX_TOP, 0x3c, AddrMode.ABSX), new Op(OP_AND, 0x3d, AddrMode.ABSX),
			new Op(OP_ROL, 0x3e, AddrMode.ABSX), new Op(OPX_RLA, 0x3f, AddrMode.ABSX),
			new Op(OP_RTI, 0x40, AddrMode.IMPLIED), new Op(OP_EOR, 0x41, AddrMode.XIND),
			new Op(OPX_JAM, 0x42, AddrMode.IMPLIED), new Op(OPX_SRE, 0x43, AddrMode.XIND),
			new Op(OPX_DOP, 0x44, AddrMode.ZEROPAGE), new Op(OP_EOR, 0x45, AddrMode.ZEROPAGE),
			new Op(OP_LSR, 0x46, AddrMode.ZEROPAGE), new Op(OPX_SRE, 0x47, AddrMode.ZEROPAGE),
			new Op(OP_PHA, 0x48, AddrMode.IMPLIED), new Op(OP_EOR, 0x49, AddrMode.IMMEDIATE),
			new Op(OP_LSR, 0x4a, AddrMode.ACC), new Op(OPX_ASR, 0x4b, AddrMode.IMMEDIATE),
			new Op(OP_JMP, 0x4c, AddrMode.ABS), new Op(OP_EOR, 0x4d, AddrMode.ABS), new Op(OP_LSR, 0x4e, AddrMode.ABS),
			new Op(OPX_SRE, 0x4f, AddrMode.ABS), new Op(OP_BVC, 0x50, AddrMode.RELATIVE),
			new Op(OP_EOR, 0x51, AddrMode.INDY), new Op(OPX_JAM, 0x52, AddrMode.IMPLIED),
			new Op(OPX_SRE, 0x53, AddrMode.INDY), new Op(OPX_DOP, 0x54, AddrMode.ZEROPAGEX),
			new Op(OP_EOR, 0x55, AddrMode.ZEROPAGEX), new Op(OP_LSR, 0x56, AddrMode.ZEROPAGEX),
			new Op(OPX_SRE, 0x57, AddrMode.ZEROPAGEX), new Op(OP_CLI, 0x58, AddrMode.IMPLIED),
			new Op(OP_EOR, 0x59, AddrMode.ABSY), new Op(OPX_NOP, 0x5a, AddrMode.IMPLIED),
			new Op(OPX_SRE, 0x5b, AddrMode.ABSY), new Op(OPX_TOP, 0x5c, AddrMode.ABSX),
			new Op(OP_EOR, 0x5d, AddrMode.ABSX), new Op(OP_LSR, 0x5e, AddrMode.ABSX),
			new Op(OPX_SRE, 0x5f, AddrMode.ABSX), new Op(OP_RTS, 0x60, AddrMode.IMPLIED),
			new Op(OP_ADC, 0x61, AddrMode.XIND), new Op(OPX_JAM, 0x62, AddrMode.IMPLIED),
			new Op(OPX_RRA, 0x63, AddrMode.XIND), new Op(OPX_DOP, 0x64, AddrMode.ZEROPAGE),
			new Op(OP_ADC, 0x65, AddrMode.ZEROPAGE), new Op(OP_ROR, 0x66, AddrMode.ZEROPAGE),
			new Op(OPX_RRA, 0x67, AddrMode.ZEROPAGE), new Op(OP_PLA, 0x68, AddrMode.IMPLIED),
			new Op(OP_ADC, 0x69, AddrMode.IMMEDIATE), new Op(OP_ROR, 0x6a, AddrMode.ACC),
			new Op(OPX_ARR, 0x6b, AddrMode.IMMEDIATE), new Op(OP_JMP, 0x6c, AddrMode.INDIRECT),
			new Op(OP_ADC, 0x6d, AddrMode.ABS), new Op(OP_ROR, 0x6e, AddrMode.ABS), new Op(OPX_RRA, 0x6f, AddrMode.ABS),
			new Op(OP_BVS, 0x70, AddrMode.RELATIVE), new Op(OP_ADC, 0x71, AddrMode.INDY),
			new Op(OPX_JAM, 0x72, AddrMode.IMPLIED), new Op(OPX_RRA, 0x73, AddrMode.INDY),
			new Op(OPX_DOP, 0x74, AddrMode.ZEROPAGEX), new Op(OP_ADC, 0x75, AddrMode.ZEROPAGEX),
			new Op(OP_ROR, 0x76, AddrMode.ZEROPAGEX), new Op(OPX_RRA, 0x77, AddrMode.ZEROPAGEX),
			new Op(OP_SEI, 0x78, AddrMode.IMPLIED), new Op(OP_ADC, 0x79, AddrMode.ABSY),
			new Op(OPX_NOP, 0x7a, AddrMode.IMPLIED), new Op(OPX_RRA, 0x7b, AddrMode.ABSY),
			new Op(OPX_TOP, 0x7c, AddrMode.ABSX), new Op(OP_ADC, 0x7d, AddrMode.ABSX),
			new Op(OP_ROR, 0x7e, AddrMode.ABSX), new Op(OPX_RRA, 0x7f, AddrMode.ABSX),
			new Op(OPX_DOP, 0x80, AddrMode.IMMEDIATE), new Op(OP_STA, 0x81, AddrMode.XIND),
			new Op(OPX_DOP, 0x82, AddrMode.IMMEDIATE), new Op(OPX_SAX, 0x83, AddrMode.ZEROPAGE),
			new Op(OP_STY, 0x84, AddrMode.ZEROPAGE), new Op(OP_STA, 0x85, AddrMode.ZEROPAGE),
			new Op(OP_STX, 0x86, AddrMode.ZEROPAGE), new Op(OPX_SAX, 0x87, AddrMode.ZEROPAGE),
			new Op(OP_DEY, 0x88, AddrMode.IMPLIED), new Op(OPX_DOP, 0x89, AddrMode.IMMEDIATE),
			new Op(OP_TXA, 0x8a, AddrMode.IMPLIED), new Op(OPX_ANE, 0x8b, AddrMode.IMMEDIATE),
			new Op(OP_STY, 0x8c, AddrMode.ABS), new Op(OP_STA, 0x8d, AddrMode.ABS), new Op(OP_STX, 0x8e, AddrMode.ABS),
			new Op(OPX_SAX, 0x8f, AddrMode.ABS), new Op(OP_BCC, 0x90, AddrMode.RELATIVE),
			new Op(OP_STA, 0x91, AddrMode.INDY), new Op(OPX_JAM, 0x92, AddrMode.IMPLIED),
			new Op(OPX_AXA, 0x93, AddrMode.INDY), new Op(OP_STY, 0x94, AddrMode.ZEROPAGEX),
			new Op(OP_STA, 0x95, AddrMode.ZEROPAGEX), new Op(OP_STX, 0x96, AddrMode.ZEROPAGEY),
			new Op(OPX_SAX, 0x97, AddrMode.ZEROPAGEY), new Op(OP_TYA, 0x98, AddrMode.IMPLIED),
			new Op(OP_STA, 0x99, AddrMode.ABSY), new Op(OP_TXS, 0x9a, AddrMode.IMPLIED),
			new Op(OPX_SHS, 0x9b, AddrMode.ABSY), new Op(OPX_SHY, 0x9c, AddrMode.ABSX),
			new Op(OP_STA, 0x9d, AddrMode.ABSX), new Op(OPX_SHX, 0x9e, AddrMode.ABSY),
			new Op(OPX_AXA, 0x9f, AddrMode.ABSY), new Op(OP_LDY, 0xa0, AddrMode.IMMEDIATE),
			new Op(OP_LDA, 0xa1, AddrMode.XIND), new Op(OP_LDX, 0xa2, AddrMode.IMMEDIATE),
			new Op(OPX_LAX, 0xa3, AddrMode.XIND), new Op(OP_LDY, 0xa4, AddrMode.ZEROPAGE),
			new Op(OP_LDA, 0xa5, AddrMode.ZEROPAGE), new Op(OP_LDX, 0xa6, AddrMode.ZEROPAGE),
			new Op(OPX_LAX, 0xa7, AddrMode.ZEROPAGE), new Op(OP_TAY, 0xa8, AddrMode.IMPLIED),
			new Op(OP_LDA, 0xa9, AddrMode.IMMEDIATE), new Op(OP_TAX, 0xaa, AddrMode.IMPLIED),
			new Op(OPX_ATX, 0xab, AddrMode.IMMEDIATE), new Op(OP_LDY, 0xac, AddrMode.ABS),
			new Op(OP_LDA, 0xad, AddrMode.ABS), new Op(OP_LDX, 0xae, AddrMode.ABS), new Op(OPX_LAX, 0xaf, AddrMode.ABS),
			new Op(OP_BCS, 0xb0, AddrMode.RELATIVE), new Op(OP_LDA, 0xb1, AddrMode.INDY),
			new Op(OPX_JAM, 0xb2, AddrMode.IMPLIED), new Op(OPX_LAX, 0xb3, AddrMode.INDY),
			new Op(OP_LDY, 0xb4, AddrMode.ZEROPAGEX), new Op(OP_LDA, 0xb5, AddrMode.ZEROPAGEX),
			new Op(OP_LDX, 0xb6, AddrMode.ZEROPAGEY), new Op(OPX_LAX, 0xb7, AddrMode.ZEROPAGEY),
			new Op(OP_CLV, 0xb8, AddrMode.IMPLIED), new Op(OP_LDA, 0xb9, AddrMode.ABSY),
			new Op(OP_TSX, 0xba, AddrMode.IMPLIED), new Op(OPX_LAE, 0xbb, AddrMode.ABSY),
			new Op(OP_LDY, 0xbc, AddrMode.ABSX), new Op(OP_LDA, 0xbd, AddrMode.ABSX),
			new Op(OP_LDX, 0xbe, AddrMode.ABSY), new Op(OPX_LAX, 0xbf, AddrMode.ABSY),
			new Op(OP_CPY, 0xc0, AddrMode.IMMEDIATE), new Op(OP_CMP, 0xc1, AddrMode.XIND),
			new Op(OPX_DOP, 0xc2, AddrMode.IMMEDIATE), new Op(OPX_DCP, 0xc3, AddrMode.XIND),
			new Op(OP_CPY, 0xc4, AddrMode.ZEROPAGE), new Op(OP_CMP, 0xc5, AddrMode.ZEROPAGE),
			new Op(OP_DEC, 0xc6, AddrMode.ZEROPAGE), new Op(OPX_DCP, 0xc7, AddrMode.ZEROPAGE),
			new Op(OP_INY, 0xc8, AddrMode.IMPLIED), new Op(OP_CMP, 0xc9, AddrMode.IMMEDIATE),
			new Op(OP_DEX, 0xca, AddrMode.IMPLIED), new Op(OPX_AXS, 0xcb, AddrMode.IMMEDIATE),
			new Op(OP_CPY, 0xcc, AddrMode.ABS), new Op(OP_CMP, 0xcd, AddrMode.ABS), new Op(OP_DEC, 0xce, AddrMode.ABS),
			new Op(OPX_DCP, 0xcf, AddrMode.ABS), new Op(OP_BNE, 0xd0, AddrMode.RELATIVE),
			new Op(OP_CMP, 0xd1, AddrMode.INDY), new Op(OPX_JAM, 0xd2, AddrMode.IMPLIED),
			new Op(OPX_DCP, 0xd3, AddrMode.INDY), new Op(OPX_DOP, 0xd4, AddrMode.ZEROPAGEX),
			new Op(OP_CMP, 0xd5, AddrMode.ZEROPAGEX), new Op(OP_DEC, 0xd6, AddrMode.ZEROPAGEX),
			new Op(OPX_DCP, 0xd7, AddrMode.ZEROPAGEX), new Op(OP_CLD, 0xd8, AddrMode.IMPLIED),
			new Op(OP_CMP, 0xd9, AddrMode.ABSY), new Op(OPX_NOP, 0xda, AddrMode.IMPLIED),
			new Op(OPX_DCP, 0xdb, AddrMode.ABSY), new Op(OPX_TOP, 0xdc, AddrMode.ABSX),
			new Op(OP_CMP, 0xdd, AddrMode.ABSX), new Op(OP_DEC, 0xde, AddrMode.ABSX),
			new Op(OPX_DCP, 0xdf, AddrMode.ABSX), new Op(OP_CPX, 0xe0, AddrMode.IMMEDIATE),
			new Op(OP_SBC, 0xe1, AddrMode.XIND), new Op(OPX_DOP, 0xe2, AddrMode.IMMEDIATE),
			new Op(OPX_ISB, 0xe3, AddrMode.XIND), new Op(OP_CPX, 0xe4, AddrMode.ZEROPAGE),
			new Op(OP_SBC, 0xe5, AddrMode.ZEROPAGE), new Op(OP_INC, 0xe6, AddrMode.ZEROPAGE),
			new Op(OPX_ISB, 0xe7, AddrMode.ZEROPAGE), new Op(OP_INX, 0xe8, AddrMode.IMPLIED),
			new Op(OP_SBC, 0xe9, AddrMode.IMMEDIATE), new Op(OP_NOP, 0xea, AddrMode.IMPLIED),
			new Op(OPX_SBC, 0xeb, AddrMode.IMMEDIATE), new Op(OP_CPX, 0xec, AddrMode.ABS),
			new Op(OP_SBC, 0xed, AddrMode.ABS), new Op(OP_INC, 0xee, AddrMode.ABS), new Op(OPX_ISB, 0xef, AddrMode.ABS),
			new Op(OP_BEQ, 0xf0, AddrMode.RELATIVE), new Op(OP_SBC, 0xf1, AddrMode.INDY),
			new Op(OPX_JAM, 0xf2, AddrMode.IMPLIED), new Op(OPX_ISB, 0xf3, AddrMode.INDY),
			new Op(OPX_DOP, 0xf4, AddrMode.ZEROPAGEX), new Op(OP_SBC, 0xf5, AddrMode.ZEROPAGEX),
			new Op(OP_INC, 0xf6, AddrMode.ZEROPAGEX), new Op(OPX_ISB, 0xf7, AddrMode.ZEROPAGEX),
			new Op(OP_SED, 0xf8, AddrMode.IMPLIED), new Op(OP_SBC, 0xf9, AddrMode.ABSY),
			new Op(OPX_NOP, 0xfa, AddrMode.IMPLIED), new Op(OPX_ISB, 0xfb, AddrMode.ABSY),
			new Op(OPX_TOP, 0xfc, AddrMode.ABSX), new Op(OP_SBC, 0xfd, AddrMode.ABSX),
			new Op(OP_INC, 0xfe, AddrMode.ABSX), new Op(OPX_ISB, 0xff, AddrMode.ABSX), };

	private static final String BLANK1 = "      "; // Spaces equals 1 hexbyte
	private static final String BLANK2 = "        "; // Spaces equal to 2 hexbytes
	private static final String DIRECT = "  $";
	private static final String DIRECT_X = ",X";
	private static final String DIRECT_Y = ",Y";
	private static final String INDIRECT = "  ($";
	private static final String SPACE1 = " ";
	private static final String SPACE2 = "  ";
	private static final String SPACE3 = "   ";

	private ProgramParser() {
		// No setup
	}

	public static String parse(final byte[] prg, int length, boolean readLoadAddr) {
		int loadAddr = readLoadAddr ? Utility.getInt16(prg, 0) : 2;
		int pos = readLoadAddr ? 2 : 0;
		StringBuilder buf = new StringBuilder();
		if (readLoadAddr) {
			buf.append("*=$").append(String.format("%04X", loadAddr)).append("\n\n");
		}
		while (prg != null && pos < length) {
			pos = parseOp(prg, pos, buf, loadAddr);
		}
		return buf.toString();
	}

	public static int getinstructionLength(byte opCode) {
		switch (OPS[opCode & 0xff].addrMode) {
		case ZEROPAGE:
		case IMMEDIATE:
		case RELATIVE:
		case XIND:
		case INDY:
		case ZEROPAGEX:
		case ZEROPAGEY:
			return 2;
		case INDIRECT:
		case ABS:
		case ABSX:
		case ABSY:
			return 3;
		case IMPLIED:
		case ACC:
		default: return 1;
		}
	}

	private static int parseOp(final byte[] prg, final int inpos, StringBuilder buf, final int loadAddr) {
		int pos = inpos;
		final Op op = OPS[prg[pos] & 0xff];
		buf.append(String.format("%04X: ", pos + loadAddr - 2)).append(HEX[op.code]).append(' ');
		switch (op.addrMode) {

		// 1 byte ops
		case IMPLIED:
			buf.append(BLANK2).append(op.name);
			break;
		case ACC:
			buf.append(BLANK2).append(op.name).append("  A");
			break;

			// 2 byte ops
		case ZEROPAGE:
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append(DIRECT).append(getInt8Str(prg, pos + 1));
			pos++;
			break;
		case IMMEDIATE:
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append("  #$").append(getInt8Str(prg, pos + 1));
			pos++;
			break;
		case RELATIVE:
			int sum = Utility.getInt8(prg, pos + 1) + loadAddr + pos;
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append(DIRECT).append(getInt16Str(sum));
			pos++;
			break;

			// 2 byte ops indexed
		case XIND:
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append(INDIRECT).append(getInt8Str(prg, pos + 1)).append(",X)");
			pos++;
			break;
		case INDY:
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append(INDIRECT).append(getInt8Str(prg, pos + 1)).append("),Y");
			pos++;
			break;
		case ZEROPAGEX:
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append(DIRECT).append(getInt8Str(prg, pos + 1)).append(DIRECT_X);
			pos++;
			break;
		case ZEROPAGEY:
			buf.append(getInt8Str(prg, pos + 1)).append(BLANK1).append(op.name).append(DIRECT).append(getInt8Str(prg, pos + 1)).append(DIRECT_Y);
			pos++;
			break;

			// 3 byte ops
		case INDIRECT:
			buf.append(getInt8Str(prg, pos + 1)).append(SPACE1).append(getInt8Str(prg, pos + 2)).append(SPACE2).append(op.name).append(INDIRECT).append(getInt16Str(prg, pos + 1)).append(')');
			pos += 2;
			break;
		case ABS:
			buf.append(getInt8Str(prg, pos + 1)).append(SPACE1).append(getInt8Str(prg, pos + 2)).append(SPACE3).append(op.name).append(DIRECT).append(getInt16Str(prg, pos + 1));
			pos += 2;
			break;
		case ABSX:
			buf.append(getInt8Str(prg, pos + 1)).append(SPACE1).append(getInt8Str(prg, pos + 2)).append(SPACE3).append(op.name).append(DIRECT).append(getInt16Str(prg, pos + 1)).append(DIRECT_X);
			pos += 2;
			break;
		case ABSY:
			buf.append(getInt8Str(prg, pos + 1)).append(SPACE1).append(getInt8Str(prg, pos + 2)).append(SPACE3).append(op.name).append(DIRECT).append(getInt16Str(prg, pos)).append(DIRECT_Y);
			pos += 2;
			break;

		}
		buf.append('\n');
		return pos + 1;
	}

	/**
	 * @param data
	 * @param pos
	 * @return unsigned byte as string from data[pos]
	 */
	private static String getInt8Str(byte[] data, int pos) {
		if (data == null || pos >= data.length) {
			return HEX[0];
		} else {
			return HEX[data[pos] & 0xff];
		}
	}

	/**
	 * Create hexadecimal string from lower 16 bits of num
	 *
	 * @param num
	 * @return String
	 */
	private static String getInt16Str(int num) {
		return HEX[num >>> 8 & 0xff] + HEX[num & 0xff];
	}

	/**
	 * Create hexadecimal string from 2 bytes a poistion pos in data.
	 *
	 * @param data
	 * @param pos
	 * @return String
	 */
	private static String getInt16Str(byte[] data, int pos) {
		int num = Utility.getInt16(data, pos);
		return HEX[num >>> 8 & 0xff] + HEX[num & 0xff];
	}

	/** Class for defining a MOS 6510 opcode */
	static final class Op {
		String name;
		int code;
		AddrMode addrMode;

		public Op(String name, int code, AddrMode addrMode) {
			this.name = name;
			this.addrMode = addrMode;
			this.code = code;
		}
	}

}
