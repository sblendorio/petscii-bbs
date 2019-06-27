package droid64.d64;

import java.util.Arrays;
import java.util.List;

/**<pre style='font-family:sans-serif;'>
 * Created on 2015-Oct-15
 *
 *   droiD64 - A graphical file manager for D64 files
 *   Copyright (C) 2015 Henrik Wetterström
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
 * @author Henrik
 * </pre>
 */
public class T64 extends DiskImage {

	private static final long serialVersionUID = 1L;
	/** Name of the image type */
	public static final String IMAGE_TYPE_NAME = "T64";
	private static final String T64_SIGNATURE = "C64S tape file\r\n";
	private static final byte SPACE = 0x20;

	public T64() {
		initCbmFile(0);
		bam = new CbmBam(1, 1);
	}

	public T64(byte[] imageData) {
		cbmDisk = imageData;
		int entryCapacity = Utility.getInt16(cbmDisk, 0x22);
		filesUsedCount = Utility.getInt16(cbmDisk, 0x24);
		initCbmFile(entryCapacity);
		bam = new CbmBam(1, 1);
	}

	@Override
	public byte[] getBlock(int track, int sector) throws CbmException {
		return new byte[0];
	}

	@Override
	protected DiskImage readImage(String filename) throws CbmException {
		bam = new CbmBam(1, 1);
		readImage(filename, 0, IMAGE_TYPE_NAME);
		int entryCapacity = Utility.getInt16(cbmDisk, 0x22);
		filesUsedCount = Utility.getInt16(cbmDisk, 0x24);
		initCbmFile(entryCapacity);
		return this;
	}

	@Override
	public void readBAM() {
		bam.setDiskName(Utility.EMPTY);
		bam.setDiskId(Utility.EMPTY);
		for (int i = 0; i < 32; i++) {
			int c = cbmDisk[0x00 + i] & 0xff;
			if (c!=0) {
				bam.setDiskName(bam.getDiskName() + Character.toUpperCase((char)(Utility.PETSCII_TABLE[c])) );
			}
		}
		checkImageFormat();
	}

	@Override
	public void readDirectory() {
		int dirPosition = 0;
		filesUsedCount = 0;
		int maxFiles = Utility.getInt16(cbmDisk, 0x22);
		for (int i=0; i<maxFiles; i++) {
			int pos = 0x40 + i * DIR_ENTRY_SIZE;
			if (pos < cbmDisk.length && (cbmDisk[pos] & 0xff) != 0) {
				// Entry is used
				int loadStartAddr = Utility.getInt16(cbmDisk, pos + 0x02);
				int loadEndAddr = Utility.getInt16(cbmDisk, pos + 0x04);
				int size = loadEndAddr - loadStartAddr;
				int offset = Utility.getInt32(cbmDisk, pos + 0x08);
				String name = Utility.trimTrailing(Utility.getString(cbmDisk, pos+0x10, 16));
				CbmFile cf = new CbmFile(name, cbmDisk[pos + 0x01] & 0x07, dirPosition, dirPosition++, offset, size);
				cf.setTrack(Utility.getInt16(cbmDisk, pos + 0x08));
				cf.setSector(Utility.getInt16(cbmDisk,pos + 0x0a));
				cf.setOffSet(offset);
				cf.setSizeInBytes(size);
				cf.setSizeInBlocks(size / (DiskImage.BLOCK_SIZE - 2));
				cf.setFileClosed(true);
				cf.setFileLocked(false);
				cf.setLoadAddr(loadStartAddr);
				cbmFile[i] = cf;
				filesUsedCount++;
			} else {
				cbmFile[i].setFileType(0);
			}
		}
	}

	@Override
	public boolean addDirectoryEntry(CbmFile cbmFile, int destTrack, int destSector, boolean isCopyFile, int lengthInBytes) {
		return saveFile(cbmFile, isCopyFile, new byte[lengthInBytes]);
	}

