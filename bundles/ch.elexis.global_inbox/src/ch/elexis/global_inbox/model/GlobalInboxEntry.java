package ch.elexis.global_inbox.model;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public class GlobalInboxEntry {
	
	private String title;
	private IPatient patient;
	private String senderId;
	private String category;
	private String keywords;
	private Date creationDate;
	private Date archivingDate;
	
	private boolean sendInfoTo;
	private List<IMandator> infoTo;
	
	private List<IPatient> patientCandidates;
	private List<IContact> senderCandidates;
	private List<LocalDate> creationDateCandidates;
	
	/**
	 * The main file bound for import
	 */
	private final File mainFile;
	/**
	 * Files that provide supportive information on mainfile, they always start with the same name
	 * as the mainFile, extending it with another extension e.g. orig file: scan.pdf, ext file:
	 * scan.pdf.edam.xml
	 */
	private final File[] extensionFiles;
	
	public GlobalInboxEntry(File mainFile, File[] extensionFiles){
		this.mainFile = mainFile;
		this.title = mainFile.getName();
		this.extensionFiles = extensionFiles;
		patientCandidates = new ArrayList<IPatient>();
		senderCandidates = new ArrayList<IContact>();
		creationDateCandidates = new ArrayList<LocalDate>();
	}
	
	public File getMainFile(){
		return mainFile;
	}
	
	public File[] getExtensionFiles(){
		return extensionFiles;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public IPatient getPatient(){
		return patient;
	}
	
	public void setPatient(IPatient patient){
		this.patient = patient;
	}
	
	
	/**
	 * The {@link IContact#getId()} of the sender this document originated from
	 * 
	 * @return
	 */
	public String getSenderId(){
		return senderId;
	}
	
	public void setSenderId(String senderId){
		this.senderId = senderId;
	}
	
	public void setCreationDate(Date creationDate){
		this.creationDate = creationDate;
	}

	public Date getCreationDate(){
		return creationDate;
	}
	
	public void setArchivingDate(Date archivingDate){
		this.archivingDate = archivingDate;
	}
	
	public Date getArchivingDate(){
		return archivingDate;
	}
	
	public String getKeywords(){
		return keywords;
	}
	
	public void setKeywords(String keywords){
		this.keywords = keywords;
	}
	
	/**
	 * The category this belongs to
	 * 
	 * @return
	 */
	public String getCategory(){
		return category;
	}
	
	public void setCategory(String category){
		this.category = category;
	}
	
	public boolean isSendInfoTo(){
		return sendInfoTo;
	}
	
	public void setSendInfoTo(boolean sendInfoTo){
		this.sendInfoTo = sendInfoTo;
	}
	
	public List<IMandator> getInfoTo(){
		return infoTo;
	}
	
	public void setInfoTo(List<IMandator> infoTo){
		this.infoTo = infoTo;
	}
	
	/**
	 * 
	 * @return an ordered list of potential patients this GlobalInboxEntry belongs to with
	 *         probability decreasing on next(). Empty if no proposals available. Final selection is
	 *         available via {@link #getPatientId()}
	 */
	public List<IPatient> getPatientCandidates(){
		return patientCandidates;
	}
	
	/**
	 * 
	 * @return an ordered list of potential senders this GlobalInboxEntry originates from with
	 *         probability decreasing on next(). Empty if no proposals available. Final selection is
	 *         available via {@link #getSenderId()}
	 */
	public List<IContact> getSenderCandidates(){
		return senderCandidates;
	}
	
	public List<LocalDate> getCreationDateCandidates(){
		return creationDateCandidates;
	}
	
	public void setPatientCandidates(List<IPatient> patientCandidates){
		this.patientCandidates = patientCandidates;
	}
	
	public void setCreationDateCandidates(List<LocalDate> creationDateCandidates){
		this.creationDateCandidates = creationDateCandidates;
	}
	
	public void setSenderCandidates(List<IContact> senderCandidates){
		this.senderCandidates = senderCandidates;
	}
	
}
