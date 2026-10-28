package ch.elexis.base.ch.arzttarife.model.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.os25.IOs25Leistung;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.Os25Leistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IStoreToStringContribution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Component
public class Os25LeistungCodeElementService implements ICodeElementServiceContribution, IStoreToStringContribution {

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Override
	public String getSystem() {
		return Os25Leistung.CODESYSTEM_NAME;
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.SERVICE;
	}

	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		TypedQuery<Os25Leistung> query = em.createNamedQuery("Os25Leistung.code", Os25Leistung.class);
		query.setParameter("code", code);
		List<Os25Leistung> resultList = query.getResultList().stream().filter(pl -> isValid(pl, context))
				.collect(Collectors.toList());
		if (!resultList.isEmpty()) {
			Optional<Identifiable> element = ArzttarifeModelAdapterFactory.getInstance()
					.getModelAdapter(resultList.get(0), IOs25Leistung.class, false);
			if (element.isPresent()) {
				return Optional.of((ICodeElement) element.get());
			}
		}
		return Optional.empty();
	}

	private boolean isValid(Os25Leistung ps25, Map<Object, Object> context) {
		LocalDate validDate = getDate(context);
		if (ps25.getValidFrom() != null && validDate.isBefore(ps25.getValidFrom())) {
			return false;
		}
		if (ps25.getValidUntil() != null && validDate.isAfter(ps25.getValidUntil())) {
			return false;
		}
		return true;
	}

	private LocalDate getDate(Map<Object, Object> context) {
		if (context != null) {
			Object date = context.get(ContextKeys.DATE);
			if (date instanceof LocalDate) {
				return (LocalDate) date;
			}
			IEncounter encounter = (IEncounter) context.get(ContextKeys.CONSULTATION);
			if (encounter != null) {
				return encounter.getDate();
			}
		}
		return LocalDate.now();
	}

	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		List<Os25Leistung> found = em
				.createQuery("SELECT entity FROM Os25Leistung entity WHERE entity.deleted = false", Os25Leistung.class)
				.getResultList().stream().filter(pl -> isValid(pl, context)).collect(Collectors.toList());
		ArzttarifeModelAdapterFactory adapterFactory = ArzttarifeModelAdapterFactory.getInstance();
		return found.stream().map(e -> adapterFactory.getModelAdapter(e, IOs25Leistung.class, false).orElse(null))
				.filter(e -> e instanceof ICodeElement).map(e -> (ICodeElement) e).collect(Collectors.toList());
	}

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		if (identifiable instanceof ch.elexis.base.ch.arzttarife.os25.model.Os25Leistung) {
			return Optional.of(ch.elexis.base.ch.arzttarife.os25.model.Os25Leistung.STS_CLASS
					+ StringConstants.DOUBLECOLON + identifiable.getId());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString.startsWith(
				ch.elexis.base.ch.arzttarife.os25.model.Os25Leistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(Os25Leistung.class, id);
			return Optional.ofNullable(
					ArzttarifeModelAdapterFactory.getInstance().getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}

	@Override
	public List<Identifiable> loadFromStringWithIdPart(String partialStoreToString) {
		if (!partialStoreToString.startsWith(
				ch.elexis.base.ch.arzttarife.os25.model.Os25Leistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			return Collections.emptyList();
		}

		String[] split = splitIntoTypeAndId(partialStoreToString);
		if (split != null && split.length == 2) {
			String id = split[1];
			Class<? extends EntityWithId> clazz = ch.elexis.core.jpa.entities.Os25Leistung.class;
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			TypedQuery<? extends EntityWithId> query = em.createQuery(
					"SELECT entity FROM " + clazz.getSimpleName() + " entity WHERE entity.id LIKE :idpart", clazz);
			query.setParameter("idpart", id + "%");
			List<? extends EntityWithId> found = query.getResultList();
			if (!found.isEmpty()) {
				ArzttarifeModelAdapterFactory adapterFactory = ArzttarifeModelAdapterFactory.getInstance();
				return found.parallelStream().map(e -> adapterFactory.getModelAdapter(e, null, false).orElse(null))
						.collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}

	@Override
	public Class<?> getEntityForType(String type) {
		if (ch.elexis.base.ch.arzttarife.os25.model.Os25Leistung.STS_CLASS.equals(type)) {
			return ch.elexis.core.jpa.entities.Os25Leistung.class;
		}
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		if (entityInstance instanceof ch.elexis.core.jpa.entities.Os25Leistung) {
			return ch.elexis.base.ch.arzttarife.os25.model.Os25Leistung.STS_CLASS;
		}
		return null;
	}

	@Override
	public String getTypeForModel(Class<?> interfaze) {
		Class<? extends EntityWithId> entityClass = ArzttarifeModelAdapterFactory.getInstance()
				.getEntityClass(interfaze);
		if (entityClass != null) {
			try {
				return getTypeForEntity(entityClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting type for model [" + interfaze + "]", e);
			}
		}
		return null;
	}
}
