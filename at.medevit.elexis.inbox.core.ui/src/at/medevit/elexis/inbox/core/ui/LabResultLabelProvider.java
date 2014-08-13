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
package at.medevit.elexis.inbox.core.ui;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.inbox.model.InboxElement;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.LabResult;

public class LabResultLabelProvider extends LabelProvider implements IColorProvider {
	@Override
	public Image getImage(Object element){
		return Images.IMG_VIEW_LABORATORY.getImage();
	}
	
	@Override
	public String getText(Object element){
		return ((InboxElement) element).getLabel();
	}
	
	@Override
	public Color getForeground(Object element){
		LabResult labResult = (LabResult) ((InboxElement) element).getObject();
		if (labResult.isFlag(LabResult.PATHOLOGIC)) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		} else {
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
	}
	
	@Override
	public Color getBackground(Object element){
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}
}
