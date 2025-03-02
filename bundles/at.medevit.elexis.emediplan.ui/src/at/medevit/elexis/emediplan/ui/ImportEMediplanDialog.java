package at.medevit.elexis.emediplan.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.elexis.emediplan.StartupHandler;
import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament.State;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.builder.IPrescriptionBuilder;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.utils.CoreUtil;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

public class ImportEMediplanDialog extends TitleAreaDialog {
	private final Medication medication;

	private TableViewer tableViewer;
	private Table table;
	private boolean showInboxBtn = true;

	private boolean bulkInsert = false;

	private EMediplanService mediplanService;

	@Optional
	@Inject
	void updatePrescription(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IPrescription prescription) {
		refreshPrescription(prescription);
	}

	@Inject
	void createPrescription(@Optional @UIEventTopic(ElexisEventTopics.EVENT_CREATE) IPrescription prescription) {
		refreshPrescription(prescription);
	}

	@Inject
	void deletePrescription(@Optional @UIEventTopic(ElexisEventTopics.EVENT_DELETE) IPrescription prescription) {
		refreshPrescription(prescription);
	}

	private void refreshPrescription(IPrescription prescription) {
		if (medication != null && medication.Patient != null && !bulkInsert) {
			if (prescription != null && prescription.getArticle() instanceof IArtikelstammItem) {
				IPatient patient = prescription.getPatient();
				if (patient != null && patient.getId().equals(medication.Patient.patientId)) {
					refreshMedicamentsTable();
				}
			}
		}
	}

	private void refreshMedicamentsTable() {
		if (medication != null) {
			for (Medicament medicament : medication.Medicaments) {
				mediplanService.setPresciptionsToMedicament(medication, medicament);
			}
		}
		tableViewer.refresh();
	}

