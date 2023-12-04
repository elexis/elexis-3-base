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

import ch.elexis.buchhaltung.util.DateTool;
import ch.elexis.buchhaltung.util.PatientIdFormatter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

public class ZahlungsJournal extends AbstractTimeSeries {
	private static final String NAME = Messages.ZahlungsJournal_PaymentJournal;
	private static final String FIELD_ACTMANDATOR = "Nur aktueller Mandant"; //$NON-NLS-1$
	private boolean bOnlyActiveMandator;

	public ZahlungsJournal() {
		super(NAME);
	}

	@GetProperty(name = FIELD_ACTMANDATOR, widgetType = WidgetTypes.BUTTON_CHECKBOX, index = 1)
	public boolean getOnlyActiveMandator() {
		return bOnlyActiveMandator;
	}

	@SetProperty(name = FIELD_ACTMANDATOR, index = 1)
	public void setOnlyActiveMandator(boolean val) {
		bOnlyActiveMandator = val;
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		int total = 10000000;
		Query<AccountTransaction> qbe = new Query<AccountTransaction>(AccountTransaction.class);
		TimeTool ttStart = new TimeTool(this.getStartDate().getTimeInMillis());
		TimeTool ttEnd = new TimeTool(this.getEndDate().getTimeInMillis());
		qbe.add("Datum", ">=", ttStart.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("Datum", "<=", ttEnd.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		monitor.beginTask(NAME, total);
		monitor.subTask(Messages.ZahlungsJournal_DatabaseQuery);
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
		String actMnId = ContextServiceHolder.getActiveMandatorOrNull().getId();
		for (AccountTransaction at : transactions) {
			Patient pat = at.getPatient();
			Money amount = at.getAmount();
			if ((amount == null) || (amount.isNegative())) {
				continue;
			}
			String remark = at.getRemark();
			if (remark.toLowerCase().contains("storno")) { //$NON-NLS-1$
				continue;
			}
			if (pat != null) {
				if (bOnlyActiveMandator) {
					Rechnung rn = at.getRechnung();
					if (rn == null) {
						continue;
					}
					if (!actMnId.equals(rn.get("MandantID"))) { //$NON-NLS-1$
						continue;
					}
				}
				Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
				row[0] = pif.format(pat.get("PatientNr")); //$NON-NLS-1$
				row[1] = new DateTool(at.getDate());
				row[2] = at.getAmount();
				row[4] = at.getRemark();
				Rechnung rn = at.getRechnung();
				if (rn != null) {
					Money rnAmount = rn.getBetrag();
					if (rnAmount.isMoreThan(amount)) {
						row[3] = Messages.ZahlungsJournal_TZ;
					} else {
						row[3] = Messages.ZahlungsJournal_ZA;
					}

				} else {
					row[3] = Messages.ZahlungsJournal_AD;
				}

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				result.add(row);
			}
			monitor.worked(step);
		}

		// Set content.
		this.dataSet.setContent(result);

		// Job finished successfully
		monitor.done();

		return Status.OK_STATUS;
	}

	@Override
	protected List<String> createHeadings() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(Messages.ZahlungsJournal_PatientNr);
		ret.add(Messages.ZahlungsJournal_Date);
		ret.add(Messages.ZahlungsJournal_Amount);
		ret.add(Messages.ZahlungsJournal_Type);
		ret.add(Messages.ZahlungsJournal_Text);
		return ret;
	}

	@Override
	public String getDescription() {
		return NAME;
	}

}
