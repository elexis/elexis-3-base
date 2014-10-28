/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.impfplan.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.impfplan.controller.ImpfplanController;
import ch.rgw.tools.TimeTool;


public class VaccinationType extends PersistentObject {
	public static final String RECOMMENDED_AGE = "recommendedAge"; //$NON-NLS-1$
	public static final String REMARKS = "remarks"; //$NON-NLS-1$
	public static final String DELAY_REP = "delay_rep"; //$NON-NLS-1$
	public static final String DELAY3TO4 = "delay_3to4"; //$NON-NLS-1$
	public static final String DELAY2TO3 = "delay_2to3"; //$NON-NLS-1$
	public static final String DELAY1TO2 = "delay_1to2"; //$NON-NLS-1$
	public static final String PRODUCT = "product"; //$NON-NLS-1$
	public static final String NAME = "name"; //$NON-NLS-1$
	private static final String TABLENAME = "CH_ELEXIS_IMPFPLAN_VACCINATION_TYPES"; //$NON-NLS-1$
	private static final String VERSION = "0.1.0"; //$NON-NLS-1$
	private static final String createDB = "CREATE TABLE " //$NON-NLS-1$
		+ TABLENAME + " (" //$NON-NLS-1$
		+ "ID	VARCHAR(25) primary key, deleted CHAR(1) default '0', lastupdate BIGINT," //$NON-NLS-1$
		+ "name VARCHAR(80), " //$NON-NLS-1$
		+ // Name of the Vaccination, e.g. FSME
		"product VARCHAR(80), " //$NON-NLS-1$
		+ // administered product, e.g. Encepur 0.5ml i.m.
		"delay_1to2 VARCHAR(10), " //$NON-NLS-1$
		+ // delay from 1st to 2nd shot in days, e.g. 14-48 or 0 if single
			// shot
		"delay_2to3 VARCHAR(10)," //$NON-NLS-1$
		+ // delay from 2nd to 3rd shot in days or 0 if no 3rd required
		"delay_3to4 VARCHAR(10), " //$NON-NLS-1$
		+ // delay from 3rd to 4th or 0 if no 4th required
		"delay_4to5 VARCHAR(10), " //$NON-NLS-1$
		+ // delay from 4th to 5th or 0 if no 5th required
		"delay_rep VARCHAR(10), " //$NON-NLS-1$
		+ // delay until rappel ot 0 if no rappel required
		"recommendedAge VARCHAR(10)," //$NON-NLS-1$
		+ // recommended age in months for 1st, e.g. 8-12 or 48- or -24
		"remarks TEXT);" // general remarks, warnings, limitations etc. //$NON-NLS-1$
		+ "INSERT INTO " + TABLENAME + " (ID,name) VALUES('VERSION','" //$NON-NLS-1$ //$NON-NLS-2$
		+ VERSION + "');"; //$NON-NLS-1$
	
	static {
		addMapping(TABLENAME, NAME, PRODUCT, DELAY1TO2, DELAY2TO3, DELAY3TO4, DELAY_REP, REMARKS,
			RECOMMENDED_AGE);
		VaccinationType ver = load("VERSION"); //$NON-NLS-1$
		if (!ver.exists()) {
			createOrModifyTable(createDB);
		}
	}
	
	public VaccinationType(String name, String subst){
		create(null);
		set(new String[] {
			NAME, PRODUCT
		}, name, subst);
	}
	
	public static VaccinationType load(String id){
		return new VaccinationType(id);
	}
	
	@Override
	public String getLabel(){
		return new StringBuilder().append(get(NAME)).append(": ").append( //$NON-NLS-1$
			get(PRODUCT)).toString();
	}
	
	/**
	 * An input definition can be such as 12y- or 3m-2y or -20y and so on possible suffixes are:
	 * y,a,j: years, m: months, w,s: weeks. If suffix is omitted, y is assumed -x will be
	 * interpreted as 0-x, x- will be interpreted as x-150y
	 * 
	 * @param inputDef
	 * @return
	 * @throws ElexisException
	 */
	public static Tuple calcDays(String inputDef) throws ElexisException{
		inputDef = inputDef.trim().toLowerCase();
		String[] in = inputDef.split("\\s*-\\s*"); //$NON-NLS-1$
		if (in.length == 1) {
			int m = doInterpret(in[0]);
			if (inputDef.startsWith("-")) { //$NON-NLS-1$
				return new Tuple(0, m);
			} else if (inputDef.endsWith("-")) { //$NON-NLS-1$
				return new Tuple(m, 36500);
			} else {
				return new Tuple(m, m);
			}
		} else if (in.length == 2) {
			return new Tuple(doInterpret(in[0]), doInterpret(in[1]));
		} else {
			throw new ElexisException(VaccinationType.class, "Too many dashes in " //$NON-NLS-1$
				+ inputDef, 2);
		}
	}
	
