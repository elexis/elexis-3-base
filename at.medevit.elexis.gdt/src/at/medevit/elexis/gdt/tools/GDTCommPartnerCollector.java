/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.tools;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import at.medevit.elexis.gdt.Activator;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartnerProvider;

public class GDTCommPartnerCollector {
	
	public static final String EP_ID = Activator.PLUGIN_ID + ".communicationPartner";
	
	public static List<IGDTCommunicationPartner> getRegisteredCommPartners(){
		List<IGDTCommunicationPartner> ret = null;
		
		IConfigurationElement[] config =
			Platform.getExtensionRegistry().getConfigurationElementsFor(EP_ID);
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("interface");
				if (o instanceof IGDTCommunicationPartner) {
					if (ret == null)
						ret = new LinkedList<IGDTCommunicationPartner>();
					ret.add((IGDTCommunicationPartner) o);
				}
				if (o instanceof IGDTCommunicationPartnerProvider) {
					if (ret == null)
						ret = new LinkedList<IGDTCommunicationPartner>();
					ret.addAll(((IGDTCommunicationPartnerProvider) o).getChildCommunicationPartners());
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		if (ret == null)
			return null;
		return ret;
	}
	
	public static IGDTCommunicationPartner identifyCommunicationPartnerByIncomingDirectory(String incomingDirectory) {
		IConfigurationElement[] config =
				Platform.getExtensionRegistry().getConfigurationElementsFor(EP_ID);
			try {
				for (IConfigurationElement e : config) {
					final Object o = e.createExecutableExtension("interface");
					if (o instanceof IGDTCommunicationPartner) {
						IGDTCommunicationPartner cp = (IGDTCommunicationPartner) o;
						if(cp.getIncomingDirectory().equalsIgnoreCase(incomingDirectory)) return cp;
					}
					if (o instanceof IGDTCommunicationPartnerProvider) {
						LinkedList<IGDTCommunicationPartner> childPartners = new LinkedList<IGDTCommunicationPartner>();
						for (IGDTCommunicationPartner igdtCommunicationPartner : childPartners) {
							if(igdtCommunicationPartner.getIncomingDirectory().equalsIgnoreCase(incomingDirectory)) {
								return igdtCommunicationPartner;
							}
						}
					}
				}
			} catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}
			return null;
	}
	
	public static IGDTCommunicationPartner identifyCommunicationPartnerByLabel(String label) {
		IConfigurationElement[] config =
				Platform.getExtensionRegistry().getConfigurationElementsFor(EP_ID);
			try {
				for (IConfigurationElement e : config) {
					final Object o = e.createExecutableExtension("interface");
					if (o instanceof IGDTCommunicationPartner) {
						IGDTCommunicationPartner cp = (IGDTCommunicationPartner) o;
						if (cp.getLabel().equalsIgnoreCase(label)) {
							return cp;
						}
					}
					if (o instanceof IGDTCommunicationPartnerProvider) {
						LinkedList<IGDTCommunicationPartner> childPartners = new LinkedList<IGDTCommunicationPartner>();
						for (IGDTCommunicationPartner igdtCommunicationPartner : childPartners) {
							if(igdtCommunicationPartner.getLabel().equalsIgnoreCase(label)) {
								return igdtCommunicationPartner;
							}
						}
					}
				}
			} catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}
			return null;
	}
	
}
