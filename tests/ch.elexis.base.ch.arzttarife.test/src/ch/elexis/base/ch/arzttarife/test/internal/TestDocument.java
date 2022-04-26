package ch.elexis.base.ch.arzttarife.test.internal;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IHistory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.types.DocumentStatus;

public class TestDocument implements IDocument {

	private IPatient patient;

	public TestDocument(IPatient patient) {
		this.patient = patient;
	}

	@Override
	public String getId() {
		return "testDocument";
	}

	@Override
	public String getLabel() {
		return "testDocument.pdf";
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
		return System.currentTimeMillis();
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
		return "testDocument.pdf";
	}

	@Override
	public void setTitle(String value) {

	}

	@Override
	public String getDescription() {
		return "testDescription";
	}

	@Override
	public void setDescription(String value) {
	}

	@Override
	public List<DocumentStatus> getStatus() {
		return Collections.singletonList(DocumentStatus.NEW);
	}

	@Override
	public Date getCreated() {
		return new Date();
	}

	@Override
	public void setCreated(Date value) {
	}

	@Override
	public Date getLastchanged() {
		return new Date();
	}

	@Override
	public void setLastchanged(Date value) {
	}

	@Override
	public String getMimeType() {
		return "application/pdf";
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
		return Collections.emptyList();
	}

	@Override
	public String getStoreId() {
		return null;
	}

	@Override
	public void setStoreId(String value) {
	}

	@Override
	public String getExtension() {
		return null;
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
		return patient;
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
		return null;
	}

	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public void setContent(InputStream content) {

	}

	@Override
	public void setStatus(DocumentStatus status, boolean active) {
	}

}
