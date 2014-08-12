package at.medevit.elexis.inbox.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElementsProvider;

public class ElementsProviderExtension {
	
	private static Logger logger = LoggerFactory.getLogger(ElementsProviderExtension.class);
	
	public static void activateAll(){
		List<IInboxElementsProvider> providers = getAllProviders();
		for (IInboxElementsProvider iInboxElementsProvider : providers) {
			iInboxElementsProvider.activate();
		}
	}
	
	public static void deactivateAll(){
		List<IInboxElementsProvider> providers = getAllProviders();
		for (IInboxElementsProvider iInboxElementsProvider : providers) {
			iInboxElementsProvider.deactivate();
		}
	}
	
	private static List<IInboxElementsProvider> getAllProviders(){
		List<IInboxElementsProvider> ret = new ArrayList<IInboxElementsProvider>();
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("at.medevit.elexis.inbox.elementsprovider");
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("provider")) {
						try {
							ret.add((IInboxElementsProvider) el.createExecutableExtension("class"));
						} catch (CoreException e) {
							logger.error("Error creating IInboxElementsProvider " + e);
						}
					}
				}
			}
		}
		return ret;
	}
}
