package ch.elexis.omnivore.data.service.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.ILocalDocumentService.ILoadHandler;
import ch.elexis.core.services.ILocalDocumentService.ISaveHandler;
import ch.elexis.omnivore.data.DocHandle;

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
		
		service.registerSaveHandler(DocHandle.class, new ISaveHandler() {
			@Override
			public boolean save(Object documentSource, ILocalDocumentService service){
				DocHandle docHandle = (DocHandle) documentSource;
				Optional<InputStream> content = service.getContent(docHandle);
				if (content.isPresent()) {
					try {
						docHandle.storeContent(IOUtils.toByteArray(content.get()));
						return true;
					} catch (IOException | ElexisException e) {
						LoggerFactory.getLogger(getClass()).error("Error saving document", e);
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
		
		service.registerLoadHandler(DocHandle.class, new ILoadHandler() {
			@Override
			public InputStream load(Object documentSource){
				DocHandle docHandle = (DocHandle) documentSource;
				try {
					return new ByteArrayInputStream(docHandle.getContentsAsBytes());
				} catch (ElexisException e) {
					LoggerFactory.getLogger(getClass()).error("Error loading document", e);
				}
				return null;
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
