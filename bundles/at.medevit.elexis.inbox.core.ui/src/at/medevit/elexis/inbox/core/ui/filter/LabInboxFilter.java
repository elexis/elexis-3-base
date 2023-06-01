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

import java.util.Optional;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.inbox.core.ui.LabGroupedInboxElements;
import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.core.model.ILabResult;

public class LabInboxFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof LabGroupedInboxElements) {
			return true;
		} else if (element instanceof IInboxElement) {
			if (((IInboxElement) element).getObject() instanceof ILabResult) {
				return true;
			}
			return false;
		} else if (element instanceof PatientInboxElements) {
			PatientInboxElements patientInbox = (PatientInboxElements) element;
			Optional<IInboxElement> selectedElement = ((PatientInboxElements) element).getElements().stream()
					.filter(ie -> select(viewer, patientInbox, ie)).findAny();
			return selectedElement.isPresent();
		}
		return true;
	}
}
