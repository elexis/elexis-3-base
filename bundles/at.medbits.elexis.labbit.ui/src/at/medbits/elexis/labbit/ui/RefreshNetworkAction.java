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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import ch.elexis.core.ui.icons.Images;

public class RefreshNetworkAction extends Action {
	
	private String network;
	
	public RefreshNetworkAction(String network){
		this.network = network;
	}
	
	@Override
	public void run(){
		DiscoveryServiceHolder.get().refresh(network);
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(){
		return Images.IMG_REFRESH.getImageDescriptor();
	}
	
	@Override
	public String getToolTipText(){
		return "Netzwerk " + network + " duchsuchen.";
	}
}
