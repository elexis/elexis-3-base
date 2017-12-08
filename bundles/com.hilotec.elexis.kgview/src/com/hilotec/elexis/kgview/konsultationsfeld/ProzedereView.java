package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class ProzedereView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.ProzedereView";

	public ProzedereView() {
		super(KonsData.FLD_PROZEDERE, KonsData.FLD_PROZEDERE_ICPC);
	}
}