	private static int doInterpret(String input) throws ElexisException{
		String s = input.trim();
		int len = s.length();
		if (len == 0) {
			return 0;
		}
		try {
			String number = s;
			int mul = 1;
			if (s.matches("[0-9]+")) { //$NON-NLS-1$
				s += "y"; //$NON-NLS-1$
				len++;
			}
			if (s.matches("[0-9]+[wmyjad]")) { //$NON-NLS-1$
				number = s.substring(0, len - 1);
				CharSequence mdef = s.subSequence(len - 1, len);
				switch (mdef.charAt(0)) {
				case 'w':
				case 's':
					mul = 7;
					break;
				case 'm':
					mul = 30;
					break;
				case 'y':
				case 'j':
				case 'a':
					mul = 365;
					break;
				case 'd':
					mul = 1;
					break;
				}
			}
			return Integer.parseInt(number) * mul;
		} catch (NumberFormatException nex) {
			throw new ElexisException(VaccinationType.class, "Can not interpret " + input, //$NON-NLS-1$
				1);
		}
	}
	
	public static List<VaccinationType> findDueFor(Patient pat) throws ElexisException{
		LinkedList<VaccinationType> ret = new LinkedList<VaccinationType>();
		Collection<VaccinationType> vaccinationsDefined = 
				ImpfplanController.allVaccs();
		HashMap<VaccinationType, List<TimeTool>> vaccs =
			new HashMap<VaccinationType, List<TimeTool>>();
		for (Vaccination v : ImpfplanController.getVaccinations(pat)) {
			List<TimeTool> l = vaccs.get(v.getVaccinationType());
			if (l == null) {
				l = new LinkedList<TimeTool>();
			}
			l.add(new TimeTool(v.get(Vaccination.DATE)));
			vaccs.put(v.getVaccinationType(), l);
		}
		
		for (VaccinationType vt : vaccinationsDefined) {
			List<TimeTool> vDone = vaccs.get(vt);
			if (vDone == null) {
				Tuple recAge = calcDays(vt.get(VaccinationType.RECOMMENDED_AGE));
				if (isInRange(pat, null, recAge)) {
					ret.add(vt);
				}
			} else {
				String v2 = vt.get(VaccinationType.DELAY1TO2);
				if (v2 != null && (!v2.equals("0")) && v2.length() > 0) { //$NON-NLS-1$
					Tuple d1to2 = calcDays(v2);
					if (vDone.size() < 2) {
						TimeTool ttFirst = new TimeTool(vDone.get(0));
						if (isInRange(pat, ttFirst, d1to2)) {
							ret.add(vt);
						}
					} else {
						String v3 = vt.get(VaccinationType.DELAY2TO3);
						if (v3 != null && (!v3.equals("0")) && v3.length() > 0) { //$NON-NLS-1$
							Tuple d2tod3 = calcDays(v3);
							if (vDone.size() < 3) {
								TimeTool ttSecond = new TimeTool(vDone.get(1));
								if (isInRange(pat, ttSecond, d2tod3)) {
									ret.add(vt);
								}
							} else {
								String v4 = vt.get(VaccinationType.DELAY3TO4);
								if (v4 != null && (!v4.equals("0")) //$NON-NLS-1$
									&& v4.length() > 0) {
									Tuple d3tod4 = calcDays(v4);
									if (vDone.size() < 4) {
										TimeTool ttThird = new TimeTool(vDone.get(2));
										if (isInRange(pat, ttThird, d3tod4)) {
											ret.add(vt);
										}
									}
								}
							}
						}
					}
				}
				
			}
			
		}
		return ret;
	}
	
	public static boolean isInRange(Patient p, TimeTool base, Tuple t){
		TimeTool now = new TimeTool();
		TimeTool bd = new TimeTool(p.getGeburtsdatum());
		int days = bd.daysTo(now);
		if (base == null) {
			base = new TimeTool(bd);
		}
		TimeTool lower = new TimeTool(base);
		lower.addDays((Integer) t.o1);
		TimeTool upper = new TimeTool(base);
		upper.addDays((Integer) t.o2);
		if (lower.isBeforeOrEqual(now)) {
			return true;
		}
		if (upper.isBeforeOrEqual(now)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	protected VaccinationType(){}
	
	protected VaccinationType(String id){
		super(id);
	}
}
