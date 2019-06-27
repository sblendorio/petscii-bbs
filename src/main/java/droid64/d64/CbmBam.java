package droid64.d64;

import java.io.Serializable;
import java.util.Arrays;
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
 *</pre>
 * @author wolf
 */
public class CbmBam implements Serializable {

	private static final long serialVersionUID = 1L;
	private int diskDosType;
	private int[] freeSectors = null;
	private int[][] trackBits = null;
	private String diskName;	//string[16]
	private String diskId;	//string[5]

	public static final String USED = "x";
	public static final String FREE = "-";
	public static final String INVALID = Utility.SPACE;
	public static final String RESERVED = "X";

	public CbmBam(int numTracks, int numTrackBytes) {
		freeSectors = new int[numTracks];
		trackBits =  new int[numTracks][numTrackBytes - 1];
	}

	public CbmBam(int diskDosType, int[] freeSectors, int[][] trackBits, String diskName, String diskId) {
		this.diskDosType = diskDosType;
		this.freeSectors = freeSectors;
		this.trackBits = trackBits;
		this.diskName = diskName;
		this.diskId = diskId;
	}

	/**
	 * @return disk dos type
	 */
	public int getDiskDosType() {
		return diskDosType;
	}

	/**
	 * @return disk id
	 */
	public String getDiskId() {
		return diskId;
	}

	/**
	 * @return disk name
	 */
	public String getDiskName() {
		return diskName;
	}

	/**
	 * @param trackNumber the track number (1..LastTrack)
	 * @return number of free sectors
	 */
	public int getFreeSectors(int trackNumber) {
		return freeSectors[trackNumber-1];
	}

	/**
	 * @param trackNumber the track number (1..LastTrack)
	 * @param byteNumber the byte number (1..3)
	 * @return track bits
	 */
	public int getTrackBits(int trackNumber, int byteNumber) {
		return trackBits[trackNumber-1][byteNumber-1];
	}

	/**
	 * @param b disk dos type
	 */
	public void setDiskDosType(int b) {
		diskDosType = b;
	}

	/**
	 * @param string disk id
	 */
	public void setDiskId(String string) {
		diskId = string;
	}

	/**
	 * @param string disk name
	 */
	public void setDiskName(String string) {
		diskName = string;
	}

	/**
	 * @param track the track, starting at 1 until last track number.
	 * @param value value
	 */
	public void setFreeSectors(int track, int value) {
		freeSectors[track-1] = value;
	}

	/**
	 * @param track track which is a number between 1 and last track.
	 * @param byteNum a number starting 1 and is the number of bytes per track.
	 * @param value value
	 */
	public void setTrackBits(int track, int byteNum, int value) {
		trackBits[track-1][byteNum-1] = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CbmBam [");
		builder.append(" diskDosType=").append(diskDosType);
		builder.append(" freeSectors=").append(Arrays.toString(freeSectors));
		builder.append(" trackBits=").append(Arrays.toString(trackBits));
		builder.append(" diskName=").append(diskName);
		builder.append(" diskId=").append(diskId);
		builder.append("]");
		return builder.toString();
	}
}
