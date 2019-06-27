package droid64.d64;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**<pre style='font-family:sans-serif;'>
 * Created on 21.06.2004
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
 *   eMail: wolfvoz@users.sourceforge.net
 *   http://droid64.sourceforge.net
 *
 * @author henrik
 * </pre>
 */
public class D67 extends DiskImage {

	private static final long serialVersionUID = 1L;
	/** Name of the image type */
	public static final String IMAGE_TYPE_NAME = "D67";
	/** Track number of directory track */
	protected static final int DIR_TRACK = 18;
	/** Track number of secondary directory track (for 1571 disks); 255 (a non-existent track), if not available */
	protected static final int DIR_TRACK2 = 255;

	protected static final int BAM_TRACK = 18;
	protected static final int BAM_SECTOR = 0;
	/** The normal size of a D67 image (690 * 256) */
	private static final int D67_SIZE = 176640;
	/** D67 format is restricted to a maximum of 144 directory entries (18 sectors with 8 entries each). Track 18 has 19 sectors, of which the first is the BAM. */
	protected static final int FILE_NUMBER_LIMIT = 144;
	/** Number of tracks */
	private static final int TRACK_COUNT = 35;
	/** Maximum number of sectors on any track */
	private static final int MAX_SECTORS = 21;
	/** CP/M sector skew (distance between two sectors within one allocation unit) */
	private static final int CPM_SECTOR_SKEW = 5;
	/** Number of 256 bytes blocks (3 blocks are not used) */
	private static final int CPM_BLOCK_COUNT = 680;
	/** Blocks per CP/M allocation unit (4 * 256 = 1024). */
	private static final int BLOCKS_PER_ALLOC_UNIT = 4;
	/** Track number of first track (may be above one for sub directories on 1581 disks) */
	private static final int FIRST_TRACK = 1;

	/**
	 * Constructor
	 */
	public D67() {
		initCbmFile(FILE_NUMBER_LIMIT);
		bam = new CbmBam(D67Constants.D67_TRACKS.length, 4);
	}

	/**
	 * Constructor
	 * @param imageData the disk image data
	 */
	public D67(byte[] imageData) {
		cbmDisk = imageData;
		initCbmFile(FILE_NUMBER_LIMIT);
		bam = new CbmBam(D67Constants.D67_TRACKS.length, 4);
	}

	@Override
	protected DiskImage readImage(String filename) throws CbmException {
		bam = new CbmBam(D67Constants.D67_TRACKS.length, 4);
		return readImage(filename, D67_SIZE, IMAGE_TYPE_NAME);
	}

	/**
	 * Reads the BAM of the D67 image and fills bam[] with entries.<br>
	 * <pre>
	 * Bytes:$00-01: Track/Sector location of the first directory sector (should be set to 18/1
	 *          but it doesn't matter,  and don't trust  what is there,  always go to  18/1 for
	 *          first directory entry)
	 *		02: Disk DOS version type (see note below) $41 ("A")
	 *		03: Unused
	 *	 04-8F: BAM entries for each track, in groups  of  four  bytes  per
	 *			track, starting on track 1 (see below for more details)
	 *	 90-9F: Disk Name (padded with $A0)
	 *	 A0-A1: Filled with $A0
	 *	 A2-A3: Disk ID
	 *		A4: Usually $A0
	 *	 A5-A6: DOS type, usually "2A"
	 *	 A7-AA: Filled with $A0
	 *	 AB-FF: Normally unused ($00), except for 40 track extended format,
	 *			see the following two entries:
	 *	 AC-BF: DOLPHIN DOS track 36-40 BAM entries (only for 40 track)
	 *	 C0-D3: SPEED DOS track 36-40 BAM entries (only for 40 track)
	 *
	 *  BAM_struct = record
	 *  diskDosType : byte;
	 *  trackBits   : array [1..40, 1..4] of byte;
	 *  diskName    : string[16];
	 *  diskId      : string[2];
	 *  DOS_type    : string[2];
	 *</pre>
	 */
	@Override
	public void readBAM() {
		int bamOffset = getSectorOffset(BAM_TRACK, BAM_SECTOR);
		bam.setDiskName(Utility.EMPTY);
		bam.setDiskId(Utility.EMPTY);
		bam.setDiskDosType( (byte) getCbmDiskValue(bamOffset + 2 ));
		for (byte track = 1; track < D67Constants.D67_TRACKS.length; track++) {
			bam.setFreeSectors(track, (byte) getCbmDiskValue(bamOffset + 4 + (track-1) * 4));
			for (int i = 1; i < 4; i++) {
				bam.setTrackBits(track, i, (byte) getCbmDiskValue(bamOffset + 4 + (track-1) * 4 + i));
			}
		}
		bam.setDiskName(Utility.getString(cbmDisk, bamOffset + 144, DISK_NAME_LENGTH));
		bam.setDiskId(Utility.getString(cbmDisk, bamOffset + 162, DISK_ID_LENGTH));
		checkImageFormat();
	}

