package droid64.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Persistent value class for representing one disk image.
 * @author Henrik
 */
/**
 * @author henke
 *
 */
public class Disk extends Value {

	private long diskId;
	private String label;
	private String filePath;
	private String fileName;
	private List<DiskFile> fileList = null;
	private Date updated;
	private int imageType;
	private Integer errors;
	private Integer warnings;
	private String hostName;

	public void setDiskId(long id) {
		this.diskId = id;
	}

	public long getDiskId() {
		return diskId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<DiskFile> getFileList() {
		if (fileList == null) {
			fileList = new ArrayList<>();
		}
		return fileList;
	}

	public void setFileList(List<DiskFile> fileList) {
		this.fileList = fileList;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}


	public Integer getErrors() {
		return errors;
	}

	public void setErrors(Integer errors) {
		this.errors = errors;
	}

	public Integer getWarnings() {
		return warnings;
	}

	public void setWarnings(Integer warnings) {
		this.warnings = warnings;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Disk[");
		builder.append(" .diskId=").append(diskId);
		builder.append(" .label=").append(label);
		builder.append(" .filePath=").append(filePath);
		builder.append(" .fileName=").append(fileName);
		builder.append(" .updated=").append(updated);
		builder.append(" .imageType=").append(imageType);
		builder.append(" .errors=").append(errors);
		builder.append(" .warnings=").append(warnings);
		builder.append(" .fileList=").append(fileList);
		builder.append(" .hostName=").append(hostName);
		builder.append(" .state=").append(getState());
		builder.append("]");
		return builder.toString();
	}

}
