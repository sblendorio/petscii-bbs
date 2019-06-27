package droid64.d64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
public class D71 extends DiskImage {

	private static final long serialVersionUID = 1L;
	/** Name of the image type */
	public static final String IMAGE_TYPE_NAME = "D71";
	/** D71 format is restricted to a maximum of 144 directory entries (18 sectors with 8 entries each). Track 18 has 19 sectors, of which the first is the BAM. */
	protected static final int FILE_NUMBER_LIMIT = 144;
	/** Number of tracks */
	private static final int TRACK_COUNT = 70;
	/** Maximum number of sectors on any track */
	private static final int MAX_SECTORS = 21;
	/** The normal size of a D71 image (1366 * 256) */
	private static final  int D71_SIZE = 349696;
	/** Track number of directory */
	private static final int DIR_TRACK   = 18;
	/** Sector number of directory */
	private static final int DIR_SECT    = 1;
	/** Track number of BAM 1 */
	protected static final int BAM_TRACK_1 = 18;
	/** Track number of BAM 2 */
	protected static final int BAM_TRACK_2 = 53;
	/** Sector number of BAM 1 and 2 */
	protected static final int BAM_SECT  = 0;
	/** CP/M sector skew (distance between two sectors within one allocation unit) */
	private static final int CPM_SECTOR_SKEW = 5;
	/** Number of 256 bytes blocks (3 blocks are not used) */
	private static final int CPM_BLOCK_COUNT = 680;

	private static final int BLOCKS_PER_ALLOC_UNIT = 8;
	/** Track number of first track (may be above one for sub directories on 1581 disks) */
	private static final int FIRST_TRACK = 1;


	public D71() {
		bam = new CbmBam(D71Constants.D71_TRACKS.length, 4);
		initCbmFile(FILE_NUMBER_LIMIT);
	}

	public D71(byte[] imageData) {
		cbmDisk = imageData;
		bam = new CbmBam(D71Constants.D71_TRACKS.length, 4);
		initCbmFile(FILE_NUMBER_LIMIT);
	}

	@Override
	public int getMaxSectors(int trackNumber) {
		return D71Constants.D71_TRACKS[trackNumber].getSectors();
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
		bam = new CbmBam(D71Constants.D71_TRACKS.length, 4);
		return readImage(filename, D71_SIZE, IMAGE_TYPE_NAME);
	}

	@Override
	public void readBAM() {
		int bamOffset1 = getSectorOffset(BAM_TRACK_1, BAM_SECT);
		int bamOffset2 = getSectorOffset(BAM_TRACK_2, BAM_SECT);
		bam.setDiskName(Utility.EMPTY);
		bam.setDiskId(Utility.EMPTY);
		bam.setDiskDosType(getCbmDiskValue(bamOffset1 + 2 ));
		for (byte track = 1; track <= D71Constants.D71_TRACKS.length; track++) {
			if (track <= 35) {
				bam.setFreeSectors(track, (byte) getCbmDiskValue(bamOffset1 + 0x04 + (track-1) * 4));
				for (int i = 1; i < 4; i++) {
					bam.setTrackBits(track, i, (byte) getCbmDiskValue(bamOffset1 + 0x04 + (track-1) * 4 + i));
				}
			} else {
				bam.setFreeSectors(track, (byte) getCbmDiskValue(bamOffset1 + 0xdd + (track-1-35) * 1));
				for (int i = 0; i < 3; i++) {
					bam.setTrackBits(track, i+1, (byte) getCbmDiskValue(bamOffset2 + (track-36) * 3 + i));
				}
			}
		}
		bam.setDiskName(Utility.getString(cbmDisk, bamOffset1 + 0x90, DISK_NAME_LENGTH));
		bam.setDiskId(Utility.getString(cbmDisk, bamOffset1 + 0xa2, DISK_ID_LENGTH));
		checkImageFormat();
	}

