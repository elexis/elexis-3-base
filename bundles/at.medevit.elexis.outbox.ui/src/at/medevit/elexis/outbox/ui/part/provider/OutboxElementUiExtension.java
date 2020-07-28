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
package at.medevit.elexis.outbox.ui.part.provider;

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

import at.medevit.elexis.outbox.model.IOutboxElement;

public class OutboxElementUiExtension {
	
	private static Logger logger = LoggerFactory.getLogger(OutboxElementUiExtension.class);
	
	private List<IOutboxElementUiProvider> providers;
	
	public OutboxElementUiExtension(){
		providers = getExtensions();
	}
	
	public List<IOutboxElementUiProvider> getProviders(){
		return providers;
	}
	
	private IOutboxElementUiProvider getProvider(IOutboxElement element){
		for (IOutboxElementUiProvider iOutboxElementUiProvider : providers) {
			if (iOutboxElementUiProvider.isProviderFor(element)) {
				return iOutboxElementUiProvider;
			}
		}
		return null;
	}
	
	private List<IOutboxElementUiProvider> getExtensions(){
		List<IOutboxElementUiProvider> ret = new ArrayList<>();
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("at.medevit.elexis.outbox.ui.elementsui");
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("uiprovider")) {
						try {
							ret.add((IOutboxElementUiProvider) el.createExecutableExtension("class"));
						} catch (CoreException e) {
							logger.error("Error creating IOutboxElementsProvider {}", e);
						}
					}
				}
			}
		}
		return ret;
	}
	
	public String getText(IOutboxElement element){
		IOutboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getLabelProvider() != null) {
			return provider.getLabelProvider().getText(element);
		}
		return null;
	}
	
	public Image getImage(IOutboxElement element){
		IOutboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getLabelProvider() != null) {
			return provider.getLabelProvider().getImage(element);
		}
		return null;
	}
	
	public Color getForeground(IOutboxElement element){
		IOutboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getColorProvider() != null) {
			return provider.getColorProvider().getForeground(element);
		}
		return null;
	}
	
	public Color getBackground(IOutboxElement element){
		IOutboxElementUiProvider provider = getProvider(element);
		if (provider != null && provider.getColorProvider() != null) {
			return provider.getColorProvider().getBackground(element);
		}
		return null;
	}
	
	public void fireDoubleClicked(IOutboxElement element){
		IOutboxElementUiProvider provider = getProvider(element);
		if (provider != null) {
			provider.doubleClicked(element);
		}
	}
}
