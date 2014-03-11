package com.hilotec.elexis.kgview.patientenfelder;

import com.hilotec.elexis.kgview.PatientTextFView;

public class FamAnamneseView extends PatientTextFView {
	public static final String ID = "com.hilotec.elexis.kgview.FamAnamneseView";
	public static final String DBFIELD = "FamilienAnamnese";
	
	public FamAnamneseView() {
		super(DBFIELD);
	}
}
