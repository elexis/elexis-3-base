package ch.elexis.covid.cert.dbcheck;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ISticker;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StickerServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificateInfo.Type;
import ch.elexis.scripting.CSVWriter;

public class FixBillMissingTestCerts extends ExternalMaintenance {

	private List<FixInfo> fixInfos;

	private SelectOptionsDialog dialog;
	private boolean dialogOk;

	private int invoiceStateChangeCount;
	private int encounterStickerCount;
	private int encounterDeleteCount;

	private class FixInfo {
		private IPatient patient;
		private LocalDate localDate;
		private List<IEncounter> encounters;

		public FixInfo(IPatient patient, LocalDate localDate, List<IEncounter> encounters) {
			this.patient = patient;
			this.localDate = localDate;
			this.encounters = encounters;
		}

		public String getInsurance() {
			Optional<IContact> insurance = encounters.stream().map(e -> e.getCoverage().getCostBearer())
					.filter(c -> c != null && c.isOrganization()).findFirst();
			return insurance.isPresent() ? insurance.get().getLabel() : StringUtils.EMPTY;
		}

		public String getInvoiceNumber() {
			return encounters.stream().filter(e -> e.getInvoice() != null).map(e -> e.getInvoice().getNumber())
					.collect(Collectors.joining("/"));
		}

		public String getInvoiceStatus() {
			return encounters.stream().filter(e -> e.getInvoice() != null)
					.map(e -> e.getInvoice().getState().toString()).collect(Collectors.joining("/"));
		}

		public String getInvoiceAmount() {
			return encounters.stream().filter(e -> e.getInvoice() != null)
					.map(e -> ((Double) e.getInvoice().getTotalAmount().doubleValue()).toString())
					.collect(Collectors.joining("/"));
		}

		public List<IInvoice> getInvoicesToFix() {
			return encounters.stream().filter(e -> e.getInvoice() != null)
					.filter(e -> e.getCoverage().getBillingSystem().getLaw() == BillingLaw.KVG).map(e -> e.getInvoice())
					.collect(Collectors.toList());
		}

		public List<IEncounter> getEncountersToFix() {
			return encounters.stream().filter(e -> e.getCoverage().getBillingSystem().getLaw() == BillingLaw.KVG)
					.collect(Collectors.toList());
		}
	}

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		dialog = null;
		dialogOk = false;
		Display display = Display.getDefault();
		if (display != null) {
			display.syncExec(() -> {
				dialog = new SelectOptionsDialog(display.getActiveShell());
				dialogOk = dialog.open() == Window.OK;
			});
		}
		if (dialog != null) {
			invoiceStateChangeCount = 0;
			encounterStickerCount = 0;
			encounterDeleteCount = 0;

			IQuery<IPatient> patientsQuery = CoreModelServiceHolder.get().getQuery(IPatient.class);

			fixInfos = new ArrayList<>();

			try (IQueryCursor<IPatient> cursor = patientsQuery.executeAsCursor()) {
				pm.beginTask("Bitte warten, falsch verrechnete COVID Test Zertifikate werden gesucht ...",
						cursor.size());
				while (cursor.hasNext()) {
					IPatient patient = cursor.next();
					List<CertificateInfo> certificates = CertificateInfo.of(patient);
					if (!certificates.isEmpty()) {
						List<CertificateInfo> testCertificates = certificates.stream()
								.filter(c -> c.getType() == Type.TEST).collect(Collectors.toList());

						testCertificates.forEach(cert -> {
							List<IEncounter> encountersAt = getEncountersAt(patient, cert.getTimestamp().toLocalDate());

							List<IEncounter> certificateBilledEncounters = encountersAt.stream()
									.filter(encounter -> isCertificateBilled(encounter)).collect(Collectors.toList());
							if (certificateBilledEncounters.size() > 1) {
								fixInfos.add(new FixInfo(patient, cert.getTimestamp().toLocalDate(),
										certificateBilledEncounters));
							}
						});
					}
					pm.worked(1);
				}
			}

			if (fixInfos != null && !fixInfos.isEmpty()) {
				writeCsv();
				if (dialogOk) {
					if (dialog.isInvoiceStatus()) {
						for (FixInfo fixInfo : fixInfos) {
							List<IInvoice> invoices = fixInfo.getInvoicesToFix();
							invoices.forEach(i -> {
								if (i.getState() == InvoiceState.PAID) {
									i.setState(InvoiceState.TOTAL_LOSS);
									invoiceStateChangeCount++;
								} else if (i.getState() != InvoiceState.PARTIAL_LOSS) {
									i.setState(InvoiceState.PARTIAL_LOSS);
									invoiceStateChangeCount++;
								}
							});
							CoreModelServiceHolder.get().save(invoices);
						}
					}
					if (dialog.isEncounterSticker()) {
						ISticker fixEncountersSticker = getOrCreateSticker("fix_bill_test_certs");
						for (FixInfo fixInfo : fixInfos) {
							List<IEncounter> encounters = fixInfo.getEncountersToFix();
							encounters.forEach(e -> {
								if (!StickerServiceHolder.get().hasSticker(e, fixEncountersSticker)) {
									StickerServiceHolder.get().addSticker(fixEncountersSticker, e);
									encounterStickerCount++;
								}
							});
						}
					}
					if (dialog.isDeleteEncounterWithSticker()) {
						ISticker fixEncountersSticker = getOrCreateSticker("fix_bill_test_certs");
						List<IEncounter> markedEncounters = StickerServiceHolder.get()
								.getObjectsWithSticker(fixEncountersSticker, IEncounter.class);
						markedEncounters.forEach(e -> {
							List<IBilled> encounterBilled = new ArrayList<IBilled>(e.getBilled());
							for (IBilled billed : encounterBilled) {
								e.removeBilled(billed);
							}
							CoreModelServiceHolder.get().delete(e);
							encounterDeleteCount++;
						});
					}
				}
			}

			return "Es wurden " + fixInfos.size()
					+ " falsch verrechnete Zertifikate gefunden.\n(FixCovidBilled.csv Datei im user home elexis Verzeichnis)\n"
					+ "Es wurden " + invoiceStateChangeCount + " Rechngsstatus geändert\n" + "Es wurden "
					+ encounterStickerCount + " Konsultationen mit Sticker markiert\n" + "Es wurden "
					+ encounterDeleteCount + " Konsultationen gelöscht\n";
		}

