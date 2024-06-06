package at.medevit.elexis.hin.sign.ui.event;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.status.ObjectStatus;

@Component(property = { EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.PERSISTENCE_EVENT_COMPATIBILITY_DELETE,
		EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.EVENT_DELETE })
public class DeleteRecipeEventHandler implements EventHandler {

	@Reference
	private IHinSignService hinSignService;

	@Override
	public void handleEvent(Event event) {
		Object object = event.getProperty(ElexisEventTopics.ECLIPSE_E4_DATA);
		if (object instanceof IRecipe) {
			String chmedUrl = hinSignService.getPrescriptionUrl((IRecipe) object).orElse(null);
			if (chmedUrl != null) {
				ObjectStatus<?> verification = hinSignService.verifyPrescription(chmedUrl);
				if (verification.isOK()) {
					Map<?, ?> verificationMap = (Map<?, ?>) verification.getObject();
					if ((Boolean) verificationMap.get("valid") && !(Boolean) verificationMap.get("revoked")) {
						ObjectStatus<?> revoke = hinSignService
								.revokePrescription((String) verificationMap.get("prescription_id"));
						if (!revoke.isOK()) {
							LoggerFactory.getLogger(getClass())
									.warn("Revoking eprescription [" + verificationMap.get("prescription_id")
											+ "] failed");
						}
					}
				}
			}
		}
	}
}
