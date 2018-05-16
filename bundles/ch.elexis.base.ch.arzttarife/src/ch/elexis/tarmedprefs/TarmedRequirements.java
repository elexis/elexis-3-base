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

import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Person;
import ch.elexis.data.TrustCenters;
import ch.elexis.data.Xid;

public class TarmedRequirements {
	
	public static final String INSURANCE = Messages
		.getString("TarmedRequirements.KostentraegerName"); //$NON-NLS-1$
	public static final String INSURANCE_NUMBER = Messages
		.getString("TarmedRequirements.InsuranceNumberName"); //$NON-NLS-1$
	public static final String CASE_NUMBER = Messages
		.getString("TarmedRequirements.CaseNumberName"); //$NON-NLS-1$
	public static final String INTERMEDIATE = Messages
		.getString("TarmedRequirements.IntermediateName"); //$NON-NLS-1$
	public static final String ACCIDENT_NUMBER = Messages
		.getString("TarmedRequirements.AccidentNumberName"); //$NON-NLS-1$
	public final static String SSN = Messages.getString("TarmedRequirements.SSNName"); //$NON-NLS-1$
	public static final String EAN_PSEUDO = "2000000000000"; //$NON-NLS-1$
	public static final String EAN_PROVIDER = "ean_provider"; //$NON-NLS-1$
	public static final String EAN_RESPONSIBLE = "ean_responsible"; //$NON-NLS-1$
	public static final String EAN_PATTERN = "[0-9]{13}";
	
	public static final String ACCIDENT_DATE = Messages
		.getString("TarmedRequirements.AccidentDate"); //$NON-NLS-1$
	public static final String CASE_LAW = Messages.getString("TarmedRequirements.Law"); //$NON-NLS-1$
	
	public static final String BILLINGSYSTEM_NAME = "TarmedLeistung";
	public static final String OUTPUTTER_NAME = "Tarmed-Drucker";
	
	public static final String DOMAIN_KSK = "www.xid.ch/id/ksk"; //$NON-NLS-1$
	public static final String DOMAIN_NIF = "www.xid.ch/id/nif"; //$NON-NLS-1$
	public static final String DOMAIN_SUVA = "www.xid.ch/id/suva"; // $NON-NLS-1$
	
	public static final String RESPONSIBLE_INFO_KEY = "ch.elexis.tarmedprefs.responsible";

	static {
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_KSK,
			Messages.getString("TarmedRequirements.kskName"), Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_NIF,
			Messages.getString("TarmedRequirements.NifName"), Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_RECIPIENT_EAN,
			"rEAN", Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_SUVA, "Suva-Nr", Xid.ASSIGNMENT_REGIONAL);
	}
	
	public static String getEAN(final Kontakt k){
		if (k == null) {
			return null;
		}
		String ret = k.getXid(DOMAIN_EAN);
		// compatibility layer
		if (ret.length() == 0) {
			ret = k.getInfoString("EAN"); //$NON-NLS-1$
			if (ret.length() > 0) {
				setEAN(k, ret);
			}
		}
		// end
		if (ret.length() == 0) {
			ret = EAN_PSEUDO;
		}
		return ret.trim();
	}
	
	public static String getRecipientEAN(final Kontakt k){
		String ret = k.getXid(DOMAIN_RECIPIENT_EAN);
		if (ret.length() == 0) {
			ret = "unknown"; //$NON-NLS-1$
		}
		return ret.trim();
	}
	
	public static String getSuvaNr(final Kontakt k){
		String ret = k.getXid(DOMAIN_SUVA);
		return ret;
	}
	
	/**
	 * Get EAN of the Intermediate where the bill shpould be sent. This must be a Fall-requirement
	 * as defined in INTERMEDIATE and must contain the EAN
	 * 
	 * @param fall
	 * @return the intermediate EAN as defined or the empty String (never null)
	 */
	public static String getIntermediateEAN(final Fall fall){
		return fall.getRequiredString(INTERMEDIATE).trim();
	}
	
	/**
	 * get ean_provider from Fall
	 * 
	 * @param fall
	 * @return the EAN or "unknown" if no valid ean was provided
	 */
	public static String getProviderEAN(final Fall fall){
		String ean = fall.getRequiredString(EAN_PROVIDER).trim();
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
	public static String getResponsibleEAN(final Fall fall){
		String ean = fall.getRequiredString(EAN_RESPONSIBLE).trim();
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
	
	public static String getKSK(final Kontakt k){
		String ret = k.getXid(DOMAIN_KSK);
		// compatibility layer
		if (ret.length() == 0) {
			ret = k.getInfoString("KSK"); //$NON-NLS-1$
			if (ret.length() > 0) {
				setKSK(k, ret);
			}
		}
		// end
		return ret.replaceAll("[\\s\\.\\-]", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getNIF(final Kontakt k){
		String ret = k.getXid(DOMAIN_NIF);
		// compatibility layer
		if (ret.length() == 0) {
			ret = k.getInfoString("NIF"); //$NON-NLS-1$
			if (ret.length() > 0) {
				setNIF(k, ret);
			}
		}
		// end
		return ret.trim();
	}
	
	public static boolean setEAN(final Kontakt k, final String ean){
		if (!ean.matches("[0-9]{13,13}")) { //$NON-NLS-1$
			return false;
		}
		k.addXid(DOMAIN_EAN, ean, true);
		return true;
	}
	
	public static void setKSK(final Kontakt k, final String ksk){
		k.addXid(DOMAIN_KSK, ksk, true);
	}
	
	public static void setNIF(final Kontakt k, final String nif){
		k.addXid(DOMAIN_NIF, nif, true);
	}
	
	public static void setSuvaNr(final Kontakt k, final String SuvaNr){
		k.addXid(DOMAIN_SUVA, SuvaNr, true);
	}
	
	public static String getAHV(final Person p){
		String ahv = p.getXid(DOMAIN_AHV);
		if (ahv.length() == 0) {
			ahv = p.getInfoString(SSN);
			if (ahv.length() == 0) {
				ahv = p.getInfoString(INSURANCE_NUMBER);
			}
			if (ahv.length() > 0) {
				setAHV(p, ahv);
			}
		}
		return ahv.trim();
	}
	
	public static void setAHV(final Person p, final String ahv){
		p.addXid(DOMAIN_AHV, ahv, true);
	}
	
	public static String getGesetz(final Fall fall){
		return fall.getConfiguredBillingSystemLaw().name();
	}
	
	public static String getTCName(Kontakt mandant){
		String tc = mandant.getInfoString(PreferenceConstants.TARMEDTC);
		return tc;
	}
	
	public static String getTCCode(Kontakt mandant){
		String tcname = getTCName(mandant);
		Integer nr = TrustCenters.tc.get(tcname);
		if (nr == null) {
			return "00"; //$NON-NLS-1$
		}
		return Integer.toString(nr);
	}
	
	public static void setTC(Kontakt mandant, String tc){
		mandant.setInfoElement(PreferenceConstants.TARMEDTC, tc);
	}
	
	public static boolean hasTCContract(Kontakt mandant){
		String hc = (String) mandant.getInfoElement(PreferenceConstants.USETC);
		return "1".equals(hc); //$NON-NLS-1$
	}
}
