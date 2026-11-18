package ch.elexis.base.ch.arzttarife.model.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.services.IElexisEntityManager;

@Component(immediate = true)
public class EntityManagerHolder {

	@Reference
	private IElexisEntityManager elexisEntityManager;

	public static IElexisEntityManager get() {
		return PortableServiceLoader.get(IElexisEntityManager.class);
	}
}