		return "Konnte Optionsdialog nicht öffnen";
	}

	private ISticker getOrCreateSticker(String name) {
		IQuery<ISticker> query = CoreModelServiceHolder.get().getQuery(ISticker.class);
		query.and("name", COMPARATOR.EQUALS, name);
		ISticker existing = query.executeSingleResult().orElse(null);
		if (existing == null) {
			existing = CoreModelServiceHolder.get().create(ISticker.class);
			existing.setName(name);
			existing.setForeground("000000");
			existing.setBackground("ffffff");
			CoreModelServiceHolder.get().save(existing);
			StickerServiceHolder.get().setStickerAddableToClass(IEncounter.class, existing);
		}
		return existing;
	}

	private void writeCsv() {
		File file = new File(CoreUtil.getWritableUserDir(), "FixCovidBilled.csv");
		try (FileWriter fw = new FileWriter(file)) {
			CSVWriter csv = new CSVWriter(fw);
			String[] header = new String[] { "PatNr", "Name", "Vorname", "GebDatum", "KonsDatum", "Krankenkasse",
					"RGNr", "RG-Status", "RG-Betrag" };
			csv.writeNext(header);

			for (FixInfo fixInfo : fixInfos) {
				String[] line = new String[header.length];
				line[0] = fixInfo.patient.getPatientNr();
				line[1] = fixInfo.patient.getLastName();
				line[2] = fixInfo.patient.getFirstName();
				line[3] = fixInfo.patient.getDateOfBirth() != null
						? fixInfo.patient.getDateOfBirth().toLocalDate().toString()
						: StringUtils.EMPTY;
				line[4] = fixInfo.localDate.toString();
				line[5] = fixInfo.getInsurance();
				line[6] = fixInfo.getInvoiceNumber();
				line[7] = fixInfo.getInvoiceStatus();
				line[8] = fixInfo.getInvoiceAmount();
				csv.writeNext(line);
			}
			csv.close();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error writing fix covid billed info", e);
		}
	}

	/**
	 * Lookup encounters at date of the newest open coverage with KVG law.
	 *
	 * @param patient
	 * @param localDate
	 * @return
	 */
	private List<IEncounter> getEncountersAt(IPatient patient, LocalDate localDate) {
		if (patient.getCoverages() != null) {
			List<ICoverage> coverages = patient.getCoverages();
			coverages.sort(new Comparator<ICoverage>() {
				@Override
				public int compare(ICoverage o1, ICoverage o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			});
			return coverages.stream().filter(coverage -> coverage.isOpen())
					.flatMap(coverage -> coverage.getEncounters().stream())
					.filter(encounter -> encounter.getDate().equals(localDate)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private boolean isCertificateBilled(IEncounter encounter) {
		Optional<IBilled> found = encounter.getBilled().stream().filter(billed -> isCertificateBilled(billed))
				.findFirst();
		return found.isPresent();
	}

	private boolean isCertificateBilled(IBilled billed) {
		IBillable billable = billed.getBillable();
		if (billable != null && "351".equals(billable.getCodeSystemCode())) {
			return "01.01.1300".equals(billable.getCode()) || "01.99.1300".equals(billable.getCode());
		}
		return false;
	}

	@Override
	public String getMaintenanceDescription() {
		return "Falsch verrechnete COVID Test Zertifikate.";
	}

	public class SelectOptionsDialog extends TitleAreaDialog {

		private Button setInvoiceStatusBtn;
		private boolean invoiceStatus;
		private Button setEncounterStickerBtn;
		private boolean encounterSticker;
		private Button deleteEncountersWithStickerBtn;
		private boolean deleteEncounterWithSticker;

		public SelectOptionsDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			setTitle("Optionen für die falsch verrechneten COVID Test Zertifikate.");
			setMessage(
					"Es sind verschiedene Optionen für die falsch verrechneten COVID Test Zertifikate verfügbar, bitte wählen Sie eine oder mehrere aus.");

			Composite container = (Composite) super.createDialogArea(parent);
			Composite area = new Composite(container, SWT.NONE);
			area.setLayoutData(new GridData(GridData.FILL_BOTH));
			area.setLayout(new GridLayout(1, false));

			setInvoiceStatusBtn = new Button(area, SWT.CHECK);
			setInvoiceStatusBtn.setText("Falsch verrechnete Rechnungen Status ändern");
			setInvoiceStatusBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					invoiceStatus = setInvoiceStatusBtn.getSelection();
				}
			});
			Label lblDesc = new Label(area, SWT.NONE);
			lblDesc.setText("Für alle gefundenen Rechnungen mit falsch verrechneten COVID Test Zertifikaten.\n"
					+ "Bei Rechnungsstaus Bezahlt -> Totalverlust, bei allen anderen Rechnungsstaus -> Teilverlust");

			setEncounterStickerBtn = new Button(area, SWT.CHECK);
			setEncounterStickerBtn.setText("Falsch verrechnete Konsultation mit Sticker markieren");
			setEncounterStickerBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					encounterSticker = setEncounterStickerBtn.getSelection();
				}
			});
			lblDesc = new Label(area, SWT.NONE);
			lblDesc.setText(
					"Für alle (auch die ohne Rechnung) gefundenen Konsultationen mit falsch verrechneten COVID Test Zertifikaten.\n"
							+ "Wird die Konsultation mit einem Sticker versehen. Damit kann diese unabhängig von der Rechnung gefunden werden.");

			deleteEncountersWithStickerBtn = new Button(area, SWT.CHECK);
			deleteEncountersWithStickerBtn.setText("Konsultation mit Sticker löschen");
			deleteEncountersWithStickerBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					deleteEncounterWithSticker = deleteEncountersWithStickerBtn.getSelection();
				}
			});
			lblDesc = new Label(area, SWT.NONE);
			lblDesc.setText("Für alle gefundenen Konsultationen die markiert wurden.\n"
					+ "Alle Konsultationen die zuvor mit dem Sticker versehen wurde, werden gelöscht.");

			return area;
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID, "OK", false);
		}

		public boolean isInvoiceStatus() {
			return invoiceStatus;
		}

		public boolean isEncounterSticker() {
			return encounterSticker;
		}

		public boolean isDeleteEncounterWithSticker() {
			return deleteEncounterWithSticker;
		}
	}
}
