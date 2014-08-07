package at.medevit.elexis.ehc.core;

import java.io.InputStream;

import ch.elexis.data.Patient;
import ehealthconnector.cda.documents.ch.CdaCh;

public interface EhcCoreService {
	public CdaCh getPatientDocument(Patient patient);
	
	public CdaCh getDocument(InputStream document);
}
