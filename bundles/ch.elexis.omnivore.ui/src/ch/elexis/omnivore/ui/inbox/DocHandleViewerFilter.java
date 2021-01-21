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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.inbox.model.IInboxElement;
import ch.elexis.omnivore.model.IDocumentHandle;

public class DocHandleViewerFilter extends ViewerFilter {
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		if (element instanceof IInboxElement) {
			Object o = ((IInboxElement) element).getObject();
			if (o instanceof IDocumentHandle) {
				return true;
			}	
			return false;
		}
		return true;
	}
}
