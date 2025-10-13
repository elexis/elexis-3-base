/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.buchhaltung.util.PatientIdFormatter;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.status.ElexisStatus;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;

public class AlleLeistungen extends AbstractTimeSeries {
	private static final String NAME = Messages.AlleLeistungen_Title;

	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //$NON-NLS-1$

	private boolean hasUserId;

	public AlleLeistungen() {
		super(NAME);
		VersionInfo elexisVersion = new VersionInfo(CoreHub.Version);
		hasUserId = !elexisVersion.isOlder("2.1.7"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.AlleLeistungen_Description;
	}

	@Override
	protected List<String> createHeadings() {
		List<String> ret = new ArrayList<String>();
		ret.add(Messages.AlleLeistungen_InvoicingParty);
		ret.add(Messages.AlleLeistungen_Mandator);
		if (hasUserId) { // $NON-NLS-1$
			ret.add(Messages.AlleLeistungen_User);
		}
		ret.add(Messages.AlleLeistungen_Doctor);
		ret.add(Messages.AlleLeistungen_TreatmentDate);
		ret.add(Messages.AlleLeistungen_PatientName);
		ret.add(Messages.AlleLeistungen_PatientFirstname);
		ret.add(Messages.AlleLeistungen_PatientId);
		ret.add(Messages.AlleLeistungen_PatientDateOfBirth);
		ret.add(Messages.AlleLeistungen_PatientSex);
		ret.add(Messages.AlleLeistungen_PatientZip);
		ret.add(Messages.AlleLeistungen_PatientCity);
		ret.add(Messages.AlleLeistungen_ActivityText);
		ret.add(Messages.AlleLeistungen_TarmedCode);
		ret.add(Messages.AlleLeistungen_TarmedAL);
		ret.add(Messages.AlleLeistungen_TarmedTL);
		ret.add(Messages.AlleLeistungen_TariffType);
		ret.add(Messages.AlleLeistungen_TaxPointValue);
		ret.add(Messages.AlleLeistungen_Quantity);
		ret.add(Messages.AlleLeistungen_PurchaseCosts);
		ret.add(Messages.AlleLeistungen_SaleCosts);
		ret.add(Messages.AlleLeistungen_Sales);
		ret.add(Messages.AlleLeistungen_VAT);
		ret.add(Messages.AlleLeistungen_BillState);
		return ret;
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		int total = 10000000;
		IQuery<IEncounter> query = CoreModelServiceHolder.get().getQuery(IEncounter.class);
		query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.GREATER_OR_EQUAL,
				new TimeTool(this.getStartDate().getTimeInMillis()).toLocalDate());
		query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.LESS_OR_EQUAL,
				new TimeTool(this.getEndDate().getTimeInMillis()).toLocalDate());
		monitor.beginTask(NAME, total);
		monitor.subTask(Messages.FakturaJournal_DatabaseQuery);
		List<IEncounter> consultations = query.execute();
		int sum = consultations.size();
		final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
		if (sum == 0) {
			monitor.done();
			this.dataSet.setContent(result);
			return Status.OK_STATUS;
		}
		int step = total / sum;
		monitor.worked(20 * step);
		PatientIdFormatter pif = new PatientIdFormatter(8);
		long time = System.currentTimeMillis();
		for (IEncounter cons : consultations) {
			IPatient patient = cons.getPatient();
			IMandator mandant = cons.getMandator();

			IInvoice consInvoice = cons.getInvoice();
			InvoiceState consInvoiceState = cons.getInvoiceState();
			String billState = (consInvoice != null ? "RG " + consInvoice.getNumber() + ": " : StringUtils.EMPTY) //$NON-NLS-1$ //$NON-NLS-2$
					+ consInvoiceState.getLocaleText();
			if (consInvoiceState.numericValue() >= InvoiceState.FROM_TODAY.numericValue()
					&& consInvoiceState.numericValue() <= InvoiceState.NOT_FROM_YOU.numericValue())
				billState = Messages.AlleLeistungen_NoBill;

			List<IBilled> activities = cons.getBilled();
			if (mandant != null && patient != null && activities != null && !activities.isEmpty()) {
				for (IBilled verrechnet : activities) {
					IBillable verrechenbar = verrechnet.getBillable();
					Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
					int index = 0;
					row[index++] = mandant.getBiller().getLabel();
					String desc = mandant.getDescription3();
					row[index++] = mandant.getLabel() + (desc != null ? " (" + desc + ")" : "");

					if (hasUserId) { // $NON-NLS-1$
						IContact user = verrechnet.getBiller();
						if (user != null)
							row[index++] = user.getLabel();
						else
							row[index++] = StringUtils.EMPTY;
					}
					IContact familyDoctor = patient.getFamilyDoctor();
					row[index++] = familyDoctor != null ? familyDoctor.getLabel() : StringUtils.EMPTY;
					row[index++] = cons.getDate().format(dateFormat);
					row[index++] = patient.getLastName();
					row[index++] = patient.getFirstName();
					row[index++] = pif.format(patient.getPatientNr());
					row[index++] = getGeburtsDatum(patient);
					row[index++] = patient.getGender();
					row[index++] = patient.getZip();
					row[index++] = patient.getCity();
					row[index++] = verrechnet.getText();

					if (verrechenbar != null) {
						try {
							row[index++] = verrechenbar.getCode() == null ? "?" : verrechenbar.getCode(); //$NON-NLS-1$
							if (verrechenbar instanceof ITarmedLeistung)
								row[index++] = Double
										.toString(((double) ((ITarmedLeistung) verrechenbar).getAL()) / 100);
							else
								row[index++] = StringUtils.EMPTY;
							if (verrechenbar instanceof ITarmedLeistung)
								row[index++] = Double
										.toString(((double) ((ITarmedLeistung) verrechenbar).getTL()) / 100);
							else
								row[index++] = StringUtils.EMPTY;
						} catch (NoClassDefFoundError error) {
							ElexisStatus status = new ElexisStatus(ElexisStatus.ERROR, "ch.elexis.buchhaltung.basis", //$NON-NLS-1$
									ElexisStatus.CODE_NOFEEDBACK, Messages.AlleLeistungen_TarmedMissing,
									ElexisStatus.LOG_FATALS);
							StatusManager.getManager().handle(status, StatusManager.SHOW);
							return Status.CANCEL_STATUS;
						}
						row[index++] = verrechenbar.getCodeSystemName();
					} else {
						row[index++] = StringUtils.EMPTY;
						row[index++] = StringUtils.EMPTY;
						row[index++] = StringUtils.EMPTY;
						row[index++] = StringUtils.EMPTY;
					}
					row[index++] = verrechnet.getFactor();
					// include partial quantity info in secondary scale
					row[index++] = verrechnet.getAmount();
					row[index++] = verrechnet.getNetPrice(); // verrechnet.getKosten();
					row[index++] = StringUtils.EMPTY; // verrechnet.getEffPreis();
					row[index++] = verrechnet.getTotal().getAmount();
					row[index++] = getVatScale(verrechnet);

					row[index++] = billState;

					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					result.add(row);
				}
			}
			monitor.worked(step);
		}

