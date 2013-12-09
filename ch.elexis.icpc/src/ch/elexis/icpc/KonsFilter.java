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

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.actions.ObjectFilterRegistry.IObjectFilterProvider;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.icpc.views.EpisodesView;

public class KonsFilter implements IObjectFilterProvider, IFilter {
	Episode mine;
	EpisodesView home;
	boolean bDaemfung;
	
	public KonsFilter(final EpisodesView home){
		this.home = home;
	}
	
	public void setProblem(final Episode problem){
		mine = problem;
		ElexisEventDispatcher.reload(Konsultation.class); // TODO why?
	}
	
	public void activate(){
		bDaemfung = true;
		home.activateKonsFilterAction(true);
		bDaemfung = false;
	}
	
	public void changed(){
		// should we mind?
	}
	
	public void deactivate(){
		bDaemfung = true;
		home.activateKonsFilterAction(false);
		bDaemfung = false;
	}
	
	public IFilter getFilter(){
		return this;
	}
	
	public String getId(){
		return "ch.elexis.icpc.konsfilter";
	}
	
	public boolean select(final Object toTest){
		if (mine == null) {
			return true;
		}
		if (toTest instanceof Konsultation) {
			Konsultation k = (Konsultation) toTest;
			List<Encounter> list =
				new Query<Encounter>(Encounter.class, "EpisodeID", mine.getId()).execute();
			for (Encounter enc : list) {
				if (enc.get("KonsID").equals(k.getId())) {
					return true;
				}
			}
		}
		return false;
	}
}