	/**
	 * Get track/sector from CP/M sector number.
	 * @param num  CP/M sector number
	 * @return TrackSector of specified CP/M sector
	 */
	private TrackSector getCpmTrackSector(int num) {
		int trk = 0;
		int sec = 0;
		if (num >= CPM_BLOCK_COUNT) {
			return null;
		} else if (imageFormat == D64_CPM_C64_IMAGE_TYPE) {
			if (num >= 544) {
				return null;
			}
			trk = num / 17 + 3;
			if (trk >= 18) {
				trk++;
			}
			sec = num % 17;
			return new TrackSector(trk, sec);
		} else if (imageFormat == D64_CPM_C128_IMAGE_TYPE) {
			int n = num;
			for (int i=0; i<4; i++) {
				n += D67Constants.CPM_ZONES[i][3];
				if (n < D67Constants.CPM_ZONES[i][2]) {
					trk = D67Constants.CPM_ZONES[i][0] + n / D67Constants.CPM_ZONES[i][1];
					sec = (CPM_SECTOR_SKEW * n) % D67Constants.CPM_ZONES[i][1];
					return new TrackSector(trk, sec);
				}
				n -= D67Constants.CPM_ZONES[i][2];
			}
			return new TrackSector(trk, sec);
		} else {
			return null;
		}
	}

	private void readCpmDirectory() {
		if (imageFormat == D64_CPM_C128_IMAGE_TYPE) {
			readCpmDirectory(D67Constants.C128_SS_DIR_TRACK, D67Constants.C128_SS_DIR_SECTORS, false);
		} else if (imageFormat == D64_CPM_C64_IMAGE_TYPE) {
			readCpmDirectory(D67Constants.C64_SS_DIR_TRACK, D67Constants.C64_SS_DIR_SECTORS, false);
		}
	}

	@Override
	public int getSectorOffset(int track, int sector) {
		return D67Constants.D67_TRACKS[track].getOffset() + (BLOCK_SIZE * sector);
	}

