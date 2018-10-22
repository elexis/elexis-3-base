package ch.elexis.base.ch.arzttarife.model.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IElexisEntityManager;

@Component
public class EntityManagerHolder {
	private static IElexisEntityManager entityManager;
	
	@Reference
	public void setModelService(IElexisEntityManager entityManager){
		EntityManagerHolder.entityManager = entityManager;
	}
	
	public static IElexisEntityManager get(){
		if (entityManager == null) {
			throw new IllegalStateException("No IElexisEntityManager available");
		}
		return entityManager;
	}
}
