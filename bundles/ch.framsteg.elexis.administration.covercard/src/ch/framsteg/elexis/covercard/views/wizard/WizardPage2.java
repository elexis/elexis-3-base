package ch.framsteg.elexis.covercard.views.wizard;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;

public class WizardPage2 extends WizardPage {

	private Composite container;
	private Table table;
	private Button btn_showCandidats;
	private Button btn_showAll;
	private Button btn_newPatient;
	private PatientInfoData patientInfoData;
	private List<Patient> allPatients;
	private String patientID;
	private boolean identifiedByAHV;

	private Properties applicationProperties;
	private Properties messagesProperties;

	private final static String BTN_SHOW_CANDIDATS = "wizard.page2.btn.show.candidats";
	private final static String BTN_SHOW_ALL = "wizard.page2.btn.show.all";
	private final static String BTN_NEW_PATIENT = "wizard.page2.btn.new.patient";
	private final static String PRENAME = "wizard.page1.prename";
	private final static String NAME = "wizard.page1.name";
	private final static String BIRTHDAY = "wizard.page1.birthday";
	private final static String SEX = "wizard.page1.sex";
	private final static String ADDRESS = "wizard.page1.address";
	private final static String ZIP = "wizard.page1.zip";
	private final static String LOCATION = "wizard.page1.location";
	private final static String ELEXIS_ID = "wizard.page2.elexis.id";
	private final static String AHV = "wizard.page2.ahv";

	private final static String XID_AHV = "domain.covercard.ahv";
	private final static String XID_INSURED_NR = "domain.covercard.insured.number";
	private final static String XID_CARD_NR = "domain.covercard.card.number";
	private final static String XID_INSURED_PERSON_NR = "domain.covercard.insured.person.number";

	private final static String MSG_TITLE = "wizard.page2.msg.title";
	private final static String MSG_TITLE_INFO = "wizard.page2.msg.info";
	private final static String MSG_NEW = "wizard.page2.msg.new";
	private final static String MSG_MOD = "wizard.page2.msg.mod";
	private final static String MSG_NO_PATIENT_FOUND = "wizard.page2.no.patient.found";
	private final static String MSG_SINGLE_PATIENT_FOUND_BY_AHV = "wizard.page2.msg.single.patient.found.by.ahv";
	private final static String MSG_SINGLE_PATIENT_FOUND_BY_BIRTHDAY_PRENAME = "wizard.page2.msg.single.patient.found.by.birthdayPrename";
	private final static String MSG_MULTIBLE_PATIENTS_FOUND_BY_BIRTHDAY_PRENAME = "wizard.page2.msg.multible.patients.found.by.birthdayPrename";

	private final static String ZERO_CANDIDATE_MSG = "wizard.page2.msg.zero.candidate";
	private final static String ONE_CANDIDATE_MSG = "wizard.page2.msg.one.candidate";
	private final static String MULTIBLE_CANDIDATES_MSG = "wizard.page2.msg.multible.candidate";

	protected WizardPage2(String pageName, PatientInfoData patientInfoData, Properties applicationProperties,
			Properties messagesProperties) {
		super(pageName);
		setTitle(pageName);
		this.patientInfoData = patientInfoData;
		loadPatients();
		setPageComplete(false);
		this.applicationProperties = applicationProperties;
		this.messagesProperties = messagesProperties;
	}

	private void loadPatients() {
		Query<Patient> query = new Query<>(Patient.class);
		this.allPatients = query.execute();
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

		table = new Table(container, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		btn_showCandidats = new Button(container, SWT.CHECK);
		btn_showCandidats.setText(messagesProperties.getProperty(BTN_SHOW_CANDIDATS));
		btn_showCandidats.setSelection(true);

		btn_showAll = new Button(container, SWT.CHECK);
		btn_showAll.setText(messagesProperties.getProperty(BTN_SHOW_ALL));
		btn_showAll.setSelection(false);

		btn_newPatient = new Button(container, SWT.CHECK);
		btn_newPatient.setText(messagesProperties.getProperty(BTN_NEW_PATIENT));
		btn_newPatient.setSelection(false);

		TableColumn tableColumn1 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn3 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn4 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn5 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn6 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn7 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn8 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn9 = new TableColumn(table, SWT.LEFT);

		Listener sortListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				TableItem[] items = table.getItems();
				Collator collator = Collator.getInstance(Locale.getDefault());
				TableColumn column = (TableColumn) e.widget;
				int index = column == tableColumn1 ? 0 : 1;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
				table.setSortColumn(column);
			}
		};

