/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    D. Lutz    - show records in a table
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.AbstractDataLoaderJob;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.actions.PersistentObjectLoader.QueryFilter;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewer.PoDoubleClickListener;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ESRView2 extends ViewPart implements IActivationListener {
	public static final String ID = "ch.elexis.banking.ESRView2"; //$NON-NLS-1$

	private static final String JOB_NAME = "ESR-Loader2"; //$NON-NLS-1$

	private static final int DATUM_INDEX = 0;
	private static final int RN_NUMMER_INDEX = 1;
	private static final int BETRAG_INDEX = 2;
	private static final int EINGELESEN_INDEX = 3;
	private static final int VERRECHNET_INDEX = 4;
	private static final int GUTGESCHRIEBEN_INDEX = 5;
	private static final int PATIENT_INDEX = 6;
	private static final int BUCHUNG_INDEX = 7;
	private static final int DATEI_INDEX = 8;

	private static final String[] COLUMN_TEXTS = { Messages.ESRView2_date, // DATUM_INDEX
			Messages.ESRView2_billNumber, // RN_NUMMER_INDEX
			Messages.ESRView2_amount, // BETRAG
			Messages.ESRView2_readDate, // EINGELESEN_INDEX
			Messages.ESRView2_accountedDate, // VERRECHNET_INDEX
			Messages.ESRView2_addedDate, // GUTGESCHRIEBEN_INDEX
			Messages.ESRView2_patient, // PATIENT_INDEX
			Messages.ESRView2_booking, // BUCHUNG_INDEX
			Messages.ESRView2_file, // DATEI_INDEX
	};
	private static final int[] COLUMN_WIDTHS = { 60, // DATUM_INDEX
			50, // RN_NUMMER_INDEX
			50, // BETRAG
			80, // EINGELESEN_INDEX
			80, // VERRECHNET_INDEX
			80, // GUTGESCHRIEBEN_INDEX
			150, // PATIENT_INDEX
			80, // BUCHUNG_INDEX
			80, // DATEI_INDEX
	};

	CommonViewer cv;
	ViewerConfigurer vc;
	// ESRLoader esrloader;
	FlatDataLoader fdl;

	Query<ESRRecord> qbe;
	// private Action loadESRFile;
	private ViewMenus menus;
	private ESRSelectionListener esrl;

	public ESRView2() {

		// Hub.acl.grantForSelf(DISPLAY_ESR);
	}

	@Override
	public void dispose() {
		// Hub.acl.revokeFromSelf(DISPLAY_ESR);
		GlobalEventDispatcher.removeActivationListener(this, getViewSite().getPart());
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		cv = new CommonViewer();
		qbe = new Query<ESRRecord>(ESRRecord.class);
		fdl = new FlatDataLoader(cv, qbe);
		/*
		 * esrloader = (ESRLoader) JobPool.getJobPool().getJob(JOB_NAME); if (esrloader
		 * == null) { esrloader = new ESRLoader(qbe);
		 * JobPool.getJobPool().addJob(esrloader); }
		 */
		fdl.addQueryFilter(new QueryFilter() {

			public void apply(Query<? extends PersistentObject> qbe) {
				if (CoreHub.acl.request(AccessControlDefaults.ACCOUNTING_GLOBAL) == false) {
					if (CoreHub.actMandant != null) {
						qbe.startGroup();
						qbe.add(ESRRecord.MANDANT_ID, Query.EQUALS, CoreHub.actMandant.getId());
						qbe.or();
						qbe.add(ESRRecord.MANDANT_ID, StringConstants.EMPTY, null);
						qbe.add(ESRRecord.FLD_REJECT_CODE, Query.NOT_EQUAL, StringConstants.ZERO);
						qbe.endGroup();
						qbe.and();
					} else {
						qbe.insertFalse();
					}
				}

			}
		});

		vc = new ViewerConfigurer(fdl, new ESRLabelProvider(),
				new DefaultControlFieldProvider(cv, new String[] { "Datum" //$NON-NLS-1$
				}), new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		cv.create(vc, parent, SWT.None, getViewSite());

		createColumns(cv.getViewerWidget());

		// JobPool.getJobPool().activate(JOB_NAME, Job.SHORT);
		makeActions();
		menus = new ViewMenus(getViewSite());
		menus.createToolbar(/* loadESRFile */);
		menus.createMenu(/* loadESRFile */);
		esrl = new ESRSelectionListener();
		cv.addDoubleClickListener(new PoDoubleClickListener() {
			public void doubleClicked(PersistentObject obj, CommonViewer cv) {
				ESRRecordDialog erd = new ESRRecordDialog(getViewSite().getShell(), (ESRRecord) obj);
				if (erd.open() == Dialog.OK) {
					cv.notify(CommonViewer.Message.update);
				}
			}

		});
		GlobalEventDispatcher.addActivationListener(this, getViewSite().getPart());

	}

	private void createColumns(StructuredViewer viewer) {
		if (!(viewer instanceof TableViewer)) {
			// no valid viewer, don't create columns
			return;
		}

		TableViewer tableViewer = (TableViewer) viewer;
		Table table = tableViewer.getTable();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (int i = 0; i < COLUMN_TEXTS.length; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(COLUMN_TEXTS[i]);
			column.setWidth(COLUMN_WIDTHS[i]);
		}
	}

	private boolean isOldShown = false;

	@Override
	public void setFocus() {
		if (!isOldShown) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Ansicht veraltet", "Die Ansicht "
					+ getTitle()
					+ " ist veraltet, und wird nicht mehr unterstützt. Bitte verwenden Sie die ESR Ansicht.");
			isOldShown = true;
		}
	}

	class ESRLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
		DecimalFormat df = new DecimalFormat("###0.00"); //$NON-NLS-1$

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String text = StringUtils.EMPTY;

			if (element instanceof ESRRecord) {
				ESRRecord rec = (ESRRecord) element;

				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					switch (columnIndex) {
					case DATUM_INDEX:
						text = rec.get("Datum"); //$NON-NLS-1$
						break;
					case RN_NUMMER_INDEX:
						text = "Summe"; //$NON-NLS-1$
						break;
					case BETRAG_INDEX:
						text = rec.getBetrag().getAmountAsString();
						break;
					case DATEI_INDEX:
						text = rec.getFile();
						break;
					}
				} else {
					switch (columnIndex) {
					case DATUM_INDEX:
						text = rec.get("Datum"); //$NON-NLS-1$
						break;
					case RN_NUMMER_INDEX:
						Rechnung rn = rec.getRechnung();
						if (rn != null) {
							text = rn.getNr();
						}
						break;
					case BETRAG_INDEX:
						text = rec.getBetrag().getAmountAsString();
						break;
					case EINGELESEN_INDEX:
						text = rec.getEinlesedatatum();
						break;
					case VERRECHNET_INDEX:
						text = rec.getVerarbeitungsdatum();
						break;
					case GUTGESCHRIEBEN_INDEX:
						text = rec.getValuta();
						break;
					case PATIENT_INDEX:
						text = rec.getPatient().getLabel();
						break;
					case BUCHUNG_INDEX:
						String dat = rec.getGebucht();
						if (StringTool.isNothing(dat)) {
							text = Messages.ESRView2_notbooked;
						} else {
							text = new TimeTool(dat).toString(TimeTool.DATE_GER);
						}
						break;
					case DATEI_INDEX:
						text = rec.getFile();
						break;
					}
				}
			}

			return text;
		}

		public Color getForeground(Object element, int columnIndex) {
			return UiDesk.getColor(UiDesk.COL_BLACK);
		}

		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof ESRRecord) {
				ESRRecord rec = (ESRRecord) element;
				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					return UiDesk.getColor(UiDesk.COL_GREEN);
				}
				String buch = rec.getGebucht();
				if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
					if (StringTool.isNothing(buch)) {
						return UiDesk.getColor(UiDesk.COL_GREY);
					}
					return UiDesk.getColor(UiDesk.COL_WHITE);
				}
				return UiDesk.getColor(UiDesk.COL_RED);
			}
			return UiDesk.getColor(UiDesk.COL_SKYBLUE);
		}

	}

	class ESRLoader extends AbstractDataLoaderJob {
		// public static final int ORDER_RNNUMMER = 1;
		// public static final int ORDER_

		Query<ESRRecord> qbe;

		ESRLoader(Query<ESRRecord> qbe) {
			super(JOB_NAME, qbe, new String[] { "Datum" //$NON-NLS-1$
			});
			this.qbe = qbe;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor) {
			monitor.beginTask(Messages.ESRView2_loadingESR, SWT.INDETERMINATE);

			qbe.clear();
			vc.getControlFieldProvider().setQuery(qbe);
			qbe.orderBy(true, new String[] { "Datum", "Gebucht" //$NON-NLS-1$ //$NON-NLS-2$
			});
			List<ESRRecord> list = qbe.execute();
			result = list.toArray();
			monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public int getSize() {
			return PersistentObject.getConnection().queryInt("SELECT COUNT(0) FROM ESRRECORDS"); //$NON-NLS-1$

		}

	}

	private void makeActions() {
		/*
		 * loadESRFile=new Action("ESR-Datei einlesen"){ {
		 * setToolTipText("Auswahl einer von der Bank heruntergeladenen ESR-Datei zum Einlesen"
		 * ); setImageDescriptor(Desk.theImageRegistry.getDescriptor(Desk.IMG_IMPORT));
		 * }
		 *
		 * @Override public void run(){ FileDialog fld=new
		 * FileDialog(getViewSite().getShell(),SWT.OPEN);
		 * fld.setText("ESR Datei auswählen"); String filename=fld.open();
		 * if(filename!=null){ ESRFile esrf=new ESRFile(); Result<List<ESRRecord>>
		 * result=esrf.read(filename); if(result.isOK()){ for(ESRRecord
		 * rec:result.get()){ if(rec.getRejectCode().equals(ESRRecord.REJECT.OK)){
		 * if(rec.getTyp().equals(ESRRecord.MODE.Summenrecord)){
		 * Hub.log.log("ESR eingelesen. Summe "+rec.getBetrag(), Log.INFOS); }else if(
		 * (rec.getTyp().equals(ESRRecord.MODE.Storno_edv)) ||
		 * (rec.getTyp().equals(ESRRecord.MODE.Storno_Schalter))){ Rechnung
		 * rn=rec.getRechnung(); Money zahlung=rec.getBetrag().negate();
		 * rn.addZahlung(zahlung,
		 * "Storno für rn "+rn.getNr()+" / "+rec.getPatient().getPatCode());
		 * rec.setGebucht(null); }else{ Rechnung rn=rec.getRechnung();
		 * if(rn.getStatus()==RnStatus.BEZAHLT){
		 * if(MessageDialog.openConfirm(getViewSite().getShell(),
		 * "Rechnung schon bezahlt",
		 * "Rechnung "+rn.getNr()+" ist bereits bezahlt. Trotzdem buchen?")==false){
		 * continue; } } Money zahlung=rec.getBetrag(); Money
		 * offen=rn.getOffenerBetrag(); if(zahlung.isMoreThan(offen)){
		 * if(MessageDialog.openConfirm(getViewSite().getShell(), "Betrag zu hoch",
		 * "Die Zahlung für Rechnung "
		 * +rn.getNr()+" übersteigt den offenen Betrag. Trotzdem buchen?")==false){
		 * continue; } }
		 *
		 * rn.addZahlung(zahlung,
		 * "VESR für rn "+rn.getNr()+" / "+rec.getPatient().getPatCode());
		 * rec.setGebucht(null); } } } }else{
		 * result.display("Fehler beim ESR-Einlesen:"); } }
		 * JobPool.getJobPool().activate(JOB_NAME, Job.SHORT);
		 * //cv.notify(CommonViewer.Message.update); } };
		 */
	}

	public void activation(boolean mode) {
		// TODO Auto-generated method stub

	}

	public void visible(boolean mode) {
		esrl.activate(mode);
	}

}
