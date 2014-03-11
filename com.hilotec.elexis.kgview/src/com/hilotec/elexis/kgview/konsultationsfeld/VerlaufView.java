package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class VerlaufView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.VerlaufView";

	public VerlaufView() {
		super(KonsData.FLD_VERLAUF);
	}
}
