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
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.model.Identifiable;
import ch.elexis.data.Artikel;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class Vaccination extends PersistentObject {

	public static final String TABLENAME = "AT_MEDEVIT_ELEXIS_IMPFPLAN"; //$NON-NLS-1$
	static final String VERSION = "1.0.0"; //$NON-NLS-1$

	//@formatter:off
	public static final String FLD_PATIENT_ID = "Patient_ID"; //$NON-NLS-1$
	public static final String FLD_ARTIKEL_REF = "Artikel_REF"; //$NON-NLS-1$
	/** Handelsname */	public static final String FLD_BUSS_NAME = "BusinessName"; //$NON-NLS-1$
	/** EAN Code */		public static final String FLD_EAN = "ean"; //$NON-NLS-1$
	/** ATC Code */		public static final String FLD_ATCCODE = "ATCCode"; //$NON-NLS-1$
	/** Chargen-No */	public static final String FLD_LOT_NO ="lotnr"; //$NON-NLS-1$
	/** Verabr. Datum*/	public static final String FLD_DOA = "dateOfAdministration"; //$NON-NLS-1$
	/** Verabreicher, entweder ein Mandant im lokalen System, oder ein Kontakt-String */
						public static final String FLD_ADMINISTRATOR = "administrator"; //$NON-NLS-1$
	/** Impfung gegen */public static final String FLD_VACC_AGAINST = "vaccAgainst"; //$NON-NLS-1$
	/** side where vaccination was applied (optional)*/
						public static final String SIDE = "Side"; //$NON-NLS-1$

	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE, FLD_LOT_NO, FLD_DOA,
				FLD_ADMINISTRATOR, FLD_VACC_AGAINST, PersistentObject.FLD_EXTINFO);
	}

	Vaccination() {
	}

	protected Vaccination(String id) {
		super(id);
	}

	public static Vaccination load(String id) {
		return new Vaccination(id);
	}

	public Vaccination(final String patientId, final Artikel a, final Date doa, final String lotNo,
			final String mandantId) {
		this(patientId, a.storeToString(), a.getLabel(), a.getEAN(), a.getATC_code(), doa, lotNo, mandantId);
	}

	public Vaccination(final String patientId, final String articleStoreToString, final String articleLabel,
			final String articleEAN, final String articleATCCode, final Date doa, final String lotNo,
			final String mandantId) {

		this(patientId, articleStoreToString, articleLabel, articleEAN, articleATCCode,
				(new TimeTool(doa)).toString(TimeTool.DATE_COMPACT), lotNo, mandantId);
	}

	public Vaccination(final String patientId, final String articleStoreToString, final String articleLabel,
			final String articleEAN, final String articleATCCode, final String doa, final String lotNo,
			final String mandantId) {

		create(null);

		String vaccAgainst = StringUtils.EMPTY;
		if (articleATCCode != null) {
			vaccAgainst = StringUtils.join(ArticleToImmunisationModel.getImmunisationForAtcCode(articleATCCode), ","); //$NON-NLS-1$
		}

		String[] fields = new String[] { FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE,
				FLD_LOT_NO, FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST };
		String[] vals = new String[] { patientId, articleStoreToString, articleLabel, articleEAN, articleATCCode, lotNo,
				doa, mandantId, vaccAgainst };
		set(fields, vals);
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	@Override
	public String getLabel() {
		return getDateOfAdministration().toString(TimeTool.DATE_COMPACT) + StringUtils.SPACE + getBusinessName() + " (" //$NON-NLS-1$
				+ getLotNo() + ") - " + getAdministratorLabel(); //$NON-NLS-1$
	}

	public TimeTool getDateOfAdministration() {
		return new TimeTool(get(FLD_DOA));
	}

	public void setDateOfAdministration(Date tt) {
		TimeTool ttd = new TimeTool(tt);
		set(FLD_DOA, ttd.toString(TimeTool.DATE_COMPACT));
	}

	public String getDateOfAdministrationLabel() {
		String doa = get(FLD_DOA);
		if (doa.endsWith("0000")) { //$NON-NLS-1$
			return doa.substring(0, doa.length() - 4);
		}
		TimeTool ttDoA = new TimeTool(doa);
		return ttDoA.toString(TimeTool.DATE_GER);
	}

	public String getBusinessName() {
		return get(FLD_BUSS_NAME);
	}

	public String getShortBusinessName() {
		String businessName = get(FLD_BUSS_NAME);
		if (businessName.contains("(")) { //$NON-NLS-1$
			return businessName.substring(0, businessName.indexOf("(")); //$NON-NLS-1$
		}
		return businessName;
	}

	public String getLotNo() {
		return get(FLD_LOT_NO);
	}

	public String getAtcCode() {
		return get(FLD_ATCCODE);
	}

	public String getPatientId() {
		return get(FLD_PATIENT_ID);
	}

	/**
	 * @return a human-readable label of the person that administered the vaccine
	 */
	public @NonNull String getAdministratorLabel() {
		String value = get(FLD_ADMINISTRATOR);
		if (value.startsWith("ch.elexis")) { //$NON-NLS-1$
			Mandant mandant = loadMandant(value);

			if (mandant == null) {
				return StringUtils.EMPTY;
			}

			String title = Person.load(mandant.getId()).get(Person.TITLE);
			if (title == null || title.isEmpty()) {
				return mandant.getName() + StringUtils.SPACE + mandant.getVorname();
			}
			return title + StringUtils.SPACE + mandant.getName() + StringUtils.SPACE + mandant.getVorname();
		} else {
			if (value == null || value.length() < 2)
				return StringUtils.EMPTY;

			return value;
		}
	}

	private Mandant loadMandant(String value) {
		Optional<Identifiable> mandator = StoreToStringServiceHolder.get().loadFromString(value);
		if (mandator.isPresent()) {
			return Mandant.load(mandator.get().getId());
		}
		return null;
	}

	public boolean isSupplement() {
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
	 * @param lotNo number to look for
	 * @return list of vaccinations matching the lotNo
	 */
	public static List<Vaccination> findByLotNo(String lotNo) {
		Query<Vaccination> qbe = new Query<Vaccination>(Vaccination.class);
		qbe.clear(true);
		qbe.add(FLD_LOT_NO, Query.EQUALS, lotNo);
		return qbe.execute();
	}

	public void setVaccAgainst(String vaccAgainst) {
		set(FLD_VACC_AGAINST, vaccAgainst);
	}

	public List<String> getVaccAgainstList() {
		List<String> vaccAgainst = new ArrayList<String>();
		String vaccAgaisntString = get(FLD_VACC_AGAINST);
		String[] split = vaccAgaisntString.split(","); //$NON-NLS-1$
		for (String va : split) {
			vaccAgainst.add(va);
		}
		return vaccAgainst;
	}

	public void setAdministratorString(String administrator) {
		set(FLD_ADMINISTRATOR, administrator);
	}

	public void setLotNo(String lotNo) {
		set(FLD_LOT_NO, lotNo);
	}

	public String getSide() {
		return checkNull(getExtInfoStoredObjectByKey(SIDE));
	}

	public void setSide(final String side) {
		setExtInfoStoredObjectByKey(SIDE, side);
	}
}
