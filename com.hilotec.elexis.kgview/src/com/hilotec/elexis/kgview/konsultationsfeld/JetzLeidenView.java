package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class JetzLeidenView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.JetzLeidenView";

	public JetzLeidenView() {
		super(KonsData.FLD_JETZLEIDEN, KonsData.FLD_JETZLEIDEN_ICPC);
	}
}
