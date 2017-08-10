package at.medevit.elexis.outbox.model.impl;

import java.util.HashSet;
import java.util.List;

import at.medevit.elexis.outbox.model.IOutboxElementService;
import at.medevit.elexis.outbox.model.IOutboxUpdateListener;
import at.medevit.elexis.outbox.model.OutboxElement;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

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
	
}
