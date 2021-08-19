/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.TarmedRechnung;

/**
 * Für die Tarmed-Rechnung spezifische Zugriffsrechte auf Mandantendaten einrichten.
 * 
 * @author Gerry
 * 
 */
public class TarmedACL {
	public final String RNBANK = "TarmedRnBank"; //$NON-NLS-1$
	public final String RNACCOUNTOWNER = "TarmedRnAccountOwner"; //$NON-NLS-1$
	public final String ESR5OR9 = "TarmedESR5OrEsr9"; //$NON-NLS-1$
	public final String ESRPLUS = "TarmedESRPlus"; //$NON-NLS-1$
	public final String ESRNUMBER = "TarmedESRParticipantNumber"; //$NON-NLS-1$
	public final String ESRSUB = "TarmedESRIdentity"; //$NON-NLS-1$
	public final String TIERS = "TarmedTiersGarantOrPayant"; //$NON-NLS-1$
	public final String SPEC = "TarmedSpezialität"; //$NON-NLS-1$
	public final String KANTON = "TarmedKanton"; //$NON-NLS-1$
	public final String LOCAL = "TarmedErbringungsOrt"; //$NON-NLS-1$
	public final String DIAGSYS = "TarmedDiagnoseSystem"; //$NON-NLS-1$
	
	/*
	 * public final String PRINTER_EZ="Drucker mit ESR-Papier"; public final String
	 * TRAY_EZ="Schacht mit ESR-Papier"; public final String
	 * PRINTER_RN="Drucker mit weissem Papier"; public final String
	 * TRAY_RN="Schacht mit weissem Papier";
	 */
	private final static String W = "Write"; //$NON-NLS-1$
	private final static String R = "Read"; //$NON-NLS-1$
	
	private static TarmedACL theInstance;
	
	private TarmedACL(){
		/*
		 * Hub.acl.grantForSelf(W+RNFRIST,W+TXRECHN,W+TXMAHN1,W+TXMAHN2,W+TXMAHN3,W+RNBANK,W+ESR5OR9,
		 * W+ESRPLUS , W+ESRNUMBER,W+ESRSUB,W+TIERS,W+SPEC,W+KANTON,W+LOCAL,W+DIAGSYS);
		 * Hub.acl.grant("Anwender",R+
		 * RNFRIST,R+TXRECHN,R+TXMAHN1,R+TXMAHN2,R+TXMAHN3,R+RNBANK,R+ESR5OR9,R+ESRPLUS,
		 * //$NON-NLS-1$ R+ESRNUMBER,R+ESRSUB,R+TIERS,R+SPEC,R+KANTON,R+LOCAL,R+DIAGSYS);
		 */
	}
	
	@Override
	protected void finalize() throws Throwable{
		/*
		 * Hub.acl.revokeFromSelf(W+RNFRIST,W+TXRECHN,W+TXMAHN1,W+TXMAHN2,W+TXMAHN3,
		 * W+RNBANK,W+ESR5OR9,W+ESRPLUS,W+ESRNUMBER,W+ESRSUB,W+TIERS,W+SPEC,
		 * W+KANTON,W+LOCAL,W+DIAGSYS);
		 * Hub.acl.revoke("Anwender",R+RNFRIST,R+TXRECHN,R+TXMAHN1,R+TXMAHN2,R+TXMAHN3,R+RNBANK,
		 * //$NON-NLS-1$ R+ESR5OR9,R+ESRPLUS,R+ESRNUMBER,R+ESRSUB,R+TIERS,R+SPEC,
		 * R+KANTON,R+LOCAL,R+DIAGSYS);
		 */
	}
	
	public static TarmedACL getInstance(){
		if (theInstance == null) {
			theInstance = new TarmedACL();
		}
		return theInstance;
	}
}
