/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.constants;

/**
 * Zusammenstellung der Geräte- und verfahrensspezifischen Kennfeldern lt. GDT Standard 2.1
 */
public class Feld8402Constants {

	public enum ALL {
		ALLE01("Anamneseerfassung allergologisch"), 
		ALLE02("Befunderfassung allergologisch"), 
		ALLE03("Diagnoseerfassung allergologisch"), 
		ALLE04("Pricktest"), 
		ALLE05("Intracutantest"), 
		ALLE06("Provokationstest"), 
		ALLE07("Invitrotest"), 
		ALLE08("Insektengift"), 
		ALLE09("Epikutantest"), 
		ALLE10("tägliche Hyposensibilisierungsbehandlung"),
		ALLG00("nicht näher spezifizierte Untersuchungen"),
		APNO00("Apnoe, allgemein"), 
		APNO01("Langzeit Schlafapnoe Screening"), 
		APNO02("Polysomnographie"),
		AUDI00("Audiometrie, allgemein"), 
		AUDI01("Reinton-Schwellen-Audiogramm"), 
		AUDI02("EEG-Audiometrie"),
		BDM00("Blutdruckmessungen, allgemein"), 
		BDM01("Langzeit-Blutdruckmessung"),
		EKG00("EKG, allgemein"),
		EKG01("Ruhe-EKG"),
		EKG02("Arrhytmie-EKG"),
		EKG03("Spätpotential-EKG"),
		EKG04("Langzeit-EKG"),
		ERGO00("Belastungs-Untersuchung, allgemein"),
		ERGO01("Belastungs-EKG"),
		ERGO02("Fluß-Volumen unter Belastung"),
		ERGO03("Blutgase"),
		ERGO04("Blutgase unter Belastung"),
		ERGO05("Spiroergometrie"),
		ERGO06("Atemgasanalyse"),
		ERGO07("Pulsoximetrie"),
		ERGO08("Indirekte Kalorimetrie"),
		ERGO09("Indirekte Kalorimetrie mit Haube"),
		ERGO10("HZV-Bestimmung über CO2-Rückatmung"),
		ERGO11("Atemantriebsmessung über CO2-Rückatmung"),
		HÄMA01("kleines Blutbild"),
		HÄMA02("großes Blutbild"),
		HÄMA03("manuelles Differentialblutbild"),
		HÄMA04("Retikulozyten"),
		HÄMA05("CD4/CD8"),
		LUFU00("Lungenfunktion, allgemein"),
		LUFU01("Langsame Spirometrie"),
		LUFU02("Forcierte Spirometrie (Fluß-Volumen)"),
		LUFU03("MVV (Maximal Voluntary Ventilation)"),
		LUFU04("Bodyplethysmographie"),
		LUFU05("FRC pl (Lungenvolumen - Bodyplethysmographie)"),
		LUFU06("FRC He (Lungenvolumen - Helium Rückatmung)"),
		LUFU07("Resistance nach Verschlußdruckmethode"),
		LUFU08("Resistance nach Impulsoscillation-Methode"),
		LUFU09("Resistance nach Oszilloresistometrie-Methode"),
		LUFU10("Compliance"),
		LUFU11("Atemmuksulaturstärke-Mesung"),
		LUFU12("Atemantrieb-Messung"),
		LUFU13("Diffusion Single-Breath"),
		LUFU14("Diffusion Steady-State"),
		LUFU15("Diffusion Rebreathing"),
		LUFU16("Diffusion Membranfaktor"),
		LUFU17("Capnographie"),
		LUFU18("Rhinomanometrie"),
		LUFU19("Ruheatmungsanalyse"),
		NEUR00("Neurologie, allgemein"),
		NEUR01("Langzeit-EEG"),
		NEUR02("EEG mit simultaner EKG-Aufzeichnung"),
		NEUR03("Motorisches NLG"),
		NEUR04("Sensorisches NLG"),
		NEUR05("Evozierte Potentiale"),
		NEUR06("Rotationstest"),
		NEUR07("Nystagmusanalyse"),
		NEUR08("Sakkadentest"),
		NEUR09("Posture"),
		NEUR10("Biofeedback"),
		OPTO00("Augenheilkunde, allgemein"),
		OPTO01("Refraktionsbestimmung, objektiv"),
		OPTO02("Refraktionsbestimmung, subjektiv"),
		OPTO03("Refraktionswerte Brille/Kontaktlinse"),
		OPTO04("Blendenempfindlichkeitsmessung (Visus)"),
		OPTO05("Gesichtsfeldmessung"),
		OPTO06("Augendruckmessung"),
		OPTO07("Hornhautmessung (Krümmungsradien/Achslagen)"),
		OPTO08("Hornhautmessung (3D-Geometriedaten)"),
		OPTO09("Fundusbilder"),
		OPTO10("Angiographiebilder"),
		OPTO11("Spaltlampenbilder"),
		OPTO12("Topographiebilder"),
		OPTO13("Schichtbilder"),
		OPTO14("generische Bilddaten"),
		PROV00("Provokation, allgemein"),
		PROV01("Spezifische Aerosol-Provokation"),
		PROV02("Unspezifische Aerosol-Provokation"),
		PROV03("Kaltluft Provokation"),
		PROV04("Bronchodilatation"),
		SONO00("Sonographie, allgemein"),
		SONO01("Ultraschall-Doppler"),
		URO00("Urologie, allgemein"),
		URO01("Uroflowmetrie");
		
		String description;
		
		ALL(String desc){
			this.description = desc;
		}
		
		@Override
		public String toString(){
			return description;
		}
	}
	
	public static <T extends Enum<T>> String[] enumNameToStringArray(T[] values) {  
	    int i = 0;  
	    String[] result = new String[values.length];  
	    for (T value: values) {  
	        result[i++] = value.name();  
	    }  
	    return result;  
	}
	
	public static <T extends Enum<T>> String[] enumNameToStringArrayDescription(T[] values) {  
	    int i = 0;  
	    String[] result = new String[values.length];  
	    for (T value: values) {  
	        result[i++] = value.toString();  
	    }  
	    return result;  
	} 
}
