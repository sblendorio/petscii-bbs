package droid64.d64;

import java.io.ByteArrayOutputStream;
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
public class D82 extends DiskImage {

	private static final long serialVersionUID = 1L;
	/** Name of the image type */
	public static final String IMAGE_TYPE_NAME = "D82";
	/** Track of disk header block */
	protected static final int HEADER_TRACK	= 39;
	/** Sector of disk header block */
	protected static final int HEADER_SECT	= 0;
	/** Max number of directory entries in image : (29 - 1) * 8 = 224 */
	protected static final int FILE_NUMBER_LIMIT = 224;
	/** The normal size of a D82 image */
	private static final int D82_SIZE       = 1066496;
	/** Maximum number of sectors on any track */
	private static final int MAX_SECTORS    = 29;
	/** Number of tracks of image */
	private static final int TRACK_COUNT	= 154;
	/** Track of BAM block 1 and BAM block 2 */
	private static final int BAM_TRACK	    = 38;
	/** Sector of BAM block 1 (38/0) */
	private static final int BAM_SECT_1	    = 0;
	/** Sector of BAM block 2 (38/3) */
	private static final int BAM_SECT_2	    = 3;
	/** Sector of BAM block 3 (38/6) */
	private static final int BAM_SECT_3	    = 6;
	/** Sector of BAM block 4 (38/9) */
	private static final int BAM_SECT_4	    = 9;
	/** Track of first directory block */
	private static final int DIR_TRACK		= 39;
	/** Sector of first directory block (40/3) */
	private static final int DIR_SECT		= 1;
	/** Track number of first track (may be above one for sub directories on 1581 disks) */
	private static final int FIRST_TRACK = 1;

	public D82() {
		bam = new CbmBam(D82Constants.D82_TRACKS.length, 5);
		initCbmFile(FILE_NUMBER_LIMIT);
	}

	public D82(byte[] imageData) {
		cbmDisk = imageData;
		bam = new CbmBam(D82Constants.D82_TRACKS.length, 5);
		initCbmFile(FILE_NUMBER_LIMIT);
	}

	@Override
	public int getMaxSectors(int trackNumber) {
		return D82Constants.D82_TRACKS[trackNumber].getSectors();
	}

	@Override
	public int getTrackCount() {
		return TRACK_COUNT;
	}

	@Override
	public int getMaxSectorCount() {
		return MAX_SECTORS;
	}

	@Override
	public int getBlocksFree() {
		int blocksFree = 0;
		if (cbmDisk != null) {
			for (int track = 1; track <= getTrackCount(); track++) {
				if (track != DIR_TRACK) {
					blocksFree = blocksFree + bam.getFreeSectors(track);
				}
			}
		}
		return blocksFree;
	}

	@Override
	protected DiskImage readImage(String filename) throws CbmException {
		bam = new CbmBam(D82Constants.D82_TRACKS.length, 5);
		return readImage(filename, D82_SIZE, IMAGE_TYPE_NAME);
	}

	@Override
	public void readBAM() {
		int headerOffset = getSectorOffset(HEADER_TRACK, HEADER_SECT);
		bam.setDiskName(Utility.EMPTY);
		bam.setDiskId(Utility.EMPTY);
		bam.setDiskDosType(getCbmDiskValue(headerOffset + 2 ));
		for (int track = 1; track <= D82Constants.D82_TRACKS.length; track++) {
			int pos = getBamTrackPos(track);
			bam.setFreeSectors(track, (byte) getCbmDiskValue(pos));
			for (int i = 1; i <= 4; i++) {
				bam.setTrackBits(track, i, (byte) getCbmDiskValue(pos + i));
			}
		}
		bam.setDiskName(Utility.getString(cbmDisk, headerOffset + 0x06, DISK_NAME_LENGTH));
		bam.setDiskId(Utility.getString(cbmDisk, headerOffset + 0x18, DISK_ID_LENGTH));
		checkImageFormat();
	}

