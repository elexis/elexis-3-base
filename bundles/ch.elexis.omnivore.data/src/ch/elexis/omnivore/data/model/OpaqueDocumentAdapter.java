package ch.elexis.omnivore.data.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Patient;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.rgw.tools.TimeTool;

public class OpaqueDocumentAdapter implements IOpaqueDocument {

	private IDocumentHandle documentHandle;

	public OpaqueDocumentAdapter(IDocumentHandle documentHandle) {
		this.documentHandle = documentHandle;
	}

	@Override
	public String getTitle() {
		return documentHandle.getTitle();
	}

	@Override
	public String getMimeType() {
		return documentHandle.getMimeType();
	}

	@Override
	public InputStream getContentsAsStream() throws ElexisException {
		return documentHandle.getContent();
	}

	@Override
	public byte[] getContentsAsBytes() throws ElexisException {
		try {
			return IOUtils.toByteArray(documentHandle.getContent());
		} catch (IOException e) {
			throw new ElexisException("Error getting content", e); //$NON-NLS-1$
		}
	}

	@Override
	public String getKeywords() {
		return documentHandle.getKeywords();
	}

	@Override
	public String getCategory() {
		return documentHandle.getCategory().getName();
	}

	@Override
	public String getCreationDate() {
		return new TimeTool(documentHandle.getCreated()).toString(TimeTool.DATE_GER);
	}

	@Override
	public Patient getPatient() {
		return Patient.load(documentHandle.getPatient().getId());
	}

	@Override
	public String getGUID() {
		return documentHandle.getId();
	}
}
