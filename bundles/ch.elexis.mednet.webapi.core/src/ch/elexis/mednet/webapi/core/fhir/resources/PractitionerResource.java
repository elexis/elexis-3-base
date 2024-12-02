package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Practitioner;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

public class PractitionerResource {

	/**
	 * Anpassung der Practitioner-Ressource: Setzt Address-Use auf WORK und passt
	 * GLN- und ZSR-Identifiers an.
	 *
	 * @param practitioner die Practitioner-Ressource
	 * @return die angepasste Practitioner-Ressource
	 */
	public static Practitioner adjustPractitioner(Practitioner practitioner) {
		// Adressen anpassen
		practitioner.getAddress().forEach(address -> {
			if (address.getUse() == Address.AddressUse.HOME) {
				address.setUse(Address.AddressUse.WORK);
			}
		});

		// Identifikatoren anpassen
		practitioner.getIdentifier().forEach(identifier -> {
			if (FHIRConstants.GLN_SYSTEM.equals(identifier.getSystem())) {
				identifier.setSystem(FHIRConstants.GLN_IDENTIFIER);
			}
			if (FHIRConstants.ZSR_SYSTEM.equals(identifier.getSystem())) {
				identifier.setSystem(FHIRConstants.ZSR_IDENTIFIER);
			}
		});

		return practitioner;
	}
}
