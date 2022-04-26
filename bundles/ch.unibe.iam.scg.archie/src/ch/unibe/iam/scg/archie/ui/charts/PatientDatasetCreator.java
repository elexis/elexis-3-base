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

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jfree.data.general.DefaultPieDataset;

import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;

/**
 * <p>
 * Dataset creator for the patients dashboard chart.
 * </p>
 *
 * $Id: PatientDatasetCreator.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class PatientDatasetCreator extends AbstractDatasetCreator {

	/**
	 * Creates a CostDatasetCreator
	 *
	 * @param jobName
	 */
	public PatientDatasetCreator(String jobName) {
		super(jobName);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.actions.BackgroundJob#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus createContent(final IProgressMonitor monitor) {
		Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		HashMap<String, Integer> patientsMap = new HashMap<String, Integer>();

		List<Patient> patients = patientQuery.execute();

		monitor.beginTask("Querying Database", patients.size());

		for (Patient patient : patients) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			String gender = patient.getGeschlecht();

			if (gender.equals(Person.MALE) || gender.equals(Person.FEMALE)) {
				if (patientsMap.containsKey(gender)) {
					Integer count = patientsMap.get(gender);
					patientsMap.put(gender, new Integer(count + 1));
				} else {
					patientsMap.put(gender, 1);
				}
			}
			monitor.worked(1);
		}

		// compose dataset
		this.dataset = new DefaultPieDataset();
		for (Entry<String, Integer> entry : patientsMap.entrySet()) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			((DefaultPieDataset) this.dataset).setValue(entry.getKey(), entry.getValue());
		}

		monitor.done();
		return Status.OK_STATUS;
	}
}