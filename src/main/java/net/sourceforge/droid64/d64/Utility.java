package net.sourceforge.droid64.d64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**<pre style='font-family:sans-serif;'>
 * Created on 07.07.2017<br>
 *
 *   droiD64 - A graphical filemanager for D64 files<br>
 *   Copyright (C) 2004 Wolfram Heyer<br>
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
 * @author henrik
 * </pre>
 */
public class Utility {

	public static final String EMPTY = "";
	public static final String SPACE = " ";
	/** PETSCII padding white space character */
	public static final byte BLANK = (byte) 0xa0;

	private static final String ERR_REQUIRED_DATA_MISSING = "Required data is missing. ";
	private static final String ERR_READ_ERROR = "Failed to read from file. ";
	private static final String ERR_ZIP_READ_ERROR = "Failed to read zip file. ";
	private static final String ERR_ZIP_WRITE_ERROR = "Failed to write zip file. ";

	/** Size of buffer reading compressed data */
	private static final int INPUT_BUFFER_SIZE = 65536;
	/** Size of buffer writing uncompressed data to byte[] */
	private static final int OUTPUT_BUFFER_SIZE = 1048576;

	/** PETSCII-ASCII mappings (ASCII to PETSCII mapping. Using 0x20 for invisible characters in PETSCII charset) */
	protected static final int[] PETSCII_TABLE = {
			0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, // 00-0f
			0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,	// 10-1f
			0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f,	// 20-2f
			0x30, 0x31, 0x32, 0x33,	0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f, // 30-3f
			0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,	0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, // 40-4f
			0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x5b,	0x5c, 0x5d, 0x5e, 0xa4, // 50-5f
			0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f,	// 60-6f
			0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0x7f,	// 70-7f
			0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,	// 80-8f
			0x20, 0x20, 0x20, 0x20,	0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,	// 90-9f
			0x20, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, // a0-af
			0xb0, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf,	// b0-bf
			0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, // c0-cf
			0x70, 0x71, 0x72, 0x73,	0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0x7f,	// d0-df
			0x20, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf,	// e0-ef
			0xb0, 0xb1, 0xb2, 0xb3,	0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf,	// f0-ff
			0x7e
	};
	/** Valid characters when mapping from PC to CBM file names */
	private static final String VALID_PC_CBM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 []()/;:<>.-_+&%$@#!";

	private Utility() {
	}

	/**
	 * Write data to file on local file system.
	 *
	 * @param targetFile
	 *            the name of the file (without path)
	 * @param data
	 *            the data to write
	 * @throws CbmException
	 *             when error
	 */
	public static void writeFile(File targetFile, byte[] data) throws CbmException {
		if (targetFile == null || data == null) {
			throw new CbmException(ERR_REQUIRED_DATA_MISSING);
		}
		try (FileOutputStream output = new FileOutputStream(targetFile)) {
			output.write(data, 0, data.length);
		} catch (Exception e) {
			throw new CbmException(ERR_READ_ERROR + e.getMessage(), e);
		}
	}

