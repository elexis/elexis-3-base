/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui.charts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;

/**
 * <p>
 * Creates Dataset for ConsultationNumberChart.
 * </p>
 *
 * $Id: ConsultationNumberDatasetCreator.java 747 2009-07-23 09:14:53Z
 * peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ConsultationNumberDatasetCreator extends AbstractDatasetCreator {

	private static final String DATE_DB_FORMAT = "yyyyMMdd";
	private static final String DATE_CONS_FORMAT = "dd.MM.yyyy";

	private boolean isEmpty;

	/**
	 * @param jobName
	 */
	public ConsultationNumberDatasetCreator(String jobName) {
		super(jobName);
		this.isEmpty = true;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public IStatus createContent(final IProgressMonitor monitor) {
		// Date formats
		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);
		final SimpleDateFormat consultationFormat = new SimpleDateFormat(DATE_CONS_FORMAT);

		final Query<Konsultation> query = new Query<Konsultation>(Konsultation.class);

		// now, corrected by end of last month
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, -1);
		now.set(Calendar.DAY_OF_MONTH, now.getMaximum(Calendar.DAY_OF_MONTH));

		// then, corrected by begining of that month
		Calendar before = Calendar.getInstance();
		before.add(Calendar.MONTH, -7);
		before.set(Calendar.DAY_OF_MONTH, before.getMinimum(Calendar.DAY_OF_MONTH));

		query.add("Datum", ">=", databaseFormat.format(before.getTime()));
		query.add("Datum", "<", databaseFormat.format(now.getTime()));
		query.add("MandantID", "=", CoreHub.actMandant.getId());

		monitor.subTask("querying database");
		final List<Konsultation> consults = query.execute();

		this.isEmpty = consults.size() <= 0;

		// Size * 2, going over consultations twice.
		monitor.beginTask("doing calculations", query.size() * 2);

		// Consultations map, grouping consultations by month
		final TreeMap<Month, ArrayList<Konsultation>> consultsMap = new TreeMap<Month, ArrayList<Konsultation>>();

		// Group consults
		for (Konsultation consult : consults) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			try {
				Date date = consultationFormat.parse(consult.getDatum());
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				// Create month
				Month month = new Month(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));

				if (consultsMap.containsKey(month)) {
					consultsMap.get(month).add(consult);
				} else {
					ArrayList<Konsultation> consultList = new ArrayList<Konsultation>(1);
					consultList.add(consult);
					consultsMap.put(month, consultList);
				}

				monitor.worked(1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// Create dataset
		TimeSeries countSeries = new TimeSeries("Count", Month.class);

		// Compute money values for grouped consultations
		for (Entry<Month, ArrayList<Konsultation>> entry : consultsMap.entrySet()) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			Month month = entry.getKey();

			int count = 0;

			count = count + entry.getValue().size();
			monitor.worked(entry.getValue().size());

			countSeries.add(month, count);
		}

		this.dataset = new TimeSeriesCollection();
		((TimeSeriesCollection) this.dataset).addSeries(countSeries);

		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected boolean isDatasetEmpty() {
		return this.isEmpty;
	}
}