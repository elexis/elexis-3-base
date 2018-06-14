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
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.core.ui.util.Log;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.RegexValidation;

/**
 * <p>
 * Implements <code>FieldComposite</code> with a <code>SmartNumericField</code>.
 * </p>
 * 
 * $Id: NumericWidget.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class NumericWidget extends TextWidget {

	/**
	 * @param parent
	 *            Composite
	 * @param style
	 *            Integer
	 * @param labelText
	 *            String
	 * @param regex
	 *            String
	 */
	public NumericWidget(Composite parent, int style, final String labelText, RegexValidation regex) {
		super(parent, style, labelText, regex);

		// Create quickFix menu listener
		this.controlDecoration.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent event) {
				// no quick fix if we aren't in error state.
				if (NumericWidget.this.smartField.isValid()) {
					return;
				}
				if (NumericWidget.this.smartField.quickFixMenu == null) {
					NumericWidget.this.smartField.quickFixMenu = NumericWidget.this
							.createQuickFixMenu((SmartNumericField) NumericWidget.this.smartField);
				}
				NumericWidget.this.smartField.quickFixMenu.setLocation(event.x, event.y);
				NumericWidget.this.smartField.quickFixMenu.setVisible(true);
			}
		});
	}

	/**
	 * Create a <code>SmartNumericField</code>
	 */
	@Override
	protected void createSmartField() {
		this.smartField = new SmartNumericField();
	}

	protected Menu createQuickFixMenu(final SmartNumericField field) {
		Menu newMenu = new Menu(this.control);
		MenuItem item = new MenuItem(newMenu, SWT.PUSH);
		item.setText(Messages.FIELD_NUMERIC_QUICKFIX);
		item.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				field.quickFix();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				// do nothing
			}
		});
		return newMenu;
	}

	/**
	 * Returns the value of the numeric field as an <code>int</code>. The String
	 * content ist parsed using the <code>Integer.parseInt()</code> method, if
	 * an exception is thrown, it's logged as an error to the Elexis log.
	 * 
	 * @return Contents of the inner <code>SmartNumericField</code> as an
	 *         <code>int</code>. This way, implementing providers can use an
	 *         <code>int</code> as parameters for their setter methods.
	 */
	@Override
	public Object getValue() {
		int value = 0;
		try {
			value = Integer.parseInt(this.smartField.getContents());
		} catch (Exception e) {
			ArchieActivator.LOG.log(e.getLocalizedMessage(), Log.ERRORS);
		}
		return value;
	}

	/**
	 * Is only valid if content are digits and nothing else. Provides a
	 * quick-fix to remove all characters that are not digits.
	 */
	private class SmartNumericField extends SmartField {

		public SmartNumericField() {
			super();
		}

		@Override
		public boolean isValid() {
			// perform basic validation for a numeric only field
			String contents = this.getContents();
			for (int i = 0; i < contents.length(); i++) {
				if (!Character.isDigit(contents.charAt(i))) {
					return false;
				}
			}

			// perform regex validation if available
			if (NumericWidget.this.hasRegexValidation()
					&& !this.getContents().matches(NumericWidget.this.regexValidation.getPattern())) {
				return false;
			}
			return true;

		}

		@Override
		public boolean hasQuickFix() {
			return true;
		}

		@Override
		protected String getQuickfixMessage() {
			return this.getErrorMessage();
		}

		@Override
		protected String getErrorMessage() {
			String error = Messages.FIELD_NUMERIC_ERROR;
			if (NumericWidget.this.hasRegexValidation()) {
				error += "\n" + NumericWidget.this.regexValidation.getMessage();
			}
			return error;
		}

		/**
		 * Removes all characters except digits.
		 */
		protected void quickFix() {
			String contents = this.getContents();
			StringBuffer digitsOnly = new StringBuffer();
			int length = contents.length();
			for (int i = 0; i < length;) {
				char ch = contents.charAt(i++);
				if (Character.isDigit(ch)) {
					digitsOnly.append(ch);
				}
			}
			this.setContents(digitsOnly.toString());
		}
	}
}
