package at.medevit.elexis.inbox.model;

import at.medevit.elexis.inbox.model.IInboxElementService.State;
import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;

public interface IInboxElement extends Identifiable, Deleteable {
	
	public State getState();
	
	public void setState(State state);
	
	public Object getObject();
	
	public IPatient getPatient();
	
	public void setPatient(IPatient patient);
	
	public IMandator getMandator();
	
	public void setMandator(IMandator mandator);
	
	public void setObject(String storeToString);
	
	public String getUri();
}