	@Override
	public void readDirectory() {
		if (isCpmImage()) {
			readCpmDirectory();
			return;
		}
		int track = DIR_TRACK;
		int sector = 1;
		try {
			boolean fileLimitReached = false;
			int dirPosition = 0;
			int filenumber = 0;
			do {
				if (track >= D67Constants.D67_TRACKS.length || sector >=  getMaxSectors(track)) {
					feedbackMessage.append("Error: Sector ").append(track).append("/").append(sector).append(" is not within image.\n");
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
		} catch (ArrayIndexOutOfBoundsException e) {	//NOSONAR
			feedbackMessage.append("Error: Sector ").append(track).append("/").append(sector).append(" is outside of image.\n");
		}
		validate(new ArrayList<Integer>());
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
			track = getCbmDiskValue( dataPosition + 0);
			sector = getCbmDiskValue( dataPosition + 1);
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
						for (int c=0; c<BLOCK_SIZE && dstPos < data.length; c++) {
							data[dstPos++] = cbmDisk[srcPos + c];
						}
					}
				}
				return data;
			} else {
				throw new CbmException("CP/M format but not a CP/M file.\n");
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
			if (thisTrack >= D67Constants.D67_TRACKS.length) {
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
				block.track = DIR_TRACK - distance;
				if (block.track >= FIRST_TRACK && block.track <= TRACK_COUNT) {
					// Track within disk limits
					found = isTrackFree(block.track);
				}
				if (!found){
					// Check the track above the directory track
					block.track = DIR_TRACK + distance;
					if (block.track <= TRACK_COUNT) {
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
				int maxSector = D67Constants.D67_TRACKS[block.track].getSectors();	// Determine how many sectors there are on that track.
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
	 * Find a sector for the next block of the file, using variables Track and Sector<BR>
	 * @param block
	 * @return sector which was found or null if no more sectors left
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
					block.sector = block.sector + C1541_INTERLEAVE;	// Move away an "interleave" number of sectors.
					if (geosFormat && block.track >= 25) {
						// Empirical GEOS optimization, get one sector backwards if over track 25.
						block.sector--;
					}
				} else {
					// For a different track of a GEOS-formatted disk, use sector skew.
					block.sector = (block.track - curTrack) << 1 + 4 + C1541_INTERLEAVE;
				}
				int maxSector = D67Constants.D67_TRACKS[block.track].getSectors();	// Determine how many sectors there are on track.
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
					if (block.track > FIRST_TRACK && block.track <= DIR_TRACK) {
						block.track = block.track - 1 ;
					} else if (block.track < TRACK_COUNT && block.track > DIR_TRACK) {
						block.track = block.track + 1 ;
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
					} else {	//if track >= DirTrack then.
						block.track++;	//If we're above the directory track then move one track upwards.
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
	 * Mark a sector in BAM as used.
	 * @param track trackNumber
	 * @param sector sectorNumber
	 */
	@Override
	public void markSectorUsed(int track, int sector) {
		int trackPos = getSectorOffset(BAM_TRACK, BAM_SECTOR) + track * 4;
		int pos = (sector / 8) + 1;
		setCbmDiskValue(trackPos + pos, getCbmDiskValue(trackPos + pos) & INVERTED_BYTE_BIT_MASKS[sector & 0x07] );
		setCbmDiskValue(trackPos, getCbmDiskValue(trackPos) - 1);
	}

	/**
	 * Mark a sector in BAM as free.
	 * @param track trackNumber
	 * @param sector sectorNumber
	 */
	@Override
	public void markSectorFree(int track, int sector) {
		int trackPos = getSectorOffset(BAM_TRACK, BAM_SECTOR) + track * 4;
		int pos = (sector / 8) + 1;
		setCbmDiskValue(trackPos + pos, getCbmDiskValue(trackPos + pos) | BYTE_BIT_MASKS[sector & 0x07] );
		setCbmDiskValue(trackPos, getCbmDiskValue(trackPos) + 1);
	}

	/**
	 * Determine if a sector is free.
	 * @param track the track number of sector to check
	 * @param sector the sector number of sector to check
	 * @return when True, the sector is free; otherwise used
	 */
	@Override
	public boolean isSectorFree(int track, int sector) {
		int trackPos = getSectorOffset(BAM_TRACK, BAM_SECTOR) + track * 4;
		int pos = (sector / 8) + 1;
		int value =  getCbmDiskValue(trackPos + pos) & BYTE_BIT_MASKS[sector & 0x07];
		return value != 0;
	}

	@Override
	protected void setDiskName(String newDiskName, String newDiskID){
		feedbackMessage.append("setDiskName: '").append(newDiskName).append("', '").append(newDiskID).append("'\n");
		Utility.setPaddedString(cbmDisk, getSectorOffset(BAM_TRACK, BAM_SECTOR) + 144, newDiskName, DISK_NAME_LENGTH);
		Utility.setPaddedString(cbmDisk, getSectorOffset(BAM_TRACK, BAM_SECTOR) + 162, newDiskID, DISK_ID_LENGTH);
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
		}
		//find the correct entry where to write new values for dirTrack and dirSector
		int thisTrack = DIR_TRACK;
		int thisSector = 1;
		int entryPosCount = 8;
		while (dirEntryNumber >= entryPosCount) {
			int nextTrack = getCbmDiskValue(thisTrack, thisSector, 0x00);
			int nextSector = getCbmDiskValue(thisTrack, thisSector, 0x01);
			if (nextTrack == 0) {
				nextTrack = thisTrack;
				boolean found = false;
				for (int i=0; !found && i<D67Constants.DIR_SECTORS.length; i++ ) {
					nextSector = D67Constants.DIR_SECTORS[i];
					found = isSectorFree(nextTrack, nextSector);
				}
				if (found) {
					nextTrack = thisTrack;
					allocSector(thisTrack, thisSector, nextTrack, nextSector);
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

	private void allocSector(int thisTrack, int thisSector, int nextTrack, int nextSector) {
		markSectorUsed(nextTrack, nextSector);
		setCbmDiskValue(thisTrack, thisSector, 0x00, nextTrack);
		setCbmDiskValue(thisTrack, thisSector, 0x01, nextSector);
		setCbmDiskValue(nextTrack, nextSector, 0x00, 0);
		setCbmDiskValue(nextTrack, nextSector, 0x01, -1);
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
			int entryPos = getSectorOffset(track, sector);
			track = getCbmDiskValue(entryPos + 0x00);
			sector = getCbmDiskValue(entryPos + 0x01);
			entryPosCount += 8;
		}
		return track == 0 ? -1 : getSectorOffset(track, sector) + (dirEntryNumber & 0x07) * DIR_ENTRY_SIZE;
	}

	@Override
	protected void writeDirectoryEntry(CbmFile cbmFile, int dirEntryNumber) {
		int num = dirEntryNumber;
		int thisTrack = DIR_TRACK;
		int thisSector = 1;
		feedbackMessage.append("writeDirectoryEntry: cbmFile to dirEntryNumber ").append(num).append(".\n");
		if (num > 7) {
			while (num > 7) {
				int entryPos = getSectorOffset(thisTrack, thisSector);
				thisTrack  = getCbmDiskValue(entryPos + 0);
				thisSector = getCbmDiskValue(entryPos + 1);
				feedbackMessage.append("LongDirectory: "+num+" dirEntrys remain, next: Track "+thisTrack+", Sector "+thisSector+"\n");
				num = num - 8;
			}
		}
		int pos = getSectorOffset(thisTrack, thisSector) + num * DIR_ENTRY_SIZE;
		setCbmDiskValue(pos + 0, cbmFile.getDirTrack());
		setCbmDiskValue(pos + 1, cbmFile.getDirSector());
		writeSingleDirectoryEntry(cbmFile, pos);
	}

	/**
	 * Copy attributes of cbmFile to a location in cbmDisk.
	 * @param cbmDisk
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
	 * Writes a new D67 file.
	 * @param filename	the filename
	 * @param newDiskName	the new name (label) of the disk
	 * @param newDiskID	the new disk-ID
	 * @return <code>true</code> when writing of the D67 file was successful
	 */
	@Override
	public boolean saveNewImage(String filename, String newDiskName, String newDiskID){
		cbmDisk = new byte[D67_SIZE];
		Arrays.fill(cbmDisk, (byte) 0);
		if (!isCpmImage()) {
			Utility.copyBytes(D67Constants.NEWD67DATA, cbmDisk, 0x00000, 91392, D67Constants.NEWD67DATA.length);
			setDiskName(Utility.cbmFileName(newDiskName, DISK_NAME_LENGTH), Utility.cbmFileName(newDiskID, DISK_NAME_LENGTH));
			return writeImage(filename);
		} else if (imageFormat == D64_CPM_C128_IMAGE_TYPE) {
			for (int s=0; s<D67Constants.C128_SS_DIR_SECTORS.length; s++) {
				int offset = getSectorOffset(D67Constants.C128_SS_DIR_TRACK, D67Constants.C128_SS_DIR_SECTORS[s]);
				Arrays.fill(cbmDisk, offset + 2, offset + BLOCK_SIZE, UNUSED);
			}
			cbmDisk[0] = 'C';
			cbmDisk[1] = 'B';
			cbmDisk[2] = 'M';
			cbmDisk[255] = 0x00;
			setDiskName(CPM_DISKNAME_1, CPM_DISKID_GCR);
			return writeImage(filename);
		} else if (imageFormat == D64_CPM_C64_IMAGE_TYPE) {
			for (int s=0; s<D67Constants.C64_SS_DIR_SECTORS.length; s++) {
				int offset = getSectorOffset(D67Constants.C64_SS_DIR_TRACK, D67Constants.C64_SS_DIR_SECTORS[s]);
				Arrays.fill(cbmDisk, offset + 2, offset + BLOCK_SIZE, UNUSED);
			}
			cbmDisk[255] = 0x00;
			setDiskName(CPM_DISKNAME_1, CPM_DISKID_GCR);
			return writeImage(filename);
		} else {
			return false;
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
			int sec = 1;
			bamEntry[trk-1][0] = Integer.toString(trk);
			for (int cnt = 1; cnt < 4; cnt++) {
				for (int bit = 0; bit < 8; bit++) {
					if (sec <= getMaxSectors(trk)) {
						if ((getBam().getTrackBits(trk, cnt) & DiskImage.BYTE_BIT_MASKS[bit]) == 0) {
							bamEntry[trk-1][sec++] = CbmBam.USED;
						} else {
							bamEntry[trk-1][sec++] = CbmBam.FREE;
						}
					}
				}
			}
		}
		return bamEntry;
	}

	@Override
	public int getMaxSectors(int trackNumber) {
		return D67Constants.D67_TRACKS[trackNumber].getSectors();
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
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("D67[").append(" compressed=").append(compressed).append(" imageFormat=").append(imageFormat)
		.append(" blocksFree=").append(getBlocksFree()).append(" cbmFile=[");
		for (int i=0; cbmFile!=null && i<cbmFile.length && i<filesUsedCount; i++) {
			if (i > 0) {
				buf.append(", ");
			}
			buf.append(this.cbmFile[i]);
		}
		buf.append("]").append(" filesUsedCount=").append(filesUsedCount).append("]");
		return buf.toString();
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
		getValidationErrorList().clear();
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
		validateDirEntries(BAM_TRACK, BAM_SECTOR, bamEntry);
		// follow each file and check data blocks
		for (int n=0; n < cbmFile.length; n++) {
			if (cbmFile[n].getFileType() == CbmFile.TYPE_DEL) {
				continue;
			} else if (cbmFile[n].getFileType() == CbmFile.TYPE_CBM) {
				getValidationErrorList().add(new ValidationError(cbmFile[n].getTrack(), cbmFile[n].getSector(), ValidationError.ERROR_PARTITIONS_UNSUPPORTED, cbmFile[n].getName()));
				errors++;
			} else {
				if (cbmFile[n].getTrack() != 0) {
					validateFileData(cbmFile[n].getTrack(), cbmFile[n].getSector(), bamEntry, n);
				}
			}
		}
		// iterate BAM and verify used blocks is matching what we got when following data chains above.
		int bamOffset = getSectorOffset(BAM_TRACK, BAM_SECTOR);
		for (int trk = 1; trk <= getTrackCount(); trk++) {
			validateBam(trk, bamEntry, repairList, bamOffset) ;
		}
		return errors;
	}

	private void validateBam(int trk, Boolean[][] bamEntry, List<Integer> repairList, int bamOffset) {
		int freeSectors = 0;
		for (int sec = 0; sec < getMaxSectors(trk); sec++) {
			Boolean bamFree = Boolean.valueOf(isSectorFree(trk,sec));
			Boolean fileFree = bamEntry[trk][sec];
			if (bamFree) {
				freeSectors++;
			}
			if (fileFree == null && bamFree || bamFree.equals(fileFree)) {
				// OK
			} else if (Boolean.FALSE.equals(fileFree) && !Boolean.FALSE.equals(bamFree)) {
				if (repairList.contains(Integer.valueOf(ValidationError.ERROR_USED_SECTOR_IS_FREE))) {
					markSectorUsed(trk, sec);
					feedbackMessage.append("Info: marked sector ").append(trk).append("/").append(sec).append(" as used.\n");
				} else {
					getValidationErrorList().add(new ValidationError(trk,sec, ValidationError.ERROR_USED_SECTOR_IS_FREE));
					errors++;
				}
			} else if (trk != BAM_TRACK){
				if (repairList.contains(Integer.valueOf(ValidationError.ERROR_UNUSED_SECTOR_IS_ALLOCATED))) {
					markSectorFree(trk, sec);
					feedbackMessage.append("Info: marked sector ").append(trk).append("/").append(sec).append(" as free.\n");
				} else {
					getValidationErrorList().add(new ValidationError(trk,sec, ValidationError.ERROR_UNUSED_SECTOR_IS_ALLOCATED));
					warnings++;
				}
			}
		}
		int bamFreeSectors = getCbmDiskValue(bamOffset + trk * 4);
		if (freeSectors != bamFreeSectors) {
			if (repairList.contains(Integer.valueOf(ValidationError.ERROR_BAM_FREE_SECTOR_MISMATCH))) {
				setCbmDiskValue(bamOffset + trk * 4, freeSectors);
				feedbackMessage.append("Info: corrected free founter on track ").append(trk).append(". New free count is ").append(freeSectors).append(".\n");
			} else {
				getValidationErrorList().add(new ValidationError(trk, 0, ValidationError.ERROR_BAM_FREE_SECTOR_MISMATCH));
				errors++;
			}
		}
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
			}
			if (bamEntry[track][sector] == null) {
				bamEntry[track][sector] = Boolean.FALSE;	// OK file's sector marked as used
			} else {
				errors++;
				TrackSector thisBlock = new TrackSector(track, sector);
				if (fileErrorList.contains(thisBlock)) {
					getValidationErrorList().add(new ValidationError(track, sector, ValidationError.ERROR_FILE_SECTOR_ALREADY_SEEN, cbmFile[fileNum].getName()));
					return;
				} else {
					fileErrorList.add(thisBlock);
				}
				if (bamEntry[track][sector].equals(Boolean.FALSE)) {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_FILE_SECTOR_ALREADY_USED, cbmFile[fileNum].getName()));
				} else {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_FILE_SECTOR_ALREADY_FREE, cbmFile[fileNum].getName()));
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
				bamEntry[track][sector] = Boolean.FALSE;	// OK
			} else {
				// Detect cyclic references by keeping track of all sectors used by one file and check if a sector is already seen.
				TrackSector thisBlock = new TrackSector(track, sector);
				if (dirErrorList.contains(thisBlock)) {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_ALREADY_SEEN));
					errors++;
					return;
				} else {
					dirErrorList.add(thisBlock);
				}
				if (bamEntry[track][sector].equals(Boolean.FALSE)) {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_ALREADY_USED));
					errors++;
				} else {
					getValidationErrorList().add(new ValidationError(track,sector, ValidationError.ERROR_DIR_SECTOR_ALREADY_FREE));
					errors++;
				}
			}
			int tmpTrack = track;
			int tmpSector = sector;
			track = getCbmDiskValue(getSectorOffset(tmpTrack, tmpSector) + 0x00);
			sector = getCbmDiskValue(getSectorOffset(tmpTrack, tmpSector) + 0x01);
		} while (track != 0);
	}

	@Override
	public void readPartition(int track, int sector, int numBlocks) throws CbmException {
		throw new CbmException("D67 images does not support partitions.");
	}

}

