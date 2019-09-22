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
	
	public static final String VACCINATION_ATC_GROUP_TRAILER = "J07";
	
	private static List<DiseaseDefinition> diseases = null;
	
	public static List<DiseaseDefinition> getDiseaseDefinitions(){
		if (diseases == null)
			initDiseases();
		return diseases;
	}
	
	private static void initDiseases(){
		diseases = new ArrayList<>();
		
		diseases.add(new DiseaseDefinition("J07AF", "Diphterie", "Diphteria"));
		diseases.add(new DiseaseDefinition("J07AM", "Starrkrampf", "Tetanus"));
		diseases.add(new DiseaseDefinition("J07AJ", "Keuchhusten", "Pertussis"));
		diseases.add(new DiseaseDefinition("J07BF", "Kinderlähmung", "Poliomyelitis"));
		diseases.add(new DiseaseDefinition("J07AG", "Haemophilus influenzae (Hib)"));
		diseases.add(new DiseaseDefinition("J07BD", "Masern", "Measles"));
		diseases.add(new DiseaseDefinition("J07BE", "Mumps"));
		diseases.add(new DiseaseDefinition("J07BJ", "Röteln", "Rubella"));
		diseases.add(new DiseaseDefinition("J07BC01", "Hepatitis B"));
		diseases.add(new DiseaseDefinition("J07BM", "humanes PapillomaVirus"));
		diseases.add(new DiseaseDefinition("J07BB", "Influenza"));
		diseases.add(new DiseaseDefinition("J07BC02", "Hepatitis A*"));
		diseases.add(new DiseaseDefinition("J07AL", "Pneumokokken", "Pneumococca"));
		diseases.add(new DiseaseDefinition("J07AH", "Meningokokken", "Meningococca"));
		diseases.add(new DiseaseDefinition("J07BA01", "FSME", "Encephalitis tick borne"));
		diseases.add(new DiseaseDefinition("J07AP", "Typhus"));
		diseases.add(new DiseaseDefinition("J07BK", "Varizellen", "Varicella zoster"));
		diseases.add(new DiseaseDefinition("J07BL", "Gelbfieber", "Yellow fever"));
		diseases.add(new DiseaseDefinition("J07AE", "Cholera"));
		diseases.add(new DiseaseDefinition("J07BA02", "japanische Encephalitis",
			"Encephalitis japanese"));
		diseases.add(new DiseaseDefinition("J07BH", "Rotaviren", "Rota virus"));
		diseases.add(new DiseaseDefinition("J07BG", "Tollwut", "Rabies"));
		diseases.add(new DiseaseDefinition("J07AN", "Tuberkulose"));
		diseases.add(new DiseaseDefinition("J07BK01", "Windpocken - Varizellen"));
		diseases.add(new DiseaseDefinition("J07BK02", "Herpes Zoster"));
		
		diseases = Collections.unmodifiableList(diseases);
	}
	
	public static class DiseaseDefinition {
		private final String ATCCode;
		private final String diseaseLabel;
		private final String diseaseSynonym;
		
		public DiseaseDefinition(String atcCode, String diseaseLabel, String diseaseSynonym){
			this.ATCCode = atcCode;
			this.diseaseLabel = diseaseLabel;
			this.diseaseSynonym = diseaseSynonym;
		}
		
		public DiseaseDefinition(String atcCode, String diseaseLabel){
			this(atcCode, diseaseLabel, null);
		}
		
		public @NonNull
		String getDiseaseLabel(){
			return diseaseLabel;
		}
		
		public @NonNull
		String getATCCode(){
			return ATCCode;
		}
		
		public @Nullable
		String getDiseaseSynonym(){
			return diseaseSynonym;
		}
		
		@Override
		public String toString(){
			if(diseaseSynonym==null) {
				return diseaseLabel;
			}
			return diseaseLabel+" - "+diseaseSynonym;
		}
	}

	/**
	 * retrieve the label for a specific disease ATC code
	 * @param diseaseAtcCode
	 * @return
	 */
	public static String getLabelForAtcCode(String diseaseAtcCode){
		for (DiseaseDefinition dd : getDiseaseDefinitions()) {
			if(diseaseAtcCode.equalsIgnoreCase(dd.ATCCode)) {
				return dd.toString();
			}
		}
		return "?????";
	}
}
