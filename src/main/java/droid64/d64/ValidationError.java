package droid64.d64;

import java.io.Serializable;

public class ValidationError implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 1. Too many errors encountered. Giving up. */
	public static final  int ERROR_TOO_MANY = 1;
	/** 2. Referred directory sector outside of image. */
	public static final  int ERROR_DIR_SECTOR_OUTSIDE_IMAGE = 2;
	/** 3. Directory sector was already seen (cyclic reference detected). */
	public static final  int ERROR_DIR_SECTOR_ALREADY_SEEN = 3;
	/** 4. Directory sector already seen and marked as used. */
	public static final  int ERROR_DIR_SECTOR_ALREADY_USED = 4;
	/** 5. Directory sector already seen and marked as free. */
	public static final  int ERROR_DIR_SECTOR_ALREADY_FREE = 5;
	/** 6. Partitions are only supported on D81 images. */
	public static final  int ERROR_PARTITIONS_UNSUPPORTED = 6;
	/** 7. Referred file sector outside of image. */
	public static final  int ERROR_FILE_SECTOR_OUTSIDE_IMAGE = 7;
	/** 8. File sector already seen (cyclic reference detected). */
	public static final  int ERROR_FILE_SECTOR_ALREADY_SEEN = 8;
	/** 9. File sector already seen and marked as used. */
	public static final  int ERROR_FILE_SECTOR_ALREADY_USED = 9;
	/** 10. File sector already seen and marked as free. */
	public static final  int ERROR_FILE_SECTOR_ALREADY_FREE = 10;
	/** 11. Used sector is marked as free. */
	public static final  int ERROR_USED_SECTOR_IS_FREE = 11;
	/** 12. Unused sector is marked as used. */
	public static final  int ERROR_UNUSED_SECTOR_IS_ALLOCATED = 12;
	/** 13. BAM free sector mismatch. */
	public static final  int ERROR_BAM_FREE_SECTOR_MISMATCH = 13;

	private static final String[] ERROR_TEXTS = {
			"Unknown error.",	// 0
			"Too many errors",	// 1
			"Directory sector outside of image", // 2
			"Cyclic directory sector references",	// 3
			"Directory sector already used",	// 4
			"Directory sector already free",	// 5
			"Partitions only supported on D81 images",	// 6
			"File sector outside of image", // 7
			"Cyclic file sector references",	// 8
			"File sector already used",	// 9
			"File sector already free",	// 10
			"Used sector is marked as free",	// 11
			"Unused sector is marked as used",	// 12
			"BAM free sector mismatch"	// 13
	};

	private int track;
	private int sector;
	private int errorCode;
	private String fileName;

	public ValidationError(int track, int sector, int errorCode) {
		this.track = track;
		this.sector = sector;
		this.errorCode = errorCode;
	}

	public ValidationError(int track, int sector, int errorCode, String fileName) {
		this.track = track;
		this.sector = sector;
		this.errorCode = errorCode;
		this.fileName = fileName;
	}

	public static String getErrorText(int errorCode) {
		return ERROR_TEXTS[errorCode < ERROR_TEXTS.length ? errorCode : 0];
	}

	public int getTrack() {
		return track;
	}

	public int getSector() {
		return sector;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("ValidationError[");
		buf.append(" .track=").append(track);
		buf.append(" .sector=").append(sector);
		buf.append(" .errorCode=").append(errorCode);
		buf.append(" .fileName=").append(fileName);
		buf.append(" .errorText=").append( ERROR_TEXTS[errorCode < ERROR_TEXTS.length ? errorCode : 0]);
		buf.append("]");
		return buf.toString();
	}

}
