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
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.TimeTool.DAYS;

/**
 * Update the configured day boundaries on all given week days from a specific
 * start date. Update means only delete the existing boundaries, as the new
 * boundaries will be initialized when the day is accessed.
 *
 */
public class TermineLockedTimesUpdater implements IRunnableWithProgress {

	private TimeTool _startDate;
	private DAYS _applyForDay;
	private String _newValues;
	private String _bereich;

	/**
	 * An updater for setting the new reserved time slots starting from a specific
	 * date. It updates all dates which are of day selectedDay and also checks
	 * whether there is already an appointment which would be blocked by this.
	 *
	 * @param startChangeDate start to change the reserved timeslots from this date
	 * @param selectedDay     updated only every e.g. tuesday, wednesday etc.
	 * @param reservedSlots   the newly assigned reserved slots
	 * @param terminBereich   the specific calendar "bereich" to update as there may
	 *                        exist several calendars
	 */
	public TermineLockedTimesUpdater(TimeTool startChangeDate, DAYS selectedDay, String reservedSlots,
			String terminBereich) {
		_startDate = startChangeDate;
		_applyForDay = selectedDay;
		_newValues = reservedSlots;
		_bereich = terminBereich;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		// select all appointments from the area of interest
		Query<Termin> qbe = new Query<Termin>(Termin.class);
		qbe.add(Termin.FLD_BEREICH, Query.LIKE, _bereich);
		List<Termin> appointments = qbe.execute();

		monitor.beginTask(Messages.TermineLockedTimesUpdater_0, 2 * appointments.size());
		List<String> skipUpdate = checkAppointmentCollision(appointments, monitor);

		TimeTool day = new TimeTool();
		// delete existing boundaries if we should not keep them on that day
		for (Termin t : appointments) {
			if (skipUpdate.contains(t.getDay())) {
				continue;
			}
			day.set(t.getDay());
			if (day.get(Calendar.DAY_OF_WEEK) != _applyForDay.numericDayValue) {
				continue;
			}

			if (_startDate.isBeforeOrEqual(day)) {
				if (t.getType().equals(Termin.typReserviert())) {
					t.delete();
				}
			}
			monitor.worked(1);
		}
		monitor.done();

		ContextServiceHolder.get().sendEvent(ElexisEventTopics.EVENT_INVALIDATE_CACHE, IAppointment.class);
	}

	/**
	 * check whether any appointments collide with the lock time changes
	 *
	 * @param appointments
	 * @param monitor
	 * @return list of days to skip when deleting the old boundaries
	 */
	private List<String> checkAppointmentCollision(List<Termin> appointments, IProgressMonitor monitor) {
		List<String> skipUpdate = new ArrayList<String>();
		String[] closedTimes = _newValues.split(StringConstants.LF);
		TimeTool day = new TimeTool();

		for (Termin t : appointments) {
			if (t.getId().equals(StringConstants.ONE))
				continue;
			if (t.getDay() == null || t.getDay().length() < 3)
				continue;

			day.set(t.getDay());
			if (_startDate.isBeforeOrEqual(day)) {
				if (day.get(Calendar.DAY_OF_WEEK) == _applyForDay.numericDayValue) {
					// ignore locktimes
					if (!t.getType().equals(Termin.typReserviert())) {
						for (String s : closedTimes) {
							int von = TimeTool.minutesStringToInt(s.split("-")[0]); //$NON-NLS-1$
							int bis = TimeTool.minutesStringToInt(s.split("-")[1]); //$NON-NLS-1$

							// check for collision
							if (t.crossesTimeFrame(von, bis - von)) {
								boolean keepOldLocktimes = MessageDialog.openQuestion(
										PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										Messages.TermineLockedTimesUpdater_4,
										Messages.TermineLockedTimesUpdater_5 + t.getLabel()
												+ Messages.TermineLockedTimesUpdater_6 + s + ". "
												+ Messages.TermineLockedTimesUpdater_7);

								// update anyway -> add appointment to delete list
								if (keepOldLocktimes)
									skipUpdate.add(t.getDay());
							}
						}
					}
				}
			}
			monitor.worked(1);
		}
		return skipUpdate;
	}
}
