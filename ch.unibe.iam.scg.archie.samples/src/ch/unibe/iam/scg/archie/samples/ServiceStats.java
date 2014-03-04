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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.samples.i18n.Messages;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Generates an overview of the services provided in a given timeframe.
 * </p>
 * 
 * $Id: ServiceStats.java 766 2009-07-24 11:28:14Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 766 $
 */
public class ServiceStats extends AbstractTimeSeries {

	private static final String DATE_DB_FORMAT = "yyyyMMdd";

	private boolean currentMandatorOnly;
	private boolean groupByCodeSystem;

	/**
	 * Costructs ServiceStats
	 */
	public ServiceStats() {
		super(Messages.SERVICES_TITLE);

		this.currentMandatorOnly = true;
		this.groupByCodeSystem = false;
	}

	/** {@inheritDoc} */
	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);

		// Prepare DB query.
		final Query<Konsultation> query = new Query<Konsultation>(Konsultation.class);
		query.add("Datum", ">=", databaseFormat.format(this.getStartDate().getTime()));
		query.add("Datum", "<=", databaseFormat.format(this.getEndDate().getTime()));
		if (this.currentMandatorOnly) {
			query.add("MandantID", "=", CoreHub.actMandant.getId());
		}

		// Get all Consultation which happened in the specified date range.
		final List<Konsultation> consultations = query.execute();

		monitor.beginTask(Messages.CALCULATING, consultations.size()); // monitoring

		final HashMap<IVerrechenbar, ServiceCounter> services = new HashMap<IVerrechenbar, ServiceCounter>();

		// Go through all consultations.
		monitor.subTask("Grouping Consultations");
		for (Konsultation consultation : consultations) {

			// Check for cancellation.
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			// Go through all services.
			List<Verrechnet> consServices = consultation.getLeistungen();
			for (Verrechnet service : consServices) {

				// Check for cancellation.
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				IVerrechenbar serviceBase = service.getVerrechenbar();
				ServiceCounter counter = services.get(serviceBase);
				if (counter == null) {
					counter = new ServiceCounter(service);
					services.put(serviceBase, counter);
				} else {
					counter.add(service);
				}

			}
			monitor.worked(1);
		}

		// Create dataset result
		final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
		final ArrayList<ServiceCounter> counters = this.isGroupByCodeSystem() ? this.groupServiceCounters(services
				.values()) : new ArrayList<ServiceCounter>(services.values());

		// sort the counters
		Collections.sort(counters);

		// Go over all services we stored and create actual dataset.
		monitor.subTask("Computing Results");
		for (ServiceCounter counter : counters) {

			// Check for cancellation
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			final Comparable<?>[] row = new Comparable[this.dataSet.getHeadings().size()];
			int i = 0;

			row[i++] = counter.getVerrechenbar().getCodeSystemName();
			// add label if we don't group by code system
			if (!this.isGroupByCodeSystem()) {
				row[i++] = counter.getService().getLabel();
			}
			row[i++] = counter.getServiceCount();
			row[i++] = new Money(counter.getCost());
			row[i++] = new Money(counter.getIncome());
			row[i++] = new Money(counter.getIncome().subtractMoney(counter.getCost()));

			result.add(row);
		}

		// Set content.
		this.dataSet.setContent(result);

		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * This methods groups the given collection containing
	 * <code>ServiceCounter</code> objects by the underlying code system name
	 * their service objects belong to.
	 * 
	 * @return An <code>ArrayList</code> containing the grouped service
	 *         counters.
	 */
	private ArrayList<ServiceCounter> groupServiceCounters(Collection<ServiceCounter> counters) {
		TreeMap<String, ServiceCounter> groupedCounters = new TreeMap<String, ServiceCounter>();
		for (ServiceCounter counter : counters) {
			String codeSystem = counter.getVerrechenbar().getCodeSystemName();
			ServiceCounter groupedCounter = groupedCounters.get(codeSystem);
			// if there's no counter with that code system name
			if (groupedCounter == null) {
				groupedCounters.put(codeSystem, counter);
			} else {
				// else we start summing up
				groupedCounter.add(counter);
			}
		}

		return new ArrayList<ServiceCounter>(groupedCounters.values());
	}

	// /////////////////////////////////////////////////////////////////////////////
	// ANNOTATION METHODS
	// /////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	protected List<String> createHeadings() {
		final ArrayList<String> headings = new ArrayList<String>(2);
		headings.add(Messages.SERVICES_HEADING_CODESYSTEM);
		// if we don't group by code system, add service name
		if (!this.isGroupByCodeSystem()) {
			headings.add(Messages.SERVICES_HEADING_SERVICE);
		}
		headings.add(Messages.SERVICES_HEADING_AMOUNT);
		headings.add(Messages.SERVICES_HEADING_COSTS);
		headings.add(Messages.SERVICES_HEADING_INCOME);
		headings.add(Messages.SERVICES_HEADING_PROFITS);
		return headings;
	}

	/** {@inheritDoc} */
	@Override
	public String getDescription() {
		return Messages.SERVICES_DESCRIPTION;
	}

	/**
	 * @return currentMandatorOnly
	 */
	@GetProperty(name = "Active Mandator Only", index = 3, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Compute statistics only for the current mandator. If unchecked, the statistics will be computed for all mandators.")
	public boolean getCurrentMandatorOnly() {
		return this.currentMandatorOnly;
	}

	/**
	 * @param currentMandatorOnly
	 */
	@SetProperty(name = "Active Mandator Only")
	public void setCurrentMandatorOnly(final boolean currentMandatorOnly) {
		this.currentMandatorOnly = currentMandatorOnly;
	}

	/**
	 * @return groupByCodeSystem
	 */
	@GetProperty(name = "Groupy By Codesystem", index = 5, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Groups services by code system.")
	public boolean isGroupByCodeSystem() {
		return this.groupByCodeSystem;
	}

	/**
	 * @param groupByCodeSystem
	 */
	@SetProperty(name = "Groupy By Codesystem")
	public void setGroupByCodeSystem(final boolean groupByCodeSystem) {
		this.groupByCodeSystem = groupByCodeSystem;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPER CLASSES
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Simple service counter class. This counter takes a given service and adds
	 * its values to the current values of this counter. It sums up the income
	 * and costs for the given service.
	 * </p>
	 * 
	 * $Id: ServiceStats.java 766 2009-07-24 11:28:14Z peschehimself $
	 * 
	 * @author Peter Siska
	 * @author Dennis Schenk
	 * @version $Rev: 766 $
	 */
	private class ServiceCounter implements Comparable<ServiceCounter> {

		private Verrechnet service;

		private Money income;
		private Money cost;

		private int totalServices;

		/**
		 * Public constructor.
		 * 
		 * @param service
		 */
		public ServiceCounter(Verrechnet service) {
			int serviceCount = service.getZahl();

			this.service = service;
			this.income = service.getNettoPreis().multiply(serviceCount);
			this.cost = service.getKosten().multiply(serviceCount);
			this.totalServices = serviceCount;
		}

		/**
		 * Compares one service counter with an other. First the service group
		 * name is compared, if that's equal, the service code is compared, if
		 * that's equal, the total income in this service counter is compared.
		 * 
		 * @param other
		 *            ServiceCounter
		 * @return int
		 */
		public int compareTo(ServiceCounter other) {
			// compare service group
			int serviceGroup = StringTool.compareWithNull(this.getVerrechenbar().getCodeSystemName(), other
					.getVerrechenbar().getCodeSystemName());
			if (serviceGroup != 0) {
				return serviceGroup;
			}

			// compare service code
			int serviceCode = StringTool.compareWithNull(this.getVerrechenbar().getCode(), other.getVerrechenbar()
					.getCode());
			if (serviceCode != 0) {
				return serviceCode;
			}

			return this.getIncome().getCents() - other.getIncome().getCents();
		}

		/**
		 * Adds a service to this counter. This means that this counter will sum
		 * up the cost and income values of the given service and the values of
		 * the counter. It also increments the internal counter for the service
		 * type.
		 * 
		 * @param service
		 */
		protected void add(Verrechnet service) {
			// increment counter
			int serviceCount = service.getZahl();
			this.totalServices += serviceCount;

			// sum up moneys
			Money totalIncome = service.getNettoPreis().multiply(serviceCount);
			Money totalCost = service.getKosten().multiply(serviceCount);
			this.cost.addMoney(totalCost);
			this.income.addMoney(totalIncome);
		}

		/**
		 * Adds a <code>ServiceCounter</code> counter object to this counter.
		 * This means suming up the values of the given counter with these of
		 * this object.
		 * 
		 * @param counter
		 */
		protected void add(ServiceCounter counter) {
			int serviceCount = counter.getServiceCount();
			this.totalServices += serviceCount;

			// sum up money values
			Money totalIncome = counter.getIncome();
			Money totalCost = counter.getCost();
			this.cost.addMoney(totalCost);
			this.income.addMoney(totalIncome);
		}

		/**
		 * Returns the
		 * <code>IVerrechenbar<code> object for the service in this counter.
		 * 
		 * @return The <code>IVerrechenbar<code> object for the service in this
		 *         counter.
		 */
		protected IVerrechenbar getVerrechenbar() {
			return this.service.getVerrechenbar();
		}

		/**
		 * Returns the service in this counter.
		 * 
		 * @return Returns the service in this counter.
		 */
		protected Verrechnet getService() {
			return this.service;
		}

		/**
		 * Returns the income.
		 * 
		 * @return Returns the income.
		 */
		protected Money getIncome() {
			return this.income;
		}

		/**
		 * Returns the cost.
		 * 
		 * @return Returns the cost.
		 */
		protected Money getCost() {
			return this.cost;
		}

		/**
		 * Returns the value of the internal service counter.
		 * 
		 * @return Total number of services for this counter.
		 */
		protected int getServiceCount() {
			return this.totalServices;
		}
	}
}