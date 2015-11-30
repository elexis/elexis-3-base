package ch.elexis.laborimport.eurolyser;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil.TransientLabResult;
import ch.elexis.core.ui.laboratory.dialogs.LabItemSelektor;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabMapping;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class EurolyserLine {
	
	private static Logger logger = LoggerFactory.getLogger(EurolyserLine.class);
	
	private Labor labor;
	
	private String line;
	
	private String mandantId;
	
	private String patientId;
	private String patientName;
	
	private String resultItemName;
	private String resultValue;
	private String resultUnit;
	
	private String resultObservationTime;
	
	public EurolyserLine(Labor labor, String line){
		this.labor = labor;
		this.line = line;
		parseLine();
	}
	
	private void parseLine(){
		String[] parts = line.split(";");
		
		if (parts.length == 5) {
			mandantId = getMandantId(parts[0]);
			patientId = getPatientId(parts[0]);
			patientName = parts[1];
			
			resultItemName = parts[2];
			resultValue = getResultValue(parts[3]);
			resultUnit = getResultUnit(parts[3]);
			resultObservationTime = parts[4];
		} else {
			throw new IllegalStateException("Result line [" + line + "] is not valid");
		}
	}
	
	private String getResultUnit(String string){
		String[] parts = string.split(" ");
		if (parts.length > 1) {
			return parts[1].trim();
		}
		return "";
	}
	
	private String getResultValue(String string){
		String[] parts = string.split(" ");
		if (parts.length > 0) {
			return parts[0].trim();
		}
		throw new IllegalStateException("Result line [" + line + "] has no result");
	}
	
	private String getPatientId(String string){
		char[] chars = string.toCharArray();
		// collect all digit characters
		StringBuilder sb = new StringBuilder();
		boolean notNullFound = false;
		for (char c : chars) {
			if (Character.isDigit(c)) {
				if (c != '0') {
					notNullFound = true;
				}
				if (notNullFound) {
					sb.append(c);
				}
			}
		}
		// remove leading 0
		return sb.toString();
	}
	
	private String getMandantId(String string){
		char[] chars = string.toCharArray();
		// collect all non digit characters
		StringBuilder sb = new StringBuilder();
		for (char c : chars) {
			if (!Character.isDigit(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Test if this line should be imported.
	 * 
	 * @return
	 */
	public boolean isRelevant(){
		String confMandantId =
			CoreHub.mandantCfg.get(EurolyserImporter.CONFIG_IMPORT_MANDANTONLY, "").trim();
		if (!confMandantId.isEmpty()) {
			return confMandantId.equalsIgnoreCase(mandantId);
		}
		return true;
	}
	
	private class AskAbortRunnable implements Runnable {
		private boolean result;
		
		@Override
		public void run(){
			result = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				"Abbrechen",
				"Kein Patient ausgewählt. Soll der gesamte Import abgerochen werden?");
		}
		
		public boolean getResult(){
			return result;
		}
	}
	
	/**
	 * Create a LabResult from the imported line information.
	 * 
	 * @return
	 */
	public TransientLabResult createResult(){
		Patient patient = resolvePatient();
		if (patient == null) {
			AskAbortRunnable askAbort = new AskAbortRunnable();
			Display.getDefault().syncExec(askAbort);
			if (askAbort.getResult()) {
				throw new RuntimeException("Import aborted");
			}
		}
		
		LabItem labItem = resolveLabItem();
		
		if (labItem != null && patient != null) {
			TimeTool analyseTime = new TimeTool(resultObservationTime);
			TransientLabResult result =
				new TransientLabResult.Builder(patient, labor, labItem, resultValue)
					.unit(resultUnit).analyseTime(analyseTime).build();
			return result;
		}
		
		return null;
	}
	
	private LabItem resolveLabItem(){
		LabItem item = LabImportUtil.getLabItem(resultItemName, labor);
		
		if (item == null) {
			Display display = Display.getDefault();
			LabItemSelectionRunnable runnable = new LabItemSelectionRunnable(resultItemName);
			display.syncExec(runnable);
			item = runnable.getLabItem();
		}
		
		return item;
	}
	
	private class LabItemSelectionRunnable implements Runnable {
		
		private LabItem labItem;
		private String labItemName;
		
		public LabItemSelectionRunnable(String labItemName){
			this.labItemName = labItemName;
		}
		
		public LabItem getLabItem(){
			return labItem;
		}
		
		@Override
		public void run(){
			LabItemSelektor selektor = new LabItemSelektor(Display.getDefault().getActiveShell());
			selektor.create();
			selektor.setMessage("Labor Parameter [" + labItemName + "] auswählen.");
			if (selektor.open() == Dialog.OK) {
				List<LabItem> items = selektor.getSelection();
				
				if (!items.isEmpty()) {
					// create a mapping with the slection
					new LabMapping(labor.getId(), labItemName, items.get(0).getId(), false);
					labItem = items.get(0);
					logger.info("Item mapping created for " + labItem.getLabel());
				}
			}
		}
		
	}
	
	private Patient resolvePatient(){
		String lastname = "";
		String firstname = "";
		String[] nameParts = patientName.split(" ");
		if (nameParts.length > 0) {
			lastname = nameParts[0];
		}
		if (nameParts.length > 1) {
			firstname = nameParts[1];
		}
		
		Patient p = Patient.loadByPatientID(patientId);
		if (p != null && p.isValid()) {
			if (p.getVorname().equalsIgnoreCase(firstname)
				&& p.getName().equalsIgnoreCase(lastname)) {
				logger.info("Patient " + p.getLabel() + " found by PracitceID [" + patientId + "]");
				return p;
			}
		}
		
		return (Patient) KontaktSelektor.showInSync(Patient.class, "Patient ausw\u00E4hlen",
			"Wer ist " + lastname + " " + firstname + "?");
	}
}
