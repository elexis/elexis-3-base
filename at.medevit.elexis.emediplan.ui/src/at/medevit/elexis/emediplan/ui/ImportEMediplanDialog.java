package at.medevit.elexis.emediplan.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

public class ImportEMediplanDialog extends TitleAreaDialog {
	private final Medication medication;
	
	private TableViewer tableViewer;
	private Table table;
	
	private ElexisEventListener eeli_presc = new ElexisUiEventListenerImpl(Prescription.class,
		ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_UPDATE | ElexisEvent.EVENT_DELETE) {
		public void runInUi(ElexisEvent ev){
			if (medication.Patient != null) {
				Prescription prescription = (Prescription) ev.getObject();
				if (prescription != null && prescription.getArtikel() instanceof ArtikelstammItem) {
					String patientId = prescription.get(Prescription.FLD_PATIENT_ID);
					if (patientId.equals(medication.Patient.patientId))
						for (Medicament medicament : medication.Medicaments) {
							if (prescription.getArtikel().equals(medicament.artikelstammItem)) {
								if ((ev.getType() == ElexisEvent.EVENT_CREATE
									|| ev.getType() == ElexisEvent.EVENT_UPDATE)
									&& prescription.getDosis().equals(medicament.dosis)) {
									medicament.exists = true;
									medicament.artikelstammItem =
										(ArtikelstammItem) prescription.getArtikel();
								} else if (ev.getType() == ElexisEvent.EVENT_DELETE
									|| ev.getType() == ElexisEvent.EVENT_UPDATE) {
									if (EMediplanServiceHolder.getService()
										.findPresciptionsByMedicament(medication, medicament)
										.isEmpty()) {
										medicament.exists = false;
									}
									
								}
							}
						}
					tableViewer.refresh();
				}
				
			}
		}
	};
		
	public ImportEMediplanDialog(Shell parentShell, Medication medication){
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS | SWT.RESIZE);
		this.medication = medication;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle("eMediplan Import");
		if (medication.Patient != null) {
			setMessage("Patient: " + medication.Patient.patientLabel);
		}
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		List<Medicament> input = getInput();
		
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		area.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(area,
			SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		ColumnViewerToolTipSupport.enableFor(tableViewer);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumns(parent);
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new ActionFixMedication());
		menuManager.add(new ActionReserveMedication());
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
		
		tableViewer.setInput(getInput());
		ElexisEventDispatcher.getInstance().addListeners(eeli_presc);
		
