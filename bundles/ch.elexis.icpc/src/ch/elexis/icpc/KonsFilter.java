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

package ch.elexis.icpc;

import java.util.List;

import org.eclipse.jface.viewers.IFilter;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.actions.ObjectFilterRegistry.IObjectFilterProvider;
import ch.elexis.data.Konsultation;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.icpc.IcpcPackage;
import ch.elexis.icpc.service.IcpcModelServiceHolder;
import ch.elexis.icpc.views.EpisodesView;

public class KonsFilter implements IObjectFilterProvider, IFilter {
	IcpcEpisode mine;
	EpisodesView home;
	boolean bDaemfung;

	public KonsFilter(final EpisodesView home) {
		this.home = home;
	}

	public void setProblem(final IcpcEpisode problem) {
		mine = problem;
	}

	public void activate() {
		bDaemfung = true;
		home.activateKonsFilterAction(true);
		bDaemfung = false;
	}

	public void changed() {
		// should we mind?
	}

	public void deactivate() {
		bDaemfung = true;
		home.activateKonsFilterAction(false);
		bDaemfung = false;
	}

	public IFilter getFilter() {
		return this;
	}

	public String getId() {
		return "ch.elexis.icpc.konsfilter";
	}

	public boolean select(final Object toTest) {
		if (mine == null) {
			return true;
		}
		if (toTest instanceof Konsultation) {
			IEncounter encounter = CoreModelServiceHolder.get().load(((Konsultation) toTest).getId(), IEncounter.class)
					.orElse(null);
			if (encounter != null) {
				return mineHasEncounter(encounter);
			}
		}
		if (toTest instanceof IEncounter) {
			IEncounter encounter = (IEncounter) toTest;
			if (encounter != null) {
				return mineHasEncounter(encounter);
			}
		}
		return false;
	}

	private boolean mineHasEncounter(IEncounter encounter) {
		IQuery<IcpcEncounter> query = IcpcModelServiceHolder.get().getQuery(IcpcEncounter.class);
		query.and(IcpcPackage.Literals.ICPC_ENCOUNTER__EPISODE, COMPARATOR.EQUALS, mine);
		List<IcpcEncounter> list = query.execute();
		for (IcpcEncounter enc : list) {
			if (enc.getEncounter().equals(encounter)) {
				return true;
			}
		}
		return false;
	}
}
