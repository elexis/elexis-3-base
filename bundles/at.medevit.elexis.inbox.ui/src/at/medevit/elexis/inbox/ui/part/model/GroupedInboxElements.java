package at.medevit.elexis.inbox.ui.part.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.InboxModelServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;

public abstract class GroupedInboxElements implements IInboxElement {

	protected HashSet<IInboxElement> inboxElements;

	public GroupedInboxElements() {
		this.inboxElements = new HashSet<>();
	}

	public void addElement(IInboxElement element) {
		synchronized (inboxElements) {
			inboxElements.add(element);
		}
	}

	public List<IInboxElement> getElements() {
		synchronized (inboxElements) {
			return new ArrayList<IInboxElement>(inboxElements);
		}
	}

	public boolean isEmpty() {
		synchronized (inboxElements) {
			return inboxElements.isEmpty();
		}
	}

	public IInboxElement getFirstElement() {
		synchronized (inboxElements) {
			return inboxElements.isEmpty() ? null : getElements().get(0);
		}
	}

	@Override
	public State getState() {
		return isEmpty() ? State.NEW : getFirstElement().getState();
	}

	@Override
	public void setState(State state) {
		getElements().forEach(iie -> iie.setState(state));
		InboxModelServiceHolder.get().save(getElements());
	}

	@Override
	public Object getObject() {
		return getElements();
	}

	@Override
	public IPatient getPatient() {
		return isEmpty() ? null : getFirstElement().getPatient();
	}

	@Override
	public void setPatient(IPatient patient) {
		getElements().forEach(iie -> iie.setPatient(patient));
	}

	@Override
	public IMandator getMandator() {
		return isEmpty() ? null : getFirstElement().getMandator();
	}

	@Override
	public void setMandator(IMandator mandator) {
		getElements().forEach(iie -> iie.setMandator(mandator));
	}

	@Override
	public void setObject(String storeToString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IXid getXid(String domain) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long getLastupdate() {
		if (getFirstElement() != null) {
			return getFirstElement().getLastupdate();
		}
		return Long.MAX_VALUE;
	}

	@Override
	public boolean isDeleted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDeleted(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		throw new UnsupportedOperationException();
	}
}
