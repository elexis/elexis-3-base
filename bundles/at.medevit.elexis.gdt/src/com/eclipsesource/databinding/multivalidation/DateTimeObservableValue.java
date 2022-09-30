/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
/**
 * From http://eclipsesource.com/blogs/2009/02/03/databinding-a-custom-observable-for-your-widget/
 */
package com.eclipsesource.databinding.multivalidation;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DateTimeObservableValue extends AbstractObservableValue {

	private final DateTime dateTime;

	protected Date oldValue;

	Listener listener = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			Date newValue = dateTimeToDate();

			if (!newValue.equals(DateTimeObservableValue.this.oldValue)) {
				fireValueChange(Diffs.createValueDiff(DateTimeObservableValue.this.oldValue, newValue));
				DateTimeObservableValue.this.oldValue = newValue;

			}
		}

	};

	public DateTimeObservableValue(final DateTime dateTime) {
		this.dateTime = dateTime;
		this.dateTime.addListener(SWT.Selection, this.listener);
	}

	@Override
	protected Object doGetValue() {
		return dateTimeToDate();
	}

	@Override
	protected void doSetValue(final Object value) {
		if (value instanceof Date) {
			Date date = (Date) value;
			dateToDateTime(date);
		}
	}

	@Override
	public Object getValueType() {
		return Date.class;
	}

	private void dateToDateTime(final Date date) {
		if (!this.dateTime.isDisposed()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			this.dateTime.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

			this.dateTime.setHours(cal.get(Calendar.HOUR_OF_DAY));
			this.dateTime.setMinutes(cal.get(Calendar.MINUTE));
			this.dateTime.setSeconds(cal.get(Calendar.SECOND));
		}
	}

	private Date dateTimeToDate() {
		Date result = null;
		if (!this.dateTime.isDisposed()) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, this.dateTime.getYear());
			cal.set(Calendar.MONTH, this.dateTime.getMonth());
			cal.set(Calendar.DAY_OF_MONTH, this.dateTime.getDay());
			cal.set(Calendar.HOUR_OF_DAY, this.dateTime.getHours());
			cal.set(Calendar.MINUTE, this.dateTime.getMinutes());
			cal.set(Calendar.SECOND, this.dateTime.getSeconds());
			result = cal.getTime();
		}
		return result;
	}

	@Override
	public synchronized void dispose() {
		this.dateTime.removeListener(SWT.Selection, this.listener);
		super.dispose();
	}

}
