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
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificateInfo.Type;
import ch.elexis.covid.cert.ui.handler.CovidHandlerUtil;

public class BillMissingTestCerts extends ExternalMaintenance {

	private int billCount;
	private int notBillableCount;

	private ICodeElementBlock kkBlock;
	private IMandator activeMandator;

	private List<IPatient> notBillablePatients;

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		kkBlock = getConfiguredBlock().orElseThrow(() -> new IllegalStateException(
				"Es ist kein Block für Krankenkasse zur Verrechnung von COVID Zertifikaten konfiguriert."));

		activeMandator = ContextServiceHolder.get().getActiveMandator()
				.orElseThrow(() -> new IllegalStateException("Es ist kein Mandant angemeldet."));

		IQuery<IPatient> patientsQuery = CoreModelServiceHolder.get().getQuery(IPatient.class);

		billCount = 0;
		notBillableCount = 0;

		try (IQueryCursor<IPatient> cursor = patientsQuery.executeAsCursor()) {
			pm.beginTask("Bitte warten, COVID Test Zertifikate werden verrechnet ...", cursor.size());
			while (cursor.hasNext()) {
				IPatient patient = cursor.next();
				List<CertificateInfo> certificates = CertificateInfo.of(patient);
				if (!certificates.isEmpty()) {
					List<CertificateInfo> testCertificates = certificates.stream().filter(c -> c.getType() == Type.TEST)
							.collect(Collectors.toList());

					testCertificates.forEach(cert -> {
						if (!isCertificateBilledAtDate(patient, cert.getTimestamp().toLocalDate())) {
							Optional<ICoverage> coverage = getCoverage(patient);
							if (coverage.isPresent()) {
								billCert(coverage.get(), cert);
								billCount++;
							} else {
								addNotBillable(patient);
								notBillableCount++;
							}
						}
					});
				}
				pm.worked(1);
			}
		}

		if (notBillablePatients != null && !notBillablePatients.isEmpty()) {
			File file = new File(CoreUtil.getWritableUserDir(), "NotBillablePatients.csv");
			try (FileWriter fw = new FileWriter(file)) {
				for (IPatient patient : notBillablePatients) {
					fw.write(patient.getPatientNr() + "," + patient.getLastName() + "," + patient.getFirstName() + ","
							+ patient.getDateOfBirth() + StringUtils.LF);

				}
			} catch (Exception e) {
				LoggerFactory.getLogger(getClass()).error("Error writing not billable patients", e);
			}
		}

		return "Es wurden " + billCount + " Zertifikate neu verrechnet." + (notBillableCount > 0 ? "\nEs gab "
				+ notBillableCount
				+ " Patienten bei denen nicht verrechnet werden konnte. (NotBillablePatients.csv Datei im user home elexis Verzeichnis)"
				: StringUtils.EMPTY);
	}

	/**
	 * Test if there is an encounter at the date with a billed covid certificate.
	 *
	 * @param patient
	 * @param localDate
	 * @return
	 */
	private boolean isCertificateBilledAtDate(IPatient patient, LocalDate localDate) {
		List<IEncounter> encountersAt = getAllEncountersAt(patient, localDate);
		return encountersAt.stream().filter(encounter -> isCertificateBilled(encounter)).findFirst().isPresent();
	}

	private void addNotBillable(IPatient patient) {
		if (notBillablePatients == null) {
			notBillablePatients = new ArrayList<>();
		}
		notBillablePatients.add(patient);
	}

	private void billCert(ICoverage coverage, CertificateInfo cert) {
		if (kkBlock != null) {
			IEncounter encounter = new IEncounterBuilder(CoreModelServiceHolder.get(), coverage, activeMandator)
					.date(cert.getTimestamp()).buildAndSave();

			LoggerFactory.getLogger(getClass()).info("Bill Certificate on new encounter [" + encounter + "] of ["
					+ encounter.getPatient().getPatientNr() + "]");
			// bill the block
			kkBlock.getElements(encounter).stream().filter(el -> el instanceof IBillable).map(el -> (IBillable) el)
					.forEach(billable -> BillingServiceHolder.get().bill(billable, encounter, 1));
		}
	}

	private Optional<ICodeElementBlock> getConfiguredBlock() {
		if (ConfigServiceHolder.get().get(CovidHandlerUtil.CFG_KK_BLOCKID, null) != null) {
			Optional<ICodeElementBlock> kkBlock = CoreModelServiceHolder.get().load(
					ConfigServiceHolder.get().get(CovidHandlerUtil.CFG_KK_BLOCKID, null), ICodeElementBlock.class);
			if (kkBlock.isPresent()) {
				return kkBlock;
			}
		}
		return Optional.empty();
	}

	/**
	 * Lookup the newest open coverage with KVG law.
	 *
	 * @param patient
	 * @return
	 */
	private Optional<ICoverage> getCoverage(IPatient patient) {
		List<ICoverage> coverages = patient.getCoverages();
		coverages.sort(new Comparator<ICoverage>() {
			@Override
			public int compare(ICoverage o1, ICoverage o2) {
				return o2.getDateFrom().compareTo(o1.getDateFrom());
			}
		});
		for (ICoverage coverage : coverages) {
			if (coverage.isOpen()) {
				if (coverage.getBillingSystem().getLaw() == BillingLaw.KVG) {
					return Optional.of(coverage);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Lookup encounters at date of all coverages.
	 *
	 * @param patient
	 * @param localDate
	 * @return
	 */
	private List<IEncounter> getAllEncountersAt(IPatient patient, LocalDate localDate) {
		if (patient.getCoverages() != null) {
			List<ICoverage> coverages = patient.getCoverages();
			coverages.sort(new Comparator<ICoverage>() {
				@Override
				public int compare(ICoverage o1, ICoverage o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			});
			return coverages.stream().flatMap(coverage -> coverage.getEncounters().stream())
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
		return "Noch nicht verrechnete COVID Test Zertifikate verrechnen.";
	}
}