	public ImportEMediplanDialog(Shell parentShell, Medication medication, boolean showInboxBtn) {
		super(parentShell);
		CoreUiUtil.injectServicesWithContext(this);
		setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS | SWT.RESIZE);
		this.medication = medication;
		this.showInboxBtn = showInboxBtn;
		this.mediplanService = EMediplanServiceHolder.getService();
	}

	@Override
	public void create() {
		super.create();
		setTitle("eMediplan Import");

		if (medication.Patient != null) {
			setMessage("Patient: " + medication.Patient.patientLabel
					+ "\nMedikamente (Fix, Reserve, Symptomatisch) für den Import auswählen.");
		}

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		List<Medicament> input = getInput();

		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		area.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(area, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 200;
		tableViewer.getControl().setLayoutData(gd);
		table = tableViewer.getTable();
		ColumnViewerToolTipSupport.enableFor(tableViewer);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createColumns(parent);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(getInput());

		Composite c1 = new Composite(parent, SWT.RIGHT_TO_LEFT);
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c1.setLayout(new GridLayout(2, false));

		Button btnImport = new Button(c1, SWT.PUSH);
		btnImport.setText("Importieren");
		btnImport.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				insertArticle(new StructuredSelection(
						getInput().stream().filter(m -> m.entryType != null).collect(Collectors.toList())));
			}
		});

		Button button = new Button(c1, SWT.PUSH);
		button.setVisible(showInboxBtn);
		button.setText("In Inbox ablegen");
		button.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int stateMask = e.stateMask;
				if ((stateMask & SWT.SHIFT) == SWT.SHIFT && medication.chunk != null) {
					File userDir = CoreUtil.getWritableUserDir();
					File jsonOutput = new File(userDir, "emediplan.json"); //$NON-NLS-1$
					try (FileWriter writer = new FileWriter(jsonOutput)) {
						writer.write(StartupHandler.getDecodedJsonString(medication.chunk));
					} catch (IOException e1) {
						LoggerFactory.getLogger(getClass()).error("Could not write emediplan json" + e);
					}
				}
				if (mediplanService.createInboxEntry(medication,
						ContextServiceHolder.get().getActiveMandator().orElse(null))) {
					MessageDialog.openInformation(getShell(), "Medikationsplan",
							"Der Medikationsplan wurde erfolgreich in die Inbox hinzugefügt.");
					close();
				} else {
					MessageDialog.openError(getShell(), "Medikationsplan",
							"Der Medikationsplan konnte nicht in die Inbox hinzugefügt werden.\nÜberprüfen Sie das LOG File.");
				}

			}
		});
		return area;
	}

	@Override
	public boolean close() {
		return super.close();
	}

	private List<Medicament> getInput() {
		return medication.Medicaments;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		/* button bar removed */
	}

	private void createColumns(Composite parent) {
		String[] titles = { "Fix", "Reserve", "Symptomatisch", "Medikament", "Dosis", "Von Bis",
				"Anwendungsinstruktion", "Anwendungsgrund" };
		int[] bounds = { 40, 40, 40, 220, 120, 150, 150, 150 };

		// short message
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new EmulatedCheckBoxLabelProvider() {
			@Override
			protected boolean isChecked(Object element) {
				Medicament mdm = (Medicament) element;
				return EntryType.FIXED_MEDICATION.equals(mdm.entryType);
			}
		});
		col.setEditingSupport(new CheckBoxColumnEditingSupport(tableViewer, EntryType.FIXED_MEDICATION));
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new EmulatedCheckBoxLabelProvider() {
			@Override
			protected boolean isChecked(Object element) {
				Medicament mdm = (Medicament) element;
				return EntryType.RESERVE_MEDICATION.equals(mdm.entryType);
			}
		});
		col.setEditingSupport(new CheckBoxColumnEditingSupport(tableViewer, EntryType.RESERVE_MEDICATION));
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new EmulatedCheckBoxLabelProvider() {
			@Override
			protected boolean isChecked(Object element) {
				Medicament mdm = (Medicament) element;
				return EntryType.SYMPTOMATIC_MEDICATION.equals(mdm.entryType);
			}
		});
		col.setEditingSupport(new CheckBoxColumnEditingSupport(tableViewer, EntryType.SYMPTOMATIC_MEDICATION));

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Medicament mdm = (Medicament) element;
				if (mdm.artikelstammItem != null) {
					return mdm.artikelstammItem.getName();
				} else if (StringUtils.isNotBlank(mediplanService.getPFieldValue(mdm, "Dsc"))) { //$NON-NLS-1$
					return mediplanService.getPFieldValue(mdm, "Dsc"); //$NON-NLS-1$
				}
				return mdm.Id;
			}

			@Override
			public Color getBackground(final Object element) {
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (medicament.artikelstammItem == null) {
						return UiDesk.getColorFromRGB("FFDDDD"); //$NON-NLS-1$
					} else if (State.GTIN_SAME_DOSAGE.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("D3D3D3"); //$NON-NLS-1$
					} else if (State.ATC.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("FFDDDD"); //$NON-NLS-1$
					} else if (State.ATC_SAME.equals(medicament.state)
							|| State.ATC_SAME_DOSAGE.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("FFDDDD"); //$NON-NLS-1$
					} else if (State.GTIN_SAME.equals(medicament.state)) {
						return UiDesk.getColorFromRGB("FFFEC3"); //$NON-NLS-1$
					}
				}
				return UiDesk.getColorFromRGB("FFFFFF"); //$NON-NLS-1$
			}

			@Override
			public String getToolTipText(Object element) {
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					return medicament.stateInfo;
				}
				return super.getToolTipText(element);
			}

			@Override
			public Color getForeground(Object element) {
				if (element instanceof Medicament) {
					Medicament medicament = (Medicament) element;
					if (medicament.isMedicationExpired()) {
						return UiDesk.getColor(UiDesk.COL_RED);
					}
				}
				return super.getForeground(element);
			}
		});

		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Medicament mdm = (Medicament) element;
				return mdm.dosis;
			}
		});

		col = createTableViewerColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Medicament mdm = (Medicament) element;
				StringBuffer buf = new StringBuffer();
				if (mdm.dateFrom != null) {
					buf.append(mdm.dateFrom);
				}
				if (mdm.dateTo != null) {
					buf.append("-"); //$NON-NLS-1$
					buf.append(mdm.dateTo);
				}
				return buf.toString();
			}
		});

		col = createTableViewerColumn(titles[6], bounds[6], 6);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Medicament mdm = (Medicament) element;
				return mdm.AppInstr;
			}
		});

		col = createTableViewerColumn(titles[7], bounds[7], 7);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Medicament mdm = (Medicament) element;
				return mdm.TkgRsn;
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget instanceof TableColumn) {
					// select all entries with the selected entrytype
					EntryType entryType = null;
					if ("Fix".equals(e.widget.getData())) {
						entryType = EntryType.FIXED_MEDICATION;
					} else if ("Reserve".equals(e.widget.getData())) {
						entryType = EntryType.RESERVE_MEDICATION;
					} else if ("Symptomatisch".equals(e.widget.getData())) {
						entryType = EntryType.SYMPTOMATIC_MEDICATION;
					}
					if (entryType != null) {
						for (Medicament medicament : getInput()) {
							medicament.entryType = entryType;
						}
					}
				}
				super.widgetSelected(e);
				tableViewer.refresh();
			}
		});
		column.setData(title);
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		return viewerColumn;
	}

	public void insertArticle(StructuredSelection selection) {
		try {
			if (selection != null) {
				Object[] selections = selection.toArray();
				IPatient patient = null;

				if (medication.Patient != null && medication.Patient.patientId != null) {
					patient = CoreModelServiceHolder.get().load(medication.Patient.patientId, IPatient.class)
							.orElse(null);
				}
				if (patient != null) {
					List<IPrescription> prescriptions = new ArrayList<>();

					for (Object selectItem : selections) {
						if (selectItem instanceof Medicament) {
							if (((Medicament) selectItem).entryType != null) {
								IPrescription prescription = insertMedicament(patient, (Medicament) selectItem,
										selections.length > 1);
								if (prescription != null) {
									prescriptions.add(prescription);

									// for bulk inserts we remove the event because of performance issues
									if (!bulkInsert && prescriptions.size() > 3) {
										bulkInsert = true;
									}
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
						for (IPrescription prescription : prescriptions) {
							buf.append(prescription.getArticle().getName());
							buf.append(StringUtils.LF);
						}
						MessageDialog.openInformation(getShell(), "Artikel", buf.toString());
					} else if (selections.length > 1) {
						MessageDialog.openInformation(getShell(), "Artikel",
								"Die ausgewählten Medikamente konnten nicht automatisch hinzugefügt werden.\n\nBitte versuchen Sie diese einzeln hinzuzufügen.");
					} else if (selections.length == 0) {
						MessageDialog.openInformation(getShell(), "Artikel",
								"Import kann nicht durchgeführt werden.\n\nBitte selektieren Sie zuerst die Medikamente, die importiert werden sollen.");
					} else if (selections.length == 1) {
						if (selections[0] instanceof Medicament && (((Medicament) selections[0]).entryType == null)) {
							MessageDialog.openInformation(getShell(), "Artikel",
									"Import kann nicht durchgeführt werden.\n\nBitte wählen Sie die Medikation aus Fix, Reserve oder Symptomatisch.");
						}
					}

				} else {
					MessageDialog.openError(getShell(), "Error", "Kein Patient ausgewählt.");
				}
			}
		} finally {
			if (bulkInsert) {
				refreshMedicamentsTable();
				bulkInsert = false;
			}
		}
	}

	private IPrescription insertMedicament(IPatient patient, Medicament medicament, boolean multiSelection) {
		if (patient != null && medicament != null && medicament.entryType != null) {
			mediplanService.setPresciptionsToMedicament(medication, medicament);
			if (medicament.artikelstammItem != null) {
				if (State.GTIN_SAME_DOSAGE.equals(medicament.state)) {
					openDialogWarning("Das Medikament kann nicht zweimal verordnet werden.", medicament,
							multiSelection);
				} else if (medicament.isMedicationExpired()) {
					openDialogWarning("Das Medikament kann nicht hinzugefügt werden.", medicament, multiSelection);
				} else if (medicament.foundPrescription != null) {
					return insertMedicamentExistingPrescription(patient, medicament, multiSelection);
				} else {
					// create new prescription
					return createPrescription(medicament, patient, multiSelection);
				}
			} else {
				openDialogWarning("Das Medikament kann nicht hinzugefügt werden.", medicament, multiSelection);
			}
		}
		return null;
	}

	private IPrescription insertMedicamentExistingPrescription(IPatient patient, Medicament medicament,
			boolean multiSelection) {

		if (multiSelection && State.GTIN_SAME.equals(medicament.state)) {
			// same medicament exist with same GTIN but different dosage - ignore this for
			// bulk insert
			return null;
		} else if (multiSelection) {
			return createPrescription(medicament, patient, multiSelection);
		} else {
			QuestionComposite medicationQuestionComposite = new QuestionComposite();
			StringBuffer buf = new StringBuffer();
			buf.append("\n\n");
			if (State.GTIN_SAME.equals(medicament.state)) {
				buf.append("Wollen Sie dieses Medikament hinzufügen und die bestehende Medikation historisieren ?");
				medicationQuestionComposite.setDefaulSelection(true);
				medicationQuestionComposite.createQuestionText(
						"Vorhandenes (" + medicament.foundPrescription.getArtikel().getName() + ") stoppen");

			} else {
				buf.append("Wollen Sie dieses Medikament hinzufügen ?");
				medicationQuestionComposite.setDefaulSelection(false);

				if (State.ATC_SAME.equals(medicament.state) || State.ATC_SAME_DOSAGE.equals(medicament.state)) {
					medicationQuestionComposite.createQuestionText(
							"Vorhandenes (" + medicament.foundPrescription.getArtikel().getName() + ") stoppen");
				}
			}
			if (openCustomDialog(medicationQuestionComposite, buf.toString(), medicament) == CustomMessageDialog.OK) {
				if (medicationQuestionComposite.isQuestionConfirmed()) {
					medicament.foundPrescription.stop(new TimeTool());
					medicament.foundPrescription.setStopReason("EMediplan Import");
				}
				return createPrescription(medicament, patient, multiSelection);
			}
		}
		return null; // no insert ignore
	}

	public int openCustomDialog(QuestionComposite medicationQuestionComposite, String text, Medicament medicament) {
		CustomMessageDialog<QuestionComposite> dlg = new CustomMessageDialog<>(getShell(), "Artikel",
				medicament.stateInfo + text, medicationQuestionComposite);
		return dlg.open();

	}

	/**
	 * No Warning dialogs appear for multiple selections
	 *
	 * @param text
	 * @param medicament
	 * @param multiSelection
	 */
	public void openDialogWarning(String text, Medicament medicament, boolean multiSelection) {
		if (medicament != null && !multiSelection) {
			MessageDialog.openWarning(getShell(), "Artikel", medicament.stateInfo + "\n\n" + text);
		}
	}

	private IPrescription createPrescription(Medicament medicament, IPatient patient, boolean multiSelection) {
		IPrescription prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(), ContextServiceHolder.get(),
				medicament.artikelstammItem, patient, medicament.dosis).build();

		getLocalDateTime(medicament.dateFrom).ifPresent(ldt -> prescription.setDateFrom(ldt));
		getLocalDateTime(medicament.dateTo).ifPresent(ldt -> prescription.setDateTo(ldt));

		prescription.setRemark(medicament.AppInstr);
		prescription.setEntryType(medicament.entryType);
		prescription.setDisposalComment(medicament.TkgRsn);

		CoreModelServiceHolder.get().save(prescription);
		return prescription;
	}

	private java.util.Optional<LocalDateTime> getLocalDateTime(String dateString) {
		if (dateString != null && !dateString.isEmpty()) {
			return java.util.Optional.of(new TimeTool(dateString).toLocalDateTime());
		}
		return java.util.Optional.empty();
	}

	class CheckBoxColumnEditingSupport extends EditingSupport {

		private final TableViewer tableViewer;
		private final EntryType forType;

		public CheckBoxColumnEditingSupport(TableViewer viewer, EntryType forType) {
			super(viewer);
			this.tableViewer = viewer;
			this.forType = forType;
		}

		@Override
		protected CellEditor getCellEditor(Object o) {
			return new CheckboxCellEditor(null, SWT.CHECK);
		}

		@Override
		protected boolean canEdit(Object o) {
			return true;
		}

		@Override
		protected Object getValue(Object o) {
			Medicament medication = (Medicament) o;
			return forType.equals(medication.entryType);
		}

		@Override
		protected void setValue(Object element, Object value) {
			Medicament medication = (Medicament) element;
			if (Boolean.TRUE.equals(value)) {
				medication.entryType = forType;
			} else {
				medication.entryType = null;
			}
			tableViewer.refresh();
		}
	}

	abstract static class EmulatedCheckBoxLabelProvider extends ColumnLabelProvider {

		private static Image CHECKED = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.emediplan.ui", //$NON-NLS-1$
				"rsc/checked_checkbox.png").createImage(); //$NON-NLS-1$

		private static Image UNCHECKED = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.emediplan.ui", //$NON-NLS-1$
				"rsc/unchecked_checkbox.png").createImage(); //$NON-NLS-1$

		@Override
		public String getText(Object element) {
			return null;
		}

		@Override
		public Image getImage(Object element) {
			return isChecked(element) ? CHECKED : UNCHECKED;
		}

		protected abstract boolean isChecked(Object element);
	}
}
