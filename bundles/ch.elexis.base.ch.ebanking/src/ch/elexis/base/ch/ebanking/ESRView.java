package ch.elexis.base.ch.ebanking;

import static ch.elexis.base.ch.ebanking.EBankingACLContributor.DISPLAY_ESR;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.wb.swt.TableViewerColumnSorter;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.ESRRecordDialog;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IUser;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.StringTool;
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

	protected final SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM);

	@Optional
	@Inject
	void activeUser(IUser user) {
		Display.getDefault().asyncExec(() -> {
			updateView();
		});
	}

	private enum SELECTION_TYPE {
		NOTPOSTED, LASTMONTH, THISMONTH, LASTWEEK, THISWEEK, PERIOD
	};

	private SELECTION_TYPE selectionType = SELECTION_TYPE.NOTPOSTED;

	static final int DATUM_INDEX = 0;
	static final int RN_NUMMER_INDEX = 1;
	static final int BETRAG_INDEX = 2;
	static final int EINGELESEN_INDEX = 3;
	static final int VERRECHNET_INDEX = 4;
	static final int GUTGESCHRIEBEN_INDEX = 5;
	static final int PATIENT_INDEX = 6;
	static final int BUCHUNG_INDEX = 7;
	static final int DATEI_INDEX = 8;

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

	private Button btnDatePeriod;
	private Button btnNotPosted;
	private Button btnThisMonth;
	private Button btnLastMonth;
	private Button btnThisWeek;
	private Button btnLastWeek;

	public ESRView() {
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
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		GC gc = new GC(parent);
		FontMetrics fm = gc.getFontMetrics();
		gc.dispose();
		int dateLength = (sdf.toPattern().length() + 2) * fm.getAverageCharWidth();

		Composite headerContainer = new Composite(parent, SWT.NONE);
		headerContainer.setLayout(new GridLayout(7, false));
		headerContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		txtFilter = new Text(headerContainer, SWT.BORDER | SWT.H_SCROLL | SWT.SEARCH | SWT.CANCEL);
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtFilter.setMessage("Suche - #RechnungsNr, $Betrag, Text zB $>100 für alle Beträge größer 100");

		Listener selectionListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				selectionType = (SELECTION_TYPE) event.widget.getData();
				updateView();
			}
		};

		btnNotPosted = new Button(headerContainer, SWT.FLAT | SWT.TOGGLE);
		btnNotPosted.setToolTipText("Noch nicht verbuchte Zahlungen");
		btnNotPosted.setText("Nicht verbucht");
		btnNotPosted.setData(SELECTION_TYPE.NOTPOSTED);
		btnNotPosted.addListener(SWT.Selection, selectionListener);

		btnThisMonth = new Button(headerContainer, SWT.FLAT | SWT.TOGGLE);
		btnThisMonth.setToolTipText("Dieser Monat");
		btnThisMonth.setText("DM");
		btnThisMonth.setData(SELECTION_TYPE.THISMONTH);
		btnThisMonth.addListener(SWT.Selection, selectionListener);

		btnLastMonth = new Button(headerContainer, SWT.FLAT | SWT.TOGGLE);
		btnLastMonth.setToolTipText("Letzter Monat");
		btnLastMonth.setText("LM");
		btnLastMonth.setData(SELECTION_TYPE.LASTMONTH);
		btnLastMonth.addListener(SWT.Selection, selectionListener);

		btnThisWeek = new Button(headerContainer, SWT.FLAT | SWT.TOGGLE);
		btnThisWeek.setToolTipText("Diese Woche");
		btnThisWeek.setText("DW");
		btnThisWeek.setData(SELECTION_TYPE.THISWEEK);
		btnThisWeek.addListener(SWT.Selection, selectionListener);

		btnLastWeek = new Button(headerContainer, SWT.FLAT | SWT.TOGGLE);
		btnLastWeek.setToolTipText("Letzte Woche");
		btnLastWeek.setText("LW");
		btnLastWeek.setData(SELECTION_TYPE.LASTWEEK);
		btnLastWeek.addListener(SWT.Selection, selectionListener);

		btnDatePeriod = new Button(headerContainer, SWT.FLAT | SWT.TOGGLE);
		btnDatePeriod.setToolTipText("Selektion über Zeitraum");
		btnDatePeriod.setData(SELECTION_TYPE.PERIOD);
		btnDatePeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectionType = (SELECTION_TYPE) event.widget.getData();

				DatePeriodSelectorDialog dpsd = new DatePeriodSelectorDialog(Display.getCurrent().getActiveShell(),
						startDate, endDate);
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

		tableViewer = new TableViewer(tableViewerComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		table = tableViewer.getTable();
		tableViewer.setUseHashlookup(true);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Datum
		TableViewerColumn tableViewerColumnDate = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDate = tableViewerColumnDate.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnDate, new ColumnPixelData(dateLength, true, false));
		tblclmnDate.setText(COLUMN_TEXTS[0]);
		new TableViewerColumnSorter(tableViewerColumnDate) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
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
		TableViewerColumn tableViewerColumnBillNumber = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnRnnum = tableViewerColumnBillNumber.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnRnnum, new ColumnPixelData(70, true, true));
		tblclmnRnnum.setText(COLUMN_TEXTS[1]);
		new TableViewerColumnSorter(tableViewerColumnBillNumber) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Rechnung r1 = ((ESRRecord) e1).getRechnung();
				Rechnung r2 = ((ESRRecord) e2).getRechnung();
				String rNr1 = (r1 != null) ? r1.getNr() : StringUtils.EMPTY;
				String rNr2 = (r2 != null) ? r2.getNr() : StringUtils.EMPTY;
				return StringTool.compareNumericStrings(rNr1, rNr2);
			}
		};

		// Betrag
		TableViewerColumn tableViewerColumnAmount = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnAmount = tableViewerColumnAmount.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnAmount, new ColumnPixelData(70, true, true));
		tblclmnAmount.setText(COLUMN_TEXTS[2]);
		new TableViewerColumnSorter(tableViewerColumnAmount) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				ESRRecord esr1 = (ESRRecord) e1;
				ESRRecord esr2 = (ESRRecord) e2;
				return esr1.getBetrag().compareTo(esr2.getBetrag());
			}
		};

		// Eingelesen Datum
		TableViewerColumn tableViewerColumnEingelesen = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnEingelesen = tableViewerColumnEingelesen.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnEingelesen, new ColumnPixelData(dateLength, true, false));
		tblclmnEingelesen.setText(COLUMN_TEXTS[3]);
		new TableViewerColumnSorter(tableViewerColumnEingelesen) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
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
		TableViewerColumn tableViewerColumnVerrechnet = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnVerrechnet = tableViewerColumnVerrechnet.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnVerrechnet, new ColumnPixelData(dateLength, true, false));
		tblclmnVerrechnet.setText(COLUMN_TEXTS[4]);
		new TableViewerColumnSorter(tableViewerColumnVerrechnet) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
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
		TableViewerColumn tableViewerColumnGutgeschrieben = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnGutgeschrieben = tableViewerColumnGutgeschrieben.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnGutgeschrieben, new ColumnPixelData(dateLength, true, false));
		tblclmnGutgeschrieben.setText(COLUMN_TEXTS[5]);
		new TableViewerColumnSorter(tableViewerColumnGutgeschrieben) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
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
		tcl_tableViewerComposite.setColumnData(tblclmnPatient,
				new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnPatient.setText(COLUMN_TEXTS[6]);
		new TableViewerColumnSorter(tableViewerColumnPatient) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Patient pat1 = ((ESRRecord) e1).getPatient();
				Patient pat2 = ((ESRRecord) e2).getPatient();
				String patLab1 = (pat1 != null) ? pat1.getLabel() : StringUtils.EMPTY;
				String patLab2 = (pat2 != null) ? pat2.getLabel() : StringUtils.EMPTY;
				return patLab1.compareTo(patLab2);
			}
		};

		// Buchungs Datum
		TableViewerColumn tableViewerColumnBooking = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnBuchung = tableViewerColumnBooking.getColumn();
		tcl_tableViewerComposite.setColumnData(tblclmnBuchung,
				new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnBuchung.setText(COLUMN_TEXTS[7]);
		new TableViewerColumnSorter(tableViewerColumnBooking) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
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
		tcl_tableViewerComposite.setColumnData(tblclmnDatei,
				new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnDatei.setText(COLUMN_TEXTS[8]);

		Composite footerComposite = new Composite(parent, SWT.NONE);
		footerComposite.setLayout(new GridLayout(2, false));
		footerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblSumme = new Label(footerComposite, SWT.NONE);
		lblSumme.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		lblSumme.setBounds(0, 0, 59, 14);
		lblSumme.setText("Summe über gewählte Einträge ");

		lblSUMME = new Label(footerComposite, SWT.NONE);
		lblSUMME.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if ((sel != null) && (!sel.isEmpty())) {
					Object element = sel.getFirstElement();
					PersistentObject po = (PersistentObject) element;
					ESRRecordDialog erd = new ESRRecordDialog(getViewSite().getShell(), (ESRRecord) po);
					erd.open();
					updateView();
				}
			}
		});

		tableViewer.setLabelProvider(new ESRLabelProvider());
		tableViewer.setContentProvider(new ESRContentProvider(lblSUMME, DISPLAY_ESR));

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
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

		ViewerFilter[] filters = new ViewerFilter[] { FilterSearchField.getInstance() };
		tableViewer.setFilters(filters);

		txtFilter.addKeyListener(new FilterKeyListener(txtFilter, tableViewer));
		txtFilter.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					FilterSearchField.getInstance().setSearchText(null);
					tableViewer.refresh();
				}
			}
		});

		updateView();
	}

	public void updateView() {
		updateButtonToggleState();

		if (SELECTION_TYPE.NOTPOSTED == selectionType) {
			btnDatePeriod.setText("Zeitraum: \u2264" + sdf.format(new Date()));
		} else {
			LocalDateTime dateHolder = LocalDateTime.now();
			switch (selectionType) {
			case LASTMONTH:
				dateHolder = dateHolder.minusMonths(1);
			case THISMONTH:
				startDate = new TimeTool(dateHolder.withDayOfMonth(1));
				endDate = new TimeTool(dateHolder.withDayOfMonth(dateHolder.getMonth().maxLength()));
				break;
			case LASTWEEK:
				dateHolder = dateHolder.minusWeeks(1);
			case THISWEEK:
				startDate = new TimeTool(dateHolder.with(DayOfWeek.MONDAY));
				endDate = new TimeTool(dateHolder.with(DayOfWeek.SUNDAY));
				break;
			default:
				break;
			}
			btnDatePeriod
					.setText("Zeitraum: " + sdf.format(startDate.getTime()) + " - " + sdf.format(endDate.getTime())); //$NON-NLS-2$
		}

		if (CoreHub.acl.request(DISPLAY_ESR) == true) {
			Job job = Job.create("ESR loading ...", (ICoreRunnable) monitor -> {
				Query<ESRRecord> qbe = new Query<ESRRecord>(ESRRecord.class, ESRRecord.TABLENAME, false,
						new String[] { ESRRecord.FLD_DATE, ESRRecord.FLD_BOOKING_DATE, ESRRecord.RECHNUNGS_ID,
								"Eingelesen", "Verarbeitet", "BetragInRp", "File" });
				qbe.add(ESRRecord.FLD_ID, Query.NOT_EQUAL, StringConstants.ONE);

				if (CoreHub.acl.request(AccessControlDefaults.ACCOUNTING_GLOBAL) == false) {
					Mandant mandator = ElexisEventDispatcher.getSelectedMandator();
					if (mandator != null) {
						qbe.startGroup();
						qbe.add(ESRRecord.MANDANT_ID, Query.EQUALS, mandator.getId());
						qbe.or();
						qbe.add(ESRRecord.MANDANT_ID, StringConstants.EMPTY, null);
						qbe.add(ESRRecord.FLD_REJECT_CODE, Query.NOT_EQUAL, StringConstants.ZERO);
						qbe.endGroup();
						qbe.and();
					} else {
						qbe.insertFalse();
					}
				}

				if (SELECTION_TYPE.NOTPOSTED == selectionType) {
					// we select by state
					qbe.startGroup();
					qbe.add(ESRRecord.FLD_BOOKING_DATE, Query.EQUALS, null);
					qbe.or();
					qbe.addToken(ESRRecord.FLD_BOOKING_DATE + " IS NULL"); //$NON-NLS-1$
					qbe.endGroup();
				} else {
					// we select by date
					qbe.add(ESRRecord.FLD_DATE, Query.GREATER_OR_EQUAL, startDate.toDBString(true));
					qbe.add(ESRRecord.FLD_DATE, Query.LESS_OR_EQUAL, endDate.toDBString(true));
				}
				List<ESRRecord> res = qbe.execute();

				Display.getDefault().asyncExec(() -> {
					tableViewer.setInput(res);
				});
			});

			// Start the Job
			job.schedule();
		} else {
			tableViewer.setInput(null);
		}
	}

	private void updateButtonToggleState() {
		btnNotPosted.setSelection(btnNotPosted.getData() == selectionType);
		btnDatePeriod.setSelection(btnDatePeriod.getData() == selectionType);
		btnLastMonth.setSelection(btnLastMonth.getData() == selectionType);
		btnThisMonth.setSelection(btnThisMonth.getData() == selectionType);
		btnLastWeek.setSelection(btnLastWeek.getData() == selectionType);
		btnThisWeek.setSelection(btnThisWeek.getData() == selectionType);
	}

	@Override
	public void setFocus() {
	}

}
