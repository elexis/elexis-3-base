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
package ch.unibe.iam.scg.archie.samples;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IService;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.Money;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.samples.i18n.Messages;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Consultation statistics, grouped by month. For each month, information about
 * the total time, costs and profits are given. Averages are computed too.
 * There's a parameter that can be set which turns the displaying of time
 * statistics on or off.
 * </p>
 * 
 * $Id: ConsultationTimeStats.java 766 2009-07-24 11:28:14Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 766 $
 */
public class ConsultationTimeStats extends AbstractTimeSeries {

	private static final String DATE_MONTH_FORMAT = "yyyy-MM";

	private boolean withTime;

	/**
	 * Constructs <code>ConsultationTimeStats</code>.
	 */
	public ConsultationTimeStats() {
		super(Messages.CONSULTATION_TIME_STATS_TITLE);
		this.withTime = true;
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		// result list
		final ArrayList<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>();

		// month to consultations map
		final TreeMap<String, ArrayList<IEncounter>> grouped = new TreeMap<>();

		// date formats
		final DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern(DATE_MONTH_FORMAT);

		// prepare query
		IQuery<IEncounter> query = CoreModelServiceHolder.get().getQuery(IEncounter.class);
		query.and(ModelPackage.Literals.IENCOUNTER__MANDATOR, COMPARATOR.EQUALS, ContextServiceHolder.get().getActiveMandator());
		query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.GREATER_OR_EQUAL,
				LocalDateTime.ofInstant(this.getStartDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.LESS_OR_EQUAL,
				LocalDateTime.ofInstant(this.getEndDate().toInstant(), ZoneId.systemDefault()).toLocalDate());

		final List<IEncounter> consults = query.execute();

		// define size and begin task
		monitor.beginTask(Messages.CALCULATING, consults.size());

		// do the light stuff...
		monitor.subTask("Grouping Consultations");
		for (final IEncounter consult : consults) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			ArrayList<IEncounter> consultGroup = new ArrayList<>();

			final String monthString = monthFormat.format(consult.getDate());
			if (grouped.get(monthString) != null) {
				consultGroup = grouped.get(monthString);
			}

			// add the current consult to this month group
			consultGroup.add(consult);
			grouped.put(monthString, consultGroup);
		}

		// do the super heavy stuff ^^
		monitor.subTask("Computing Results");
		for (final Entry<String, ArrayList<IEncounter>> entry : grouped.entrySet()) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			final Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
			int column = 0;

			// first column is month
			row[column++] = entry.getKey();

			// compute time and money statistics
			int consTotal = 0, total = 0, max = 0;
			double income = 0, spending = 0, profit = 0;

			for (final IEncounter consultation : entry.getValue()) {
				// check for cancelation
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;

				// time statistics for a month
				consTotal++;
				if (this.withTime) {
					final int minutes = getMinutes(consultation);
					total += minutes;
					max = (minutes > max) ? minutes : max;
				}

				// money statistics
				income += getUmsatz(consultation);
				spending += getKosten(consultation);

				monitor.worked(1);
			}

			// add all that to the row
			row[column++] = consTotal;

			if (this.withTime) {
				// rounded avg
				final DecimalFormat df = new DecimalFormat("0.00");
				final String avg = df.format((double) total / entry.getValue().size());

				row[column++] = total;
				row[column++] = max;
				row[column++] = avg;
			}

			// compute profit
			profit = income - spending;

			row[column++] = new Money(income / 100);
			row[column++] = new Money(spending / 100);
			row[column++] = new Money(profit / 100);

			content.add(row);
		}

		// set heading and content
		this.dataSet.setContent(content);

		monitor.done();
		return Status.OK_STATUS;
	}

	private double getKosten(IEncounter encounter) {
		return encounter.getBilled().stream().mapToDouble(b -> b.getNetPrice().doubleValue()).sum();
	}

	private double getUmsatz(IEncounter encounter) {
		return encounter.getBilled().stream().mapToDouble(b -> b.getTotal().doubleValue()).sum();
	}

	private int getMinutes(IEncounter encounter) {
		return encounter.getBilled().stream().mapToInt(b -> {
			if (b.getBillable() instanceof IService) {
				return (int) (((IService) b.getBillable()).getMinutes() * b.getAmount());
			}
			return 0;
		}).sum();
	}

	@Override
	protected List<String> createHeadings() {
		final ArrayList<String> headings = new ArrayList<String>();
		headings.add("Date");
		headings.add("Consultations Total");

		// time statistics
		if (this.withTime) {
			headings.add(Messages.CONSULTATION_TIME_STATS_HEADING_TIME_TOTAL);
			headings.add(Messages.CONSULTATION_TIME_STATS_HEADING_TIME_MAX);
			headings.add(Messages.CONSULTATION_TIME_STATS_HEADING_TIME_AVERAGE);
		}

		// money statistics
		headings.add(Messages.CONSULTATION_TIME_STATS_HEADING_INCOME);
		headings.add(Messages.CONSULTATION_TIME_STATS_HEADING_SPENDING);
		headings.add(Messages.CONSULTATION_TIME_STATS_HEADING_PROFIT);

		return headings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Messages.CONSULTATION_TIME_STATS_DESCRIPTION;
	}

	/**
	 * Returns the currently set parameter for including time stats.
	 * 
	 * @return whithTime True if time statistics are being included, false else.
	 */
	@GetProperty(name = "Include Time", index = 1, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Include time statistics for consultations.")
	public boolean getShowTime() {
		return this.withTime;
	}

	/**
	 * Sets the show time property. If set, time statistics will be included in
	 * the result.
	 * 
	 * @param showTime
	 *            True if time stats should be included, false else.
	 */
	@SetProperty(name = "Include Time")
	public void setShowTime(final boolean showTime) {
		this.withTime = showTime;
	}
}