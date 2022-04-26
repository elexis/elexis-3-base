/*******************************************************************************
 * Copyright (c) 2005-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.core.ui.icons.Images;

public class TerminSuchenDialog extends TitleAreaDialog {
	IPlannable actPlannable;

	TerminSuchenDialog(IPlannable act, Shell parent) {
		super(parent);
		actPlannable = act;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		return super.createDialogArea(parent);
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.TerminSuchenDialog_findTermin);
		setMessage(Messages.TerminSuchenDialog_enterfind);
		getShell().setText(Messages.TerminSuchenDialog_findTermin);
		setTitleImage(Images.IMG_LOGO.getImage());
	}

}
