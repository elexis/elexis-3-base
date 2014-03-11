/*******************************************************************************
 * Copyright (c) 2009-2010, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    A. Kaufmann - copied from befunde-Plugin and adapted to new data structure 
 *    G. Weirich - adapted to Eventhandling API Change in 2.1
 *    M. Descher - added copy function for value, added CSV Export for a Table
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.data.Messung;
import com.hilotec.elexis.messwerte.v2.data.MessungKonfiguration;
import com.hilotec.elexis.messwerte.v2.data.MessungTyp;
import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;

/**
 * View fuer Uebersicht ueber alle Messungen des aktuellen Patienten
 * 
 * @author Antoine Kaufmann
 * 
 */
public class MessungenUebersichtV20 extends ViewPart implements ElexisEventListener {
	private final MessungKonfiguration config;
	private ScrolledForm form;
	private final ArrayList<MessungstypSeite> seiten;
	private CTabFolder tabsfolder;
	
	private Action neuAktion;
	private Action editAktion;
	private Action copyAktion;
	private Action loeschenAktion;
	private Action exportAktion;
	private Action reloadXMLAction;
	
	public MessungenUebersichtV20(){
		config = MessungKonfiguration.getInstance();
		config.readFromXML(null);
		seiten = new ArrayList<MessungstypSeite>();
	}
	
	/**
	 * Ein einzelner Tab fuer einen bestimmten Messungstyp. In diesem wird dann eine Tabelle mit
	 * allen Messungen des aktuell ausgewaehlten Patienten angezeigt.
	 * 
	 * @author Antoine Kaufmann
	 * 
	 */
	class MessungstypSeite extends Composite {
		private final MessungTyp typ;
		private final Table table;
		private final TableColumn cols[];
		private Patient patient;
		
		/**
		 * Einzelnes Tab fuer einen bestimmten Typ Messungen mit Tabelle der Messwerte.
		 * 
		 * @param dt
		 *            Typ der Messungen
		 */
		public MessungstypSeite(Composite parent, MessungTyp dt){
			super(parent, SWT.NONE);
			typ = dt;
			
			parent.setLayout(new FillLayout());
			setLayout(new GridLayout());
			
			table = new Table(this, SWT.FULL_SELECTION | SWT.V_SCROLL);
			table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			
			cols = new TableColumn[typ.getMesswertTypen().size() + 1];
			
			// Spalten anlegen
			int i = 0;
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setText(Messages.MessungenUebersicht_Table_Datum);
			cols[i].setWidth(80);
			i++;
			for (IMesswertTyp dft : typ.getMesswertTypen()) {
				cols[i] = new TableColumn(table, SWT.NONE);
				if (dft.getUnit().equals("")) { //$NON-NLS-1$
					cols[i].setText(dft.getTitle());
				} else {
					cols[i].setText(dft.getTitle() + " [" + dft.getUnit() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				cols[i].setWidth(80);
				i++;
			}
			
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(final MouseEvent e){
					editAktion.run();
				}
			});
			
			ViewMenus menu = new ViewMenus(getViewSite());
			menu.createControlContextMenu(table, editAktion, copyAktion, loeschenAktion, neuAktion,
				exportAktion);
		}
		
		/**
		 * Seite neu zeichnen (damit veraenterte Daten aktualisiert werden)
		 */
		public void aktualisieren(){
			if (!table.isDisposed()) {
				table.removeAll();
				
				if (patient == null) {
					return;
				}
				
				for (Messung messung : Messung.getPatientMessungen(patient, typ)) {
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setData(messung);
					
					int i = 0;
					ti.setText(i++, messung.getDatum());
					for (Messwert mwrt : messung.getMesswerte()) {
						ti.setText(i++, mwrt.getDarstellungswert());
					}
				}
			}
		}
		
		/**
		 * Aktuell angezeigten Patienten festlegen. Dabei wird die Ansicht neu aufgebaut, da die
		 * Daten meist aendern.
		 */
		public void setCurPatient(Patient p){
			patient = p;
			aktualisieren();
		}
		
		/**
		 * @return Messungstyp der von dieser Seite angezeigt wird
		 */
		public MessungTyp getTyp(){
			return typ;
		}
	}
	
