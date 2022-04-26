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
package at.medevit.elexis.inbox.ui.part.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;

public class InboxElementUiExtension {

	private static Logger logger = LoggerFactory.getLogger(InboxElementUiExtension.class);

	private List<IInboxElementUiProvider> providers;

	public InboxElementUiExtension() {
		providers = getExtensions();
	}

	public List<IInboxElementUiProvider> getProviders() {
		return providers;
	}

	private IInboxElementUiProvider getProvider(IInboxElement element) {
		for (IInboxElementUiProvider iInboxElementUiProvider : providers) {
			if (iInboxElementUiProvider.isProviderFor(element)) {
				return iInboxElementUiProvider;
			}
		}
		return null;
	}

	private List<IInboxElementUiProvider> getExtensions() {
		List<IInboxElementUiProvider> ret = new ArrayList<IInboxElementUiProvider>();
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("at.medevit.elexis.inbox.ui.elementsui");
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("uiprovider")) {
						try {
							ret.add((IInboxElementUiProvider) el.createExecutableExtension("class"));
						} catch (CoreException e) {
							logger.error("Error creating IInboxElementsProvider " + e);
						}
					}
				}
			}
		}
		return ret;
	}

	public String getText(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getLabelProvider() != null) {
			return provider.getLabelProvider().getText(element);
		}
		return null;
	}

	public Image getImage(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getLabelProvider() != null) {
			return provider.getLabelProvider().getImage(element);
		}
		return null;
	}

	public Color getForeground(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getColorProvider() != null) {
			return provider.getColorProvider().getForeground(element);
		}
		return null;
	}

	public Color getBackground(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getColorProvider() != null) {
			return provider.getColorProvider().getBackground(element);
		}
		return null;
	}

	public void fireDoubleClicked(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null) {
			provider.doubleClicked(element);
		}
	}

	public boolean isVisible(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null) {
			return provider.isVisible(element);
		}
		return true;
	}
}
