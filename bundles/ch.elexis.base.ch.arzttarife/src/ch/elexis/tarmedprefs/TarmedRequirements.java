/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.tarmedprefs;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;
import static ch.elexis.core.constants.XidConstants.DOMAIN_RECIPIENT_EAN;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.importer.TrustCenters;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IXid;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.data.Xid;

public class TarmedRequirements {
	
	public static final String INSURANCE_NUMBER = Messages.TarmedRequirements_InsuranceNumberName;
	public static final String CASE_NUMBER = Messages.TarmedRequirements_CaseNumberName;
	public static final String INTERMEDIATE = Messages.TarmedRequirements_IntermediateName;
	public static final String ACCIDENT_NUMBER = Messages.TarmedRequirements_AccidentNumberName;
	public final static String SSN = Messages.TarmedRequirements_SSNName;
	public static final String EAN_PSEUDO = "2000000000000"; //$NON-NLS-1$
	public static final String EAN_PROVIDER = "ean_provider"; //$NON-NLS-1$
	public static final String EAN_RESPONSIBLE = "ean_responsible"; //$NON-NLS-1$
	public static final String EAN_PATTERN = "[0-9]{13}"; //$NON-NLS-1$
	
	public static final String ACCIDENT_DATE = Messages.TarmedRequirements_AccidentDate;
	public static final String CASE_LAW = Messages.TarmedRequirements_Law;
	
	public static final String BILLINGSYSTEM_NAME = "TarmedLeistung"; //$NON-NLS-1$
	public static final String OUTPUTTER_NAME = "Tarmed-Drucker"; //$NON-NLS-1$
	
	public static final String DOMAIN_KSK = "www.xid.ch/id/ksk"; //$NON-NLS-1$
	public static final String DOMAIN_NIF = "www.xid.ch/id/nif"; //$NON-NLS-1$
	public static final String DOMAIN_SUVA = "www.xid.ch/id/suva"; // $NON-NLS-1$
	
	public static final String RESPONSIBLE_INFO_KEY = "ch.elexis.tarmedprefs.responsible";
	private static Logger logger = LoggerFactory.getLogger(TarmedRequirements.class);
	
