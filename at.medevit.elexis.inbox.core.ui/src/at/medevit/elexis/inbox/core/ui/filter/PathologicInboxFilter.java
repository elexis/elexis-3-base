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
package at.medevit.elexis.inbox.core.ui.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.inbox.model.InboxElement;
import ch.elexis.data.LabResult;

public class PathologicInboxFilter extends ViewerFilter {
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		if (element instanceof InboxElement) {
			if (((InboxElement) element).getObject() instanceof LabResult) {
				LabResult labResult = (LabResult) ((InboxElement) element).getObject();
				if (labResult.isFlag(LabResult.PATHOLOGIC)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
}
