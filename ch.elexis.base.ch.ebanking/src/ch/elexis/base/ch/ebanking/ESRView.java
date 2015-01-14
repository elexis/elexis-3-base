package ch.elexis.base.ch.ebanking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.wb.swt.TableViewerColumnSorter;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.ESRRecordDialog;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IPersistentObject;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.TimeTool;

public class ESRView extends ViewPart {
	public static final String ID = "ch.elexis.ebanking_ch.ESRView"; //$NON-NLS-1$
	private Table table;
	private Text txtFilter;
	private Label lblSUMME;
	private TableViewer tableViewer;
	
	private TimeTool startDate;
	private TimeTool endDate;
	
	private TimeTool compTT1, compTT2; // for comparison only
			
	protected final SimpleDateFormat sdf = (SimpleDateFormat) DateFormat
		.getDateInstance(DateFormat.MEDIUM);
	
	public final static ACE DISPLAY_ESR = new ACE(AccessControlDefaults.DATA,
		"ch.elexis.ebanking_ch:DisplayESR", Messages.ESRView_showESRData);
	
	private final ElexisUiEventListenerImpl eeli_user = new ElexisUiEventListenerImpl(
		Anwender.class,
		ElexisEvent.EVENT_USER_CHANGED) {
		
		public void runInUi(ElexisEvent ev){
			updateView();
		};
	};
	
	static final int DATUM_INDEX = 0;
	static final int RN_NUMMER_INDEX = 1;
	static final int BETRAG_INDEX = 2;
	static final int EINGELESEN_INDEX = 3;
	static final int VERRECHNET_INDEX = 4;
	static final int GUTGESCHRIEBEN_INDEX = 5;
	static final int PATIENT_INDEX = 6;
	static final int BUCHUNG_INDEX = 7;
	static final int DATEI_INDEX = 8;
	
	private static final String[] COLUMN_TEXTS = {
		Messages.ESRView2_date, // DATUM_INDEX
		Messages.ESRView2_billNumber, // RN_NUMMER_INDEX
		Messages.ESRView2_amount, // BETRAG
		Messages.ESRView2_readDate, // EINGELESEN_INDEX
		Messages.ESRView2_accountedDate, // VERRECHNET_INDEX
		Messages.ESRView2_addedDate, // GUTGESCHRIEBEN_INDEX
		Messages.ESRView2_patient, // PATIENT_INDEX
		Messages.ESRView2_booking, // BUCHUNG_INDEX
		Messages.ESRView2_file, // DATEI_INDEX
	};
	private Button datePeriod;
	
