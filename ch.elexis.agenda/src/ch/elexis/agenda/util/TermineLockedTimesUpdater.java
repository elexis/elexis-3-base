/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.TimeTool.DAYS;

public class TermineLockedTimesUpdater implements IRunnableWithProgress {
	
	private TimeTool _startDate;
	private DAYS _applyForDay;
	private String _newValues;
	private String _bereich;
	
	/**
	 * An updater for setting the new reserved time slots starting from a specific date. It updates
	 * all dates which are of day selectedDay and also checks whether there is already an
	 * appointment which would be blocked by this.
	 * 
	 * @param startChangeDate
	 *            start to change the reserved timeslots from this date
	 * @param selectedDay
	 *            updated only every e.g. tuesday, wednesday etc.
	 * @param reservedSlots
	 *            the newly assigned reserved slots
	 * @param terminBereich
	 *            the specific calendar "bereich" to update as there may exist several calendars
	 */
	public TermineLockedTimesUpdater(TimeTool startChangeDate, DAYS selectedDay,
		String reservedSlots, String terminBereich){
		_startDate = startChangeDate;
		_applyForDay = selectedDay;
		_newValues = reservedSlots;
		_bereich = terminBereich;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
		InterruptedException{
		String[] closedTimes = _newValues.split(StringConstants.LF);
		
		List<String> skipUpdate = new ArrayList<String>();
		
		Query<Termin> qbe = new Query<Termin>(Termin.class);
		qbe.add(Termin.FLD_BEREICH, Query.LIKE, _bereich);
		// den richtigen Bereich selektieren!
		List<Termin> qre = qbe.execute();
		monitor.beginTask(Messages.TermineLockedTimesUpdater_0, 2 * qre.size());
		
		// First we check, whether any dates clash
		for (Termin t : qre) {
			if (t.getId().equals("1")) //$NON-NLS-1$
				continue;
			if (t.getDay() == null || t.getDay().length() < 3)
				continue;
			TimeTool day = new TimeTool(t.getDay());
			if (_startDate.isBeforeOrEqual(day)) {
				if (day.get(Calendar.DAY_OF_WEEK) == _applyForDay.numericDayValue) {
					if (!t.getType().equals(Termin.typReserviert())) {
						// If we have a non reserved date, check whether it clashes with the
						// new closed times
						for (String s : closedTimes) {
							int von = TimeTool.minutesStringToInt(s.split("-")[0]); //$NON-NLS-1$
							int bis = TimeTool.minutesStringToInt(s.split("-")[1]); //$NON-NLS-1$
							boolean clash = t.crossesTimeFrame(von, bis - von);
							if (clash) {
								boolean doNotUpdate =
									MessageDialog.openQuestion(PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell(),
										Messages.TermineLockedTimesUpdater_4,
										Messages.TermineLockedTimesUpdater_5 + t.getLabel()
											+ Messages.TermineLockedTimesUpdater_6 + s + ". "
											+ Messages.TermineLockedTimesUpdater_7);
								if (doNotUpdate)
									skipUpdate.add(t.getDay());
							}
						}
					}
				}
			}
			monitor.worked(1);
		}
		
		for (Termin t : qre) {
			TimeTool day = new TimeTool(t.getDay());
			if (_startDate.isBeforeOrEqual(day)) {
				if (t.getType().equals(Termin.typReserviert())) {
					// if no clash or accepted by user, delete the entry
					if (skipUpdate.contains(t.getDay()))
						continue;
					t.delete();
				}
			}
			monitor.worked(1);
		}
		
		monitor.done();
		
	}
}
