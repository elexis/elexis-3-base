package at.medevit.elexis.cobasmira.resulthandler;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.model.CobasMiraMapping;
import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import at.medevit.elexis.cobasmira.model.CobasMiraPatientResult;
import at.medevit.elexis.cobasmira.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class ImportPatientResult {
	private static Logger logger = LoggerFactory.getLogger(ImportPatientResult.class);
	
	public static int importPatientResult(CobasMiraMessage msg){
		
		TimeTool resultDate = msg.getEntryDate();
		LinkedList<CobasMiraPatientResult> patientResults = msg.getPatientResults();
		logger.debug(resultDate + " Handling " + patientResults.size() + " results.");
		
		for (CobasMiraPatientResult coMiPaRe : patientResults) {
			String labitemID = CobasMiraMapping.getId(coMiPaRe.getTestName());
			LabItem l = LabItem.load(labitemID);
			if (!l.exists()) { // LabItem does not exist
				logger.warn("LabItem ID " + labitemID + " could not be loaded.");
				if (patientResults.size() == 1)
					return CobasMiraMessage.ELEXIS_RESULT_LABITEM_NOT_FOUND;
				continue;
			}
			
			// Resolve Patient
			String patientElexisID = coMiPaRe.getPatientIdentification();
			String kontrollePatientID = CoreHub.localCfg.get(Preferences.CONTROLUSER, "KO").trim();
			if (patientElexisID.equalsIgnoreCase(kontrollePatientID)) {
				int ret = ControlResultHandler.writeControlResult(coMiPaRe, msg.getEntryDate());
				if (patientResults.size() == 1)
					return ret;
				continue;
			}
			// Need to find PersistentObject ID for given PatientNr
			String patID =
				new Query<Patient>(Patient.class).findSingle(Patient.FLD_PATID, Query.EQUALS,
					patientElexisID);
			Patient p = Patient.load(patID);
			if (!p.exists()) { // Patient does not exist
				logger.warn("Patient " + patientElexisID + " could not be loaded.");
				if (patientResults.size() == 1)
					return CobasMiraMessage.ELEXIS_RESULT_PATIENT_NOT_FOUND;
				continue;
			}
			
			//TODO: check getForDate() Funktion?!		
			Query<LabResult> labResult = new Query<LabResult>(LabResult.class);
			labResult.add(LabResult.ITEM_ID, "=", labitemID);
			labResult.and();
			labResult.add(LabResult.DATE, "=", resultDate.toString(TimeTool.DATE_GER));
			labResult.and();
			labResult.add(LabResult.PATIENT_ID, "=", coMiPaRe.getPatientIdentification());
			List<LabResult> labResults = labResult.execute();
			
			if (labResults.size() == 0) {
				int noDecPlaces = CobasMiraMapping.getNoDecPlaces(coMiPaRe.getTestName());
				Float result = roundToDecimals(coMiPaRe.getConcentration(), noDecPlaces);
				// if remark value is set, value is blue and callable from lab view
				//				LabResult lr = new LabResult(p, resultDate, l, result.toString(), CobasMiraCodes.getRemarkString(coMiPaRe.remark));
				LabResult lr;
				if (noDecPlaces == 0) {
					lr = new LabResult(p, resultDate, l, Integer.toString(result.intValue()), "");
				} else {
					lr = new LabResult(p, resultDate, l, result.toString(), "");
				}
				
				lr.set("Quelle", l.getLabor().getKuerzel());
				lr.setFlag(0, true);
			} else if (labResults.size() == 1) { // We already have an entry
				//				LabResult lr = labResults.get(0);
				//				lr.set(LabResult.RESULT, result.toString());
				//				lr.set("Quelle", _labItem.getLabor().getKuerzel());
				//				lr.setFlag(0, true);
				logger.warn("We already have an entry for " + labResults);
				return CobasMiraMessage.ELEXIS_RESULT_RESULT_ALREADY_HERE;
			} else {
				logger.warn("Invalid number of entries (" + labResults.size() + ") for "
					+ labResults);
			}
			
		}
		return CobasMiraMessage.ELEXIS_RESULT_INTEGRATION_OK;
	}
	
	/**
	 * Round a given float value to a selected number of comma places
	 * 
	 * @param d
	 *            the value to be rounded
	 * @param c
	 *            the number of decimal places to round to
	 */
	public static float roundToDecimals(float d, int c){
		//TODO(marco): If c==0 write an integer into the labresult, not a float because float.toString() results in x.y
		if (c == 0)
			return Math.round(d);
		BigDecimal l_bd = new BigDecimal(d);
		l_bd = l_bd.setScale(c, BigDecimal.ROUND_HALF_UP);
		return l_bd.floatValue();
	}
	
}
