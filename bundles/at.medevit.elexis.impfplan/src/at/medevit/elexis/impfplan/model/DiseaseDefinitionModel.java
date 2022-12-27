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
package at.medevit.elexis.impfplan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;

public class DiseaseDefinitionModel {

	public static final String VACCINATION_ATC_GROUP_TRAILER = "J07"; //$NON-NLS-1$

	private static List<DiseaseDefinition> diseases = null;

	public static List<DiseaseDefinition> getDiseaseDefinitions() {
		if (diseases == null)
			initDiseases();
		return diseases;
	}

	private static void initDiseases() {
		diseases = new ArrayList<>();

		diseases.add(new DiseaseDefinition("J07AF", "Diphterie", "Diphteria")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AM", "Starrkrampf", "Tetanus")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AJ", "Keuchhusten", "Pertussis")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BF", "Kinderlähmung", "Poliomyelitis")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AG", "Haemophilus influenzae (Hib)")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BD", "Masern", "Measles")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BE", "Mumps")); //$NON-NLS-1$
		diseases.add(new DiseaseDefinition("J07BJ", "Röteln", "Rubella")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BC01", "Hepatitis B")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BM", "humanes Papilloma Virus (Mädchen)")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BB", "Influenza (> 65)")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BC02", "Hepatitis A*")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07AL", "Pneumokokken", "Pneumococca")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AH", "Meningokokken", "Meningococca")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BA01", "FSME", "Encephalitis tick borne")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AP", "Typhus")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BK01", "Windpocken", "Varizellen")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BL", "Gelbfieber", "Yellow fever")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AE", "Cholera")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BA02", "japanische Encephalitis", "Encephalitis japanese")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BH", "Rotaviren", "Rota virus")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07BG", "Tollwut", "Rabies")); //$NON-NLS-1$ //$NON-NLS-3$
		diseases.add(new DiseaseDefinition("J07AN", "Tuberkulose")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BK02", "Herpes Zoster")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BX03", "COVID-19")); //$NON-NLS-1$ //$NON-NLS-2$
		diseases.add(new DiseaseDefinition("J07BX", "COVID-19")); //$NON-NLS-1$ //$NON-NLS-2$

		diseases = Collections.unmodifiableList(diseases);
	}

	public static class DiseaseDefinition {
		private final String ATCCode;
		private final String diseaseLabel;
		private final String diseaseSynonym;

		public DiseaseDefinition(String atcCode, String diseaseLabel, String diseaseSynonym) {
			this.ATCCode = atcCode;
			this.diseaseLabel = diseaseLabel;
			this.diseaseSynonym = diseaseSynonym;
		}

		public DiseaseDefinition(String atcCode, String diseaseLabel) {
			this(atcCode, diseaseLabel, null);
		}

		public @NonNull String getDiseaseLabel() {
			return diseaseLabel;
		}

		public @NonNull String getATCCode() {
			return ATCCode;
		}

		public @Nullable String getDiseaseSynonym() {
			return diseaseSynonym;
		}

		@Override
		public String toString() {
			if (diseaseSynonym == null) {
				return diseaseLabel;
			}
			return diseaseLabel + " - " + diseaseSynonym; //$NON-NLS-1$
		}
	}

	/**
	 * retrieve the label for a specific disease ATC code
	 *
	 * @param diseaseAtcCode
	 * @return
	 */
	public static String getLabelForAtcCode(String diseaseAtcCode) {
		for (DiseaseDefinition dd : getDiseaseDefinitions()) {
			if (diseaseAtcCode.equalsIgnoreCase(dd.ATCCode)) {
				return dd.toString();
			}
		}
		return "?????"; //$NON-NLS-1$
	}
}
