/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
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

	private static List<IInboxElementsProvider> instances;

	public synchronized static void activateAll() {
		if (instances == null) {
			updateInstances();
		}
		for (IInboxElementsProvider iInboxElementsProvider : instances) {
			iInboxElementsProvider.activate();
		}
	}

	public synchronized static void deactivateAll() {
		if (instances == null) {
			updateInstances();
		}
		for (IInboxElementsProvider iInboxElementsProvider : instances) {
			iInboxElementsProvider.deactivate();
		}
	}

	private static void updateInstances() {
		instances = new ArrayList<IInboxElementsProvider>();
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("at.medevit.elexis.inbox.elementsprovider"); //$NON-NLS-1$
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("provider")) { //$NON-NLS-1$
						try {
							instances.add((IInboxElementsProvider) el.createExecutableExtension("class")); //$NON-NLS-1$
						} catch (CoreException e) {
							logger.error("Error creating IInboxElementsProvider " + e); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}
}
