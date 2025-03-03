package com.hilotec.elexis.kgview;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.MultilineFieldEditor;

/**
 * Einstelllungsseite fuer kgview-Plugin.
 *
 * @author Antoine Kaufmann
 */
public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private static ConfigServicePreferenceStore store;
	public static final String CFG_EVLISTE = "hilotec/kgview/einnahmevorschriften";
	public static final String CFG_FLORDZ = "hilotec/kgview/ordnungszahlfavliste";
	public static final String CFG_MK_INCSTOP = "hilotec/kgview/mkincludestopdate";
	public static final String CFG_AKG_HEARTBEAT = "hilotec/kgview/archivkgheartbaeat";
	public static final String CFG_AKG_SCROLLPERIOD = "hilotec/kgview/archivkgscrollperiod";
	public static final String CFG_AKG_SCROLLDIST_UP = "hilotec/kgview/archivkgscrolldistup";
	public static final String CFG_AKG_SCROLLDIST_DOWN = "hilotec/kgview/archivkgscrolldistdown";

	static {
		store = new ConfigServicePreferenceStore(Scope.MANDATOR);

		// Standardwerte setzten
		store.setDefault(CFG_FLORDZ, false);
		store.setDefault(CFG_MK_INCSTOP, false);
		store.setDefault(CFG_AKG_HEARTBEAT, 10);
		store.setDefault(CFG_AKG_SCROLLPERIOD, 200);
		store.setDefault(CFG_AKG_SCROLLDIST_UP, 5);
		store.setDefault(CFG_AKG_SCROLLDIST_DOWN, 5);
	}

	public Preferences() {
		super(GRID);
		setPreferenceStore(store);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		addField(new MultilineFieldEditor(CFG_EVLISTE, "Einnahmevorschriften", 5, SWT.V_SCROLL, true,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(CFG_FLORDZ, "Ordnungszahl in FML anzeigen", getFieldEditorParent()));
		addField(new BooleanFieldEditor(CFG_MK_INCSTOP, "In Medikarte bis/mit Stoppdatum anzeigen?",
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_HEARTBEAT, "Archiv KG Heartbeat", getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_SCROLLPERIOD, "Archiv KG Scroll Periode [ms]", getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_SCROLLDIST_UP, "Archiv KG Scroll Distanz hoch [px]",
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(CFG_AKG_SCROLLDIST_DOWN, "Archiv KG Scroll Distanz runter [px]",
				getFieldEditorParent()));

		Button migrationBtn = new Button(getFieldEditorParent().getParent(), SWT.PUSH);
		migrationBtn.setText("Diagnosen Migration");
		migrationBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
				try {
					progressDialog.run(true, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							DiagnosisMigrator migrator = new DiagnosisMigrator();
							migrator.migrate(monitor);
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					MessageDialog.openError(getShell(), "Diagnosen konvertieren",
							"Fehler beim erzeugen der strukturierten Diagnosen.");
					LoggerFactory.getLogger(getClass()).error("Error creating structured diagnosis", e);
				}
			}
		});

		migrationBtn = new Button(getFieldEditorParent().getParent(), SWT.PUSH);
		migrationBtn.setText("ArchivKG Migration");
		migrationBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
				try {
					progressDialog.run(true, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							ArchivKGMigrator migrator = new ArchivKGMigrator();
							migrator.migrate(monitor);
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					MessageDialog.openError(getShell(), "ArchivKG konvertieren",
							"Fehler beim migrieren der Konsultationen.");
					LoggerFactory.getLogger(getClass()).error("Error migrating encounters", e);
				}
			}
		});
	}

	/**
	 * @return Konfigurierte Einnahmevorschriften im aktuellen Mandant.
	 */
	public static String[] getEinnahmevorschriften() {
		String s = ConfigServiceHolder.getMandator(CFG_EVLISTE, StringUtils.EMPTY);
		return s.split(",");
	}

	/**
	 * @return
	 */
	public static boolean getOrdnungszahlInFML() {
		boolean oz = ConfigServiceHolder.getMandator(CFG_FLORDZ, false);
		return oz;
	}

	/**
	 * @return Sollen in der gefilterten Medikarteansicht auch Medikament angezeigt
	 *         werden, die das aktuelle Datum als Stoppdatum haben?
	 */
	public static boolean getMedikarteStopdatumInkl() {
		return store.getBoolean(CFG_MK_INCSTOP);
	}

	/**
	 * @return Heartbeat abstand in Sekunden, fuer die Aktualisierung der
	 *         ArchivKG-Ansicht.
	 */
	public int getArchivKGHeartbeat() {
		int n = store.getInt(CFG_AKG_HEARTBEAT);
		if (n < 1)
			n = 1;
		return n;
	}

	/**
	 * @return Fuer automatisches Scrollen in ArchivKG, Periode in Millisekunden.
	 */
	public static int getArchivKGScrollPeriod() {
		int n = store.getInt(CFG_AKG_SCROLLPERIOD);
		if (n < 50)
			n = 50;
		return n;
	}

	/**
	 * @return Fuer automatisches Scrollen in ArchivKG, Scrolldistanz in Pixel
	 */
	public static int getArchivKGScrollDistUp() {
		int n = store.getInt(CFG_AKG_SCROLLDIST_UP);
		if (n < 1)
			n = 1;
		return n;
	}

	/**
	 * @return Fuer automatisches Scrollen in ArchivKG, Scrolldistanz in Pixel
	 */
	public static int getArchivKGScrollDistDown() {
		int n = store.getInt(CFG_AKG_SCROLLDIST_DOWN);
		if (n < 1)
			n = 1;
		return n;
	}
}
