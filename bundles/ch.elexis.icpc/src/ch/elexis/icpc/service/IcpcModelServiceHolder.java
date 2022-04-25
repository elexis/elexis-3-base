package ch.elexis.icpc.service;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.icpc.model.icpc.IcpcCode;

@Component
public class IcpcModelServiceHolder {
	private static IModelService modelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.icpc.model)")
	public void setModelService(IModelService modelService) {
		IcpcModelServiceHolder.modelService = modelService;
	}

	public static IModelService get() {
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return modelService;
	}

	public static List<IcpcCode> loadAllFromComponent(String chapter, String component, boolean order) {
		IQuery<IcpcCode> query = get().getQuery(IcpcCode.class);
		query.and("component", COMPARATOR.EQUALS, component.substring(0, 1));
		query.startGroup();
		query.and("ID", COMPARATOR.LIKE, "*%");
		query.or("ID", COMPARATOR.LIKE, chapter.substring(0, 1) + "%");
		query.andJoinGroups();
		query.orderBy("ID", order ? ORDER.ASC : ORDER.DESC);
		return query.execute();
	}
}