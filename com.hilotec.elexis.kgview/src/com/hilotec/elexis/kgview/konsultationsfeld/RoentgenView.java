package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class RoentgenView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.RoentgenView";

	public RoentgenView() {
		super(KonsData.FLD_ROENTGEN);
	}
}
