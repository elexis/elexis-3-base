package at.medevit.elexis.medicationlist.model;

public enum AbgabeTyp {
	/**
	 * Artikel werden verrechnet
	 */
	SELBSTDISPENSATION("SD", Messages.getString("AbgabeTyp.SD_Tooltip")), //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * Artikel werden nicht verrechnet
	 */
	REZEPT("RP", Messages.getString("AbgabeTyp.RP_Tooltip")), //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * Artikel werden ohne MWSt verrechnet
	 */
	APPLIKATION("AP", Messages.getString("AbgabeTyp.AP_Tooltip")); //$NON-NLS-1$ //$NON-NLS-2$
	
	public final String acronym;
	public final String tooltip;
	
	private AbgabeTyp(String acronym, String tooltip){
		this.acronym = acronym;
		this.tooltip = tooltip;
	}
	
	public AbgabeTyp getNext(){
		return values()[(ordinal() + 1) % values().length];
	}
}