	/**
	 * Read file into byte array
	 *
	 * @param file
	 *            file to read from
	 * @return byte array
	 * @throws CbmException
	 *             when error
	 */
	public static byte[] readFile(File file) throws CbmException {
		byte[] data;
		try (FileInputStream input = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[65536];
			int read;
			while ((read = input.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			data = out.toByteArray();
		} catch (Exception e) {
			throw new CbmException(ERR_READ_ERROR + e.getMessage(), e);
		}
		return data;
	}

	/**
	 * Read gzipped file.
	 *
	 * @param fileName
	 *            name of zip file
	 * @return byte array with uncompressed data
	 * @throws CbmException
	 *             when failure
	 */
	public static byte[] readGZippedFile(String fileName) throws CbmException {
		try (FileInputStream fis = new FileInputStream(fileName);
				GZIPInputStream gis = new GZIPInputStream(fis, INPUT_BUFFER_SIZE);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(OUTPUT_BUFFER_SIZE)) {
			while (gis.available() == 1) {
				bos.write(gis.read());
			}
			return bos.toByteArray();
		} catch (IOException e) {
			throw new CbmException(ERR_ZIP_READ_ERROR + e.getMessage(), e);
		}
	}

	/**
	 * Write data to a gzipped file
	 *
	 * @param fileName
	 *            name of zip file to create
	 * @param data
	 *            the data
	 * @throws CbmException
	 *             when error
	 */
	public static void writeGZippedFile(String fileName, byte[] data) throws CbmException {
		if (data == null) {
			return;
		}
		try (FileOutputStream output = new FileOutputStream(fileName);
				GZIPOutputStream zipStream = new GZIPOutputStream(output)) {
			zipStream.write(data);
		} catch (IOException e) {
			throw new CbmException(ERR_ZIP_WRITE_ERROR + e.getMessage(), e);
		}
	}

	/**
	 * @param file
	 *            file to check
	 * @return true if file seems to be gzipped.
	 * @throws CbmException
	 *             if error
	 */
	public static boolean isGZipped(String file) throws CbmException {
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			return GZIPInputStream.GZIP_MAGIC == (raf.read() & 0xff | ((raf.read() << 8) & 0xff00));
		} catch (IOException e) {
			throw new CbmException("Failed to open zip header. " + e.getMessage(), e);
		}
	}

	public static String trimTrailing(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.replaceAll("\\s+$", Utility.EMPTY);
	}
	/**
	 * @param data the data
	 * @param pos the index to get value from
	 * @return Get signed byte as int from data[pos]
	 */
	public static int getInt8(byte[] data, int pos) {
		if (data == null || pos >= data.length) {
			return 0;
		} else {
			int n = data[pos] & 0xff;
			return (n & 0x80) != 0 ? n | ~0xff : n;
		}
	}

	/**
	 * Get 16 bit value, with LSB first.
	 * @param data the data
	 * @param pos the index to get value from
	 * @return Get two unsigned bytes as int from data[pos]
	 */
	public static int getInt16(byte[] data, int pos) {
		if (data == null || pos + 1 >= data.length) {
			return 0;
		} else {
			return data[pos] & 0xff | (data[pos + 1] & 0xff) << 8;
		}
	}

	/**
	 * Get 32 bit value with LSB first.
	 * @param data the data
	 * @param pos the index to get value from
	 * @return the 32 bit value
	 */
	public static int getInt32(byte[] data, int pos) {
		return (data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24);
	}

	public static void setInt16(byte[] data, int pos, int value) {
		data[pos] = (byte) (value & 0xff);
		data[pos + 1] = (byte) ((value >>> 8) & 0xff);
	}

	public static void setInt32(byte[] data, int pos, int value) {
		data[pos] = (byte) (value & 0xff);
		data[pos + 1] = (byte) ((value >>> 8) & 0xff);
		data[pos + 2] = (byte) ((value >>> 16) & 0xff);
		data[pos + 3] = (byte) ((value >>> 24) & 0xff);
	}

	public static String getString(byte[] data, int pos, int length) {
		char[] chars = new char[length];
		for (int i=0; i< length; i++) {
			chars[i] = pos + i < data.length ? (char)( data[pos + i] & 0xff) : 0;
		}
		return new String(chars);
	}

	/**
	 * Set string into bytes, if string is shorter than length, then pad it with 0xA0 (blank)
	 * @param data the byes to insert string into
	 * @param pos the position to start writing at
	 * @param string the string
	 * @param length the length of the string
	 */
	public static void setPaddedString(byte[] data, int pos, String string, int length) {
		for (int i=0; i < length; i++) {
			if (i < string.length()) {
				data[pos + i] = (byte) string.charAt(i);
			} else {
				data[pos + i] = BLANK;
			}
		}
	}

	/**
	 * Copy bytes from short[] to byte[].
	 * @param fromData the short[] to copy from
	 * @param toData the byte[] to copy to
	 * @param fromPos start reading at this position in fromData
	 * @param toPos start writing to this position in toData
	 * @param length the number of bytes to copy
	 */
	public static void copyBytes(short[] fromData, byte[] toData, int fromPos, int toPos, int length) {
		for (int i = 0; i < length; i++) {
			toData[toPos + i] = (byte) (fromData[fromPos + i] & 0xff);
		}
	}

	/**
	 * Copy bytes from byte[] to byte[].
	 * @param fromData the byte[] to copy from
	 * @param toData the byte[] to copy to
	 * @param fromPos start reading at this position in fromData
	 * @param toPos start writing to this position in toData
	 * @param length the number of bytes to copy
	 */
	public static void copyBytes(byte[] fromData, byte[] toData, int fromPos, int toPos, int length) {
		for (int i = 0; i < length; i++) {
			toData[toPos + i] = fromData[fromPos + i];
		}
	}

	public static String getTrimmedString(byte[] data, int pos, int length) throws CbmException {
		byte[] tmp = new byte[length];
		for (int i = 0; i < length; i++) {
			byte b = data[pos+i];
			tmp[i] = b == 0xa0 ? 0x20 : b;
		}
		try {
			return new String(tmp, "ISO-8859-1").trim();
		} catch (UnsupportedEncodingException e) {	// NOSONAR
			throw new CbmException("Unsupported character encoding.", e);
		}
	}

	/**
	 * Convert a PC filename to a proper CBM filename.<BR>
	 * @param orgName orgName
	 * @param maxLength max length
	 * @return the CBM filename
	 */
	public static String cbmFileName(String orgName, int maxLength) {
		char[] fileName = new char[maxLength];
		int out = 0;
		for (int i=0; i<maxLength && i<orgName.length(); i++) {
			char c = Character.toUpperCase(orgName.charAt(i));
			if (VALID_PC_CBM_CHARS.indexOf(c) >= 0) {
				fileName[out++] = c;
			}
		}
		return new String(Arrays.copyOfRange(fileName, 0, out));
	}

	/**
	 * Create string using 7-bit characters.
	 * @param data the data
	 * @param pos position of first byte
	 * @param len number of bytes
	 * @return String
	 */
	public static String getCpmString(byte[] data, int pos, int len) {
		char[] string = new char[len];
		for (int i=0; i<len; i++) {
			string[i] = (char) (data[pos + i] & 0x7f);
		}
		return new String(string);
	}

}
