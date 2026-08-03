package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.INativeQuery;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

public class IndicationCodeUtil {

	// @formatter:off
	private static final String PRESCRIPTION_BYPATIENT_ANDARTIKEL = "SELECT id FROM patient_artikel_joint"
	+ " WHERE deleted = '0'"
	+ " AND PatientID = ?1"
	+ " AND Artikel = ?2"
	+ " ORDER BY DateFrom DESC";
	// @formatter:on

	public static Optional<String> getLastIndicationCode(IArtikelstammItem item, IPatient patient,
			List<EntryType> filterType) {
		INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(PRESCRIPTION_BYPATIENT_ANDARTIKEL);
		Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1), patient.getId(),
				Integer.valueOf(2), StoreToStringServiceHolder.getStoreToString(item));
		Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
		while (result.hasNext()) {
			String next = result.next().toString();
			IPrescription precription = CoreModelServiceHolder.get().load(next, IPrescription.class).get();
			if (filterType != null && !filterType.isEmpty()) {
				if (!filterType.contains(precription.getEntryType())) {
					continue;
				}
			}
			if (precription.getExtInfo(Constants.FLD_EXT_INDICATIONCODE) instanceof String indicationCode
					&& StringUtils.isNotBlank(indicationCode)) {
				return Optional.of(indicationCode);
			}
		}
		return Optional.empty();
	}

}
