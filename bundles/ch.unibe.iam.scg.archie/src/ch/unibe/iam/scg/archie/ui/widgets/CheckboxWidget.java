/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.unibe.iam.scg.archie.model.RegexValidation;

/**
 * <p>
 * A simple FieldComposite containing a checkbox button.
 * </p>
 *
 * $Id: CheckboxWidget.java 764 2009-07-24 11:20:03Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 764 $
 */
public class CheckboxWidget extends AbstractWidget {

	/**
	 * @param parent    Composite
	 * @param style     Integer
	 * @param labelText String
	 */
	public CheckboxWidget(Composite parent, int style, final String labelText, RegexValidation regex) {
		super(parent, style, labelText, regex);

		// Create Label
		this.label = new Label(this, SWT.NONE);
		this.label.setText(labelText);

		// Create Checkbox
		this.control = new Button(this, SWT.CHECK);

		// Layout Data
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL);
		this.layout.horizontalSpacing = AbstractWidget.STD_COLUMN_HORIZONTAL_SPACING;
		this.control.setLayoutData(layoutData);
	}

	/**
	 * @return true if checkbox is selected, false else.
	 */
	public boolean getSelection() {
		return ((Button) this.control).getSelection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue() {
		return this.getSelection();
	}

	/**
	 * Checkbox is always valid.
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final Object value) {
		if (value instanceof Boolean) {
			((Button) this.control).setSelection((Boolean) value);
		} else {
			throw new IllegalArgumentException("Must be a boolean."); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDescription(String description) {
		this.label.setToolTipText(description);
		this.control.setToolTipText(description);
	}
}
