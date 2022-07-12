package ch.elexis.privatrechnung.model.internal;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.slf4j.LoggerFactory;

import ch.elexis.core.common.ElexisEvent;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.model.adapter.AbstractModelService;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IStoreToStringContribution;
import ch.elexis.core.services.IXidService;

@Component(property = IModelService.SERVICEMODELNAME + "=ch.elexis.privatrechnung.model")
public class PrivatRechnungModelService extends AbstractModelService
		implements IModelService, IStoreToStringContribution {

	public static final String XIDDOMAIN = "www.xid.ch/id/customservice"; //$NON-NLS-1$

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Reference
	private IXidService xidService;

	@Reference
	private EventAdmin eventAdmin;

	@Activate
	public void activate() {
		adapterFactory = PrivatRechnungModelAdapterFactory.getInstance();

		xidService.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Privatleistung", XidConstants.ASSIGNMENT_LOCAL); //$NON-NLS-1$
	}

	@Override
	public <T> IQuery<T> getQuery(Class<T> clazz, boolean refreshCache, boolean includeDeleted) {
		return new PrivatRechnungModelQuery<>(clazz, refreshCache, (EntityManager) entityManager.getEntityManager(),
				includeDeleted);
	}

	@Override
	protected EntityManager getEntityManager(boolean managed) {
		return (EntityManager) entityManager.getEntityManager(managed);
	}

	@Override
	protected void closeEntityManager(EntityManager entityManager) {
		this.entityManager.closeEntityManager(entityManager);
	}

	@Override
	protected EventAdmin getEventAdmin() {
		return eventAdmin;
	}

	@Override
	protected ElexisEvent getCreateEvent(Identifiable identifiable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		if (identifiable instanceof Leistung) {
			return Optional.of(Leistung.STS_CLASS + StringConstants.DOUBLECOLON + identifiable.getId());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString.startsWith(Leistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(ch.elexis.core.jpa.entities.PrivatLeistung.class, id);
			return Optional.ofNullable(PrivatRechnungModelAdapterFactory.getInstance()
					.getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}

	@Override
	public void clearCache() {
		entityManager.clearCache();
	}

	@Override
	public Class<?> getEntityForType(String type) {
		if (Leistung.STS_CLASS.equals(type)) {
			return ch.elexis.core.jpa.entities.PrivatLeistung.class;
		}
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		if (entityInstance instanceof ch.elexis.core.jpa.entities.PrivatLeistung) {
			return Leistung.STS_CLASS;
		}
		return null;
	}

	@Override
	public String getTypeForModel(Class<?> interfaze) {
		Class<? extends EntityWithId> entityClass = adapterFactory.getEntityClass(interfaze);
		if (entityClass != null) {
			try {
				return getTypeForEntity(entityClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting type for model [" + interfaze + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return null;
	}
}
