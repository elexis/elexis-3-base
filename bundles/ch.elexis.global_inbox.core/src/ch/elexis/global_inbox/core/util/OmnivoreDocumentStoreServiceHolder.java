package ch.elexis.global_inbox.core.util;

import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.services.IDocumentStore;

public class OmnivoreDocumentStoreServiceHolder {

	public static IDocumentStore get() {
		return PortableServiceLoader.getService(IDocumentStore.class, "(storeid=ch.elexis.data.store.omnivore)")
				.orElseThrow(() -> new IllegalStateException("No IDocumentStore available"));
	}

	public static boolean isAvailable() {
		return PortableServiceLoader.getService(IDocumentStore.class, "(storeid=ch.elexis.data.store.omnivore)")
				.isPresent();
	}
}
