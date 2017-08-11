package at.medevit.elexis.outbox.model.impl;

import java.io.ByteArrayInputStream;
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

import at.medevit.elexis.outbox.model.IOutboxElementService;
import at.medevit.elexis.outbox.model.IOutboxUpdateListener;
import at.medevit.elexis.outbox.model.OutboxElement;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Brief;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.omnivore.data.DocHandle;

public class OutboxElementService implements IOutboxElementService {
	HashSet<IOutboxUpdateListener> listeners = new HashSet<IOutboxUpdateListener>();
	
	@Override
	public void createOutboxElement(Patient patient, Kontakt mandant, String uri){
		OutboxElement element = new OutboxElement(patient, mandant, uri);
		fireUpdate(element);
		
	}
	
	@Override
	public void changeOutboxElementState(OutboxElement element, State state){
		element.set(OutboxElement.FLD_STATE, Integer.toString(state.ordinal()));
		fireUpdate(element);
	}
	
	@Override
	public List<OutboxElement> getOutboxElements(Mandant mandant, Patient patient, State state){
		Query<OutboxElement> qie = new Query<>(OutboxElement.class);
		if (mandant != null) {
			qie.add(OutboxElement.FLD_MANDANT, "=", mandant.getId());
		}
		if (patient != null) {
			qie.add(OutboxElement.FLD_PATIENT, "=", patient.getId());
		}
		if (state != null) {
			qie.add(OutboxElement.FLD_STATE, "=", Integer.toString(state.ordinal()));
		}
		return qie.execute();
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
	
	private void fireUpdate(OutboxElement element){
		synchronized (listeners) {
			for (IOutboxUpdateListener listener : listeners) {
				listener.update(element);
			}
		}
	}

	public void activate(){
		System.out.println("active providers");
		ElementsProviderExtension.activateAll();
	}
	
	public void deactivate(){
		System.out.println("deactive providers");
	}
	
	@Override
	public InputStream getContentsAsStream(OutboxElement outboxElement)
		throws IOException{
		Object object = outboxElement.getObject();
		if (object instanceof PersistentObject) {
			PersistentObject po = (PersistentObject) object;
			if (po instanceof Brief)
			{
				byte[] contents = ((Brief) po).loadBinary();
				return new ByteArrayInputStream(contents);
			} else if (po instanceof DocHandle) {
				byte[] contents = ((DocHandle) po).getContents();
				return new ByteArrayInputStream(contents);
			}
		} else if (object instanceof Path) {
			Path path = (Path) object;
			return Files.newInputStream(path);
		}
		return null;
	}
	
	@Override
	public Optional<File> getTempFileWithContents(File folder, OutboxElement outboxElement)
		throws IOException{
		Object object = outboxElement.getObject();
		File tmpDir = CoreHub.getTempDir();
		InputStream in = getContentsAsStream(outboxElement);
		if (in != null) {
			if (folder != null && folder.exists()) {
				File tmpFile = new File(folder, outboxElement.getLabel());
				try (FileOutputStream fout = new FileOutputStream(tmpFile)) {
					IOUtils.copy(in, fout);
					IOUtils.closeQuietly(in);
					return Optional.of(tmpFile);
				}
			}
		}
		return Optional.empty();
	}
}