	@Override
	public void readDirectory() {
		if (isCpmImage()) {
			// Read CP/M directory here
			return;
		}
		boolean fileLimitReached = false;
		int track = DIR_TRACK;
		int sector = DIR_SECT;
		int dirPosition = 0;
		int filenumber = 0;
		do {
			if (track >= D82Constants.D82_TRACKS.length) {
				feedbackMessage.append("Error: Track ").append(track).append(" is not within image.\n");
				break;
			}
			int dataPosition = getSectorOffset(track, sector);
			for (int i = 0; i < DIR_ENTRIES_PER_SECTOR; i ++) {
				cbmFile[filenumber] = new CbmFile(cbmDisk, dataPosition + (i * DIR_ENTRY_SIZE));
				if (!cbmFile[filenumber].isFileScratched()) {
					cbmFile[filenumber].setDirPosition(dirPosition);
					if (filenumber < FILE_NUMBER_LIMIT)  {
						filenumber++;
					} else {
						// Too many files in directory check
						feedbackMessage.append("Error: Too many entries in directory (more than ").append(FILE_NUMBER_LIMIT).append(")!\n");
						fileLimitReached = true;
					}
				}
				dirPosition++;
			}
			track = getCbmDiskValue(dataPosition + 0);
			sector = getCbmDiskValue(dataPosition + 1);
		} while (track != 0 && !fileLimitReached);
		filesUsedCount = filenumber;
		validate(null);
	}

	@Override
	public void readPartition(int track, int sector, int numBlocks) throws CbmException {
		throw new CbmException("Not yet implemented D82 partitions.");
	}

