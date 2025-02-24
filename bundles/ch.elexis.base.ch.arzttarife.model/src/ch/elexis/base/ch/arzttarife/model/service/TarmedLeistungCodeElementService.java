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

import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedConstants;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.ICoverage;
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
public class TarmedLeistungCodeElementService implements ICodeElementServiceContribution, IStoreToStringContribution {

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Override
	public String getSystem() {
		return TarmedConstants.TarmedLeistung.CODESYSTEM_NAME;
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.SERVICE;
	}

	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		return Optional.ofNullable(TarmedLeistung.getFromCode(code, getDate(context), getLaw(context)));
	}

	private LocalDate getDate(Map<Object, Object> context) {
		Object date = context.get(ContextKeys.DATE);
		if (date instanceof LocalDate) {
			return (LocalDate) date;
		}
		IEncounter encounter = (IEncounter) context.get(ContextKeys.CONSULTATION);
		if (encounter != null) {
			return encounter.getDate();
		}
		return LocalDate.now();
	}

	private String getLaw(Map<Object, Object> context) {
		Object law = context.get(ContextKeys.LAW);
		if (law instanceof String) {
			return (String) law;
		}
		Object coverage = context.get(ContextKeys.COVERAGE);
		if (coverage instanceof ICoverage) {
			return ((ICoverage) coverage).getBillingSystem().getLaw().name();
		}
		Object consultation = context.get(ContextKeys.CONSULTATION);
		if (consultation instanceof IEncounter && ((IEncounter) consultation).getCoverage() != null) {
			return ((IEncounter) consultation).getCoverage().getBillingSystem().getLaw().name();
		}
		return null;
	}

	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		if (identifiable instanceof TarmedLeistung) {
			return Optional.of(ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS
					+ StringConstants.DOUBLECOLON + identifiable.getId());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString.startsWith(
				ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(ch.elexis.core.jpa.entities.TarmedLeistung.class, id);
			return Optional.ofNullable(
					ArzttarifeModelAdapterFactory.getInstance().getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}

	@Override
	public List<Identifiable> loadFromStringWithIdPart(String partialStoreToString) {
		if (!partialStoreToString.startsWith(
				ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			return Collections.emptyList();
		}

		String[] split = splitIntoTypeAndId(partialStoreToString);
		if (split != null && split.length == 2) {
			String id = split[1];
			Class<? extends EntityWithId> clazz = ch.elexis.core.jpa.entities.TarmedLeistung.class;
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
		if (ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS.equals(type)) {
			return ch.elexis.core.jpa.entities.TarmedLeistung.class;
		}
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		if (entityInstance instanceof ch.elexis.core.jpa.entities.TarmedLeistung) {
			return ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS;
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
