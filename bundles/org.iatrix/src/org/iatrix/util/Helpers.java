/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.iatrix.data.Problem;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionedResource;
import ch.rgw.tools.VersionedResource.ResourceItem;

public class Helpers {

	/**
	 * used by selectionEvent(PersistentObject obj)
	 */
	public static Konsultation getTodaysLatestKons(Fall fall){
		Konsultation result = null;
		TimeTool today = new TimeTool();

		Konsultation[] konsultationen = fall.getBehandlungen(true);
		if (konsultationen != null) {
			// find the latest Konsultation according to the text entry's timestamp
			long timestamp = -1;
			for (Konsultation k : konsultationen) {
				if (new TimeTool(k.getDatum()).isSameDay(today)) {
					VersionedResource vr = k.getEintrag();
					if (vr != null) {
						ResourceItem ri = vr.getVersion(vr.getHeadVersion());
						if (ri != null) {
							if (ri.timestamp > timestamp) {
								timestamp = ri.timestamp;
								result = k;
							}
						}
					}
				}
			}
		}
		return result;
	}

	private static final DateComparator DATE_COMPARATOR = new DateComparator();

	/**
	 * Copies the most important data to the clipboard, suitable to import into other text.
	 * Includes: - patient data (name, birthdate, address, phone numbers) - list of problems
	 * including medicals (all or selected only) - the latest consultations
	 */
	public static String exportToClipboard(Patient patient, Problem p){
		final int NUMBER_OF_CONS = 1;

		String lineSeparator = System.getProperty("line.separator");
		String fieldSeparator = "\t";

		String clipboardText = "";

		if (patient != null) {
			StringBuffer output = new StringBuffer();

			// get list of selected problems
			List<Problem> problems = new ArrayList<>();
			System.out.println("TODO problemAssignmentViewer"); // TODO: ngngng
			/*
			Problem p = getSelectedProblem();
*/
			if (p != null) {
				problems.add(p);
			}
			// patient label
			StringBuffer patientLabel = new StringBuffer();
			patientLabel.append(patient.getName() + " " + patient.getVorname());
			patientLabel.append(" (");
			patientLabel.append(patient.getGeschlecht());
			patientLabel.append("), ");
			patientLabel.append(patient.getGeburtsdatum());
			patientLabel.append(", ");

			output.append(patientLabel);

			// patient address
			StringBuffer patientAddress = new StringBuffer();
			Anschrift anschrift = patient.getAnschrift();
			patientAddress.append(anschrift.getStrasse());
			patientAddress.append(", ");
			patientAddress.append(anschrift.getPlz() + " " + anschrift.getOrt());
			patientAddress.append(lineSeparator);

			output.append(patientAddress);

			// patient phone numbers
			boolean isFirst = true;
			StringBuffer patientPhones = new StringBuffer();
			String telefon1 = patient.get("Telefon1");
			String telefon2 = patient.get("Telefon2");
			String natel = patient.get("Natel");
			String eMail = patient.get("E-Mail");
			if (!StringTool.isNothing(telefon1)) {
				if (isFirst) {
					isFirst = false;
				} else {
					patientPhones.append(", ");
				}
				patientPhones.append("T: ");
				patientPhones.append(telefon1);
				if (!StringTool.isNothing(telefon2)) {
					patientPhones.append(", ");
					patientPhones.append(telefon2);
				}
			}
			if (!StringTool.isNothing(natel)) {
				if (isFirst) {
					isFirst = false;
				} else {
					patientPhones.append(", ");
				}
				patientPhones.append("M: ");
				patientPhones.append(natel);
			}
			if (!StringTool.isNothing(natel)) {
				if (isFirst) {
					isFirst = false;
				} else {
					patientPhones.append(", ");
				}
				patientPhones.append(eMail);
			}
			patientPhones.append(lineSeparator);

			output.append(patientPhones);
			output.append(lineSeparator);

			// consultations
			List<Konsultation> konsultationen = new ArrayList<>();

			if (problems.size() > 0) {
				// get consultations of selected problems
				for (Problem problem : problems) {
					konsultationen.addAll(problem.getKonsultationen());
				}
			} else {
				// get all consultations
				for (Fall fall : patient.getFaelle()) {
					for (Konsultation k : fall.getBehandlungen(false)) {
						konsultationen.add(k);
					}
				}
			}

			// sort list of consultations in reverse order, get the latest ones
			Collections.sort(konsultationen, new Comparator<Konsultation>() {
				@Override
				public int compare(Konsultation k1, Konsultation k2){
					String d1 = k1.getDatum();
					String d2 = k2.getDatum();

					if (d1 == null) {
						return 1;
					}
					if (d2 == null) {
						return -1;
					}

					TimeTool date1 = new TimeTool(d1);
					TimeTool date2 = new TimeTool(d2);

					// reverse order
					return -(date1.compareTo(date2));
				}
			});

			for (int i = 0; i < NUMBER_OF_CONS && konsultationen.size() >= (i + 1); i++) {
				Konsultation konsultation = konsultationen.get(i);

				// output
				StringBuffer sb = new StringBuffer();

				sb.append(konsultation.getLabel());

				List<Problem> konsProblems = Problem.getProblemsOfKonsultation(konsultation);
				if (konsProblems != null && konsProblems.size() > 0) {
					StringBuffer problemsLabel = new StringBuffer();
					problemsLabel.append(" (");
					// first problem in list
					problemsLabel.append(konsProblems.get(0).getTitle());
					for (int j = 1; j < konsProblems.size(); j++) {
						// further problems in list
						problemsLabel.append(", ");
						problemsLabel.append(konsProblems.get(j).getTitle());
					}
					problemsLabel.append(")");

					sb.append(problemsLabel);
				}

				sb.append(lineSeparator);
				Samdas samdas = new Samdas(konsultation.getEintrag().getHead());
				sb.append(samdas.getRecordText());
				sb.append(lineSeparator);
				sb.append(lineSeparator);

				output.append(sb);
			}

			if (problems.size() == 0) {
				List<Problem> allProblems = Problem.getProblemsOfPatient(patient);
				if (allProblems != null) {
					problems.addAll(allProblems);
				}
			}

			Collections.sort(problems, DATE_COMPARATOR);

			StringBuffer problemsText = new StringBuffer();

			problemsText.append("Persönliche Anamnese");
			problemsText.append(lineSeparator);

			for (Problem problem : problems) {
				String date = problem.getStartDate();
				String text = problem.getTitle();

				List<String> therapy = new ArrayList<>();
				String procedure = problem.getProcedere();
				if (!StringTool.isNothing(procedure)) {
					therapy.add(procedure.trim());
				}

				List<Prescription> prescriptions = problem.getPrescriptions();
				for (Prescription prescription : prescriptions) {
					if (prescription != null && prescription.getArtikel() != null) {
						StringBuffer label = new StringBuffer(prescription.getArtikel().getLabel());
						if (label != null) {
							label.append(" (" + prescription.getDosis() + ")");
						}
						therapy.add(label.toString().trim());
					} else {
						therapy.add("Kein Artikel");
					}

				}

				StringBuffer sb = new StringBuffer();
				sb.append(date);
				sb.append(fieldSeparator);
				sb.append(text);
				sb.append(fieldSeparator);

				if (!therapy.isEmpty()) {
					// first therapy entry
					sb.append(therapy.get(0));
				}
				sb.append(lineSeparator);

				// further therapy entries
				if (therapy.size() > 1) {
					for (int i = 1; i < therapy.size(); i++) {
						sb.append(fieldSeparator);
						sb.append(fieldSeparator);
						sb.append(therapy.get(i));
						sb.append(lineSeparator);
					}
				}

				problemsText.append(sb);
			}

			output.append(problemsText);

			clipboardText = output.toString();
		}

		Clipboard clipboard = new Clipboard(UiDesk.getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] transfers = new Transfer[] {
			textTransfer
		};
		Object[] data = new Object[] {
			clipboardText
		};
		clipboard.setContents(data, transfers);
		clipboard.dispose();
		return clipboardText;
	}
	/**
	 * Compare two consultations to see whether have belong to the same patient
	 * @param thisKons
	 * @param otherKons
	 * @return both null || both same getId()
	 */
	public static boolean twoKonsSamePatient(Konsultation thisKons, Konsultation otherKons) {
		if (thisKons == null && otherKons == null ) {
			return true;
		}
		if (thisKons == null || otherKons == null ) {
			return false;
		}
		return thisKons.getFall().getPatient().getId().equals(
			otherKons.getFall().getPatient().getId());
	}

