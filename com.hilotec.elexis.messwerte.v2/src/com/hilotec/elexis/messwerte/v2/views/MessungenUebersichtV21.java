/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
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

import com.hilotec.elexis.messwerte.v2.data.ExportData;
import com.hilotec.elexis.messwerte.v2.data.Messung;
import com.hilotec.elexis.messwerte.v2.data.MessungKonfiguration;
import com.hilotec.elexis.messwerte.v2.data.MessungTyp;
import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;

public class MessungenUebersichtV21 extends ViewPart implements ElexisEventListener {
	
	private static int DEFAULT_COL_WIDTH = 80;
	private static String DATA_PATIENT = "patient"; //$NON-NLS-1$
	private static String DATA_TYP = "typ"; //$NON-NLS-1$
	private static String DATA_VIEWER = "viewer"; //$NON-NLS-1$
	
	private MessungKonfiguration config;
	private ScrolledForm form;
	private CTabFolder tabfolder;
	private final ArrayList<TableViewer> tableViewers;
	
	private Action neuAktion;
	private Action editAktion;
	private Action copyAktion;
	private Action loeschenAktion;
	private Action exportAktion;
	private Action reloadXMLAction;
	
	public MessungenUebersichtV21(){
		config = MessungKonfiguration.getInstance();
		tableViewers = new ArrayList<TableViewer>();
	}
	
	private class CustomColumnLabelProvider extends ColumnLabelProvider {
		
		private final String messwertName;
		
		public CustomColumnLabelProvider(int columnIndex, String name){
			messwertName = name;
		}
		
		@Override
		public String getText(Object element){
			Messung m = (Messung) element;
			return m.getMesswert(messwertName).getDarstellungswert();
		}
	};
	
