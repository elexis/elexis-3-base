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
package at.medevit.elexis.emediplan.inbox;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.inbox.model.IInboxElement;
import ch.elexis.core.model.IBlob;
import ch.rgw.tools.TimeTool;

public class EMediplanLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		IBlob document = (IBlob) ((IInboxElement) element).getObject();
		return "EMediplan von " + new TimeTool(document.getLastupdate()).toString(TimeTool.FULL_GER);
	}

	@Override
	public Image getImage(Object element) {
		return ResourceManager.getPluginImage("at.medevit.elexis.emediplan.ui", "rsc/logo.png"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
