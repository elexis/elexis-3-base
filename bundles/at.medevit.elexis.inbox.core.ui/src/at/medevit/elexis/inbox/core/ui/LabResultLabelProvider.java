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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.inbox.core.ui.preferences.InboxPreferences;
import at.medevit.elexis.inbox.model.IInboxElement;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.LabResultConstants;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.LabResult;
import ch.rgw.tools.TimeTool;

public class LabResultLabelProvider extends LabelProvider implements IColorProvider {

	public enum LabelFields {
		LAB_VALUE_SHORT("Kürzel"), LAB_VALUE_NAME("Name"), REF_RANGE("Referenzbereich"), LAB_RESULT("Resultat"),
		ORIGIN("Herkunft"), DATE("Datum");

		private final String text;

		private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		private LabelFields(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

		public static LabelFields getEnum(String value) {
			if (value.equals(LAB_VALUE_SHORT.toString())) {
				return LAB_VALUE_SHORT;
			} else if (value.equals(LAB_VALUE_NAME.toString())) {
				return LAB_VALUE_NAME;
			} else if (value.equals(LAB_RESULT.toString())) {
				return LAB_RESULT;
			} else if (value.equals(REF_RANGE.toString())) {
				return REF_RANGE;
			} else if (value.equals(ORIGIN.toString())) {
				return ORIGIN;
			} else if (value.equals(DATE.toString())) {
				return DATE;
			} else {
				return null;
			}
		}

		public String getValue(LabResult labResult) {
			switch (this) {
			case LAB_VALUE_SHORT:
				return labResult.getItem().getKuerzel();
			case LAB_VALUE_NAME:
				return labResult.getItem().getName();
			case LAB_RESULT:
				return labResult.getResult();
			case REF_RANGE:
				return labResult.getRefMale() + "/" + labResult.getRefFemale();
			case ORIGIN:
				return labResult.getOrigin().getLabel();
			case DATE:
				TimeTool observationTime = labResult.getObservationTime();
				if (observationTime == null) {
					return labResult.getDate();
				}
				return observationTime.toString(TimeTool.DATE_GER);
			default:
				return "";
			}
		}

		public String getValue(ILabResult labResult) {
			switch (this) {
			case LAB_VALUE_SHORT:
				return labResult.getItem().getCode();
			case LAB_VALUE_NAME:
				return labResult.getItem().getName();
			case LAB_RESULT:
				return labResult.getResult();
			case REF_RANGE:
				return labResult.getReferenceMale() + "/" + labResult.getReferenceFemale();
			case ORIGIN:
				return labResult.getOrigin().getLabel();
			case DATE:
				LocalDateTime observationTime = labResult.getObservationTime();
				if (observationTime == null) {
					return labResult.getDate().format(formatter);
				}
				return observationTime.format(formatter);
			default:
				return "";
			}
		}
	}

	@Override
	public Image getImage(Object element) {
		return Images.IMG_VIEW_LABORATORY.getImage();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof LabGroupedInboxElements) {
			return ((LabGroupedInboxElements) element).getLabel();
		}
		Object object = ((IInboxElement) element).getObject();
		if (object instanceof LabResult) {
			LabResult labResult = (LabResult) object;
			List<LabelFields> labelFields = InboxPreferences.getChoosenLabel();

			StringBuilder sb = new StringBuilder();
			for (LabelFields lblField : labelFields) {
				sb.append(lblField.getValue(labResult));
				sb.append(", ");
			}

			if (!sb.toString().isEmpty() && sb.substring(sb.length() - 2, sb.length()).equals(", ")) {
				sb.replace(sb.length() - 2, sb.length(), "");
			}
			return sb.toString();
		} else if (object instanceof ILabResult) {
			ILabResult labResult = (ILabResult) object;
			List<LabelFields> labelFields = InboxPreferences.getChoosenLabel();

			StringBuilder sb = new StringBuilder();
			for (LabelFields lblField : labelFields) {
				sb.append(lblField.getValue(labResult));
				sb.append(", ");
			}

			if (!sb.toString().isEmpty() && sb.substring(sb.length() - 2, sb.length()).equals(", ")) {
				sb.replace(sb.length() - 2, sb.length(), "");
			}
			return sb.toString();
		}
		return ((IInboxElement) element).getLabel();
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof LabGroupedInboxElements) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
		boolean pathologic = false;
		Object object = ((IInboxElement) element).getObject();
		if (object instanceof LabResult) {
			pathologic = ((LabResult) object).isFlag(LabResultConstants.PATHOLOGIC);
		} else if (object instanceof ILabResult) {
			pathologic = ((ILabResult) object).isPathologic();
		}
		if (pathologic) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		} else {
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
	}

	@Override
	public Color getBackground(Object element) {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}
}
