package ch.netzkonzept.elexis.medidata.receive;

public class MessageLogEntry {
	
	private String id;
	private LocalisedString subject;
	private LocalisedString message;
	private String severity;
	private boolean read;
	private String created;
	private String template;
	private String mode;
	private String errorCode;
	private LocalisedString potentialReasons;
	private LocalisedString possibleSolutions;
	private String technicalInformation;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LocalisedString getSubject() {
		return subject;
	}
	public void setSubject(LocalisedString subject) {
		this.subject = subject;
	}
	public LocalisedString getMessage() {
		return message;
	}
	public void setMessage(LocalisedString message) {
		this.message = message;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public LocalisedString getPotentialReasons() {
		return potentialReasons;
	}
	public void setPotentialReasons(LocalisedString potentialReasons) {
		this.potentialReasons = potentialReasons;
	}
	public LocalisedString getPossibleSolutions() {
		return possibleSolutions;
	}
	public void setPossibleSolutions(LocalisedString possibleSolutions) {
		this.possibleSolutions = possibleSolutions;
	}
	public String getTechnicalInformation() {
		return technicalInformation;
	}
	public void setTechnicalInformation(String technicalInformation) {
		this.technicalInformation = technicalInformation;
	}	
	
}

class LocalisedString {
	
	private String de;
	private String fr;
	private String it;
	public String getDe() {
		return de;
	}
	public void setDe(String de) {
		this.de = de;
	}
	public String getFr() {
		return fr;
	}
	public void setFr(String fr) {
		this.fr = fr;
	}
	public String getIt() {
		return it;
	}
	public void setIt(String it) {
		this.it = it;
	}
}