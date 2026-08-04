package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Collections;
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
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

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
						// check if already a selection is present for patient and article
						// the selection is performed by the billing process see
						// GenericTypeOriginalAdjuster
						Optional<String> selection = IndicationCodeUtil
								.getIndicationCodeSelection(prescription.getPatient(), item);
						if (selection.isPresent()) {
							prescription.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selection.get());
							CoreModelServiceHolder.get().save(prescription);
							return;
						}

						Optional<String> indicationCodeHistory = IndicationCodeUtil.getLastIndicationCode(item,
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
}
