/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;

public class EditCatsDialog extends TitleAreaDialog {
	EditCatsDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Text ret = new Text(parent, SWT.MULTI | SWT.BORDER);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setText(StringTool.join(KassenbuchEintrag.getCategories(), "\n"));
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Kassenbuch-Kategorien");
		setMessage("Geben Sie eine Kategorie pro Zeile ein");
		getShell().setText("Elexis-Kassenbuch");
	}

	@Override
	protected void okPressed() {
		String ncats = ((Text) getDialogArea()).getText().replaceAll("\n", KassenbuchEintrag.GLOBAL_CFG_SEPARATOR);
		ConfigServiceHolder.setGlobal(KassenbuchEintrag.CATEGORIES, ncats.replaceAll("\r", ""));
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
