/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.model.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.model.Identifiable;
import ch.elexis.data.Artikel;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;

public class Vaccination extends PersistentObject {
	
	public static final String TABLENAME = "AT_MEDEVIT_ELEXIS_IMPFPLAN";
	static final String VERSION = "1.0.0";
	
	//@formatter:off
	public static final String FLD_PATIENT_ID = "Patient_ID";
	public static final String FLD_ARTIKEL_REF = "Artikel_REF";
	/** Handelsname */	public static final String FLD_BUSS_NAME = "BusinessName";
	/** EAN Code */		public static final String FLD_EAN = "ean";
	/** ATC Code */		public static final String FLD_ATCCODE = "ATCCode";
	/** Chargen-No */	public static final String FLD_LOT_NO ="lotnr";	
	/** Verabr. Datum*/	public static final String FLD_DOA = "dateOfAdministration";
	/** Verabreicher, entweder ein Mandant im lokalen System, oder ein Kontakt-String */	
						public static final String FLD_ADMINISTRATOR = "administrator";
	/** Impfung gegen */public static final String FLD_VACC_AGAINST = "vaccAgainst";
	/** side where vaccination was applied (optional)*/	
						public static final String SIDE = "Side";
	
	/** Definition of the database table */
	static final String createDB =
		"CREATE TABLE " + TABLENAME
			+ "("
			+ "ID VARCHAR(25) primary key," // has to be of type varchar else version.exists fails
			+ "lastupdate BIGINT,"
			+ "deleted CHAR(1) default '0'," // will never be set to 1
			+ FLD_PATIENT_ID +" VARCHAR(25),"	
			+ FLD_ARTIKEL_REF +" VARCHAR(255),"	
			+ FLD_BUSS_NAME +" VARCHAR(255),"
			+ FLD_EAN +" VARCHAR(13),"
			+ FLD_ATCCODE +" VARCHAR(20),"
			+ FLD_LOT_NO +" VARCHAR(255),"
			+ FLD_DOA +" CHAR(8),"
			+ FLD_ADMINISTRATOR +" VARCHAR(255),"
			+ FLD_VACC_AGAINST +" VARCHAR(255),"
			+ PersistentObject.FLD_EXTINFO + " BLOB"
			+ "); "

