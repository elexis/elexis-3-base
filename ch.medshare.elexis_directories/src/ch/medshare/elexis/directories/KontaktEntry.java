/*******************************************************************************
 * Copyright (c) 2007, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Imhof - initial implementation
 *    G. Weirich - added toHashmap
 *    
 *******************************************************************************/

package ch.medshare.elexis.directories;

import java.util.HashMap;

import ch.elexis.data.Patient;

public class KontaktEntry {
	private final String vorname;
	private final String name;
	private final String zusatz;
	private final String adresse;
	private final String plz;
	private final String ort;
	private final String tel;
	private final String fax;
	private final String email;
	private final boolean isDetail; // List Kontakt oder Detail Kontakt
	
	public KontaktEntry(final String vorname, final String name, final String zusatz,
		final String adresse, final String plz, final String ort, final String tel, String fax,
		String email, boolean isDetail){
		super();
		this.vorname = vorname;
		this.name = name;
		this.zusatz = zusatz;
		this.adresse = adresse;
		this.plz = plz;
		this.ort = ort;
		this.tel = tel;
		this.fax = fax;
		this.email = email;
		this.isDetail = isDetail;
	}
	
	/**
	 * Fill all fields into a hashmap
	 * 
	 * @return a hashmap with all non-empty fields with standard names
	 * @author gerry
	 */
	public HashMap<String, String> toHashmap(){
		HashMap<String, String> ret = new HashMap<String, String>();
		if (countValue(name) > 0) {
			ret.put(Patient.FLD_NAME, name);
		}
		if (countValue(vorname) > 0) {
			ret.put(Patient.FLD_FIRSTNAME, vorname);
		}
		if (countValue(adresse) > 0) {
			ret.put(Patient.FLD_STREET, adresse);
		}
		if (countValue(plz) > 0) {
			ret.put(Patient.FLD_ZIP, plz);
		}
		if (countValue(ort) > 0) {
			ret.put(Patient.FLD_PLACE, ort);
		}
		if (countValue(tel) > 0) {
			ret.put(Patient.FLD_PHONE1, tel);
		}
		if (countValue(fax) > 0) {
			ret.put(Patient.FLD_FAX, fax);
		}
		return ret;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getVorname(){
		return this.vorname;
	}
	
	public String getZusatz(){
		return this.zusatz;
	}
	
	public String getAdresse(){
		return this.adresse;
	}
	
	public String getPlz(){
		return this.plz;
	}
	
	public String getOrt(){
		return this.ort;
	}
	
	public String getTelefon(){
		return this.tel;
	}
	
	public String getFax(){
		return fax;
	}
	
	public String getEmail(){
		return email;
	}
	
	public boolean isDetail(){
		return this.isDetail;
	}
	
	private int countValue(String value){
		if (value != null && value.length() > 0) {
			return 1;
		}
		return 0;
	}
	
	public int countNotEmptyFields(){
		return countValue(getVorname()) + countValue(getName()) + countValue(getZusatz())
			+ countValue(getAdresse()) + countValue(getPlz()) + countValue(getOrt())
			+ countValue(getTelefon()) + countValue(getFax()) + countValue(getEmail());
	}
	
	public String toString(){
		return getName() + ", " + getZusatz() + ", " + getAdresse() + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ getPlz() + " " + getOrt() + " " + getTelefon(); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
