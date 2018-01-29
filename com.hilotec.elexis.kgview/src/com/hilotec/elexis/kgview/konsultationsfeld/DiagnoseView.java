package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class DiagnoseView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.DiagnoseView";

	public DiagnoseView() {
		super(KonsData.FLD_DIAGNOSE, KonsData.FLD_DIAGNOSE_ICPC);
	}
}
