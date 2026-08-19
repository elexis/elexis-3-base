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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
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
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;

public class InboxElementUiExtension {

	private static Logger logger = LoggerFactory.getLogger(InboxElementUiExtension.class);

	private Map<String, IInboxElementUiProvider> providers;

	public InboxElementUiExtension() {
		providers = getExtensions();
	}

	public List<IInboxElementUiProvider> getProviders() {
		ArrayList<IInboxElementUiProvider> ret = new ArrayList<>(providers.values());
		return ret;
	}

	public String getId(IInboxElementUiProvider provider) {
		for (Entry<String, IInboxElementUiProvider> entry : providers.entrySet()) {
			if (entry.getValue().getClass().equals(provider.getClass())) {
				return entry.getKey();
			}
		}
		return StringUtils.EMPTY;
	}

	private IInboxElementUiProvider getProvider(IInboxElement element) {
		for (IInboxElementUiProvider iInboxElementUiProvider : providers.values()) {
			if (iInboxElementUiProvider.isProviderFor(element)) {
				return iInboxElementUiProvider;
			}
		}
		return null;
	}

	private Map<String, IInboxElementUiProvider> getExtensions() {
		Map<String, IInboxElementUiProvider> ret = new HashMap<>();
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("at.medevit.elexis.inbox.ui.elementsui"); //$NON-NLS-1$
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("uiprovider")) { //$NON-NLS-1$
						try {
							ret.put(el.getAttribute("id"), //$NON-NLS-1$
									(IInboxElementUiProvider) el.createExecutableExtension("class"));//$NON-NLS-1$
						} catch (CoreException e) {
							logger.error("Error creating IInboxElementsProvider " + e); //$NON-NLS-1$
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

	public String getTooltipText(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getToolTipProvider() != null) {
			return provider.getToolTipProvider().getToolTipText(element);
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

	public void fireSingleClicked(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null) {
			provider.singleClicked(element);
		}
	}

	public boolean isVisible(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null) {
			return provider.isVisible(element);
		}
		return true;
	}

	public GroupedInboxElements getGrouped(PatientInboxElements patientInboxElements, IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.isGrouped()) {
			return provider.getGrouped(patientInboxElements, element);
		}
		return null;
	}

	public LocalDate getObjectDate(IInboxElement element) {
		IInboxElementUiProvider provider = getProvider(element);
		if (provider != null) {
			return provider.getObjectDate(element);
		}
		return null;
	}
}
