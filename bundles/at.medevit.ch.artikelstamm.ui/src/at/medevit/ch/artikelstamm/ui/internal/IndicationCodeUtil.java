package at.medevit.ch.artikelstamm.ui.internal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndication;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndicationInfo;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.INativeQuery;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

public class IndicationCodeUtil {

	private static LoadingCache<IndicationCodeLoaderKey, String> indicationSelectionCache = CacheBuilder.newBuilder()
			.expireAfterWrite(5, TimeUnit.SECONDS).build(new IndicationCodeLoader());

	// @formatter:off
	private static final String PRESCRIPTION_BYPATIENT_ANDARTIKEL = "SELECT id FROM patient_artikel_joint"
	+ " WHERE deleted = '0'"
	+ " AND PatientID = ?1"
	+ " AND Artikel = ?2"
	+ " ORDER BY DateFrom DESC";
	// @formatter:on

	/**
	 * Lookup the last used indication code set on {@link IPrescription} for the
	 * {@link IPatient} and the {@link IArtikelstammItem}. Which
	 * {@link IPrescription}s are considered can be configured with the filterType
	 * parameter, null or empty list considers all. If the {@link IPrescription} is
	 * active at current date is ignored.
	 * 
	 * @param item
	 * @param patient
	 * @param filterType
	 * @return
	 */
	public static Optional<String> getLastIndicationCode(IArtikelstammItem item, IPatient patient,
			List<EntryType> filterType) {
		return getLastIndicationCode(item, patient, filterType, false);

	}

	/**
	 * Lookup the last used indication code set on {@link IPrescription} for the
	 * {@link IPatient} and the {@link IArtikelstammItem}. Which
	 * {@link IPrescription}s are considered can be configured with the filterType
	 * parameter, null or empty list considers all. If parameter active is true only
	 * active {@link IPrescription} on current date are considered.
	 * 
	 * @param item
	 * @param patient
	 * @param filterType
	 * @param active
	 * @return
	 */
	public static Optional<String> getLastIndicationCode(IArtikelstammItem item, IPatient patient,
			List<EntryType> filterType, boolean active) {
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
			if (active) {
				if (precription.getDateTo() != null && precription.getDateTo().isBefore(LocalDateTime.now())) {
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

	/**
	 * Lookup the last used indication code set on {@link IBilled} for the
	 * {@link IPatient} and the {@link IArtikelstammItem}.
	 * 
	 * @param item
	 * @param patient
	 * @return
	 */
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
	 * If there is only a single indication code available for the item, that code
	 * is returned. Returns empty if multiple or no indication code available.
	 * 
	 * @param item
	 * @return
	 */
	public static Optional<String> getSingleIndicationCode(IArtikelstammItem item) {
		if (item != null && item.getIndicationInfo().isPresent()) {
			List<ArticleIndication> indications = item.getIndicationInfo().get().getIndications();
			if (indications.size() == 1) {
				return Optional.of(indications.get(0).getCode());
			}
		}
		return Optional.empty();
	}

	/**
	 * Check if the {@link IPrescription} has an {@link IArtikelstammItem} as
	 * article. Returns true if that {@link IArtikelstammItem#isPm()} and has no
	 * {@link Constants#FLD_EXT_INDICATIONCODE} information set.
	 * 
	 * @param prescription
	 * @return
	 */
	public static boolean needsIndicationCode(IPrescription prescription) {
		if (prescription.getArticle() instanceof IArtikelstammItem) {
			IArtikelstammItem item = (IArtikelstammItem) prescription.getArticle();
			return item.isPm()
					&& StringUtils.isBlank((String) prescription.getExtInfo(Constants.FLD_EXT_INDICATIONCODE));
		}
		return false;
	}

	/**
	 * Add an indication code to the provided {@link IPrescription} including user
	 * interaction if needed.
	 * 
	 * @param prescription
	 */
	public static void addIndicationCodeWithUi(IPrescription prescription) {
		IArtikelstammItem item = (IArtikelstammItem) prescription.getArticle();
		if (item.isPm()) {
			Optional<ArticleIndicationInfo> indicationInfo = item.getIndicationInfo();
			if (indicationInfo.isPresent() && !indicationInfo.get().getIndications().isEmpty()) {
				// direct apply single indication code
				Optional<String> singleIndicationCode = IndicationCodeUtil.getSingleIndicationCode(item);
				if (singleIndicationCode.isPresent()) {
					prescription.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, singleIndicationCode.get());
					CoreModelServiceHolder.get().save(prescription);
					return;
				}
				if (prescription.getEntryType() == EntryType.SELF_DISPENSED) {
					// lookup in prescriptions, and use that value without user interaction
					Optional<String> indicationCodeHistory = IndicationCodeUtil.getLastIndicationCode(item,
							prescription.getPatient(), Arrays.asList(EntryType.FIXED_MEDICATION,
									EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION),
							true);
					if (indicationCodeHistory.isPresent()) {
						prescription.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, indicationCodeHistory.get());
						CoreModelServiceHolder.get().save(prescription);
						return;
					}
				}
				Optional<String> selection = getIndicationCodeSelection(prescription.getPatient(), item, prescription,
						null);
				if (selection.isPresent()) {
					prescription.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selection.get());
					CoreModelServiceHolder.get().save(prescription);
					// apply to other medication if self dispensed was missing information
					if (prescription.getEntryType() == EntryType.SELF_DISPENSED) {
						IndicationCodeUtil.applyToMedicationIfMissing(prescription);
					}
				}
			}
		}
	}

