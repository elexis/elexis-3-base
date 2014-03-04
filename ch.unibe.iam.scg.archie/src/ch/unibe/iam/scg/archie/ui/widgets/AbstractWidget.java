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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ch.unibe.iam.scg.archie.model.RegexValidation;

/**
 * <p>
 * An AbstractWidget has a label, a layout and any kind of control.
 * </p>
 * 
 * $Id: AbstractWidget.java 764 2009-07-24 11:20:03Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 764 $
 */
public abstract class AbstractWidget extends Composite {

	/** Horizontal space between components in this composite. */
	public final static int STD_COLUMN_HORIZONTAL_SPACING = 20;

	protected GridLayout layout;
	protected Label label;
	protected Control control;

	/** regex is final */
	protected final RegexValidation regexValidation;

	/**
	 * Constructs a FieldComposite. Checks Arguments and creates a layout
	 * (specified by implementor classes).
	 * 
	 * @param parent
	 *            Composite
	 * @param style
	 *            Integer
	 * @param labelText
	 *            String
	 */
	public AbstractWidget(Composite parent, int style, final String labelText, RegexValidation regex) {
		super(parent, style);

		// Check Arguments
		if (labelText == null || labelText.equals("")) {
			throw new IllegalArgumentException("LabelText can not be null or empty!");
		}
		
		// can be null
		this.regexValidation = regex;

		// Create Layout
		this.createLayout();
	}

	/**
	 * Returns a widget's value. This is usually the contents of the control
	 * field in a widget. Depending on the widget type, different types of
	 * values are returned (e.g. checkbox <code>true</code>, combo
	 * <code>string</code> etc.).
	 * 
	 * @return text String
	 */
	abstract public Object getValue();

	/**
	 * Set's a widget's value.
	 * 
	 * @param value
	 */
	abstract public void setValue(final Object value);

	/**
	 * Describes this FieldComposites (e.g. for a ToolTip)
	 * 
	 * @param description
	 */
	abstract public void setDescription(final String description);

	/**
	 * Returns true if the value in a widget's control field is valid.
	 * 
	 * @return true if this field is valid.
	 */
	abstract public boolean isValid();
	
	/**
	 * Enables or disables the control of this FieldComposite
	 * 
	 * @param enabled
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.control.setEnabled(enabled);
	}

	/**
	 * Returns the label for this field composite.
	 * 
	 * @return label
	 */
	public Label getLabel() {
		return this.label;
	}

	/**
	 * Standard implementation: GridLayout with two columns.
	 */
	protected void createLayout() {
		this.layout = new GridLayout();
		this.layout.numColumns = 2;
		this.layout.marginWidth = 2;
		this.setLayout(this.layout);
	}
	
	/**
	 * Checks whether we have a regexValidation or not.
	 */
	protected boolean hasRegexValidation() {
		if (this.regexValidation == null) {
			return false;
		}
		return true;
	}
}
