/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.data;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class GDTProtokoll extends PersistentObject {

	public static final String FLD_DATETIME = "DateTime"; //$NON-NLS-1$
	public static final String FLD_PATIENT_ID = "PatientID"; //$NON-NLS-1$
	public static final String FLD_BEZEICHNUNG = "Bezeichnung"; //$NON-NLS-1$
	public static final String FLD_BEMERKUNGEN = "Bemerkungen"; //$NON-NLS-1$
	public static final String FLD_MESSAGE_TYPE = "MessageType"; //$NON-NLS-1$
	public static final String FLD_MESSAGE_DIRECTION = "MessageDirection"; //$NON-NLS-1$
	public static final String FLD_GEGENSTELLE = "Remote"; //$NON-NLS-1$
	public static final String FLD_MESSAGE = "Message"; //$NON-NLS-1$

	public static final String MESSAGE_DIRECTION_IN = "IN"; //$NON-NLS-1$
	public static final String MESSAGE_DIRECTION_OUT = "OUT"; //$NON-NLS-1$

	static final String TABLENAME = "at_medevit_elexis_gdt_protokoll"; //$NON-NLS-1$

	static {
		addMapping(TABLENAME, FLD_DATETIME, FLD_PATIENT_ID, FLD_BEZEICHNUNG, FLD_BEMERKUNGEN, FLD_MESSAGE_TYPE,
				FLD_MESSAGE_DIRECTION, FLD_GEGENSTELLE, FLD_MESSAGE);
	}

	GDTProtokoll() {
	}

	protected GDTProtokoll(String id) {
		super(id);
	}

	public <U extends GDTSatzNachricht> GDTProtokoll(String messageDirection, IGDTCommunicationPartner cp,
			U satzNachricht) {
		create(null);

		Patient pat = Patient.loadByPatientID(satzNachricht.getValue(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG));

		StringBuilder sb = new StringBuilder();
		String[] message = satzNachricht.getMessage();
		for (int i = 0; i < message.length; i++) {
			sb.append(message[i]);
		}

		set(new String[] { FLD_DATETIME, FLD_PATIENT_ID, FLD_MESSAGE_TYPE, FLD_MESSAGE_DIRECTION, FLD_GEGENSTELLE,
				FLD_MESSAGE, FLD_BEZEICHNUNG }, new TimeTool().toString(TimeTool.TIMESTAMP),
				(pat != null) ? pat.getId() : "nicht zugeordnet", //$NON-NLS-1$
				satzNachricht.getValue(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION), messageDirection, cp.getLabel(),
				sb.toString(), satzNachricht.getValue(GDTConstants.FELDKENNUNG_TEST_IDENT));

		int satznachrichtType = Integer.parseInt(satzNachricht.getValue(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION));
		if (messageDirection.equalsIgnoreCase(GDTProtokoll.MESSAGE_DIRECTION_OUT)) {
			switch (satznachrichtType) {
			case GDTConstants.SATZART_UNTERSUCHUNG_ANFORDERN:
				set(FLD_BEZEICHNUNG, "Anforderung Untersuchung: "
						+ satzNachricht.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD));
				break;
			case GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_ZEIGEN:
				set(FLD_BEZEICHNUNG, "Anzeige Untersuchung: " //$NON-NLS-1$
						+ satzNachricht.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD));
				break;
			case GDTConstants.SATZART_STAMMDATEN_UEBERMITTELN:
				set(FLD_BEZEICHNUNG, "Stammdaten übermittelt");
				break;
			default:
				break;
			}
		}
		if (messageDirection.equalsIgnoreCase(GDTProtokoll.MESSAGE_DIRECTION_IN)) {
			switch (satznachrichtType) {
			case GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN:
				set(FLD_BEZEICHNUNG, "Resultat Untersuchung: "
						+ satzNachricht.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD));
				break;

			default:
				break;
			}
		}
	}

	public static GDTProtokoll load(String id) {
		return new GDTProtokoll(id);
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		TimeTool tt = new TimeTool(get(FLD_DATETIME));
		Patient pat = Patient.loadByPatientID(get(FLD_PATIENT_ID));
		sb.append(tt.toString(TimeTool.DATE_GER) + ": " + pat.getLabel() + StringUtils.SPACE + FLD_MESSAGE_TYPE //$NON-NLS-1$
				+ StringUtils.SPACE + FLD_MESSAGE_DIRECTION);
		return sb.toString();
	}

	public String getMenuLabel() {
		StringBuilder sb = new StringBuilder();
		TimeTool tt = new TimeTool(get(FLD_DATETIME));

		sb.append(tt.toString(TimeTool.DATE_GER) + ": " + get(FLD_BEZEICHNUNG) + StringUtils.SPACE + "[" //$NON-NLS-1$ //$NON-NLS-2$
				+ get(FLD_GEGENSTELLE) + "]"); //$NON-NLS-1$
		return sb.toString();
	}

	public static <U extends GDTSatzNachricht> GDTProtokoll addEntry(String messageDirection,
			IGDTCommunicationPartner cp, U satzNachricht) {
		return new GDTProtokoll(messageDirection, cp, satzNachricht);
	}

	public static GDTProtokoll[] getAllEntries() {
		Query<GDTProtokoll> qbe = new Query<GDTProtokoll>(GDTProtokoll.class);
		qbe.add("ID", Query.NOT_EQUAL, "VERSION"); //$NON-NLS-1$ //$NON-NLS-2$
		List<GDTProtokoll> qre = qbe.execute();
		return qre.toArray(new GDTProtokoll[] {});
	}

	/**
	 * 
	 * @param pat
	 * @param remoteName  if not <code>null</code> included as search filter
	 * @param messageType if not <code>null</code> included as search filter
	 * @return
	 */
	public static List<GDTProtokoll> getEntriesForPatient(String patientId, @Nullable String remoteName,
			@Nullable String messageType) {
		if (patientId != null) {
			Query<GDTProtokoll> qbe = new Query<GDTProtokoll>(GDTProtokoll.class, FLD_PATIENT_ID, patientId, TABLENAME,
					new String[] { FLD_MESSAGE_TYPE, FLD_MESSAGE_DIRECTION, FLD_DATETIME });
			if (remoteName != null) {
				qbe.add(GDTProtokoll.FLD_GEGENSTELLE, Query.EQUALS, remoteName);
			}
			if (messageType != null) {
				qbe.add(GDTProtokoll.FLD_MESSAGE_TYPE, Query.EQUALS, messageType);
			}
			return qbe.execute();
		}
		return Collections.emptyList();
	}

	public String getMessageDirection() {
		return get(FLD_MESSAGE_DIRECTION);
	}

	public TimeTool getEntryTime() {
		return new TimeTool(get(FLD_DATETIME));
	}

	/**
	 * @return value of {@link GDTConstants#FELDKENNUNG_SATZIDENTIFIKATION}
	 */
	public String getMessageType() {
		return get(FLD_MESSAGE_TYPE);
	}

	public Patient getEntryRelatedPatient() {
		return Patient.load(get(FLD_PATIENT_ID));
	}

	public String getBezeichnung() {
		return get(FLD_BEZEICHNUNG);
	}

	public void setBezeichnung(String bezeichnung) {
		set(FLD_BEZEICHNUNG, bezeichnung);
	}

	public String getBemerkungen() {
		return get(FLD_BEMERKUNGEN);
	}

	public void setBemerkungen(String bemerkung) {
		set(FLD_BEMERKUNGEN, bemerkung);
	}

	public String getGegenstelle() {
		return get(FLD_GEGENSTELLE);
	}

	public String getMessage() {
		return get(FLD_MESSAGE);
	}
}
