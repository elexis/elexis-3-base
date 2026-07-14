package ch.elexis.fire.core.internal;

import java.util.function.Consumer;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Resource;

public abstract class AllRecursiveBundleResourceConsumer implements Consumer<Resource> {

	@Override
	public void accept(Resource resource) {
		if (resource instanceof Bundle) {
			traverseAllResources((Bundle) resource, this);
		}
	}

	private void traverseAllResources(Bundle bundle, Consumer<Resource> consumer) {
		for (BundleEntryComponent entry : bundle.getEntry()) {
			if (entry.hasResource()) {
				consumer.accept(entry.getResource());
			}
		}
	}
}
