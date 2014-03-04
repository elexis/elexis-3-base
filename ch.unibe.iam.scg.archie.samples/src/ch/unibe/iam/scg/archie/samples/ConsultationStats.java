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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.Money;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.model.Cohort;
import ch.unibe.iam.scg.archie.model.SetDataException;
import ch.unibe.iam.scg.archie.samples.i18n.Messages;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Provides statistics about consultations. The resulting dataset contains
 * information about costs, profits and the total number of consultations for a
 * given age group.
 * </p>
 * 
 * $Id: ConsultationStats.java 766 2009-07-24 11:28:14Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 766 $
 */
public class ConsultationStats extends AbstractTimeSeries {

	private int cohortSize;

	private boolean currentMandatorOnly;

	private static final String DATE_DB_FORMAT = "yyyyMMdd";

	/** Constructor */
	public ConsultationStats() {
		super(Messages.CONSULTATION_STATS_TITLE);
		this.cohortSize = 5;
		this.currentMandatorOnly = true;
	}

	/** {@inheritDoc} */
	@Override
	protected List<String> createHeadings() {
		final ArrayList<String> headings = new ArrayList<String>(6);
		headings.add(Messages.CONSULTATION_STATS_AGE_GROUP);
		headings.add(Messages.CONSULTATION_STATS_NUMBER_OF_CONSULTATIONS);
		headings.add(Messages.CONSULTATION_STATS_TOTAL_COSTS);
		headings.add(Messages.CONSULTATION_STATS_AVERAGE_COSTS);
		headings.add(Messages.CONSULTATION_STATS_TOTAL_PROFITS);
		headings.add(Messages.CONSULTATION_STATS_AVERAGE_PROFITS);
		return headings;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected IStatus createContent(IProgressMonitor monitor) {

		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);

		// Prepare DB query
		final Query<Konsultation> query = new Query<Konsultation>(Konsultation.class);
		query.add("Datum", ">=", databaseFormat.format(this.getStartDate().getTime()));
		query.add("Datum", "<=", databaseFormat.format(this.getEndDate().getTime()));
		if (this.currentMandatorOnly) {
			query.add("MandantID", "=", CoreHub.actMandant.getId());
		}

		// Get all Consultation which happened in the specified date range.
		final List<Konsultation> consultations = query.execute();

		monitor.beginTask(Messages.CALCULATING, consultations.size()); // monitoring

		// Create a list of cohorts which we will be using as main data holder.
		// Key is cohort name, entry is cohort itself
		TreeMap<Cohort, Cohort> cohorts = new TreeMap<Cohort, Cohort>();

		monitor.subTask("Grouping Consultations");
		for (Konsultation consultation : consultations) {
			// Check for cancellation
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			// Get the patient which is linked to this consultation
			Patient patient = consultation.getFall().getPatient();

			int age = 0;
			// We get age as a string (thankyouverymuch) so we have to parse it
			try {
				age = Integer.parseInt(patient.getAlter());
			} catch (NumberFormatException exception) {
				// If the age of a patient was not formatted right, we just
				// ignore him.
				continue; // gets us out of the loop...
			}

			// In which cohort does this patient belong to?
			// gets rounded down
			int lowerBound = ((age / this.cohortSize) * this.cohortSize);
			int upperBound = lowerBound + this.cohortSize - 1;

			// Initialize empty cohort content, which we will fill with
			// consultation costs and profits.
			// cohortContent is an Array with two ArrayLists<Double> in it.
			ArrayList<Double>[] cohortContent = new ArrayList[2];

			// Prepare Cohort.
			Cohort cohort = new Cohort(lowerBound, upperBound, cohortContent);

			ArrayList<Double> cohortContentCosts = new ArrayList<Double>(consultations.size());
			ArrayList<Double> cohortContentProfits = new ArrayList<Double>(consultations.size());

			cohortContent[0] = cohortContentCosts;
			cohortContent[1] = cohortContentProfits;

			// Cohort is not in cohort list yet, add it.
			if (!cohorts.containsKey(cohort)) {
				// Cast int to double.
				cohortContentCosts.add(((Integer) consultation.getKosten()).doubleValue());
				cohortContentProfits.add(consultation.getGewinn());
				cohorts.put(cohort, cohort);
			}
			// Cohort is already in cohort list. Add consultation costs and
			// profits to it's content.
			else {
				ArrayList<Double>[] cohortContentOld = (ArrayList<Double>[]) cohorts.get(cohort).getValue();
				((ArrayList<Double>) cohortContentOld[0]).add(((Integer) consultation.getKosten()).doubleValue());
				((ArrayList<Double>) cohortContentOld[1]).add(consultation.getGewinn());
			}

			monitor.worked(1); // monitor
		}

		// Create dataset result
		ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();

		// Go over all cohorts we stored and create actual dataset.
		monitor.subTask("Computing Results");
		for (final Entry<Cohort, Cohort> entry : cohorts.entrySet()) {
			// Check for cancellation
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			final Comparable<?>[] row = new Comparable[this.dataSet.getHeadings().size()];

			ArrayList<Double>[] cohortContent = (ArrayList<Double>[]) entry.getValue().getValue();

			double totalCosts = 0.0;
			double totalProfits = 0.0;

			// Go through all consultation costs and add them together
			for (Double costs : ((ArrayList<Double>) cohortContent[0])) {
				totalCosts += costs;
			}

			// Go through all consultation profits and add them together
			for (Double profits : ((ArrayList<Double>) cohortContent[1])) {
				totalProfits += profits;
			}

			row[0] = entry.getValue(); // cohortName
			row[1] = ((Integer) cohortContent[0].size()); // numberOfConsultations
			// CAVEAT: Money(int) is constructed with cents, Money(double)
			// isn't. We have to divide by 100 :(
			row[2] = new Money(totalCosts / 100); // totalCosts
			row[3] = new Money(totalCosts / cohortContent[0].size() / 100); // averageCosts
			row[4] = new Money(totalProfits / 100); // totalProfits
			row[5] = new Money(totalProfits / cohortContent[1].size() / 100); // averageProfits

			result.add(row);
		}

		// set content
		this.dataSet.setContent(result);

		monitor.done();
		return Status.OK_STATUS;
	}

