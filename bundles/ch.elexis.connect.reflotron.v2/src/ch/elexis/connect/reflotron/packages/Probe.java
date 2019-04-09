package ch.elexis.connect.reflotron.packages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.elexis.connect.reflotron.Messages;
import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Probe {
	private static final String UNIT_TEST_RUNNING = "ElexisReflotronUnitTestRunning";
	private static int NAME = 0;
	private static int VALUE = 1;
	private static int UNIT = 2;
	
	private boolean isEnzym;
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
			resultat = strArray[3];
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
		if (resultat == null || resultat.isEmpty()) {
			throw new PackageException(Messages.Reflotron_Probe_ResultatMsg);
		}
		
		isEnzym = false;
		String paramName, value, unit;
		
		String[] resFields = splitResultFields();
		int noFields = resFields.length;
		int offset = 0;
		
		paramName = resFields[NAME];
		// check if an offset is needed
		if ((!isEnzym && noFields == 4) || (isEnzym && noFields == 5)) {
			offset = 1;
			value = (resFields[VALUE] + resFields[VALUE + offset]).trim();
		} else {
			value = resFields[VALUE];
		}
		unit = resFields[UNIT + offset];
		
		Value val = Value.getValue(paramName, unit);
		
		// for Unit-Test only
		if (UNIT_TEST_RUNNING.equals(hint)) {
			return val.get_longName() + ";" + val.get_shortName() + ";" + value + ";" + unit;
		}
		
		TransientLabResult result = val.fetchValue(patient, value, "", getDate()); //$NON-NLS-1$
		
		LabImportUtilHolder.get().importLabResults(Collections.singletonList(result),
			new DefaultLabImportUiHandler());
		
		return val.getWarning();
	}
	
	private String[] splitResultFields() throws PackageException{
		//replace all multispaces with a single space
		resultat = resultat.replaceAll("\\s+", " ").trim();
		
		String[] split = resultat.split(" ");
		String regex = "^[a-zA-Z0-9/]+$"; //letter,digit, '/'
		
		// might be an enzym
		if (resultat.endsWith("C")) {
			String prevChar = resultat.substring(resultat.length() - 2, resultat.length() - 1);
			boolean matches = prevChar.matches(regex);
			
			//Enzym
			if (!matches) {
				isEnzym = true;
				if (split.length < 4 && resultat.length() > 20) {
					List<String> splitList = new ArrayList<String>();
					splitList.add(resultat.substring(0, 4).trim().toUpperCase()); //name
					splitList.add(resultat.substring(4, 10).trim()); //value
					splitList.add(resultat.substring(10, 16).trim()); //unit 
					splitList.add(resultat.substring(16, 20).trim()); //ref. temp
					
					return splitList.toArray(new String[splitList.size()]);
				}
			}
		}
		
		if (split.length < 3) {
			throw new PackageException(Messages.Reflotron_Probe_ResultatMsg); //$NON-NLS-1$
		}
		return split;
		
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
	
	public void setResult(String resultat){
		this.resultat = resultat;
	}
}
