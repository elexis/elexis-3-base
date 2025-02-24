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

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jfree.data.general.DefaultKeyedValues2DDataset;

import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.model.Cohort;

/**
 * <p>
 * Creates dataset for PatientsConsHistChart.
 * </p>
 *
 * $Id: PatientsConsHistDatasetCreator.java 747 2009-07-23 09:14:53Z
 * peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class PatientsConsHistDatasetCreator extends AbstractDatasetCreator {

	private final static int MALE_INDEX = 0;
	private final static int FEMALE_INDEX = 1;

	private int cohortSize;

	private boolean isEmpty;

	/**
	 * Creates a AgeHistogrammDatasetCreator
	 *
	 * @param jobName
	 * @param cohortSize
	 */
	public PatientsConsHistDatasetCreator(String jobName, int cohortSize) {
		super(jobName);
		this.setCohortSize(cohortSize);
		this.isEmpty = true;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public IStatus createContent(final IProgressMonitor monitor) {
		this.dataset = new DefaultKeyedValues2DDataset();

		Query<Patient> query = new Query<Patient>(Patient.class);
		List<Patient> patients = query.execute();

		this.isEmpty = patients.size() <= 0;

		monitor.beginTask("Querying Database...", patients.size());

		// TreeSet with Cohort title as key, and the cohort as value
		TreeMap<Cohort, Cohort> histogramm = new TreeMap<Cohort, Cohort>();

		for (Patient patient : patients) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			int age = 0;
			// We get age as a string, so we have to parse it
			try {
				age = Integer.parseInt(patient.getAlter());
			} catch (NumberFormatException exception) {
				// If the age of a patient was malformated, we just ignore him.
				continue; // gets us out of the loop...
			}
			Double consCostsPerPatient = new Double(0);
			for (Fall fall : patient.getFaelle()) {
				for (Konsultation konsultation : fall.getBehandlungen(false)) {
					consCostsPerPatient += (konsultation.getKosten() / 100); // we don't want cents
				}
			}

			String gender = patient.getGeschlecht();

			// If the gender of a patient is neither male nor female, we just ignore him.
			if (!gender.equals(Person.MALE) && !gender.equals(Person.FEMALE)) {
				continue; // gets us out of the loop...
			}

			// Calculate bounds of the cohort the current patient fits in
			int lowerBound = ((age / this.cohortSize) * this.cohortSize); // gets rounded down
			int upperBound = lowerBound + (this.cohortSize - 1);

			Double[] consultationGenderCosts = new Double[2];
			Cohort cohort = new Cohort(lowerBound, upperBound, consultationGenderCosts);

			// No entry for this age group: we create one.
			if (!histogramm.containsKey(cohort)) {

				// We use negative numbers for male count, positive for female count

				if (gender.equals(Person.MALE)) {
					consultationGenderCosts[MALE_INDEX] = consCostsPerPatient;
					consultationGenderCosts[FEMALE_INDEX] = 0.0;
					histogramm.put(cohort, cohort);
				}
				// We already checked for malformed gender, so at this point we are sure the
				// patient is female.
				else {
					consultationGenderCosts[MALE_INDEX] = 0.0;
					consultationGenderCosts[FEMALE_INDEX] = -consCostsPerPatient;
					histogramm.put(cohort, cohort);
				}
			}
			// ...else we update the existing one
			else {
				if (gender.equals(Person.MALE)) {
					Double[] genderCountTmp = (Double[]) histogramm.get(cohort).getValue();
					genderCountTmp[MALE_INDEX] -= consCostsPerPatient;
				}
				// We already checked for malformed gender, so at this point we are sure the
				// patient is female.
				else {
					Double[] genderCountTmp = (Double[]) histogramm.get(cohort).getValue();
					genderCountTmp[FEMALE_INDEX] += consCostsPerPatient;
				}
			}
			monitor.worked(1);
		}

		for (Entry<Cohort, Cohort> entry : histogramm.entrySet()) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			Double[] ConsCosts = new Double[2];
			ConsCosts = (Double[]) entry.getValue().getValue();
			((DefaultKeyedValues2DDataset) this.dataset).addValue(ConsCosts[MALE_INDEX], "Male", entry.getKey()); //$NON-NLS-1$
			((DefaultKeyedValues2DDataset) this.dataset).addValue(ConsCosts[FEMALE_INDEX], "Female", entry.getKey()); //$NON-NLS-1$
		}

		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * Sets the cohort size for this creator.
	 *
	 * @param cohortSize
	 */
	public void setCohortSize(int cohortSize) {
		this.cohortSize = cohortSize;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected boolean isDatasetEmpty() {
		return this.isEmpty;
	}
}