		tableColumn1.setText(messagesProperties.getProperty(NAME));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn2.setText(messagesProperties.getProperty(PRENAME));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn3.setText(messagesProperties.getProperty(BIRTHDAY));
		tableColumn3.setAlignment(SWT.LEFT);
		tableColumn4.setText(messagesProperties.getProperty(SEX));
		tableColumn4.setAlignment(SWT.LEFT);
		tableColumn5.setText(messagesProperties.getProperty(ADDRESS));
		tableColumn5.setAlignment(SWT.LEFT);
		tableColumn6.setText(messagesProperties.getProperty(ZIP));
		tableColumn6.setAlignment(SWT.LEFT);
		tableColumn7.setText(messagesProperties.getProperty(LOCATION));
		tableColumn7.setAlignment(SWT.LEFT);
		tableColumn8.setText(messagesProperties.getProperty(ELEXIS_ID));
		tableColumn8.setAlignment(SWT.LEFT);
		tableColumn9.setText(messagesProperties.getProperty(AHV));
		tableColumn9.setAlignment(SWT.LEFT);

		table.setHeaderVisible(true);
		loadTable(table, inspectCandidates());

		if (table.getItemCount() == 0) {
			MessageDialog.openInformation(container.getShell(), messagesProperties.getProperty(MSG_TITLE_INFO),
					messagesProperties.getProperty(MSG_NO_PATIENT_FOUND));
			table.setVisible(false);
			btn_showCandidats.setVisible(false);
			btn_showAll.setVisible(false);
			btn_newPatient.setVisible(true);
			btn_newPatient.setSelection(true);
			setDescription(messagesProperties.getProperty(ZERO_CANDIDATE_MSG));
			setPageComplete(true);
		}

		if (table.getItemCount() == 1) {
			btn_showCandidats.setSelection(false);
			btn_showAll.setSelection(false);
			if (identifiedByAHV) {
				MessageDialog.openInformation(container.getShell(), messagesProperties.getProperty(MSG_TITLE_INFO),
						messagesProperties.getProperty(MSG_SINGLE_PATIENT_FOUND_BY_AHV));
				btn_newPatient.setEnabled(false);
				btn_showAll.setEnabled(false);
				btn_showCandidats.setEnabled(false);
			} else {
				MessageDialog.openWarning(container.getShell(), messagesProperties.getProperty(MSG_TITLE_INFO),
						messagesProperties.getProperty(MSG_SINGLE_PATIENT_FOUND_BY_BIRTHDAY_PRENAME));
				btn_newPatient.setEnabled(true);
				btn_showAll.setEnabled(true);
				btn_showCandidats.setEnabled(true);
			}
			btn_newPatient.setSelection(false);
			setDescription(messagesProperties.getProperty(ONE_CANDIDATE_MSG));
		}

		if (table.getItemCount() > 1) {
			MessageDialog.openWarning(container.getShell(), messagesProperties.getProperty(MSG_TITLE_INFO),
					messagesProperties.getProperty(MSG_MULTIBLE_PATIENTS_FOUND_BY_BIRTHDAY_PRENAME));
			setDescription(messagesProperties.getProperty(MULTIBLE_CANDIDATES_MSG));
		}

