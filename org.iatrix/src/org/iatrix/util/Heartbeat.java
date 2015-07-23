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

import java.util.concurrent.CopyOnWriteArrayList;

import org.iatrix.Iatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.UiDesk;

/**
 * A small wrapper around the Elexis HeartListener which may be globally disabled via the
 * org.iatrix preferences.
 *
 * @author niklaus
 *
 */
public class Heartbeat implements HeartListener {

	private static Logger log = LoggerFactory.getLogger(Heartbeat.class);
	private boolean heartbeatActive = false;
	private boolean heartbeatProblemEnabled = true;
	private CopyOnWriteArrayList<IatrixHeartListener> iatrixListener;
	private static Heartbeat theHeartbeat;

	private Heartbeat(){
		iatrixListener = new CopyOnWriteArrayList<IatrixHeartListener>();
	}

	/**
	 * Das Singleton holen
	 *
	 * @return den Heartbeat der Anwendung
	 */
	public static Heartbeat getInstance(){
		if (theHeartbeat == null) {
			theHeartbeat = new Heartbeat();
		}
		return theHeartbeat;
	}

	public void enableListener(boolean mode){
		if (mode) {
			CoreHub.heart.addListener(this);

		} else {

			CoreHub.heart.removeListener(this);
		}
	}

	/**
	 * Allow KTableCellEditor to temporarily suppress heartbeat
	 *
	 * @param value
	 */
	public void setHeartbeatProblemEnabled(boolean value){
		log.debug("setHeartbeatProblemEnabled" + heartbeatProblemEnabled);
		heartbeatProblemEnabled = value;
	}

	/**
	 * Trigger the hearbeat Should only be called from one place in JournalView
	 */
	@Override
	public void heartbeat(){
		// don't run while another heartbeat is currently processed
		if (heartbeatActive) {
			return;
		}

		heartbeatActive = true;
		UiDesk.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run(){
				log.debug("run for " + iatrixListener.size() + " listeners");
				notifyListeners();
			}
		});

		heartbeatActive = false;
	}

	/**
	 * Return the auto-save time period interval, as configured in CoreHub.userCfg
	 *
	 * @return the calculated period interval, or 1 if there are invalid configuration values, or 0
	 *         if autos-save is disabled
	 */
	public int getKonsTextSaverPeriod(){
		int timePeriod =
			CoreHub.userCfg.get(Iatrix.CFG_AUTO_SAVE_PERIOD, Iatrix.CFG_AUTO_SAVE_PERIOD_DEFAULT);
		if (timePeriod == 0) {
			// no auto-save
			return 0;
		}

		log.debug("TimePeriod: " + timePeriod);
		int heartbeatInterval =
			CoreHub.localCfg.get(ch.elexis.core.constants.Preferences.ABL_HEARTRATE, 30);
		if (heartbeatInterval > 0 && timePeriod >= heartbeatInterval) {
			int period = timePeriod / heartbeatInterval;
			if (period > 0) {
				return period;
			} else {
				// shouldn't occur...
				return 1;
			}
		} else {
			// shouldn't occur...
			return 1;
		}
	}

	private void notifyListeners(){
		for (IatrixHeartListener name : iatrixListener) {
			name.heartbeat();
		}
	}

	public void addListener(IatrixHeartListener newListener){
		iatrixListener.add(newListener);
	}

	public interface IatrixHeartListener {
		/**
		 * Die Methode heartbeat wird in "einigermassen" regelmässigen (aber nicht garantiert immer
		 * genau identischen) Abständen aufgerufen
		 *
		 */
		public void heartbeat();
	}
}
