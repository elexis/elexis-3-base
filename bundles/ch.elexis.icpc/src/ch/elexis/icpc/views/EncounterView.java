/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc.views;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import jakarta.inject.Inject;

public class EncounterView extends ViewPart {
	public static final String ID = "ch.elexis.icpc.encounterView"; //$NON-NLS-1$
	private EncounterDisplay display;

	@Inject
	void activePatient(@Optional IPatient patient) {
		if (display != null && !display.isDisposed()) {
			Display.getDefault().asyncExec(() -> {
				display.setEncounter(null);
			});
		}
	}

	@Inject
	void selectedEncounter(@Optional IcpcEncounter encounter) {
		if (display != null && !display.isDisposed()) {
			Display.getDefault().asyncExec(() -> {
				display.setEncounter(encounter);
			});
		}
	}

	public EncounterView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		display = new EncounterDisplay(parent);
		display.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
	}

	@Override
	public void setFocus() {
		display.setFocus();
	}

	public void selectionEvent(PersistentObject obj) {
		if (obj instanceof IcpcEncounter) {
			display.setEncounter((IcpcEncounter) obj);
		} else if (obj instanceof Patient) {
			display.setEncounter(null);
		}

	}

}
