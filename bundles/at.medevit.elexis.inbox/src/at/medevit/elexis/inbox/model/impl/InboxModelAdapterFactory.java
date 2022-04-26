package at.medevit.elexis.inbox.model.impl;

import at.medevit.elexis.inbox.model.IInboxElement;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

public class InboxModelAdapterFactory extends AbstractModelAdapterFactory {

	private static InboxModelAdapterFactory INSTANCE;

	public static synchronized InboxModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new InboxModelAdapterFactory();
		}
		return INSTANCE;
	}

	private InboxModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(new MappingEntry(IInboxElement.class, InboxElement.class,
				ch.elexis.core.jpa.entities.InboxElement.class));
	}
}
