package at.medevit.elexis.outbox.model.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService;
import at.medevit.elexis.outbox.model.IOutboxUpdateListener;
import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;

@Component
public class OutboxElementService implements IOutboxElementService {
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=at.medevit.elexis.outbox.model)")
	private IModelService modelService;
	
	HashSet<IOutboxUpdateListener> listeners = new HashSet<IOutboxUpdateListener>();
	
	@Override
	public IOutboxElement createOutboxElement(IPatient patient, IMandator mandator, String uri){
		//		OutboxElement element = new OutboxElement(patient, mandant, uri);
		IOutboxElement element = modelService.create(IOutboxElement.class);
		element.setPatient(patient);
		element.setMandator(mandator);
		element.setUri(uri);
		element.setState(State.NEW);
		modelService.save(element);
		fireUpdate(element);
		return element;
	}
	
	@Override
	public void changeOutboxElementState(IOutboxElement element, State state){
		element.setState(state);
		modelService.save(element);
		fireUpdate(element);
	}
	
	@Override
	public List<IOutboxElement> getOutboxElements(String uri, State state){
		IQuery<IOutboxElement> query = modelService.getQuery(IOutboxElement.class);
		if (uri != null) {
			query.and("uri", COMPARATOR.EQUALS, uri);
		}
		if (state != null) {
			query.and("state", COMPARATOR.EQUALS, Integer.toString(state.ordinal()));
		}
		return query.execute();
	}
	
	@Override
	public List<IOutboxElement> getOutboxElements(IMandator mandator, IPatient patient,
		State state){
		IQuery<IOutboxElement> query = modelService.getQuery(IOutboxElement.class);
		if (mandator != null) {
			query.and("mandant", COMPARATOR.EQUALS, mandator);
		}
		if (patient != null) {
			query.and("patient", COMPARATOR.EQUALS, patient);
		}
		if (state != null) {
			query.and("state", COMPARATOR.EQUALS, Integer.toString(state.ordinal()));
		}
		return query.execute();
	}
	
	@Override
	public void addUpdateListener(IOutboxUpdateListener listener){
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	@Override
	public void removeUpdateListener(IOutboxUpdateListener listener){
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void fireUpdate(IOutboxElement element){
		synchronized (listeners) {
			for (IOutboxUpdateListener listener : listeners) {
				listener.update(element);
			}
		}
	}

	@Activate
	public void activate(){
		System.out.println("active providers");
		ElementsProviderExtension.activateAll();
	}
	
	@Deactivate
	public void deactivate(){
		System.out.println("deactive providers");
	}
	
	@Override
	public InputStream getContentsAsStream(IOutboxElement outboxElement)
		throws IOException{
		Object object = outboxElement.getObject();
		if (object instanceof Path) {
			Path path = (Path) object;
			return Files.newInputStream(path);
		}
		else if (object instanceof IDocument) {
			Optional<InputStream> in =
				DocumentStoreServiceHolder.getService().loadContent((IDocument) object);
			if (in.isPresent()) {
				return in.get();
			}
		} else if (object instanceof Identifiable) {
			throw new UnsupportedOperationException("Identifiable to InputStream");
		}
		return null;
	}
	
	@Override
	public Optional<File> createTempFileWithContents(File folder, IOutboxElement outboxElement)
		throws IOException{
		try (InputStream in = getContentsAsStream(outboxElement)) {
			if (in != null) {
				if (folder != null && folder.exists()) {
					File tmpFile = new File(folder, outboxElement.getLabel());
					try (FileOutputStream fout = new FileOutputStream(tmpFile)) {
						IOUtils.copy(in, fout);
						tmpFile.deleteOnExit();
						return Optional.of(tmpFile);
					}
				}
			}
		}
		return Optional.empty();
	}
	
	@Override
	public void deleteOutboxElement(IOutboxElement outboxElement){
		if (outboxElement != null) {
			modelService.delete(outboxElement);
			fireUpdate(outboxElement);
		}
	}
}
