package com.hilotec.elexis.kgview;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.MultilineFieldEditor;

/**
 * Einstelllungsseite fuer kgview-Plugin.
 * 
 * @author Antoine Kaufmann
 */
public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private static SettingsPreferenceStore store;
	public static final String CFG_EVLISTE = "hilotec/kgview/einnahmevorschriften";
	public static final String CFG_FLORDZ = "hilotec/kgview/ordnungszahlfavliste";
	public static final String CFG_MK_INCSTOP = "hilotec/kgview/mkincludestopdate";
	public static final String CFG_AKG_HEARTBEAT = "hilotec/kgview/archivkgheartbaeat";
	public static final String CFG_AKG_SCROLLPERIOD = "hilotec/kgview/archivkgscrollperiod";
	public static final String CFG_AKG_SCROLLDIST_UP = "hilotec/kgview/archivkgscrolldistup";
	public static final String CFG_AKG_SCROLLDIST_DOWN = "hilotec/kgview/archivkgscrolldistdown";
	
	static {
		store = new SettingsPreferenceStore(CoreHub.mandantCfg);
		
		// Standardwerte setzten
		store.setDefault(CFG_FLORDZ, false);
		store.setDefault(CFG_MK_INCSTOP, false);
		store.setDefault(CFG_AKG_HEARTBEAT, 10);
		store.setDefault(CFG_AKG_SCROLLPERIOD, 200);
		store.setDefault(CFG_AKG_SCROLLDIST_UP, 5);
		store.setDefault(CFG_AKG_SCROLLDIST_DOWN, 5);
	}
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(store);
	}
	
	@Override
	public void init(IWorkbench workbench){}
	
	@Override
	protected void createFieldEditors(){
		addField(new MultilineFieldEditor(CFG_EVLISTE, "Einnahmevorschriften", 5, SWT.V_SCROLL,
			true, getFieldEditorParent()));
		addField(new BooleanFieldEditor(CFG_FLORDZ, "Ordnungszahl in FML anzeigen",
			getFieldEditorParent()));
		addField(new BooleanFieldEditor(CFG_MK_INCSTOP,
			"In Medikarte bis&mit Stoppdatum anzeigen?", getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_HEARTBEAT, "Archiv KG Heartbeat",
			getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_SCROLLPERIOD, "Archiv KG Scroll Periode [ms]",
			getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_SCROLLDIST_UP,
			"Archiv KG Scroll Distanz hoch [px]", getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_SCROLLDIST_DOWN,
			"Archiv KG Scroll Distanz runter [px]", getFieldEditorParent()));
	}
	
	/**
	 * @return Konfigurierte Einnahmevorschriften im aktuellen Mandant.
	 */
	public static String[] getEinnahmevorschriften(){
		String s = CoreHub.mandantCfg.get(CFG_EVLISTE, "");
		return s.split(",");
	}
	
	/**
	 * @return
	 */
	public static boolean getOrdnungszahlInFML(){
		boolean oz = CoreHub.mandantCfg.get(CFG_FLORDZ, false);
		return oz;
	}
	
	/**
	 * @return Sollen in der gefilterten Medikarteansicht auch Medikament angezeigt werden, die das
	 *         aktuelle Datum als Stoppdatum haben?
	 */
	public static boolean getMedikarteStopdatumInkl(){
		return store.getBoolean(CFG_MK_INCSTOP);
	}
	
	/**
	 * @return Heartbeat abstand in Sekunden, fuer die Aktualisierung der ArchivKG-Ansicht.
	 */
	public int getArchivKGHeartbeat(){
		int n = store.getInt(CFG_AKG_HEARTBEAT);
		if (n < 1)
			n = 1;
		return n;
	}
	
	/**
	 * @return Fuer automatisches Scrollen in ArchivKG, Periode in Millisekunden.
	 */
	public static int getArchivKGScrollPeriod(){
		int n = store.getInt(CFG_AKG_SCROLLPERIOD);
		if (n < 50)
			n = 50;
		return n;
	}
	
	/**
	 * @return Fuer automatisches Scrollen in ArchivKG, Scrolldistanz in Pixel
	 */
	public static int getArchivKGScrollDistUp(){
		int n = store.getInt(CFG_AKG_SCROLLDIST_UP);
		if (n < 1)
			n = 1;
		return n;
	}
	
	/**
	 * @return Fuer automatisches Scrollen in ArchivKG, Scrolldistanz in Pixel
	 */
	public static int getArchivKGScrollDistDown(){
		int n = store.getInt(CFG_AKG_SCROLLDIST_DOWN);
		if (n < 1)
			n = 1;
		return n;
	}
}
