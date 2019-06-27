package droid64.d64;

/**<pre style='font-family:sans-serif;'>
 * Created on 1.09.2015
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
public class BasicParser {

	/** The BASIC V7 tokens 0x80  to 0xFF */
	private static final String[] BASIC_V7_TOKENS = {
			// 0x80 - 0xff
			"END",     "FOR",    "NEXT",   "DATA",    "INPUT#",  "INPUT",  "DIM",       "READ",
			"LET",     "GOTO",   "RUN",    "IF",      "RESTORE", "GOSUB",  "RETURN",    "REM",
			"STOP",    "ON",     "WAIT",   "LOAD",    "SAVE",    "VERIFY", "DEF",       "POKE",
			"PRINT#",  "PRINT",  "CONT",   "LIST",    "CLR",     "CMD",    "SYS",       "OPEN",
			"CLOSE",   "GET",    "NEW",    "TAB(",    "TO",      "FN",     "SPC(",      "THEN",
			"NOT",     "STEP",   "+",      "-",	      "*",       "/",      "^",         "AND",
			"OR",      ">",      "=",      "<",	      "SGN",     "INT",    "ABS",       "USR",
			"FRE",     "POS",    "SQR",    "RND",     "LOG",     "EXP",    "COS",       "SIN",
			"TAN",     "ATN",    "PEEK",   "LEN",     "STR$",    "VAL",    "ASC",       "CHR$",
			"LEFT$",   "RIGHT$", "MID$",   "GO",   /* End of BASIC V2 */
			"RGR",     "RCLR",   "POT??",     "JOY",
			"RDOT",    "DEC",    "HEX",    "ERR$",    "INSTR",   "ELSE",   "RESUME",    "TRAP",
			"TRON",    "TROFF",  "SOUND",  "VOL",     "AUTO",    "PUDEF",  "GRAPHIC",   "PAINT",
			"CHAR",    "BOX",    "CIRCLE", "GSHAPE",  "SSHAPE",  "DRAW",   "LOCATE",    "COLOR",
			"SCNCLR",  "SCALE",  "HELP",   "DO",      "LOOP",    "EXIT",   "DIRECTORY", "DSAVE",
			"DLOAD",   "HEADER", "SCRATCH","COLLECT", "COPY",    "RENAME", "BACKUP",    "DELETE",
			"RENUMBER","KEY",    "MONITOR","USING",   "UNTIL",   "WHILE",  "BANK??",    "PI"
	};
	/** The second BASIC V7 tokens starting with 0xCE */
	private  static final String[]  BASIC_V7_CE_TOKENS = {
			// 0x00 - 0x0A (invalid: 0x00, 0x01)
			null,   null,       "POT",     "BUMP",  "PEN",  "RSPPOS",  "RSPRITE",  "RSPCOLOR",
			"XOR",  "RWINDOW",  "POINTER"
	};
	/** The second BASIC V7 tokens starting with 0xFE */
	private static final String[]  BASIC_V7_FE_TOKENS = {
			// 0x00 - 0x26 (invalid: 0x00, 0x01, 0x20, 0x022)
			null,       null,    "BANK",     "FILTER", "PLAY",    "TEMPO",  "MOVSPR", "SPRITE",
			"SPRCOLOR", "RREG",  "ENVELOPE", "SLEEP",  "CATALOG", "DOPEN",  "APPEND", "DCLOSE",
			"BSAVE",    "BLOAD", "RECORD",   "CONCAT", "DVERIFY", "DCLEAR", "SPRSAV", "COLLISION",
			"BEGIN",    "BEND",  "WINDOW",   "BOOT",   "WIDTH",   "SPRDEF", "QUIT",   "STASH",
			null,       "FETCH", null,       "SWAP",   "OFF",     "FAST",   "SLOW"
	};

	private BasicParser() {
	}

	public static String parseCbmBasicPrg(byte[] prg) {
		if (prg == null || prg.length < 4) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		ByteIterator iter = new ByteIterator(prg);
		iter.skip(2);	// loadaddr
		while (iter.hasNextInt16()) {
			int nextLine = iter.nextInt16();
			if (nextLine == 0 || !iter.hasNextInt16()) {
				break;
			}
			buf.append(iter.nextInt16()).append(' ');	// line number
			int quoteCount = 0;
			while (iter.hasNext()) {
				int op = iter.nextInt8();
				if (op == 0) {
					break;
				}
				quoteCount = parseOp(op, buf, iter, quoteCount);
			}
			buf.append('\n');
		}
		return buf.toString();
	}

	private static int parseOp(int op, StringBuilder buf, ByteIterator iter, int quoteCount) {
		if ((quoteCount & 1) == 0 && op >= 0x80 && op <= 0xff) {
			parseV7(op, buf, iter);
		} else if((quoteCount & 1) == 0 && op == 0xff) {
			buf.append("PI");
		} else {
			buf.append(Character.toChars(op));
			if (op == 0x22) {
				return quoteCount + 1;
			}
		}
		return quoteCount;
	}

	private static void parseV7(int op, StringBuilder buf, ByteIterator iter) {
		if (op == 0xce) {
			int op2 = iter.nextInt8();
			if (op2 >=0 && op2 < BASIC_V7_CE_TOKENS.length && BASIC_V7_CE_TOKENS[op2] != null) {
				buf.append(BASIC_V7_CE_TOKENS[op2]);
			} else {
				buf.append("0xCE").append(Integer.toHexString(op2)).append(")");
			}
		} else if (op == 0xfe) {
			int op2 = iter.nextInt8();
			if (op2 >=0 && op2 < BASIC_V7_FE_TOKENS.length && BASIC_V7_FE_TOKENS[op2] != null) {
				buf.append(BASIC_V7_FE_TOKENS[op2]);
			} else {
				buf.append("0xFE").append(Integer.toHexString(op2)).append(")");
			}
		} else {
			buf.append(BASIC_V7_TOKENS[op - 0x80]);
		}
	}
}
