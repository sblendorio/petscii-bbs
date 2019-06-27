package droid64.db;

/**
 * Class which is inherited by persistent value object.
 *
 * @author Henrik
 */
public class Value {

	private enum mode_t {
		CLEAN, INSERT, UPDATE, DELETE
	}

	private mode_t mode = mode_t.INSERT;

	/**
	 * Instance has no unsaved changes.
	 * @return true when not marked as changed
	 */
	public boolean isClean() {
		return mode == mode_t.CLEAN;
	}

	/**
	 * Instance is new and does not yet exist.
	 * @return true when marked as new entry
	 * */
	public boolean isInsert() {
		return mode == mode_t.INSERT;
	}

	/**
	 * Instance will be deleted on save.
	 * @return true when marked for deletion
	 */
	public boolean isDelete() {
		return mode == mode_t.DELETE;
	}

	/**
	 * Instance exists and has changes.
	 * @return true when marked for updating existing row
	 */
	public boolean isUpdate() {
		return mode == mode_t.UPDATE;
	}

	public void setClean() {
		mode = mode_t.CLEAN;
	}

	public void setInsert() {
		mode = mode_t.INSERT;
	}

	public void setDelete() {
		mode = mode_t.DELETE;
	}

	public void setUpdate() {
		mode = mode_t.UPDATE;
	}

	public String getState() {
		switch (mode) {
		case CLEAN:
			return "clean";
		case INSERT:
			return "insert";
		case DELETE:
			return "delete";
		case UPDATE:
			return "update";
		default:
			return "unknown";
		}
	}

}
