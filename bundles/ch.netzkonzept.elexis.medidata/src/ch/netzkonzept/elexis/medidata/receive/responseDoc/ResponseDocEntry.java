package ch.netzkonzept.elexis.medidata.receive.responseDoc;

public class ResponseDocEntry {

	private String created;
	private String filename;
	private String path;

	public String get(int columnNumber) {
		String returnString = new String();
		switch (columnNumber) {
		case 0:
			returnString = getCreated();
			break;
		case 1:
			returnString = getFilename();
			break;
		case 2:
			returnString = getPath();
			break;
		}
		return returnString;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
