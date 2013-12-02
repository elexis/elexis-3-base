/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.data;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.action.IAction;

import ch.elexis.base.ch.diagnosecodes_schweiz.Messages;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Verrechnet;

/**
 * Die Tessinercodes werden nicht in der Datenbank vorgehalten, sondern sind aus Effizientgründen
 * hier in dieser Klasse fest verdrahtet. Die Klasse ist trotzdem im data-Package, um die
 * Programmierkonsistenz zu wahren.
 * 
 * @author Gerry
 * 
 */
public class TICode extends PersistentObject implements IDiagnose {
	public static final String CODESYSTEM_NAME = "TI-Code";
	private static Hashtable<String, TICode> hash = new Hashtable<String, TICode>();
	private String Text;
	
	TICode(String Code, String Text){
		super(Code);
		this.Text = Text;
	}
	
	public String getText(){
		return Text;
	}
	
	public String getCode(){
		return getId();
	}
	
	public String getLabel(){
		return getId() + " " + Text; //$NON-NLS-1$
	}
	
	public String getCodeSystemName(){
		return CODESYSTEM_NAME; //$NON-NLS-1$
	}
	
	public static TICode load(String code){
		return getFromCode(code);
	}
	
	public static TICode[] getRootNodes(){
		TICode[] ret = new TICode[ticode.length];
		int i;
		for (i = 0; i < ticode.length; i++) {
			String[] line = ticode[i];
			ret[i] = new TICode(line[0], line[1]);
		}
		return ret;
	}
	
	public static TICode getFromCode(String code){
		TICode ret = hash.get(code);
		if (ret == null) {
			String chapter = code.substring(0, 1);
			int subch = 0;
			if (code.length() == 2) {
				subch = Integer.parseInt(code.substring(1));
			}
			for (int i = 0; i < ticode.length; i++) {
				if (ticode[i][0].startsWith(chapter)) {
					if (subch == 9) {
						subch = ticode[i].length - 2;
						ret = new TICode(chapter + "9", ticode[i][subch + 1]); //$NON-NLS-1$
					} else {
						ret = new TICode(chapter + subch, ticode[i][subch + 1]);
					}
					hash.put(code, ret);
					return ret;
				}
			}
		}
		return ret;
	}
	
	public TICode getParent(){
		if (getId().length() == 1) {
			return null;
		}
		return getFromCode(getId().substring(0, 1));
	}
	
