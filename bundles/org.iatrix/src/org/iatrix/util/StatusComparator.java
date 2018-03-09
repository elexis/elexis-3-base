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

import java.util.Comparator;

import org.iatrix.data.Problem;

import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.Episode;

/**
 * Compare by status. ACTIVE problems are sorted before INACTIVE problems. Problems with same
 * status are sorted by date.
 *
 * @author danlutz
 */
public class StatusComparator implements Comparator<Problem> {
	@Override
	public int compare(Problem o1, Problem o2){
		if (o1 == null && o2 == null) {
			return 0;
		}

		if (o1 == null) {
			return 1;
		}

		if (o2 == null) {
			return -1;
		}

		int status1 = o1.getStatus();
		int status2 = o2.getStatus();

		if (status1 == status2) {
			// same status, compare date
			return PersistentObject.checkNull(o1.getStartDate())
				.compareTo(PersistentObject.checkNull(o2.getStartDate()));
		} else if (status1 == Episode.ACTIVE) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public boolean equals(Object obj){
		return super.equals(obj);
	}
}