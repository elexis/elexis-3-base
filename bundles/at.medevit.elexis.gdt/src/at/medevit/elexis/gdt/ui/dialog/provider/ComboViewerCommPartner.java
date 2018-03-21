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
package at.medevit.elexis.gdt.ui.dialog.provider;

import org.eclipse.jface.viewers.LabelProvider;

import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;

public class ComboViewerCommPartner extends LabelProvider {
	
	@Override
	public String getText(Object element){
		IGDTCommunicationPartner cp = (IGDTCommunicationPartner) element;
		return cp.getLabel();
	}
	
}
