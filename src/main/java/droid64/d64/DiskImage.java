package droid64.d64;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import droid64.db.Disk;
import droid64.db.DiskFile;
import droid64.db.Settings;

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
public abstract class DiskImage implements Serializable {

	private static final long serialVersionUID = 1L;
	/** Unknown or undefined image type */
	public static final int UNKNOWN_IMAGE_TYPE      = 0;
	/** Normal D64 (C1541 5.25") image */
	public static final int D64_IMAGE_TYPE          = 1;
	/** Normal D71 (C1571 5.25") image */
	public static final int D71_IMAGE_TYPE          = 2;
	/** Normal D81 (C1581 3.5") image */
	public static final int D81_IMAGE_TYPE          = 3;
	/** Normal T64 image (tape) */
	public static final int T64_IMAGE_TYPE          = 4;
	/** CP/M for C64 on a D64 image */
	public static final int D64_CPM_C64_IMAGE_TYPE  = 5;
	/** CP/M for C128 on a D64 image */
	public static final int D64_CPM_C128_IMAGE_TYPE = 6;
	/** CP/M on a D71 image */
	public static final int D71_CPM_IMAGE_TYPE      = 7;
	/** CP/M on a D81 image */
	public static final int D81_CPM_IMAGE_TYPE      = 8;
	/** Normal D82 (C8250 5.25") image */
	public static final int D82_IMAGE_TYPE          = 9;
	/** Normal D80 (C8050 5.25") image */
	public static final int D80_IMAGE_TYPE          = 10;
	/** Normal D67 (C2040 5.25") image */
	public static final int D67_IMAGE_TYPE          = 11;
	/** Lynx */
	public static final int LNX_IMAGE_TYPE          = 12;

	/** String array to convert imageType to String name */
	protected static final String[] IMAGE_TYPE_NAMES = {
			"Unknown", "D64", "D71", "D81", "T64",
			"CP/M D64 (C64)" , "CP/M D64 (C128)", "CP/M D71", "CP/M D81",
			"D82", "D80", "D67", "LNX" };

