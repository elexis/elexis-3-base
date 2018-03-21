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
package ch.unibe.iam.scg.archie.samples;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.samples.i18n.Messages;
import ch.unibe.iam.scg.archie.samples.widgets.CustomComboWidget;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Simple Prescriptions Overview
 * </p>
 * 
 * $Id: PrescriptionsOverview.java 766 2009-07-24 11:28:14Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 766 $
 */
public class PrescriptionsOverview extends AbstractTimeSeries {

	private static final String DATE_DB_FORMAT = "yyyyMMdd";
	private static final String DATE_PRESCRIPTION_FORMAT = "dd.MM.yyyy";
	private static final String DB_START_DATE = "DatumVon";
	private static final String DB_END_DATE = "DatumBis";
	
	private String comboValue;
	private String customComboValue;

	/**
	 * Constructs Prescription Overview
	 */
	public PrescriptionsOverview() {
		super(Messages.PRESCRIPTIONS_OVERVIEW_TITLE);
		
		this.comboValue = "Two";
		this.customComboValue = CustomComboWidget.DEFAULT_SELECTED;
	}

	/**
	 * @see ch.unibe.iam.scg.archie.model.AbstractDataProvider#createHeadings()
	 */
	@Override
	protected List<String> createHeadings() {
		final ArrayList<String> headings = new ArrayList<String>(3);
		headings.add(Messages.PRESCRIPTIONS_OVERVIEW_HEADING_NAME);
		headings.add(Messages.PRESCRIPTIONS_OVERVIEW_HEADING_COUNT);
		headings.add(Messages.PRESCRIPTIONS_OVERVIEW_HEADING_AVG_TIME);
		return headings;
	}

	/**
	 * @see ch.unibe.iam.scg.archie.model.AbstractDataProvider#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.PRESCRIPTIONS_OVERVIEW_DESCRIPTION;
	}

	/** {@inheritDoc} */
	@Override
	public IStatus createContent(IProgressMonitor monitor) {
		// initialize list
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>();

		// query settings
		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PRESCRIPTION_FORMAT);

		Query<Prescription> query = new Query<Prescription>(Prescription.class);
		query.add(DB_END_DATE, ">=", databaseFormat.format(this.getStartDate().getTime()));
		query.add(DB_START_DATE, "<=", databaseFormat.format(this.getEndDate().getTime()));
		List<Prescription> prescriptions = query.execute();

		// set job size and begin task
		int size = prescriptions.size() * 2; // Double size because we have two loops.
		monitor.beginTask(Messages.CALCULATING, size); // monitor

		TreeMap<String, List<Prescription>> prescriptionCount = new TreeMap<String, List<Prescription>>();

		// group prescriptions by count
		monitor.subTask("Grouping Prescriptions");
		for (Prescription prescription : prescriptions) {
			// check for cancelation
			if(monitor.isCanceled()) return Status.CANCEL_STATUS;
			
			String key = prescription.getArtikel().getLabel();
			if (!prescriptionCount.containsKey(key)) {
				ArrayList<Prescription> prescriptionList = new ArrayList<Prescription>();
				prescriptionList.add(prescription);
				prescriptionCount.put(key, prescriptionList);
			} else {
				List<Prescription> prescritionList = prescriptionCount.get(key);
				prescritionList.add(prescription);
			}
			monitor.worked(1); // monitoring
		}

		// compute prescription stats in grouped list
		monitor.subTask("Computing Results");
		for (final Entry<String, List<Prescription>> entry : prescriptionCount.entrySet()) {
			// check for cancellation
			if(monitor.isCanceled()) return Status.CANCEL_STATUS;
			
			final Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
			row[0] = entry.getKey();
			row[1] = entry.getValue().size();

			long startDate = 0;
			long endDate = 0;
			long durationInMiliSeconds = 0;

			// Get Average Time of Prescription
			for (final Prescription prescription : entry.getValue()) {
				try {
					startDate = (dateFormat.parse(prescription.getBeginDate())).getTime();
					endDate = (dateFormat.parse(prescription.getEndDate())).getTime();
					durationInMiliSeconds += (endDate - startDate);

				} catch (ParseException e) {
					e.printStackTrace();
				}
				monitor.worked(1); // monitoring
			}

			durationInMiliSeconds /= entry.getValue().size();

			row[2] = (durationInMiliSeconds / (24 * 60 * 60 * 1000)) + " days";

			content.add(row);
		}

		// set content
		this.dataSet.setContent(content);

		// job finished successfully
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	/**
	 * @return Value of the combo item set.
	 */
	@GetProperty(name = "Combo Test", index = 10, widgetType = WidgetTypes.COMBO, description = "Testing combo boxes.", items = {"One", "Two", "Three"})
	public String getComboValue() {
		return this.comboValue;
	}

	/**
	 * @param Sets the combo value.
	 */
	@SetProperty(name = "Combo Test")
	public void setComboValue(final String comboValue) {
		this.comboValue = comboValue;
	}
	
	/**
	 * @return Value of the combo item set.
	 */
	@GetProperty(name = "Custom Combo Test", index = 11, widgetType = WidgetTypes.VENDOR, description = "Testing custom combos.", vendorClass = CustomComboWidget.class)
	public String getCustomComboValue() {
		return this.customComboValue;
	}

	/**
	 * @param Sets the combo value.
	 */
	@SetProperty(name = "Custom Combo Test")
	public void setCustomComboValue(final String comboValue) {
		this.customComboValue = comboValue;
	}
}