	/**
	 * Get the indication code for the provided {@link IPatient} and
	 * {@link IArtikelstammItem} using a cache to handle concurrency and billing in
	 * combination with prescribing. The provided {@link IPrescription} or
	 * {@link IBilled} control where the lookup of last indication code for the ui
	 * is performed. For the combination of {@link IPatient} and
	 * {@link IArtikelstammItem} the cache holds the value for 5 seconds.
	 * 
	 * @param patient
	 * @param item
	 * @param prescription
	 * @param billed
	 * @return
	 */
	public static Optional<String> getIndicationCodeSelection(IPatient patient, IArtikelstammItem item,
			IPrescription prescription, IBilled billed) {
		try {
			String selection = indicationSelectionCache
					.get(IndicationCodeLoaderKey.of(patient, item, prescription, billed));
			if (StringUtils.isNotBlank(selection)) {
				return Optional.of(selection);
			}
		} catch (ExecutionException e) {
			LoggerFactory.getLogger(IndicationCodeUtil.class).error("Error getting indication code", e);
		}
		return Optional.empty();
	}

	/**
	 * Lookup of other {@link IPrescription} with type
	 * {@link EntryType#FIXED_MEDICATION} or
	 * {@link EntryType#SYMPTOMATIC_MEDICATION} or
	 * {@link EntryType#RESERVE_MEDICATION} with the same {@link IArtikelstammItem}
	 * for the {@link IPatient} that are not stopped at current date time. And apply
	 * the {@link Constants#FLD_EXT_INDICATIONCODE} extinfo from the provided
	 * {@link IPrescription} on those.
	 * 
	 * @param prescription
	 */
	public static void applyToMedicationIfMissing(IPrescription prescription) {
		applyToMedicationIfMissing(prescription.getPatient(), (IArtikelstammItem) prescription.getArticle(),
				(String) prescription.getExtInfo(Constants.FLD_EXT_INDICATIONCODE));
	}

	/**
	 * Lookup of other {@link IPrescription} with type
	 * {@link EntryType#FIXED_MEDICATION} or
	 * {@link EntryType#SYMPTOMATIC_MEDICATION} or
	 * {@link EntryType#RESERVE_MEDICATION} with the same {@link IArtikelstammItem}
	 * for the {@link IPatient} that are not stopped at current date time. And apply
	 * the provided indicationCode on those.
	 * 
	 * @param prescription
	 */
	public static void applyToMedicationIfMissing(IPatient patient, IArtikelstammItem item, String indicationCode) {
		List<IPrescription> medicationMissingIndication = new ArrayList<>();
		List<EntryType> filterType = Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION,
				EntryType.RESERVE_MEDICATION);
		INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(PRESCRIPTION_BYPATIENT_ANDARTIKEL);
		Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1), patient.getId(),
				Integer.valueOf(2), StoreToStringServiceHolder.getStoreToString(item));
		Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
		while (result.hasNext()) {
			String next = result.next().toString();
			IPrescription loadedPrecription = CoreModelServiceHolder.get().load(next, IPrescription.class).get();
			if (filterType != null && !filterType.isEmpty()) {
				if (!filterType.contains(loadedPrecription.getEntryType())) {
					continue;
				}
			}
			if (loadedPrecription.getDateTo() != null && loadedPrecription.getDateTo().isBefore(LocalDateTime.now())) {
				continue;
			}
			if (StringUtils.isNotBlank((String) loadedPrecription.getExtInfo(Constants.FLD_EXT_INDICATIONCODE))) {
				continue;
			}
			medicationMissingIndication.add(loadedPrecription);
		}
		medicationMissingIndication.forEach(missing -> {
			missing.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, indicationCode);
			CoreModelServiceHolder.get().save(missing);
			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, missing);
		});

	}

	private static class IndicationCodeLoaderKey {
		
		private IPatient patient;
		private IArtikelstammItem item;
		
		private IPrescription prescription;
		private IBilled billed;
		
		public static IndicationCodeLoaderKey of(IPatient patient, IArtikelstammItem item, IPrescription prescription,
				IBilled billed) {
			IndicationCodeLoaderKey ret = new IndicationCodeLoaderKey();
			ret.patient = patient;
			ret.item = item;
			ret.prescription = prescription;
			ret.billed = billed;
			return ret;
		}

		@Override
		public int hashCode() {
			return Objects.hash(item, patient);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IndicationCodeLoaderKey other = (IndicationCodeLoaderKey) obj;
			return Objects.equals(item, other.item) && Objects.equals(patient, other.patient);
		}

	}

	private static class IndicationCodeLoader extends CacheLoader<IndicationCodeLoaderKey, String> {

		private String indicationCodeSelection;

		private Optional<String> indicationCodeHistory;

		@Override
		public synchronized String load(IndicationCodeLoaderKey key) throws Exception {
			indicationCodeSelection = StringUtils.EMPTY;
			if (key != null) {
				indicationCodeHistory = Optional.empty();
				if (key.prescription != null) {
					indicationCodeHistory = IndicationCodeUtil.getLastIndicationCode(key.item,
							key.patient, Collections.emptyList());
				} else if (key.billed != null) {
					indicationCodeHistory = IndicationCodeUtil.getLastIndicationCode(key.item, key.patient);
				}

				Display.getDefault().syncExec(() -> {
					IndicationCodeSelectionDialog dialog = new IndicationCodeSelectionDialog(key.item,
							Display.getDefault().getActiveShell());

					indicationCodeHistory.ifPresent(code -> {
						dialog.setSelectedCode(code);
					});
					if (dialog.open() == Window.OK) {
						if (dialog.getSelectedCode() instanceof String) {
							indicationCodeSelection = dialog.getSelectedCode();
						}
					}
				});
			}
			return indicationCodeSelection;
		}
	}
}
