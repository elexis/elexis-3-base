package ch.elexis.omnivore.data.service.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.ILocalDocumentService.ILoadHandler;
import ch.elexis.core.services.ILocalDocumentService.ISaveHandler;
import ch.elexis.omnivore.model.IDocumentHandle;

/**
 * Service component for {@link LocalDocumentService} access. <br/>
 * <br/>
 * Component annotation on views leads to startup problems. The reason is that on DS start a display
 * is created in the context of the resolving Thread.
 * 
 * @author thomas
 *
 */
@Component(service = {})
public class LocalDocumentServiceHolder {
	private static Optional<ILocalDocumentService> localDocumentService;
	
	@Reference
	public void bind(ILocalDocumentService service){
		LocalDocumentServiceHolder.localDocumentService = Optional.ofNullable(service);
		
		service.registerSaveHandler(IDocumentHandle.class, new ISaveHandler() {
			@Override
			public boolean save(Object documentSource, ILocalDocumentService service){
				IDocumentHandle docHandle = (IDocumentHandle) documentSource;
				Optional<InputStream> content = service.getContent(docHandle);
				if (content.isPresent()) {
					try {
						docHandle.setContent(content.get());
						return true;
					} finally {
						try {
							content.get().close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
				return false;
			}
		});
		
		service.registerLoadHandler(IDocumentHandle.class, new ILoadHandler() {
			@Override
			public InputStream load(Object documentSource){
				IDocumentHandle docHandle = (IDocumentHandle) documentSource;
				return docHandle.getContent();
			}
		});
	}
	
	public void unbind(ILocalDocumentService service){
		LocalDocumentServiceHolder.localDocumentService = Optional.empty();
	}
	
	public static Optional<ILocalDocumentService> getService(){
		return localDocumentService;
	}
}
