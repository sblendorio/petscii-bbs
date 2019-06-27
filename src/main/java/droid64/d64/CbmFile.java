package droid64.d64;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <pre style='font-family:sans-serif;'>
 * Created on 21.06.2004
 *
 *   droiD64 - A graphical filemanager for D64 files
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
 *   eMail: wolfvoz@users.sourceforge.net
 *   http://droid64.sourceforge.net
 * </pre>
 *
 * @author wolf
 */
public class CbmFile implements Comparable<CbmFile>, Serializable {

	private static final long serialVersionUID = 1L;
	private boolean fileScratched;
	private int fileType;
	private boolean fileLocked;
	private boolean fileClosed;
	private int track;
	private int sector;
	private String name; // string[16]
	private int relTrack;
	private int relSector;
	/** FileStructure, FileType, Year, Month, Day. Hour, Minute */
	private int[] geos = new int[7];
	private int sizeInBytes;
	private int sizeInBlocks;
	private int dirTrack; // next directory track
	private int dirSector; // next directory sector
	private int dirPosition; // position in directory
	private int offSet;
	private int lsu; // last sector usage
	private int loadAddr;

	public static final int GEOS_NORMAL = 0x00;
	public static final int GEOS_BASIC = 0x01;
	public static final int GEOS_ASM = 0x02;
	public static final int GEOS_DATA = 0x03;
	public static final int GEOS_SYS = 0x04;
	public static final int GEOS_DESK_ACC = 0x05;
	public static final int GEOS_APPL = 0x06;
	public static final int GEOS_APPL_DATA = 0x07;
	public static final int GEOS_FONT = 0x08;
	public static final int GEOS_PRT_DRV = 0x09;
	public static final int GEOS_INPUT_DRV = 0x0a;
	public static final int GEOS_DISK_DRV = 0x0b;
	public static final int GEOS_SYS_BOOT = 0x0c;
	public static final int GEOS_TEMP = 0x0d;
	public static final int GEOS_AUTOEXEC = 0x0e;
	public static final int GEOS_UNDFINED = 0xff;

	/** Type of C64 file (DEL, SEQ, PRG, USR, REL) */
	protected static final String[] FILE_TYPES = { "DEL", "SEQ", "PRG", "USR", "REL", "CBM" };

	public static final int TYPE_DEL = 0;
	public static final int TYPE_SEQ = 1;
	public static final int TYPE_PRG = 2;
	public static final int TYPE_USR = 3;
	public static final int TYPE_REL = 4;
	public static final int TYPE_CBM = 5;	// C1581 partition

	public CbmFile() {
		fileScratched = true;
		fileType = 0;
		fileLocked = false;
		fileClosed = false;
		track = 0;
		sector = 0;
		name = Utility.EMPTY;
		relTrack = 0;
		relSector = 0;
		geos[0] = 0;
		geos[1] = 0;
		geos[2] = 0;
		geos[3] = 0;
		geos[4] = 0;
		geos[5] = 0;
		sizeInBytes = 0;
		sizeInBlocks = 0;
		dirTrack = 0;
		dirSector = 0;
		dirPosition = 0;
		lsu = 0;
		loadAddr = 0;
	}

	public CbmFile(CbmFile that) {
		this.fileScratched = that.fileScratched;
		this.fileType = that.fileType;
		this.fileLocked = that.fileLocked;
		this.fileClosed = that.fileClosed;
		this.track = that.track;
		this.sector = that.sector;
		this.name = that.name;
		this.relTrack = that.relTrack;
		this.relSector = that.relSector;
		this.geos[0] = that.geos[0];
		this.geos[1] = that.geos[1];
		this.geos[2] = that.geos[2];
		this.geos[3] = that.geos[3];
		this.geos[4] = that.geos[4];
		this.geos[5] = that.geos[5];
		this.sizeInBytes = that.sizeInBytes;
		this.sizeInBlocks = that.sizeInBlocks;
		this.dirTrack = that.dirTrack;
		this.dirSector = that.dirSector;
		this.dirPosition = that.dirPosition;
		this.lsu = that.lsu;
		this.loadAddr = that.loadAddr;
	}

	public CbmFile(String name, int fileType, int dirPosition, int track, int sector, int size) {
		this.name = name;
		this.fileType = fileType;
		this.dirPosition = dirPosition;
		this.track = track;
		this.sector = sector;
		this.sizeInBytes = size;
		fileScratched = false;
		fileLocked = false;
		fileClosed = false;
	}

