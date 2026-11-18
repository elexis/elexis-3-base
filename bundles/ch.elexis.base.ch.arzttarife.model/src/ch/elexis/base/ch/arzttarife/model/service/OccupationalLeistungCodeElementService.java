package ch.elexis.base.ch.arzttarife.model.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.OccupationalLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IStoreToStringContribution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Component
public class OccupationalLeistungCodeElementService
		implements ICodeElementServiceContribution, IStoreToStringContribution {

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Override
	public String getSystem() {
		return OccupationalLeistung.CODESYSTEM_NAME;
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.SERVICE;
	}

	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		TypedQuery<OccupationalLeistung> gtinQuery = em.createNamedQuery("OccupationalLeistung.code",
				OccupationalLeistung.class);
		gtinQuery.setParameter("code", code);
		List<OccupationalLeistung> resultList = gtinQuery.getResultList();
		if (resultList.size() > 0) {
			Optional<Identifiable> element = ArzttarifeModelAdapterFactory.getInstance()
					.getModelAdapter(resultList.get(0), IOccupationalLeistung.class, false);
			if (element.isPresent()) {
				return Optional.of((ICodeElement) element.get());
			}
		}
		return Optional.empty();
	}

	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		if (identifiable instanceof ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung) {
			return Optional.of(ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung.STS_CLASS
					+ StringConstants.DOUBLECOLON
					+ ((ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung) identifiable).getId());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString.startsWith(ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung.STS_CLASS
				+ StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(OccupationalLeistung.class, id);
			return Optional.ofNullable(
					ArzttarifeModelAdapterFactory.getInstance().getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}

	@Override
	public List<Identifiable> loadFromStringWithIdPart(String partialStoreToString) {
		if (!partialStoreToString
				.startsWith(ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung.STS_CLASS
						+ StringConstants.DOUBLECOLON)) {
			return Collections.emptyList();
		}

		String[] split = splitIntoTypeAndId(partialStoreToString);
		if (split != null && split.length == 2) {
			String id = split[1];
			Class<? extends EntityWithId> clazz = ch.elexis.core.jpa.entities.OccupationalLeistung.class;
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
		if (ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung.STS_CLASS.equals(type)) {
			return ch.elexis.core.jpa.entities.OccupationalLeistung.class;
		}
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		if (entityInstance instanceof ch.elexis.core.jpa.entities.OccupationalLeistung) {
			return ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung.STS_CLASS;
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
