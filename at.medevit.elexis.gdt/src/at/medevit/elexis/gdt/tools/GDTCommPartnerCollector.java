/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.tools;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import at.medevit.elexis.gdt.Activator;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;

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
						if(cp.getLabel().equalsIgnoreCase(label)) return cp;
					}
				}
			} catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}
			return null;
	}
	
}
