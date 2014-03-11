package com.hilotec.elexis.kgview.patientenfelder;

import com.hilotec.elexis.kgview.PatientTextFView;

public class SozAnamneseView extends PatientTextFView {
	public static final String ID = "com.hilotec.elexis.kgview.SozAnamneseView";
	private static final String DBFIELD = "Bemerkung";

	public SozAnamneseView() {
		super(DBFIELD);
	}

}
