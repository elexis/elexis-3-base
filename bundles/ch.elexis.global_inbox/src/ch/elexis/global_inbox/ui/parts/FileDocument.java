package ch.elexis.global_inbox.ui.parts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IHistory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.types.DocumentStatus;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;

/**
 * Represent an {@link IVirtualFilesystemHandle} as an {@link IDocument} -
 * readable only.
 */
class FileDocument implements IDocument {

	private final IVirtualFilesystemHandle file;
	/**
	 * Creation and modification timestamps are only available on the local
	 * filesystem, <code>null</code> for smb, dav and davs.
	 */
	private final BasicFileAttributes attr;

	public static FileDocument of(IVirtualFilesystemHandle file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("must not be null"); //$NON-NLS-1$
		}

		BasicFileAttributes attr = null;
		Optional<File> localFile = file.toFile();
		if (localFile.isPresent()) {
			attr = Files.readAttributes(localFile.get().toPath(), BasicFileAttributes.class);
		}
		FileDocument fileDocument = new FileDocument(file, attr);
		return fileDocument;
	}

	private FileDocument(IVirtualFilesystemHandle file, BasicFileAttributes attr) {
		this.file = file;
		this.attr = attr;
	}

	@Override
	public String getId() {
		return file.getName();
	}

	@Override
	public String getLabel() {
		return file.getName();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return false;
	}

	@Override
	public IXid getXid(String domain) {
		return null;
	}

	@Override
	public Long getLastupdate() {
		return attr != null ? attr.lastModifiedTime().to(TimeUnit.MILLISECONDS) : null;
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public void setDeleted(boolean value) {
	}

	@Override
	public String getTitle() {
		return file.getName();
	}

	@Override
	public void setTitle(String value) {
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setDescription(String value) {
	}

	@Override
	public List<DocumentStatus> getStatus() {
		return null;
	}

	@Override
	public Date getCreated() {
		if (attr == null) {
			return null;
		}
		long milliseconds = attr.creationTime().to(TimeUnit.MILLISECONDS);
		if ((milliseconds > Long.MIN_VALUE) && (milliseconds < Long.MAX_VALUE)) {
			Date creationDate = new Date(attr.creationTime().to(TimeUnit.MILLISECONDS));
			return creationDate;
		}
		return null;
	}

	@Override
	public void setCreated(Date value) {
	}

	@Override
	public Date getLastchanged() {
		if (attr == null) {
			return null;
		}
		long milliseconds = attr.lastModifiedTime().to(TimeUnit.MILLISECONDS);
		if ((milliseconds > Long.MIN_VALUE) && (milliseconds < Long.MAX_VALUE)) {
			Date lastModified = new Date(attr.lastModifiedTime().to(TimeUnit.MILLISECONDS));
			return lastModified;
		}
		return null;
	}

	@Override
	public void setLastchanged(Date value) {
	}

	@Override
	public String getMimeType() {
		return null;
	}

	@Override
	public void setMimeType(String value) {
	}

	@Override
	public ICategory getCategory() {
		return null;
	}

	@Override
	public void setCategory(ICategory value) {
	}

	@Override
	public List<IHistory> getHistory() {
		return null;
	}

	@Override
	public String getStoreId() {
		return "filesystem"; //$NON-NLS-1$
	}

	@Override
	public void setStoreId(String value) {
	}

	@Override
	public String getExtension() {
		return file.getExtension();
	}

	@Override
	public void setExtension(String value) {

	}

	@Override
	public String getKeywords() {
		return null;
	}

	@Override
	public void setKeywords(String value) {

	}

	@Override
	public IPatient getPatient() {
		return null;
	}

	@Override
	public void setPatient(IPatient value) {
	}

	@Override
	public IContact getAuthor() {
		return null;
	}

	@Override
	public void setAuthor(IContact value) {
	}

	@Override
	public InputStream getContent() {
		try {
			return file.openInputStream();
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).warn("[{}] getContent()", GlobalInboxUtil.toLogString(file), e); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public void setContent(InputStream content) {
	}

	@Override
	public void setStatus(DocumentStatus status, boolean active) {
	}

	@Override
	public long getContentLength() {
		try {
			return file.getContentLenght();
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).warn("[{}] getContentLength()", GlobalInboxUtil.toLogString(file), e); //$NON-NLS-1$
			return 0;
		}
	}

}