	/**
	 * Compare two consultations to see whether have the same id
	 * @param thisKons
	 * @param otherKons
	 * @return both null || both same getId()
	 */
	public static boolean twoKonsEqual(Konsultation thisKons, Konsultation otherKons) {
		if (thisKons == null && otherKons == null ) {
			return true;
		}
		if (thisKons == null || otherKons == null ) {
			return false;
		}
		return thisKons.getId().equals(otherKons.getId());
	}

	/**
	 * Compare two consultations to see whether they are from the same day
	 * and have the same consultation text
	 * @param thisKons
	 * @param otherKons
	 * @return sameKonsId AND same konsText
	 */
	public static boolean haveSameContent(Konsultation thisKons, Konsultation otherKons) {
		if (thisKons == null && otherKons == null ) {
			return true;
		}
		if (thisKons == null || otherKons == null ) {
			return false;
		}
		if (!thisKons.getId().equals(otherKons.getId()))  {
			return false;
		}
		if (!thisKons.getDatum().equals(otherKons.getDatum())) {
			return false;
		}
		if ( thisKons.getEintrag() == null && otherKons.getEintrag() == null) {
			return true;
		}
		if ( thisKons.getEintrag() == null || otherKons.getEintrag() == null) {
			return false;
		}
		ResourceItem thisResource = thisKons.getEintrag().getVersion(thisKons.getHeadVersion());
		ResourceItem otherResource = otherKons.getEintrag().getVersion(otherKons.getHeadVersion());
		if (thisResource == null && otherResource == null) {
			return true;
		}
		if (thisResource == null || otherResource == null) {
			return false;
		}
		String thisText = thisResource.data;
		String otherText = otherResource.data;
		if (thisText.equals(otherText))
		{
			return true;
		}
		return false;
	}