	public boolean hasChildren(){
		if (getId().length() == 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean exists(){
		return true;
	}
	
	public TICode[] getChildren(){
		if (getId().length() > 1) {
			return null;
		}
		String chapter = getId().substring(0, 1);
		for (int i = 0; i < ticode.length; i++) {
			if (ticode[i][0].equals(chapter)) {
				TICode[] ret = new TICode[ticode[i].length - 2];
				for (int j = 2; j < ticode[i].length; j++) {
					String x;
					if (j == ticode[i].length - 1) {
						x = "9"; //$NON-NLS-1$
					} else {
						x = Integer.toString(j - 1);
					}
					ret[j - 2] = new TICode(chapter + x, ticode[i][j]);
				}
				return ret;
			}
		}
		return null;
	}
	
	private static final String[][] ticode = {
		{
			"A", Messages.TICode_Heart, //$NON-NLS-1$
			Messages.TICode_valves, Messages.TICode_coronaria, Messages.TICode_rhythm,
			Messages.TICode_hypertonia, Messages.TICode_arteries, Messages.TICode_veins,
			Messages.TICode_lymphVessels, Messages.TICode_heartOther
		},
		{
			"B", Messages.TICode_blood, //$NON-NLS-1$
			Messages.TICode_anemia, Messages.TICode_coagulo, Messages.TICode_boneMarrow,
			Messages.TICode_spleen, Messages.TICode_bloodOther
		},
		
		{
			"C", Messages.TICode_lung, //$NON-NLS-1$
			Messages.TICode_asthma, Messages.TICode_cough, Messages.TICode_embolism,
			Messages.TICode_lungPleural, Messages.TICode_lungOther
		},
		
		{
			"D", Messages.TICode_locomotion, //$NON-NLS-1$
			Messages.TICode_muscle, Messages.TICode_joint, Messages.TICode_arthtiris,
			Messages.TICode_arthrosis, Messages.TICode_vertebral, Messages.TICode_locoOther
		},
		
		{
			"E", Messages.TICode_digestive, //$NON-NLS-1$
			Messages.TICode_esophagus, Messages.TICode_bowel, Messages.TICode_rectum,
			Messages.TICode_liver, Messages.TICode_pancreas, Messages.TICode_diaphragm,
			Messages.TICode_hernia, Messages.TICode_digestiveOther
		},
		
		{
			"F", Messages.TICode_metabolic, //$NON-NLS-1$
			Messages.TICode_diabetes, Messages.TICode_thyroid, Messages.TICode_metabolicOther
		},
		
		{
			"G", Messages.TICode_infections, //$NON-NLS-1$
			Messages.TICode_simpleInfection, Messages.TICode_tuberculosis,
			Messages.TICode_hepatitis, Messages.TICode_infectionOther
		},
		
		{
			"H", Messages.TICode_urinary, //$NON-NLS-1$
			Messages.TICode_kidney, Messages.TICode_stones, Messages.TICode_ureters,
			Messages.TICode_urinaryOther
		},
		
		{
			"I", Messages.TICode_sexual, //$NON-NLS-1$
			Messages.TICode_male, Messages.TICode_vaginal, Messages.TICode_uterus,
			Messages.TICode_adnexes, Messages.TICode_cycle, Messages.TICode_mammae,
			Messages.TICode_sterilisation, Messages.TICode_sexualOther
		},
		
		{
			"K", Messages.TICode_reproduction, //$NON-NLS-1$
			Messages.TICode_pregnancyNormal, Messages.TICode_pregnancyAbnormal,
			Messages.TICode_sterility, Messages.TICode_reproductionOther
		},
		
		{
			"L", Messages.TICode_nervous, //$NON-NLS-1$
			Messages.TICode_brain, Messages.TICode_nerves, Messages.TICode_palsy,
			Messages.TICode_migraine, Messages.TICode_epilepsy, Messages.TICode_nervousOther
		},
		
		{
			"M", Messages.TICode_psyche, //$NON-NLS-1$
			Messages.TICode_sleep, Messages.TICode_psychic, Messages.TICode_psychoorganic,
			Messages.TICode_psycheOther
		},
		
		{
			"N", Messages.TICode_skin, //$NON-NLS-1$
			Messages.TICode_allergic, Messages.TICode_inflammation, Messages.TICode_ekcema,
			Messages.TICode_vaskular, Messages.TICode_psoriasis, Messages.TICode_scars,
			Messages.TICode_skinOther
		},
		
		{
			"O", Messages.TICode_orl, //$NON-NLS-1$
			Messages.TICode_nose, Messages.TICode_sinuses, Messages.TICode_mouth,
			Messages.TICode_tonsil, Messages.TICode_larynx, Messages.TICode_earform,
			Messages.TICode_middleEar, Messages.TICode_innerEar, Messages.TICode_orlOther
		},
		
		{
			"P", Messages.TICode_eye, //$NON-NLS-1$
			Messages.TICode_lid, Messages.TICode_cornea, Messages.TICode_eyemuscles,
			Messages.TICode_iris, Messages.TICode_retina, Messages.TICode_eyeOther
		},
		
		{
			"Q", Messages.TICode_jaw, //$NON-NLS-1$
			Messages.TICode_cyst, Messages.TICode_toothabscess, Messages.TICode_fibroma,
			Messages.TICode_jawOther
		},
		
		{
			"R", Messages.TICode_accidents, //$NON-NLS-1$
			Messages.TICode_accidentHead, Messages.TICode_accisdentThorax,
			Messages.TICode_accidentAbdomen, Messages.TICode_accidentArm,
			Messages.TICode_accidentLeg, Messages.TICode_AccidentOther
		},
		
		{
			"S", Messages.TICode_nonmust, //$NON-NLS-1$
			Messages.TICode_nonmust
		},
		
		{
			"T", Messages.TICode_prevention, //$NON-NLS-1$
			Messages.TICode_preventionCheck, Messages.TICode_vaccination, Messages.TICode_other
		},
		
		{
			"U", Messages.TICode_docInformed, Messages.TICode_docInformed}, //$NON-NLS-1$
		{
			"0", Messages.TICode_accessory, //$NON-NLS-1$
			Messages.TICode_right, Messages.TICode_left, Messages.TICode_acute,
			Messages.TICode_chronic, Messages.TICode_infectiuous, Messages.TICode_functional,
			Messages.TICode_neoplastic, Messages.TICode_professional
		}
	};
	
	@Override
	protected String getTableName(){
		return "None"; //$NON-NLS-1$
	}
	
	@Override
	public boolean isDragOK(){
		return !hasChildren();
	}
	
	TICode(){}
	
	public String getCodeSystemCode(){
		return "999"; //$NON-NLS-1$
	}
	
	public List<Object> getActions(Object kontext){
		// TODO Auto-generated method stub
		return null;
	}
	
}