	private void setCurPatient(Patient patient){
		if (patient == null) {
			form.setText(Messages.MessungenUebersicht_kein_Patient);
		} else {
			form.setText(patient.getLabel());
		}
		CTabItem tab = tabfolder.getSelection();
		Control c = tab.getControl();
		MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
		refreshContent(patient, t);
	}
	
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
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		intializeView(parent);
		if (form.getCursor() == null)
			form.setCursor(new Cursor(form.getShell().getDisplay(), SWT.CURSOR_WAIT));
		config = MessungKonfiguration.getInstance();
		erstelleAktionen();
		erstelleMenu(getViewSite());
		initializeContent();
		if (form.getCursor() != null)
			form.setCursor(null);
	}
	
	@Override
	public void setFocus(){
		CTabItem tab = tabfolder.getSelection();
		Control c = tab.getControl();
		TableViewer tv = (TableViewer) c.getData(DATA_VIEWER);
		if (tv != null) {
			if (tv.getInput() == null) {
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				if (p == null) {
					p = ElexisEventDispatcher.getSelectedPatient();
				}
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				refreshContent(p, t);
			}
		}
	}
	
	private void intializeView(Composite parent){
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.setText(Messages.MessungenUebersicht_kein_Patient);
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		
		tabfolder = new CTabFolder(body, SWT.NONE);
		tabfolder.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tabfolder.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e){
				CTabFolder tf = (CTabFolder) e.widget;
				Patient p = (Patient) tf.getData(DATA_PATIENT);
				if (p == null) {
					return;
				}
				CTabItem tab = tf.getSelection();
				Control c = tab.getControl();
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				refreshContent(p, t);
			}
			
			public void widgetDefaultSelected(SelectionEvent e){
				// Auto-generated method stub, but not needed
			}
		});
		
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	private void initializeContent(){
		tableViewers.clear();
		config.readFromXML();
		for (MessungTyp t : config.getTypes()) {
			TableViewer tv = createTableViewer(tabfolder, t);
			Control c = tv.getControl();
			c.setData(DATA_TYP, t);
			c.setData(DATA_VIEWER, tv);
			tableViewers.add(tv);
			CTabItem ti = new CTabItem(tabfolder, SWT.NONE);
			ti.setText(t.getTitle());
			ti.setControl(c);
			tv.setInput(null);
			tv.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event){
					editAktion.run();
				}
			});
			ViewMenus menu = new ViewMenus(getViewSite());
			menu.createControlContextMenu(tv.getControl(), editAktion, copyAktion, loeschenAktion,
				neuAktion, exportAktion);
		}
		tabfolder.setSelection(0);
	}
	
	private void refreshContent(Patient patient, MessungTyp requestedTyp){
		
		if (patient != null) {
			if (form.getCursor() == null)
				form.setCursor(new Cursor(form.getShell().getDisplay(), SWT.CURSOR_WAIT));
			
			form.setText(patient.getLabel());
			tabfolder.setData(DATA_PATIENT, patient);
			
			MessungTyp typToRefresh = requestedTyp;
			TableViewer viewerToRefresh = null;
			
			for (TableViewer tv : tableViewers) {
				Control c = tv.getControl();
				if (!c.isDisposed()) {
					MessungTyp typ = (MessungTyp) c.getData(DATA_TYP);
					
					// bei unbekannten typen (z.B. bei reloadXML) einfach den ersten refreshen
					if (typToRefresh == null) {
						typToRefresh = typ;
						viewerToRefresh = tv;
						break;
					} else {
						if (requestedTyp.getName().equals(typ.getName())) {
							typToRefresh = typ;
							viewerToRefresh = tv;
							break;
						}
					}
				}
			}
			viewerToRefresh.setInput(Messung.getPatientMessungen(patient, typToRefresh));
			if (form.getCursor() != null)
				form.setCursor(null);
		}
	}
	
	private TableViewer createTableViewer(Composite parent, MessungTyp t){
		TableViewer viewer =
			new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.BORDER);
		createColumns(parent, viewer, t);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(new ArrayContentProvider());
		
		// Make the selection available to other views
		getSite().setSelectionProvider(viewer);
		// Set the sorter for the table
		
		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		viewer.setComparator(new MessungenComparator());
		return viewer;
	}
	
	private void createColumns(final Composite parent, final TableViewer viewer, MessungTyp t){
		// First column is for the measure date
		TableViewerColumn col;
		col =
			createTableViewerColumn(viewer, Messages.MessungenUebersicht_Table_Datum,
				DEFAULT_COL_WIDTH, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element){
				Messung m = (Messung) element;
				return m.getDatum();
			}
		});
		
		int i = 0;
		for (IMesswertTyp dft : t.getMesswertTypen()) {
			String colTitle = dft.getTitle();
			if (!dft.getUnit().equals("")) //$NON-NLS-1$
				colTitle += " [" + dft.getUnit() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
				
			col = createTableViewerColumn(viewer, colTitle, DEFAULT_COL_WIDTH, 0);
			col.setLabelProvider(new CustomColumnLabelProvider(i, dft.getName()));
			i++;
		}
	}
	
	private TableViewerColumn createTableViewerColumn(final TableViewer viewer, String title,
		int bound, final int colNumber){
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(viewer, column, colNumber));
		return viewerColumn;
	}
	
	private SelectionAdapter getSelectionAdapter(final TableViewer viewer,
		final TableColumn column, final int index){
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				MessungenComparator comparator = (MessungenComparator) viewer.getComparator();
				comparator.setColumn(0);
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(viewer.getTable().getColumn(0));
				viewer.refresh();
			}
		};
		return selectionAdapter;
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
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				if (p == null) {
					return;
				}
				
				CTabItem tab = tabfolder.getSelection();
				Control c = tab.getControl();
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				
				Messung messung = new Messung(p, t);
				MessungBearbeiten dialog = new MessungBearbeiten(getSite().getShell(), messung);
				if (dialog.open() != Dialog.OK) {
					messung.delete();
				}
				refreshContent(p, t);
			}
		};
		
		editAktion = new Action(Messages.MessungenUebersicht_action_edit) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.MessungenUebersicht_action_edit_ToolTip);
			}
			
			@Override
			public void run(){
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				if (p == null) {
					return;
				}
				
				CTabItem tab = tabfolder.getSelection();
				Control c = tab.getControl();
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				
				TableItem[] tableitems = ((Table) c).getSelection();
				if (tableitems.length == 1) {
					Messung messung = (Messung) tableitems[0].getData();
					MessungBearbeiten dialog = new MessungBearbeiten(getSite().getShell(), messung);
					if (dialog.open() == Dialog.OK) {
						refreshContent(p, t);
					}
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
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				if (p == null) {
					return;
				}
				
				CTabItem tab = tabfolder.getSelection();
				Control c = tab.getControl();
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				
				TableItem[] tableitems = ((Table) c).getSelection();
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
						messungnew.set("deleted", "0"); // kopierte Messung als gültig markieren //$NON-NLS-1$ //$NON-NLS-2$
						
						refreshContent(p, t);
						
					} else {
						SWTHelper.showError(Messages.MessungenUebersicht_action_copy_error,
							Messages.MessungenUebersicht_action_copy_errorMessage);
					}
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
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				if (p == null) {
					return;
				}
				
				CTabItem tab = tabfolder.getSelection();
				Control c = tab.getControl();
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				
				TableItem[] tableitems = ((Table) c).getSelection();
				if ((tableitems.length > 0)
					&& SWTHelper.askYesNo(Messages.MessungenUebersicht_action_loeschen_delete_0,
						Messages.MessungenUebersicht_action_loeschen_delete_1)) {
					for (TableItem ti : tableitems) {
						Messung messung = (Messung) ti.getData();
						messung.delete();
					}
					refreshContent(p, t);
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
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				CTabItem tab = tabfolder.getSelection();
				Control c = tab.getControl();
				MessungTyp t = (MessungTyp) c.getData(DATA_TYP);
				
				ExportData expData = new ExportData();
				if (p != null) {
					expData.setPatientNumberFrom(Integer.parseInt(p.getPatCode()));
					expData.setPatientNumberTo(Integer.parseInt(p.getPatCode()));
				}
				
				ExportDialog expDialog = new ExportDialog(form.getShell(), expData);
				
				if (expDialog.open() == Dialog.OK) {
					
					String label = t.getTitle();
					String date = new TimeTool().toString(TimeTool.DATE_COMPACT);
					String filename = label + "-export-" + date + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$
					
					FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
					String[] extensions = {
						"*.csv" //$NON-NLS-1$
					};
					fd.setOverwrite(true);
					fd.setFilterExtensions(extensions);
					fd.setFileName(filename);
					fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
					
					String filepath = fd.open();
					if (filepath != null) {
						
						try {
							Exporter exporter = new Exporter(expData, t, filepath);
							new ProgressMonitorDialog(form.getShell()).run(true, true, exporter);
							
							if (!exporter.wasAborted()) {
								SWTHelper.showInfo(MessageFormat.format(
									Messages.MessungenUebersicht_action_export_title, label),
									MessageFormat.format(
										Messages.MessungenUebersicht_action_export_success, label,
										filepath));
							} else {
								SWTHelper.showError(MessageFormat.format(
									Messages.MessungenUebersicht_action_export_title, label),
									MessageFormat.format(
										Messages.MessungenUebersicht_action_export_aborted, label,
										filepath));
							}
							
						} catch (InvocationTargetException e) {
							SWTHelper.showError(Messages.MessungenUebersichtV21_Error,
								e.getMessage());
						} catch (InterruptedException e) {
							SWTHelper.showInfo(Messages.MessungenUebersichtV21_Cancelled,
								e.getMessage());
						}
					} else {
						SWTHelper.showInfo(Messages.MessungenUebersichtV21_Information,
							Messages.MessungenUebersicht_action_export_filepath_error);
					}
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
				Patient p = (Patient) tabfolder.getData(DATA_PATIENT);
				if (p == null) {
					return;
				}
				for (CTabItem ci : tabfolder.getItems()) {
					ci.getControl().dispose();
					ci.dispose();
				}
				for (Control ctrl : tabfolder.getChildren()) {
					ctrl.dispose();
				}
				if (form.getCursor() == null)
					form.setCursor(new Cursor(form.getShell().getDisplay(), SWT.CURSOR_WAIT));
				initializeContent();
				refreshContent(p, null);
				if (form.getCursor() != null)
					form.setCursor(null);
			}
		};
	}
	
	/**
	 * Menuleiste generieren
	 */
	private ViewMenus erstelleMenu(IViewSite site){
		ViewMenus menu = new ViewMenus(site);
		menu.createToolbar(neuAktion, editAktion, copyAktion, loeschenAktion, exportAktion);
		menu.createMenu(reloadXMLAction);
		return menu;
	}
	
	class Exporter implements IRunnableWithProgress {
		private final ExportData expData;
		private final MessungTyp typ;
		private final String filepath;
		private Boolean aborted = false;
		
		/**
		 * LongRunningOperation constructor
		 * 
		 * @param indeterminate
		 *            whether the animation is unknown
		 */
		public Exporter(ExportData xpd, MessungTyp t, String fp){
			expData = xpd;
			typ = t;
			filepath = fp;
		}
		
		/**
		 * Runs the long running operation
		 * 
		 * @param monitor
		 *            the progress monitor
		 */
		public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException{
			monitor.beginTask(Messages.MessungenUebersichtV21_Initializing,
				IProgressMonitor.UNKNOWN);
			try {
				FileOutputStream fout = new FileOutputStream(filepath);
				OutputStreamWriter writer = new OutputStreamWriter(fout, "ISO-8859-1"); //$NON-NLS-1$
				
				ArrayList<IMesswertTyp> messwertTypen = typ.getMesswertTypen();
				
				String headerstring = "PatientenNr;Name;Vorname;Geburtsdatum;Geschlecht;datum;"; //$NON-NLS-1$
				
				for (IMesswertTyp messwertTyp : messwertTypen) {
					headerstring = headerstring + messwertTyp.getName();
					String unit = messwertTyp.getUnit();
					if (!"".equals(unit))
						headerstring += "(" //$NON-NLS-1$
							+ messwertTyp.getUnit() + ")"; //$NON-NLS-2$
						
					headerstring += ";"; //$NON-NLS-1$
					
				}
				
				headerstring = headerstring.substring(0, headerstring.length() - 1);
				writer.append(headerstring + "\n"); //$NON-NLS-1$
				
				List<Messung> messungen =
					Messung.getMessungenForExport(typ, expData.getDateFrom(), expData.getDateTo());
				monitor.beginTask(Messages.MessungenUebersicht_action_export_progress,
					messungen.size());
				for (Messung m : messungen) {
					Patient p = Patient.load(m.getPatient().getId());
					
					int curPatNr = -1;
					try {
						curPatNr = Integer.parseInt(p.getPatCode());
						
					} catch (Exception e) {}
					if ((curPatNr >= expData.getPatientNumberFrom())
						&& (curPatNr <= expData.getPatientNumberTo())) {
						monitor.subTask(p.getLabel() + " - " + m.getDatum()); //$NON-NLS-1$
						String messungstring = m.getPatient().getPatCode() + ";"; //$NON-NLS-1$
						messungstring += m.getPatient().getName() + ";"; //$NON-NLS-1$
						messungstring += m.getPatient().getVorname() + ";"; //$NON-NLS-1$
						messungstring += m.getPatient().getGeburtsdatum() + ";"; //$NON-NLS-1$
						messungstring += m.getPatient().getGeschlecht() + ";"; //$NON-NLS-1$
						messungstring += m.getDatum() + ";"; //$NON-NLS-1$
						for (Messwert messwert : m.getMesswerte()) {
							messungstring += messwert.getWert() + ";"; //$NON-NLS-1$
						}
						messungstring = messungstring.substring(0, messungstring.length() - 1);
						writer.append(messungstring + "\n"); //$NON-NLS-1$
					}
					monitor.worked(1);
					if (monitor.isCanceled()) {
						aborted = true;
						break;
					}
				}
				writer.flush();
				writer.close();
				fout.flush();
				fout.close();
				
			} catch (Exception e) {
				SWTHelper.showError(Messages.MessungenUebersicht_action_export_error, e.toString());
			}
			
			monitor.done();
		}
		
		private Boolean wasAborted(){
			return aborted;
		}
	}
}
