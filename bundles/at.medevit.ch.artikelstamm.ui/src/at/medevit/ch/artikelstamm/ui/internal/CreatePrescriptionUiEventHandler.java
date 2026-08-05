package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndicationInfo;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;

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
		if (prescription.getArticle() instanceof IArtikelstammItem) {
			if (StringUtils.isBlank((String) prescription.getExtInfo(Constants.FLD_EXT_INDICATIONCODE))) {
				IArtikelstammItem item = (IArtikelstammItem) prescription.getArticle();
				if (item.isPm()) {
					Optional<ArticleIndicationInfo> indicationInfo = item.getIndicationInfo();
					if (indicationInfo.isPresent() && !indicationInfo.get().getIndications().isEmpty()) {

//						if (selection.isPresent()) {
//								prescription.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selection.get());
//								CoreModelServiceHolder.get().save(prescription);
//								if (prescription.getEntryType() == EntryType.SELF_DISPENSED) {
//									IndicationCodeUtil.applyToMedicationIfMissing(prescription);
//								}
//								return;
//							}
							IndicationCodeUtil.addIndicationCodeWithUi(prescription);
					}
				}
			}
		}
	}
}