	@Override
	public void readDirectory() {
		if (isCpmImage()) {
			readCpmDirectory(D71Constants.C128_DS_DIR_TRACK, D71Constants.C128_DS_DIR_SECTORS, false);
			return;
		}
		boolean fileLimitReached = false;
		int track = DIR_TRACK;
		int sector = DIR_SECT;
		int dirPosition = 0;
		int filenumber = 0;
		do {
			if (track >= D71Constants.D71_TRACKS.length) {
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
						fileLimitReached = true;
					}
				}
				dirPosition++;
			}
			track = getCbmDiskValue(dataPosition + 0);
			sector = getCbmDiskValue(dataPosition + 1);
		} while (track != 0 && !fileLimitReached);
		if (fileLimitReached) {
			feedbackMessage.append("Error: Too many entries in directory (more than ").append(FILE_NUMBER_LIMIT).append(")!\n");
		}
		filesUsedCount = filenumber;
		validate(new ArrayList<Integer>());
	}

	/**
	 * Get track/sector from CP/M sector number.
	 * @param sector  CP/M sector number
	 * @return TrackSector of specified CP/M sector
	 */
	private TrackSector getCpmTrackSector(int sector) {
		int num = sector;
		int upperHalf = 0;
		int trk = 0;
		int sec = 0;
		if (num >= CPM_BLOCK_COUNT) {
			num = num - CPM_BLOCK_COUNT;
			upperHalf = 35;
			if (num > CPM_BLOCK_COUNT) {
				return null;
			}
		}
		for (int i=0; i<4; i++) {
			num += D71Constants.CPM_ZONES[i][3];
			if (num < D71Constants.CPM_ZONES[i][2]) {
				trk = D71Constants.CPM_ZONES[i][0] + num / D71Constants.CPM_ZONES[i][1];
				sec = (CPM_SECTOR_SKEW * num) % D71Constants.CPM_ZONES[i][1];
				return new TrackSector(trk + upperHalf, sec);
			}
			num -= D71Constants.CPM_ZONES[i][2];
		}
		return new TrackSector(trk + upperHalf, sec);
	}

	@Override
	public byte[] getFileData(int number) throws CbmException {
		if (cbmDisk == null) {
			throw new CbmException("getFileData: No disk data exist.");
		} else if (number >= cbmFile.length) {
			throw new CbmException("getFileData: File number " + number + " does not exist.");
		} else if (isCpmImage()) {
			feedbackMessage.append("getFileData: CP/M mode.\n");
			if (cbmFile[number] instanceof CpmFile) {
				CpmFile cpm = (CpmFile)cbmFile[number];
				byte[] data = new byte[ cpm.getRecordCount() * CPM_RECORD_SIZE ];
				int dstPos = 0;
				for (Integer au : cpm.getAllocList()) {
					for (int r=0; r < BLOCKS_PER_ALLOC_UNIT; r++) {
						TrackSector ts = getCpmTrackSector(au * BLOCKS_PER_ALLOC_UNIT + r);
						if (ts == null) {
							throw new CbmException("Failed to find track/sector for allocation unit " + au + ".\n");
						}
						int srcPos = getSectorOffset(ts.getTrack(), ts.getSector());
						for (int c=0; c < BLOCK_SIZE && dstPos < data.length; c++) {
							data[dstPos++] = cbmDisk[srcPos + c];
						}
					}
				}
				return data;
			} else {
				throw new CbmException("Not yet implemented for CP/M format.");
			}
		} else if (cbmFile[number].isFileScratched()) {
			throw new CbmException("getFileData: File number " + number + " is deleted.");
		}
		feedbackMessage.append("getFileData: ").append(number).append(" '").append(cbmFile[number].getName()).append("'\n");
		feedbackMessage.append("Tracks / Sectors: ");
		int thisTrack = cbmFile[number].getTrack();
		int thisSector = cbmFile[number].getSector();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
			if (thisTrack >= D71Constants.D71_TRACKS.length) {
				throw new CbmException("Track " + thisTrack + " outside of image.");
			}
			int blockPos = getSectorOffset(thisTrack, thisSector);
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
		if (firstBlock == null) {
			feedbackMessage.append("\nsaveFileData: Error: No free sectors on disk. Disk is full.\n");
			return null;
		}
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
		return firstBlock;
	}

	/**
	 * Mark a sector in BAM as used.
	 * @param track trackNumber
	 * @param sector sectorNumber
	 */
	@Override
	public void markSectorUsed(int track, int sector) {
		int trackPos;
		int freePos;
		if (track <= 35) {
			trackPos = getSectorOffset(BAM_TRACK_1, BAM_SECT) + 4 * (track);
			freePos = trackPos;
		} else {
			trackPos = getSectorOffset(BAM_TRACK_2, BAM_SECT) + 3 * (track - 36);
			freePos = getSectorOffset(BAM_TRACK_1, BAM_SECT) + 0xdd + (track - 36);
		}
		int pos = (sector / 8) + (track<=35 ? 1 : 0);
		setCbmDiskValue(trackPos + pos, getCbmDiskValue(trackPos + pos) & INVERTED_BYTE_BIT_MASKS[sector & 0x07] );
		setCbmDiskValue(freePos, getCbmDiskValue(freePos) - 1);
	}

	/**
	 * Mark a sector in BAM as free.
	 * @param track trackNumber
	 * @param sector sectorNumber
	 */
	@Override
	public void markSectorFree(int track, int sector) {
		int trackPos;
		int freePos;
		if (track <= 35) {
			trackPos = getSectorOffset(BAM_TRACK_1, BAM_SECT) + 4 * (track);
			freePos = trackPos;
		} else {
			trackPos = getSectorOffset(BAM_TRACK_2, BAM_SECT) + 3 * (track - 36);
			freePos = getSectorOffset(BAM_TRACK_1, BAM_SECT) + 0xdd + (track - 36);
		}
		int pos = (sector / 8) + (track<=35 ? 1 : 0);
		setCbmDiskValue(trackPos + pos, getCbmDiskValue(trackPos + pos) | BYTE_BIT_MASKS[sector & 0x07] );
		setCbmDiskValue(freePos, getCbmDiskValue(freePos) + 1);
	}

	/**
	 * Determine if a sector is free.
	 * @param track the track number of sector to check
	 * @param sector the sector number of sector to check
	 * @return when True, the sector is free; otherwise used
	 */
	@Override
	public boolean isSectorFree(int track, int sector) {
		int trackPos;
		if (track <= 35) {
			trackPos = getSectorOffset(BAM_TRACK_1, BAM_SECT) + 4 * (track) + 1;
		} else {
			trackPos = getSectorOffset(BAM_TRACK_2, BAM_SECT) + 3 * (track - 36);
		}
		int value =  getCbmDiskValue(trackPos + (sector / 8)) & BYTE_BIT_MASKS[sector & 0x07];
		return value != 0;
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
			while (!found && distance < 128) {
				// Search until we find a track with free blocks or move too far from the directory track.
				block.track = BAM_TRACK_1 - distance;
				if (block.track >= FIRST_TRACK && block.track <= TRACK_COUNT && block.track != BAM_TRACK_2) {
					// Track within disk limits
					found = isTrackFree(block.track);
				}
				if (!found){
					// Check the track above the directory track
					block.track = BAM_TRACK_1 + distance;
					if (block.track <= TRACK_COUNT && block.track != BAM_TRACK_2) {
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
				int maxSector = getMaximumSectors(block.track);		// Determine how many sectors there are on that track.
				block.sector = 0;									// Start off with sector zero.
				do {
					found = isSectorFree(block.track, block.sector);
					if (!found) {
						block.sector++;	// Try the next sector.
					}
				} while (!found && block.sector <= maxSector);	// Repeat until there is a free sector or run off the track.
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
				int maxSector = getMaximumSectors(block.track);	// Get the number of sectors on the current track.
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
					if (block.track > FIRST_TRACK && block.track <= BAM_TRACK_1) {
						block.track = block.track - 1 ;
					} else if (block.track < TRACK_COUNT && block.track > BAM_TRACK_1) {
						block.track = block.track + 1 ;
						if (block.track == BAM_TRACK_2) {
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
						if (block.track == BAM_TRACK_2) {
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

	/**
	 * Determine the number of sectors (or the highest valid sector number plus one) for a track<BR>
	 * @param trackNumber track_number
	 * @return the number of sectors on the track
	 */
	private int getMaximumSectors(int trackNumber) {
		return D71Constants.D71_TRACKS[trackNumber].getSectors();
	}

	@Override
	protected void setDiskName(String newDiskName, String newDiskID) {
		feedbackMessage.append("setDiskName('").append(newDiskName).append("', '").append(newDiskID).append("')\n");
		Utility.setPaddedString(cbmDisk, getSectorOffset(BAM_TRACK_1, BAM_SECT) + 144, newDiskName, DISK_NAME_LENGTH);
		Utility.setPaddedString(cbmDisk, getSectorOffset(BAM_TRACK_1, BAM_SECT) + 162, newDiskID, DISK_ID_LENGTH);
	}

	@Override
	protected void writeDirectoryEntry(CbmFile cbmFile, int dirEntryNumber) {
		int entryNum = dirEntryNumber;
		int thisTrack = DIR_TRACK;
		int thisSector = 1;
		feedbackMessage.append("writeDirectoryEntry: bufferCbmFile to dirEntryNumber ").append(entryNum).append(".\n");
		if (entryNum > 7) {
			while (entryNum > 7) {
				int entryPos = getSectorOffset(thisTrack, thisSector);
				thisTrack  = getCbmDiskValue(entryPos + 0x00);
				thisSector = getCbmDiskValue(entryPos + 0x01);
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
		cbmDisk = new byte[D71_SIZE];
		Arrays.fill(cbmDisk, (byte) 0);
		Utility.copyBytes(D71Constants.NEWD71BAM1DATA, cbmDisk, 0x00000, 0x16500, D71Constants.NEWD71BAM1DATA.length);
		Utility.copyBytes(D71Constants.NEWD71BAM2DATA, cbmDisk, 0x00000, 0x41000, D71Constants.NEWD71BAM2DATA.length);
		setDiskName(Utility.cbmFileName(newDiskName, DISK_NAME_LENGTH), Utility.cbmFileName(newDiskID, DISK_NAME_LENGTH));
		return writeImage(filename);
	}

	@Override
	public boolean addDirectoryEntry(CbmFile cbmFile, int fileTrack, int fileSector, boolean isCopyFile, int lengthInBytes){
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
					final int[]  dirSectors = {
							1, 4, 7, 10, 13, 16,
							2, 5, 8, 11, 14, 17,
							3, 6, 9, 12, 15, 18 };
					boolean found = false;
					for (int i=0; !found && i<dirSectors.length; i++ ) {
						nextSector = dirSectors[i];
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
			for (int cnt = 1; cnt < 4; cnt++) {
				for (int bit = 0; bit < 8; bit++) {
					if (bitCounter <= getMaxSectors(trk)) {
						if (trk == BAM_TRACK_1 || trk == BAM_TRACK_2) {
							bamEntry[trk-1][bitCounter++] = CbmBam.RESERVED;
						} else if ((getBam().getTrackBits(trk, cnt) & DiskImage.BYTE_BIT_MASKS[bit]) == 0) {
							bamEntry[trk-1][bitCounter++] = CbmBam.USED;
						} else {
							bamEntry[trk-1][bitCounter++] = CbmBam.FREE;
						}
					}
				}
			}
		}
		return bamEntry;
	}

	@Override
	public int getSectorOffset(int track, int sector) {
		return D71Constants.D71_TRACKS[track].getOffset() + (BLOCK_SIZE * sector);
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
	public void readPartition(int track, int sector, int numBlocks) throws CbmException {
		throw new CbmException("D71 images does not support partitions.");
	}

	@Override
	public Integer validate(List<Integer> repairList) {
		// init to null
		Boolean[][] bamEntry = new Boolean[getTrackCount() + 1][getMaxSectorCount()];
		for (int trk = 0; trk < bamEntry.length; trk++) {
			Arrays.fill(bamEntry[trk], null);
		}
		// read all the chains of BAM/directory blocks. Mark each block as used and also check that
		// the block is not already marked as used. It would mean a block is referred to twice.
		// first check the chain of directory blocks.
		errors = 0;
		warnings = 0;
		int track = BAM_TRACK_1;
		int sector = BAM_SECT;
		validateDirEntries(track, sector, bamEntry);
		// follow each file and check data blocks
		for (int n=0; n < cbmFile.length; n++) {
			if (cbmFile[n].getFileType() == CbmFile.TYPE_CBM) {
				getValidationErrorList().add(new ValidationError(track, sector, ValidationError.ERROR_PARTITIONS_UNSUPPORTED, cbmFile[n].getName()));
				errors++;
				continue;
			} else if (cbmFile[n].getFileType() != CbmFile.TYPE_DEL) {
				track = cbmFile[n].getTrack();
				sector = cbmFile[n].getSector();
				if (track != 0) {
					validateFileData(track, sector, bamEntry, n);
				}
			}
		}
		// iterate BAM and verify used blocks is matching what we got when following data chains above.
		for (int trk = 1; trk <= getTrackCount(); trk++) {
			for (int sec = 0; sec < getMaxSectors(trk); sec++) {
				Boolean bamFree = Boolean.valueOf(isSectorFree(trk,sec));
				Boolean fileFree = bamEntry[trk][sec];
				if (fileFree == null && bamFree || bamFree.equals(fileFree)) {
					// no action
				} else if (Boolean.FALSE.equals(fileFree) && !Boolean.FALSE.equals(bamFree)) {
					getValidationErrorList().add(new ValidationError(trk,sec, ValidationError.ERROR_USED_SECTOR_IS_FREE));
					errors++;
				} else if (trk != BAM_TRACK_1 && trk != BAM_TRACK_2){
					getValidationErrorList().add(new ValidationError(trk,sec, ValidationError.ERROR_UNUSED_SECTOR_IS_ALLOCATED));
					warnings++;
				}
			}
		}
		return errors;
	}

	private void validateFileData(int startTrack, int startSector, Boolean[][] bamEntry, int fileNum) {
		int track = startTrack;
		int sector = startSector;
		List<TrackSector> fileErrorList = new ArrayList<>();
		do {
			if (errors > 1000) {
				getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_TOO_MANY));
				return;
			} else if (track >= bamEntry.length || sector >= bamEntry[track].length) {
				getValidationErrorList().add(new ValidationError(track, sector, ValidationError.ERROR_FILE_SECTOR_OUTSIDE_IMAGE, cbmFile[fileNum].getName()));
				errors++;
				return;
			} else if (bamEntry[track][sector] == null) {
				bamEntry[track][sector] = Boolean.FALSE;	// OK
			} else {
				errors++;
				// Detect cyclic references by keeping track of all sectors used by one file and check if a sector is already seen.
				TrackSector thisBlock = new TrackSector(track, sector);
				if (fileErrorList.contains(thisBlock)) {
					getValidationErrorList().add(new ValidationError(track, sector, ValidationError.ERROR_FILE_SECTOR_ALREADY_SEEN, cbmFile[fileNum].getName()));
					return;
				} else {
					fileErrorList.add(thisBlock);
				}
				if (bamEntry[track][sector].equals(Boolean.FALSE)) {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_FILE_SECTOR_ALREADY_USED));
				} else {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_FILE_SECTOR_ALREADY_FREE));
				}
			}
			int tmpTrack = track;
			int tmpSector = sector;
			track = getCbmDiskValue(getSectorOffset(tmpTrack, tmpSector) + 0x00);
			sector = getCbmDiskValue(getSectorOffset(tmpTrack, tmpSector) + 0x01);
		} while (track != 0);
	}

	private void validateDirEntries(int dirTrack, int dirSector, Boolean[][] bamEntry) {
		int track = dirTrack;
		int sector = dirSector;
		List<TrackSector> dirErrorList = new ArrayList<>();
		do {
			if (errors > 1000) {
				getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_TOO_MANY));
				return;
			} else if (track >= bamEntry.length || sector >= bamEntry[track].length) {
				getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_OUTSIDE_IMAGE));
				errors++;
				return;
			} else if (bamEntry[track][sector] == null) {
				bamEntry[track][sector] = Boolean.FALSE;
			} else {
				errors++;
				TrackSector thisBlock = new TrackSector(track, sector);
				if (dirErrorList.contains(thisBlock)) {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_ALREADY_SEEN));
					return;
				} else {
					dirErrorList.add(thisBlock);
				}
				if (bamEntry[track][sector].equals(Boolean.FALSE)) {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_ALREADY_USED));
				} else {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_ALREADY_FREE));
				}
			}
			int tmpTrack = track;
			int tmpSector = sector;
			track = getCbmDiskValue(getSectorOffset(tmpTrack, tmpSector) + 0x00);
			sector = getCbmDiskValue(getSectorOffset(tmpTrack, tmpSector) + 0x01);
		} while (track != 0);
	}

}
