/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.model.vaccplans;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractVaccinationPlan {
	public final String name;
	public final String id;
	public final List<RequiredVaccination> baseVaccinations;
	public final List<RequiredVaccination> extendedVaccinations;

	public AbstractVaccinationPlan(String id, String name) {
		this.name = name;
		this.id = id;

		initialize();

		this.baseVaccinations = Collections.unmodifiableList(addBaseVaccinations(new ArrayList<RequiredVaccination>()));
		this.extendedVaccinations = Collections
				.unmodifiableList(addExtendedVaccinations(new ArrayList<RequiredVaccination>()));
	}

	protected abstract void initialize();

	protected abstract List<RequiredVaccination> addBaseVaccinations(List<RequiredVaccination> baseVaccinations);

	protected abstract List<RequiredVaccination> addExtendedVaccinations(
			List<RequiredVaccination> extendedVaccinations);

	/**
	 * @return an ordered list of all base diseases ATC code
	 */
	public abstract List<String> getOrderedBaseDiseases();

	/**
	 *
	 * @return an ordered list of all extended diseases ATC code
	 */
	public abstract List<String> getOrderedExtendedDiseases();

	public static class RequiredVaccination {
		public final int beginAgeInMonths;
		public final int endAgeInMonths;
		public final String diseaseAtcCode;

		/**
		 * A vaccination that is required in a certain timespan
		 *
		 * @param beginAgeInMonths start of the timespan
		 * @param endAgeInMonths   end of the timespan, -1 if no end defined
		 * @param diseaseAtcCode
		 */
		public RequiredVaccination(int beginAgeInMonths, int endAgeInMonths, String diseaseAtcCode) {
			this.beginAgeInMonths = beginAgeInMonths;
			this.endAgeInMonths = endAgeInMonths;
			this.diseaseAtcCode = diseaseAtcCode;
		}

		@Override
		public String toString() {
			return "(" + beginAgeInMonths + ")-(" + endAgeInMonths + "): " + getClearedDiseaseAtcCode(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		/**
		 *
		 * @return the ATC code with trailing /B and /E removed, use this to get the
		 *         plain ATC-Code information without its allocation to base or extended
		 */
		public String getClearedDiseaseAtcCode() {
			return diseaseAtcCode.replaceAll("/[BbEe]$", StringUtils.EMPTY); //$NON-NLS-1$
		}
	}

}
