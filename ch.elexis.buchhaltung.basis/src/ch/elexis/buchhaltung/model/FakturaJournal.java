/*******************************************************************************
 * Copyright (c) 2008, G. Weirich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.buchhaltung.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.LoggerFactory;

import ch.elexis.buchhaltung.util.DateTool;
import ch.elexis.buchhaltung.util.PatientIdFormatter;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * An AbstractDataProvider that counts all bills,payments and stornos in a given period of time.
 * 
 * @author gerry
 * 
 */
public class FakturaJournal extends AbstractTimeSeries {
	private static final String NAME = Messages.FakturaJournal_FakturaJournal;
	private boolean bOnlyActiveMandator;
	
	public FakturaJournal(){
		super(NAME);
	}
	
	@GetProperty(name = "Nur aktueller Mandant", widgetType = WidgetTypes.BUTTON_CHECKBOX, index = 1)
	public boolean getOnlyActiveMandator(){
		return bOnlyActiveMandator;
	}
	
	@SetProperty(name = "Nur aktueller Mandant", index = 1)
	public void setOnlyActiveMandator(boolean val){
		bOnlyActiveMandator = val;
	}
	
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		int total = 10000000;
		Query<AccountTransaction> qbe = new Query<AccountTransaction>(AccountTransaction.class);
		TimeTool ttStart = new TimeTool(this.getStartDate().getTimeInMillis());
		TimeTool ttEnd = new TimeTool(this.getEndDate().getTimeInMillis());
		qbe.add(AccountTransaction.FLD_DATE, Query.GREATER_OR_EQUAL,
			ttStart.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add(AccountTransaction.FLD_DATE, Query.LESS_OR_EQUAL,
			ttEnd.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		monitor.beginTask(NAME, total);
		monitor.subTask(Messages.FakturaJournal_DatabaseQuery);
		List<AccountTransaction> transactions = qbe.execute();
		int sum = transactions.size();
		final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
		if (sum == 0) {
			monitor.done();
			this.dataSet.setContent(result);
			return Status.OK_STATUS;
		}
		int step = total / sum;
		monitor.worked(20 * step);
		PatientIdFormatter pif = new PatientIdFormatter(8);
		String actMnId = CoreHub.actMandant.getId();
		long time = System.currentTimeMillis();
		for (AccountTransaction at : transactions) {
			Patient pat = at.getPatient();
			if (pat != null) {
				if (bOnlyActiveMandator) {
					Rechnung rn = at.getRechnung();
					if (rn == null) {
						continue;
					}
					Mandant mn = rn.getMandant();
					if (mn != null) {
						if (!mn.getId().equals(actMnId)) {
							continue;
						}
					}
				}
				Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
				row[0] = pif.format(pat.get(Patient.FLD_PATID));
				row[1] = new DateTool(at.getDate());
				row[2] = at.getAmount();
				row[4] = at.getRemark();
				if (((Money) row[2]).isNegative()) {
					row[3] = Messages.FakturaJournal_FA;
				} else {
					if (((String) row[4]).toLowerCase().contains("storno")) { //$NON-NLS-1$
						row[3] = Messages.FakturaJournal_ST;
					} else {
						row[3] = Messages.FakturaJournal_GU;
					}
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				result.add(row);
			}
			monitor.worked(step);
		}
		
		LoggerFactory.getLogger(FakturaJournal.class)
			.debug("calculation of account transactions size: " + transactions.size() + " took "
				+ Long.valueOf((System.currentTimeMillis() - time) / 1000) + " seconds.");
		
		// Set content.
		this.dataSet.setContent(result);
		
		// Job finished successfully
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	@Override
	protected List<String> createHeadings(){
		List<String> ret = new ArrayList<String>();
		ret.add(Messages.FakturaJournal_PatientNr);
		ret.add(Messages.FakturaJournal_Date);
		ret.add(Messages.FakturaJournal_Amount);
		ret.add(Messages.FakturaJournal_Type);
		ret.add(Messages.FakturaJournal_Text);
		return ret;
	}
	
	@Override
	public String getDescription(){
		
		return Messages.FakturaJournal_Faktura;
	}
	
}
