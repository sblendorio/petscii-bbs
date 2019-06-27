package droid64.d64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import droid64.db.Settings;

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
	public static final String MIMETYPE_HTML = "text/html";
	public static final String MIMETYPE_TEXT = "text/plain";

	private static final String ERR_REQUIRED_DATA_MISSING = "Required data is missing. ";
	private static final String ERR_READ_ERROR = "Failed to read from file. ";
	private static final String ERR_WRITE_ERROR = "Failed to write to file. ";
	private static final String ERR_ZIP_READ_ERROR = "Failed to read zip file. ";
	private static final String ERR_ZIP_WRITE_ERROR = "Failed to write zip file. ";

	/** Size of buffer reading compressed data */
	private static final int INPUT_BUFFER_SIZE = 65536;
	/** Size of buffer writing uncompressed data to byte[] */
	private static final int OUTPUT_BUFFER_SIZE = 1048576;
	private static String hostName = null;

	/** Used for quick translation from byte to string */
	private static final String[] HEX = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0a", "0b", "0c",
			"0d", "0e", "0f", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b", "1c", "1d", "1e",
			"1f", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f", "30",
			"31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c", "3d", "3e", "3f", "40", "41", "42",
			"43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50", "51", "52", "53", "54",
			"55", "56", "57", "58", "59", "5a", "5b", "5c", "5d", "5e", "5f", "60", "61", "62", "63", "64", "65", "66",
			"67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73", "74", "75", "76", "77", "78",
			"79", "7a", "7b", "7c", "7d", "7e", "7f", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8a",
			"8b", "8c", "8d", "8e", "8f", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9a", "9b", "9c",
			"9d", "9e", "9f", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa", "ab", "ac", "ad", "ae",
			"af", "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf", "c0",
			"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf", "d0", "d1", "d2",
			"d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df", "e0", "e1", "e2", "e3", "e4",
			"e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef", "f0", "f1", "f2", "f3", "f4", "f5", "f6",
			"f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff" };
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
	/** Valid characters when mapping from CBM to PC file names */
	private static final String VALID_CBM_PC_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789 !#$%&()-@_{}.";

	private Utility() {
	}

	/**
	 * Copy data from sourceFile to targetFile.
	 *
	 * @param sourceFile
	 *            read from
	 * @param targetFile
	 *            write to
	 * @throws CbmException
	 *             when error
	 */
	public static void writeFile(File sourceFile, File targetFile) throws CbmException {
		if (targetFile == null || sourceFile == null) {
			throw new CbmException(ERR_REQUIRED_DATA_MISSING);
		}
		try (FileInputStream input = new FileInputStream(sourceFile);
				FileOutputStream output = new FileOutputStream(targetFile)) {
			byte[] buffer = new byte[256];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			throw new CbmException(ERR_WRITE_ERROR + e.getMessage(), e);
		}
	}

	/**
	 * Copy data from sourceFile to targetFile.
	 *
	 * @param sourceFile
	 *            the source
	 * @param targetFile
	 *            the target
	 * @return true if write was successful
	 */
	public static boolean writeFileSafe(File sourceFile, File targetFile) {
		if (targetFile == null || sourceFile == null) {
			return false;
		}
		try (FileInputStream input = new FileInputStream(sourceFile);
				FileOutputStream output = new FileOutputStream(targetFile)) {
			byte[] buffer = new byte[256];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			return true;
		} catch (Exception e) { // NOSONAR
			return false;
		}
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
	 * Write bytes to file.
	 *
	 * @param targetFile
	 *            the file
	 * @param data
	 *            the data to write
	 * @return true if write was successful
	 */
	public static boolean writeFileSafe(File targetFile, byte[] data) {
		if (targetFile == null || data == null) {
			return false;
		}
		try (FileOutputStream output = new FileOutputStream(targetFile)) {
			output.write(data, 0, data.length);
			return true;
		} catch (Exception e) { // NOSONAR
			return false;
		}
	}

	/**
	 * Write data to file on local file system.
	 *
	 * @param targetFile
	 *            the name of the file (without path)
	 * @param string
	 *            the string to write
	 * @throws CbmException
	 *             when error
	 */
	public static void writeFile(File targetFile, String string) throws CbmException {
		if (targetFile == null || string == null) {
			throw new CbmException(ERR_REQUIRED_DATA_MISSING);
		}
		try (PrintWriter output = new PrintWriter(targetFile)) {
			output.println(string);
		} catch (Exception e) {
			throw new CbmException(ERR_WRITE_ERROR + e.getMessage(), e);
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
		byte[] data = null;
		try (FileInputStream input = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[65536];
			int read = 0;
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
	 * Get entries from a zip file.
	 * @param file the zip file.
	 * @param firstFileNum the fileNumber to assign to the first entry.
	 * @return the list of entries in the zip file.
	 * @throws CbmException in case of zip errors
	 */
	public static List<DirEntry> getZipFileEntries(File file, int firstFileNum) throws CbmException {
		List<DirEntry> list = new ArrayList<> ();
		try (ZipFile zipFile = new ZipFile(file)) {
			int fileNum = firstFileNum;
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()){
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					list.add(new DirEntry(file, entry, fileNum++));
				}
			}
		} catch (IOException e) {
			throw new CbmException(ERR_ZIP_READ_ERROR + e.getMessage(), e);
		}
		return list;
	}

	/**
	 * Create a new zip file with one entry.
	 * @param file the zip file to be created
	 * @param entryName the name of the entry in the zip file
	 * @param data the bytes to be written for entryName into the zip file
	 * @throws CbmException in case of zip errors
	 */
	public static void createNewZipFile(File file, String entryName, byte[] data) throws CbmException {
		try (FileOutputStream fos = new FileOutputStream(file);
				ZipOutputStream zos = new ZipOutputStream(fos);
				InputStream in = new ByteArrayInputStream(data)) {
			ZipEntry zipEntry = new ZipEntry(entryName);
			zos.putNextEntry(zipEntry);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
		} catch (IOException e) {
			throw new CbmException(ERR_ZIP_WRITE_ERROR + e.getMessage(), e);
		}
	}

	/**
	 * Extract data from an entry in a Zip file.
	 *
	 * @param zipFileName
	 *            name of zip file
	 * @param entryName
	 *            zip file entry to extract
	 * @return file data in a byte[].
	 * @throws IOException
	 *             when error
	 */
	public static byte[] getDataFromZipFileEntry(String zipFileName, String entryName) throws IOException {
		byte[] data = null;
		try (ZipFile zipFile = new ZipFile(zipFileName)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entryName.equals(entry.getName())) {
					InputStream zin = zipFile.getInputStream(entry);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] bytesIn = new byte[1024];
					int read;
					while ((read = zin.read(bytesIn)) != -1) {
						bos.write(bytesIn, 0, read);
					}
					bos.close();
					data = bos.toByteArray();
					bos.close();
					zin.close();
					break;
				}
			}
		}
		return data;
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

	/**
	 * Calculate MD5 checksum on data.
	 *
	 * @param data
	 *            the bytes
	 * @return string with MD5 checksum
	 * @throws CbmException when failure
	 */
	public static String calcMd5Checksum(byte[] data) throws CbmException {
		try {
			byte[] digest = MessageDigest.getInstance("MD5").digest(data);
			StringBuilder buf = new StringBuilder();
			for (byte b : digest) {
				buf.append(HEX[(b) & 0xff]);
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new CbmException("Failed to get MD5", e);
		}
	}

	/**
	 * Convert an 32 bit int to a hexadecimal string with leading zeroes (unlike
	 * Integer.tohexString())
	 *
	 * @param i
	 *            the number
	 * @return String
	 * @see Integer#toHexString(int)
	 */
	public static String getIntHexString(int i) {
		return HEX[i >>> 24 & 0xff] + HEX[i >>> 16 & 0xff] + HEX[i >>> 8 & 0xff] + HEX[i & 0xff];
	}

	/**
	 * Convert the lowest byte in integer to a two character hex string.
	 *
	 * @param num
	 *            the integer
	 * @return string
	 */
	public static String getByteString(int num) {
		return HEX[num & 0xff];
	}

	public static String getByteStringUpperCase(int num) {
		return HEX[num & 0xff].toUpperCase();
	}

	/**
	 * Trim string from whitespace, check if it is null.
	 * @param string the string
	 * @return trimmed string or null if string was null or empty
	 */
	public static String safeTrim(String string) {
		if (string != null) {
			String s = string.trim();
			return s.isEmpty() ? null : s;
		}
		return null;
	}

	/**
	 * Round integer to have a value between 0 and 255
	 * @param value to be trimmed
	 * @return the trimmed value
	 */
	public static int trimIntByte(int value) {
		return Math.max(0, Math.min(255, value));
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

	/**
	 * Parse integer from string, and return default value if string can't be parsed.
	 * @param string to be parsed
	 * @param defaultValue the default value
	 * @return the parsed value or defaultValue
	 */
	public static int parseInteger(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
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
	 * Set string into bytes,
	 * @param data the byes to insert string into
	 * @param pos the position to start writing at
	 * @param string the string
	 */
	public static void setString(byte[] data, int pos, String string) {
		if (string == null) {
			return;
		}
		for (int i=0; i < string.length(); i++) {
			data[pos + i] = (byte) string.charAt(i);
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

	public static String getHostName() {
		if (hostName != null) {
			return hostName;
		}
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME")) {
			hostName = env.get("COMPUTERNAME");
		}
		if (hostName == null && env.containsKey("HOSTNAME")) {
			hostName = env.get("HOSTNAME");
		}
		if (hostName == null) {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {	//NOSONAR
				return null;
			}
		}
		return hostName;
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
	 * Get PC filename from a CbmFile.
	 * @param cbmFile the CBM file
	 * @return the PC filename
	 */
	public static String pcFilename(CbmFile cbmFile) {
		if (cbmFile instanceof CpmFile) {
			return ((CpmFile)cbmFile).getCpmNameAndExt();
		} else {
			String fileName = cbmFile.getName().toLowerCase();
			for (int i = 0; i < fileName.length(); i++) {
				if (VALID_CBM_PC_CHARS.indexOf(fileName.charAt(i)) == -1) {
					fileName = fileName.substring(0, i) + "_" + fileName.substring(i + 1, fileName.length());
				}
			}
			return fileName + "." + CbmFile.FILE_TYPES[cbmFile.getFileType()].toLowerCase();
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

	/**
	 * Load a text file within the jar file into a string.
	 * @param resourceFile name of the resource file to load
	 * @return String
	 */
	public static String getResource(String resourceFile) {
		try (InputStream in = Settings.class.getResourceAsStream(resourceFile); Scanner scanner = new Scanner(in, "utf-8")) {
			return scanner.useDelimiter("\\Z").next();
		} catch (Exception e) {	//NOSONAR
			return "Failed to read " + resourceFile + " resource: \n"+e.getMessage();
		}
	}

	public static String hexDump(byte[] data) {
		if (data == null) {
			return "null";
		}
		StringBuilder buf = new StringBuilder();
		char[] asc = new char[16];
		for (int i = 0; i<data.length; i++) {
			if (i % 16 == 0) {
				if (i > 0) {
					buf.append("  ").append(asc).append("\n");
				}
				buf.append(getIntHexString(i)).append(": ");
			}
			int c = data[i]&0xff;
			buf.append(' ').append(HEX[c]);
			if (c >= 0x20 && c < 0x7f) {
				asc[i % 16] = (char) c;
			} else {
				asc[i % 16] = '.';
			}
		}
		if ((data.length - 1) % 16 > 0) {
			buf.append("  ").append(asc);
		}
		return buf.toString();
	}


	/**
	 * Sort array of files
	 * @param files the array of files
	 * @return returns the modified array of files
	 */
	public static File[] sortFiles(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File a, File b) {
				if (a.isDirectory() && !b.isDirectory()) {
					return -1;
				} else if (!a.isDirectory() && b.isDirectory()) {
					return 1;
				}
				return a.getName().compareTo(b.getName());
			}
		});
		return files;
	}

}
