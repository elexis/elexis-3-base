package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.INativeQuery;
import ch.elexis.core.services.holder.ContextServiceHolder;
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

	// @formatter:off
	private static final String VERRECHNET_BYPATIENT_ANDCODE = "SELECT leistungen.ID FROM leistungen, behandlungen, faelle"
	+ " WHERE leistungen.deleted = '0'"
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.artikelstamm.elexis.common.ArtikelstammItem'"
	+ " AND faelle.ID = behandlungen.fallID"
	+ " AND faelle.PatientID = ?1"
	+ " AND leistungen.LEISTG_CODE like ?2"
	+ " ORDER BY behandlungen.Datum DESC";
	// @formatter:on

	public static Optional<String> getLastIndicationCode(IArtikelstammItem item, IPatient patient) {
		INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(VERRECHNET_BYPATIENT_ANDCODE);
		String itemIdStart = item.getId().substring(0, item.getId().indexOf(item.getCode()) + item.getCode().length());
		Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1), patient.getId(),
				Integer.valueOf(2), itemIdStart + "%");
		Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
		while (result.hasNext()) {
			String next = result.next().toString();
			IBilled billed = CoreModelServiceHolder.get().load(next, IBilled.class).get();
			if (billed.getExtInfo(Constants.FLD_EXT_INDICATIONCODE) instanceof String indicationCode
					&& StringUtils.isNotBlank(indicationCode)) {
				return Optional.of(indicationCode);
			}
		}
		return Optional.empty();
	}

	/**
	 * Lookup the "indicationcode.selection" named matching the provided
	 * {@link IPatient} and {@link IArtikelstammItem} from the
	 * {@link IContextService}. The current value is always cleared after lookup.
	 * 
	 * @param patient
	 * @param item
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Optional<String> getIndicationCodeSelection(IPatient patient, IArtikelstammItem item) {
		Optional<String> ret = Optional.empty();
		Optional<String> selection = (Optional<String>) ContextServiceHolder.get().getNamed("indicationcode.selection");
		if (selection.isPresent()) {
			String prefix = patient.getId() + "|" + item.getId() + "|";
			if (selection.get().startsWith(prefix)) {
				ret = Optional.of(selection.get().substring(prefix.length()));
			}
			ContextServiceHolder.get().setNamed("indicationcode.selection", null);
		}
		return ret;
	}

	public static void setIndicationCodeSelection(IPatient patient, IArtikelstammItem item, String selectedCode) {
		ContextServiceHolder.get().setNamed("indicationcode.selection",
				toIndicationCodeSelection(patient, item, selectedCode));
	}

	private static String toIndicationCodeSelection(IPatient patient, IArtikelstammItem item, String selectedCode) {
		return patient.getId() + "|" + item.getId() + "|" + selectedCode;
	}
}
