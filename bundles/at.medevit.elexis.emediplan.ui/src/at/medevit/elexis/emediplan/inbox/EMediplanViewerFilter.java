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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.inbox.model.InboxElement;
import ch.elexis.data.NamedBlob;

public class EMediplanViewerFilter extends ViewerFilter {
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		if (element instanceof InboxElement) {
			Object o = ((InboxElement) element).getObject();
			if (o instanceof NamedBlob && ((NamedBlob) o).getId().startsWith("Med_")) {
				return true;
			}
			return false;
		}
		return true;
	}
}
