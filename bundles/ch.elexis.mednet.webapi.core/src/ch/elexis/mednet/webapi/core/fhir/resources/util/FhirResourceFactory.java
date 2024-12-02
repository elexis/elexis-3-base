package ch.elexis.mednet.webapi.core.fhir.resources.util;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.findings.util.fhir.IFhirTransformer;
import ch.elexis.core.findings.util.fhir.IFhirTransformerRegistry;

@Component
(service = FhirResourceFactory.class)
public class FhirResourceFactory {

    @Reference
    private IFhirTransformerRegistry transformerRegistry;

    @SuppressWarnings("unchecked")
	public <FHIR> FHIR getResource(Object localObject, Class<?> localClass, Class<FHIR> resourceClass) {
		IFhirTransformer<FHIR, Object> transformer = (IFhirTransformer<FHIR, Object>) transformerRegistry
				.getTransformerFor(resourceClass, localClass);
		
        return transformer.getFhirObject(localObject)
                .orElseThrow(() -> new IllegalStateException("Could not create resource of type " + resourceClass.getSimpleName()));
    }
}
