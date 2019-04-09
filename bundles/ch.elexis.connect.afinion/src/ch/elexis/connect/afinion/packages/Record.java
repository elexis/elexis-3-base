package ch.elexis.connect.afinion.packages;

import java.util.ArrayList;
import java.util.Calendar;

import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.data.Patient;

/**
 * Diese Klasse ist Platzhalter für eine Patient Record
 * 
 * @author immi
 * 
 */
public class Record {
	private HeaderPart header;
	private SubRecordPart[] parts = new SubRecordPart[4];
	private boolean isValid = false;
	private boolean isOutOfRange = false;
	
	public Record(final byte[] bytes){
		parse(bytes);
	}
	
	/**
	 * Header, Subparts werden geparst Footer interessiert nicht
	 * 
	 * @param bytes
	 */
	private void parse(byte[] bytes){
		header = new HeaderPart(bytes);
		int pos = header.length();
		for (int i = 0; i < 4; i++) {
			parts[i] = new SubRecordPart(bytes, pos);
			if (parts[i].isValid()) {
				isValid = true;
			}
			if (parts[i].isOutOfRange()) {
				isOutOfRange = true;
			}
			pos += parts[i].length();
		}
	}
	
	public String getId(){
		return this.header.getId();
	}
	
	public int getRecordNum(){
		return this.header.getRecordNum();
	}
	
	public Calendar getCalendar(){
		return this.header.getCalendar();
	}
	
	public int getRunNr(){
		return this.header.getRunNr();
	}
	
	public boolean isValid(){
		return this.isValid;
	}
	
	public boolean isOutOfRange(){
		return this.isOutOfRange;
	}
	
	public String getText(){
		String text = "";
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isValid()) {
				if (text.length() > 0) {
					text += ", ";
				}
				text +=
					parts[i].getKuerzel() + " " + parts[i].getResultStr() + " "
						+ parts[i].getUnit();
			}
		}
		return text;
	}
	
	/**
	 * Schreibt die Werte in die Datenbank
	 * 
	 * @param patient
	 * @throws PackageException
	 */
	public void write(Patient patient) throws PackageException{
		ArrayList<TransientLabResult> results = new ArrayList<TransientLabResult>();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isValid()) {
				Value val = Value.getValue(parts[i].getKuerzel(), parts[i].getUnit());
				results.add(val.fetchValue(patient, parts[i].getResultStr(), "",
					this.header.getDate()));
			}
		}
		LabImportUtilHolder.get().importLabResults(results, new DefaultLabImportUiHandler());
	}
	
	public String toString(){
		String str = header.toString() + "\n";
		for (int i = 0; i < parts.length; i++) {
			str += "S-Record " + i + ";";
			str += parts[i].toString() + "\n";
		}
		return str;
	}
	
}
