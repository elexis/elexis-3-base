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
package at.medevit.elexis.ehc.ui.inbox;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.ehc.ui.model.EhcDocument;
import at.medevit.elexis.inbox.model.IInboxElement;

public class EhcDocumentLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		EhcDocument document = (EhcDocument) ((IInboxElement) element).getObject();
		return document.getLabel();
	}

	@Override
	public Image getImage(Object element) {
		return ResourceManager.getPluginImage("at.medevit.elexis.ehc.ui", "icons/ehc.jpg"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
