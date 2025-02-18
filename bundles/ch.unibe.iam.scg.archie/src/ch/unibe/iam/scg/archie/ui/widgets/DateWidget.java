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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.RegexValidation;

/**
 * Implements <code>AbstractWidget</code> with a SmartDateField.
 *
 * $Id: DateWidget.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class DateWidget extends TextWidget {

	/**
	 * Valid DateFormat pattern for DateTextFieldComposites.
	 */
	public final static String VALID_DATE_FORMAT = "dd.MM.yyyy"; //$NON-NLS-1$

	/**
	 * A simple date format used in this class.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DateWidget.VALID_DATE_FORMAT);

	private DateTime datePicker;

	private Shell datePickerShell;

	/**
	 * @param parent
	 * @param style
	 * @param labelText
	 * @param regex
	 */
	public DateWidget(Composite parent, int style, final String labelText, RegexValidation regex) {
		super(parent, style, labelText, regex);

		// Add datePicker Popup Button (as Label)
		Label datePickerPopupButton = new Label(this, SWT.FLAT);
		Image image = ArchieActivator.getInstance().getImageRegistry().get(ArchieActivator.IMG_BUTTON_CALENDAR);
		datePickerPopupButton.setImage(image);
		datePickerPopupButton.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent event) {
				DateWidget.this.popUpCalendar();
			}

			public void mouseDoubleClick(MouseEvent e) {
				// Nothing here. Move along.
			}

			public void mouseUp(MouseEvent e) {
				// Nothing here. Move along.
			}
		});

		// Init Datepicker Popup Shell
		this.datePickerShell = new Shell(this.getDisplay(), SWT.APPLICATION_MODAL);

		// Layout
		GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		this.datePickerShell.setLayout(shellLayout);

		// Add Datepicker
		this.datePicker = new DateTime(this.datePickerShell, SWT.CALENDAR);

		// Add Select Date Button
		Button button = new Button(this.datePickerShell, SWT.NONE);
		button.setText(Messages.BUTTON_DATE_SELECT);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing here. Move along...
			}

			public void mouseDown(MouseEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, DateWidget.this.datePicker.getYear());
				cal.set(Calendar.MONTH, DateWidget.this.datePicker.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, DateWidget.this.datePicker.getDay());
				DateWidget.this.smartField.setContents(DateWidget.DATE_FORMAT.format(cal.getTime()));
				DateWidget.this.popDownCalendar();
			}

			public void mouseUp(MouseEvent e) {
				// Nothing here. Move along...
			}
		});

		// Pack Datepicker Popup Shell
		this.datePickerShell.pack();

		// Allow to close datePicker shell with ESC or ENTER
		this.datePickerShell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
				case SWT.TRAVERSE_RETURN:
					DateWidget.this.popDownCalendar();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
				}
			}
		});

		this.datePicker.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing here. Move along...
			}

			public void mouseDown(MouseEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, DateWidget.this.datePicker.getYear());
				cal.set(Calendar.MONTH, DateWidget.this.datePicker.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, DateWidget.this.datePicker.getDay());
				DateWidget.this.smartField.setContents(DateWidget.DATE_FORMAT.format(cal.getTime()));
			}

			public void mouseUp(MouseEvent e) {
				// Nothing here. Move along...
			}
		});

		// Create quickFix menu listener
		this.controlDecoration.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent event) {
				// no quick fix if we aren't in error state.
				if (DateWidget.this.smartField.isValid()) {
					return;
				}
				if (DateWidget.this.smartField.quickFixMenu == null) {
					DateWidget.this.smartField.quickFixMenu = DateWidget.this
							.createQuickFixMenu((SmartDateField) DateWidget.this.smartField);
				}
				DateWidget.this.smartField.quickFixMenu.setLocation(event.x, event.y);
				DateWidget.this.smartField.quickFixMenu.setVisible(true);
			}
		});
	}

	/**
	 * Custom layout creation as the date picker has three columns.
	 */
	@Override
	protected void createLayout() {
		// Create layout.
		this.layout = new GridLayout();
		this.layout.numColumns = 3;
		this.layout.marginWidth = 2;
		this.setLayout(this.layout);
	}

	/**
	 * Create a <code>SmartNumericField</code>
	 */
	@Override
	protected void createSmartField() {
		this.smartField = new SmartDateField();
	}

	protected void popUpCalendar() {
		// TODO: make sure the shell never gets displayed outside of the screen.
		this.updateCalendarPopup();
		Point pt = this.getDisplay().getCursorLocation();
		this.datePickerShell.setLocation(pt.x - this.datePicker.getSize().x, pt.y);
		this.datePickerShell.setVisible(true);
		this.datePickerShell.setFocus();

	}

	protected void popDownCalendar() {
		this.datePickerShell.setVisible(false);
	}

	/**
	 * Sets the date of the calendar popup to the current contents of the date
	 * smartfield.
	 */
	private void updateCalendarPopup() {
		// add current date from the smartfield
		if (this.smartField.isValid()) {
			Date date;
			try {
				date = DateWidget.DATE_FORMAT.parse(this.smartField.getContents());
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				this.datePicker.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	protected Menu createQuickFixMenu(final SmartDateField field) {
		Menu newMenu = new Menu(this.control);
		MenuItem item = new MenuItem(newMenu, SWT.PUSH);
		item.setText("You can set this field to the value of today's date.");
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
	 * Smart Date Field: knows if its date is formated right.
	 */
	private class SmartDateField extends SmartField {

		public SmartDateField() {
			super();
		}

		@Override
		protected String getErrorMessage() {
			String format = DateWidget.VALID_DATE_FORMAT;
			String error = NLS.bind(Messages.ERROR_DATE_FORMAT, format.toUpperCase());
			if (DateWidget.this.hasRegexValidation()) {
				error += StringUtils.SPACE + DateWidget.this.regexValidation.getMessage();
			}
			return error;
		}

		@Override
		public boolean isValid() {
			// An empty field is never valid.
			if (this.getContents().equals(StringUtils.EMPTY)) {
				return false;
			}

			Date testDate = null;

			// If the format of the string provided doesn't match the format we
			// declared in SimpleDateFormat() we will get an exception
			try {
				testDate = DateWidget.DATE_FORMAT.parse(this.getContents());
			} catch (ParseException e) {
				return false;
			}

			// Dateformat.parse will accept any date as long as it's in the
			// format we defined, it simply rolls dates over, for example,
			// december 32 becomes jan 1 and december 0 becomes november 30.
			// This statement will make sure that once the string has been
			// checked for proper formatting the date is still the date
			// that was entered, if it's not, we assume that the date is invalid
			if (!DateWidget.DATE_FORMAT.format(testDate).equals(this.getContents())) {
				return false;
			}

			// Check for possible regex validation for dates
			if (DateWidget.this.hasRegexValidation()
					&& !this.getContents().matches(DateWidget.this.regexValidation.getPattern())) {
				return false;
			}

			return true;
		}

		/**
		 * @see ch.unibe.iam.scg.archie.ui.widgets.TextWidget.SmartField#hasQuickFix()
		 */
		@Override
		public boolean hasQuickFix() {
			return true;
		}

		/**
		 * @see ch.unibe.iam.scg.archie.ui.widgets.TextWidget.SmartField#getQuickfixMessage()
		 */
		@Override
		protected String getQuickfixMessage() {
			return this.getErrorMessage();
		}

		/**
		 * Sets the date field contents to the value of today's date.
		 */
		protected void quickFix() {
			Calendar cal = Calendar.getInstance();
			this.setContents(DateWidget.DATE_FORMAT.format(cal.getTime()));
		}
	}
}
