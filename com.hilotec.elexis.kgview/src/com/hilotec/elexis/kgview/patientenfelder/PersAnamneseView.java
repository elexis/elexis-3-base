package com.hilotec.elexis.kgview.patientenfelder;

import com.hilotec.elexis.kgview.PatientTextFView;

public class PersAnamneseView extends PatientTextFView {
	public static final String ID = "com.hilotec.elexis.kgview.PersAnamneseView";
	public static final String DBFIELD = "PersAnamnese";
	
	public PersAnamneseView() {
		super(DBFIELD);
	}
}