	@Override
	public byte[] getFileData(int number) throws CbmException {
		if (number < cbmFile.length && number >= 0) {
			CbmFile cf = cbmFile[number];
			if (cf.getFileType() == 0) {
				feedbackMessage.append("getFileData ["+number+"]: No data!\n");
				return null;
			}
			int dataStart = cf.getOffSet();
			int dataEnd = cf.getOffSet() + cf.getSizeInBytes();
			if (cbmDisk.length <  dataEnd || dataStart < 0) {
				throw new CbmException("T64 file ["+number+"] ends at 0x"+Integer.toHexString(dataEnd)+" outside image of size "+Integer.toHexString(cbmDisk.length)+".");
			}
			byte[] data = new byte[cf.getSizeInBytes() + 2];
			if (cf.getSizeInBytes() <= 0) {
				return data;
			}
			// first two bytes stored as loadAddr
			data[0] = (byte) (cf.getLoadAddr() & 0xff);
			if (cf.getSizeInBytes() < 2) {
				return data;
			}
			data[1] = (byte) ((cf.getLoadAddr() >>> 8) & 0xff);
			Utility.copyBytes(cbmDisk, data, dataStart, 2, cf.getSizeInBytes() - 2);
			return data;
		} else {
			throw new CbmException("T64 file number "+number+" does not exist.");
		}
	}

	@Override
	protected TrackSector saveFileData(byte[] saveData) {
		return new TrackSector(0,0);
	}

	@Override
	public boolean saveFile(CbmFile cbmFile, boolean isCopyFile, byte[] saveData) {
		int dataPos = expand(saveData.length);
		int entryNum = findUnusedEntry();
		if (entryNum < 0) {
			feedbackMessage.append("saveFile: No free directory entry!\n");
			return false;
		}
		writeEntry(cbmFile, entryNum, dataPos, saveData);
		Utility.setInt16(cbmDisk, 0x24, ++filesUsedCount);
		for (int i=2; i < saveData.length; i++) {
			cbmDisk[dataPos + i -2] = saveData[i];
		}
		feedbackMessage.append("saveFile: Saved file at entry "+entryNum +" with data at 0x"+Integer.toHexString(dataPos)+".\n");
		return true;
	}

	@Override
	public boolean saveNewImage(String filename, String newDiskName, String newDiskID) {
		cbmDisk = null;
		expand(0);
		Utility.setPaddedString(cbmDisk, 0x00, T64_SIGNATURE, T64_SIGNATURE.length());
		setDiskName(Utility.cbmFileName(newDiskName, DISK_NAME_LENGTH), Utility.cbmFileName(newDiskID, DISK_NAME_LENGTH));
		return writeImage(filename);
	}

	@Override
	public String[][] getBamTable() {
		return new String[0][0];
	}

	@Override
	public int getSectorOffset(int track, int sector) {
		return 0;
	}

	@Override
	public void deleteFile(CbmFile cf) throws CbmException {
		int pos = 0x40 + cf.getDirPosition() * DIR_ENTRY_SIZE;
		int num = cf.getDirPosition();
		if (cbmDisk[pos] != 0) {
			feedbackMessage.append("deleteFile ["+num+"]: " +cf.getName() + "\n");
			filesUsedCount = filesUsedCount > 0 ? filesUsedCount - 1 : 0;
			cbmDisk[pos] = 0;
			cbmFile[num].setFileType(0);
		} else {
			feedbackMessage.append("deleteFile ["+num+"]: already deleted. "+cf.getName()+ " \n");
		}
	}

	@Override
	public void readPartition(int track, int sector, int numBlocks) throws CbmException {
		throw new CbmException("T64 images does not support partitions.");
	}

	@Override
	public Integer validate(List<Integer> repairList) {
		feedbackMessage.append("validate: not supported for T64 images.\n");
		return 0;
	}

	@Override
	public boolean isSectorFree(int track, int sector) {
		// No sectors on tape
		return false;
	}

	@Override
	public void markSectorFree(int track, int sector) {
		// No sectors on tape
	}

	@Override
	public void markSectorUsed(int track, int sector) {
		// No sectors on tape
	}

	@Override
	protected void setDiskName(String newDiskName, String newDiskID) {
		feedbackMessage.append("setDiskName('").append(newDiskName).append("')\n");
		for (int i=0; i < 24; i++) {
			if (i < newDiskName.length()) {
				setCbmDiskValue(0x28 + i, newDiskName.charAt(i));
			} else {
				setCbmDiskValue(0x28 + i, SPACE);
			}
		}
	}