	public ESRView(){
		endDate = new TimeTool();
		startDate = new TimeTool();
		startDate.add(Calendar.MONTH, -3);
		
		compTT1 = new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
		compTT2 = new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
	}
	
	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout(1, false));
		
		GC gc = new GC(parent);
		FontMetrics fm = gc.getFontMetrics();
		gc.dispose();
		int dateLength = (sdf.toPattern().length() + 2) * fm.getAverageCharWidth();
		
		Composite headerContainer = new Composite(parent, SWT.NONE);
		headerContainer.setLayout(new GridLayout(2, false));
		headerContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtFilter = new Text(headerContainer, SWT.BORDER | SWT.H_SCROLL | SWT.SEARCH | SWT.CANCEL);
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtFilter
			.setMessage("Suche - #RechnungsNr, $Betrag, Text zB $>100 für alle Beträge größer 100");
		
		datePeriod = new Button(headerContainer, SWT.FLAT);
		datePeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DatePeriodSelectorDialog dpsd =
					new DatePeriodSelectorDialog(Display.getCurrent().getActiveShell(), startDate,
						endDate);
				int retVal = dpsd.open();
				if (retVal == Dialog.OK) {
					updateView();
				}
			}
		});
		
		Composite tableViewerComposite = new Composite(parent, SWT.NONE);
		tableViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TableColumnLayout tcl_tableViewerComposite = new TableColumnLayout();
		tableViewerComposite.setLayout(tcl_tableViewerComposite);
		
		tableViewer =
			new TableViewer(tableViewerComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		table = tableViewer.getTable();
		tableViewer.setUseHashlookup(true);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		// Datum
		TableViewerColumn tableViewerColumnDate = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDate = tableViewerColumnDate.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnDate, new ColumnPixelData(dateLength, true,
			false));
		tblclmnDate.setText(COLUMN_TEXTS[0]);
		new TableViewerColumnSorter(tableViewerColumnDate) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				if (!compTT1.set(esr1.get(ESRRecord.FLD_DATE)))
					compTT1.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				if (!compTT2.set(esr2.get(ESRRecord.FLD_DATE)))
					compTT2.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				return compTT1.compareTo(compTT2);
			}
		};
		
		// Rechnungs-Nummer
		TableViewerColumn tableViewerColumnBillNumber =
			new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnRnnum = tableViewerColumnBillNumber.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnRnnum, new ColumnPixelData(70, true, true));
		tblclmnRnnum.setText(COLUMN_TEXTS[1]);
		new TableViewerColumnSorter(tableViewerColumnBillNumber) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				Rechnung r1 = ((ESRRecord) e1).getRechnung();
				Rechnung r2 = ((ESRRecord) e2).getRechnung();
				String rNr1 = (r1 != null) ? r1.getNr() : "";
				String rNr2 = (r2 != null) ? r2.getNr() : "";
				return rNr1.compareTo(rNr2);
			}
		};
		
		// Betrag
		TableViewerColumn tableViewerColumnAmount = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnAmount = tableViewerColumnAmount.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnAmount, new ColumnPixelData(70, true, true));
		tblclmnAmount.setText(COLUMN_TEXTS[2]);
		new TableViewerColumnSorter(tableViewerColumnAmount) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				return esr1.getBetrag().compareTo(esr2.getBetrag());
			}
		};
		
		// Eingelesen Datum
		TableViewerColumn tableViewerColumnEingelesen =
			new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnEingelesen = tableViewerColumnEingelesen.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnEingelesen, new ColumnPixelData(dateLength,
			true, false));
		tblclmnEingelesen.setText(COLUMN_TEXTS[3]);
		new TableViewerColumnSorter(tableViewerColumnEingelesen) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				if (!compTT1.set(esr1.getEinlesedatatum()))
					compTT1.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				if (!compTT2.set(esr2.getEinlesedatatum()))
					compTT2.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				return compTT1.compareTo(compTT2);
			}
		};
		
		// Verrechnet Datum
		TableViewerColumn tableViewerColumnVerrechnet =
			new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnVerrechnet = tableViewerColumnVerrechnet.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnVerrechnet, new ColumnPixelData(dateLength,
			true, false));
		tblclmnVerrechnet.setText(COLUMN_TEXTS[4]);
		new TableViewerColumnSorter(tableViewerColumnVerrechnet) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				if (!compTT1.set(esr1.getVerarbeitungsdatum()))
					compTT1.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				if (!compTT2.set(esr2.getVerarbeitungsdatum()))
					compTT2.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				return compTT1.compareTo(compTT2);
			}
		};
		
		// Gutgeschrieben Datum
		TableViewerColumn tableViewerColumnGutgeschrieben =
			new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnGutgeschrieben = tableViewerColumnGutgeschrieben.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnGutgeschrieben, new ColumnPixelData(
			dateLength, true, false));
		tblclmnGutgeschrieben.setText(COLUMN_TEXTS[5]);
		new TableViewerColumnSorter(tableViewerColumnGutgeschrieben) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				if (!compTT1.set(esr1.getValuta()))
					compTT1.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				if (!compTT2.set(esr2.getValuta()))
					compTT2.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				return compTT1.compareTo(compTT2);
			}
		};
		
		// Patient
		TableViewerColumn tableViewerColumnPatient = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnPatient = tableViewerColumnPatient.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnPatient, new ColumnWeightData(1,
			ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnPatient.setText(COLUMN_TEXTS[6]);
		new TableViewerColumnSorter(tableViewerColumnPatient) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				Patient pat1 = ((ESRRecord) e1).getPatient();
				Patient pat2 = ((ESRRecord) e2).getPatient();
				String patLab1 = (pat1 != null) ? pat1.getLabel() : "";
				String patLab2 = (pat2 != null) ? pat2.getLabel() : "";
				return patLab1.compareTo(patLab2);
			}
		};
		
		// Buchungs Datum
		TableViewerColumn tableViewerColumnBooking = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnBuchung = tableViewerColumnBooking.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnBuchung, new ColumnWeightData(1,
			ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnBuchung.setText(COLUMN_TEXTS[7]);
		new TableViewerColumnSorter(tableViewerColumnBooking) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2){
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				if (!compTT1.set(esr1.getGebucht()))
					compTT1.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				if (!compTT2.set(esr2.getGebucht()))
					compTT2.set(TimeTool.BEGINNING_OF_UNIX_EPOCH);
				return compTT1.compareTo(compTT2);
			}
		};
		
		TableViewerColumn tableViewerColumnFile = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDatei = tableViewerColumnFile.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnDatei, new ColumnWeightData(1,
			ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnDatei.setText(COLUMN_TEXTS[8]);
		
		Composite footerComposite = new Composite(parent, SWT.NONE);
		footerComposite.setLayout(new GridLayout(2, false));
		footerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSumme = new Label(footerComposite, SWT.NONE);
		lblSumme.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		lblSumme.setBounds(0, 0, 59, 14);
		lblSumme.setText("Summe über gewählten Zeitraum ");
		
		lblSUMME = new Label(footerComposite, SWT.NONE);
		lblSUMME.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event){
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if ((sel != null) && (!sel.isEmpty())) {
					Object element = sel.getFirstElement();
					IPersistentObject po = (IPersistentObject) element;
					ESRRecordDialog erd =
						new ESRRecordDialog(getViewSite().getShell(), (ESRRecord) po);
					erd.open();
					updateView();
				}
			}
		});
		
		tableViewer.setLabelProvider(new ESRLabelProvider());
		tableViewer.setContentProvider(new ESRContentProvider(lblSUMME, DISPLAY_ESR));
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				StructuredSelection ss = (StructuredSelection) tableViewer.getSelection();
				Object firstElement = ss.getFirstElement();
				if (firstElement != null) {
					ESRRecord selRecord = (ESRRecord) firstElement;
					ElexisEventDispatcher.fireSelectionEvent(selRecord);
					Rechnung rn = selRecord.getRechnung();
					if (rn != null) {
						ElexisEventDispatcher.fireSelectionEvent(rn);
					}
				} else {
					ElexisEventDispatcher.clearSelection(ESRRecord.class);
				}
				
			}
		});
		
		ViewerFilter[] filters = new ViewerFilter[] {
			FilterSearchField.getInstance()
		};
		tableViewer.setFilters(filters);
		
		txtFilter.addKeyListener(new FilterKeyListener(txtFilter, tableViewer));
		txtFilter.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e){
				if (e.detail == SWT.CANCEL) {
					FilterSearchField.getInstance().setSearchText(null);
					tableViewer.refresh();
				}
			}
		});
		
		updateView();
		
		ElexisEventDispatcher.getInstance().addListeners(eeli_user);
	}
	
	public void updateView(){
		datePeriod.setText("Zeitraum: " + sdf.format(startDate.getTime()) + " - "
			+ sdf.format(endDate.getTime()));
		
		if (CoreHub.acl.request(DISPLAY_ESR) == true) {
			Runnable loadingArticles = new Runnable() {
				public void run(){
					
					Query<ESRRecord> qbe = new Query<ESRRecord>(ESRRecord.class);
					if (CoreHub.acl.request(AccessControlDefaults.ACCOUNTING_GLOBAL) == false) {
						if (CoreHub.actMandant != null) {
							qbe.startGroup();
							qbe.add(ESRRecord.MANDANT_ID, Query.EQUALS, CoreHub.actMandant.getId());
							qbe.or();
							qbe.add(ESRRecord.MANDANT_ID, StringConstants.EMPTY, null);
							qbe.add(ESRRecord.FLD_REJECT_CODE, Query.NOT_EQUAL,
								StringConstants.ZERO);
							qbe.endGroup();
							qbe.and();
						} else {
							qbe.insertFalse();
						}
					}
					qbe.add(ESRRecord.FLD_ID, Query.NOT_EQUAL, StringConstants.ONE);
					qbe.add(ESRRecord.FLD_DATE, Query.GREATER_OR_EQUAL, startDate.toDBString(true));
					qbe.add(ESRRecord.FLD_DATE, Query.LESS_OR_EQUAL, endDate.toDBString(true));
					List<ESRRecord> res = qbe.execute();
					tableViewer.setInput(res);
				}
			};
			Display.getCurrent().asyncExec(loadingArticles);
		} else {
			tableViewer.setInput(null);
		}
	}
	
	@Override
	public void dispose(){
		super.dispose();
		ElexisEventDispatcher.getInstance().removeListeners(eeli_user);
	}
	
	@Override
	public void setFocus(){}
	
}
