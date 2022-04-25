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
package at.medevit.elexis.inbox.ui.part.provider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.types.Gender;
import ch.elexis.core.ui.icons.Images;

public class InboxElementLabelProvider extends LabelProvider implements IColorProvider {

	private InboxElementUiExtension extension;

	public InboxElementLabelProvider() {
		extension = new InboxElementUiExtension();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PatientInboxElements) {
			return ((PatientInboxElements) element).toString();
		} else if (element instanceof IInboxElement) {
			String text = extension.getText((IInboxElement) element);
			if (text != null) {
				return text;
			} else {
				Object obj = ((IInboxElement) element).getObject();
				if (obj != null) {
					return "unbekannt [" + obj.getClass().getSimpleName() + "]";
				} else {
					return "unbekannt";
				}
			}
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PatientInboxElements) {
			IPatient pat = ((PatientInboxElements) element).getPatient();
			if (pat != null) {
				if (pat.getGender() == Gender.MALE) {
					return Images.IMG_MANN.getImage();
				} else {
					return Images.IMG_FRAU.getImage();
				}
			} else {
				return Images.IMG_QUESTION_MARK.getImage();
			}
		} else if (element instanceof IInboxElement) {
			Image image = extension.getImage((IInboxElement) element);
			if (image != null) {
				return image;
			} else {
				return Images.IMG_QUESTION_MARK.getImage();
			}
		}
		return null;
	}

	public Color getForeground(Object element) {
		if (element instanceof IInboxElement) {
			Color color = extension.getForeground((IInboxElement) element);
			if (color != null) {
				return color;
			}
		}
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}

	public Color getBackground(Object element) {
		if (element instanceof IInboxElement) {
			Color color = extension.getBackground((IInboxElement) element);
			if (color != null) {
				return color;
			}
		}
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

	public boolean isVisible(IInboxElement element) {
		return extension.isVisible(element);
	}
}