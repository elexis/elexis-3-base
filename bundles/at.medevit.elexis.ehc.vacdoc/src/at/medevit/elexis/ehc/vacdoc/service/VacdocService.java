package at.medevit.elexis.ehc.vacdoc.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.cda.ch.vacd.Immunization;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public interface VacdocService {

	/**
	 * Create a XDM as stream, with the provided document as content.
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public InputStream getXdmAsStream(CdaChVacd document) throws Exception;

	/**
	 * Get empty {@link CdaChVacd} document with patient as Patient and mandant as
	 * Author. Also the fields Custodian and LegalAuthenticator are initialized.
	 *
	 * @param patient
	 * @param mandant
	 * @return vaccination document
	 */
	public CdaChVacd getVacdocDocument(Patient patient, Mandant mandant);

	/**
	 * Load a {@link CdaChVacd} instance from the provided {@link InputStream}.
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Optional<CdaChVacd> loadVacdocDocument(InputStream document) throws Exception;

	/**
	 * Import the immunizations into the Elexis domain (Database).
	 *
	 * @param elexisPatient
	 * @param immunizations
	 */
	public void importImmunizations(Patient elexisPatient, List<Immunization> immunizations);

	/**
	 * Add all {@link Vaccination} instances, of the {@link Patient} referenced in
	 * the provided {@link CdaChVacd}, to the provided {@link CdaChVacd}.
	 *
	 * @param doc
	 */
	public void addAllVaccinations(CdaChVacd doc);

	/**
	 * Add the provided {@link Vaccination} instances to the provided
	 * {@link CdaChVacd}.
	 *
	 * @param doc
	 */
	public void addVaccinations(CdaChVacd doc, List<Vaccination> vaccinations);
}
