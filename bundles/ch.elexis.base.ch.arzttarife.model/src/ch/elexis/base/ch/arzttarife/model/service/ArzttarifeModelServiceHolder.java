package ch.elexis.base.ch.arzttarife.model.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.services.IModelService;

@Component(immediate = true)
public class ArzttarifeModelServiceHolder {

	@Reference
	PortableServiceLoader portableServiceLoader;

	public static IModelService get() {
		return PortableServiceLoader.getArzttarifeModelService();
	}
}