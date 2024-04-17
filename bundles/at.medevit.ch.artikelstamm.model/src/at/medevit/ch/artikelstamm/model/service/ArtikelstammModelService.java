package at.medevit.ch.artikelstamm.model.service;

import static at.medevit.ch.artikelstamm.ArtikelstammConstants.CODESYSTEM_NAME;
import static at.medevit.ch.artikelstamm.ArtikelstammConstants.STS_CLASS;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.ArtikelstammConstants.ContextKeys;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.common.ElexisEvent;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.ArtikelstammItem;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.model.adapter.AbstractIdModelAdapter;
import ch.elexis.core.jpa.model.adapter.AbstractModelService;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IStoreToStringContribution;

@Component(property = IModelService.SERVICEMODELNAME + "=at.medevit.ch.artikelstamm.model")
public class ArtikelstammModelService extends AbstractModelService
		implements IModelService, IStoreToStringContribution, ICodeElementServiceContribution {

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Reference
	private EventAdmin eventAdmin;

	@Activate
	public void activate() {
		adapterFactory = ArtikelstammModelAdapterFactory.getInstance();
	}

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		if (identifiable instanceof IArtikelstammItem) {
			return Optional.of(STS_CLASS + StringConstants.DOUBLECOLON + identifiable.getId());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString == null) {
			LoggerFactory.getLogger(getClass()).warn("StoreToString is null"); //$NON-NLS-1$
			return Optional.empty();
		}

		if (storeToString.startsWith(STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(ArtikelstammItem.class, id);
			return Optional.ofNullable(adapterFactory.getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}

	@Override
	public List<Identifiable> loadFromStringWithIdPart(String partialStoreToString) {
		if (partialStoreToString == null) {
			LoggerFactory.getLogger(getClass()).warn("StoreToString is null"); //$NON-NLS-1$
			return Collections.emptyList();
		}

		if (partialStoreToString.startsWith(STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(partialStoreToString);
			String id = split[1];
			Class<? extends EntityWithId> clazz = ArtikelstammItem.class;
			if (clazz != null) {
				EntityManager em = (EntityManager) entityManager.getEntityManager();
				TypedQuery<? extends EntityWithId> query = em.createQuery(
						"SELECT entity FROM " + clazz.getSimpleName() + " entity WHERE entity.id LIKE :idpart", clazz); //$NON-NLS-1$ //$NON-NLS-2$
				query.setParameter("idpart", id + "%"); //$NON-NLS-1$ //$NON-NLS-2$
				List<? extends EntityWithId> found = query.getResultList();
				if (!found.isEmpty()) {
					return found.parallelStream().map(e -> adapterFactory.getModelAdapter(e, null, false).orElse(null))
							.collect(Collectors.toList());
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public <T> IQuery<T> getQuery(Class<T> clazz, boolean refreshCache, boolean includeDeleted) {
		return new ArtikelstammQuery<>(clazz, refreshCache, (EntityManager) entityManager.getEntityManager(),
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
		if (identifiable instanceof AbstractIdModelAdapter<?>) {
			return ElexisEvent.of(ElexisEventTopics.PERSISTENCE_EVENT_CREATE, identifiable.getId(), STS_CLASS);
		}
		return null;
	}

	@Override
	public String getSystem() {
		return CODESYSTEM_NAME;
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.ARTICLE;
	}

	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		boolean includeBlackBoxed = getIncludeBlackBoxed(context);
		INamedQuery<IArtikelstammItem> query = getNamedQuery(IArtikelstammItem.class, "gtin"); //$NON-NLS-1$
		List<IArtikelstammItem> found = query.executeWithParameters(query.getParameterMap("gtin", code)); //$NON-NLS-1$
		if (!includeBlackBoxed) {
			found = found.stream().filter(ai -> !ai.isBlackBoxed()).collect(Collectors.toList());
		}
		if (found.size() > 0) {
			if (found.size() > 1) {
				LoggerFactory.getLogger(getClass()).warn("Found more than 1 code element for gtin [" + code + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return Optional.of(found.get(0));
		} else {
			// try pharma code for compatibility
			IQuery<IArtikelstammItem> pharmaQuery = getQuery(IArtikelstammItem.class);
			pharmaQuery.and("phar", COMPARATOR.EQUALS, code); //$NON-NLS-1$
			found = pharmaQuery.execute();
			if (found.size() > 0) {
				if (found.size() > 1) {
					LoggerFactory.getLogger(getClass()).warn("Found more than 1 code element for phar [" + code + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				return Optional.of(found.get(0));
			}
		}

		if (context.containsKey("CONSIDER_PRODNO")) { //$NON-NLS-1$
			IQuery<IArtikelstammItem> prodnoQuery = getQuery(IArtikelstammItem.class);
			prodnoQuery.and("id", COMPARATOR.EQUALS, code); //$NON-NLS-1$
			found = prodnoQuery.execute();
			if (found.size() > 0) {
				if (found.size() > 1) {
					LoggerFactory.getLogger(getClass())
							.warn("Found more than 1 code element for prodno [" + code + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				return Optional.of(found.get(0));
			}
		}

		return Optional.empty();
	}

	private boolean getIncludeBlackBoxed(Map<Object, Object> context) {
		if (context != null) {
			return context != null && context.get(ContextKeys.INCLUDE_BB) != null
					&& context.get(ContextKeys.INCLUDE_BB) == Boolean.TRUE;
		}
		return false;
	}

	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearCache() {
		entityManager.clearCache();
	}

	@Override
	public Class<?> getEntityForType(String type) {
		if (STS_CLASS.equals(type)) {
			return ArtikelstammItem.class;
		}
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		if (entityInstance instanceof ArtikelstammItem) {
			return STS_CLASS;
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

	@Override
	protected IModelService getCoreModelService() {
		return null;
	}
}