	/** Size of a disk block */
	protected static final int BLOCK_SIZE = 256;
	/** CP/M used byte marker. Single density disks are filled with this value from factory. CP/M use this to detect empty disks are blank. */
	public static final byte UNUSED = (byte) 0xe5;
	/** Max size of a PRG file */
	protected static final int MAX_PRG = 65536;
	/** Eight masks used to mask a bit out of a byte. Starting with LSB. */
	protected static final int[] BYTE_BIT_MASKS = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 };
	/** Eight masks used to mask a bit out of a byte. Starting with MSB. */
	protected static final int[] REVERSE_BYTE_BIT_MASKS = { 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01 };
	/** Eight masks used to mask all but one bits out of a byte. Starting with LSB. */
	protected static final int[] INVERTED_BYTE_BIT_MASKS = { 254, 253, 251, 247, 239, 223, 191, 127 };
	protected static final String CPM_DISKNAME_1 = "CP/M PLUS";
	protected static final String CPM_DISKNAME_2 = "CP/M DISK";
	protected static final String CPM_DISKID_GCR = "65 2A";
	protected static final String CPM_DISKID_1581 = "80 3D";
	/** The GEOS label found in BAM sector on GEOS formatted images */
	protected static final String DOS_LABEL_GEOS = "GEOS format";
	/** C1541 sector interleave. The gap between two blocks when saving a file */
	protected static final int C1541_INTERLEAVE = 10;
	/** C1571 sector interleave. The gap between two blocks when saving a file */
	protected static final int C1571_INTERLEAVE = 6;
	/** C1581 sector interleave. The gap between two blocks when saving a file */
	protected static final int C1581_INTERLEAVE = 1;
	/** Size of each directory entry on DIR_TRACK. */
	protected static final int DIR_ENTRY_SIZE = 32;
	/** Number of directory entries per directory sector */
	protected static final int DIR_ENTRIES_PER_SECTOR = 8;
	/** Maximum length of disk name */
	public static final int DISK_NAME_LENGTH = 16;
	/** Maximum length of disk ID */
	protected static final int DISK_ID_LENGTH = 5;
	/** Size of a CP/M records (128 bytes) */
	protected static final int CPM_RECORD_SIZE = 128;
	/** Type of image (D64, D71, D81, CP/M ... ) */
	protected int imageFormat = UNKNOWN_IMAGE_TYPE;
	protected static final String NOT_IMPLEMENTED_FOR_CPM = "Not yet implemented for CP/M format.\n";
	/** When True, this is a GEOS-formatted disk, therefore files must be saved the GEOS way. */
	protected boolean geosFormat = false;
	/** True if image is compressed */
	protected boolean compressed;
	/** Error messages are appended here, and get presented in GUI */
	protected StringBuilder feedbackMessage = new StringBuilder();
	/** Data of the whole image. */
	protected byte[] cbmDisk = null;
	/** Number of files in image */
	protected int filesUsedCount;
	/**
	 * A cbmFile holds all additional attributes (like fileName, fileType etc) for a file on the image.<br>
	 * These attributes are used in the directory and are initialized in initCbmFiles() and filled with data in readDirectory().<br>
	 * Their index is the directory-position they have in the image file (see readDirectory()).
	 */
	protected CbmFile[] cbmFile = null;
	/** All attributes which are stored in the BAM of a image file - gets filled with data in readBAM() */
	protected CbmBam bam;
	/** The number of validation errors, or null is no validation has been done. */
	protected Integer errors = null;
	protected Integer warnings = null;
	private List<ValidationError> validationErrorList = new ArrayList<>();

	/**
	 * Get number of sectors on specified track
	 * @param trackNumber track number
	 * @return number of sectors on specified track.
	 */
	public int getMaxSectors(int trackNumber) {
		return 0;
	}
	/**
	 * Get numbers of tracks on image.
	 * @return number of tracks.
	 */
	public int getTrackCount() {
		return 0;
	}
	/**
	 * Get maximum number of sectors on any track.
	 * @return maximum number of sectors
	 */
	public int getMaxSectorCount() {
		return 0;
	}
	/**
	 * Get number of free blocks.
	 * @return blocks free
	 */
	public int getBlocksFree() {
		return 0;
	}

	/**
	 * Reads image file.
	 * @param filename	the filename
	 * @return DiskImage
	 * @throws CbmException when error
	 */
	protected abstract DiskImage readImage(String filename) throws CbmException;
	/**
	 * Reads the BAM of the D64 image and fills bam[] with entries.
	 */
	public abstract void readBAM();
	/**
	 * Reads the directory of the image, fills cbmFile[] with entries.
	 */
	public abstract void readDirectory();

	/**
	 * Reads the directory of the partition
	 * @param track track
	 * @param sector sector
	 * @param numBlocks number of blocks
	 * @throws CbmException if partition is not supported on the image.
	 */
	public abstract void readPartition(int track, int sector, int numBlocks) throws CbmException;

	/**
	 * Get data of a single file.
	 * @param number the file number in the image
	 * @return byte array file file contents
	 * @throws CbmException when error
	 */
	public abstract byte[] getFileData(int number) throws CbmException;
	/**
	 * Write the data of a single file to image.
	 * @param saveData byte[]
	 * @return the first track/sector of the file (for use in directory entry).
	 */
	protected abstract TrackSector saveFileData(byte[] saveData);
	/**
	 * Set a disk name and disk-id in BAM.
	 * @param newDiskName the new name of the disk
	 * @param newDiskID the new id of the disk
	 */
	protected abstract void setDiskName(String newDiskName, String newDiskID);
	/**
	 * Copy attributes of bufferCbmFile to a directoryEntry in cbmDisk.
	 * @param cbmFile cbm file
	 * @param dirEntryNumber position where to put this entry in the directory
	 */
	protected abstract void writeDirectoryEntry(CbmFile cbmFile, int dirEntryNumber);
	/**
	 *
	 * @param filename file name
	 * @param newDiskName new disk name
	 * @param newDiskID new disk id
	 * @return true when successful
	 */
	public abstract boolean saveNewImage(String filename, String newDiskName, String newDiskID);
	/**
	 * Add a directory entry of a single file to the image.<BR>
	 * @param cbmFile the CbmFile
	 * @param destTrack track where file starts
	 * @param destSector sector where file starts
	 * @param isCopyFile indicates whether a file is copied or whether a file gets inserted into the directory
	 * @param lengthInBytes length in bytes
	 * @return returns true is adding the entry to the directory was successful
	 */
	public abstract boolean addDirectoryEntry(CbmFile cbmFile, int destTrack, int destSector, boolean isCopyFile, int lengthInBytes);
	/**
	 * Parse BAM track bits and store allocated/free blocks as strings.
	 * @return String[track][sector]
	 */
	public abstract String[][] getBamTable();
	/**
	 * Get offset to start of sector from beginning of image.
	 * @param track track
	 * @param sector sector
	 * @return offset offset to position in image where sector starts
	 * */
	public abstract int getSectorOffset(int track, int sector);
	/**
	 * Delete a file from disk image
	 * @param cbmFile The file to be deleted
	 * @throws CbmException when error
	 */
	public abstract void deleteFile(CbmFile cbmFile) throws CbmException;
	/**
	 * Validate image
	 * @param repairList list of error codes which should be corrected if found.
	 * @return number or validation errors
	 */
	public abstract Integer validate(List<Integer> repairList);
	public abstract boolean isSectorFree(int track, int sector);
	public abstract void markSectorFree(int track, int sector);
	public abstract void markSectorUsed(int track, int sector);

	public static String[] getImageTypeNames() {
		return IMAGE_TYPE_NAMES;
	}

	/**
	 * Initiate image structure.
	 * @param fileNumberLimit file number limit
	 */
	protected void initCbmFile(int fileNumberLimit) {
		cbmFile = new CbmFile[fileNumberLimit + 1];
		for (int i = 0; i < fileNumberLimit+1; i++) {
			cbmFile[i] = new CbmFile();
		}
	}

	public boolean isCpmImage() {
		return 	imageFormat == D64_CPM_C64_IMAGE_TYPE ||
				imageFormat == D64_CPM_C128_IMAGE_TYPE ||
				imageFormat == D71_CPM_IMAGE_TYPE ||
				imageFormat == D81_CPM_IMAGE_TYPE;
	}

	public static DiskImage getDiskImage(String filename, byte[] imageData) throws CbmException {
		String name = filename.toLowerCase();
		Map<Integer,List<String>> map = Settings.getFileExtensionMap();
		for (Entry<Integer, List<String>> entry : map.entrySet()) {
			for (String ext : entry.getValue()) {
				if (name.endsWith(ext.toLowerCase())) {
					switch (entry.getKey()) {
					case DiskImage.D64_IMAGE_TYPE:
						return new D64(imageData);
					case DiskImage.D67_IMAGE_TYPE:
						return new D67(imageData);
					case DiskImage.D71_IMAGE_TYPE:
						return new D71(imageData);
					case DiskImage.D81_IMAGE_TYPE:
						return new D81(imageData);
					case DiskImage.T64_IMAGE_TYPE:
						return new T64(imageData);
					case DiskImage.D80_IMAGE_TYPE:
						return new D80(imageData);
					case DiskImage.D82_IMAGE_TYPE:
						return new D82(imageData);
					case DiskImage.LNX_IMAGE_TYPE:
						return new LNX(imageData);
					default:
						// Try next
					}
				}
			}
		}
		throw new CbmException("Unknown file format.");
	}

	/**
	 * Load disk image from file. Use file name extension to identify type of disk image.
	 * @param filename file name
	 * @return DiskImage
	 * @throws CbmException if image could not be loaded (file missing, file corrupt out of memory etc).
	 */
	public static DiskImage getDiskImage(String filename) throws CbmException {
		String name = filename.toLowerCase();
		Map<Integer,List<String>> map = Settings.getFileExtensionMap();
		for (Entry<Integer, List<String>> entry : map.entrySet()) {
			for (String ext : entry.getValue()) {
				if (name.endsWith(ext.toLowerCase())) {
					switch (entry.getKey()) {
					case DiskImage.D64_IMAGE_TYPE:
						return new D64().readImage(filename);
					case DiskImage.D67_IMAGE_TYPE:
						return new D67().readImage(filename);
					case DiskImage.D71_IMAGE_TYPE:
						return new D71().readImage(filename);
					case DiskImage.D81_IMAGE_TYPE:
						return new D81().readImage(filename);
					case DiskImage.T64_IMAGE_TYPE:
						return new T64().readImage(filename);
					case DiskImage.D80_IMAGE_TYPE:
						return new D80().readImage(filename);
					case DiskImage.D82_IMAGE_TYPE:
						return new D82().readImage(filename);
					case DiskImage.LNX_IMAGE_TYPE:
						return new LNX().readImage(filename);
					default:
						// Try next extension
						break;
					}
				}
			}
		}
		throw new CbmException("Unknown file format.");
	}

	/**
	 * Load image from disk
	 * @param filename file name of disk image
	 * @param expectedFileSize if uncompressed image is smaller, then throw CbmException. If larger, print warning message.
	 * @param type the type of image to load. Used for logging.
	 * @return DiskImage
	 * @throws CbmException when error
	 */
	protected DiskImage readImage(String filename, int expectedFileSize, String type) throws CbmException {
		feedbackMessage = new StringBuilder();
		feedbackMessage.append("Trying to load "+type+" image ").append(filename).append("\n");
		this.cbmDisk = null;
		if (Utility.isGZipped(filename)) {
			feedbackMessage.append("GZIP compressed file detected.\n");
			cbmDisk = Utility.readGZippedFile(filename);
			compressed = true;
		} else {
			File file = new File(filename);
			if (!file.isFile()) {
				throw new CbmException("File is not a regular file.");
			} else if (file.length() <= 0) {
				throw new CbmException("File is empty.");
			} else if (file.length() > Integer.MAX_VALUE) {
				throw new CbmException("File is too large.");
			} else if (file.length() < expectedFileSize && expectedFileSize > 0) {
				throw new CbmException("File smaller than normal size. A "+type+" file should be " + expectedFileSize + " bytes.");
			} else if (file.length() > expectedFileSize && expectedFileSize > 0) {
				feedbackMessage.append("Warning: File larger than normal size. A "+type+" file should be ").append(expectedFileSize).append(" bytes.\n");
			}
			this.cbmDisk = Utility.readFile(file);
		}
		feedbackMessage.append(type+" disk image was loaded.\n");
		return this;
	}

	/**
	 * @param dirTrack directory track
	 * @param dirSectors directory sectors
	 * @param use16bitau true if using 16 bit allocation units
	 */
	protected void readCpmDirectory(int dirTrack, int[] dirSectors, boolean use16bitau) {
		if (!isCpmImage()) {
			return;
		}
		int filenumber = 0;
		CpmFile entry = null;
		for (int s=0; s<dirSectors.length; s++) {
			int idx = getSectorOffset(dirTrack, dirSectors[s]);
			for (int i=0; i < DIR_ENTRIES_PER_SECTOR; i++) {
				CpmFile newFile = getCpmFile(entry, idx + i * DIR_ENTRY_SIZE, use16bitau);
				if (newFile != null) {
					cbmFile[filenumber++] = newFile;
					entry = newFile;
				}
			}
		}
		filesUsedCount = filenumber;
	}

	/**
	 * Write the data and the directory entry of a single file to disk image.
	 * @param cbmFile the cbm file to save
	 * @param isCopyFile indicates whether a file is copied or whether a file gets inserted into the directory
	 * @param saveData the data to write to the file
	 * @return true if writing was successful (if there was enough space on disk image etc)
	 */
	public boolean saveFile(CbmFile cbmFile, boolean isCopyFile, byte[] saveData) {
		feedbackMessage = new StringBuilder();
		if (isCpmImage()) {
			feedbackMessage.append("saveFile: Not yet implemented for CP/M format.\n");
			return false;
		}
		if (!isCopyFile && cbmFile.getName().toLowerCase().endsWith(".prg")) {
			cbmFile.setName(cbmFile.getName().substring(0, cbmFile.getName().length()-4));
		}
		TrackSector firstBlock;
		if (cbmFile.getFileType() == CbmFile.TYPE_DEL && saveData.length == 0) {
			feedbackMessage.append("saveFile: '").append(cbmFile.getName()).append("'  (empty DEL file)\n");
			firstBlock = new TrackSector(0, 0);
		} else {
			feedbackMessage.append("saveFile: '").append(cbmFile.getName()).append("'  ("+saveData.length+" bytes)\n");
			firstBlock = saveFileData(saveData);
		}
		if (firstBlock != null) {
			if (addDirectoryEntry(cbmFile, firstBlock.track, firstBlock.sector, isCopyFile, saveData.length)) {
				return true;
			}
		} else {
			feedbackMessage.append("saveFile: Error occurred.\n");
		}
		return false;
	}

	/**
	 * Renames a disk image (label) <BR>
	 * @param filename	the filename
	 * @param newDiskName	the new name (label) of the disk
	 * @param newDiskID	the new disk-ID
	 * @return <code>true</code> when writing of the image file was successful
	 */
	public boolean renameImage(String filename, String newDiskName, String newDiskID){
		feedbackMessage = new StringBuilder("renameImage(): ").append(newDiskName).append(", ").append(newDiskID);
		if (isCpmImage()) {
			feedbackMessage.append(NOT_IMPLEMENTED_FOR_CPM);
			return false;
		}
		setDiskName(newDiskName, newDiskID);
		return writeImage(filename);
	}

	/**
	 * Sets new attributes for a single file.
	 * @param cbmFileNumber which file to rename
	 * @param newFileName the new name of the file
	 * @param newFileType the new type of the file (PRG, REL, SEQ, DEL, USR)
	 */
	public void renameFile(int cbmFileNumber, String newFileName, int newFileType) {
		feedbackMessage.append("renameFile: oldName '").append(cbmFile[cbmFileNumber].getName()).append(" newName '").append(newFileName).append("'\n");
		CbmFile newFile = new CbmFile(cbmFile[cbmFileNumber]);
		newFile.setName(newFileName);
		newFile.setFileType(newFileType);
		writeDirectoryEntry(newFile, newFile.getDirPosition());
	}

	/**
	 * @param previousFile the previously found entry, or null if nothing yet.
	 * @param pos offset into disk image
	 * @param use16bitau use 16 bit allocation units
	 * @return CpmFile if a new file entry was found and prepared. If previous was updated or entry was scratched null is returned.
	 */
	protected CpmFile getCpmFile(CpmFile previousFile, int pos, boolean use16bitau) {
		CpmFile newFile = null;
		int userNum = cbmDisk[pos + 0x00] & 0xff;
		if (userNum >=0x00 && userNum <= 0x0f) {
			return getCpmFileEntry(previousFile, pos, use16bitau);
		} else if (userNum == 0x20) {
			String label = Utility.getCpmString(cbmDisk, pos + 0x01, 8);
			String labelType = Utility.getCpmString(cbmDisk, pos + 0x09, 3);
			bam.setDiskName(label+"."+labelType);
			feedbackMessage.append("CP/M label "+label+"."+labelType);
		} else if (userNum != (UNUSED & 0xff)) {
			// 0x10 - 0x1f: password entries
			// 0x21: timestamp
		}
		return newFile;
	}

	private CpmFile getCpmFileEntry(CpmFile previousFile, int pos, boolean use16bitau) {
		CpmFile newFile = null;
		CpmFile tempFile = null;
		String name = Utility.getCpmString(cbmDisk, pos + 0x01, 8);
		String nameExt = Utility.getCpmString(cbmDisk, pos + 0x09, 3);
		boolean readOnly = (cbmDisk[pos + 0x09] & 0x80 ) == 0x80 ? true : false;
		boolean hidden   = (cbmDisk[pos + 0x0a] & 0x80 ) == 0x80 ? true : false;
		boolean archive  = (cbmDisk[pos + 0x0b] & 0x80 ) == 0x80 ? true : false;
		int extNum       =  cbmDisk[pos + 0x0c] & 0xff | ((cbmDisk[pos + 0x0e] & 0xff) << 8);
		int s1           =  cbmDisk[pos + 0x0d] & 0xff;	// Last Record Byte Count
		int rc           =  cbmDisk[pos + 0x0f] & 0xff;	// Record Count
		// Obviously, extNum is in numerical order, but it doesn't always start with 0, and it can skip some numbers.
		if (previousFile == null || !(previousFile.getCpmName().equals(name) && previousFile.getCpmNameExt().equals(nameExt)) ) {
			newFile = new CpmFile();
			newFile.setName(name + "." + nameExt);
			newFile.setFileType(CbmFile.TYPE_PRG);
			newFile.setCpmName(name);
			newFile.setCpmNameExt(nameExt);
			newFile.setReadOnly(readOnly);
			newFile.setArchived(archive);
			newFile.setHidden(hidden);
			newFile.setFileScratched(false);
			newFile.setSizeInBlocks(rc);
			newFile.setSizeInBytes(rc * CPM_RECORD_SIZE);
			tempFile = newFile;
		} else {
			previousFile.setSizeInBlocks(previousFile.getSizeInBlocks() + rc);
			previousFile.setSizeInBytes(previousFile.getSizeInBlocks() * CPM_RECORD_SIZE);
			tempFile = previousFile;
		}
		tempFile.setLastExtNum(extNum);
		tempFile.setLastRecordByteCount(s1);
		tempFile.setRecordCount(extNum * 128 + rc);
		readCpmAllocUnits(use16bitau, pos, tempFile);
		return newFile;
	}

	private void readCpmAllocUnits(boolean use16bitau, int pos, CpmFile cpmFile ) {
		if (use16bitau) {
			for (int al=0; al < 8; al++) {
				int au = ((cbmDisk[pos + 0x10 + al * 2 + 1] & 0xff) << 8) | (cbmDisk[pos + 0x10 + al * 2 + 0] & 0xff);
				if (au != 0) {
					cpmFile.addAllocUnit(au);
				}
			}
		} else {
			for (int al=0; al < 16; al++) {
				int au = cbmDisk[pos + 0x10 + al] & 0xff;
				if (au != 0) {
					cpmFile.addAllocUnit(au);
				}
			}
		}
	}

	/**
	 * Set up variables in a new cbmFile which will be appended to the directory.
	 * These variables will inserted into the directory later.
	 * @param cbmFile cbmFile
	 * @param thisFilename this file name
	 * @param thisFileType  this file type
	 * @param destTrack track number
	 * @param destSector sector number
	 * @param lengthInBytes file length in bytes
	 */
	protected void setNewDirEntry(CbmFile cbmFile, String thisFilename, int thisFileType, int destTrack, int destSector, int lengthInBytes) {
		cbmFile.setFileScratched(false);
		cbmFile.setFileType(thisFileType);
		cbmFile.setFileLocked(false);
		cbmFile.setFileClosed(true);
		cbmFile.setTrack(destTrack);
		cbmFile.setSector(destSector);
		cbmFile.setName(thisFilename);
		cbmFile.setRelTrack( 0);		//TODO: relative files
		cbmFile.setRelSector( 0);
		for (int i = 0; i < 7; i++) {
			cbmFile.setGeos(i,0);		//TODO: GEOS files
		}
		cbmFile.setSizeInBytes(lengthInBytes);
		cbmFile.setSizeInBlocks((cbmFile.getSizeInBytes() - 2) / 254	);
		if ( ((cbmFile.getSizeInBytes()-2) % 254) >0 ) {
			cbmFile.setSizeInBlocks(cbmFile.getSizeInBlocks()+1);
		}
	}

	/**
	 * Get <code>Disk</code> instance of current image. This is used when saving to database.
	 * @return Disk
	 */
	public Disk getDisk() {
		Disk disk = new Disk();
		disk.setLabel(getBam().getDiskName());
		disk.setImageType(imageFormat);
		disk.setErrors(errors);
		disk.setWarnings(warnings);
		for (int filenumber = 0; filenumber <= getFilesUsedCount() - 1;	filenumber++) {
			if (getCbmFile(filenumber) != null) {
				boolean isLocked = getCbmFile(filenumber).isFileLocked();
				boolean isClosed = getCbmFile(filenumber).isFileClosed();
				DiskFile file = new DiskFile();
				file.setName(getCbmFile(filenumber).getName());
				file.setSize(getCbmFile(filenumber).getSizeInBlocks());
				file.setFileType(getCbmFile(filenumber).getFileType());
				file.setFileNum(filenumber);
				file.setFlags((isLocked ? DiskFile.FLAG_LOCKED : 0) | (isClosed ? 0 : DiskFile.FLAG_NOT_CLOSED));
				disk.getFileList().add(file);
			}
		}
		return disk;
	}

	/**
	 * Return a string from a specified position on a block and having the specified length.
	 * @param track track
	 * @param sector sector
	 * @param pos position within block
	 * @param length the length of the returned string
	 * @return String, or null if outside of disk image.
	 */
	private String getStringFromBlock(int track, int sector, int pos, int length) {
		int dataPos = getSectorOffset(track, sector) + pos;
		if (dataPos + length < cbmDisk.length) {
			return new String(Arrays.copyOfRange(cbmDisk, dataPos, dataPos + length));
		} else {
			return null;
		}
	}

	private boolean checkCpmImageFormat() {
		String diskName = bam.getDiskName()!=null ? bam.getDiskName().replaceAll("\\u00a0", Utility.EMPTY).trim() : null;
		if (!CPM_DISKNAME_1.equals(diskName) && !CPM_DISKNAME_2.equals(diskName)) {
			return false;
		}
		String diskId = bam.getDiskId() != null ? bam.getDiskId().replaceAll("\\u00a0", Utility.SPACE).trim() : null;
		if (CPM_DISKID_GCR.equals(diskId)) {
			if ("CBM".equals(getStringFromBlock(1, 0, 0, 3))) {
				if (this instanceof D71 && (getCbmDiskValue(BLOCK_SIZE - 1) & 0xff) == 0xff) {
					feedbackMessage.append("CP/M C128 double sided disk detected.\n");
					imageFormat = D71_CPM_IMAGE_TYPE;
					return true;
				} else if (this instanceof D64) {
					feedbackMessage.append("CP/M C128 single sided disk detected.\n");
					imageFormat = D64_CPM_C128_IMAGE_TYPE;
					return true;
				}
			} else if (this instanceof D64 ) {
				feedbackMessage.append("CP/M C64 single sided disk detected.\n");
				imageFormat = D64_CPM_C64_IMAGE_TYPE;
				return true;
			}
		} else if (this instanceof D81 && CPM_DISKID_1581.equals(diskId)) {
			feedbackMessage.append("CP/M 3.5\" disk detected.\n");
			imageFormat = D81_CPM_IMAGE_TYPE;
			return true;
		}
		return false;
	}

	/**
	 * Checks, sets and return image format.
	 * @return image format
	 */
	public int checkImageFormat() {
		if (checkCpmImageFormat()) {
			return imageFormat;
		} else if (this instanceof D64) {
			imageFormat = D64_IMAGE_TYPE;
			geosFormat = DOS_LABEL_GEOS.equals(getStringFromBlock(D64.BAM_TRACK, D64.BAM_SECTOR, 0xad, DOS_LABEL_GEOS.length()));
		} else if (this instanceof D67) {
			imageFormat = D67_IMAGE_TYPE;
			geosFormat = DOS_LABEL_GEOS.equals(getStringFromBlock(D67.BAM_TRACK, D67.BAM_SECTOR, 0xad, DOS_LABEL_GEOS.length()));
		} else if (this instanceof D71) {
			imageFormat = D71_IMAGE_TYPE;
			geosFormat = DOS_LABEL_GEOS.equals(getStringFromBlock(D71.BAM_TRACK_1, D71.BAM_SECT, 0xad, DOS_LABEL_GEOS.length()));
		} else if (this instanceof D81) {
			imageFormat = D81_IMAGE_TYPE;
			geosFormat = DOS_LABEL_GEOS.equals(getStringFromBlock(D81.HEADER_TRACK, D81.HEADER_SECT, 0xad, DOS_LABEL_GEOS.length()));
		} else if (this instanceof T64) {
			imageFormat = T64_IMAGE_TYPE;
			geosFormat = false;
		} else if (this instanceof D80) {
			imageFormat = D80_IMAGE_TYPE;
			geosFormat = DOS_LABEL_GEOS.equals(getStringFromBlock(D80.HEADER_TRACK, D80.HEADER_SECT, 0xad, DOS_LABEL_GEOS.length()));
		} else if (this instanceof D82) {
			imageFormat = D82_IMAGE_TYPE;
			geosFormat = DOS_LABEL_GEOS.equals(getStringFromBlock(D82.HEADER_TRACK, D82.HEADER_SECT, 0xad, DOS_LABEL_GEOS.length()));
		} else if (this instanceof LNX) {
			imageFormat = LNX_IMAGE_TYPE;
			geosFormat = false;
		} else {
			imageFormat = UNKNOWN_IMAGE_TYPE;
			geosFormat = false;
		}
		if (geosFormat) {
			feedbackMessage.append("GEOS formatted image detected.\n");
		}
		return imageFormat;
	}

	/**
	 * Writes a image to file system<BR>
	 * @param filename the filename
	 * @return true if successfully written
	 */
	public boolean writeImage(String filename) {
		if (cbmDisk == null) {
			feedbackMessage.append("No disk data. Nothing to write.\n");
			return false;
		}
		feedbackMessage.append("writeImage: Trying to save ").append(compressed ? " compressed " : Utility.EMPTY).append(filename).append("... \n");
		try {
			if (compressed) {
				Utility.writeGZippedFile(filename, cbmDisk);
			} else {
				Utility.writeFile(new File(filename), cbmDisk);
			}
			return true;
		} catch (Exception e) {	//NOSONAR
			feedbackMessage.append("Error: Could not write filedata.\n").append(e.getMessage()).append("\n");
			return false;
		}
	}

	/**
	 * Switch directory locations of two files to move one of them upwards and the other downwards in the listing.
	 * @param cbmFile1 cbm file 1
	 * @param cbmFile2 cbm file 2
	 */
	public void switchFileLocations(CbmFile cbmFile1, CbmFile cbmFile2) {
		if (!isCpmImage()) {
			feedbackMessage.append("DiskImage.switchFileLocations: '"+cbmFile1.getName() + "'  '"+cbmFile2.getName()+"'\n");
			int tmpDirTrack = cbmFile2.getDirTrack();
			int tmpDirSector = cbmFile2.getDirSector();
			cbmFile2.setDirTrack(cbmFile1.getDirTrack());
			cbmFile2.setDirSector(cbmFile1.getDirSector());
			cbmFile1.setDirTrack(tmpDirTrack);
			cbmFile1.setDirSector(tmpDirSector);
			writeDirectoryEntry(cbmFile1, cbmFile2.getDirPosition());
			writeDirectoryEntry(cbmFile2, cbmFile1.getDirPosition());
		}
	}

	/**
	 * Determine if there's, at least, one free sector on a track.
	 * @param trackNumber the track number of sector to check.
	 * @return when true, there is at least one free sector on the track.
	 */
	protected boolean isTrackFree(int trackNumber) {
		readBAM();
		int freeSectors = bam.getFreeSectors(trackNumber);
		return freeSectors > 0 ? true : false;
	}

	/**
	 * Get byte from a position within disk image.
	 * @param position position
	 * @return data at position, or 0 if position is not within the size of image.
	 */
	protected int getCbmDiskValue(int position){
		try {
			return cbmDisk[ position ] & 0xff;
		} catch (ArrayIndexOutOfBoundsException e) {	// NOSONAR
			feedbackMessage.append("Error: reading outside of image at position "+position+"\n");
			return 0;
		}
	}

	/**
	 * Get byte from a block
	 * @param track the track
	 * @param sector the sector
	 * @param offset the offset within the block
	 * @return data at position, or 0 if position is not within the size of image.
	 */
	protected int getCbmDiskValue(int track, int sector, int offset){
		int pos = getSectorOffset(track, sector) + offset;
		try {
			return cbmDisk[ pos ] & 0xff;
		} catch (ArrayIndexOutOfBoundsException e) {	// NOSONAR
			feedbackMessage.append("Error: reading outside of image at position "+pos+"\n");
			return 0;
		}
	}

	/**
	 * Set a byte at a position on the disk image.
	 * @param position the position within disk image
	 * @param value value
	 */
	protected void setCbmDiskValue(int  position, int value){
		if (cbmDisk != null) {
			cbmDisk[ position] = (byte) value;
		}
	}

	/**
	 * Set a byte at a position on the disk image.
	 * @param track the track
	 * @param sector the sector
	 * @param offset offset within block
	 * @param value value
	 */
	protected void setCbmDiskValue(int track, int sector, int  offset, int value){
		int pos = getSectorOffset(track, sector) + offset;
		if (cbmDisk != null) {
			cbmDisk[ pos] = (byte) value;
		}
	}

	/**
	 * @return feedback message
	 */
	public String getFeedbackMessage() {
		String res = feedbackMessage.toString();
		feedbackMessage = new StringBuilder();
		return res;
	}

	/**
	 * @param string feedback message
	 */
	public void setFeedbackMessage(String string) {
		feedbackMessage = new StringBuilder(string);
	}

	/**
	 * @return max file number
	 */
	public int getFilesUsedCount() {
		return filesUsedCount;
	}

	/**
	 * @return maximum number of file entries which can be stored
	 */
	public int getFileCapacity() {
		return cbmFile != null ? cbmFile.length : 0;
	}

	/**
	 * @param number file number
	 * @return cbm file
	 */
	public CbmFile getCbmFile(int number) {
		if (number<cbmFile.length && number >= 0) {
			return cbmFile[number];
		} else {
			return null;
		}
	}

	/**
	 * @param number file number
	 * @param file cbm file
	 */
	public void setCbmFile(int number, CbmFile file) {
		cbmFile[number] = file;
	}

	/**
	 * @return BAM
	 */
	public CbmBam getBam() {
		return bam;
	}

	public void setCompressed(boolean compressed) {
		this.compressed  = compressed;
	}

	public int getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(int imageFormat) {
		this.imageFormat  = imageFormat;
	}

	public static boolean isImageFileName(File f) {
		if (f.isDirectory()) {
			return false;
		} else {
			return isImageFileName(f.getName());
		}
	}

	public static boolean isImageFileName(String fileName) {
		String lowerFileName = fileName.toLowerCase();
		Map<Integer,List<String>> map = Settings.getFileExtensionMap();
		for (Entry<Integer,List<String>> entry : map.entrySet()) {
			for (String ext : entry.getValue()) {
				if (lowerFileName.endsWith(ext.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return the number of validation errors, or null if validation has not been performed.
	 */
	public Integer getErrors() {
		return this.errors;
	}
	public Integer getWarnings() {
		return warnings;
	}
	public List<ValidationError> getValidationErrorList() {
		return validationErrorList;
	}
	public static String getImageTypeName(int imageType) {
		if (imageType < 0 || imageType >= IMAGE_TYPE_NAMES.length) {
			return IMAGE_TYPE_NAMES[0];
		}
		return IMAGE_TYPE_NAMES[imageType];
	}

	/**
	 * Get data on block.
	 * @param track track
	 * @param sector sector
	 * @return data from specified block
	 * @throws CbmException when error
	 */
	public byte[] getBlock(int track, int sector) throws CbmException {
		if (track < 1 || track > getTrackCount()) {
			throw new CbmException("Track "+track+" is not valid.");
		} else if (sector < 0 || sector >= getMaxSectors(track)) {
			throw new CbmException("Sector "+sector+" is not valid.");
		} else {
			int pos = getSectorOffset(track, sector);
			return Arrays.copyOfRange(cbmDisk, pos, pos + BLOCK_SIZE);
		}
	}

	/**
	 * Lookup first file matching criteria.
	 * @param name the name of the file
	 * @param fileType the typ eof file to lokk up
	 * @return found file or null if nothing found.
	 */
	public CbmFile findFile(String name, int fileType) {
		if (cbmFile == null || name == null) {
			return null;
		}
		for (CbmFile cf : cbmFile) {
			if (cf != null && cf.getName() != null && cf.getName().equals(name) && cf.getFileType() == fileType) {
				return cf;
			}
		}
		return null;
	}

	/**
	 * Fill sector in image with data. Pad with zeroes if saveData is smaller than BLOCK_SIZE - 2.
	 * @param track track number
	 * @param sector sector number
	 * @param dataPosition start filling sector at this position in saveData
	 * @param nextTrack next track
	 * @param nextSector next sector
	 * @param saveData data to fill sector with
	 */
	protected void fillSector(int track, int sector, int dataPosition, int nextTrack, int nextSector, byte[] saveData) {
		final int pos = getSectorOffset(track, sector);
		Arrays.fill(cbmDisk, pos + 0x02, pos + BLOCK_SIZE, (byte) 0);
		setCbmDiskValue(pos + 0x00, nextTrack);
		setCbmDiskValue(pos + 0x01, nextSector);
		for (int i = 0; i < (BLOCK_SIZE - 2); i++) {
			int value = saveData.length > dataPosition + i ? saveData[dataPosition + i] & 0xff : 0;
			setCbmDiskValue(pos + 0x02 + i, value);
		}
	}

}
