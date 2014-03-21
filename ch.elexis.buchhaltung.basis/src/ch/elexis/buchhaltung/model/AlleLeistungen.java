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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.elexis.buchhaltung.util.DateTool;
import ch.elexis.buchhaltung.util.PatientIdFormatter;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.RnStatus;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;

public class AlleLeistungen extends AbstractTimeSeries {
	private static final String NAME = Messages.AlleLeistungen_Title;
	
	private boolean hasUserId;
	
	public AlleLeistungen(){
		super(NAME);
		VersionInfo elexisVersion = new VersionInfo(CoreHub.Version);
		hasUserId = !elexisVersion.isOlder("2.1.7"); //$NON-NLS-1$
	}
	
	@Override
	public String getDescription(){
		return Messages.AlleLeistungen_Description;
	}
	
	@Override
	protected List<String> createHeadings(){
		List<String> ret = new ArrayList<String>();
		ret.add(Messages.AlleLeistungen_InvoicingParty);
		ret.add(Messages.AlleLeistungen_Mandator);
		if (hasUserId) { //$NON-NLS-1$
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
	protected IStatus createContent(IProgressMonitor monitor){
		int total = 10000000;
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		TimeTool ttStart = new TimeTool(this.getStartDate().getTimeInMillis());
		TimeTool ttEnd = new TimeTool(this.getEndDate().getTimeInMillis());
		qbe.add(AccountTransaction.FLD_DATE, Query.GREATER_OR_EQUAL,
			ttStart.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add(AccountTransaction.FLD_DATE, Query.LESS_OR_EQUAL,
			ttEnd.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		monitor.beginTask(NAME, total);
		monitor.subTask(Messages.FakturaJournal_DatabaseQuery);
		List<Konsultation> consultations = qbe.execute();
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
		for (Konsultation cons : consultations) {
			Patient patient = cons.getFall().getPatient();
			Mandant mandant = cons.getMandant();
			String billState = cons.getStatusText();
			if (cons.getStatus() >= RnStatus.VON_HEUTE
				&& cons.getStatus() <= RnStatus.NICHT_VON_IHNEN)
				billState = Messages.AlleLeistungen_NoBill;
			
			List<Verrechnet> activities = cons.getLeistungen();
			if (mandant != null && patient != null && activities != null && !activities.isEmpty()) {
				for (Verrechnet verrechnet : activities) {
					IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
					Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
					int index = 0;
					row[index++] = mandant.getRechnungssteller().getLabel();
					row[index++] = mandant.getMandantLabel();
					if (hasUserId) { //$NON-NLS-1$
						String userid = verrechnet.get("userID"); //$NON-NLS-1$
						Kontakt user = Kontakt.load(userid);
						if (user.exists())
							row[index++] = user.getLabel();
						else
							row[index++] = ""; //$NON-NLS-1$
					}
					row[index++] =
						(patient.getStammarzt() != null) ? patient.getStammarzt().getLabel() : ""; //$NON-NLS-1$
					row[index++] = new DateTool(cons.getDatum());
					row[index++] = patient.getName();
					row[index++] = patient.getVorname();
					row[index++] = pif.format(patient.get(Patient.FLD_PATID));
					row[index++] = new DateTool(patient.getGeburtsdatum());
					row[index++] = patient.getGeschlecht();
					row[index++] = patient.getAnschrift().getPlz();
					row[index++] = patient.getAnschrift().getOrt();
					row[index++] = verrechnet.getText();
					
					if (verrechenbar != null) {
						try {
							row[index++] =
								verrechenbar.getCode() == null ? "?" : verrechenbar.getCode(); //$NON-NLS-1$
							if (verrechenbar instanceof TarmedLeistung)
								row[index++] =
									Double.toString(((double) ((TarmedLeistung) verrechenbar)
										.getAL()) / 100);
							else
								row[index++] = ""; //$NON-NLS-1$
							if (verrechenbar instanceof TarmedLeistung)
								row[index++] =
									Double.toString(((double) ((TarmedLeistung) verrechenbar)
										.getTL()) / 100);
							else
								row[index++] = ""; //$NON-NLS-1$
						} catch (NoClassDefFoundError error) {
							ElexisStatus status =
								new ElexisStatus(ElexisStatus.ERROR,
									"ch.elexis.buchhaltung.basis", //$NON-NLS-1$
									ElexisStatus.CODE_NOFEEDBACK,
									Messages.AlleLeistungen_TarmedMissing, ElexisStatus.LOG_FATALS);
							StatusManager.getManager().handle(status, StatusManager.SHOW);
							return Status.CANCEL_STATUS;
						}
					} else {
						row[index++] = ""; //$NON-NLS-1$
						row[index++] = ""; //$NON-NLS-1$
						row[index++] = ""; //$NON-NLS-1$
					}
					row[index++] = getClassName(verrechnet);
					row[index++] = verrechnet.getTPW();
					// include partial quantity info in secondary scale
					row[index++] = verrechnet.getZahl() * verrechnet.getSecondaryScaleFactor();
					row[index++] = verrechnet.getKosten();
					row[index++] = verrechnet.getEffPreis();
					row[index++] = getSales(verrechnet);
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
		
		// Set content.
		this.dataSet.setContent(result);
		
		// Job finished successfully
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	private String getClassName(Verrechnet verrechnet){
		String fullname = verrechnet.get(Verrechnet.CLASS);
		if (fullname != null && !fullname.isEmpty() && fullname.lastIndexOf('.') != -1)
			return fullname.substring(fullname.lastIndexOf('.') + 1);
		return ""; //$NON-NLS-1$
	}
	
	private Money getSales(Verrechnet verrechnet){
		double vk_tp = 0.0;
		try {
			vk_tp = Double.parseDouble(verrechnet.get(Verrechnet.SCALE_TP_SELLING));
		} catch (NumberFormatException ne) {/* just leave 0.0 as value */}
		double vk_scale = 1.0;
		try {
			vk_scale = Double.parseDouble(verrechnet.get(Verrechnet.SCALE_SELLING));
		} catch (NumberFormatException ne) {/* just leave 1.0 as value */}
		double scale1 = verrechnet.getPrimaryScaleFactor();
		double scale2 = verrechnet.getSecondaryScaleFactor();
		// get sales for the verrechnet including all scales and quantity
		return new Money(
			(int) (Math.round(vk_tp * vk_scale) * scale1 * scale2 * verrechnet.getZahl()));
	}
	
	private String getVatScale(Verrechnet verrechnet){
		String scale = verrechnet.getDetail(Verrechnet.VATSCALE);
		if (scale != null)
			return scale;
		else
			return "0.0"; //$NON-NLS-1$
	}
	
	private int getVatInfoCode(Verrechnet verrechnet){
		String scale = getVatScale(verrechnet);
		if (scale != null)
			return guessVatCode(scale);
		else
			return 0;
	}
	
	private int guessVatCode(String vatRate){
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
}
