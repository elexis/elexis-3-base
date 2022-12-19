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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImpfplanSchweiz2019 extends AbstractVaccinationPlan {

	private List<RequiredVaccination> vaccPlan;

	private final static String vaccPlanCH2019[] = { "2;J07AF/B,J07AM/B,J07AJ/B,J07BF/B,J07AG/B,J07AL/B,J07BC01/B",
			"4;J07AF/B,J07AM/B,J07AJ/B,J07BF/B,J07AG/B,J07AL/B,J07BC01/B", "9;J07BD/B,J07BE/B,J07BJ/B",
			"12;J07AF/B,J07AM/B,J07AJ/B,J07BF/B,J07AG/B,J07AL/B,J07BC01/B,J07BD/B,J07BE/B,J07BJ/B", "24;J07AH/E",
			"48-84;J07AF/B,J07AM/B,J07AJ/B,J07BF/B",
			"132-180;J07AF/B,J07AM/B,J07AJ/B,J07BC01/B,J07BM/B,J07BK01/B,J07AH/E",
			"300;J07AF/B,J07AM/B,J07AJ/B,J07BM/E", "180-312;J07BM/E", "540;J07AF/B,J07AM/B",
			"780;J07AF/B,J07AM/B,J07BB,J07BK02/E" };

	public ImpfplanSchweiz2019() {
		super("VACC_CH_2019", "Schweizerischer Impfplan 2022"); //$NON-NLS-1$
		// vaccPlan is needed for initialization only
		vaccPlan = null;
	}

	@Override
	protected void initialize() {
		vaccPlan = parseVaccPlan(new ArrayList<RequiredVaccination>());
	}

	@Override
	public List<String> getOrderedBaseDiseases() {
		List<String> rv = new ArrayList<>();
		String[] values = "J07AF,J07AM,J07AJ,J07AG,J07BF,J07BC01,J07AL,J07BD,J07BE,J07BJ,J07BK01,J07BM,J07BB"
				.split(",");
		for (String string : values) {
			rv.add(string);
		}
		Collections.reverse(rv);
		return rv;
	}

	@Override
	public List<String> getOrderedExtendedDiseases() {
		List<String> rv = new ArrayList<>();
		String[] values = "J07AH,J07BK02".split(",");
		for (String string : values) {
			rv.add(string);
		}
		Collections.reverse(rv);
		return rv;
	}

	@Override
	protected List<RequiredVaccination> addBaseVaccinations(List<RequiredVaccination> baseVaccinations) {
		for (RequiredVaccination rv : vaccPlan) {
			if (rv.diseaseAtcCode.endsWith("/B"))
				baseVaccinations.add(rv);
		}
		return baseVaccinations;
	}

	@Override
	protected List<RequiredVaccination> addExtendedVaccinations(List<RequiredVaccination> extendedVaccinations) {
		for (RequiredVaccination rv : vaccPlan) {
			if (rv.diseaseAtcCode.endsWith("/E"))
				extendedVaccinations.add(rv);
		}
		return extendedVaccinations;
	}

	private List<RequiredVaccination> parseVaccPlan(List<RequiredVaccination> vaccPlan) {
		for (String line : vaccPlanCH2019) {
			String[] split = line.split(";");
			int[] timeFrames = splitTimeFrames(split[0]);
			List<String> atcCodes = Arrays.asList(split[1].split(","));

			for (String atc : atcCodes) {
				RequiredVaccination rv = new RequiredVaccination(timeFrames[0], timeFrames[1], atc);
				vaccPlan.add(rv);
			}
		}
		return vaccPlan;
	}

	private int[] splitTimeFrames(String timeString) {
		int[] retVal = new int[2];
		Arrays.fill(retVal, -1);

		String[] split = timeString.split("-");
		for (int i = 0; i < split.length; i++) {
			retVal[i] = Integer.parseInt(split[i]);
		}
		return retVal;
	}
}
