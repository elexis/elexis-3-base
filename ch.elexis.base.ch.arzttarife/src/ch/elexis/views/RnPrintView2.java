/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.views;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.Brief;
import ch.elexis.core.data.Fall;
import ch.elexis.core.data.Kontakt;
import ch.elexis.core.data.Mandant;
import ch.elexis.core.data.Patient;
import ch.elexis.core.data.Rechnung;
import ch.elexis.core.data.Rechnungssteller;
import ch.elexis.core.data.RnStatus;
import ch.elexis.core.data.Zahlung;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.data.util.SortedList;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Log;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

/**
 * This is a pop-in replacement for RnPrintView. To avoid several problems around OpenOffice based
 * bills we keep things easier here. Thus this approach does not optimize printer access but rather
 * waits for each page to be printed before starting the next.
 * 
 * We also corrected several problems around the TrustCenter-system. Tokens are printed only on TG
 * bills and only if the mandator has a TC contract. Tokens are computed correctly now with the TC
 * number as identifier in TG bills and left as ESR in TP bills.
 * 
 * @author Gerry
 * 
 */
public class RnPrintView2 extends ViewPart {
	public static final String ID = "ch.elexis.arzttarife_ch.printview2";
	
	private double cmAvail = 21.4; // Verfügbare Druckhöhe in cm
	private static double cmPerLine = 0.67; // Höhe pro Zeile (0.65 plus
	// Toleranz)
	private static double cmFirstPage = 13.0; // Platz auf der ersten Seite
	private static double cmMiddlePage = 21.0; // Platz auf Folgeseiten
	private static double cmFooter = 4.5; // Platz für Endabrechnung
	private final Log log = Log.get("RnPrint");
	private String paymentMode;
	private Brief actBrief;
	TextContainer text;
	TarmedACL ta = TarmedACL.getInstance();
	
	public RnPrintView2(){
		
	}
	
