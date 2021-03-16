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
package ch.elexis.privatrechnung.rechnung;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Verrechnet;
import ch.elexis.privatrechnung.data.PreferenceConstants;
import ch.elexis.privatrechnung.rechnung.RnPrintView.VatRateSum.VatRateElement;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class RnPrintView extends ViewPart {
	// constants to access vat information from the extinfo of the Rechnungssteller
	public static final String VAT_ISMANDANTVAT = "at.medevit.medelexis.vat_ch/IsMandantVat";
	public static final String VAT_MANDANTVATNUMBER =
		"at.medevit.medelexis.vat_ch/MandantVatNumber";
	
	private static NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
	
	final static String ID = "ch.elexis.privatrechnung.view";
	TextContainer tc;
	Fall fall;
	
	@Override
	public void createPartControl(final Composite parent){
		tc = new TextContainer(parent.getShell());
		tc.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {
			
			public void save(){
				// we don't save
			}
			
			public boolean saveAs(){
				return false; // nope
			}
		});
		
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * print a bill into a text container
	 */
	public Result<Rechnung> doPrint(final Rechnung rn){
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		
		Result<Rechnung> ret = new Result<Rechnung>();
		fall = rn.getFall();
		ElexisEventDispatcher.fireSelectionEvent(fall);
		Kontakt adressat = fall.getGarant();
		if (!adressat.isValid()) {
			adressat = fall.getPatient();
		}
		tc.createFromTemplateName(null, PrivaterechnungTextTemplateRequirement.getBillTemplate(),
			Brief.RECHNUNG, adressat, rn.getNr());
		fillFields();
		List<Konsultation> kons = rn.getKonsultationen();
		Collections.sort(kons, new Comparator<Konsultation>() {
			TimeTool t0 = new TimeTool();
			TimeTool t1 = new TimeTool();
			
			public int compare(final Konsultation arg0, final Konsultation arg1){
				t0.set(arg0.getDatum());
				t1.set(arg1.getDatum());
				return t0.compareTo(t1);
			}
			
		});
		Object pos = null;
		// Das Wort Leistungen soll jeder selbst in die Vorlage nehmen:
		// pos = tc.getPlugin().insertText("[Leistungen]", "Leistungen\n\n", SWT.LEFT);
		pos = tc.getPlugin().insertText("[Leistungen]", "", SWT.LEFT);
		Money sum = new Money();
		VatRateSum vatSummer = new VatRateSum();
		for (Konsultation k : kons) {
			tc.getPlugin().setStyle(SWT.BOLD);
			// print date
			pos =
				tc.getPlugin().insertText(pos,
					new TimeTool(k.getDatum()).toString(TimeTool.DATE_GER) + "\n", SWT.LEFT);
			tc.getPlugin().setStyle(SWT.NORMAL);
			// print header for Leistungen
			StringBuilder header = new StringBuilder();
			header.append("Anzahl").append("\t").append("MWSt.").append("\t").append("Einzelpreis")
				.append("\t").append("Betrag").append("\n\n");
			pos = tc.getPlugin().insertText(pos, header.toString(), SWT.LEFT);
			// print info for each Leistung
			for (Verrechnet vv : k.getLeistungen()) {
				tc.getPlugin().setStyle(SWT.BOLD);
				pos = tc.getPlugin().insertText(pos, "- " + vv.getText() + "\n", SWT.LEFT);
				tc.getPlugin().setStyle(SWT.NORMAL);
				
				Money preis = vv.getNettoPreis().roundTo5();
				int zahl = vv.getZahl();
				Money subtotal = new Money(preis);
				subtotal.multiply(zahl).roundTo5();
				StringBuilder sb = new StringBuilder();
				sb.append(zahl).append("\t").append(getVatRate(vv, subtotal, vatSummer))
					.append("\t").append(preis.getAmountAsString()).append("\t")
					.append(subtotal.getAmountAsString()).append("\n");
				pos = tc.getPlugin().insertText(pos, sb.toString(), SWT.LEFT);
				sum.addMoney(subtotal);
			}
		}
		pos =
			tc.getPlugin().insertText(
				pos,
				"____________________________________________________________________\nTotal:\t\t\t"
					+ sum.roundTo5().getAmountAsString(),
				SWT.LEFT);
		
		// print vat info of whole bill
		String vatNumber =
			rn.getMandant().getRechnungssteller().getInfoString(VAT_MANDANTVATNUMBER);
		tc.getPlugin().setStyle(SWT.BOLD);
		pos = tc.getPlugin().insertText(pos, "\n\nMWSt.Nr. \t", SWT.LEFT);
		tc.getPlugin().setStyle(SWT.NORMAL);
		if (vatNumber != null && vatNumber.length() > 0)
			pos = tc.getPlugin().insertText(pos, vatNumber + "\n", SWT.LEFT);
		else
			pos = tc.getPlugin().insertText(pos, "keine\n", SWT.LEFT);
		
		tc.getPlugin().setStyle(SWT.BOLD);
		pos = tc.getPlugin().insertText(pos, "\nSatz\tBetrag\tMWSt\n", SWT.LEFT);
		tc.getPlugin().setStyle(SWT.NORMAL);
		
		VatRateElement[] vatValues = vatSummer.rates.values().toArray(new VatRateElement[0]);
		Arrays.sort(vatValues);
		for (VatRateElement rate : vatValues) {
			StringBuilder sb = new StringBuilder();
			sb.append(nf.format(rate.scale)).append("\t").append(nf.format(rate.sumamount))
				.append("\t").append(nf.format(rate.sumvat)).append("\n");
			pos = tc.getPlugin().insertText(pos, sb.toString(), SWT.LEFT);
		}
		
		tc.getPlugin().setStyle(SWT.BOLD);
		pos =
			tc.getPlugin().insertText(pos,
				"\nTotal\t" + sum.getAmountAsString() + "\t" + nf.format(vatSummer.sumvat) + "\n",
				SWT.LEFT);
		tc.getPlugin().setStyle(SWT.NORMAL);
		
		String toPrinter = CoreHub.localCfg.get("Drucker/A4/Name", null);
		tc.getPlugin().print(toPrinter, null, false);
		tc.createFromTemplateName(null, PrivaterechnungTextTemplateRequirement.getESRTemplate(),
			Brief.RECHNUNG, adressat, rn.getNr());
		fillFields();
		ESR esr =
			new ESR(CoreHub.globalCfg.get(PreferenceConstants.esrIdentity, ""),
				CoreHub.globalCfg.get(PreferenceConstants.esrUser, ""), rn.getRnId(), 27);
		Kontakt bank = Kontakt.load(CoreHub.globalCfg.get(PreferenceConstants.cfgBank, ""));
		if (!bank.isValid()) {
			SWTHelper.showError("Keine Bank", "Bitte geben Sie eine Bank für die Zahlungen ein");
		}
		esr.printBESR(bank, adressat, rn.getMandant(), sum.getCentsAsString(), tc);
		tc.replace("\\[Leistungen\\]", sum.getAmountAsString());
		tc.getPlugin().print(CoreHub.localCfg.get("Drucker/A4ESR/Name", null), null, false);
		tc.getPlugin().setFont(null, SWT.NORMAL, 0.0f);
		return ret;
	}
	
	private void fillFields(){
		Kontakt versicherung = Kontakt.load(fall.getInfoString("Versicherung"));
		if (versicherung.isValid()) {
			tc.replace("\\?\\?Versicherung\\.Name\\?\\?]", versicherung.getLabel());
			tc.replace("\\?\\?Versicherung\\.Anschrift\\?\\?", versicherung.getPostAnschrift(true));
		}
		
	}
	
	/**
	 * Get the correct VAT value based on the Verrechent and the info if the Rechnungssteller has to
	 * pay VAT.
	 * 
	 * @param verrechnet
	 * @param amount
	 */
	private String getVatRate(Verrechnet verrechnet, Money amount, VatRateSum vatsum){
		
		Boolean isVat =
			(Boolean) verrechnet.getKons().getMandant().getRechnungssteller()
				.getInfoElement(VAT_ISMANDANTVAT);
		
		double value = 0.0;
		if (isVat != null && isVat) {
			String vatScale = verrechnet.getDetail(Verrechnet.VATSCALE);
			if (vatScale != null && vatScale.length() > 0)
				value = Double.parseDouble(vatScale);
		}
		vatsum.add(value, amount.doubleValue());
		
		return Double.toString(value); //$NON-NLS-1$
	}
	
	/**
	 * Class for keeping track of vat scales and corresponding amounts.
	 * 
	 * @author thomas
	 * 
	 */
	class VatRateSum {
		class VatRateElement implements Comparable<VatRateElement> {
			double scale;
			double sumamount;
			double sumvat;
			
			VatRateElement(double scale){
				this.scale = scale;
				sumamount = 0;
				sumvat = 0;
			}
			
			void add(double amount){
				this.sumamount += amount;
				sumvat += (amount / (100.0 + scale)) * scale;
			}
			
			public int compareTo(VatRateElement other){
				if (scale < other.scale)
					return -1;
				else if (scale > other.scale)
					return 1;
				else
					return 0;
			}
		}
		
		HashMap<Double, VatRateElement> rates = new HashMap<Double, VatRateElement>();
		double sumvat = 0.0;
		
		public void add(double scale, double amount){
			VatRateElement element = rates.get(Double.valueOf(scale));
			if (element == null) {
				element = new VatRateElement(scale);
				rates.put(new Double(scale), element);
			}
			element.add(amount);
			sumvat += (amount / (100.0 + scale)) * scale;
		}
	}
}
