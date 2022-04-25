/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Splits up Values from a Mythic22 output into defaultValues and
 * haematologicalValues haematologicalValues all have the exact same structure,
 * while defaultValues can have different amounts of values
 *
 * @author Christian
 *
 */
public class Mythic22Result {

	// special fields regarding matrices, need to be processed separately
	public static final String LMNEMATRIX = "LMNE MATRIX";
	public static final String LMNESHADEMATRIX = "LMNE SHADE MATRIX";
	public static final String THRES5DLMNEMATRIX = "THRES 5D LMNE MATRIX";

	// regular fields of a mythic22 output
	public static final String[] FIELDS = { "MYTHIC", "DATE", "TIME", "MODE", "UNIT", "SEQ", "SID", "PID", "ID", "TYPE",
			"TEST", "OPERATOR", "PREL", "CYCLE", "WBC CURVE", "WBC THRESHOLDS", "RBC CURVE", "RBC THRESHOLDS",
			"PLT CURVE", "PLT THRESHOLDS", "ALARMS", "INTERPRETIVE_WBC", "INTERPRETIVE_RBC", "INTERPRETIVE_PLT",
			"COMMENT", "END_RESULT", LMNEMATRIX, LMNESHADEMATRIX, THRES5DLMNEMATRIX };

	// regular haematological fields of a mythic22 output
	public static final String[] HAEMATOLOGICALFIELDS = { "WBC", "RBC", "HGB", "HCT", "MCV", "MCH", "MCHC", "RDW",
			"PLT", "MPV", "PCT", "PDW", "LYM", "MON", "NEU", "LYM%", "MON%", "NEU%", "EOS", "BAS", "EOS%", "BAS%" };

	// Member variables
	private LinkedList<HaematologicalValue> m_haematologicalValues;

	private HashMap<String, LinkedList<String>> m_defaultTypeValues;

//	private LinkedList<DefaultTypeValue> m_defaultTypeValues;

	public Mythic22Result(LinkedList<HaematologicalValue> haematologicalValues,
			HashMap<String, LinkedList<String>> defaultTypeValues) {
		super();
		m_haematologicalValues = haematologicalValues;
		m_defaultTypeValues = defaultTypeValues;
	}

	public Mythic22Result() {
		super();
	}

	public LinkedList<HaematologicalValue> getHaematologicalValues() {
		return m_haematologicalValues;
	}

	public void setHaematologicalValues(LinkedList<HaematologicalValue> haematologicalValues) {
		m_haematologicalValues = haematologicalValues;
	}

	public HashMap<String, LinkedList<String>> getDefaultTypeValues() {
		return m_defaultTypeValues;
	}

	public void setDefaultTypeValues(HashMap<String, LinkedList<String>> defaultTypeValues) {
		m_defaultTypeValues = defaultTypeValues;
	}

}
