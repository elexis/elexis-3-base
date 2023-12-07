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

import ch.elexis.buchhaltung.util.PatientIdFormatter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.Fall;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Zahlung;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.SetDataException;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * Find all bills that are payable at a given date
 *
 * @author user
 *
 */
public class OffenePostenListe extends AbstractDataProvider {
	private static final String OFFENE_RECHNUNGEN_PER = Messages.OffenePostenListe_OpenBillsPer;
	private static final String OFFENE_POSTEN = Messages.OffenePostenListe_Open;
	private static final String ANALYSIERE_RECHNUNGEN = Messages.OffenePostenListe_AnalyzingBills;
	private static final String DATENBANKABFRAGE = Messages.OffenePostenListe_DatabaseQuery;
	private static final String NAME = OFFENE_POSTEN;
	private static final String FIELD_ACTMANDATOR = "Nur aktueller Mandant"; //$NON-NLS-1$
	private static final String FIELD_AUSGANGSDATUM = "Ausgangsdatum"; //$NON-NLS-1$
	private static final String FIELD_STICHTAG = "Stichtag"; //$NON-NLS-1$

	private TimeTool stichtag = new TimeTool();
	private TimeTool startTag = new TimeTool();
	private boolean bOnlyActiveMandator;

	public OffenePostenListe() {
		super(NAME);
		startTag.set(TimeTool.MONTH, TimeTool.JANUARY);
		startTag.set(TimeTool.DAY_OF_MONTH, 1);
	}

	public void setStartTag(TimeTool starttag) {
		this.startTag.set(starttag);
	}

	public TimeTool getStartTag() {
		return new TimeTool(startTag);
	}

	public void setStichtag(TimeTool stichtag) {
		this.stichtag.set(stichtag);
	}

	public TimeTool getStichtag() {
		return new TimeTool(stichtag);
	}

	@GetProperty(name = FIELD_ACTMANDATOR, widgetType = WidgetTypes.BUTTON_CHECKBOX)
	public boolean getOnlyActiveMandator() {
		return bOnlyActiveMandator;
	}

	@SetProperty(name = FIELD_ACTMANDATOR)
	public void setOnlyActiveMandator(boolean val) {
		bOnlyActiveMandator = val;
	}

	@GetProperty(name = FIELD_AUSGANGSDATUM, widgetType = WidgetTypes.TEXT_DATE)
	public String metaGetStarttag() {
		return getStartTag().toString(TimeTool.DATE_SIMPLE);
	}

	@SetProperty(name = FIELD_AUSGANGSDATUM, index = -2)
	public void metaSetStarttag(String tag) throws SetDataException {
		TimeTool tt = new TimeTool(tag);
		this.setStartTag(tt);
	}

	@GetProperty(name = FIELD_STICHTAG, widgetType = WidgetTypes.TEXT_DATE)
	public String metaGetStichtag() {
		return getStichtag().toString(TimeTool.DATE_SIMPLE);
	}

	@SetProperty(name = FIELD_STICHTAG)
	public void metaSetStichtag(String stichtag) throws SetDataException {
		TimeTool tt = new TimeTool(stichtag);
		this.setStichtag(tt);
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		int totalwork = 1000000;
		monitor.beginTask(OFFENE_RECHNUNGEN_PER + getStichtag().toString(TimeTool.DATE_SIMPLE), totalwork);
		monitor.subTask(DATENBANKABFRAGE);
		Query<Rechnung> qbe = new Query<Rechnung>(Rechnung.class);
		qbe.add("RnDatum", "<=", getStichtag().toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("RnDatum", ">=", getStartTag().toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
		if (bOnlyActiveMandator) {
			qbe.add("MandantID", "=", ContextServiceHolder.getActiveMandatorOrThrow().getId()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		List<Rechnung> rnn = qbe.execute();
		monitor.worked(1000);
		final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
		int size = rnn.size();
		if (size == 0) {
			monitor.done();
			this.dataSet.setContent(result);
			return Status.OK_STATUS;
		}
		int step = totalwork / size;
		monitor.subTask(ANALYSIERE_RECHNUNGEN);
		TimeTool now = getStichtag();
		PatientIdFormatter pif = new PatientIdFormatter(8);
		for (Rechnung rn : rnn) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			Fall fall = rn.getFall();
			if (fall != null) {
				Patient pat = fall.getPatient();
				Money betrag = rn.getBetrag();

				if ((pat != null) && (betrag != null) && (!betrag.isNeglectable())) {
					int status = rn.getStatusAtDate(now);
					if (RnStatus.isActive(status)) {
						Comparable[] row = new Comparable[this.getDataSet().getHeadings().size()];
						row[0] = pif.format(pat.get("PatientNr")); //$NON-NLS-1$
						row[1] = rn.getNr();
						List<Zahlung> zahlungen = rn.getZahlungen();
						for (Zahlung z : zahlungen) {
							TimeTool tt = new TimeTool(z.getDatum());
							if (tt.isAfter(now)) {
								continue;
							}
							betrag.subtractMoney(z.getBetrag());
						}
						row[3] = Double.toString(betrag.getAmount());
						row[2] = RnStatus.getStatusText(status);
						result.add(row);
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
		ret.add(Messages.OffenePostenListe_PatientNr);
		ret.add(Messages.OffenePostenListe_BillNr);
		ret.add(Messages.OffenePostenListe_BillState);
		ret.add(Messages.OffenePostenListe_OpenAmount);
		return ret;
	}

	@Override
	public String getDescription() {
		return OFFENE_POSTEN;
	}

}
