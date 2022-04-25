/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;

public interface IAgendaLayout {
	public Composite getComposite();

	public int getLeftOffset();

	public int getPadding();

	public double getWidthPerColumn();

	public double getPixelPerMinute();

	public MenuManager getContextMenuManager();
}
