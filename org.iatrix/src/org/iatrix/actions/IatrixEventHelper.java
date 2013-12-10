/*******************************************************************************
 * Copyright (c) 2007-2010, Praxis Dr. med. Peter Schönbucher and Medelexis AG
 * 
 * All rights reserved.
 * This code must not be used without permission of the copyright holder.
 * Contributors:
 *    D. Lutz - Initial implementation
 * 
 *******************************************************************************/
package org.iatrix.actions;

import org.iatrix.data.Problem;

import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.icpc.Episode;

public class IatrixEventHelper {
	/**
	 * Select a problem by selecting the corresponding episode.
	 * 
	 * @param problem
	 *            the problem to be selected
	 */
	public static void fireSelectionEventProblem(Problem problem){
		if (problem != null) {
			Episode episode = Episode.load(problem.getId());
			ElexisEventDispatcher.fireSelectionEvent(episode);
		}
	}
	
	/**
	 * Convert Problem to Episode and send update event for Episode
	 * 
	 * @param problem
	 *            the problem to send an update event for
	 */
	public static void updateProblem(Problem problem){
		if (problem != null) {
			Episode episode = Episode.load(problem.getId());
			ElexisEventDispatcher.update(episode);
		}
	}
	
	/**
	 * Get the currently selected problem. Actually looks for the selected episode and converts it
	 * to a problem.
	 * 
	 * @return the selected problem
	 */
	public static Problem getSelectedProblem(){
		Episode episode = (Episode) ElexisEventDispatcher.getSelected(Episode.class);
		return Problem.convertEpisodeToProblem(episode);
	}
	
	/**
	 * Make sure no Problem is selected. Actually deselects the corresponding Episode.
	 */
	public static void clearSelectionProblem(){
		ElexisEventDispatcher.clearSelection(Episode.class);
	}
}
