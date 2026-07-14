package ch.elexis.fire.core.internal;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

public class FindNonAnonResource extends AllRecursiveBundleResourceConsumer {

	@Override
	public void accept(Resource resource) {
		super.accept(resource);
		if (resource instanceof Patient) {
			if (((Patient) resource).hasName() || ((Patient) resource).hasAddress()) {
				throw new IllegalStateException("Found non anon resource");
			}
		}
	}

}