	/**
	 * Construct entry from position in disk image.
	 *
	 * @param data
	 *            byte[]
	 * @param position
	 *            int
	 */
	public CbmFile(byte[] data, int position) {
		dirTrack = data[position + 0x00] & 0xff;
		dirSector = data[position + 0x01] & 0xff;
		fileScratched = (data[position + 0x02] & 0xff) == 0 ? true : false;
		fileType = data[position + 0x02] & 0x07;
		fileLocked = (data[position + 0x02] & 0x40) == 0 ? false : true;
		fileClosed = (data[position + 0x02] & 0x80) == 0 ? false : true;
		track = data[position + 0x03] & 0xff;
		sector = data[position + 0x04] & 0xff;
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			int c = data[position + 0x05 + i] & 0xff;
			if (c != (Utility.BLANK & 0xff)) {
				buf.append((char) (Utility.PETSCII_TABLE[c]));
			}
		}
		name = buf.toString();
		relTrack = data[position + 0x15] & 0xff;
		relSector = data[position + 0x16] & 0xff;
		for (int i = 0; i < geos.length; i++) {
			geos[i] = data[position + 0x17 + i] & 0xff;
		}
		sizeInBlocks = (data[position + 0x1e] & 0xff) | ((data[position + 0x1f] & 0xff) * 256);
	}

	/**
	 * @param type type
	 * @return file type
	 */
	public static String getFileType(int type) {
		return  type < CbmFile.FILE_TYPES.length ? CbmFile.FILE_TYPES[type] : null;
	}

	/**
	 * Checks if fileName ends with .del, .seq, .prg, .usr or .rel and returns the corresponding file type.<br>
	 * If there is no matching file extension, TYPE_PRG is return
	 * @param fileName fileName
	 * @return file type
	 */
	public static int getFileTypeFromFileExtension(String fileName) {
		String name = fileName != null ? fileName.toLowerCase() : Utility.EMPTY;
		if (name.endsWith(".del")) {
			return TYPE_DEL;
		} else if (name.endsWith(".seq")) {
			return TYPE_SEQ;
		} else if (name.endsWith(".usr")) {
			return TYPE_USR;
		} else if (name.endsWith(".rel")) {
			return TYPE_REL;
		} else {
			return TYPE_PRG;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CbmFile [");
		toString(builder);
		builder.append("]");
		return builder.toString();
	}

	public String asDirString() {
		return String.format("%-5s%-18s %s%-3s%s", sizeInBlocks, "\"" + name + "\"", fileClosed ? Utility.SPACE : "*",
				fileType < CbmFile.FILE_TYPES.length ? CbmFile.FILE_TYPES[fileType] : "???",
						fileLocked ? "<" : Utility.SPACE);
	}

	protected void toString(StringBuilder builder) {
		builder.append(" dirSector=").append(dirSector);
		builder.append(" dirTrack=").append(dirTrack);
		builder.append(" fileClosed=").append(fileClosed);
		builder.append(" fileLocked=").append(fileLocked);
		builder.append(" fileScratched=").append(fileScratched);
		builder.append(" fileType=").append(fileType);
		builder.append(" geos=").append(Arrays.toString(geos));
		builder.append(" loadAddr=").append(loadAddr);
		builder.append(" lsu=").append(lsu);
		builder.append(" name=").append(name);
		builder.append(" offSet=").append(offSet);
		builder.append(" relSector=").append(relSector);
		builder.append(" relTrack=").append(relTrack);
		builder.append(" sector=").append(sector);
		builder.append(" sizeInBlocks=").append(sizeInBlocks);
		builder.append(" sizeInBytes=").append(sizeInBytes);
		builder.append(" track=").append(track);
	}

	/**
	 * @return true when file is closed
	 */
	public boolean isFileClosed() {
		return fileClosed;
	}

	/**
	 * @return true when file is locked
	 */
	public boolean isFileLocked() {
		return fileLocked;
	}

	/**
	 * @return true when file is scratched
	 */
	public boolean isFileScratched() {
		return fileScratched;
	}

	/**
	 * @return type of file
	 */
	public int getFileType() {
		return fileType;
	}

	/**
	 * @param whichone
	 *            goes attribute number
	 * @return true if geos
	 */
	public int getGeos(int whichone) {
		return geos[whichone];
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return relative sector
	 */
	public int getRelSector() {
		return relSector;
	}

	/**
	 * @return relative track
	 */
	public int getRelTrack() {
		return relTrack;
	}

	/**
	 * @return sector
	 */
	public int getSector() {
		return sector;
	}

	/**
	 * @return size in bytes
	 */
	public int getSizeInBytes() {
		return sizeInBytes;
	}

	/**
	 * @return track
	 */
	public int getTrack() {
		return track;
	}

	/**
	 * @param b
	 *            file closed
	 */
	public void setFileClosed(boolean b) {
		fileClosed = b;
	}

	/**
	 * @param b
	 *            file locked
	 */
	public void setFileLocked(boolean b) {
		fileLocked = b;
	}

	/**
	 * @param b
	 *            file scratched
	 */
	public void setFileScratched(boolean b) {
		fileScratched = b;
	}

	/**
	 * @param b
	 *            file type
	 */
	public void setFileType(int b) {
		fileType = b;
	}

	/**
	 * @param where
	 *            geos field number
	 * @param bs
	 *            data
	 */
	public void setGeos(int where, int bs) {
		geos[where] = bs;
	}

	/**
	 * @param string
	 *            name
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param b
	 *            relative sector
	 */
	public void setRelSector(int b) {
		relSector = b;
	}

	/**
	 * @param b
	 *            relative track
	 */
	public void setRelTrack(int b) {
		relTrack = b;
	}

	/**
	 * @param b
	 *            sector
	 */
	public void setSector(int b) {
		sector = b;
	}

	/**
	 * @param i
	 *            size in bytes
	 */
	public void setSizeInBytes(int i) {
		sizeInBytes = i;
	}

	/**
	 * @param b
	 *            track
	 */
	public void setTrack(int b) {
		track = b;
	}

	/**
	 * @return directory sector
	 */
	public int getDirSector() {
		return dirSector;
	}

	/**
	 * @return directory track
	 */
	public int getDirTrack() {
		return dirTrack;
	}

	/**
	 * @return size in blocks
	 */
	public int getSizeInBlocks() {
		return sizeInBlocks;
	}

	/**
	 * @param i
	 *            directory sector
	 */
	public void setDirSector(int i) {
		dirSector = i;
	}

	/**
	 * @param i
	 *            directory track
	 */
	public void setDirTrack(int i) {
		dirTrack = i;
	}

	/**
	 * @param i
	 *            size in blocks
	 */
	public void setSizeInBlocks(int i) {
		sizeInBlocks = i;
	}

	/**
	 * @return directory position
	 */
	public int getDirPosition() {
		return dirPosition;
	}

	/**
	 * @param i
	 *            direcotry position
	 */
	public void setDirPosition(int i) {
		dirPosition = i;
	}

	public int getOffSet() {
		return offSet;
	}

	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}

	public int getLsu() {
		return lsu;
	}

	/**
	 * @param lsu
	 *            last sector usage
	 */
	public void setLsu(int lsu) {
		this.lsu = lsu;
	}

	public int getLoadAddr() {
		return loadAddr;
	}

	public void setLoadAddr(int loadAddr) {
		this.loadAddr = loadAddr;
	}

	@Override
	public int compareTo(CbmFile that) {
		if (this.name == null && that.name == null) {
			return 0;
		} else if (this.name == null || that.name == null) {
			return this.name == null ? -1 : 1;
		} else {
			return name.compareTo(that.name);
		}
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		CbmFile other = (CbmFile) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Write CbmFile as bytes in data at offset.
	 * @param data the disk data
	 * @param offset where to start writing
	 */
	public void toBytes(byte[] data, int offset) {
		if (data == null || offset + DiskImage.DIR_ENTRY_SIZE > data.length) {
			return;
		}
		// file attributes
		data[offset + 2] = 0;
		if (!fileScratched) {
			data[offset + 2] = (byte) fileType;
			if (fileLocked) {
				data[offset + 2] |= 64;
			}
			if (fileClosed) {
				data[offset + 2] |= 128;
			}
		}
		// file track / sector (where to start reading)
		data[offset + 3] = (byte) track;
		data[offset + 4] = (byte) sector;
		// FileName
		for (int i = 0; i < DiskImage.DISK_NAME_LENGTH; i++) {
			data[offset + 5 + i] = i < name.length() ? (byte) name.charAt(i) : Utility.BLANK;
		}
		// relative Track/Sector
		data[offset + 21] = (byte) relTrack;
		data[offset + 22] = (byte) relSector;
		// GEOS
		for (int i = 0; i <= 5 && i < geos.length; i++) {
			data[offset + 23 + i] = (byte) geos[i];
		}
		// Size
		data[offset + 30] = (byte) sizeInBlocks;
		data[offset + 31] = (byte) (sizeInBlocks / DiskImage.BLOCK_SIZE);
	}
}
