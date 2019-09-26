/*******************************************************************************
 * Copyright (c) 2019 Medbits GmbH.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Thomas Huster - initial API and implementation
 *******************************************************************************/
package at.medbits.elexis.labbit.ui;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medbits.elexis.labbit.discovery.Discovery;

@Component
public class DiscoveryServiceHolder {
	
	private static Discovery service;
	
	@Reference
	public void setDiscovery(Discovery discovery){
		DiscoveryServiceHolder.service = discovery;
	}
	
	public static Discovery get(){
		if (service == null) {
			throw new IllegalStateException("No discovery service available");
		}
		return service;
	}
}
