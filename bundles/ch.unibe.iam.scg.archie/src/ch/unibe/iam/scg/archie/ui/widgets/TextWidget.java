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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.Decorators;

/**
 * <p>
 * Serves as a base for implementing field composites with a text control. They
 * have an inner <code>SmartField</code>. The smartField is an inner class,
 * which is able to validate itself, decorate itself, provide content assistance
 * and a quick-fix.
 * </p>
 *
 * $Id: TextWidget.java 764 2009-07-24 11:20:03Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 764 $
 */
public class TextWidget extends AbstractWidget {

	// Margin between field and decoration image in pixels.
	protected final static int DECORATION_HORIZONTAL_MARGIN = 3;

	protected ControlDecoration controlDecoration;

	protected SmartField smartField;

	/**
	 * @param parent    Composite
	 * @param style     Integer
	 * @param labelText String
	 * @param regex     String Optional <code>RegexValidation</code>, can be
	 *                  <code>null</code> if not desired.
	 */
	public TextWidget(Composite parent, int style, final String labelText, RegexValidation regex) {
		super(parent, style, labelText, regex);

		// Create label
		this.label = new Label(this, SWT.NONE);
		this.label.setText(labelText);

		this.layout.horizontalSpacing = AbstractWidget.STD_COLUMN_HORIZONTAL_SPACING;

		// Create actual text field.
		this.control = new Text(this, SWT.BORDER);

		// Layout data
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL);
		layoutData.widthHint = 100;
		this.control.setLayoutData(layoutData);

		// Create SmartTextField.
		this.createSmartField();

