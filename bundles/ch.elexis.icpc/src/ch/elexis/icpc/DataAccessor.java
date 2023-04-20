/*******************************************************************************
 * Copyright (c) 2011, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.data.interfaces.IDataAccess;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.icpc.IcpcPackage;
import ch.elexis.icpc.service.IcpcModelServiceHolder;
import ch.rgw.tools.Result;

public class DataAccessor implements IDataAccess {

	public String getName() {
		return "ICPC2-Daten";
	}

	public String getDescription() {
		return "Daten aus dem ICPC2-Plugin";
	}

	public List<Element> getList() {
		// TODO Auto-generated method stub
		return null;
	}

	private String code(IcpcCode ic) {
		return (ic == null ? StringUtils.EMPTY : ic.getCode());
	}

	/**
	 * Alle Encounters in der Episode ep suchen, die zum Zeitraum dates passen und
	 * Liste zurueck geben.
	 */
	private Result<Object> sucheEncounters(IcpcEpisode ep, String dates, Object dep) {
		IQuery<IcpcEncounter> query = IcpcModelServiceHolder.get().getQuery(IcpcEncounter.class);
		query.and(IcpcPackage.Literals.ICPC_ENCOUNTER__EPISODE, COMPARATOR.EQUALS, ep);
		if (dep instanceof Konsultation) {
			IEncounter encounter = CoreModelServiceHolder.get().load(((Konsultation) dep).getId(), IEncounter.class)
					.orElse(null);
			query.and(IcpcPackage.Literals.ICPC_ENCOUNTER__ENCOUNTER, COMPARATOR.EQUALS, encounter);
		}
		List<IcpcEncounter> encounters = query.execute();

		// TODO: Filtern nach dates

		// Encounters ohne RFE, Diag und Procedere rauswerfen
		encounters = encounters.parallelStream().filter(e -> filterEmptyEncounter(e)).collect(Collectors.toList());

		// Sortieren
		Collections.sort(encounters, new Comparator<IcpcEncounter>() {
			public int compare(IcpcEncounter e1, IcpcEncounter e2) {
				return e1.getEncounter().getDate().compareTo(e2.getEncounter().getDate());
			}
		});

		return new Result<Object>(encounters);
	}

	private boolean filterEmptyEncounter(IcpcEncounter encounter) {
		return encounter.getRfe() != null || encounter.getDiag() != null || encounter.getProc() != null;
	}

	private Result<Object> sucheEncounters(List<IcpcEpisode> eps, Map<IcpcEpisode, List<IcpcEncounter>> encs,
			Object dep, String dates) {

		IPatient pat;

		if (dep instanceof Konsultation) {
			IEncounter encounter = CoreModelServiceHolder.get().load(((Konsultation) dep).getId(), IEncounter.class)
					.orElse(null);
			pat = encounter.getPatient();
		} else if (dep instanceof Patient) {
			pat = CoreModelServiceHolder.get().load(((Patient) dep).getId(), IPatient.class).orElse(null);
		} else {
			return new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.INVALID_PARAMETERS, "Ungültiger Parameter",
					dep, true);
		}

		// Alle Episoden des Patienten zusammensuchen
		IQuery<IcpcEpisode> query = IcpcModelServiceHolder.get().getQuery(IcpcEpisode.class);
		query.and(IcpcPackage.Literals.ICPC_EPISODE__PATIENT, COMPARATOR.EQUALS, pat);
		query.orderBy(IcpcPackage.Literals.ICPC_EPISODE__START_DATE, ORDER.ASC);
		List<IcpcEpisode> raw_eps = query.execute();

		int count = 0;
		for (IcpcEpisode ep : raw_eps) {
			// Betroffene Encounters suchen
			Result<Object> res = sucheEncounters(ep, dates, dep);
			if (!res.isOK()) {
				return res;
			}

			// Falls mindestens ein Encounter gefunden wurde,
			// Episode in Liste aufnehmen.
			@SuppressWarnings("unchecked")
			List<IcpcEncounter> ep_encs = (List<IcpcEncounter>) res.get();
			if (ep_encs.size() > 0) {
				eps.add(ep);
				encs.put(ep, ep_encs);
				// Eine Zeile fuer die Episode + eine pro Encounter
				count += 1 + ep_encs.size();
			}
		}

		return new Result<Object>(count);
	}

	public Result<Object> getObject(String descriptor, PersistentObject dependentObject, String dates,
			String[] params) {

		if (descriptor.toLowerCase().equals("encounters")) { //$NON-NLS-1$
			/*
			 * Tabelle in der Form: Problem1 | Datum | aktiv/inaktiv | Datum | RFE |
			 * Diagnose | Procedere . . . . . Problem2 | Datum | aktiv/inaktiv . . . . .
			 */

			List<IcpcEpisode> episodes = new LinkedList<IcpcEpisode>();
			HashMap<IcpcEpisode, List<IcpcEncounter>> encounters = new HashMap<IcpcEpisode, List<IcpcEncounter>>();

			Result<Object> res = sucheEncounters(episodes, encounters, dependentObject, dates);
			if (!res.isOK()) {
				return res;
			}

			int rows = (Integer) res.get() + 1;
			String result[][] = new String[rows][5];
			int i = 0;

			// Spaltenüberschriften
			result[i][0] = "Problem";
			result[i][1] = "Datum";
			result[i][2] = "RFE";
			result[i][3] = "Diagnose";
			result[i][4] = "Procedere";
			i++;

			for (IcpcEpisode ep : episodes) {
				/* Zeile fuer Episode generieren */
				result[i][0] = ep.getTitle();
				result[i][1] = ep.getStartDate();
				result[i][2] = getStatusText(ep.getStatus());
				result[i][3] = result[i][4] = StringUtils.EMPTY;
				i++;

				/* Zeilen fuer Encounters generieren */
				for (IcpcEncounter en : encounters.get(ep)) {
					result[i][0] = StringUtils.EMPTY;
					result[i][1] = en.getEncounter().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); //$NON-NLS-1$
					result[i][2] = code(en.getRfe());
					result[i][3] = code(en.getDiag());
					result[i][4] = code(en.getProc());
					i++;
				}
			}

			return new Result<Object>(result);
		} else {
			return new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND, "Ungültiger Parameter",
					descriptor, true);
		}
	}

	private String getStatusText(int status) {
		if (status == 1) {
			return Messages.Active;
		} else {
			return Messages.Inactive;
		}
	}
}
