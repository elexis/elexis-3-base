package ch.elexis.global_inbox.ui.parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IHistory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.types.DocumentStatus;

/**
 * Represent a {@link File} as an {@link IDocument} - readable only.
 */
class FileDocument implements IDocument {

	private final File file;
	private final BasicFileAttributes attr;

	public static FileDocument of(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("must not be null"); //$NON-NLS-1$
		}

		BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		FileDocument fileDocument = new FileDocument(file, attr);
		return fileDocument;
	}

	private FileDocument(File file, BasicFileAttributes attr) {
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
		return file.lastModified();
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
		String _url = file.toURI().toString();
		int lastIndexOf = _url.lastIndexOf('.');
		if (lastIndexOf > -1) {
			return _url.substring(lastIndexOf + 1);
		}
		return StringUtils.EMPTY;
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
			return new FileInputStream(file);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).warn("[{}] getContent()", file.getAbsolutePath(), e); //$NON-NLS-1$
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
		return file.length();
	}

}
