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
package ch.elexis.omnivore.ui.inbox;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.inbox.model.InboxElement;
import ch.elexis.omnivore.data.DocHandle;

public class DocHandleLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element){
		DocHandle document = (DocHandle) ((InboxElement) element).getObject();
		return document.getTitle() + " vom " + document.getDate();
	}
	
	@Override
	public Image getImage(Object element){
		return ResourceManager.getPluginImage("ch.elexis.omnivore.ui", "icons/fressen.gif");
	}
}
