package ch.elexis.omnivore.data.model.util;

import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.MimeTool;

@Component
public class ModelUtil {
	
	private static IModelService modelService;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelService(IModelService modelService){
		ModelUtil.modelService = modelService;
	}
	
	/**
	 * Get the file extension part of the input String.
	 * 
	 * @param input
	 * @return
	 */
	public static String evaluateFileExtension(String input){
		String ext = MimeTool.getExtension(input);
		if (StringUtils.isEmpty(ext)) {
			ext = FilenameUtils.getExtension(input);
			if (StringUtils.isEmpty(ext)) {
				ext = input;
			}
		}
		return ext;
	}
	
	/**
	 * Add an {@link IXid} to the {@link Identifiable}.
	 * 
	 * @param identifiable
	 * @param domain
	 * @param id
	 * @param updateIfExists
	 * @return
	 */
	public static boolean addXid(Identifiable identifiable, String domain, String id,
		boolean updateIfExists){
		Optional<IXid> existing = getXid(domain, id);
		if (existing.isPresent()) {
			if (updateIfExists) {
				IXid xid = existing.get();
				xid.setDomain(domain);
				xid.setDomainId(id);
				xid.setObject(identifiable);
				return true;
			}
		} else {
			IXid xid = modelService.create(IXid.class);
			xid.setDomain(domain);
			xid.setDomainId(id);
			xid.setObject(identifiable);
			return true;
		}
		return false;
	}
	
	/**
	 * Get an {@link IXid} with matching domain and id.
	 * 
	 * @param domain
	 * @param id
	 * @return
	 */
	public static Optional<IXid> getXid(String domain, String id){
		IQuery<IXid> query = modelService.getQuery(IXid.class);
		query.and(ModelPackage.Literals.IXID__DOMAIN, COMPARATOR.EQUALS, domain);
		query.and(ModelPackage.Literals.IXID__DOMAIN_ID, COMPARATOR.EQUALS, id);
		List<IXid> xids = query.execute();
		if (xids.size() > 0) {
			if (xids.size() > 1) {
				LoggerFactory.getLogger(ModelUtil.class).error(
					"XID [" + domain + "] [" + id + "] on multiple objects, returning first.");
			}
			return Optional.of(xids.get(0));
		}
		return Optional.empty();
	}
	
	/**
	 * Get an {@link IXid} with matching {@link Identifiable} and domain.
	 * 
	 * @param identifiable
	 * @param domain
	 * @return
	 */
	public static Optional<IXid> getXid(Identifiable identifiable, String domain){
		IQuery<IXid> query = modelService.getQuery(IXid.class);
		query.and(ModelPackage.Literals.IXID__DOMAIN, COMPARATOR.EQUALS, domain);
		query.and(ModelPackage.Literals.IXID__OBJECT_ID, COMPARATOR.EQUALS, identifiable.getId());
		List<IXid> xids = query.execute();
		if (xids.size() > 0) {
			if (xids.size() > 1) {
				LoggerFactory.getLogger(ModelUtil.class).error("XID [" + domain + "] ["
					+ identifiable + "] on multiple objects, returning first.");
			}
			return Optional.of(xids.get(0));
		}
		return Optional.empty();
	}
	
	public static <T> T loadCoreModel(EntityWithId entity, Class<T> clazz){
		return (T) modelService.load(entity.getId(), clazz).orElse(null);
	}
}
