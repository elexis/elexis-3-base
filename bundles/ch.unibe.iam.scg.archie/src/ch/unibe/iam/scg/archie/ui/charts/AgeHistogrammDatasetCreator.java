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

import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.model.Cohort;

/**
 * <p>
 * Dataset creator for the age distribution histogram of all patients in the
 * system.
 * </p>
 *
 * $Id: AgeHistogrammDatasetCreator.java 666 2008-12-13 00:07:54Z peschehimself
 * $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class AgeHistogrammDatasetCreator extends AbstractDatasetCreator {

	/**
	 * Magic constant for male patients, position in the genderCount array.
	 */
	private final static int MALE_INDEX = 0;

	/**
	 * Magic constant for female patients, position in the genderCount array.
	 */
	private final static int FEMALE_INDEX = 1;

	private int cohortSize;

	private boolean isEmpty;

	/**
	 * Creates a AgeHistogrammDatasetCreator
	 *
	 * @param jobName
	 * @param cohortSize
	 */
	public AgeHistogrammDatasetCreator(String jobName, int cohortSize) {
		super(jobName);
		this.setCohortSize(cohortSize);
		this.isEmpty = true;
	}

	/**
	 * @{inheritDoc
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
			// Check for cancelation.
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			int age = 0;
			// We get age as a string (thankyouverymuch) so we have to parse it.
			try {
				age = Integer.parseInt(patient.getAlter());
			} catch (NumberFormatException exception) {
				// If the age of a patient was malformated, we just ignore him.
				continue; // gets us out of the loop...
			}

			String gender = patient.getGeschlecht();

			// If the gender of a patient is neither male nor female, we just
			// ignore him.
			if (!gender.equals(Person.MALE) && !gender.equals(Person.FEMALE)) {
				continue; // gets us out of the loop...
			}

			// Calculate bounds of the cohort the current patient fits in
			int lowerBound = ((age / this.cohortSize) * this.cohortSize); // gets
			// rounded
			// down
			int upperBound = lowerBound + (this.cohortSize);

			Integer[] genderCount = new Integer[2]; // Empty integer array for
			// male and female count.
			Cohort cohort = new Cohort(lowerBound, upperBound, genderCount);

			// No entry for this age group: we create one.
			if (!histogramm.containsKey(cohort)) {

				// We use negative numbers for male count, positive for female
				// count

				if (gender.equals(Person.MALE)) {
					genderCount[MALE_INDEX] = -1; // It's a boy!
					genderCount[FEMALE_INDEX] = 0;
					histogramm.put(cohort, cohort);
				}
				// We already checked for malformed gender, so at this point we
				// are sure the patient is female.
				else {
					genderCount[MALE_INDEX] = 0;
					genderCount[FEMALE_INDEX] = 1; // It's a girl!
					histogramm.put(cohort, cohort);
				}
			} else {
				if (gender.equals(Person.MALE)) {
					Integer[] genderCountTmp = (Integer[]) histogramm.get(cohort).getValue();
					genderCountTmp[MALE_INDEX] -= 1;
				}
				// We already checked for malformed gender, so at this point we
				// are sure the patient is female.
				else {
					Integer[] genderCountTmp = (Integer[]) histogramm.get(cohort).getValue();
					genderCountTmp[FEMALE_INDEX] += 1;
				}
			}
			monitor.worked(1);
		}

		for (Entry<Cohort, Cohort> entry : histogramm.entrySet()) {
			// check for cancellation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			Integer[] genderCount = new Integer[2];
			genderCount = (Integer[]) entry.getValue().getValue();

			((DefaultKeyedValues2DDataset) this.dataset).addValue(genderCount[MALE_INDEX], "Male", entry.getKey());
			((DefaultKeyedValues2DDataset) this.dataset).addValue(genderCount[FEMALE_INDEX], "Female", entry.getKey());
		}

		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * Sets the cohort size for this chart creator.
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