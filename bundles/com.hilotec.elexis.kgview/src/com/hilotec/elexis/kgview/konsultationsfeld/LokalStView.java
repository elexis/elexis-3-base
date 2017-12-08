package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class LokalStView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.LokalStView";

	public LokalStView() {
		super(KonsData.FLD_LOKSTATUS);
	}
}
