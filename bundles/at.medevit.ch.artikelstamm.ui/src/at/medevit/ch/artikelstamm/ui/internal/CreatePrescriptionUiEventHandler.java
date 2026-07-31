package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndicationInfo;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.INativeQuery;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

@Component(property = { EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.PERSISTENCE_EVENT_COMPATIBILITY_CREATE,
		EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.EVENT_CREATE })
public class CreatePrescriptionUiEventHandler implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		Object object = event.getProperty(ElexisEventTopics.ECLIPSE_E4_DATA);
		if (object instanceof IPrescription) {
			handlePrescription((IPrescription) object);
		}
	}

	private void handlePrescription(IPrescription prescription) {
		// self dispensed has indication on bill
		if (prescription.getEntryType() != EntryType.SELF_DISPENSED
				&& prescription.getArticle() instanceof IArtikelstammItem) {
			if (StringUtils.isBlank((String) prescription.getExtInfo(Constants.FLD_EXT_INDICATIONCODE))) {
				IArtikelstammItem item = (IArtikelstammItem) prescription.getArticle();
				if (item.isPm()) {
					Optional<ArticleIndicationInfo> indicationInfo = item.getIndicationInfo();
					if (indicationInfo.isPresent() && !indicationInfo.get().getIndications().isEmpty()) {
						Optional<String> indicationCodeHistory = getLastIndicationCode(item,
									prescription.getPatient(), Collections.emptyList());

						Display.getDefault().syncExec(() -> {
							IndicationCodeSelectionDialog dialog = new IndicationCodeSelectionDialog(item,
									Display.getDefault().getActiveShell());

							indicationCodeHistory.ifPresent(code -> {
								dialog.setSelectedCode(code);
							});
							if (dialog.open() == Window.OK) {
								if (dialog.getSelectedCode() instanceof String selectedCode) {
									prescription.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selectedCode);
									CoreModelServiceHolder.get().save(prescription);
								}
							}
						});
					}
				}
			}
		}
	}

	// @formatter:off
	private static final String PRESCRIPTION_BYPATIENT_ANDARTIKEL = "SELECT id FROM patient_artikel_joint"
	+ " WHERE deleted = '0'"
	+ " AND PatientID = ?1"
	+ " AND Artikel = ?2"
	+ " ORDER BY DateFrom DESC";
	// @formatter:on

	private Optional<String> getLastIndicationCode(IArtikelstammItem item, IPatient patient,
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
