package ch.elexis.omnivore.data;

import java.io.ByteArrayInputStream;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.data.Patient;
import ch.elexis.omnivore.model.IDocumentHandle;

@RunWith(Suite.class)
@SuiteClasses({
	Test_Utils.class
})
public class AllOmnivoreDataTests {
	
	private static IModelService omnivoreModelService;
	
	public static IDocumentHandle createDocumentHandle(String category, byte[] doc, Patient pat,
		String title, String mime, String keyw){
		IDocumentHandle ret = getOmnivoreModelService().create(IDocumentHandle.class);
		getOmnivoreModelService().setEntityProperty("category", category, ret);
		ret.setPatient(CoreModelServiceHolder.get().load(pat.getId(), IPatient.class).orElse(null));
		ret.setTitle(title);
		ret.setMimeType(mime);
		ret.setKeywords(keyw);
		ret.setContent(new ByteArrayInputStream(doc));
		getOmnivoreModelService().save(ret);
		return ret;
	}
	
	public static IModelService getOmnivoreModelService(){
		if (omnivoreModelService == null) {
			omnivoreModelService = OsgiServiceUtil.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
				.orElseThrow(
					() -> new IllegalStateException("No omnivore model service available"));
		}
		return omnivoreModelService;
	}
	
}