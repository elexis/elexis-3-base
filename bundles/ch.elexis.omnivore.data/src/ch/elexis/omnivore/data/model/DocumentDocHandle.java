package ch.elexis.omnivore.data.model;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.jpa.entities.DocHandle;
import ch.elexis.core.jpa.entities.Kontakt;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IHistory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.types.DocumentStatus;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.omnivore.data.Messages;
import ch.elexis.omnivore.data.Preferences;
import ch.elexis.omnivore.data.model.util.ModelUtil;
import ch.rgw.tools.ExHandler;

public class DocumentDocHandle extends AbstractIdDeleteModelAdapter<DocHandle>
		implements IdentifiableWithXid, IDocumentHandle {
	
	private String storeId = "";
	private String keywords;
	
	public DocumentDocHandle(DocHandle entity){
		super(entity);
	}
	
	@Override
	public String getTitle(){
		return getEntity().getTitle();
	}
	
	@Override
	public void setTitle(String value){
		getEntity().setTitle(value);
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
	public DocumentStatus getStatus(){
		return DocumentStatus.NEW;
	}
	
	@Override
	public void setStatus(DocumentStatus value){
		// entity does not support status
	}
	
	@Override
	public Date getCreated(){
		return toDate(getEntity().getCreationDate());
	}
	
	@Override
	public void setCreated(Date value){
		getEntity().setCreationDate(toLocalDate(value).toLocalDate());
	}
	
	@Override
	public Date getLastchanged(){
		if (getEntity().getLastupdate() != null) {
			return new Date(getEntity().getLastupdate().longValue());
		}
		return new Date(0);
	}
	
	@Override
	public void setLastchanged(Date value){
		// entity does not support last changed
	}
	
	@Override
	public String getMimeType(){
		return getEntity().getMimetype();
	}
	
	@Override
	public void setMimeType(String value){
		getEntity().setMimetype(value);
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
		getEntity().setCategory(value.getName());
	}
	
	@Override
	public List<IHistory> getHistory(){
		return Collections.emptyList();
	}
	
	@Override
	public String getStoreId(){
		return storeId;
	}
	
	@Override
	public void setStoreId(String value){
		storeId = value;
	}
	
	@Override
	public String getExtension(){
		return ModelUtil.evaluateFileExtension(getEntity().getTitle());
	}
	
	@Override
	public void setExtension(String value){
		// entity does not support setting extension
	}
	
	@Override
	public String getKeywords(){
		return this.keywords;
	}
	
	@Override
	public void setKeywords(String value){
		this.keywords = value;
	}
	
	@Override
	public IPatient getPatient(){
		return ModelUtil.loadCoreModel(getEntity().getKontakt(), IPatient.class);
	}
	
	@Override
	public void setPatient(IPatient value){
		if (value instanceof AbstractIdDeleteModelAdapter) {
			getEntity().setKontakt((Kontakt) ((AbstractIdDeleteModelAdapter<?>) value).getEntity());
		} else if (value == null) {
			getEntity().setKontakt(null);
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
		return new ByteArrayInputStream(getContents());
	}
	
	@Override
	public void setContent(InputStream content){
		try {
			File file = getStorageFile(false);
			if (file == null) {
				getEntity().setDoc(IOUtils.toByteArray(content));
			} else {
				try (BufferedOutputStream bout =
					new BufferedOutputStream(new FileOutputStream(file))) {
					bout.write(IOUtils.toByteArray(content));
				}
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error setting content", e);
		}
	}
	
	private byte[] getContents(){
		byte[] ret = getEntity().getDoc();
		if (ret == null) {
			File file = getStorageFile(true);
			if (file != null) {
				try {
					byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
					// if we stored the file in the file system but decided
					// later to store it in the
					// database: copy the file from the file system to the
					// database
					if (!Preferences.storeInFilesystem()) {
						try {
							getEntity().setDoc(bytes);
						} catch (PersistenceException pe) {
							SWTHelper.showError(Messages.DocHandle_readErrorCaption,
								Messages.DocHandle_importErrorText + "; " + pe.getMessage());
						}
					}
					
					return bytes;
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.showError(Messages.DocHandle_readErrorHeading,
						Messages.DocHandle_importError2,
						MessageFormat.format(Messages.DocHandle_importErrorText2 + ex.getMessage(),
							file.getAbsolutePath()));
				}
			}
		}
		return ret;
	}
	
	private File getStorageFile(boolean force){
		if (force || Preferences.storeInFilesystem()) {
			String pathname = Preferences.getBasepath();
			if (pathname != null) {
				File dir = new File(pathname);
				if (dir.isDirectory()) {
					IPatient patient =
						ModelUtil.loadCoreModel(getEntity().getKontakt(), IPatient.class);
					File subdir = new File(dir, patient.getPatientNr());
					if (!subdir.exists()) {
						subdir.mkdir();
					}
					File file = new File(subdir, getId() + "." + getExtension()); //$NON-NLS-1$
					return file;
				}
			}
			if (Preferences.storeInFilesystem()) {
				configError();
			}
		}
		return null;
	}
	
	private void configError(){
		SWTHelper.showError("config error", Messages.DocHandle_configErrorCaption, //$NON-NLS-1$
			Messages.DocHandle_configErrorText);
	}
}
