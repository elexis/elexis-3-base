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
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.medevit.elexis.inbox.model.IInboxElement;
import ch.elexis.omnivore.model.IDocumentHandle;

public class DocHandleLabelProvider extends LabelProvider {
	private Image image;

	@Override
	public String getText(Object element) {
		IDocumentHandle document = (IDocumentHandle) ((IInboxElement) element).getObject();
		return document.getTitle();
	}

	@Override
	public Image getImage(Object element) {
		if (image == null) {
			image = AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.omnivore.ui", "icons/fressen.gif") //$NON-NLS-1$ //$NON-NLS-2$
					.createImage();
		}
		return image;
	}
}