	/**
	 * Normally, the thext of a Konsultation may only be changed, if the Konsultation has not yet
	 * been billed. Due to customer demand, this was weakened: A User can have the right
	 * ADMIN_KONS_EDIT_IF_BILLED and then can edit all Konsultations, even billed ones.
	 * @param kons the cons to be checked
	 * @param showError display a message if not editable
	 * @return user can edit the cons
	 */
	public static boolean hasRightToChangeConsultations(Konsultation kons, boolean showError){
		return CoreHub.acl.request(AccessControlDefaults.ADMIN_KONS_EDIT_IF_BILLED) || kons.isEditable(showError);
	}
	boolean hasRight = CoreHub.acl.request(AccessControlDefaults.ADMIN_KONS_EDIT_IF_BILLED);

	/**
	 * Return an explanation a ADMIN_KONS_EDIT_IF_BILLED
	 * @return Whether user has the right + how to change it
	 */
	public static String getExplantionForKonsEditIfBillet(){
		StringBuilder explanation = new StringBuilder("");
		if (CoreHub.acl.request(AccessControlDefaults.ADMIN_KONS_EDIT_IF_BILLED) ) {
			explanation.append("Sie haben das Recht verrechnete Konsultation zu ändern.");
		} else {
			explanation.append("Sie dürfen verrechnete Konsultation nicht ändern.");
		}
		explanation.append("\nDas Ändern von verrechneten Konsultation kann in den Einstellungen geändert werden");
		explanation.append("\nUnter Gruppen und Rechten..");
		explanation.append("\n  Rollen und Rechten");
		explanation.append("\n    Recht: main:Konsultations");
		explanation.append("\n      Verrechnete ändern");
		explanation.append("\nkönnen Sie den gewünschten Gruppen diese Recht erteilen");
		explanation.append("\nDieses Recht kann sinnvoll sein, wenn Sie eine Konsultation ");
		explanation.append("noch editieren wollen, nachdem sie von der MPA schon verrechnet wurde");
		return explanation.toString();
	}

	/**
	 * Allowed combinations of actPat/actKons are:
	 * actPat == null, actKons == null when starting up or no patient selected
	 * actPat != null, actKons != null when working inside KG iatrix and a kons is selected
	 * actPat != null, actKons == null  KG Iatrix not active, eg. before dropping something into Verrechnung
	 * forbidden is: actPat == null &&  actKons  != nullW
	 *
	 * @return false if actPat == null &&  actKons  != null
	 */
	public static boolean checkActPatKons(Patient actPat, Konsultation actKons) {
		if (actPat == null &&  actKons  != null) {
			System.out.println(String.format("Inkonsistent state of actPat %s and actKons %s", actPat, actKons));
			return false;
		}
		return true;
	}
	/**
	 * @param konsultation the concerned consultations
	 * 
	 * @return whether user may is the author of this consultation
	 */
	public static boolean userIsKonsAuthor(Konsultation kons){
		return kons.getAuthor().isEmpty() || kons.getAuthor().contentEquals(CoreHub.actUser.getLabel()) ;
	}
	/**
	 * @param konsultation the concerned consultations
	 * 
	 * @return whether user may edit this consultation
	 */
	public static boolean userMayEditKons(Konsultation kons){
		return userIsKonsAuthor(kons) && hasRightToChangeConsultations(kons, false) ;
	}
}
