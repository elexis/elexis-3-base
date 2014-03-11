package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class AllgemeinStView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.AllgemeinStView";
	
	public AllgemeinStView() {
		super(KonsData.FLD_ALLGSTATUS);
	}
}
