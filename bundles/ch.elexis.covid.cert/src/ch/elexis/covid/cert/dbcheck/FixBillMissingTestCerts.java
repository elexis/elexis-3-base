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

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificateInfo.Type;
import ch.elexis.scripting.CSVWriter;

public class FixBillMissingTestCerts extends ExternalMaintenance {
	
	private List<FixInfo> fixInfos;
	
	private class FixInfo {
		private IPatient patient;
		private LocalDate localDate;
		private List<IEncounter> encounters;
		
		public FixInfo(IPatient patient, LocalDate localDate, List<IEncounter> encounters){
			this.patient = patient;
			this.localDate = localDate;
			this.encounters = encounters;
		}
		
		public String getInsurance(){
			Optional<IContact> insurance =
				encounters.stream().map(e -> e.getCoverage().getCostBearer())
					.filter(c -> c != null && c.isOrganization()).findFirst();
			return insurance.isPresent() ? insurance.get().getLabel() : "";
		}
		
		public String getInvoiceNumber(){
			return encounters.stream().filter(e -> e.getInvoice() != null)
				.map(e -> e.getInvoice().getNumber())
				.collect(Collectors.joining("/"));
		}
		
		public String getInvoiceStatus(){
			return encounters.stream().filter(e -> e.getInvoice() != null)
				.map(e -> e.getInvoice().getState().toString())
				.collect(Collectors.joining("/"));
		}
		
		public String getInvoiceAmount(){
			return encounters.stream().filter(e -> e.getInvoice() != null)
				.map(e -> ((Double) e.getInvoice().getTotalAmount().doubleValue()).toString())
				.collect(Collectors.joining("/"));
		}
	}
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		IQuery<IPatient> patientsQuery = CoreModelServiceHolder.get().getQuery(IPatient.class);
		
		fixInfos = new ArrayList<>();
		
		try (IQueryCursor<IPatient> cursor = patientsQuery.executeAsCursor()) {
			pm.beginTask(
				"Bitte warten, falsch verrechnete COVID Test Zertifikate werden gesucht ...",
				cursor.size());
			while (cursor.hasNext()) {
				IPatient patient = cursor.next();
				List<CertificateInfo> certificates = CertificateInfo.of(patient);
				if (!certificates.isEmpty()) {
					List<CertificateInfo> testCertificates = certificates.stream()
						.filter(c -> c.getType() == Type.TEST)
						.collect(Collectors.toList());
					
					testCertificates.forEach(cert -> {
						List<IEncounter> encountersAt =
							getEncountersAt(patient, cert.getTimestamp().toLocalDate());
						
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
			File file = new File(CoreUtil.getWritableUserDir(), "FixCovidBilled.csv");
			try (FileWriter fw = new FileWriter(file)) {
				CSVWriter csv = new CSVWriter(fw);
				String[] header = new String[] {
					"PatNr", "Name", "Vorname", "GebDatum", "KonsDatum", "Krankenkasse", "RGNr",
					"RG-Status", "RG-Betrag"
				};
				csv.writeNext(header);
				
				for (FixInfo fixInfo : fixInfos) {
					String[] line = new String[header.length];
					line[0] = fixInfo.patient.getPatientNr();
					line[1] = fixInfo.patient.getLastName();
					line[2] = fixInfo.patient.getFirstName();
					line[3] = fixInfo.patient.getDateOfBirth() != null
							? fixInfo.patient.getDateOfBirth().toLocalDate().toString()
							: "";
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
		
		return "Es wurden " + fixInfos.size() + " falsch verrechnete Zertifikate gefunden.\n(FixCovidBilled.csv Datei im user home elexis Verzeichnis)";
	}
	
	/**
	 * Lookup encounters at date of the newest open coverage with KVG law.
	 * 
	 * @param patient
	 * @param localDate
	 * @return
	 */
	private List<IEncounter> getEncountersAt(IPatient patient, LocalDate localDate){
		if (patient.getCoverages() != null) {
			List<ICoverage> coverages = patient.getCoverages();
			coverages.sort(new Comparator<ICoverage>() {
				@Override
				public int compare(ICoverage o1, ICoverage o2){
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			});
			return coverages.stream()
				.filter(coverage -> coverage.isOpen())
				.flatMap(coverage -> coverage.getEncounters().stream())
				.filter(encounter -> encounter.getDate().equals(localDate))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	private boolean isCertificateBilled(IEncounter encounter){
		Optional<IBilled> found = encounter.getBilled().stream()
			.filter(billed -> isCertificateBilled(billed))
			.findFirst();
		return found.isPresent();
	}
	
	private boolean isCertificateBilled(IBilled billed){
		IBillable billable = billed.getBillable();
		if (billable != null && "351".equals(billable.getCodeSystemCode())) {
			return "01.01.1300".equals(billable.getCode())
				|| "01.99.1300".equals(billable.getCode());
		}
		return false;
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "Falsch verrechnete COVID Test Zertifikate.";
	}
}
