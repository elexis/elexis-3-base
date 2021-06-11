package at.medevit.elexis.outbox.model.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService.State;
import at.medevit.elexis.outbox.model.OutboxElementType;
import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.jpa.entities.Kontakt;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

public class OutboxElement
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.OutboxElement>
		implements IOutboxElement {
	
	public OutboxElement(ch.elexis.core.jpa.entities.OutboxElement entity){
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
	public void setUri(String uri){
		getEntityMarkDirty().setUri(uri);
	}
	
	@Override
	public String getUri(){
		return getEntity().getUri();
	}
	
	@Override
	public Object getObject(){
		String uri = getEntity().getUri();
		OutboxElementType outboxElementType = OutboxElementType.parseType(uri);
		if (outboxElementType != null) {
			switch (outboxElementType) {
			case DB:
				Optional<Identifiable> loaded =
					StoreToStringServiceHolder.get().loadFromString(uri);
				if (loaded.isPresent()) {
					return loaded.get();
				}
				break;
			case FILE:
				String refFile = uri.substring(OutboxElementType.FILE.getPrefix().length());
				Path p = Paths.get(refFile);
				return p;
			case DOC:
				String refDoc = uri.substring(OutboxElementType.DOC.getPrefix().length());
				String[] splits = refDoc.split(DocumentStore.ID_WITH_STOREID_SPLIT);
				if (splits.length == 2) {
					Optional<IDocument> doc =
						DocumentStoreServiceHolder.getService().loadDocument(splits[0], splits[1]);
					if (doc.isPresent()) {
						return doc.get();
					}
				}
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
	public void setPatient(IPatient value){
		if (value instanceof AbstractIdDeleteModelAdapter) {
			getEntityMarkDirty()
				.setPatient((Kontakt) ((AbstractIdDeleteModelAdapter<?>) value).getEntity());
		} else if (value == null) {
			getEntityMarkDirty().setPatient(null);
		}
	}
	
	@Override
	public IMandator getMandator(){
		return ModelUtil.loadCoreModel(getEntity().getMandant(), IMandator.class);
	}
	
	@Override
	public void setMandator(IMandator value){
		if (value instanceof AbstractIdDeleteModelAdapter) {
			getEntityMarkDirty()
				.setMandant((Kontakt) ((AbstractIdDeleteModelAdapter<?>) value).getEntity());
		} else if (value == null) {
			getEntityMarkDirty().setMandant(null);
		}
	}
	
	@Override
	public String getLabel(){
		Object element = getObject();
		if (element instanceof IDocument) {
			String lbl = ((IDocument) element).getLabel();
			String ext = ((IDocument) element).getExtension();
			return lbl.endsWith(ext) ? lbl : lbl + "." + ext.toLowerCase();
		} else if (element instanceof Identifiable) {
			return ((Identifiable) element).getLabel();
		} else if (element instanceof Path) {
			return ((Path) element).getFileName().toString();
		}
		return "OutboxElement " + this.getId() + " with no object.";
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