		// Add ModifyListener to text field.
		((Text) this.control).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				TextWidget.this.handleModify(TextWidget.this.smartField);
			}
		});

		// Create control decoration.
		this.controlDecoration = new ControlDecoration(this.control, SWT.RIGHT | SWT.CENTER);
		this.controlDecoration.setShowOnlyOnFocus(false);
		this.controlDecoration.setMarginWidth(TextWidget.DECORATION_HORIZONTAL_MARGIN);
	}

	/**
	 * Default implementation, subclasses may override.
	 */
	protected void createSmartField() {
		this.smartField = new SmartField();
	}

	/**
	 * @return String Contents of the <code>AbstractSmartField</code>
	 */
	@Override
	public Object getValue() {
		return this.smartField.getContents();
	}

	/**
	 * Sets the text (contents) of the containing smart field.
	 *
	 * @param value
	 */
	@Override
	public void setValue(Object value) {
		this.smartField.setContents(value.toString());
	}

	/**
	 * @return <code>true</code> if the contents of this objects smartField are
	 *         valid, <code>false</code> else.
	 */
	@Override
	public boolean isValid() {
		return this.smartField.isValid();
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#addListener(int,
	 *      org.eclipse.swt.widgets.Listener)
	 */
	@Override
	public void addListener(int eventType, Listener listener) {
		super.addListener(eventType, listener);
		this.control.addListener(eventType, listener);
	}

	/**
	 * A SmartField has a control (e.g. a text field) a decoration and optional a
	 * quick-fix menu. It is able to validate itself.
	 */
	protected class SmartField {

		protected IControlContentAdapter contentAdapter;

		protected FieldDecoration fieldDecoration;

		// optional quickFix Menu
		protected Menu quickFixMenu;

		/**
		 * Constructs a SmartField with a <code>TextContentAdapter</code>
		 */
		public SmartField() {
			this.contentAdapter = new TextContentAdapter();
		}

		/**
		 * Is valid if not empty by default.
		 *
		 * @return true if valid, false else.
		 */
		public boolean isValid() {
			// empty fields are invalid by default - which means that every
			// field is required by default.
			if (this.getContents().equals(StringUtils.EMPTY)) {
				return false;
			}

			// check whether we have a regex validation and if it matches the
			// fields content.
			if (TextWidget.this.hasRegexValidation()
					&& this.getContents().matches(TextWidget.this.regexValidation.getPattern())) {
				return false;
			}
			return true;
		}

		/**
		 * Is never in warning state by default.
		 *
		 * @return false
		 */
		public boolean isWarning() {
			return false;
		}

		/**
		 * No quick-fix by default.
		 *
		 * @return false
		 */
		public boolean hasQuickFix() {
			return false;
		}

		/**
		 * @return String Retrieves content of field.
		 */
		public String getContents() {
			return this.contentAdapter.getControlContents(TextWidget.this.control);
		}

		/**
		 * @param contents String Content to add to field.
		 */
		public void setContents(final String contents) {
			this.contentAdapter.setControlContents(TextWidget.this.control, contents, contents.length());
		}

		public FieldDecoration getFieldDecoration() {
			return this.fieldDecoration;
		}

		public void setFieldDecoration(FieldDecoration fieldDecoration) {
			this.fieldDecoration = fieldDecoration;
		}

		/**
		 * @return decorator standard messages.
		 */
		protected String getDecorationMessage(int type) {
			switch (type) {
			case Decorators.ERROR:
				return this.getErrorMessage();
			case Decorators.QUICKFIX:
				return this.getQuickfixMessage();
			case Decorators.WARNING:
				return this.getWarningMessage();
			case Decorators.VALID:
				return this.getValidMessage();
			default:
				return StringUtils.EMPTY;
			}
		}

		/**
		 * @return Default decoration error message.
		 */
		protected String getErrorMessage() {
			String error = Messages.FIELD_GENERAL_ERROR;
			if (TextWidget.this.hasRegexValidation()) {
				error += StringUtils.SPACE + TextWidget.this.regexValidation.getMessage();
			}
			return error;
		}

		/**
		 * @return Default decoration quickfix message.
		 */
		protected String getQuickfixMessage() {
			return Messages.FIELD_GENERAL_ERROR_QUICKFIX;
		}

		/**
		 * @return Default decoration warning message.
		 */
		protected String getWarningMessage() {
			return Messages.FIELD_GENERAL_WARNING;
		}

		/**
		 * @return Default decoration valid message.
		 */
		protected String getValidMessage() {
			return Messages.FIELD_GENERAL_VALID;
		}
	}

	/**
	 * Every time the field gets modified this gets run. Checks in what state the
	 * field is an decorates accordingly.
	 *
	 * @param smartField
	 */
	protected void handleModify(final SmartField smartField) {
		// Hide everything.
		this.hideQuickfix(smartField);
		this.hideError(smartField);
		this.hideValid(smartField);
		this.hideWarning(smartField);

		// Show something.
		if (!smartField.isValid()) {
			if (smartField.hasQuickFix()) {
				this.showQuickfix(smartField);
			} else {
				this.showError(smartField);
			}
		} else {
			if (smartField.isWarning()) {
				this.showWarning(smartField);
			} else {
				this.showValid(smartField);
			}
		}
	}

	/**
	 * @param smartField
	 */
	protected void showError(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.ERROR, true);
	}

	/**
	 * @param smartField
	 */
	protected void hideError(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.ERROR, false);
	}

	/**
	 * @param smartField
	 */
	protected void showQuickfix(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.QUICKFIX, true);
	}

	/**
	 * @param smartField
	 */
	protected void hideQuickfix(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.QUICKFIX, false);
	}

	/**
	 * @param smartField
	 */
	protected void showWarning(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.WARNING, true);
	}

	/**
	 * @param smartField
	 */
	protected void hideWarning(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.WARNING, false);
	}

	/**
	 * @param smartField
	 */
	protected void showValid(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.VALID, true);
	}

	/**
	 * @param smartField
	 */
	protected void hideValid(final SmartField smartField) {
		this.showDecoration(smartField, Decorators.VALID, false);
	}

	private void showDecoration(final SmartField smartField, int type, final boolean show) {
		smartField.setFieldDecoration(Decorators.getFieldDecoration(type, smartField.getDecorationMessage(type)));
		if (show) {
			this.controlDecoration.setImage(smartField.getFieldDecoration().getImage());
			this.controlDecoration.setDescriptionText(smartField.getFieldDecoration().getDescription());
			this.controlDecoration.show();
		} else {
			this.controlDecoration.hide();
		}
	}

	/**
	 * @see ch.unibe.iam.scg.archie.ui.widgets.AbstractWidget#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String description) {
		this.label.setToolTipText(description);
		this.control.setToolTipText(description);
	}
}
