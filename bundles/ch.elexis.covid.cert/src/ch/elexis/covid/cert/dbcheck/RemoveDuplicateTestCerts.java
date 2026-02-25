package ch.elexis.covid.cert.dbcheck;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificateInfo.Type;

public class RemoveDuplicateTestCerts extends ExternalMaintenance {

	private IDocumentStore omnivoreDocumentStore;

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		omnivoreDocumentStore = OsgiServiceUtil
				.getService(IDocumentStore.class, "(storeid=ch.elexis.data.store.omnivore)")
				.orElseThrow(() -> new IllegalStateException("No Omnivore Document Store available."));

		IQuery<IPatient> patientsQuery = CoreModelServiceHolder.get().getQuery(IPatient.class);

		int removeCount = 0;
		int totalCount = 0;

		try (IQueryCursor<IPatient> cursor = patientsQuery.executeAsCursor()) {
			pm.beginTask("Bitte warten, COVID Test Zertifikat Duplikate werden entfernt ...", cursor.size());
			while (cursor.hasNext()) {
				IPatient patient = cursor.next();
				List<CertificateInfo> certificates = CertificateInfo.of(patient);
				if (!certificates.isEmpty()) {
					List<CertificateInfo> testCertificates = certificates.stream().filter(c -> c.getType() == Type.TEST)
							.collect(Collectors.toList());
					totalCount += testCertificates.size();
					Map<LocalDate, List<CertificateInfo>> certificatesDayMap = getCertificatesDayMap(testCertificates);
					for (LocalDate date : certificatesDayMap.keySet()) {
						List<CertificateInfo> list = certificatesDayMap.get(date);
						if (list.size() > 1) {
							list.sort(new Comparator<CertificateInfo>() {
								@Override
								public int compare(CertificateInfo o1, CertificateInfo o2) {
									return o2.getTimestamp().compareTo(o1.getTimestamp());
								}
							});
							for (int i = 1; i < list.size(); i++) {
								CertificateInfo info = list.get(i);
								java.util.Optional<IDocument> document = omnivoreDocumentStore
										.loadDocument(info.getDocumentId());
								if (document.isPresent()) {
									omnivoreDocumentStore.removeDocument(document.get());
								}
								CertificateInfo.remove(info, patient);
								removeCount++;
							}
						}
					}
				}
				pm.worked(1);
			}
		}

		OsgiServiceUtil.ungetService(omnivoreDocumentStore);

		return "Es wurden " + removeCount + " Duplikate aus " + totalCount + " Zertifikaten entfernt.";
	}

	private Map<LocalDate, List<CertificateInfo>> getCertificatesDayMap(List<CertificateInfo> testCertificates) {
		Map<LocalDate, List<CertificateInfo>> ret = new HashMap<LocalDate, List<CertificateInfo>>();
		for (CertificateInfo certificateInfo : testCertificates) {
			LocalDate date = certificateInfo.getTimestamp().toLocalDate();
			List<CertificateInfo> list = ret.get(date);
			if (list == null) {
				list = new ArrayList<CertificateInfo>();
			}
			list.add(certificateInfo);
			ret.put(date, list);
		}
		return ret;
	}

	@Override
	public String getMaintenanceDescription() {
		return "COVID Test Zertifikat Duplikate (am selben Tag) entfernen.";
	}
}
