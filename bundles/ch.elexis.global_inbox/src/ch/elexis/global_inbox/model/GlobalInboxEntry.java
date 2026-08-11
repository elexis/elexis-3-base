package ch.elexis.global_inbox.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;

public class GlobalInboxEntry {

	private String title;
	private String mimetype;
	private IPatient patient;
	private IContact sender;
	private String category;
	private String keywords;
	private Date creationDate;
	private Date archivingDate;

	private boolean sendInfoTo;
	private List<IMandator> infoTo;

	private List<IPatient> patientCandidates;
	private List<IContact> senderCandidates;
	private List<LocalDate> dateTokens;
	private LocalDate creationDateCandidate;

	/**
	 * The main file bound for import
	 */
	private final IVirtualFilesystemHandle mainFile;
	/**
	 * Files that provide supportive information on mainfile, they always start with
	 * the same name as the mainFile, extending it with another extension e.g. orig
	 * file: scan.pdf, ext file: scan.pdf.edam.xml
	 */
	private final IVirtualFilesystemHandle[] extensionFiles;

	public GlobalInboxEntry(IVirtualFilesystemHandle mainFile, IVirtualFilesystemHandle[] extensionFiles) {
		this.mainFile = mainFile;
		this.title = mainFile.getName();
		this.extensionFiles = extensionFiles;
		patientCandidates = new ArrayList<IPatient>();
		senderCandidates = new ArrayList<IContact>();
		dateTokens = new ArrayList<LocalDate>();
		infoTo = new ArrayList<IMandator>();
	}

	public IVirtualFilesystemHandle getMainFile() {
		return mainFile;
	}

	public IVirtualFilesystemHandle[] getExtensionFiles() {
		return extensionFiles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getMimetype() {
		return mimetype;
	}

	public IPatient getPatient() {
		return patient;
	}

	public void setPatient(IPatient patient) {
		this.patient = patient;
	}

	public IContact getSender() {
		return sender;
	}

	public void setSender(IContact sender) {
		this.sender = sender;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setArchivingDate(Date archivingDate) {
		this.archivingDate = archivingDate;
	}

	public Date getArchivingDate() {
		return archivingDate;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * The category this belongs to
	 *
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isSendInfoTo() {
		return sendInfoTo;
	}

	public void setSendInfoTo(boolean sendInfoTo) {
		this.sendInfoTo = sendInfoTo;
	}

	public List<IMandator> getInfoTo() {
		return infoTo;
	}

	public void setInfoTo(List<IMandator> infoTo) {
		this.infoTo = infoTo;
	}

	/**
	 *
	 * @return an ordered list of potential patients this GlobalInboxEntry belongs
	 *         to with probability decreasing on next(). Empty if no proposals
	 *         available. Final selection is available via {@link #getPatientId()}
	 */
	public List<IPatient> getPatientCandidates() {
		return patientCandidates;
	}

	/**
	 *
	 * @return an ordered list of potential senders this GlobalInboxEntry originates
	 *         from with probability decreasing on next(). Empty if no proposals
	 *         available. Final selection is available via {@link #getSenderId()}
	 */
	public List<IContact> getSenderCandidates() {
		return senderCandidates;
	}

	public List<LocalDate> getDateTokens() {
		return dateTokens;
	}

	public void setPatientCandidates(List<IPatient> patientCandidates) {
		this.patientCandidates = patientCandidates;
	}

	public void setDateTokens(List<LocalDate> dateTokens) {
		this.dateTokens = dateTokens;
	}

	public void setSenderCandidates(List<IContact> senderCandidates) {
		this.senderCandidates = senderCandidates;
	}

	public void setCreationDateCandidate(LocalDate creationDateCandidate) {
		this.creationDateCandidate = creationDateCandidate;
	}

	public LocalDate getCreationDateCandidate() {
		return creationDateCandidate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(mainFile.getAbsolutePath());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlobalInboxEntry other = (GlobalInboxEntry) obj;
		return Objects.equals(mainFile.getAbsolutePath(), other.mainFile.getAbsolutePath());
	}

	/**
	 * @return if there is an extension file that ends with .preview.pdf it will be
	 *         returned, else the main-file is returned
	 */
	public IVirtualFilesystemHandle getPdfPreviewFile() {
		for (IVirtualFilesystemHandle extFile : extensionFiles) {
			if (extFile.getName().endsWith(".preview.pdf")) { //$NON-NLS-1$
				return extFile;
			}
		}
		return getMainFile();
	}

}