		return area;
	}
	
	@Override
	public boolean close(){
		ElexisEventDispatcher.getInstance().removeListeners(eeli_presc);
		return super.close();
	}

	private List<Medicament> getInput(){
		return medication.Medicaments;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent){
		/* button bar removed */
	}
	
	private void createColumns(Composite parent){
		String[] titles = {
			"Medikatment", "Dosis", "Von Bis", "Anwendungsinstruktion", "Anwendungsgrund"
		};
		int[] bounds = {
			220, 120, 150, 150, 150
		};
		
		//short message
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element){
				Medicament mdm = (Medicament) element;
				if (mdm.artikelstammItem != null) {
					return mdm.artikelstammItem.getName();
				}
				return mdm.Id;
			}
			
			@Override
			public Color getBackground(final Object element) {
			    if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (medicament.artikelstammItem == null) {
			            return new Color(Display.getDefault(), 0xFF, 0xDD, 0xDD);
			        }
					else if (medicament.exists) {
						return new Color(Display.getDefault(), 0xFF, 0xFE, 0xC3);
			        }
			    }
				return new Color(Display.getDefault(), 0xFF, 0xFF, 0xFF);
			}
			
			@Override
			public String getToolTipText(Object element){
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (medicament.artikelstammItem == null) {
						return "Dieser Artikel wurde nicht gefunden.";
					} else  {
						StringBuffer buf = new StringBuffer();
						if (medicament.exists)
						{
							buf.append("Diese Medikation existiert bereits in Elexis.");
						}
						if (isMedicationExpired(medicament))
						{
							if (buf.length() > 0) {
								buf.append("\n");
							}
							buf.append("Diese Medikation ist bereits abgelaufen.");
						}
						if (buf.length() > 0) {
							return buf.toString();
						}
					}
				}
				return super.getToolTipText(element);
			}
			
			@Override
			public Color getForeground(Object element){
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (isMedicationExpired(medicament)) {
						return UiDesk.getColor(UiDesk.COL_RED);
					}
				}
				return super.getForeground(element);
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element){
				Medicament mdm = (Medicament) element;
				return mdm.dosis;
			}
		});
		

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element){
				Medicament mdm = (Medicament) element;
				StringBuffer buf = new StringBuffer();
				if (mdm.dateFrom != null) {
					buf.append(mdm.dateFrom);
				}
				if (mdm.dateTo != null) {
					buf.append("-");
					buf.append(mdm.dateTo);
				}
				return buf.toString();
			}
		});
		

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element){
				Medicament mdm = (Medicament) element;
				return mdm.AppInstr;
			}
		});
		
		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element){
				Medicament mdm = (Medicament) element;
				return mdm.TkgRsn;
			}
		});
		
	}
	
	private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber){
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		return viewerColumn;
	}
	

	private class ActionFixMedication extends Action {
		@Override
		public ImageDescriptor getImageDescriptor(){
			return null;
		}
		
		@Override
		public String getText(){
			return "übernehmen als Fixmedikation";
		}
		
		@Override
		public void run(){
			insertArticle((StructuredSelection) tableViewer.getSelection(),
				EntryType.FIXED_MEDICATION);
		}
	}
	
	private class ActionReserveMedication extends Action {
		@Override
		public ImageDescriptor getImageDescriptor(){
			return null;
		}
		
		@Override
		public String getText(){
			return "übernehmen als Reservemedikation";
		}
		
		@Override
		public void run(){
			insertArticle((StructuredSelection) tableViewer.getSelection(),
				EntryType.RESERVE_MEDICATION);
		}
	}
	
	public void insertArticle(StructuredSelection selection, EntryType entryType){
		if (selection != null && selection.getFirstElement() instanceof Medicament) {
			Medicament medicament = (Medicament) selection.getFirstElement();
			if (medicament.artikelstammItem != null) {
				if (medication.Patient != null && medication.Patient.patientId != null) {
					Patient patient = Patient.load(medication.Patient.patientId);
					if (patient != null && patient.exists()) {
						// find if already exists
						List<Prescription> results = EMediplanServiceHolder.getService()
							.findPresciptionsByMedicament(medication, medicament);
						if (!results.isEmpty() || medicament.exists) {
							if (!MessageDialog.openConfirm(getShell(), "Artikel",
								"Diese Medikation wurde bereits hinzugefügt.\nWollen Sie es erneut hinzufügen ?")) {
								return;
							}
						}
						Prescription prescription = new Prescription(medicament.artikelstammItem,
							patient, medicament.dosis, medicament.AppInstr);
						prescription.set(new String[] {
							Prescription.FLD_PRESC_TYPE, Prescription.FLD_DATE_FROM,
							Prescription.FLD_DATE_UNTIL
						}, String.valueOf(entryType.numericValue()), medicament.dateFrom,
							medicament.dateTo);
						prescription.setDisposalComment(medicament.TkgRsn);
						CoreHub.getLocalLockService().acquireLock(prescription);
						CoreHub.getLocalLockService().releaseLock(prescription);
						return;
					}
				}
				MessageDialog.openError(getShell(), "Error", "Kein Patient ausgewählt.");
			}
		}
	}
	
	private boolean isMedicationExpired(Medicament medicament){
		if (medicament.dateTo != null) {
			TimeTool now = new TimeTool();
			now.add(TimeTool.SECOND, 5);
			if (new TimeTool(medicament.dateTo).isBefore(now)) {
				return true;
			}
		}
		return false;
	}
	
}
