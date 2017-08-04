/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.data;

import java.util.ArrayList;
import java.util.List;

import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink.Stm;

/**
 * Compatibility datatype for old Problem style.
 *
 * The type Problem is now an extension of Episode. This class allows to load problems from the
 * old-style table. This table is now deprecated.
 *
 * @author Daniel Lutz <danlutz@watz.ch>
 *
 */

public class OldProblem extends PersistentObject {
	private static final String PROBLEM_TABLENAME = "IATRIX_PROBLEM";

	static {
		addMapping(PROBLEM_TABLENAME, "PatientID", "Bezeichnung", "Nummer", "Datum", "Procedere",
			"Status");
	}

	@Override
	public String getLabel(){
		return get("Bezeichnung");
	}

	@Override
	protected String getTableName(){
		return PROBLEM_TABLENAME;
	}

	public static OldProblem load(String id){
		return new OldProblem(id);
	}

	/**
	 * Der parameterlose Konstruktor wird nur von der Factory gebraucht und sollte nie public sein.
	 */
	protected OldProblem(){
		// empty
	}

	protected OldProblem(String id){
		super(id);
	}

	/**
	 * Return a list of old-style problems. This serves for compatibility purposes.
	 */
	public static List<OldProblem> getOldProblems(){
		Stm stm = getDefaultConnection().getStatement();
		String sql = "SELECT ID FROM " + PROBLEM_TABLENAME + ";";
		List<String> ids = stm.queryList(sql.toString(), new String[] {
			"ID"
		});
		getDefaultConnection().releaseStatement(stm);

		List<OldProblem> problems = new ArrayList<>();
		for (String id : ids) {
			OldProblem problem = load(id);
			problems.add(problem);
		}

		return problems;
	}

}