		LoggerFactory.getLogger(AlleLeistungen.class).debug("calculation of konsultations size: " + consultations.size() //$NON-NLS-1$
				+ " took " + Long.valueOf((System.currentTimeMillis() - time) / 1000) + " seconds."); //$NON-NLS-1$ //$NON-NLS-2$

		// Set content.
		this.dataSet.setContent(result);

		// Job finished successfully
		monitor.done();

		return Status.OK_STATUS;
	}

	private String getVatScale(IBilled verrechnet) {
		String scale = (String) verrechnet.getExtInfo(Verrechnet.VATSCALE);
		if (scale != null)
			return scale;
		else
			return "0.0"; //$NON-NLS-1$
	}

	private int getVatInfoCode(IBilled verrechnet) {
		String scale = getVatScale(verrechnet);
		if (scale != null)
			return guessVatCode(scale);
		else
			return 0;
	}

	private int guessVatCode(String vatRate) {
		if (vatRate != null && !vatRate.isEmpty()) {
			double scale = Double.parseDouble(vatRate);
			// make a guess for the correct code
			if (scale == 0)
				return 0;
			else if (scale < 7)
				return 2;
			else
				return 1;
		}
		return 0;
	}

	private String getGeburtsDatum(IPatient patient) {
		LocalDateTime dob = patient.getDateOfBirth();
		return dob != null ? dob.format(dateFormat) : "";
	}
}
