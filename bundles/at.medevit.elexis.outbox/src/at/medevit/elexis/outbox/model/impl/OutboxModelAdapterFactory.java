package at.medevit.elexis.outbox.model.impl;

import at.medevit.elexis.outbox.model.IOutboxElement;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

public class OutboxModelAdapterFactory extends AbstractModelAdapterFactory {

	private static OutboxModelAdapterFactory INSTANCE;

	public static synchronized OutboxModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OutboxModelAdapterFactory();
		}
		return INSTANCE;
	}

	private OutboxModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(new MappingEntry(IOutboxElement.class, OutboxElement.class,
				ch.elexis.core.jpa.entities.OutboxElement.class));
	}
}
