package at.medevit.elexis.inbox.model.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.model.InboxElementType;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.jpa.entities.Kontakt;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;

public class InboxElement
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.InboxElement>
		implements IInboxElement {
	
	private Identifiable object;
	
	public InboxElement(ch.elexis.core.jpa.entities.InboxElement entity){
		super(entity);
	}
	
	@Override
	public State getState(){
		String stateStr = getEntity().getState();
		if (StringUtils.isEmpty(stateStr)) {
			return State.NEW;
		} else {
			return State.values()[Integer.parseInt(stateStr.trim())];
		}
	}
	
	@Override
	public void setState(State state){
		getEntityMarkDirty().setState(Integer.toString(state.ordinal()));
	}
	
	@Override
	public Object getObject(){
		String uri = getEntity().getObject();
		InboxElementType outboxElementType = InboxElementType.parseType(uri);
		if (outboxElementType != null) {
			switch (outboxElementType) {
			case DB:
				if (object == null && StringUtils.isNotBlank(uri)) {
					object = StoreToStringServiceHolder.get().loadFromString(uri).orElse(null);
				}
				if (object != null) {
					return object;
				}
				break;
			case FILE:
				String refFile = uri.substring(InboxElementType.FILE.getPrefix().length());
				Path p = Paths.get(refFile);
				return p;
			default:
				break;
			}
		}
		return null;
	}
	
	@Override
	public IPatient getPatient(){
		return ModelUtil.loadCoreModel(getEntity().getPatient(), IPatient.class);
	}
	
	@Override
	public void setPatient(IPatient patient){
		if (patient instanceof AbstractIdDeleteModelAdapter) {
			getEntityMarkDirty()
				.setPatient((Kontakt) ((AbstractIdDeleteModelAdapter<?>) patient).getEntity());
		} else if (patient == null) {
			getEntityMarkDirty().setPatient(null);
		}
	}
	
	@Override
	public IMandator getMandator(){
		return ModelUtil.loadCoreModel(getEntity().getMandant(), IMandator.class);
		
	}
	
	@Override
	public void setMandator(IMandator mandator){
		if (mandator instanceof AbstractIdDeleteModelAdapter) {
			getEntityMarkDirty()
				.setMandant((Kontakt) ((AbstractIdDeleteModelAdapter<?>) mandator).getEntity());
		} else if (mandator == null) {
			getEntityMarkDirty().setMandant(null);
		}
	}
	
	@Override
	public void setObject(String storeToString){
		object = null;
		getEntityMarkDirty().setObject(storeToString);
	}
	
	@Override
	public String getUri(){
		return getEntity().getObject();
	}
	
	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists){
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public IXid getXid(String domain){
		// TODO Auto-generated method stub
		return null;
	}
}
