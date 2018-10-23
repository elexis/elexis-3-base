package ch.elexis.base.ch.arzttarife.model.service;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IConfigService;

@Component
public class ConfigServiceHolder {
	private static IConfigService configService;
	
	@Reference
	public void setModelService(IConfigService configService){
		ConfigServiceHolder.configService = configService;
	}
	
	public static Optional<IConfigService> get(){
		return Optional.ofNullable(configService);
	}
}
