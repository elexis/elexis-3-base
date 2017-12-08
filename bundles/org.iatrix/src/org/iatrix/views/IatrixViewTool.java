/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.views;

import java.util.List;

import org.iatrix.data.Problem;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Konsultation;
import ch.elexis.icpc.Encounter;

/**
 * Utilitiy methods for views
 *
 * @author Daniel Lutz <danlutz@schoenbucher.ch>
 *
 */
public class IatrixViewTool {
	/**
	 * Remove problem from konsultation, i. e. delete enconter. Ask user for confirmation if
	 * encounter contains data.
	 *
	 * @param problem
	 *            the problem to remove from the konsultation
	 * @param konsultation
	 *            the konsultation which the problem is to be removed from
	 * @return true, if encounter has been removed, false otherwise
	 */
	public static boolean removeProblemFromKonsultation(Konsultation konsultation, Problem problem){
		boolean removed = false;

		// check for existing encounters with data

		// TODO: prefer problem.getEncounter. We use getEncounters for backwards compatibility
		List<Encounter> encounters = problem.getEncounters(konsultation);
		boolean hasData = false;
		for (Encounter encounter : encounters) {
			if (encounter.getRFE() != null || encounter.getDiag() != null
				|| encounter.getProc() != null) {

				// there's at least one encounter containing data
				hasData = true;
				break;
			}
		}
		if (hasData) {
			if (SWTHelper.askYesNo("Encounter mit Daten exisitert",
				"Dieser Konsultation ist ein Enconter zugeordnet, in dem Daten erfasst sind."
					+ " Soll dieser Encounter gelöscht werden? (Die erfassten Daten gehen verloren.)")) {

				problem.removeFromKonsultation(konsultation);
				removed = true;
			}
			// Note: in case of canceling this operation, the following
			// call of updateProblemAssignmentViewer will re-eneable the checkbox.

		} else {
			// no data available, we can safely delete this counter
			problem.removeFromKonsultation(konsultation);
			removed = true;
		}

		return removed;
	}
}
