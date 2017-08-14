package at.medevit.elexis.emediplan.ui;

import java.util.ArrayList;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament.State;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

public class ImportEMediplanDialog extends TitleAreaDialog {
	private final Medication medication;
	
	private TableViewer tableViewer;
	private Table table;
	private boolean showInboxBtn = true;
	
	private ElexisEventListener eeli_presc = new ElexisUiEventListenerImpl(Prescription.class,
		ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_UPDATE | ElexisEvent.EVENT_DELETE) {
		public void runInUi(ElexisEvent ev){
			if (medication.Patient != null) {
				Prescription prescription = (Prescription) ev.getObject();
				if (prescription != null && prescription.getArtikel() instanceof ArtikelstammItem) {
					String patientId = prescription.get(Prescription.FLD_PATIENT_ID);
					if (patientId != null && patientId.equals(medication.Patient.patientId)) {
						refreshMedicamentsTable();
					}
				}
			}
		}
	};
	
	private void refreshMedicamentsTable(){
		if (medication != null) {
			for (Medicament medicament : medication.Medicaments) {
				EMediplanServiceHolder.getService().setPresciptionsToMedicament(medication,
					medicament);
			}
		}
		tableViewer.refresh();
	}
	
	public ImportEMediplanDialog(Shell parentShell, Medication medication, boolean showInboxBtn){
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS | SWT.RESIZE);
		this.medication = medication;
		this.showInboxBtn = showInboxBtn;
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
			SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 200;
		tableViewer.getControl().setLayoutData(gd);
		table = tableViewer.getTable();
		ColumnViewerToolTipSupport.enableFor(tableViewer);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumns(parent);
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new ActionSymMedication());
		menuManager.add(new ActionFixMedication());
		menuManager.add(new ActionReserveMedication());
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
		
		tableViewer.setInput(getInput());
		
		Button button = new Button(parent, SWT.PUSH);
		button.setVisible(showInboxBtn);
		button.setText("In Inbox ablegen");
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (EMediplanServiceHolder.getService().createInboxEntry(medication,
					ElexisEventDispatcher.getSelectedMandator())) {
					MessageDialog.openInformation(getShell(), "Medikationsplan",
						"Der Medikationsplan wurde erfolgreich in die Inbox hinzugefügt.");
					close();
				} else {
					MessageDialog.openError(getShell(), "Medikationsplan",
						"Der Medikationsplan konnte nicht in die Inbox hinzugefügt werden.\nÜberprüfen Sie das LOG File.");
				}
				
			}
		});
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
			public Color getBackground(final Object element){
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (medicament.artikelstammItem == null) {
						return UiDesk.getColorFromRGB("FFDDDD");
					} else if (State.GTIN_SAME_DOSAGE.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("D3D3D3");
					} else if (State.ATC.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("FFDDDD");
					} else if (State.ATC_SAME.equals(medicament.state)
						|| State.ATC_SAME_DOSAGE.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("FFDDDD");
					} else if (State.GTIN_SAME.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("FFFEC3");
					}
				}
				return UiDesk.getColorFromRGB("FFFFFF");
			}
			
			@Override
			public String getToolTipText(Object element){
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					return medicament.stateInfo;
				}
				return super.getToolTipText(element);
			}
			
			@Override
			public Color getForeground(Object element){
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (medicament.isMedicationExpired()) {
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
	
	private class ActionSymMedication extends Action {
		@Override
		public ImageDescriptor getImageDescriptor(){
			return Images.IMG_SYMPTOM_MEDI.getImageDescriptor();
		}
		
		@Override
		public String getText(){
			return "übernehmen als Symptomatische Medikation";
		}
		
		@Override
		public void run(){
			insertArticle((StructuredSelection) tableViewer.getSelection(),
				EntryType.SYMPTOMATIC_MEDICATION);
		}
	}
	
	private class ActionFixMedication extends Action {
		@Override
		public ImageDescriptor getImageDescriptor(){
			return Images.IMG_FIX_MEDI.getImageDescriptor();
		}
		
		@Override
		public String getText(){
			return "übernehmen als Fix Medikation";
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
			return Images.IMG_RESERVE_MEDI.getImageDescriptor();
		}
		
		@Override
		public String getText(){
			return "übernehmen als Reserve Medikation";
		}
		
		@Override
		public void run(){
			insertArticle((StructuredSelection) tableViewer.getSelection(),
				EntryType.RESERVE_MEDICATION);
		}
	}
	
	public void insertArticle(StructuredSelection selection, EntryType entryType){
		boolean bulkInsert = false;
		try {
			if (selection != null) {
				Object[] selections = selection.toArray();
				Patient patient = null;
				
				if (medication.Patient != null && medication.Patient.patientId != null) {
					patient = Patient.load(medication.Patient.patientId);
					if (patient != null && !patient.exists()) {
						patient = null;
					}
				}
				if (patient != null) {
					List<Prescription> prescriptions = new ArrayList<>();
			
					for (Object selectItem : selections) {
						if (selectItem instanceof Medicament) {
							Prescription prescription = insertMedicament(patient,
								(Medicament) selectItem, entryType, selections.length > 1);
							if (prescription != null) {
								prescriptions.add(prescription);
								
								// for bulk inserts we remove the event because of performance issues
								if (!bulkInsert && prescriptions.size() > 3) {
									ElexisEventDispatcher.getInstance().removeListeners(eeli_presc);
									bulkInsert = true;
								}
							}
						}
					}
					int sizeInserts = prescriptions.size();
					if (sizeInserts > 0) {
						StringBuffer buf = new StringBuffer();
						if (sizeInserts > 1) {
							buf.append("Folgende Medikamente wurden erfolgreich hinzugefügt: ");
						} else {
							buf.append("Folgendes Medikament wurde erfolgreich hinzugefügt: ");
						}
						
						buf.append("\n\n");
						for (Prescription prescription : prescriptions) {
							buf.append(prescription.getArtikel().getName());
							buf.append("\n");
						}
						MessageDialog.openInformation(getShell(), "Artikel", buf.toString());
					} else if (selections.length > 1) {
						MessageDialog.openInformation(getShell(), "Artikel",
							"Die ausgewählten Medikamente konnten nicht automatisch hinzugefügt werden.\n\nBitte versuchen Sie diese einzeln hinzuzufügen.");
					}
					
				} else {
					MessageDialog.openError(getShell(), "Error", "Kein Patient ausgewählt.");
				}
			}
		} finally {
			if (bulkInsert) {
				refreshMedicamentsTable();
				// register the event after bulk insert
				ElexisEventDispatcher.getInstance().addListeners(eeli_presc);
			}
		}
	}
	
	private Prescription insertMedicament(Patient patient, Medicament medicament,
		EntryType entryType,
		boolean multiSelection){
		if (patient != null && medicament != null && entryType != null) {
			EMediplanServiceHolder.getService().setPresciptionsToMedicament(medication, medicament);
			if (medicament.artikelstammItem != null) {
				if (State.GTIN_SAME_DOSAGE.equals(medicament.state)) {
					openDialogWarning("Das Medikament kann nicht zweimal verordnet werden.",
						medicament, multiSelection);
				} else if (medicament.isMedicationExpired()) {
					openDialogWarning("Das Medikament kann nicht hinzugefügt werden.", medicament,
						multiSelection);
				} else if (medicament.foundPrescription != null) {
					return insertMedicamentExistingPrescription(patient, medicament, entryType,
						multiSelection);
				} else {
					// create new prescription
					return createPrescription(entryType, medicament, patient, multiSelection);
				}
			} else {
				openDialogWarning("Das Medikament kann nicht hinzugefügt werden.", medicament,
					multiSelection);
			}
		}
		return null;
	}
	
	private Prescription insertMedicamentExistingPrescription(Patient patient,
		Medicament medicament,
		EntryType entryType, boolean multiSelection){
		
		if (multiSelection && State.GTIN_SAME.equals(medicament.state)) {
			// same medicament exist with same GTIN but different dosage - ignore this for bulk insert
			return null;
		} else if (multiSelection) {
			return createPrescription(entryType, medicament, patient, multiSelection);
		} else {
			QuestionComposite medicationQuestionComposite = new QuestionComposite();
			StringBuffer buf = new StringBuffer();
			buf.append("\n\n");
			if (State.GTIN_SAME.equals(medicament.state)) {
				buf.append(
					"Wollen Sie dieses Medikament hinzufügen und die bestehende Medikation historisieren ?");
				medicationQuestionComposite.setDefaulSelection(true);
				medicationQuestionComposite.createQuestionText("Vorhandenes ("
					+ medicament.foundPrescription.getArtikel().getName() + ") stoppen");
				
			} else {
				buf.append("Wollen Sie dieses Medikament hinzufügen ?");
				medicationQuestionComposite.setDefaulSelection(false);
				
				if (State.ATC_SAME.equals(medicament.state)
					|| State.ATC_SAME_DOSAGE.equals(medicament.state)) {
					medicationQuestionComposite.createQuestionText("Vorhandenes ("
						+ medicament.foundPrescription.getArtikel().getName() + ") stoppen");
				}
			}
			if (openCustomDialog(medicationQuestionComposite, buf.toString(),
				medicament) == CustomMessageDialog.OK) {
				if (medicationQuestionComposite.isQuestionConfirmed()) {
					medicament.foundPrescription.stop(new TimeTool());
					medicament.foundPrescription.setStopReason("EMediplan Import");
				}
				return createPrescription(entryType, medicament, patient, multiSelection);
			}
		}
		return null; // no insert ignore
	}
	
	public int openCustomDialog(QuestionComposite medicationQuestionComposite,
		String text, Medicament medicament){
		CustomMessageDialog<QuestionComposite> dlg = new CustomMessageDialog<>(getShell(),
			"Artikel", medicament.stateInfo + text, medicationQuestionComposite);
		return dlg.open();
		
	}
	
	/**
	 * No Warning dialogs appear for multiple selections
	 * 
	 * @param text
	 * @param medicament
	 * @param multiSelection
	 */
	public void openDialogWarning(String text, Medicament medicament, boolean multiSelection){
		if (medicament != null && !multiSelection) {
			MessageDialog.openWarning(getShell(), "Artikel", medicament.stateInfo + "\n\n" + text);
		}
	}
	
	private Prescription createPrescription(EntryType entryType, Medicament medicament,
		Patient patient,
		boolean multiSelection){
		Prescription prescription = new Prescription(medicament.artikelstammItem, patient,
			medicament.dosis, medicament.AppInstr);
		prescription.set(new String[] {
			Prescription.FLD_PRESC_TYPE, Prescription.FLD_DATE_FROM, Prescription.FLD_DATE_UNTIL
		}, String.valueOf(entryType.numericValue()), medicament.dateFrom, medicament.dateTo);
		prescription.setDisposalComment(medicament.TkgRsn);
		CoreHub.getLocalLockService().acquireLock(prescription);
		CoreHub.getLocalLockService().releaseLock(prescription);
		return prescription;
	}
}
