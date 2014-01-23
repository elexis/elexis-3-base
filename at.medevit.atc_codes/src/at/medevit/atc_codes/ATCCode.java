/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.atc_codes;

import java.io.Serializable;

public class ATCCode implements Serializable {
	
	private static final long serialVersionUID = 7610286245146955774L;
	
	public enum DDD_UNIT_TYPE {
		G, MG, MCG, U, TU, MU, MMOL, ML, LSU, TABLET
	};
	
	final public String name;
	final public String atcCode;
	final public int level;
	final public float dailyDefinedDose;
	final public DDD_UNIT_TYPE dailyDefinedDoseUnitType;
	final public String administrativeCode;
	final public String dddComment;
	
	public ATCCode(String atcCode, String name, int level, float dailyDefinedDose,
		DDD_UNIT_TYPE dddut, String admCode, String dddComment){
		this.name = name;
		this.atcCode = atcCode;
		this.level = level;
		this.dailyDefinedDose = dailyDefinedDose;
		this.dailyDefinedDoseUnitType = dddut;
		this.administrativeCode = admCode;
		this.dddComment = dddComment;
	}
}