	@Override
	public void createPartControl(final Composite parent){
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {
			
			public void save(){
				// TODO Auto-generated method stub
				
			}
			
			public boolean saveAs(){
				// TODO Auto-generated method stub
				return false;
			}
		});
		text.getPlugin().setParameter(ITextPlugin.Parameter.NOUI);
	}
	
	private void createBrief(final String template, final Kontakt adressat){
		actBrief =
			text.createFromTemplateName(null, template, Brief.RECHNUNG, adressat,
				Messages.RnPrintView_tarmedBill);
	}
	
	private boolean deleteBrief(){
		if (actBrief != null) {
			return actBrief.delete();
		}
		return true;
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Druckt die Rechnung auf eine Vorlage, deren Ränder alle auf 0.5cm eingestellt sein müssen,
	 * und die unterhalb von 170 mm leer ist. (Papier mit EZ-Schein wird erwartet) Zweite und
	 * Folgeseiten müssen gem Tarmedrechnung formatiert sein.
	 * 
	 * @param rn
	 *            die Rechnung
	 * @param saveFile
	 *            Filename für eine XML-Kopie der Rechnung oder null: Keine Kopie
	 * @param withForms
	 * @param monitor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean doPrint(final Rechnung rn, final IRnOutputter.TYPE rnType,
		final String saveFile, final boolean withESR, final boolean withForms,
		final boolean doVerify, final IProgressMonitor monitor){
		Mandant mSave = CoreHub.actMandant;
		monitor.subTask(rn.getLabel());
		Fall fall = rn.getFall();
		Mandant mnd = rn.getMandant();
		if (fall == null || mnd == null) {
			return false;
		}
		Patient pat = fall.getPatient();
		Hub.setMandant(mnd);
		Rechnungssteller rs = mnd.getRechnungssteller();
		if (pat == null || rs == null) {
			return false;
		}
		
		String printer = null;
		XMLExporter xmlex = new XMLExporter();
		DecimalFormat df = new DecimalFormat(StringConstants.DOUBLE_ZERO);
		Document xmlRn = xmlex.doExport(rn, saveFile, rnType, doVerify);
		if (rn.getStatus() == RnStatus.FEHLERHAFT) {
			return false;
		}
		Element invoice =
			xmlRn.getRootElement().getChild(XMLExporter.ELEMENT_INVOICE, XMLExporter.ns);
		Element balance = invoice.getChild(XMLExporter.ELEMENT_BALANCE, XMLExporter.ns);
		paymentMode = XMLExporter.TIERS_GARANT; // fall.getPaymentMode();
		Element eTiers = invoice.getChild(XMLExporter.ELEMENT_TIERS_GARANT, XMLExporter.ns);
		if (eTiers == null) {
			eTiers = invoice.getChild(XMLExporter.ELEMENT_TIERS_PAYANT, XMLExporter.ns);
			paymentMode = XMLExporter.TIERS_PAYANT;
		}
		
		String tcCode = null;
		if (TarmedRequirements.hasTCContract(rs) && paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			tcCode = TarmedRequirements.getTCCode(rs);
		} else if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			tcCode = "01";
		}
		ElexisEventDispatcher.fireSelectionEvents(rn, fall, pat, rs);
		
		// make sure the Textplugin can replace all fields
		fall.setInfoString("payment", paymentMode);
		fall.setInfoString("Gesetz", TarmedRequirements.getGesetz(fall));
		mnd.setInfoElement("EAN", TarmedRequirements.getEAN(mnd));
		rs.setInfoElement("EAN", TarmedRequirements.getEAN(rs));
		mnd.setInfoElement("KSK", TarmedRequirements.getKSK(mnd));
		mnd.setInfoElement("NIF", TarmedRequirements.getNIF(mnd));
		if (!mnd.equals(rs)) {
			rs.setInfoElement("EAN", TarmedRequirements.getEAN(rs));
			rs.setInfoElement("KSK", TarmedRequirements.getKSK(rs));
			rs.setInfoElement("NIF", TarmedRequirements.getNIF(rs));
		}
		
		Kontakt adressat;
		
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			adressat = fall.getRequiredContact(TarmedRequirements.INSURANCE);
		} else {
			adressat = fall.getGarant();
		}
		if ((adressat == null) || (!adressat.exists())) {
			adressat = pat;
		}
		adressat.getPostAnschrift(true); // damit sicher eine existiert
		String userdata = rn.getRnId();
		ESR esr =
			new ESR(rs.getInfoString(ta.ESRNUMBER), rs.getInfoString(ta.ESRSUB), userdata,
				ESR.ESR27);
		Money mDue =
			XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_DUE));
		Money mPaid =
			XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_PREPAID));
		String offenRp = mDue.getCentsAsString();
		// Money mEZDue=new Money(xmlex.mTotal);
		Money mEZDue = new Money(mDue); // XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount_obligations"));
		Money mEZBrutto = new Money(mDue);
		mEZDue.addMoney(mPaid);
		if (withESR == true) {
			String tmpl = "Tarmedrechnung_EZ"; //$NON-NLS-1$
			if ((rn.getStatus() == RnStatus.MAHNUNG_1)
				|| (rn.getStatus() == RnStatus.MAHNUNG_1_GEDRUCKT)) {
				tmpl = "Tarmedrechnung_M1"; //$NON-NLS-1$
			} else if ((rn.getStatus() == RnStatus.MAHNUNG_2)
				|| (rn.getStatus() == RnStatus.MAHNUNG_2_GEDRUCKT)) {
				tmpl = "Tarmedrechnung_M2"; //$NON-NLS-1$
			} else if ((rn.getStatus() == RnStatus.MAHNUNG_3)
				|| (rn.getStatus() == RnStatus.MAHNUNG_3_GEDRUCKT)) {
				tmpl = "Tarmedrechnung_M3"; //$NON-NLS-1$
			}
			createBrief(tmpl, adressat);
			
			List<Zahlung> extra = rn.getZahlungen();
			Kontakt bank = Kontakt.load(rs.getInfoString(ta.RNBANK));
			final StringBuilder sb = new StringBuilder();
			String sTarmed = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_TARMED);
			String sMedikament = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_DRUG);
			String sAnalysen = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_LAB);
			String sMigel = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_MIGEL);
			String sPhysio = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_PHYSIO);
			String sOther = balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_UNCLASSIFIED);
			sb.append(Messages.RnPrintView_tarmedPoints).append(sTarmed).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_medicaments).append(sMedikament)
				.append(StringConstants.LF);
			sb.append(Messages.RnPrintView_labpoints).append(sAnalysen).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_migelpoints).append(sMigel).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_physiopoints).append(sPhysio).append(StringConstants.LF);
			sb.append(Messages.RnPrintView_otherpoints).append(sOther).append(StringConstants.LF);
			
			for (Zahlung z : extra) {
				Money betrag = new Money(z.getBetrag()).multiply(-1.0);
				if (!betrag.isNegative()) {
					sb.append(z.getBemerkung())
						.append(":\t").append(betrag.getAmountAsString()).append(StringConstants.LF); //$NON-NLS-1$ 
					mEZDue.addMoney(betrag);
				}
			}
			sb.append("--------------------------------------").append(StringConstants.LF); //$NON-NLS-1$ 
			
			sb.append(Messages.RnPrintView_sum).append(mEZDue);
			
			if (!mPaid.isZero()) {
				sb.append(Messages.RnPrintView_prepaid).append(mPaid.getAmountAsString())
					.append(StringConstants.LF);
				// sb.append("Noch zu zahlen:\t").append(xmlex.mDue.getAmountAsString()).append("\n");
				sb.append(Messages.RnPrintView_topay)
					.append(mEZDue.subtractMoney(mPaid).roundTo5().getAmountAsString())
					.append(StringConstants.LF);
			}
			
			text.getPlugin().setFont("Serif", SWT.NORMAL, 9); //$NON-NLS-1$
			text.replace("\\[Leistungen\\]", sb.toString());
			
			if (esr.printBESR(bank, adressat, rs, mEZDue.roundTo5().getCentsAsString(), text) == false) {
				// avoid dead letters
				deleteBrief();
				;
				Hub.setMandant(mSave);
				return false;
			}
			printer = CoreHub.localCfg.get("Drucker/A4ESR/Name", null); //$NON-NLS-1$
			String esrTray = CoreHub.localCfg.get("Drucker/A4ESR/Schacht", null); //$NON-NLS-1$
			if (StringTool.isNothing(esrTray)) {
				esrTray = null;
			}
			// Das mit der Tray- Einstellung funktioniert sowieso nicht richtig.
			// OOo nimmt den Tray aus der Druckformatvorlage. Besser wir setzen
			// ihn hier auf
			// null vorläufig.
			// Alternative: Wir verwenden ihn, falls er eingestellt ist, sonst
			// nicht.
			// Dies scheint je nach Druckertreiber unterschiedlich zu
			// funktionieren.
			if (text.getPlugin().print(printer, esrTray, false) == false) {
				SWTHelper.showError("Fehler beim Drucken", "Konnte den Drucker nicht starten");
				rn.addTrace(Rechnung.REJECTED, "Druckerfehler");
				// avoid dead letters
				deleteBrief();
				;
				CoreHub.setMandant(mSave);
				return false;
			}
			
			monitor.worked(2);
		}
		if (withForms == false) {
			// avoid dead letters
			deleteBrief();
			;
			Hub.setMandant(mSave);
			return true;
		}
		printer = CoreHub.localCfg.get("Drucker/A4/Name", null); //$NON-NLS-1$
		String tarmedTray = CoreHub.localCfg.get("Drucker/A4/Schacht", null); //$NON-NLS-1$
		if (StringTool.isNothing(tarmedTray)) {
			tarmedTray = null;
		}
		createBrief("Tarmedrechnung_S1", adressat);
		
		StringBuilder sb = new StringBuilder();
		Element root = xmlRn.getRootElement();
		Namespace ns = root.getNamespace();
		//Element invoice=root.getChild("invoice",ns); //$NON-NLS-1$
		if (invoice.getAttributeValue("resend").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
			text.replace("\\[F5\\]", Messages.RnPrintView_yes); //$NON-NLS-1$
		} else {
			text.replace("\\[F5\\]", Messages.RnPrintView_no); //$NON-NLS-1$
		}
		
		// Vergütungsart F17
		// replaced with Fall.payment
		
		if (fall.getAbrechnungsSystem().equals("UVG")) { //$NON-NLS-1$
			text.replace("\\[F58\\]", fall.getBeginnDatum()); //$NON-NLS-1$
		} else {
			text.replace("\\[F58\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		Element detail = invoice.getChild("detail", ns); //$NON-NLS-1$
		Element diagnosis = detail.getChild("diagnosis", ns); //$NON-NLS-1$
		String type = diagnosis.getAttributeValue(Messages.RnPrintView_62);
		
		// TODO Cheap workaround, fix
		if (type.equals("by_contract")) { //$NON-NLS-1$
			type = "TI-Code"; //$NON-NLS-1$
		}
		text.replace("\\[F51\\]", type); //$NON-NLS-1$
		if (type.equals("freetext")) { //$NON-NLS-1$
			text.replace("\\[F52\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
			text.replace("\\[F53\\]", diagnosis.getText()); //$NON-NLS-1$
		} else {
			text.replace("\\[F52\\]", diagnosis.getAttributeValue("code")); //$NON-NLS-1$ //$NON-NLS-2$
			text.replace("\\[F53\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// lookup EAN numbers in services and set field 98
		HashSet<String> eanUniqueSet = new HashSet<String>();
		List allServices = detail.getChild(XMLExporter.ELEMENT_SERVICES, ns).getChildren(); //$NON-NLS-1$
		for (Object object : allServices) {
			if (object instanceof Element) {
				Element service = (Element) object;
				String tariftype = service.getAttributeValue("tariff_type");
				// look into all tarmed 001 and physio 311 services
				if (tariftype != null && (tariftype.equals("001") || tariftype.equals("311"))) {
					String ean_responsible = service.getAttributeValue("ean_responsible");
					if (ean_responsible != null && !ean_responsible.isEmpty()) {
						eanUniqueSet.add(ean_responsible);
					}
					String ean_provider = service.getAttributeValue("ean_provider");
					if (ean_provider != null && !ean_provider.isEmpty()) {
						eanUniqueSet.add(ean_provider);
					}
				}
			}
		}
		String[] eanArray = getEANArray(eanUniqueSet);
		HashMap<String, String> eanMap = getEANHashMap(eanArray);
		text.replace("\\[F98\\]", getEANList(eanArray));
		
		Kontakt zuweiser = fall.getRequiredContact("Zuweiser");
		if (zuweiser != null) {
			String ean = TarmedRequirements.getEAN(zuweiser);
			if (!ean.equals(TarmedRequirements.EAN_PSEUDO)) {
				text.replace("\\[F23\\]", ean);
			}
		}
		
		Element services = detail.getChild(XMLExporter.ELEMENT_SERVICES, ns); //$NON-NLS-1$
		SortedList<Element> ls = new SortedList(services.getChildren(), new RnComparator());
		
		Element remark = invoice.getChild(XMLExporter.ELEMENT_REMARK); //$NON-NLS-1$
		if (remark != null) {
			final String rem = remark.getText();
			text.getPlugin().findOrReplace(Messages.RnPrintView_remark, new ReplaceCallback() {
				public String replace(final String in){
					return Messages.RnPrintView_remarksp + rem;
				}
			});
		}
		replaceHeaderFields(text, rn);
		text.replace("\\[F.+\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Object cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
		TimeTool r = new TimeTool();
		int page = 1;
		double seitentotal = 0.0;
		double sumPfl = 0.0;
		double sumNpfl = 0.0;
		double sumTotal = 0.0;
		ITextPlugin tp = text.getPlugin();
		cmAvail = cmFirstPage;
		monitor.worked(2);
		for (Element s : ls) {
			tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
			cursor = tp.insertText(cursor, "\t" + s.getText() + "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			tp.setFont("Helvetica", SWT.NORMAL, 8); //$NON-NLS-1$
			sb.setLength(0);
			if (r.set(s.getAttributeValue("date_begin")) == false) { //$NON-NLS-1$
				continue;
			}
			sb.append("■ "); //$NON-NLS-1$
			sb.append(r.toString(TimeTool.DATE_GER)).append("\t"); //$NON-NLS-1$
			sb.append(getValue(s, "tariff_type")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "ref_code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "number")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			if (getValue(s, "body_location").startsWith("l")) //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("L\t");
			else if (getValue(s, "body_location").startsWith("r")) //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("R\t");
			else
				sb.append(" \t");
			sb.append(getValue(s, "quantity")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			String val = s.getAttributeValue("unit.mt"); //$NON-NLS-1$
			if (StringTool.isNothing(val)) {
				val = s.getAttributeValue("unit"); //$NON-NLS-1$
				if (StringTool.isNothing(val)) {
					val = "\t"; //$NON-NLS-1$
				}
			}
			sb.append(val).append("\t"); //$NON-NLS-1$
			sb.append(getValue(s, "scale_factor.mt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			val = s.getAttributeValue("unit_factor.mt"); //$NON-NLS-1$
			if (StringTool.isNothing(val)) {
				val = s.getAttributeValue("unit_factor"); //$NON-NLS-1$
				if (StringTool.isNothing(val)) {
					val = "\t"; //$NON-NLS-1$
				}
			}
			sb.append(val).append("\t"); //$NON-NLS-1$
			sb.append(getValue(s, "unit.tt")).append("\t\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getValue(s, "unit_factor.tt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			
			// set responsible (field 77) and provider (field 78)
			String tariftype = s.getAttributeValue("tariff_type");
			// look into all tarmed 001 and physio 311 services
			if (tariftype != null && (tariftype.equals("001") || tariftype.equals("311"))) {
				String ean_responsible = s.getAttributeValue("ean_responsible"); //$NON-NLS-1$
				if (ean_responsible != null && !ean_responsible.isEmpty()) {
					sb.append(eanMap.get(ean_responsible) + "\t"); //$NON-NLS-1$
				}
				String ean_provider = s.getAttributeValue("ean_provider"); //$NON-NLS-1$
				if (ean_provider != null && !ean_provider.isEmpty()) {
					sb.append(eanMap.get(ean_provider) + "\t"); //$NON-NLS-1$
				}
			} else {
				sb.append("\t\t");
			}
			
			String pfl = s.getAttributeValue("obligation"); //$NON-NLS-1$
			String am = s.getAttributeValue(XMLExporter.ATTR_AMOUNT); //$NON-NLS-1$
			// double dLine=Double.parseDouble(am);
			double dLine;
			try {
				dLine = XMLTool.xmlDoubleToMoney(am).getAmount();
			} catch (NumberFormatException ex) {
				// avoid dead letters
				deleteBrief();
				;
				log.log("Fehlerhaftes Format für amount bei " + sb.toString(), Log.ERRORS);
				Hub.setMandant(mSave);
				return false;
			}
			sumTotal += dLine;
			if (pfl.equalsIgnoreCase("true")) { //$NON-NLS-1$
				sb.append("0\t"); //$NON-NLS-1$
				sumPfl += dLine;
			} else {
				sb.append("1\t"); //$NON-NLS-1$
				sumNpfl += dLine;
			}
			sb.append(Integer.toString(guessVatCode(getValue(s, "vat_rate")))).append("\t"); //$NON-NLS-1$
			
			sb.append(am);
			seitentotal += dLine;
			sb.append("\n"); //$NON-NLS-1$
			cursor = tp.insertText(cursor, sb.toString(), SWT.LEFT);
			cmAvail -= cmPerLine;
			if (cmAvail <= 0) {
				StringBuilder footer = new StringBuilder();
				cursor = tp.insertText(cursor, "\n\n", SWT.LEFT); //$NON-NLS-1$
				footer
					.append("■ Zwischentotal\t\tCHF\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t").append(df.format(seitentotal)); //$NON-NLS-1$
				tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
				cursor = tp.insertText(cursor, footer.toString(), SWT.LEFT);
				seitentotal = 0.0;
				if (tcCode != null) {
					esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
				}
				
				if (text.getPlugin().print(printer, tarmedTray, false) == false) {
					// avoid dead letters
					deleteBrief();
					;
					Hub.setMandant(mSave);
					return false;
				}
				
				insertPage(++page, adressat, rn);
				cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
				cmAvail = cmMiddlePage;
				monitor.worked(2);
			}
			
		}
		cursor = tp.insertText(cursor, "\n", SWT.LEFT); //$NON-NLS-1$
		if (cmAvail < cmFooter) {
			if (tcCode != null) {
				esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
			}
			if (text.getPlugin().print(printer, tarmedTray, false) == false) {
				// avoid dead letters
				deleteBrief();
				;
				Hub.setMandant(mSave);
				return false;
			}
			insertPage(++page, adressat, rn);
			cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			monitor.worked(2);
		}
		StringBuilder footer = new StringBuilder(100);
		//Element balance=invoice.getChild("balance",ns); //$NON-NLS-1$
		
		cursor = text.getPlugin().insertTextAt(0, 220, 190, 45, " ", SWT.LEFT); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tTARMED AL \t"); //$NON-NLS-1$
		footer.append(balance.getAttributeValue("amount_tarmed.mt")) //$NON-NLS-1$
			.append("  (").append(balance.getAttributeValue("unit_tarmed.mt")).append(")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cursor = print(cursor, tp, false, footer.toString());
		cursor = print(cursor, tp, true, "Physio \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_physio")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tMiGeL \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_migel")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tÜbrige \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_unclassified")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\n\tTARMED TL \t"); //$NON-NLS-1$
		footer.setLength(0);
		footer.append(balance.getAttributeValue("amount_tarmed.tt")) //$NON-NLS-1$
			.append("  (").append(balance.getAttributeValue("unit_tarmed.tt")).append(")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cursor = print(cursor, tp, false, footer.toString());
		cursor = print(cursor, tp, true, "Labor \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_lab")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tMedi \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_drug")); //$NON-NLS-1$
		cursor = print(cursor, tp, true, "\tKantonal \t"); //$NON-NLS-1$
		cursor = print(cursor, tp, false, getValue(balance, "amount_cantonal")); //$NON-NLS-1$
		
		footer.setLength(0);
		footer.append("\n\n").append("■ Gesamtbetrag\t\tCHF\t\t").append(df.format(sumTotal)) //$NON-NLS-1$ //$NON-NLS-2$
			.append("\tdavon PFL \t").append(df.format(sumPfl)).append("\tAnzahlung \t") //$NON-NLS-1$ //$NON-NLS-2$
			.append(mPaid.getAmountAsString())
			.append("\tFälliger Betrag \t").append(mDue.getAmountAsString()); //$NON-NLS-1$
		
		Element vat = balance.getChild("vat", ns);
		String vatNumber = getValue(vat, "vat_number");
		if (vatNumber.equals(" "))
			vatNumber = "keine";
		
		footer.append("\n\n■ MwSt.Nr. \t\t"); //$NON-NLS-1$
		cursor = print(cursor, tp, true, footer.toString());
		cursor = print(cursor, tp, false, vatNumber + "\n\n"); //$NON-NLS-1$
		
		Boolean isVat =
			(Boolean) mnd.getRechnungssteller().getInfoElement(XMLExporter.VAT_ISMANDANTVAT);
		if (isVat != null && isVat) {
			cursor = print(cursor, tp, true, "  Code\tSatz\t\tBetrag\t\tMwSt\n"); //$NON-NLS-1$
			tp.setFont("Helvetica", SWT.NORMAL, 9); //$NON-NLS-1$
			footer.setLength(0);
			
			List<Element> rates = vat.getChildren();
			
			// get vat lines ordered by code
			List<String> vatLines = new ArrayList<String>();
			for (Element rate : rates) {
				StringBuilder vatBuilder = new StringBuilder();
				int code = guessVatCode(getValue(rate, "vat_rate"));
				// set amount of tabs needed according, use 7
				String amount = getValue(rate, "amount");
				String tabs = "\t\t";
				if (amount.length() > 7)
					tabs = "\t";
				
				vatBuilder.append("■ ").append(Integer.toString(code)).append("\t")
					.append(getValue(rate, "vat_rate")).append("\t\t").append(amount).append(tabs)
					.append(getValue(rate, "vat")).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				// insert according to code
				if (code == 0) {
					vatLines.add(0, vatBuilder.toString());
				} else if (code == 1) {
					if (vatLines.size() == 2)
						vatLines.add(1, vatBuilder.toString());
					else
						vatLines.add(vatBuilder.toString());
				} else if (code == 2) {
					vatLines.add(vatBuilder.toString());
				}
			}
			for (String string : vatLines) {
				footer.append(string);
			}
			
			cursor = print(cursor, tp, false, footer.toString());
			cursor = print(cursor, tp, true, "\n Total\t\t\t"); //$NON-NLS-1$
			// set amount of tabs needed according to amount, use 8 as font is
			// bold
			String amount = mDue.getAmountAsString();
			String tabs = "\t\t";
			if (amount.length() > 8)
				tabs = "\t";
			
			footer.setLength(0);
			footer.append(amount).append(tabs).append(getValue(vat, "vat")); //$NON-NLS-1$
		} else {
			cursor = print(cursor, tp, true, "\n Total\t\t"); //$NON-NLS-1$
			footer.setLength(0);
			footer.append(mDue.getAmountAsString()); //$NON-NLS-1$
		}
		
		tp.setFont("Helvetica", SWT.BOLD, 9); //$NON-NLS-1$
		tp.insertText(cursor, footer.toString(), SWT.LEFT);
		if (tcCode != null) {
			esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
		}
		
		if (text.getPlugin().print(printer, tarmedTray, false) == false) {
			// avoid dead letters
			deleteBrief();
			;
			Hub.setMandant(mSave);
			return false;
		}
		monitor.worked(2);
		// avoid dead letters
		deleteBrief();
		;
		Hub.setMandant(mSave);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// never mind
		}
		return true;
	}
	
	private String getEANList(String[] eans){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < eans.length; i++) {
			if (i > 0)
				sb.append("  ");
			sb.append(Integer.toString(i + 1) + "/" + eans[i]);
		}
		return sb.toString();
	}
	
	private String[] getEANArray(HashSet<String> responsibleEANs){
		String[] eans = responsibleEANs.toArray(new String[responsibleEANs.size()]);
		return eans;
	}
	
	private HashMap<String, String> getEANHashMap(String[] eans){
		HashMap<String, String> ret = new HashMap<String, String>();
		for (int i = 0; i < eans.length; i++) {
			ret.put(eans[i], Integer.toString(i + 1));
		}
		return ret;
	}
	
	private void insertPage(final int page, final Kontakt adressat, final Rechnung rn){
		createBrief("Tarmedrechnung_S2", adressat);
		replaceHeaderFields(text, rn);
		text.replace("\\[Seite\\]", StringTool.pad(StringTool.LEFT, '0', Integer.toString(page), 2)); //$NON-NLS-1$
	}
	
	/*
	 * private TextContainer insertPage(final int page, final Kontakt adressat, TextContainer text,
	 * final Rechnung rn){
	 * 
	 * if(--existing<0){ ctF=addItem("Tarmedrechnung_S2",Messages.RnPrintView_page+page,adressat);
	 * //$NON-NLS-1$ }else{ ctF=ctab.getItem(page); useItem(page,"Tarmedrechnung_S2", adressat);
	 * //$NON-NLS-1$ } text=(TextContainer) ctF.getData("text"); //$NON-NLS-1$
	 * replaceHeaderFields(text, rn); text.replace("\\[Seite\\]",StringTool.pad(SWT
	 * .LEFT,'0',Integer.toString(page),2)); //$NON-NLS-1$ return text;
	 * 
	 * }
	 */
	private Object print(final Object cur, final ITextPlugin p, final boolean small,
		final String text){
		if (small) {
			p.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
		} else {
			p.setFont("Helvetica", SWT.NORMAL, 9); //$NON-NLS-1$
		}
		return p.insertText(cur, text, SWT.LEFT);
	}
	
	private String getValue(final Element s, final String field){
		String ret = s.getAttributeValue(field);
		if (StringTool.isNothing(ret)) {
			return " "; //$NON-NLS-1$
		}
		return ret;
	}
	
	private void replaceHeaderFields(final TextContainer text, final Rechnung rn){
		Fall fall = rn.getFall();
		Mandant m = rn.getMandant();
		text.replace("\\[F1\\]", rn.getRnId()); //$NON-NLS-1$
		
		String titel;
		String titelMahnung;
		
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) { //$NON-NLS-1$
			titel = Messages.RnPrintView_tbBill;
			
			switch (rn.getStatus()) {
			case RnStatus.MAHNUNG_1_GEDRUCKT:
			case RnStatus.MAHNUNG_1:
				titelMahnung = Messages.RnPrintView_firstM;
				break;
			case RnStatus.MAHNUNG_2:
			case RnStatus.MAHNUNG_2_GEDRUCKT:
				titelMahnung = Messages.RnPrintView_secondM;
				break;
			case RnStatus.IN_BETREIBUNG:
			case RnStatus.TEILVERLUST:
			case RnStatus.TOTALVERLUST:
			case RnStatus.MAHNUNG_3:
			case RnStatus.MAHNUNG_3_GEDRUCKT:
				titelMahnung = Messages.RnPrintView_thirdM;
				break;
			default:
				titelMahnung = ""; //$NON-NLS-1$
			}
			;
		} else {
			titel = Messages.RnPrintView_getback;
			titelMahnung = ""; //$NON-NLS-1$
		}
		
		text.replace("\\[Titel\\]", titel); //$NON-NLS-1$
		text.replace("\\[TitelMahnung\\]", titelMahnung); //$NON-NLS-1$
		
		if (fall.getAbrechnungsSystem().equals("IV")) { //$NON-NLS-1$
			text.replace("\\[NIF\\]", TarmedRequirements.getNIF(m)); //$NON-NLS-1$
			String ahv = TarmedRequirements.getAHV(fall.getPatient());
			if (StringTool.isNothing(ahv)) {
				ahv = fall.getRequiredString("AHV-Nummer");
			}
			text.replace("\\[F60\\]", ahv); //$NON-NLS-1$
		} else {
			text.replace("\\[NIF\\]", TarmedRequirements.getKSK(m)); //$NON-NLS-1$
			text.replace("\\[F60\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		text.replace("\\?\\?\\??[a-zA-Z0-9 \\.]+\\?\\?\\??", "");
		
	}
	
	private class RnComparator implements Comparator<Element> {
		TimeTool tt0 = new TimeTool();
		TimeTool tt1 = new TimeTool();
		
		public int compare(Element e0, Element e1){
			if (!tt0.set(e0.getAttributeValue("date_begin"))) {
				return 1;
			}
			if (!tt1.set(e1.getAttributeValue("date_begin"))) {
				return -1;
			}
			int dat = tt0.compareTo(tt1);
			if (dat != 0) {
				return dat;
			}
			String t0 = e0.getAttributeValue(XMLExporter.ATTR_TARIFF_TYPE);
			String t1 = e1.getAttributeValue(XMLExporter.ATTR_TARIFF_TYPE);
			if (t0.equals("001")) { // tarmed-tarmed: nach code sortieren
				if (t1.equals("001")) {
					String c0 = e0.getAttributeValue(XMLExporter.ATTR_CODE);
					String c1 = e1.getAttributeValue(XMLExporter.ATTR_CODE);
					return c0.compareTo(c1);
				} else {
					return -1; // tarmed immer oberhab nicht-tarmed
				}
			} else if (t1.equals("001")) {
				return 1; // nicht-tarmed immer unterhalb tarmed
			} else { // nicht-tarmed - nicht-tarmed: alphabetisch
				int diffc = t0.compareTo(t1);
				if (diffc == 0) {
					diffc = e0.getText().compareToIgnoreCase(e1.getText());
				}
				return diffc;
			}
		}
	}
	
	/**
	 * Make a guess for the correct code value for the provided vat rate. Guessing is necessary as
	 * the correct code is not part of the XML invoice.
	 * 
	 * @param vatRate
	 * @return
	 */
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