			+ "INSERT INTO " + TABLENAME + " (ID,"+FLD_PATIENT_ID+","+FLD_DOA+") VALUES ('"+StringConstants.VERSION_LITERAL+"',"
			+ JdbcLink.wrap(VERSION) +","+new TimeTool().toString(TimeTool.DATE_COMPACT)+");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE,
			FLD_LOT_NO, FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST, PersistentObject.FLD_EXTINFO);
	}
	
	Vaccination(){}
	
	protected Vaccination(String id){
		super(id);
	}
	
	public static Vaccination load(String id){
		return new Vaccination(id);
	}
	
	public Vaccination(final String patientId, final Artikel a, final Date doa, final String lotNo,
		final String mandantId){
		this(patientId, a.storeToString(), a.getLabel(), a.getEAN(), a.getATC_code(), doa, lotNo,
			mandantId);
	}
	
	public Vaccination(final String patientId, final String articleStoreToString,
		final String articleLabel, final String articleEAN, final String articleATCCode,
		final Date doa, final String lotNo, final String mandantId){
		
		this(patientId, articleStoreToString, articleLabel, articleEAN, articleATCCode,
			(new TimeTool(doa)).toString(TimeTool.DATE_COMPACT), lotNo, mandantId);
	}
	
	public Vaccination(final String patientId, final String articleStoreToString,
		final String articleLabel, final String articleEAN, final String articleATCCode,
		final String doa, final String lotNo, final String mandantId){
		
		create(null);
		
		String vaccAgainst = "";
		if (articleATCCode != null) {
			vaccAgainst = StringUtils
				.join(ArticleToImmunisationModel.getImmunisationForAtcCode(articleATCCode), ",");
		}
		
		String[] fields = new String[] {
			FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE, FLD_LOT_NO,
			FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST
		};
		String[] vals = new String[] {
			patientId, articleStoreToString, articleLabel, articleEAN, articleATCCode, lotNo, doa,
			mandantId, vaccAgainst
		};
		set(fields, vals);
	}
	
	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE,
			FLD_LOT_NO, FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST, PersistentObject.FLD_EXTINFO);
		Vaccination version = load(StringConstants.VERSION_LITERAL); //$NON-NLS-1$
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			// update code
		}
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	@Override
	public String getLabel(){
		return getDateOfAdministration().toString(TimeTool.DATE_COMPACT) + " " + getBusinessName()
			+ " (" + getLotNo() + ") - " + getAdministratorLabel();
	}
	
	public TimeTool getDateOfAdministration(){
		return new TimeTool(get(FLD_DOA));
	}
	
	public void setDateOfAdministration(Date tt){
		TimeTool ttd = new TimeTool(tt);
		set(FLD_DOA, ttd.toString(TimeTool.DATE_COMPACT));
	}
	
	public String getDateOfAdministrationLabel(){
		String doa = get(FLD_DOA);
		if (doa.endsWith("0000")) {
			return doa.substring(0, doa.length() - 4);
		}
		TimeTool ttDoA = new TimeTool(doa);
		return ttDoA.toString(TimeTool.DATE_GER);
	}
	
	public String getBusinessName(){
		return get(FLD_BUSS_NAME);
	}
	
	public String getShortBusinessName(){
		String businessName = get(FLD_BUSS_NAME);
		if (businessName.contains("(")) {
			return businessName.substring(0, businessName.indexOf("("));
		}
		return businessName;
	}
	
	public String getLotNo(){
		return get(FLD_LOT_NO);
	}
	
	public String getAtcCode(){
		return get(FLD_ATCCODE);
	}
	
	public String getPatientId(){
		return get(FLD_PATIENT_ID);
	}
	
	/**
	 * @return a human-readable label of the person that administered the vaccine
	 */
	public @NonNull String getAdministratorLabel(){
		String value = get(FLD_ADMINISTRATOR);
		if (value.startsWith(Mandant.class.getName()) || value.startsWith(Person.class.getName())) {
			Mandant mandant = loadMandant(value);
			
			if (mandant == null) {
				return "";
			}
			
			String title = Person.load(mandant.getId()).get(Person.TITLE);
			if (title == null || title.isEmpty()) {
				return mandant.getName() + " " + mandant.getVorname();
			}
			return title + " " + mandant.getName() + " " + mandant.getVorname();
		} else {
			if (value == null || value.length() < 2)
				return "";
				
			return value;
		}
	}
	
	private Mandant loadMandant(String value){
		Optional<Identifiable> mandator =
			StoreToStringServiceHolder.get().loadFromString(value);
		if (mandator.isPresent()) {
			return Mandant.load(mandator.get().getId());
		}
		return null;
	}
	
	public boolean isSupplement(){
		String value = get(FLD_ADMINISTRATOR);
		if (value.startsWith(Mandant.class.getName()) || value.startsWith(Person.class.getName())) {
			Mandant mandant = loadMandant(value);
			
			if (mandant != null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Find all vaccinations with a specific lotNo
	 * 
	 * @param lotNo
	 *            number to look for
	 * @return list of vaccinations matching the lotNo
	 */
	public static List<Vaccination> findByLotNo(String lotNo){
		Query<Vaccination> qbe = new Query<Vaccination>(Vaccination.class);
		qbe.clear(true);
		qbe.add(FLD_LOT_NO, Query.EQUALS, lotNo);
		return qbe.execute();
	}
	
	public void setVaccAgainst(String vaccAgainst){
		set(FLD_VACC_AGAINST, vaccAgainst);
	}
	
	public List<String> getVaccAgainstList(){
		List<String> vaccAgainst = new ArrayList<String>();
		String vaccAgaisntString = get(FLD_VACC_AGAINST);
		String[] split = vaccAgaisntString.split(",");
		for (String va : split) {
			vaccAgainst.add(va);
		}
		return vaccAgainst;
	}
	
	public void setAdministratorString(String administrator){
		set(FLD_ADMINISTRATOR, administrator);
	}
	
	public void setLotNo(String lotNo){
		set(FLD_LOT_NO, lotNo);
	}
	
	public String getSide(){
		return checkNull(getExtInfoStoredObjectByKey(SIDE));
	}
	
	public void setSide(final String side){
		setExtInfoStoredObjectByKey(SIDE, side);
	}
}