		for (TableColumn tc : table.getColumns()) {
			tc.addListener(SWT.Selection, sortListener);
			tc.pack();
		}
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int[] selections = table.getSelectionIndices();
				TableItem item1 = table.getItem(selections[0]);
				patientID = item1.getText(7);
				setPageComplete(true);
			}
		});

		sortTable(table, 0);
		table.pack();
		container.pack();
		container.getShell().pack();

		btn_showAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btn_showAll.getSelection()) {
					table.removeAll();
					loadTable(table, allPatients);
					sortTable(table, 0);
					btn_showCandidats.setSelection(false);
				} else {
					table.removeAll();
					loadTable(table, inspectCandidates());
					sortTable(table, 0);
				}
				table.pack();
				container.pack();
				container.getShell().pack();
			}
		});

		btn_showCandidats.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btn_showCandidats.getSelection()) {
					table.removeAll();
					loadTable(table, inspectCandidates());
					sortTable(table, 0);
					btn_showAll.setSelection(false);
				} else {
					table.removeAll();
					loadTable(table, allPatients);
					sortTable(table, 0);
				}
			}
		});

		btn_newPatient.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btn_newPatient.getSelection()) {
					table.setEnabled(false);
					btn_showAll.setEnabled(false);
					btn_showAll.setSelection(false);
					btn_showCandidats.setEnabled(false);
					btn_showCandidats.setSelection(false);
					setPageComplete(true);
				} else {
					table.setEnabled(true);
					btn_showAll.setEnabled(true);
					btn_showAll.setSelection(false);
					btn_showCandidats.setEnabled(true);
					btn_showCandidats.setSelection(true);
					table.removeAll();
					loadTable(table, inspectCandidates());
					sortTable(table, 0);
					setPageComplete(false);
				}
			}
		});
		canFlipToNextPage();
		setControl(container);
	}

	private void loadTable(Table table, List<Patient> patients) {
		for (Patient patient : patients) {
			TableItem item = new TableItem(table, SWT.HOME);

			item.setText(new String[] { patient.getName(), patient.getVorname(), patient.getGeburtsdatum(),
					patient.getGeschlecht(), patient.getAnschrift().getStrasse(), patient.getAnschrift().getPlz(),
					patient.getAnschrift().getOrt(), patient.getId(),
					patient.getXid(applicationProperties.getProperty(XID_AHV)) });
		}
	}

	private void sortTable(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		TableColumn column = table.getColumn(columnNumber);

		int index = column == table.getColumn(columnNumber) ? 0 : 1;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				if (collator.compare(value1, value2) < 0) {
					String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
							items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
							items[i].getText(7), items[i].getText(8) };
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
	}

	private ArrayList<Patient> inspectCandidates() {
		ArrayList<Patient> candidates = new ArrayList<Patient>();
		identifiedByAHV = false;
		for (Patient patient : allPatients) {
			if (patient.getXid(applicationProperties.getProperty(XID_AHV))
					.equalsIgnoreCase(patientInfoData.getCardholderIdentifier())) {
				candidates.clear();
				candidates.add(patient);
				// Prohibit the creation if patient already exists
				// btn_newPatient.setEnabled(false);
				identifiedByAHV = true;
				break;

			} else if (patient.get(Patient.FLD_DOB).equalsIgnoreCase(patientInfoData.getBirthday())
					&& patient.get(Patient.FLD_FIRSTNAME).equalsIgnoreCase(patientInfoData.getPrename())) {
				candidates.add(patient);
			}
		}
		return candidates;
	}

	public void finish() {

		if (btn_newPatient.getSelection()) {
			Patient patient = new Patient(patientInfoData.getName(), patientInfoData.getPrename(),
					patientInfoData.getBirthday(), patientInfoData.getSex());
			patient.set(Patient.FLD_SEX, patientInfoData.getSex());
			patient.set(Patient.FLD_NAME, patientInfoData.getName());
			patient.set(Patient.FLD_FIRSTNAME, patientInfoData.getPrename());
			patient.set(Patient.FLD_DOB, patientInfoData.getBirthday());
			patient.set(Patient.FLD_STREET, patientInfoData.getAddress());
			patient.set(Patient.FLD_ZIP, patientInfoData.getZip());
			patient.set(Patient.FLD_PLACE, patientInfoData.getLocation());
			patient.addXid(applicationProperties.getProperty(XID_AHV), patientInfoData.getCardholderIdentifier(), true);
			patient.addXid(applicationProperties.getProperty(XID_INSURED_NR), patientInfoData.getInsuredNumber(), true);
			patient.addXid(applicationProperties.getProperty(XID_CARD_NR), patientInfoData.getCardNumber(), true);
			patient.addXid(applicationProperties.getProperty(XID_INSURED_PERSON_NR),
					patientInfoData.getInsuredPersonNumber(), true);

			MessageDialog.openInformation(container.getShell(), messagesProperties.getProperty(MSG_TITLE),
					MessageFormat.format(messagesProperties.getProperty(MSG_NEW), patientInfoData.getPrename(),
							patientInfoData.getName()));

		}
		if (btn_showCandidats.getSelection() || btn_showAll.getSelection()) {
			for (Patient patient : allPatients) {
				if (patient.getId().equalsIgnoreCase(patientID)) {
					patient.set(Patient.FLD_SEX, patientInfoData.getSex());
					patient.set(Patient.FLD_NAME, patientInfoData.getName());
					patient.set(Patient.FLD_FIRSTNAME, patientInfoData.getPrename());
					patient.set(Patient.FLD_DOB, patientInfoData.getBirthday());
					patient.set(Patient.FLD_STREET, patientInfoData.getAddress());
					patient.set(Patient.FLD_ZIP, patientInfoData.getZip());
					patient.set(Patient.FLD_PLACE, patientInfoData.getLocation());
					patient.addXid(applicationProperties.getProperty(XID_AHV),
							patientInfoData.getCardholderIdentifier(), true);
					patient.addXid(applicationProperties.getProperty(XID_INSURED_NR),
							patientInfoData.getInsuredNumber(), true);
					patient.addXid(applicationProperties.getProperty(XID_CARD_NR), patientInfoData.getCardNumber(),
							true);
					patient.addXid(applicationProperties.getProperty(XID_INSURED_PERSON_NR),
							patientInfoData.getInsuredPersonNumber(), true);

					MessageDialog.openInformation(container.getShell(), messagesProperties.getProperty(MSG_TITLE),
							MessageFormat.format(messagesProperties.getProperty(MSG_MOD), patientInfoData.getPrename(),
									patientInfoData.getName()));
					break;
				}
			}
		}
		if (!btn_showCandidats.getSelection() && !btn_showAll.getSelection() && !btn_newPatient.getSelection()) {
			for (Patient patient : allPatients) {
				if (patient.getId().equalsIgnoreCase(patientID)) {
					patient.set(Patient.FLD_SEX, patientInfoData.getSex());
					patient.set(Patient.FLD_NAME, patientInfoData.getName());
					patient.set(Patient.FLD_FIRSTNAME, patientInfoData.getPrename());
					patient.set(Patient.FLD_DOB, patientInfoData.getBirthday());
					patient.set(Patient.FLD_STREET, patientInfoData.getAddress());
					patient.set(Patient.FLD_ZIP, patientInfoData.getZip());
					patient.set(Patient.FLD_PLACE, patientInfoData.getLocation());
					patient.addXid(applicationProperties.getProperty(XID_AHV),
							patientInfoData.getCardholderIdentifier(), true);
					patient.addXid(applicationProperties.getProperty(XID_INSURED_NR),
							patientInfoData.getInsuredNumber(), true);
					patient.addXid(applicationProperties.getProperty(XID_CARD_NR), patientInfoData.getCardNumber(),
							true);

					patient.addXid(applicationProperties.getProperty(XID_INSURED_PERSON_NR),
							patientInfoData.getInsuredPersonNumber(), true);

					MessageDialog.openInformation(container.getShell(), messagesProperties.getProperty(MSG_TITLE),
							MessageFormat.format(messagesProperties.getProperty(MSG_MOD), patientInfoData.getPrename(),
									patientInfoData.getName()));
					break;
				}
			}
		}
	}
}
