package ch.elexis.base.ch.arzttarife.model.service;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IContextService;

@Component
public class ContextServiceHolder {
	private static IContextService contextService;
	
	@Reference
	public void setModelService(IContextService contextService){
		ContextServiceHolder.contextService = contextService;
	}
	
	public static Optional<IContextService> get(){
		return Optional.ofNullable(contextService);
	}
}
