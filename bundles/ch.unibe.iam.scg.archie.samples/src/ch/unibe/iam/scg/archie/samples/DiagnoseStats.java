/*******************************************************************************
 * Copyright (c) 2008-2011 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *     Gerry Weirich - Adapt to API Changes for 2.2
 *******************************************************************************/
package ch.unibe.iam.scg.archie.samples;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jfree.data.statistics.Statistics;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.samples.i18n.Messages;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Provides statistics about diagnoses count and age distribution.
 * </p>
 * 
 * $Id: DiagnoseStats.java 783 2011-10-20 06:22:39Z gerry.weirich@gmail.com $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 783 $
 */
public class DiagnoseStats extends AbstractTimeSeries {

	/**
	 * Date format for data that comes from the database.
	 */
	private static final String DATE_DB_FORMAT = "yyyyMMdd";

	private boolean currentMandatorOnly;

	/**
	 * Constructs DiagnoseStats
	 */
	public DiagnoseStats() {
		super(Messages.DIAGNOSES_TITLE);
		this.currentMandatorOnly = true;
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {

		// Form query.
		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);
		final Query<Konsultation> query = new Query<Konsultation>(Konsultation.class);

		query.add("Datum", ">=", databaseFormat.format(this.getStartDate().getTime()));
		query.add("Datum", "<=", databaseFormat.format(this.getEndDate().getTime()));

		if (this.currentMandatorOnly) {
			query.add("MandantID", "=", CoreHub.actMandant.getId());
		}

		final List<Konsultation> consults = query.execute();

		monitor.beginTask(Messages.CALCULATING, consults.size());

		final TreeMap<String, List<Patient>> diagnoseMap = new TreeMap<String, List<Patient>>();

		// Get consultations and their patient and diagnoses stats and put them
		// all in a map that we can process later.
		monitor.subTask("Grouping Consultations");
		for (Konsultation consult : consults) {
			// Check for Cancellation.
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			List<IDiagnose> diagnoses = consult.getDiagnosen();
			Fall fall = (Fall) consult.getFall();
			if (fall != null && fall.exists()) {
				Patient patient = fall.getPatient();

				for (IDiagnose diagnose : diagnoses) {
					List<Patient> patientList = diagnoseMap.get(diagnose.getLabel());

					if (patientList != null) {
						patientList.add(patient);
					} else {
						ArrayList<Patient> list = new ArrayList<Patient>();
						list.add(patient);
						diagnoseMap.put(diagnose.getLabel(), list);
					}
				}
			}
			monitor.worked(1);
		}

		final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();

		// Build up result list from diagnose map.
		monitor.subTask("Computing Results");
		for (Entry<String, List<Patient>> entry : diagnoseMap.entrySet()) {
			// Check for cancellation.
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
			List<Patient> patients = entry.getValue();
			int column = 0;

			row[column++] = entry.getKey();
			row[column++] = patients.size();

			// Compute patient age stats
			double ageMin = 10000, ageMax = 0, ageTotal = 0, ageMedian = 0;
			ArrayList<Integer> ageList = new ArrayList<Integer>();
			for (Patient patient : patients) {
				Integer age = new Integer(patient.getAlter());
				ageList.add(age);
				ageMin = (age < ageMin) ? age : ageMin;
				ageMax = (age > ageMax) ? age : ageMax;
				ageTotal += age;
			}

			// Sort ages and compute median.
			ageMedian = Statistics.calculateMedian(ageList);

			final DecimalFormat df = new DecimalFormat("0.0");
			final String ageAvg = df.format((double) ageTotal / patients.size());

			row[column++] = ageMin;
			row[column++] = ageMax;
			row[column++] = ageAvg;
			row[column++] = ageMedian;

			result.add(row);
		}

		// Set content.
		this.dataSet.setContent(result);

		// Job finished successfully
		monitor.done();
		return Status.OK_STATUS;
	}

	@Override
	protected List<String> createHeadings() {
		final ArrayList<String> headings = new ArrayList<String>(6);
		headings.add(Messages.DIAGNOSES_HEADING_DIAGNOSE);
		headings.add(Messages.DIAGNOSES_HEADING_COUNT);
		headings.add(Messages.DIAGNOSES_HEADING_AGE_MIN);
		headings.add(Messages.DIAGNOSES_HEADING_AGE_MAX);
		headings.add(Messages.DIAGNOSES_HEADING_AGE_AVG);
		headings.add(Messages.DIAGNOSES_HEADING_AGE_MED);
		return headings;
	}

	/**
	 * @see ch.unibe.iam.scg.archie.model.AbstractDataProvider#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DIAGNOSES_DESCRIPTION;
	}

	/**
	 * @return currentMandatorOnly
	 */
	@GetProperty(name = "Active Mandator Only", index = 1, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Compute statistics only for the current mandant. If unchecked, the statistics will be computed for all mandants.")
	public boolean getCurrentMandatorOnly() {
		return this.currentMandatorOnly;
	}

	/**
	 * @param currentMandatorOnly
	 */
	@SetProperty(name = "Active Mandator Only")
	public void setCurrentMandatorOnly(final boolean currentMandatorOnly) {
		this.currentMandatorOnly = currentMandatorOnly;
	}
}
