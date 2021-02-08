package ch.elexis.omnivore.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jpa.entities.DocHandle;
import ch.elexis.core.jpa.entities.Kontakt;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IHistory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.DocumentStatus;
import ch.elexis.core.types.DocumentStatusMapper;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.model.internal.ModelUtil;
import ch.elexis.omnivore.model.internal.Preferences;

public class DocumentDocHandle extends AbstractIdDeleteModelAdapter<DocHandle>
		implements Identifiable, IDocumentHandle {
	
	private String storeId = "";
	
	public DocumentDocHandle(DocHandle entity){
		super(entity);
	}
	
	@Override
	public String getTitle(){
		return getEntity().getTitle();
	}
	
	@Override
	public void setTitle(String value){
		getEntityMarkDirty().setTitle(value);
	}
	
	@Override
	public String getDescription(){
		return "";
	}
	
	@Override
	public void setDescription(String value){
		// entity does not support description
	}
	
	@Override
	public List<DocumentStatus> getStatus(){
		int status = getEntity().getStatus();
		return new ArrayList<>(DocumentStatusMapper.map(status));
	}
	
	@Override
	public void setStatus(DocumentStatus status, boolean active){
		Set<DocumentStatus> _statusSet = new HashSet<>(getStatus());
		if (active) {
			_statusSet.add(status);
		} else {
			_statusSet.remove(status);
		}
		int value = DocumentStatusMapper.map(_statusSet);
		getEntity().setStatus(value);
	}
	
	@Override
	public Date getCreated(){
		LocalDate creationDate = getEntity().getCreationDate();
		return creationDate != null ? toDate(creationDate) : getLastchanged();
	}
	
	@Override
	public void setCreated(Date value){
		getEntityMarkDirty().setCreationDate(toLocalDate(value).toLocalDate());
	}
	
	@Override
	public Date getLastchanged(){
		if (getEntity().getDatum() != null) {
			return toDate(getEntity().getDatum());
		}
		if (getEntity().getLastupdate() != null) {
			return new Date(getEntity().getLastupdate().longValue());
		}
		return new Date(0);
	}
	
	@Override
	public void setLastchanged(Date value){
		getEntityMarkDirty().setDatum(toLocalDate(value).toLocalDate());
	}
	
	@Override
	public String getMimeType(){
		return StringUtils.defaultString(getEntity().getMimetype());
	}
	
	@Override
	public void setMimeType(String value){
		getEntityMarkDirty().setMimetype(value);
	}
	
	@Override
	public ICategory getCategory(){
		if (getEntity().getCategory() != null) {
			return new TransientCategory(getEntity().getCategory());
		}
		return new TransientCategory("?");
	}
	
	@Override
	public void setCategory(ICategory value){
		getEntityMarkDirty().setCategory(value.getName());
	}
	
	@Override
	public List<IHistory> getHistory(){
		return Collections.emptyList();
	}
	
	@Override
	public String getStoreId(){
		return StringUtils.isNotEmpty(storeId) ? storeId : "ch.elexis.data.store.omnivore";
	}
	
	@Override
	public void setStoreId(String value){
		storeId = value;
	}
	
	@Override
	public String getExtension(){
		return ModelUtil.evaluateFileExtension(getEntity().getMimetype());
	}
	
	@Override
	public void setExtension(String value){
		// entity does not support setting extension
	}
	
	@Override
	public String getKeywords(){
		return StringUtils.defaultString(getEntity().getKeywords());
	}
	
	@Override
	public void setKeywords(String value){
		getEntityMarkDirty().setKeywords(value);
	}
	
	@Override
	public IPatient getPatient(){
		return ModelUtil.loadCoreModel(getEntity().getKontakt(), IPatient.class);
	}
	
	@Override
	public void setPatient(IPatient value){
		if (value instanceof AbstractIdDeleteModelAdapter) {
			getEntityMarkDirty()
				.setKontakt((Kontakt) ((AbstractIdDeleteModelAdapter<?>) value).getEntity());
		} else if (value == null) {
			getEntityMarkDirty().setKontakt(null);
		}
	}
	
	@Override
	public IContact getAuthor(){
		// entity does not support author
		return null;
	}
	
	@Override
	public void setAuthor(IContact value){
		// entity does not support author
	}
	
	@Override
	public InputStream getContent(){
		byte[] contents = getContents();
		if (contents != null) {
			return new ByteArrayInputStream(contents);
		}
		return null;
	}
	
	@Override
	public void setContent(InputStream content){
		setStatus(DocumentStatus.PREPROCESSED, false);
		setStatus(DocumentStatus.INDEXED, false);
		setLastchanged(new Date());
		try {
			IVirtualFilesystemHandle vfsHandle = getStorageFile(false);
			if (vfsHandle == null) {
				getEntityMarkDirty().setDoc(IOUtils.toByteArray(content));
			} else {
				try (OutputStream out = vfsHandle.openOutputStream()) {
					out.write(IOUtils.toByteArray(content));
				}
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error setting content", e);
		}
	}
	
	private byte[] getContents(){
		byte[] ret = getEntity().getDoc();
		if (ret == null) {
			IVirtualFilesystemHandle vfsHandle = getStorageFile(true);
			try {
				if (vfsHandle != null && vfsHandle.exists()) {
					byte[] bytes = vfsHandle.readAllBytes();
					// if we stored the file in the file system but decided
					// later to store it in the
					// database: copy the file from the file system to the
					// database
					if (!Preferences.storeInFilesystem()) {
						getEntity().setDoc(bytes);
					}
					
					return bytes;
				} else {
					LoggerFactory.getLogger(getClass()).error(
						"Error content of [{}] from [{}] does not exist",
						getId(), vfsHandle);
				}
			} catch (Exception ex) {
				LoggerFactory.getLogger(getClass()).error("Getting content of [{}] fails [{}]",
					getId(), vfsHandle, ex);
				throw new IllegalStateException(ex);
			}
		}
		return ret;
	}
	
	@Override
	public IVirtualFilesystemHandle getHandle(){
		return getStorageFile(true);
	}
	
	public IVirtualFilesystemHandle getStorageFile(boolean force){
		if (force || Preferences.storeInFilesystem()) {
			String pathname = Preferences.getBasepath();
			if (pathname != null) {
				try {
					IVirtualFilesystemHandle storageDir =
						VirtualFilesystemServiceHolder.get().of(pathname);
					if (storageDir.isDirectory()) {
						IPatient patient =
							ModelUtil.loadCoreModel(getEntity().getKontakt(), IPatient.class);
						if (patient != null) {
							IVirtualFilesystemHandle patientDir =
								storageDir.subDir(patient.getPatientNr());
							if (!patientDir.exists()) {
								patientDir.mkdir();
							}
							IVirtualFilesystemHandle file =
								patientDir.subFile(getId() + "." + getExtension()); //$NON-NLS-1$
							if (!file.exists()) {
								// check if we can fix this with file without extension #21901
								IVirtualFilesystemHandle noExtensionFile =
									patientDir.subFile(getId());
								if (noExtensionFile.exists()) {
									noExtensionFile.copyTo(file);
									noExtensionFile.delete();
								}
							}
							return file;
						} else {
							if (getEntity().getKontakt() == null) {
								LoggerFactory.getLogger(getClass()).error(
									"DocHandle [" + getEntity().getId() + "] has no patient");
							} else {
								LoggerFactory.getLogger(getClass()).error("Contact ["
									+ getEntity().getKontakt().getId() + "] is not a patient");
							}
						}
					}
					
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass())
						.error("DocHandle [" + getEntity().getId() + "] IOException", e);
					return null;
				}
			}
			if (Preferences.storeInFilesystem()) {
				LoggerFactory.getLogger(getClass())
					.error("Config error: " + Messages.DocHandle_configErrorText);
			}
		}
		return null;
	}
	
	@Override
	public boolean isCategory(){
		return getMimeType().equals(Constants.CATEGORY_MIMETYPE);
	}
	
	@Override
	public boolean exportToFileSystem(){
		byte[] doc = getEntity().getDoc();
		// return true if doc is already on file system
		if (doc == null) {
			return true;
		}
		IVirtualFilesystemHandle vfsHandle = getStorageFile(true);
		try (OutputStream out = vfsHandle.openOutputStream()) {
			out.write(doc);
			getEntity().setDoc(null);
		} catch (IOException ios) {
			LoggerFactory.getLogger(getClass())
				.error("Exporting dochandle [" + getId() + "] to filesystem fails.");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists){
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}
	
	@Override
	public IXid getXid(String domain){
		return XidServiceHolder.get().getXid(this, domain);
	}
	
	@Override
	public String getLabel(){
		if (isCategory()) {
			return getTitle();
		} else {
			StringBuilder sb = new StringBuilder();
			// avoid adding only a space - causes trouble in renaming of categories
			Date date = getCreated();
			if (date != null) {
				SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
				sb.append(format.format(date));
				sb.append(" ");
			}
			sb.append(getTitle());
			return sb.toString();
		}
	}
}
