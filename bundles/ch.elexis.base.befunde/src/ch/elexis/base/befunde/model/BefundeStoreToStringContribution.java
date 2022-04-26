package ch.elexis.base.befunde.model;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IStoreToStringContribution;

@Component(immediate = true)
public class BefundeStoreToStringContribution implements IStoreToStringContribution {

	// wait for db connection
	@Reference(cardinality = ReferenceCardinality.MANDATORY, target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Class<?> getEntityForType(String type) {
		return ElexisTypeMap.get(type);
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		return ElexisTypeMap.getKeyForObject((EntityWithId) entityInstance);
	}

	@Override
	public String getTypeForModel(Class<?> interfaze) {
		// TODO Auto-generated method stub
		return null;
	}

}
