package ch.elexis.base.ch.arzttarife.model.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.PhysioLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IStoreToStringContribution;

@Component
public class PhysioLeistungCodeElementService implements ICodeElementServiceContribution, IStoreToStringContribution {

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Override
	public String getSystem() {
		return PhysioLeistung.CODESYSTEM_NAME;
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.SERVICE;
	}

	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		String law = getLaw(context);
		LocalDate date = getDate(context);

		IQuery<IPhysioLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
		query.and("ziffer", COMPARATOR.EQUALS, code);

		if (law != null) {
			if (!ArzttarifeUtil.isAvailableLaw(law)) {
				query.startGroup();
				query.or("law", COMPARATOR.EQUALS, StringUtils.EMPTY);
				query.or("law", COMPARATOR.EQUALS, null);
				query.andJoinGroups();
			} else {
				query.and("law", COMPARATOR.EQUALS, law, true);
			}
		}
		List<IPhysioLeistung> leistungen = query.execute();
		for (IPhysioLeistung leistung : leistungen) {
			if ((date.isAfter(leistung.getValidFrom()) || date.equals(leistung.getValidFrom()))) {
				if (leistung.getValidTo() != null) {
					if (date.isBefore(leistung.getValidTo()) || date.equals(leistung.getValidTo())) {
						return Optional.of(leistung);
					}
				} else {
					return Optional.of(leistung);
				}
			}
		}
		return Optional.empty();
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

	private String getLaw(Map<Object, Object> context) {
		if (context != null) {
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
		if (identifiable instanceof ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung) {
			return Optional
					.of(ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.STS_CLASS + StringConstants.DOUBLECOLON
							+ ((ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung) identifiable).getId());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString.startsWith(
				ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(PhysioLeistung.class, id);
			return Optional.ofNullable(
					ArzttarifeModelAdapterFactory.getInstance().getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}

	@Override
	public List<Identifiable> loadFromStringWithIdPart(String partialStoreToString) {
		if (!partialStoreToString.startsWith(
				ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			return Collections.emptyList();
		}

		String[] split = splitIntoTypeAndId(partialStoreToString);
		if (split != null && split.length == 2) {
			String id = split[1];
			Class<? extends EntityWithId> clazz = ch.elexis.core.jpa.entities.PhysioLeistung.class;
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
		if (ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.STS_CLASS.equals(type)) {
			return ch.elexis.core.jpa.entities.PhysioLeistung.class;
		}
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		if (entityInstance instanceof ch.elexis.core.jpa.entities.PhysioLeistung) {
			return ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.STS_CLASS;
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
