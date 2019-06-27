package droid64.d64;

import java.io.Serializable;
import java.util.ArrayList;
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
public class CpmFile extends CbmFile implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Integer> allocList = null;	//NOSONAR
	private int lastRecordByteCount = 0;
	private int recordCount = 0;
	private boolean hidden = false;
	private boolean readOnly = false;
	private boolean archived = false;
	private int lastExtNum = 0;

	private String cpmName;
	private String cpmNameExt;

	public CpmFile() {
		// needed to match CbmFile
	}

	public CpmFile(CpmFile that) {
		super(that);
		this.recordCount = that.recordCount;
		this.hidden = that.hidden;
		this.readOnly = that.readOnly;
		this.archived = that.archived;
		this.lastExtNum = that.lastExtNum;
		this.cpmName = that.cpmName;
		this.cpmNameExt = that.cpmNameExt;
		if (that.allocList != null) {
			this.allocList = new ArrayList<>(that.allocList);
		}
	}

	public void addAllocUnit(int unit) {
		if (allocList == null) {
			allocList = new ArrayList<>();
		}
		allocList.add(unit);
	}

	public List<Integer> getAllocList() {
		if (allocList == null) {
			allocList = new ArrayList<>();
		}
		return allocList;
	}

	public String getCpmNameAndExt() {
		if (cpmNameExt==null || cpmNameExt.isEmpty()) {
			return cpmName != null ? cpmName.trim().toLowerCase() : getName();
		} else {
			return cpmName != null ? cpmName.trim().toLowerCase() + "." + cpmNameExt.trim().toLowerCase() : getName();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		CpmFile other = (CpmFile) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (cpmName == other.cpmName) {
			return true;
		} else if (!getName().equals(other.getName()) || cpmName == null || !cpmName.equals(other.cpmName) || !cpmNameExt.equals(other.cpmNameExt)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ((allocList == null) ? 0 : allocList.hashCode());
		result = 31 * result + (archived ? 1231 : 1237);
		result = 31 * result + ((cpmName == null) ? 0 : cpmName.hashCode());
		result = 31 * result + ((cpmNameExt == null) ? 0 : cpmNameExt.hashCode());
		result = 31 * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	public void setLastRecordByteCount(int count) {
		lastRecordByteCount = count;
	}
	public int setLastRecordByteCount() {
		return lastRecordByteCount;
	}

	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getLastExtNum() {
		return lastExtNum;
	}

	public void setLastExtNum(int lastExtNum) {
		this.lastExtNum = lastExtNum;
	}

	public String getCpmName() {
		return cpmName;
	}

	public void setCpmName(String name) {
		this.cpmName = name;
	}

	public String getCpmNameExt() {
		return cpmNameExt;
	}

	public void setCpmNameExt(String nameExt) {
		this.cpmNameExt = nameExt;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public String asDirString() {
		return String.format("%-5s %-8s %-3s %-3s",
				recordCount, cpmName, cpmNameExt,
				(readOnly ? "R" : "-") + (hidden ? "H" : "-") + (archived ? "A" : "-"));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CpmFile[");
		super.toString(builder);
		builder.append(" lastRecordByteCount=").append(lastRecordByteCount);
		builder.append(" recordCount=").append(recordCount);
		builder.append(" hidden=").append(hidden);
		builder.append(" allocList=").append(allocList);
		builder.append("]");
		return builder.toString();
	}

}
