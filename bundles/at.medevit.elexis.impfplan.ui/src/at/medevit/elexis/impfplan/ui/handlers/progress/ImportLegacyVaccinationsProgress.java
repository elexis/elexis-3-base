package at.medevit.elexis.impfplan.ui.handlers.progress;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.handlers.ImportLegacyVaccinationsHandler;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

/**
 * Performs the vaccination conversion. Converts vaccinations from ch.elexis.impfplan to
 * vaccinations of this plugin. This class is only usable if the optional ch.elexis.impflan
 * dependency is resolvable.
 * 
 * @author Lucia
 *
 */
public class ImportLegacyVaccinationsProgress implements IRunnableWithProgress {
	private static Logger log = LoggerFactory.getLogger(ImportLegacyVaccinationsHandler.class);
	
	/**
	 * Information about import failure reason
	 */
	public static enum ErrorCode {
		//@formatter:off
		PATIENT_NOTFOUND("Patient nicht gefunden"), 
		VACC_AGAINST_NOT_SET("Impfung gegen Krankheit(en) nicht gesetzt"), 
		VACC_TYPE_NOT_SET("Keiner Impfung zuzuordnen");
		//@formatter:on
		
		private final String text;
		
		private ErrorCode(final String text){
			this.text = text;
		}
		
		@Override
		public String toString(){
			return text;
		}
	}
	
	private String administrator;
	private Map<ch.elexis.impfplan.model.Vaccination, ErrorCode> errorMap;
	private List<ch.elexis.impfplan.model.Vaccination> alreadyImported;
	
	/**
	 * Constructor
	 * 
	 * @param administrator
	 *            the doctor to use as administrator of all the vaccinations that will be imported
	 */
	public ImportLegacyVaccinationsProgress(String administrator){
		this.administrator = administrator;
		
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
		InterruptedException{
		log.debug("Start vaccination import");
		monitor.beginTask("Importiere Impfungen", 2);
		
		monitor.subTask("Lese zu importierende Impfungen ein...");
		Query<ch.elexis.impfplan.model.Vaccination> qbe =
			new Query<>(ch.elexis.impfplan.model.Vaccination.class);
		List<ch.elexis.impfplan.model.Vaccination> list = qbe.execute();
		
		errorMap = new HashMap<ch.elexis.impfplan.model.Vaccination, ErrorCode>();
		alreadyImported = new ArrayList<ch.elexis.impfplan.model.Vaccination>();
		
		monitor.worked(1);
		
		monitor.subTask("Importiere...");
		for (ch.elexis.impfplan.model.Vaccination vacc : list) {
			String patId = vacc.getPatientId();
			
			// validate patient ref
			if (!Patient.load(patId).exists()) {
				if (!vacc.getId().equals("VERSION")) {
					log.warn(ErrorCode.PATIENT_NOTFOUND + " [" + patId + "], Vacc[" + vacc.getId()
						+ "], " + vacc.getLabel());
					errorMap.put(vacc, ErrorCode.PATIENT_NOTFOUND);
				}
				continue;
			}
			
			// is vaccination type set
			if (vacc.getVaccinationType() == null) {
				log.warn(ErrorCode.VACC_TYPE_NOT_SET + " - VaccinationTyp ref. missing, Vacc["
					+ vacc.getId() + "], " + vacc.getLabel());
				errorMap.put(vacc, ErrorCode.VACC_TYPE_NOT_SET);
				continue;
			}
			
			// is vaccination against field set
			String vaccAgainst = vacc.getVaccinationType().getVaccAgainst();
			if (vaccAgainst == null || vaccAgainst.isEmpty()) {
				log.warn(ErrorCode.VACC_AGAINST_NOT_SET + ", Vacc[" + vacc.getId() + "], "
					+ vacc.getLabel());
				errorMap.put(vacc, ErrorCode.VACC_AGAINST_NOT_SET);
				continue;
			}
			
			// did we already import this vaccination
			if (alreadyImported(patId, vacc)) {
				log.debug("Already imported: " + vacc.getId() + ", " + vacc.getLabel());
				alreadyImported.add(vacc);
				continue;
			}
			
			String doa = vacc.getRawDateString();
			String vaccName = vacc.getVaccinationType().getLabel();
			
			// create vaccination
			Vaccination v =
				new Vaccination(patId, null, vaccName, null, null, doa, null, administrator);
			v.setVaccAgainst(vaccAgainst);
		}
		monitor.worked(1);
		monitor.done();
		log.debug("Vaccination import completed");
	}
	
	/**
	 * Feedback whether the import completed normal or there where any specially handled
	 * vaccinations
	 * 
	 * @return true if special handling was needed, false if completed normally
	 */
	public boolean isAbnormalImport(){
		if (errorMap.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Provides knowledge about all failed to import {@link ch.elexis.impfplan.model.Vaccination}s
	 * with the belonging {@link ErrorCode}
	 * 
	 * @return a map holding the actual vaccination and error code, may be empty
	 */
	public Map<ch.elexis.impfplan.model.Vaccination, ErrorCode> getErrorMap(){
		return errorMap;
	}
	
	/**
	 * List of already imported vaccinations
	 * 
	 * @return List of {@link ch.elexis.impfplan.model.Vaccination}, may be empty
	 */
	public List<ch.elexis.impfplan.model.Vaccination> getAlreadyImportedVaccinations(){
		return alreadyImported;
	}
	
	/**
	 * check if vaccination has already be converted
	 * 
	 * @param patId
	 *            Id of the patient the vaccination was given to
	 * @param vacc
	 *            the vaccination itself
	 * @return true if already imported, false if not
	 */
	private boolean alreadyImported(String patId, ch.elexis.impfplan.model.Vaccination vacc){
		Query<Vaccination> qbe = new Query<>(Vaccination.class);
		qbe.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, patId);
		qbe.add(Vaccination.FLD_BUSS_NAME, Query.EQUALS, vacc.getVaccinationType().getLabel());
		List<Vaccination> list = qbe.execute();
		
		if (list == null || list.isEmpty()) {
			return false;
		}
		return true;
	}
	
}