	/** {@inheritDoc} */
	@Override
	public String getDescription() {
		return Messages.CONSULTATION_STATS_DESCRIPTION;
	}

	/**
	 * Returns the cohort size.
	 * 
	 * @return cohortSize
	 */
	@GetProperty(name = "Cohort Size", index = 2, widgetType = WidgetTypes.TEXT_NUMERIC, validationRegex = "^([1-9]){1}\\d{0,2}", validationMessage = "This field has to consist of at least one, at most three numbers.")
	public int getCohortSize() {
		return this.cohortSize;
	}

	/**
	 * Sets the cohort size.
	 * 
	 * @param cohortSize
	 *            Size of the cohort.
	 * @throws SetDataException
	 *             Thrown when a cohort size is smaller than 1.
	 */
	@SetProperty(name = "Cohort Size")
	public void setCohortSize(final int cohortSize) throws SetDataException {
		try {
			if (cohortSize < 1) {
				throw new Exception("Cohort size must be at least 1!");
			}
			this.cohortSize = cohortSize;
		} catch (final Exception e) {
			throw new SetDataException(Messages.CONSULTATION_STATS_COHORT_SIZE_EXCEPTION);
		}
	}

	/**
	 * Returns true if the statistic is run for the current mandator only.
	 * 
	 * @return currentMandatorOnly True if the statistic is run for the current
	 *         mandator only, false else.
	 */
	@GetProperty(name = "Active Mandator Only", index = 3, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Compute statistics only for the current mandator. If unchecked, the statistics will be computed for all mandator.")
	public boolean getCurrentMandatorOnly() {
		return this.currentMandatorOnly;
	}

	/**
	 * Sets whether the statistics should be run for the current mandator only.
	 * 
	 * @param currentMandatorOnly
	 *            True if the statistic needs to be run for the current mandator
	 *            only, false else.
	 */
	@SetProperty(name = "Active Mandator Only")
	public void setCurrentMandatorOnly(final boolean currentMandatorOnly) {
		this.currentMandatorOnly = currentMandatorOnly;
	}
}