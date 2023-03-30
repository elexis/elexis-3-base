/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.berchtold.emanuel.privatrechnung.rechnung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.berchtold.emanuel.privatrechnung.data.PreferenceConstants;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.constants.ExtensionPointConstantsData;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.Extensions;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class RnPrintView extends ViewPart {
	final static String ID = "ch.berchtold.privatrechung.view";
	final static double lineHeight = 0.63;
	TextContainer tc;
	Fall fall;
	private IRnOutputter tarmedOutputter;

	@Override
	public void createPartControl(final Composite parent) {
		tc = new TextContainer(parent.getShell());
		tc.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {

			public void save() {
				// we don't save
			}

			public boolean saveAs() {
				return false; // nope
			}
		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * print a bill into a text container
	 */
	public Result<Rechnung> doPrint(final Rechnung rn, Properties props) {
		Mandant sm = ElexisEventDispatcher.getSelectedMandator();
		if (sm == null || (!sm.isValid())) {
			return new Result<Rechnung>(SEVERITY.ERROR, 1, "Kein Mandant eingeloggt", null, true);
		}

		String id = sm.getId();
		Result<Rechnung> ret = new Result<Rechnung>();
		fall = rn.getFall();
		ElexisEventDispatcher.fireSelectionEvent(fall);
		Kontakt adressat = fall.getGarant();
		if (!adressat.isValid()) {
			adressat = fall.getPatient();
		}

		List<Konsultation> kons = rn.getKonsultationen();
		Collections.sort(kons, new Comparator<Konsultation>() {
			TimeTool t0 = new TimeTool();
			TimeTool t1 = new TimeTool();

			public int compare(final Konsultation arg0, final Konsultation arg1) {
				t0.set(arg0.getDatum());
				t1.set(arg1.getDatum());
				return t0.compareTo(t1);
			}

		});
		// Leistungen und Artikel gruppieren
		Money sum = new Money();
		HashMap<String, List<IBilled>> groups = new HashMap<String, List<IBilled>>();
		for (Konsultation k : kons) {
			IEncounter encounter = NoPoUtil.loadAsIdentifiable(k, IEncounter.class).get();
			for (IBilled v : encounter.getBilled()) {
				sum.addMoney(v.getTotal());
				IBillable iv = v.getBillable();
				if (iv != null) {
					String csName = iv.getCodeSystemName();
					List<IBilled> gl = groups.get(csName);
					if (gl == null) {
						gl = new ArrayList<IBilled>();
						groups.put(csName, gl);
					}
					gl.add(v);
				}
			}
		}
		if (props.get("Summary").equals(Boolean.toString(true))) {
			// Seite mit ESR
			tc.createFromTemplateName(null, BerchtoldPrivatrechnungTextTemplateRequirement.getESRTemplate(),
					Brief.RECHNUNG, adressat, rn.getNr());
			fillFields();
			ESR esr = new ESR(CoreHub.localCfg.get(PreferenceConstants.esrIdentity + "/" + id, StringUtils.EMPTY),
					CoreHub.localCfg.get(PreferenceConstants.esrUser + "/" + id, StringUtils.EMPTY), rn.getRnId(), 27);
			Kontakt bank = Kontakt
					.load(CoreHub.localCfg.get(PreferenceConstants.cfgBank + "/" + id, StringUtils.EMPTY));
			if (!bank.isValid()) {
				SWTHelper.showError("Keine Bank", "Bitte geben Sie eine Bank für die Zahlungen ein");
			}
			esr.printBESR(bank, adressat, rn.getMandant(), sum.getCentsAsString(), tc);
			Object pos = tc.getPlugin().insertText("[Leistungen]", "Leistungspositionen\n", SWT.LEFT);
			for (String k : groups.keySet()) {
				tc.getPlugin().setFont("Helvetiva", SWT.BOLD, 10);
				pos = tc.getPlugin().insertText(pos, k + ":\t", SWT.LEFT);
				tc.getPlugin().setFont("Helvetiva", SWT.NORMAL, 10);
				List<IBilled> lv = groups.get(k);
				Money zeile = new Money();
				for (IBilled vv : lv) {
					zeile.addMoney(vv.getTotal());
				}
				pos = tc.getPlugin().insertText(pos, "\t\t" + zeile.getAmountAsString() + StringUtils.LF, SWT.LEFT);
			}
			pos = tc.getPlugin().insertText(pos,
					"_____________________________________\nSumme\t\t\t" + sum.getAmountAsString(), SWT.LEFT);
			tc.getPlugin().print(CoreHub.localCfg.get("Drucker/A4ESR/Name", null), null, false);
		}
		if (props.get("Detail").equals(Boolean.toString(true))) {
			// Seite Detail
			double cmAvail = CoreHub.localCfg.get(PreferenceConstants.cfgTemplateBillHeight + "/" + id, 15.0);
			String toPrinter = CoreHub.localCfg.get("Drucker/A4/Name", null);
			tc.createFromTemplateName(null, BerchtoldPrivatrechnungTextTemplateRequirement.getBill1Template(),
					Brief.RECHNUNG, adressat, rn.getNr());
			fillFields();
			Object pos = tc.getPlugin().insertText("[Leistungen]", StringUtils.LF, SWT.LEFT);
			sum = new Money();
			int page = 1;
			tc.getPlugin().setFont("Helvetica", SWT.NORMAL, 8);
			for (Konsultation k : kons) {
				IEncounter encounter = NoPoUtil.loadAsIdentifiable(k, IEncounter.class).get();
				/*
				 * tc.getPlugin().setFont("Helvetica", SWT.BOLD, 10); pos =
				 * tc.getPlugin().insertText(pos, new
				 * TimeTool(k.getDatum()).toString(TimeTool.DATE_GER) + StringUtils.LF,
				 * SWT.LEFT);
				 */
				String date = new TimeTool(k.getDatum()).toString(TimeTool.DATE_GER);
				for (IBilled vv : encounter.getBilled()) {
					StringBuilder sb = new StringBuilder();
					sb.append(date).append("\t").append(vv.getAmount()).append("\t").append(vv.getText()).append("\t")
							.append(vv.getScaledPrice()).append("\t").append(vv.getTotal().getAmountAsString())
							.append(StringUtils.LF);
					pos = tc.getPlugin().insertText(pos, sb.toString(), SWT.LEFT);
					sum.addMoney(vv.getTotal());
					cmAvail -= lineHeight;
					if (cmAvail <= 0.0) {
						StringBuilder footer = new StringBuilder();
						pos = tc.getPlugin().insertText(pos, "\n\n", SWT.LEFT); //$NON-NLS-1$
						footer.append("Zwischentotal:\t\t").append(sum.getAmountAsString()); //$NON-NLS-1$
						pos = tc.getPlugin().insertText(pos, footer.toString(), SWT.LEFT);
						if (tc.getPlugin().print(toPrinter, null, false) == false) {
							return new Result<Rechnung>(SEVERITY.ERROR, 2, "Fehler beim Drucken der Rn. " + rn.getNr(),
									null, true);
						}

						insertPage(++page, adressat, rn);
						pos = tc.getPlugin().insertText("[Leistungen]", StringUtils.LF, SWT.LEFT); //$NON-NLS-1$
						cmAvail = CoreHub.localCfg.get(PreferenceConstants.cfgTemplateBill2Height + "/" + id, 20.0);

					}
				}
			}
			pos = tc.getPlugin().insertText(pos,
					StringTool.filler("_", 110) + "\nTotal:\t\t\t\t" + sum.getAmountAsString(), SWT.LEFT);

			tc.getPlugin().print(toPrinter, null, false);
		}
		if (props.get(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM).equals(Boolean.toString(true))) {
			Optional<IRnOutputter> tarmedOutputter = getTarmedOutputter();
			if (tarmedOutputter.isPresent()) {
				Properties tarmedProps = new Properties();
				tarmedProps.put(IRnOutputter.PROP_OUTPUT_MODIFY_INVOICESTATE, Boolean.toString(false));
				tarmedProps.put(IRnOutputter.PROP_OUTPUT_WITH_ESR, Boolean.toString(false));
				tarmedProps.put(IRnOutputter.PROP_OUTPUT_WITH_RECLAIM, Boolean.toString(true));

				tarmedOutputter.get().doOutput(IRnOutputter.TYPE.ORIG, Collections.singletonList(rn), tarmedProps);
			} else {
				SWTHelper.showError("Keine Tarmed Ausgabe gefunden",
						"Es ist keine Tarmed Ausgabe bei dieser Installation verfügbar.");
			}
		}
		return ret;
	}

	private Optional<IRnOutputter> getTarmedOutputter() {
		if (tarmedOutputter != null) {
			return Optional.of(tarmedOutputter);
		}
		@SuppressWarnings("unchecked")
		List<IRnOutputter> foundOutputters = Extensions.getClasses(ExtensionPointConstantsData.RECHNUNGS_MANAGER,
				"outputter");
		for (IRnOutputter outputter : foundOutputters) {
			if (outputter.getClass().getName().endsWith("TarmedRechnung.RechnungsDrucker")) {
				tarmedOutputter = outputter;
				return Optional.of(tarmedOutputter);
			}
		}
		return Optional.empty();
	}

	private void insertPage(final int page, final Kontakt adressat, final Rechnung rn) {
		tc.createFromTemplateName(null, BerchtoldPrivatrechnungTextTemplateRequirement.getBill2Template(),
				Brief.RECHNUNG, adressat, rn.getNr());
		fillFields();
		tc.replace("\\[Seite\\]", StringTool.pad(StringTool.LEFT, '0', Integer.toString(page), 2)); //$NON-NLS-1$
	}

	private void fillFields() {
		Kontakt versicherung = Kontakt.load(fall.getInfoString("Versicherung"));
		if (versicherung.isValid()) {
			tc.replace("\\?\\?Versicherung\\.Name\\?\\?]", versicherung.getLabel());
			tc.replace("\\?\\?Versicherung\\.Anschrift\\?\\?", versicherung.getPostAnschrift(true));
		}

	}

}
