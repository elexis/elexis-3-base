/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import ch.elexis.core.ui.UiDesk;

/**
 * A utility class for creating error message boxes.
 *
 */
public final class MessageBoxUtil {
	public static void showErrorDialog(final String title, final String message) {
		MessageBox messgeBox = new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
		messgeBox.setText(title);
		messgeBox.setMessage(message);
		messgeBox.open();
	}
}