	@Override
	public byte[] getFileData(int number) throws CbmException {
		if (cbmDisk == null) {
			throw new CbmException("getFileData: No disk data exist.");
		} else if (number >= cbmFile.length) {
			throw new CbmException("getFileData: File number " + number + " does not exist.");
		} else if (isCpmImage()) {
			feedbackMessage.append("getFileData: CP/M mode.\n");
			throw new CbmException("Not yet implemented for CP/M format.");
		} else if (cbmFile[number].isFileScratched()) {
			throw new CbmException("getFileData: File number " + number + " is deleted.");
		}
		feedbackMessage.append("getFileData: ").append(number).append(" '").append(cbmFile[number].getName()).append("'\n");
		feedbackMessage.append("Tracks / Sectors: ");
		// write ints
		int thisTrack = cbmFile[number].getTrack();
		int thisSector = cbmFile[number].getSector();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
			if (thisTrack >= D82Constants.D82_TRACKS.length) {
				throw new CbmException("Track " + thisTrack + " outside of image.");
			}
			int blockPos = D82Constants.D82_TRACKS[thisTrack].getOffset() + (BLOCK_SIZE * thisSector);
			int nextTrack  = getCbmDiskValue(blockPos + 0);
			int nextSector = getCbmDiskValue(blockPos + 1);
			feedbackMessage.append(thisTrack).append("/").append(thisSector).append(Utility.SPACE);
			if (nextTrack > 0) {
				out.write(cbmDisk, blockPos + 2, BLOCK_SIZE - 2);
			} else {
				feedbackMessage.append("\nRemaining bytes: ").append(nextSector).append("\n");
				out.write(cbmDisk, blockPos + 2, nextSector - 2 + 1);
			}
			thisTrack = nextTrack;
			thisSector = nextSector;
		} while (thisTrack != 0);
		feedbackMessage.append("OK.\n");
		return out.toByteArray();
	}

	@Override
	protected TrackSector saveFileData(byte[] saveData) {
		if (isCpmImage()) {
			feedbackMessage.append(NOT_IMPLEMENTED_FOR_CPM);
			return null;
		}
		int usedBlocks = 0;
		int dataRemain = saveData.length;
		feedbackMessage.append("SaveFileData: ").append(dataRemain).append(" bytes of data.\n");
		TrackSector firstBlock = findFirstCopyBlock();
		if (firstBlock != null) {
			TrackSector block = new TrackSector(firstBlock.track, firstBlock.sector);
			int thisTrack;
			int thisSector;
			int dataPos = 0;
			while (dataRemain >= 0 && block != null) {
				feedbackMessage.append(dataRemain).append(" bytes remain: block ").append(block.track).append("/").append(block.sector).append("\n");
				thisTrack = block.track;
				thisSector = block.sector;
				markSectorUsed(thisTrack, thisSector);
				if (dataRemain >= (BLOCK_SIZE - 2)) {
					block = findNextCopyBlock(block);
					if (block != null) {
						fillSector(thisTrack, thisSector, dataPos, block.track, block.sector, saveData);
						usedBlocks++;
						dataRemain = dataRemain - (BLOCK_SIZE - 2);
						dataPos = dataPos + (BLOCK_SIZE - 2);
					} else {
						feedbackMessage.append("\nsaveFileData: Error: Not enough free sectors on disk. Disk is full.\n");
						firstBlock = null;
					}
				} else {
					fillSector(thisTrack, thisSector, dataPos, 0, dataRemain + 1, saveData);
					usedBlocks++;
					dataRemain = -1;
				}
			}
			if (dataRemain <= 0) {
				feedbackMessage.append("All data written ("+usedBlocks+" blocks).\n");
			}
		} else {
			feedbackMessage.append("\nsaveFileData: Error: No free sectors on disk. Disk is full.\n");
			return null;
		}
		return firstBlock;
	}

	/**
	 * Find a sector for the first block of the file,
	 * @return track/sector or null if none is available.
	 */
	private TrackSector findFirstCopyBlock() {
		TrackSector block = new TrackSector(0, 0);
		if (geosFormat) {
			// GEOS formatted disk, so use the other routine, from track one upwards.
			block.track = 1;
			block.sector = 0;
			block = findNextCopyBlock(block);
		} else {
			boolean found = false;	// No free sector found yet
			int distance = 1;		// On a normal disk, start looking checking the tracks just besides the directory track.
			while (!found && distance < 200) {
				// Search until we find a track with free blocks or move too far from the directory track.
				block.track = BAM_TRACK - distance;
				if (block.track >= FIRST_TRACK && block.track <= TRACK_COUNT && block.track != HEADER_TRACK) {
					// Track within disk limits
					found = isTrackFree(block.track);
				}
				if (!found){
					// Check the track above the directory track
					block.track = BAM_TRACK + distance;
					if (block.track <= TRACK_COUNT && block.track != HEADER_TRACK) {
						// Track within disk limits
						found = isTrackFree(block.track);
					}
				}
				if (!found) {
					// Move one track further away from the directory track and try again.
					distance++;
				}
			}
			if (found) {
				// Found a track with, at least one free sector, so search for a free sector in it.
				int maxSector = getMaxSectors(block.track);		// Determine how many sectors there are on that track.
				block.sector = 0;									// Start off with sector zero.
				do {
					found = isSectorFree(block.track, block.sector);
					if (!found) {
						block.sector++;	// Try the next sector.
					}
				} while (!found && block.sector <= maxSector);	// Repeat until there is a free sector or run off the track.
				if (!found) {
					feedbackMessage.append("firstCopyBlock: Error: "+block.track +" should have free sectors but didn't.\n");
					block = null;
				}
			} else {
				// Disk full. No tracks with any free blocks.
				block = null;
			}
		}
		if (block != null) {
			feedbackMessage.append("firstCopyBlock: The first block will be ").append(block.track).append("/").append(block.sector).append(".\n");
		} else {
			feedbackMessage.append("firstCopyBlock: Error: Disk is full!\n");
		}
		return block;
	}

	/**
	 * Find a sector for the next block of the file, using variables Track and Sector.
	 * @param block
	 * @return sector found, null if no more sectors left
	 */
	private TrackSector findNextCopyBlock(TrackSector block) {
		boolean found;
		if ((block.track == 0) || (block.track > TRACK_COUNT)) {
			// If we somehow already ran off the disk then there are no more free sectors left.
			return null;
		}
		int tries = 3;			// Set the number of tries to three.
		found = false;			// We found no free sector yet.
		int curTrack = block.track;		// Remember the current track number.
		while (!found && tries > 0) {
			// Keep trying until we find a free sector or run out of tries.
			if (isTrackFree(block.track)) {
				// If there's, at least, one free sector on the track then get searching.
				if (block.track == curTrack || !geosFormat) {
					// If this is a non-GEOS disk or we're still on the same track of a GEOS-formatted disk then...
					block.sector = block.sector + C1571_INTERLEAVE;	// Move away an "interleave" number of sectors.
					if (geosFormat && block.track >= 25) {
						// Empirical GEOS optimization, get one sector backwards if over track 25.
						block.sector--;
					}
				} else {
					// For a different track of a GEOS-formatted disk, use sector skew.
					block.sector = (block.track - curTrack) << 1 + 4 + C1571_INTERLEAVE;
				}
				int maxSector = getMaxSectors(block.track);	// Get the number of sectors on the current track.
				while (block.sector >= maxSector) {
					// If we ran off the track then correct the result.
					block.sector = block.sector - maxSector + 1;	// Subtract the number of sectors on the track.
					if (block.sector > 0 && !geosFormat) {
						// Empirical optimization, get one sector backwards if beyond sector zero.
						block.sector--;
					}
				}
				int curSector = block.sector;	// Remember the first sector to be checked.
				do {
					found = isSectorFree(block.track, block.sector);
					if (!found) {
						block.sector++;	// Try next sector
					}
					if (block.sector >= maxSector) {
						block.sector = 0;	// Went off track, wrap around to sector 0.
					}
				} while (!found && block.sector != curSector);	// Continue until finding a free sector, or we are back on the curSector again.
				if (!found) {
					// According to the free sector counter in BAM, this track should have free sectors, but it didn't.
					// Try a different track. Obviously, this disk needs to be validated.
					feedbackMessage.append("Warning: Track ").append(block.track).append(" should have at least one free sector, but didn't.");
					if (block.track > FIRST_TRACK && block.track <= BAM_TRACK) {
						block.track = block.track - 1 ;
					} else if (block.track < TRACK_COUNT && block.track > BAM_TRACK) {
						block.track = block.track + 1 ;
						if (block.track == HEADER_TRACK) {
							block.track = block.track + 1 ;
						}
					} else {
						tries--;
					}
				}
			} else {
				if (block.track == DIR_TRACK) {
					// If we already tried the directory track then there are no more tries.
					tries = 0;
				} else {
					if (block.track < DIR_TRACK) {
						block.track --;	//If we're below the directory track then move one track downwards.
						if (block.track < FIRST_TRACK) {
							block.track = DIR_TRACK + 1; //If we ran off the disk then step back to the track just above the directory track and zero the sector number.
							block.sector = 0;
							//If there are no tracks available above the directory track then there are no tries left; otherwise just decrease the number of tries.
							if (block.track <= TRACK_COUNT) {
								tries--;
							} else {
								tries = 0;
							}
						}
					} else {
						block.track++;	//If we're above the directory track then move one track upwards.
						if (block.track == BAM_TRACK) {
							block.track++;
						}
						if (block.track > TRACK_COUNT) {
							block.track = DIR_TRACK - 1;	//If we ran off the disk then step back to the track just below the directory track and zero the sector number.
							block.sector = 0;
							//If there are no tracks available below the directory track then there are no tries left; otherwise just decrease the number of tries.
							if (block.track >= FIRST_TRACK) {
								tries--;
							} else {
								tries = 0;
							}
						}
					}
				}
			}
		}
		return found ? block : null;
	}

	@Override
	protected void setDiskName(String newDiskName, String newDiskID) {
		feedbackMessage.append("setDiskName: '").append(newDiskName).append("', '").append(newDiskID).append("'\n");
		Utility.setPaddedString(cbmDisk, getSectorOffset(HEADER_TRACK, HEADER_SECT) + 0x06, newDiskName, DISK_NAME_LENGTH);
		Utility.setPaddedString(cbmDisk, getSectorOffset(HEADER_TRACK, HEADER_SECT) + 0x18, newDiskID, DISK_ID_LENGTH);
	}

	@Override
	protected void writeDirectoryEntry(CbmFile cbmFile, int dirEntryNumber) {
		int entryNum = dirEntryNumber;
		int thisTrack = DIR_TRACK;
		int thisSector = 1;
		feedbackMessage.append("writeDirectoryEntry: bufferCbmFile to dirEntryNumber ").append(entryNum).append(".\n");
		if (entryNum > 7) {
			while (entryNum > 7) {
				thisTrack  = getCbmDiskValue(getSectorOffset(thisTrack, thisSector) + 0x00);
				thisSector = getCbmDiskValue(getSectorOffset(thisTrack, thisSector) + 0x01);
				feedbackMessage.append("LongDirectory: "+entryNum+" dirEntrys remain, next block: "+thisTrack+"/"+thisSector+"\n");
				entryNum = entryNum - 8;
			}
		}
		int pos = getSectorOffset(thisTrack, thisSector) + entryNum * 32;
		setCbmDiskValue(pos + 0, cbmFile.getDirTrack());
		setCbmDiskValue(pos + 1, cbmFile.getDirSector());
		writeSingleDirectoryEntry(cbmFile, pos);
	}

	@Override
	public boolean saveNewImage(String filename, String newDiskName, String newDiskID) {
		cbmDisk = new byte[D82_SIZE];
		Arrays.fill(cbmDisk, (byte) 0);
		if (!isCpmImage()) {
			Utility.copyBytes(D82Constants.NEWD82DATA_1, cbmDisk, 0x00000, 0x43100, D82Constants.NEWD82DATA_1.length);
			Utility.copyBytes(D82Constants.NEWD82DATA_2, cbmDisk, 0x00000, 0x43400, D82Constants.NEWD82DATA_2.length);
			Utility.copyBytes(D82Constants.NEWD82DATA_3, cbmDisk, 0x00000, 0x43700, D82Constants.NEWD82DATA_3.length);
			Utility.copyBytes(D82Constants.NEWD82DATA_4, cbmDisk, 0x00000, 0x43a00, D82Constants.NEWD82DATA_4.length);
			setCbmDiskValue(0x44e00+0, 38);
			setCbmDiskValue(0x44e00+1, 0);
			setCbmDiskValue(0x44e00+2, 'C');
			setCbmDiskValue(0x44f00+1, 0xff);
			setDiskName(Utility.cbmFileName(newDiskName, DISK_NAME_LENGTH), Utility.cbmFileName(newDiskID, DISK_NAME_LENGTH));
			return writeImage(filename);
		}
		return false;
	}

	@Override
	public boolean addDirectoryEntry(CbmFile cbmFile, int fileTrack, int fileSector, boolean isCopyFile, int lengthInBytes) {
		feedbackMessage.append(String.format("addDirectoryEntry: \"%s\", %s, %d/%d%n", cbmFile.getName(), CbmFile.FILE_TYPES[cbmFile.getFileType()], fileTrack, fileSector));
		if (isCpmImage()) {
			feedbackMessage.append("Not yet implemented for CP/M format.\n");
			return false;
		}
		if (isCopyFile) {
			// This a substitute for setNewDirectoryEntry(thisFilename, thisFiletype, destTrack, destSector, dirPosition)
			// since we do not need to set other values than destTrack and destSector when copying a file.
			cbmFile.setTrack(fileTrack);
			cbmFile.setSector(fileSector);
		} else {
			setNewDirEntry(cbmFile, cbmFile.getName(), cbmFile.getFileType(), fileTrack, fileSector, lengthInBytes);
		}
		cbmFile.setDirTrack(0);
		cbmFile.setDirSector(-1);
		int dirEntryNumber = findFreeDirEntry();
		if (dirEntryNumber != -1 && setNewDirLocation(cbmFile, dirEntryNumber)) {
			writeSingleDirectoryEntry(cbmFile, getDirectoryEntryPosition(dirEntryNumber));
			filesUsedCount++;	// increase the maximum file numbers
			return true;
		} else {
			feedbackMessage.append("Error: Could not find a free sector on track "+DIR_TRACK+" for new directory entries.\n");
			return false;
		}
	}

	/**
	 * Copy attributes of bufferCbmFile to a location in cbmDisk.
	 * @param cbmFile
	 * @param where data position where to write to cbmDisk
	 */
	private void writeSingleDirectoryEntry(CbmFile cbmFile, int where){
		if (isCpmImage()) {
			feedbackMessage.append("Not yet implemented for CP/M format.\n");
			return ;
		}
		feedbackMessage.append("writeSingleDirectoryEntry: dirpos="+cbmFile.getDirPosition()+"\n");
		cbmFile.toBytes(cbmDisk, where);
	}

	/**
	 * Find offset to a directory entry.
	 * @param dirEntryNumber directory entry number to look up
	 * @return offset in image to directory entry, or -1 if dirEntry is not available.
	 */
	private int getDirectoryEntryPosition(int dirEntryNumber) {
		if (dirEntryNumber < 0 || dirEntryNumber >= FILE_NUMBER_LIMIT) {
			return -1;
		}
		int track = DIR_TRACK;
		int sector = 1;
		int entryPosCount = 8;
		while (dirEntryNumber >= entryPosCount && track != 0) {
			track = getCbmDiskValue(getSectorOffset(track, sector) + 0x00);
			sector = getCbmDiskValue(getSectorOffset(track, sector) + 0x01);
			entryPosCount += 8;
		}
		if (track == 0) {
			return -1;
		} else {
			return getSectorOffset(track, sector) + (dirEntryNumber & 0x07) * 32;
		}
	}

	/**
	 * Iterate directory sectors to find the specified directory entry. If needed, attempt to allocate more directory sectors
	 * and continue iterating until either directory entry is available or FILE_NUMBER_LIMIT is reached,
	 * @param cbmFile
	 * @param dirEntryNumber position where to put this entry in the directory
	 * @return returns true if a free directory block was found
	 */
	private boolean setNewDirLocation(CbmFile cbmFile, int dirEntryNumber){
		if (dirEntryNumber < 0 || dirEntryNumber >= FILE_NUMBER_LIMIT) {
			feedbackMessage.append( "Error: Invalid directory entry number ").append(dirEntryNumber).append(" at setNewDirectoryLocation.\n");
			return false;
		} else if ( (dirEntryNumber & 0x07) != 0) {
			// If this is not the eighth entry we are lucky and do not need to do anything...
			cbmFile.setDirTrack(0);
			cbmFile.setDirSector(0);
			return true;
		} else {
			//find the correct entry where to write new values for dirTrack and dirSector
			int thisTrack = DIR_TRACK;
			int thisSector = 1;
			int entryPosCount = 8;
			while (dirEntryNumber >= entryPosCount) {
				int nextTrack = getCbmDiskValue(getSectorOffset(thisTrack, thisSector) + 0x00);
				int nextSector = getCbmDiskValue(getSectorOffset(thisTrack, thisSector) + 0x01);
				if (nextTrack == 0) {
					nextTrack = thisTrack;
					boolean found = false;
					for (int i=0; !found && i<D82Constants.DIR_SECTORS.length; i++ ) {
						nextSector = D82Constants.DIR_SECTORS[i];
						found = isSectorFree(nextTrack, nextSector);
					}
					if (found) {
						nextTrack = thisTrack;
						markSectorUsed(nextTrack, nextSector);
						setCbmDiskValue(getSectorOffset(thisTrack, thisSector) + 0x00, nextTrack);
						setCbmDiskValue(getSectorOffset(thisTrack, thisSector) + 0x01, nextSector);
						setCbmDiskValue(getSectorOffset(nextTrack, nextSector) + 0x00, 0);
						setCbmDiskValue(getSectorOffset(nextTrack, nextSector) + 0x01, -1);
						feedbackMessage.append("Allocated additonal directory sector (").append(nextTrack).append("/").append(nextSector).append(") for dir entry ").append(dirEntryNumber).append(".\n");
					} else {
						feedbackMessage.append( "Error: no more directory sectors. Can't add file.\n");
						return false;
					}
				}
				thisTrack = nextTrack;
				thisSector = nextSector;
				entryPosCount += 8;
			}
			return true;
		}
	}

	/**
	 * Find first free directory entry.
	 * Looks through the allocated directory sectors.
	 * @return number of next free directory entry, or -1 if none is free.
	 */
	private int findFreeDirEntry() {
		int track = DIR_TRACK;
		int sector = 1;
		int dirPosition = 0;
		do {
			int dataPosition = getSectorOffset(track, sector);
			for (int i = 0; i < DIR_ENTRIES_PER_SECTOR; i++) {
				int fileType = cbmDisk[dataPosition + (i * DIR_ENTRY_SIZE) + 0x02] & 0xff;
				if (fileType  == 0) {
					// Free or scratched entry
					return dirPosition;
				}
				dirPosition++;
			}
			track = getCbmDiskValue(dataPosition + 0);
			sector = getCbmDiskValue(dataPosition + 1);
		} while (track != 0);
		if (dirPosition < FILE_NUMBER_LIMIT + 2) {
			// next entry, on a new dir sector. not yet hit max number of entries.
			return dirPosition;
		} else {
			// Hit max number of file entries. can't add more.
			feedbackMessage.append("Error: No free directory entry avaiable.\n");
			return -1;
		}
	}


	@Override
	public String[][] getBamTable() {
		String[][] bamEntry = new String[TRACK_COUNT][MAX_SECTORS + 1];
		for (int trk = 0; trk < TRACK_COUNT; trk++) {
			for (int sec = 0; sec <= MAX_SECTORS; sec++) {
				bamEntry[trk][sec] =  CbmBam.INVALID;
			}
		}
		for (int trk = 1; trk <= TRACK_COUNT; trk++) {
			int bitCounter = 1;
			bamEntry[trk-1][0] = Integer.toString(trk);
			for (int cnt = 1; cnt <= 4; cnt++) {
				for (int bit = 0; bit < 8 && bitCounter <= getMaxSectors(trk); bit++) {
					setBamSector(bamEntry, trk, bitCounter++, cnt, bit);
				}
			}
		}
		return bamEntry;
	}

	private void setBamSector(String[][] bamEntry, int trk, int sec, int bamByteNum, int bitNum) {
		if (trk == BAM_TRACK) {
			bamEntry[trk-1][sec] = CbmBam.RESERVED;
		} else if ((getBam().getTrackBits(trk, bamByteNum) & DiskImage.BYTE_BIT_MASKS[bitNum]) == 0) {
			bamEntry[trk-1][sec] = CbmBam.USED;
		} else {
			bamEntry[trk-1][sec] = CbmBam.FREE;
		}
	}

	private int getBamTrackPos(int track) {
		if (track <= 50) {
			return getSectorOffset(BAM_TRACK, BAM_SECT_1) + track * 5 + 1;
		} else if (track <= 100) {
			return getSectorOffset(BAM_TRACK, BAM_SECT_2) + (track - 50) * 5 + 1;
		} else if (track <= 150) {
			return getSectorOffset(BAM_TRACK, BAM_SECT_3) + (track - 100) * 5 + 1;
		} else {
			return getSectorOffset(BAM_TRACK, BAM_SECT_4) + (track - 150) * 5 + 1;
		}
	}

	@Override
	public int getSectorOffset(int track, int sector) {
		return D82Constants.D82_TRACKS[track].getOffset() + (BLOCK_SIZE * sector);
	}

	@Override
	public void deleteFile(CbmFile cbmFile) throws CbmException {
		feedbackMessage = new StringBuilder();
		if (isCpmImage()) {
			throw new CbmException("Delete not yet implemented for CP/M format.");
		}
		cbmFile.setFileType(0);
		cbmFile.setFileScratched(true);
		int dirEntryNumber = cbmFile.getDirPosition();
		int dirEntryPos = getDirectoryEntryPosition(dirEntryNumber);
		if (dirEntryPos != -1) {
			setCbmDiskValue(dirEntryPos + 0x02, 0);
			// Free used blocks
			int track = cbmFile.getTrack();
			int sector = cbmFile.getSector();
			while (track != 0) {
				int tmpTrack = getCbmDiskValue(track, sector, 0x00);
				int tmpSector = getCbmDiskValue(track, sector, 0x01);
				markSectorFree(track, sector);
				track = tmpTrack;
				sector = tmpSector;
			}
		} else {
			feedbackMessage.append("Error: Failed to delete ").append(cbmFile.getName());
		}
	}

	@Override
	public Integer validate(List<Integer> repairList) {
		return 0;
	}

	@Override
	public boolean isSectorFree(int track, int sector) {
		int trackPos = getBamTrackPos(track);
		int pos = (sector / 8) + 1;
		int value =  getCbmDiskValue(trackPos + pos) & BYTE_BIT_MASKS[sector & 0x07];
		return value != 0;
	}

	@Override
	public void markSectorFree(int track, int sector) {
		int trackPos = getBamTrackPos(track);
		int pos = (sector / 8) + 1;
		setCbmDiskValue(trackPos + pos, getCbmDiskValue(trackPos + pos) | BYTE_BIT_MASKS[sector & 0x07] );
		setCbmDiskValue(trackPos, getCbmDiskValue(trackPos) + 1);
	}

	@Override
	public void markSectorUsed(int track, int sector) {
		// BAM on 38/1 (track 1-50), 38/3 (track 51-100), 38/6 (track 101-150) and 38/9 (track 151-154)
		// Skip first 6 bytes on BAM sector. 5 bytes per track, the first byte is free sectors.
		int trackPos = getBamTrackPos(track);
		int pos = (sector / 8) + 1;
		setCbmDiskValue(trackPos + pos, getCbmDiskValue(trackPos + pos) & INVERTED_BYTE_BIT_MASKS[sector & 0x07] );
		setCbmDiskValue(trackPos, getCbmDiskValue(trackPos) - 1);
	}

}
