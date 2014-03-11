package com.hilotec.elexis.kgview.konsultationsfeld;

import com.hilotec.elexis.kgview.KonsDataFView;
import com.hilotec.elexis.kgview.data.KonsData;

public class EKGView extends KonsDataFView {
	public static final String ID = "com.hilotec.elexis.kgview.EKGView";

	public EKGView() {
		super(KonsData.FLD_EKG);
	}
}
