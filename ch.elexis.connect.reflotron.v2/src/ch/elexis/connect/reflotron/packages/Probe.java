package ch.elexis.connect.reflotron.packages;

import java.util.Collections;

import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil.TransientLabResult;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Probe {
	private TimeTool date;
	private String ident;
	private String resultat;
	private String hint;
	private String zusatztext;
	
	public Probe(final String[] strArray){
		parse(strArray);
	}
	
	/**
	 * Liest Probendaten aus Array
	 * 
	 * @param strArray
	 *            Array[7]
	 */
	private void parse(final String[] strArray){
		int dateIndex = strArray[1].indexOf("."); //$NON-NLS-1$
		int timeIndex = strArray[1].indexOf(":"); //$NON-NLS-1$
		
		String dateStr = strArray[1].substring(dateIndex - 2, dateIndex + 6);
		String timeStr = strArray[1].substring(timeIndex - 2, timeIndex + 6);
		
		date = new TimeTool(dateStr);
		date.set(timeStr);
		
		if (strArray.length > 2) {
			ident = strArray[2].trim();
		}
		
		if (strArray.length > 3) {
			resultat = strArray[3].trim();
		}
		
		if (strArray.length > 4) {
			hint = strArray[4].trim();
		}
		
		if (strArray.length > 5) {
			zusatztext = strArray[5].trim();
		}
	}
	
	/**
	 * Schreibt Labordaten
	 * 
	 * @return String Warnungsmeldung, die geloggt werden sollte
	 */
	public String write(Patient patient) throws PackageException{
		if (getResultat().length() < 19) {
			throw new PackageException(Messages.getString("Probe.ResultatMsg")); //$NON-NLS-1$
		}
		
		String paramName;
		String value;
		String unit;
		if (getResultat().length() > 20) {
			if (getResultat().charAt(20) == 'C') {
				// Enzym
				paramName = getResultat().substring(0, 4).trim().toUpperCase();
				value = getResultat().substring(4, 10).trim();
				unit = getResultat().substring(10, 16).trim();
			} else {
				// Substrat
				paramName = getResultat().substring(0, 4).trim().toUpperCase();
				value = getResultat().substring(5, 11).trim();
				unit = getResultat().substring(12, 21).trim();
			}
		} else {
			// Substrat
			paramName = getResultat().substring(0, 4).trim().toUpperCase();
			value = getResultat().substring(5, 11).trim();
			unit = getResultat().substring(12, getResultat().length()).trim();
		}
		
		Value val = Value.getValue(paramName, unit);
		TransientLabResult result = val.fetchValue(patient, value, "", getDate()); //$NON-NLS-1$
		
		LabImportUtil.importLabResults(Collections.singletonList(result),
			new DefaultLabImportUiHandler());
		
		return val.getWarning();
	}
	
	public TimeTool getDate(){
		return date;
	}
	
	public String getIdent(){
		return ident;
	}
	
	public String getResultat(){
		return resultat;
	}
	
	public String getHint(){
		return hint;
	}
	
	public String getZusatztext(){
		return zusatztext;
	}
}