	/**
	 * Aktionen fuer Menuleiste und Kontextmenu initialisieren
	 */
	private void erstelleAktionen(){
		neuAktion = new Action(Messages.MessungenUebersicht_action_neu) {
			{
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_neu_ToolTip);
			}
			
			@Override
			public void run(){
				Patient p = ElexisEventDispatcher.getSelectedPatient();
				if (p == null) {
					return;
				}
				
				CTabItem tab = tabsfolder.getSelection();
				MessungstypSeite mts = (MessungstypSeite) tab.getControl();
				Messung messung = new Messung(p, mts.getTyp());
				MessungBearbeiten dialog = new MessungBearbeiten(getSite().getShell(), messung);
				if (dialog.open() != Dialog.OK) {
					messung.delete();
				}
				aktualisieren();
				
			}
		};
		
		editAktion = new Action(Messages.MessungenUebersicht_action_edit) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_edit_ToolTip);
			}
			
			@Override
			public void run(){
				CTabItem ci = tabsfolder.getSelection();
				if (ci == null) {
					return;
				}
				MessungstypSeite seite = (MessungstypSeite) ci.getControl();
				TableItem[] tableitems = seite.table.getSelection();
				if (tableitems.length == 1) {
					Messung messung = (Messung) tableitems[0].getData();
					MessungBearbeiten dialog = new MessungBearbeiten(getSite().getShell(), messung);
					dialog.open();
					aktualisieren();
				}
			}
		};
		
		copyAktion = new Action(Messages.MessungenUebersicht_action_copy) {
			{
				setImageDescriptor(Images.IMG_CLIPBOARD.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_copy_ToolTip);
			}
			
			@Override
			public void run(){
				CTabItem ci = tabsfolder.getSelection();
				if (ci == null) {
					return;
				}
				
				MessungstypSeite seite = (MessungstypSeite) ci.getControl();
				TableItem[] tableitems = seite.table.getSelection();
				if (tableitems.length == 1) {
					Messung messung = (Messung) tableitems[0].getData();
					String messungsdatum = messung.getDatum();
					TimeTool date = new TimeTool();
					String newdatum = date.toString(TimeTool.DATE_GER);
					
					if (!messungsdatum.equalsIgnoreCase(newdatum)) {
						// Nur wenn Messung nich vom selben Tag wie heute!!
						System.out.println(messung.getDatum());
						System.out.println(date.toString(TimeTool.DATE_GER));
						
						Messung messungnew = new Messung(messung.getPatient(), messung.getTyp());
						messungnew.setDatum(date.toString(TimeTool.DATE_GER));
						
						for (Messwert messwert : messung.getMesswerte()) {
							Messwert copytemp = messungnew.getMesswert(messwert.getName());
							copytemp.setWert(messwert.getWert());
						}
						
						aktualisieren();
						
					} else {
						SWTHelper.showError(Messages.MessungenUebersicht_action_copy_error,
							Messages.MessungenUebersicht_action_copy_errorMessage);
					}
					
				}
				
			}
		};
		
		exportAktion = new Action(Messages.MessungenUebersicht_action_export) {
			{
				setImageDescriptor(Images.IMG_EXPORT.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_export_ToolTip);
			}
			
			@Override
			public void run(){
				CTabItem ci = tabsfolder.getSelection();
				if (ci == null) {
					return;
				}
				
				MessungstypSeite seite = (MessungstypSeite) ci.getControl();
				TableItem[] tableitems = seite.table.getItems();
				
				try {
					String date = new TimeTool().toString(TimeTool.DATE_COMPACT);
					String filename = ci.getText() + "-export-" + date + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$
					String fqfilename =
						System.getProperty("user.home") + File.separatorChar + filename; //$NON-NLS-1$
					FileWriter writer = new FileWriter(fqfilename);
					
					// Get the headers Name (Unit); Name (Unit); ...
					Messung headermessung = (Messung) tableitems[0].getData();
					String headerstring = "datum;"; //$NON-NLS-1$
					for (Messwert messwert : headermessung.getMesswerte()) {
						headerstring = headerstring + messwert.getTyp().getName() + "(" //$NON-NLS-1$
							+ messwert.getTyp().getUnit() + ")" + ";"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					headerstring = headerstring.substring(0, headerstring.length() - 1);
					writer.append(headerstring + "\n"); //$NON-NLS-1$
					
					for (int i = 0; i < tableitems.length; i++) {
						Messung messung = (Messung) tableitems[i].getData();
						String messungstring = messung.getDatum() + ";"; //$NON-NLS-1$
						for (Messwert messwert : messung.getMesswerte()) {
							messungstring =
								messungstring
									+ messwert.getTyp().erstelleDarstellungswert(messwert) + ";"; //$NON-NLS-1$
						}
						messungstring = messungstring.substring(0, messungstring.length() - 1);
						writer.append(messungstring + "\n"); //$NON-NLS-1$
					}
					
					writer.flush();
					writer.close();
					
					SWTHelper.showInfo(MessageFormat.format(
						Messages.MessungenUebersicht_action_export_title, ci.getText()),
						MessageFormat.format(Messages.MessungenUebersicht_action_export_success,
							fqfilename));
					
				} catch (Exception e) {
					SWTHelper.showError(Messages.MessungenUebersicht_action_export_error,
						e.toString());
				}
				
			}
		};
		
		loeschenAktion = new Action(Messages.MessungenUebersicht_action_loeschen) {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_loeschen_ToolTip);
			}
			
			@Override
			public void run(){
				CTabItem ci = tabsfolder.getSelection();
				if (ci == null) {
					return;
				}
				
				MessungstypSeite seite = (MessungstypSeite) ci.getControl();
				TableItem[] tableitems = seite.table.getSelection();
				if ((tableitems.length > 0)
					&& SWTHelper.askYesNo(Messages.MessungenUebersicht_action_loeschen_delete_0,
						Messages.MessungenUebersicht_action_loeschen_delete_1)) {
					for (TableItem ti : tableitems) {
						Messung messung = (Messung) ti.getData();
						messung.delete();
					}
					aktualisieren();
				}
			}
		};
		
		reloadXMLAction = new Action(Messages.MessungenUebersicht_action_reload) {
			{
				setImageDescriptor(Images.IMG_REFRESH.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_reload_ToolTip);
			}
			
			@Override
			public void run(){
				for (CTabItem ci : tabsfolder.getItems()) {
					ci.getControl().dispose();
					ci.dispose();
				}
				for (Control c : tabsfolder.getChildren()) {
					c.dispose();
				}
				config.readFromXML(null);
				erstelleSeiten();
				aktualisieren();
			}
		};
	}
	
	/**
	 * Menuleiste generieren
	 */
	private ViewMenus erstelleMenu(IViewSite site){
		ViewMenus menu = new ViewMenus(site);
		erstelleAktionen();
		menu.createToolbar(neuAktion, editAktion, copyAktion, loeschenAktion, exportAktion);
		menu.createMenu(reloadXMLAction);
		return menu;
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new FillLayout());
		tabsfolder = new CTabFolder(body, SWT.NONE);
		tabsfolder.setLayout(new FillLayout());
		
		erstelleMenu(getViewSite());
		erstelleSeiten();
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	void erstelleSeiten(){
		for (MessungTyp t : config.getTypes()) {
			CTabItem cti = new CTabItem(tabsfolder, SWT.NONE);
			cti.setText(t.getTitle());
			MessungstypSeite mts = new MessungstypSeite(tabsfolder, t);
			seiten.add(mts);
			cti.setControl(mts);
		}
		tabsfolder.setSelection(0);
		setCurPatient(ElexisEventDispatcher.getSelectedPatient());
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Aktuell ausgewaehlten Patient festlegen
	 * 
	 * @param patient
	 *            Ausgewaehlter Patient oder null falls keiner ausgewaehlt ist.
	 */
	private void setCurPatient(Patient patient){
		if (patient == null) {
			form.setText(Messages.MessungenUebersicht_kein_Patient);
		} else {
			form.setText(patient.getLabel());
		}
		
		// Tabs benachrichtigen
		for (MessungstypSeite mts : seiten) {
			mts.setCurPatient(patient);
		}
	}
	
	/**
	 * Alle Seiten aktualisieren
	 */
	private void aktualisieren(){
		for (MessungstypSeite mts : seiten) {
			mts.aktualisieren();
		}
	}
	
	/**
	 * Dieser Event-Handler ist dafuer zustaendig, uns ueber den aktuell ausgewaehlten Patienten auf
	 * dem Laufenden zu halten, damit die Ansicht aktualisiert wird.
	 */
	public void catchElexisEvent(final ElexisEvent ev){
		UiDesk.asyncExec(new Runnable() {
			public void run(){
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					setCurPatient((Patient) ev.getObject());
				} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					setCurPatient(null);
					
				}
			}
		});
	}
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, Patient.class,
		ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED);
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
}
