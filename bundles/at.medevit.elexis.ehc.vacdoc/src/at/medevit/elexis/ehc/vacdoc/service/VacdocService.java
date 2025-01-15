package at.medevit.elexis.ehc.vacdoc.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Medication;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public interface VacdocService {

	/**
	 * Create a XDM as stream, with the provided bundle as content.
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public InputStream getXdmAsStream(Bundle document) throws Exception;

	/**
	 * Get empty {@link CdaChVacd} document with patient as Patient and mandant as
	 * Author. Also the fields Custodian and LegalAuthenticator are initialized.
	 *
	 * @param patient
	 * @param mandant
	 * @return vaccination document
	 */
	public Bundle getVacdocDocument(Patient patient, Mandant mandant);

	/**
	 * Load a {@link CdaChVacd} instance from the provided {@link InputStream}.
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Optional<Bundle> loadVacdocDocument(InputStream document) throws Exception;

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
	 * @param bundle
	 */
	public void addAllVaccinations(Bundle bundle);

	/**
	 * Add the provided {@link Vaccination} instances to the provided
	 * {@link CdaChVacd}.
	 *
	 * @param doc
	 */
	public void addVaccinations(Bundle doc, List<Vaccination> vaccinations);

	/**
	 * Get all {@link Immunization} resources from the {@link Bundle}.
	 * 
	 * @param ehcDocument
	 * @return
	 */
	public List<Immunization> getImmunizations(Bundle ehcDocument);

	/**
	 * Get the {@link Medication} resource for the provided {@link Immunization}.
	 * 
	 * @param immunization
	 * @return
	 */
	public Optional<Medication> getMedication(Immunization immunization);
}
