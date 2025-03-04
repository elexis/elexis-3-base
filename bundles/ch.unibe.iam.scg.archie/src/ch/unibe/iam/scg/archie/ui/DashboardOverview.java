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
package ch.unibe.iam.scg.archie.ui;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ch.elexis.data.Person;
import ch.elexis.data.RnStatus;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.utils.DatabaseHelper;

/**
 * <p>
 * Dashboard overview is the dashboard description panel on the top side of the
 * entire dashboard. Contains some basic welcome message and some general data
 * about the system (simple gender overview of users in the system and
 * more).<br>
 * <br>
 * The overview panel also contains two buttons, one to start the creation of
 * the charts, the other to recreate them once available.
 * </p>
 *
 * $Id: DashboardOverview.java 748 2009-07-23 09:44:49Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 748 $
 */
public class DashboardOverview extends Composite {

	private Label patients;
	private Label invoices;
	private Label consultations;

	/**
	 * Public constructor.
	 *
	 * @param parent Parent composite.
	 * @param style  SWT control style.
	 */
	public DashboardOverview(final Composite parent, final int style) {
		super(parent, style);
		this.setLayout(new GridLayout());

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		Group overview = new Group(this, SWT.NONE);
		overview.setText("Statistics");
		overview.setLayout(layout);
		overview.setLayoutData(layoutData);

		this.createDescriptionPanel(overview);
		this.createStatsPanel(overview);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Triggers a refresh of this dashboard overview. The values update with the
	 * latest values from the database.
	 */
	public void refresh() {
		this.setValues();
	}

	// ////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates the description panel for this dashboard overview. This is the left
	 * hand side of the overview, containing the buttons that control the chart
	 * generation.
	 *
	 * @param parent Parent composite.
	 * @return Composite containing the created controls.
	 */
	private Composite createDescriptionPanel(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		container.setLayout(layout);
		container.setLayoutData(layoutData);

		Label introduction = new Label(container, SWT.NONE | SWT.WRAP);
		introduction.setText(Messages.DASHBOARD_WELCOME);
		introduction.setLayoutData(layoutData);

		return container;
	}

	/**
	 * Creates the statistics panel in this dashboard overview. This is the right
	 * hand side of the overview, containing some statistical data about the system.
	 *
	 * @param parent Parent composite.
	 * @return Composite containing the created controls.
	 */
	private Composite createStatsPanel(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		container.setLayout(layout);
		container.setLayoutData(layoutData);

		// Create labels
		this.patients = new Label(container, SWT.NONE | SWT.WRAP);
		this.invoices = new Label(container, SWT.NONE | SWT.WRAP);
		this.consultations = new Label(container, SWT.NONE | SWT.WRAP);
		this.consultations.setLayoutData(layoutData);

		// Set label values
		this.setValues();

		return container;
	}

	/**
	 * Sets the values of all labels based on the values in the dabase.
	 */
	private void setValues() {
		// Stats data
		int patientsTotal = DatabaseHelper.getNumberOfPatients();
		int patientsMale = DatabaseHelper.getNumberGenderPatients(Person.MALE);
		int patientsFemale = DatabaseHelper.getNumberGenderPatients(Person.FEMALE);

		int invoicesTotal = DatabaseHelper.getTotalNumberOfInvoices();
		int invoicesPaid = DatabaseHelper.getNumberOfInvoices(RnStatus.BEZAHLT);
		int invoicesOpen = DatabaseHelper.getNumberOfInvoices(RnStatus.OFFEN)
				+ DatabaseHelper.getNumberOfInvoices(RnStatus.OFFEN_UND_GEDRUCKT);

		int consultationsTotal = DatabaseHelper.getNumberOfConsultations();

		this.patients.setText(Messages.PATIENTS + ": " + patientsTotal + StringUtils.LF + Messages.MALE + ": " //$NON-NLS-1$ //$NON-NLS-2$
				+ writePercent(patientsMale, patientsTotal) + StringUtils.LF + Messages.FEMALE + ": " //$NON-NLS-1$
				+ writePercent(patientsFemale, patientsTotal) + StringUtils.LF + Messages.UNKNOWN + ": " //$NON-NLS-1$
				+ writePercent(patientsTotal - patientsFemale - patientsMale, patientsTotal));

		this.invoices.setText(Messages.INVOICES + ": " + invoicesTotal + StringUtils.LF + Messages.PAID + ": " //$NON-NLS-1$ //$NON-NLS-2$
				+ writePercent(invoicesPaid, invoicesTotal) + StringUtils.LF + Messages.OPEN + ": " //$NON-NLS-1$
				+ writePercent(invoicesOpen, invoicesTotal) + StringUtils.LF + Messages.OTHER + ": " //$NON-NLS-1$
				+ writePercent(invoicesTotal - invoicesOpen - invoicesPaid, invoicesTotal));

		this.consultations.setText(Messages.CONSULTATIONS + ": " + consultationsTotal + StringUtils.LF); //$NON-NLS-1$
	}

	/**
	 * Calculates the percent value from two given amounts.
	 *
	 * @param givenAmount Amount given.
	 * @param totalAmount Total amount.
	 * @return float How much percent is givenAmount of totalAmount
	 */
	private float calculatePercent(final float givenAmount, final float totalAmount) {
		if (totalAmount <= 0) {
			return 0;
		}
		return (givenAmount / totalAmount) * 100;
	}

	/**
	 * Writes the percent value from two given amounts.
	 *
	 * @param givenAmount Amount given.
	 * @param totalAmount Total amount.
	 * @return String How much percent is givenAmount of totalAmount, written as
	 *         string containing the % sign.
	 */
	private String writePercent(final float givenAmount, final float totalAmount) {
		BigDecimal percent = new BigDecimal(calculatePercent(givenAmount, totalAmount));
		percent = percent.setScale(1, BigDecimal.ROUND_HALF_UP);
		return percent.doubleValue() + " %"; //$NON-NLS-1$
	}
}