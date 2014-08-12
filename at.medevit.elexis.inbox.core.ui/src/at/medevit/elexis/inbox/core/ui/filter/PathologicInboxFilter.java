/*******************************************************************************
 * Copyright (c) 2013, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
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
