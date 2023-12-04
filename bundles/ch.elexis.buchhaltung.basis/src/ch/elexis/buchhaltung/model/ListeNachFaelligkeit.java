/*******************************************************************************
 * Copyright (c) 2008-2010 G. Weirich
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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.buchhaltung.util.DateTool;
import ch.elexis.buchhaltung.util.PatientIdFormatter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.Fall;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.SetDataException;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * An AbstractDataProvider that calculates income due at a given date
 *
 * @author gerry
 *
 */
public class ListeNachFaelligkeit extends AbstractDataProvider {
	private static final String ANALYSIERE_RECHNUNGEN = Messages.ListeNachFaelligkeit_AnalyzingBills;
	private static final String DATENBANKABFRAGE = Messages.ListeNachFaelligkeit_DatabaseQuery;
	private static final String NAME = Messages.ListeNachFaelligkeit_BillsAfterDaysDue;
	private static final String DUE_AFTER_TEXT = "Fällig nach Tagen"; //$NON-NLS-1$
	private static final String DUE_DATE_TEXT = "Stichtag"; //$NON-NLS-1$
	private static final String FIELD_ACTMANDATOR = "Nur aktueller Mandant";
	private int dueAfter;
	private DateTool stichTag = new DateTool();
	private boolean bOnlyActiveMandator;

	public ListeNachFaelligkeit() {
		super(NAME);

	}

	@SetProperty(name = DUE_DATE_TEXT)
	public void setStichtag(String stichtag) throws SetDataException {
		stichTag = new DateTool(stichtag);
	}

	@GetProperty(name = DUE_DATE_TEXT, widgetType = WidgetTypes.TEXT_DATE, index = -2)
	public String getStichtag() {
		return stichTag.toString(DateTool.DATE_GER);
	}

	@GetProperty(name = DUE_AFTER_TEXT, widgetType = WidgetTypes.TEXT_NUMERIC)
	public int getDueAfter() {
		return dueAfter;
	}

	@SetProperty(name = DUE_AFTER_TEXT)
	public void setDueAfter(int date) {
		dueAfter = date;
	}

	@GetProperty(name = FIELD_ACTMANDATOR, widgetType = WidgetTypes.BUTTON_CHECKBOX, index = 2)
	public boolean getOnlyActiveMandator() {
		return bOnlyActiveMandator;
	}

	@SetProperty(name = FIELD_ACTMANDATOR, index = 1)
	public void setOnlyActiveMandator(boolean val) {
		bOnlyActiveMandator = val;
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		int totalwork = 1000000;
		monitor.beginTask(NAME, totalwork);
		monitor.subTask(DATENBANKABFRAGE);
		Query<Rechnung> qbe = new Query<Rechnung>(Rechnung.class);
		qbe.add("RnStatus", "<>", Integer.toString(RnStatus.BEZAHLT)); //$NON-NLS-1$ //$NON-NLS-2$
		List<Rechnung> rnn = qbe.execute();
		monitor.worked(1000);
		int size = rnn.size();
		if (size == 0) {
			monitor.done();
			return Status.OK_STATUS;
		}
		int step = totalwork / rnn.size();
		monitor.subTask(ANALYSIERE_RECHNUNGEN);
		ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
		PatientIdFormatter pif = new PatientIdFormatter(8);
		String actMnId = ContextServiceHolder.getActiveMandatorOrNull().getId();
		for (Rechnung rn : rnn) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			if (bOnlyActiveMandator && (!actMnId.equals(rn.get("MandantID")))) { //$NON-NLS-1$
				continue;
			}
			if (RnStatus.isActive(rn.getStatus()) && StringUtils.isNotBlank(rn.getNr())) {
				DateTool date = new DateTool(rn.getDatumRn());
				date.addDays(dueAfter);
				if (date.isBefore(stichTag)) {
					Comparable<?>[] row = new Comparable[dataSet.getHeadings().size()];
					Fall fall = rn.getFall();
					if (fall != null) {
						Patient pat = fall.getPatient();
						if (pat != null) {
							row[0] = pif.format(pat.get("PatientNr")); //$NON-NLS-1$
							row[1] = Integer.parseInt(rn.getNr());
							row[2] = new DateTool(date);
							row[3] = rn.getBetrag();
							result.add(row);
						}
					}

				}
			}
			monitor.worked(step);
		}
		this.dataSet.setContent(result);
		monitor.done();
		return Status.OK_STATUS;
	}

	@Override
	protected List<String> createHeadings() {
		List<String> ret = new ArrayList<String>();
		ret.add(Messages.ListeNachFaelligkeit_PatientNr);
		ret.add(Messages.ListeNachFaelligkeit_BillNr);
		ret.add(Messages.ListeNachFaelligkeit_Due);
		ret.add(Messages.ListeNachFaelligkeit_Amount);
		return ret;
	}

	@Override
	public String getDescription() {
		return NAME;
	}

}
