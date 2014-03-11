package com.hilotec.elexis.kgview.diagnoseliste;


/**
 * View um die Diagnoseliste anzuzeigen und zu bearbeiten
 */
public class DiagnoselisteView extends DiagnoselisteBaseView {
	public static final String ID = "com.hilotec.elexis.kgview.DiagnoselisteView";

	public DiagnoselisteView() {
		super(DiagnoselisteItem.TYP_DIAGNOSELISTE);
		showDate = false;
		canAdd = false;
		canClear = true;
		allowImport = true;
		allowImportCB = false;
		allowImportDL = false;
		allowICPC = false;
	}
}