	@Override
	protected void writeDirectoryEntry(CbmFile cbmFile, int dirEntryNumber) {
		int pos = 0x40 + dirEntryNumber * DIR_ENTRY_SIZE;
		feedbackMessage.append("T64.writeDirectoryEntry: 0x"+Integer.toHexString(pos));
		String name = cbmFile.getName();
		for (int i=0; i<16; i++) {
			if (i < name.length()) {
				char c = name.charAt(i);
				cbmDisk[pos + 0x10 + i] = c == SPACE ? Utility.BLANK : (byte) c;
			} else {
				cbmDisk[pos + 0x10 + i] = SPACE;
			}
		}
	}

	private int findUnusedEntry() {
		for (int i=0; i<getFileCapacity(); i++) {
			int pos = 0x40 + i * DIR_ENTRY_SIZE;
			if ((cbmDisk[pos] & 0xff) == 0) {
				return i;
			}
		}
		return -1;
	}

	private int expand(int dataSize) {
		int dataPos;
		int newCapacity;
		if (cbmDisk != null) {
			byte[] oldDisk = cbmDisk;
			int oldCapacity = Utility.getInt16(cbmDisk, 0x22);
			cbmDisk = new byte[cbmDisk.length + DIR_ENTRY_SIZE + dataSize];
			Arrays.fill(cbmDisk, (byte) 0);
			// copy header
			Utility.copyBytes(oldDisk, cbmDisk, 0, 0, 0x40);
			// copy entries
			for (int i=0; i < oldCapacity; i++) {
				int pos = 0x40 + i * DIR_ENTRY_SIZE;
				if (pos < oldDisk.length && (oldDisk[pos] & 0xff) != 0) {
					Utility.copyBytes(oldDisk, cbmDisk, pos, pos, DIR_ENTRY_SIZE);
					Utility.setInt32(cbmDisk, pos + 0x08, Utility.getInt32(cbmDisk, pos + 0x08) + DIR_ENTRY_SIZE);
				}
			}
			// copy data
			for (int i=0x40 + getFileCapacity() * DIR_ENTRY_SIZE; i< oldDisk.length; i++) {
				cbmDisk[i + DIR_ENTRY_SIZE] = oldDisk[i];
			}
			//
			Utility.setInt16(cbmDisk, 0x22, oldCapacity + 1);
			newCapacity = oldCapacity + 1;
			dataPos = oldDisk.length + DIR_ENTRY_SIZE;
		} else {
			cbmDisk = new byte[0x40 + DIR_ENTRY_SIZE + dataSize];
			Arrays.fill(cbmDisk, (byte) 0);
			Utility.setInt16(cbmDisk, 0x20, 0x0101);	// version
			Utility.setInt16(cbmDisk, 0x22, 1);	// maxEntries
			Utility.setInt16(cbmDisk, 0x24, 0);	// usedEntries
			for (int i=0x28; i<0x40; i++) {
				cbmDisk[i] = SPACE;
			}
			newCapacity = 1;
			filesUsedCount = 0;
			dataPos = 0x40 + DIR_ENTRY_SIZE;
		}
		initCbmFile(newCapacity);
		readDirectory();
		return dataPos;
	}

	private void writeEntry(CbmFile cf, int entryNum, int dataPos, byte[] data) {
		if (entryNum >= 0) {
			int loadAddr;
			int endAddr;
			if (data != null && data.length >= 2) {
				loadAddr = (data[0] & 0xff) | ((data[1] << 8) & 0xff00);
				endAddr = (loadAddr + data.length) & 0xffff;
				if (loadAddr > endAddr) {
					loadAddr = 0x0000;
					endAddr = data.length & 0xffff;
				}
			} else {
				loadAddr = 0x0000;
				endAddr = data != null ? data.length : 0;
			}
			int pos = 0x40 + entryNum * DIR_ENTRY_SIZE;
			cbmDisk[pos + 0x00] = 1;	// normal file
			cbmDisk[pos + 0x01] = (byte) ((cf.getFileType() | 0x80 ) & 0xff);
			Utility.setInt16(cbmDisk, pos + 0x02, loadAddr);	// loadAddr
			Utility.setInt16(cbmDisk, pos + 0x04, endAddr);		// loadAddr + dataLength = endAddr
			Utility.setInt32(cbmDisk, pos + 0x08, dataPos);		// offset into container for data
			for (int i=0; i< 16; i++) {
				int c = i < cf.getName().length() ? cf.getName().charAt(i) : SPACE;
				cbmDisk[pos + 0x10 + i] = (byte) (c & 0xff);
			}
		}
	}

}
