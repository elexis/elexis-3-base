/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.base.konsextension.privatnotizen;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import ch.elexis.core.ui.icons.ImageSize;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.privatnotizen.Privatnotiz;

public class NotizInputDialog extends TitleAreaDialog {
	private Privatnotiz mine;
	private Text textField;

	public NotizInputDialog(Shell shell, Privatnotiz note) {
		super(shell);
		mine = note;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		textField = new Text(parent, SWT.MULTI | SWT.BORDER);
		textField.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		textField.setText(mine.getText());
		return textField;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.NotizInputDialog_noteDlgTitle);
		getShell().setText(Messages.NotizInputDialog_noteDlgText);
		setTitleImage(Images.IMG_LOGO.getImage(ImageSize._75x66_TitleDialogIconSize));
		setMessage(Messages.NotizInputDialog_noteDlgMessage);
	}

	@Override
	protected void okPressed() {
		mine.setText(textField.getText());
		super.okPressed();
	}

}
