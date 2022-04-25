package ch.elexis.icpc.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.ICodeElementService;

@Component
public class CodeElementServiceHolder {

	private static ICodeElementService service;

	@Reference
	public void setCodeElementService(ICodeElementService service) {
		CodeElementServiceHolder.service = service;
	}

	public static ICodeElementService get() {
		return service;

	}
}
