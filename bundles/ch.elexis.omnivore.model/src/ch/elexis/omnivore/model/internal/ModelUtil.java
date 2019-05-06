package ch.elexis.omnivore.model.internal;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.services.IModelService;
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
	
	public static <T> T loadCoreModel(EntityWithId entity, Class<T> clazz){
		return (T) modelService.load(entity.getId(), clazz).orElse(null);
	}
}
