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
package ch.unibe.iam.scg.archie.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.osgi.util.NLS;

import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.ui.widgets.DateWidget;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Can be used by any statistic that needs a time span defined.
 * </p>
 *
 * $Id: AbstractTimeSeries.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public abstract class AbstractTimeSeries extends AbstractDataProvider {

	/**
	 * The start date of the time span we are interested in.
	 */
	private Calendar startDate;

	/**
	 * The end date of the time span we are interested in.
	 */
	private Calendar endDate;

	/**
	 * @param name
	 */
	public AbstractTimeSeries(final String name) {
		super(name);
		this.initDates();
	}

	private void initDates() {
		this.setStartDate(Calendar.getInstance());
		this.getStartDate().set(this.getStartDate().get(Calendar.YEAR), Calendar.JANUARY, 1);

		this.setEndDate(Calendar.getInstance());
		this.getEndDate().set(this.getEndDate().get(Calendar.YEAR), Calendar.DECEMBER, 31);
	}

	/**
	 * @param startDate
	 */
	public void setStartDate(final Calendar startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Calendar
	 */
	public Calendar getStartDate() {
		return this.startDate;
	}

	/**
	 * @param endDate
	 */
	public void setEndDate(final Calendar endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return Calendar
	 */
	public Calendar getEndDate() {
		return this.endDate;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// ANNOTATION METHODS
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the start date property of this data provider, formatted according to
	 * the valid date format in <code>DateWidget</code> class.
	 *
	 * @return The start date of this query.
	 */
	@GetProperty(name = "Start Date", index = -2, widgetType = WidgetTypes.TEXT_DATE, validationRegex = "\\d{2}\\.\\d{2}\\.\\d{4}", validationMessage = "The date needs to have the following format: "
			+ DateWidget.VALID_DATE_FORMAT)
	public String metaGetStartDate() {
		SimpleDateFormat format = new SimpleDateFormat(DateWidget.VALID_DATE_FORMAT);
		return format.format(this.getStartDate().getTime());
	}

	/**
	 * Set the start date of this query. Inclusive the given date. Consult the
	 * <code>DateWidget</code> class for valid date format.
	 *
	 * @param startDate Start date as string in a valid date format.
	 * @throws SetDataException Exception thrown when the date could not be set.
	 * @see DateWidget#VALID_DATE_FORMAT
	 */
	@SetProperty(name = "Start Date", index = -2)
	public void metaSetStartDate(String startDate) throws SetDataException {
		Calendar cal;
		try {
			SimpleDateFormat format = new SimpleDateFormat(DateWidget.VALID_DATE_FORMAT);
			Date date = format.parse(startDate);
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.get(Calendar.DAY_OF_MONTH); // these throw IllegalArgument...
			cal.get(Calendar.MONTH);
			cal.get(Calendar.YEAR);
		} catch (ParseException e) { // converting failure
			throw new SetDataException(NLS.bind(Messages.ERROR_SET_START_DATE, DateWidget.VALID_DATE_FORMAT));
		} catch (IllegalArgumentException e) { // illegal date
			throw new SetDataException(Messages.ERROR_START_DATE_VALID);
		}
		this.setStartDate(cal);
	}

	/**
	 * Returns the end date for this data provider.
	 *
	 * @return The end date of this data provider.
	 */
	@GetProperty(name = "End Date", widgetType = WidgetTypes.TEXT_DATE, validationRegex = "\\d{2}\\.\\d{2}\\.\\d{4}", validationMessage = "Datumsformat blubb...")
	public String metaGetEndDate() {
		SimpleDateFormat format = new SimpleDateFormat(DateWidget.VALID_DATE_FORMAT);
		return format.format(this.getEndDate().getTime());
	}

	/**
	 * Set the end date of this query. Inclusive the given date. Consult the
	 * <code>DateWidget</code> class for valid date format.
	 *
	 * @param endDate End date as string in a valid date format.
	 * @throws SetDataException Start date as string in a valid date format.
	 * @see DateWidget#VALID_DATE_FORMAT
	 */
	@SetProperty(name = "End Date")
	public void metaSetEndDate(final String endDate) throws SetDataException {
		Calendar cal;
		try {
			SimpleDateFormat format = new SimpleDateFormat(DateWidget.VALID_DATE_FORMAT);
			Date date = format.parse(endDate);
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.get(Calendar.DAY_OF_MONTH); // these throw IllegalArgument...
			cal.get(Calendar.MONTH);
			cal.get(Calendar.YEAR);
		} catch (ParseException e) { // converting failure
			throw new SetDataException(NLS.bind(Messages.ERROR_SET_END_DATE, DateWidget.VALID_DATE_FORMAT));
		} catch (IllegalArgumentException e) { // illegal date
			throw new SetDataException(Messages.ERROR_END_DATE_VALID);
		}
		if (cal.compareTo(this.getStartDate()) < 0) {
			throw new SetDataException(Messages.ERROR_DATE_DIFFERENCE);
		}
		this.setEndDate(cal);
	}

}