	static {
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_KSK, Messages.TarmedRequirements_kskName,
			Xid.ASSIGNMENT_REGIONAL);
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_NIF, Messages.TarmedRequirements_NifName,
			Xid.ASSIGNMENT_REGIONAL);
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_RECIPIENT_EAN, "rEAN", //$NON-NLS-1$
			Xid.ASSIGNMENT_REGIONAL);
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_SUVA, "Suva-Nr", Xid.ASSIGNMENT_REGIONAL);
	}
	
	public static String getEAN(final IContact contact){
		if (contact == null) {
			return null;
		}
		IXid xid = contact.getXid(DOMAIN_EAN);
		// compatibility layer
		if (xid == null) {
			if (contact.getExtInfo("EAN") instanceof String
				&& StringUtils.isNotBlank((String) contact.getExtInfo("EAN"))) {
				setEAN(contact, (String) contact.getExtInfo("EAN"));
				xid = contact.getXid(DOMAIN_EAN);
			}
		}
		if (xid != null && xid.getDomainId() != null && !xid.getDomainId().isEmpty()) {
			return xid.getDomainId().trim();
		} else if (xid == null) {
			return EAN_PSEUDO;
		}
		return "";
	}
	
	public static String getRecipientEAN(final IContact contact){
		if (contact == null) {
			return null;
		}
		IXid ret = contact.getXid(DOMAIN_RECIPIENT_EAN);
		if (ret == null || ret.getDomainId() == null || ret.getDomainId().isEmpty()) {
			return "unknown"; //$NON-NLS-1$
		}
		return ret.getDomainId().trim();
	}
	
	public static String getSuvaNr(final IContact k){
		IXid ret = k.getXid(DOMAIN_SUVA);
		return ret != null ? ret.getDomainId() : "";
	}
	
	/**
	 * Get EAN of the Intermediate where the bill shpould be sent. This must be a Fall-requirement
	 * as defined in INTERMEDIATE and must contain the EAN
	 * 
	 * @param fall
	 * @return the intermediate EAN as defined or the empty String (never null)
	 */
	public static String getIntermediateEAN(final ICoverage coverage){
		return CoverageServiceHolder.get().getRequiredString(coverage, INTERMEDIATE).trim();
	}
	
	/**
	 * get ean_provider from Fall
	 * 
	 * @param fall
	 * @return the EAN or "unknown" if no valid ean was provided
	 */
	public static String getProviderEAN(final ICoverage coverage){
		String ean = CoverageServiceHolder.get().getRequiredString(coverage, EAN_PROVIDER).trim();
		if (!ean.matches("(20[0-9]{11}|76[0-9]{11}|unknown|[A-Z][0-9]{6})")) { //$NON-NLS-1$
			return "unknown"; //$NON-NLS-1$
		}
		return ean;
	}
	
	/**
	 * get ean_responsible from Fall
	 * 
	 * @param fall
	 * @return the EAN or "unknown" if no valid ean was provided
	 */
	public static String getResponsibleEAN(final ICoverage coverage){
		String ean =
			CoverageServiceHolder.get().getRequiredString(coverage, EAN_RESPONSIBLE).trim();
		if (!ean.matches("(20[0-9]{11}|76[0-9]{11}|unknown|[A-Z][0-9]{6})")) { //$NON-NLS-1$
			return "unknown"; //$NON-NLS-1$
		}
		return ean;
	}
	
	/**
	 * wandelt KSK's von der G123456-Schreibweise in die G 1234.56 Schreibweise um und umgekehrt
	 * 
	 * @param KSK
	 *            die KSK, welche aus exakt einem Buchstaben, exakt 6 Ziffern und optional exakt
	 *            einem Leerzeichen nach dem Buchstaben und einem Punkt vor den letzten beiden
	 *            Ziffern besteht.
	 * @return bei bCompact true eine KSK wie G123456, sonst eine wie G 1234.56
	 */
	public static String normalizeKSK(String KSK, boolean bCompact){
		if (!KSK.matches("[a-zA-Z] ?[0-9]{4,4}\\.?[0-9]{2,2}")) { //$NON-NLS-1$
			return "invalid"; //$NON-NLS-1$
		}
		KSK = KSK.replaceAll("[^a-zA-Z0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (bCompact) {
			return KSK;
		}
		KSK = KSK.substring(0, 1) + " " + KSK.substring(1, 5) + "." + KSK.substring(5); //$NON-NLS-1$ //$NON-NLS-2$
		return KSK.trim();
	}
	
	public static String getKSK(final IContact contact){
		IXid xid = contact.getXid(DOMAIN_KSK);
		// compatibility layer
		if (xid == null) {
			if (contact.getExtInfo("KSK") instanceof String
				&& StringUtils.isNotBlank((String) contact.getExtInfo("KSK"))) {
				setKSK(contact, (String) contact.getExtInfo("KSK"));
				xid = contact.getXid(DOMAIN_KSK);
			}
		}
		return xid != null ? xid.getDomainId().replaceAll("[\\s\\.\\-]", "").trim() : ""; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getNIF(final IContact contact){
		IXid xid = contact.getXid(DOMAIN_NIF);
		// compatibility layer
		if (xid == null) {
			if (contact.getExtInfo("NIF") instanceof String
				&& StringUtils.isNotBlank((String) contact.getExtInfo("NIF"))) {
				setNIF(contact, (String) contact.getExtInfo("NIF"));
				xid = contact.getXid(DOMAIN_NIF);
			}
		}
		return xid != null ? xid.getDomainId().trim() : "";
	}
	
	public static boolean setEAN(final IContact k, final String ean){
		if (!ean.matches("[0-9]{13,13}")) { //$NON-NLS-1$
			return false;
		}
		k.addXid(DOMAIN_EAN, ean, true);
		return true;
	}
	
	public static void setKSK(final IContact k, final String ksk){
		k.addXid(DOMAIN_KSK, ksk, true);
	}
	
	public static void setNIF(final IContact k, final String nif){
		k.addXid(DOMAIN_NIF, nif, true);
	}
	
	public static void setSuvaNr(final IContact k, final String SuvaNr){
		k.addXid(DOMAIN_SUVA, SuvaNr, true);
	}
	
	public static String getAHV(final IPerson p){
		IXid ahv = p.getXid(DOMAIN_AHV);
		String ret = ahv != null ? ahv.getDomainId() : "";
		if (ret.length() == 0) {
			ret = StringUtils.defaultString((String) p.getExtInfo(SSN));
			if (ret.length() == 0) {
				ret = StringUtils.defaultString((String) p.getExtInfo(INSURANCE_NUMBER));
			}
			if (ret.length() > 0) {
				setAHV(p, ret);
			}
		}
		return ret.trim();
	}
	
	public static void setAHV(final IPerson p, final String ahv){
		p.addXid(DOMAIN_AHV, ahv, true);
	}
	
	public static String getGesetz(final ICoverage coverage){
		String ret = coverage.getBillingSystem().getLaw().name();
		if (ret.equalsIgnoreCase("IV")) {
			ret = "IVG";
		} else if (ret.equalsIgnoreCase("MV")) {
			ret = "MVG";
		}
		return ret;
	}
	
	public static String getTCName(IContact mandant){
		if (mandant != null) {
			String tc = (String) mandant.getExtInfo(PreferenceConstants.TARMEDTC);
			return tc;
		}
		return null;
	}
	
	public static String getTCCode(IContact mandant){
		String tcname = getTCName(mandant);
		Integer nr = TrustCenters.tc.get(tcname);
		if (nr == null) {
			return "00"; //$NON-NLS-1$
		}
		return Integer.toString(nr);
	}
	
	public static void setTC(IContact mandant, String value){
		mandant.setExtInfo(PreferenceConstants.TARMEDTC, value);
	}
	
	public static void setHasTCContract(IContact mandant, boolean value){
		mandant.setExtInfo(PreferenceConstants.USETC, value ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean hasTCContract(IContact mandant){
		if (mandant != null) {
			String hc = (String) mandant.getExtInfo(PreferenceConstants.USETC);
			return "1".equals(hc); //$NON-NLS-1$
		}
		return false;
	}
}
