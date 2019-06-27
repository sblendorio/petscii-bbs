package droid64.d64;

public class TrackSector {

	int track;
	int sector;

	public TrackSector(int track, int sector) {
		this.track = track;
		this.sector = sector;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public int getSector() {
		return sector;
	}

	public void setSector(int sector) {
		this.sector = sector;
	}

	@Override
	public String toString() {
		return "[" + track + ":" + sector + "]";
	}

	public void toString(StringBuilder buf) {
		buf.append("[").append(track).append("/").append(sector).append("]");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null || !(obj instanceof TrackSector)) {
			return false;
		} else {
			TrackSector other = (TrackSector) obj;
			return this.track == other.track && this.sector == other.sector;
		}
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + sector;
		result = 31 * result + track;
		return result;
	}

}
