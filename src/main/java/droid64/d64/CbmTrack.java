package droid64.d64;
/**<pre style='font-family:sans-serif;'>
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
 *</pre>
 * @author wolf
 */
public class CbmTrack {

	private int sectors;
	private int sectorsIn;
	private int offset;

	public CbmTrack(int sectors, int sectorsIn, int offset){
		this.sectors = sectors;
		this.sectorsIn = sectorsIn;
		this.offset = offset;
	}

	/**
	 * @return offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return sectors
	 */
	public int getSectors() {
		return sectors;
	}

	/**
	 * @return sectors in
	 */
	public int getSectorsIn() {
		return sectorsIn;
	}

	/**
	 * @param d offset
	 */
	public void setOffset(int d) {
		offset = d;
	}

	/**
	 * @param b sectors
	 */
	public void setSectors(int b) {
		sectors = b;
	}

	/**
	 * @param b sectors in
	 */
	public void setSectorsIn(int b) {
		sectorsIn = b;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CbmTrack[");
		builder.append(" sectors=").append(sectors);
		builder.append(" sectorsIn=").append(sectorsIn);
		builder.append(" offset=").append(offset);
		builder.append("]");
		return builder.toString();
	}
}
