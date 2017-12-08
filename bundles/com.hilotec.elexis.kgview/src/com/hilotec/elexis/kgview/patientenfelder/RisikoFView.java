package com.hilotec.elexis.kgview.patientenfelder;

import com.hilotec.elexis.kgview.PatientTextFView;

public class RisikoFView extends PatientTextFView {
	public static final String ID = "com.hilotec.elexis.kgview.RisikoFView";
	public static final String DBFIELD = "Risiken";
	
	public RisikoFView() {
		super(DBFIELD);
	}
}
