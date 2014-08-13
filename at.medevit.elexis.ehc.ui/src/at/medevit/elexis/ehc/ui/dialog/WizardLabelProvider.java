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
package at.medevit.elexis.ehc.ui.dialog;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import at.medevit.elexis.ehc.ui.extension.IWizardCategory;
import at.medevit.elexis.ehc.ui.extension.IWizardDescriptor;
import ch.elexis.core.ui.icons.Images;

public class WizardLabelProvider extends LabelProvider implements IBaseLabelProvider {
	@Override
	public String getText(Object element){
		if (element instanceof IWizardCategory) {
			return ((IWizardCategory) element).getLabel();
		} else if (element instanceof IWizardDescriptor) {
			return ((IWizardDescriptor) element).getLabel();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element){
		if (element instanceof IWizardCategory) {
			return Images.IMG_BOOK.getImage();
		} else if (element instanceof IWizardDescriptor) {
			return Images.IMG_IMPORT.getImage();
		}
		return super.getImage(element);
	}
}
