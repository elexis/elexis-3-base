package ch.elexis.tarmed.printer;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.TarmedRechnung.XMLExporterBalance;
import ch.elexis.TarmedRechnung.XMLExporterServices;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter.TYPE;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.data.util.SortedList;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.tarmed.printer.EZPrinter.EZPrinterData;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

public class XML44Printer {
	
	private static Logger logger = LoggerFactory.getLogger(XML44Printer.class);
	
	private static double cmPerLine = 0.67; // Höhe pro Zeile (0.65 plus Toleranz)
	private static double cmFirstPage = 13.0; // Platz auf der ersten Seite
	private static double cmMiddlePage = 21.0; // Platz auf Folgeseiten
	private static double cmFooter = 4; // Platz für Endabrechnung
	
	private TextContainer text;

	private Brief actBrief;
	
	private double cmAvail = 21.4; // Verfügbare Druckhöhe in cm
	
	private String printer;
	
	private static DecimalFormat df = new DecimalFormat(StringConstants.DOUBLE_ZERO);
	
	private static Namespace namespace = Namespace.getNamespace("invoice", //$NON-NLS-1$
		"http://www.forum-datenaustausch.ch/invoice"); //$NON-NLS-1$

	public XML44Printer(TextContainer text){
		this.text = text;
	}

	private EZPrinter.EZPrinterData getEZPrintData(XMLExporterBalance xmlBalance,
		XMLExporterServices xmlServices, Element body){
		EZPrinter.EZPrinterData ret = new EZPrinter.EZPrinterData();
		
		ret.amountTarmed = xmlServices.getTarmedMoney();
		ret.amountDrug = xmlServices.getMedikamentMoney();
		ret.amountLab = xmlServices.getAnalysenMoney();
		ret.amountMigel = xmlServices.getMigelMoney();
		ret.amountPhysio = xmlServices.getPhysioMoney();
		ret.amountUnclassified = xmlServices.getUebrigeMoney();
		
		ret.due = xmlBalance.getDue();
		ret.paid = xmlBalance.getPrepaid();
		
		Element eTiers = body.getChild(XMLExporter.ELEMENT_TIERS_GARANT, namespace);
		if (eTiers == null) {
			ret.paymentMode = XMLExporter.TIERS_PAYANT;
		}
		return ret;
	}

	public boolean doPrint(Rechnung rn, Document xmlRn, TYPE rnType, String saveFile,
		boolean withESR, boolean withForms, boolean doVerify, IProgressMonitor monitor){
		
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

		Element payload = xmlRn.getRootElement().getChild("payload", namespace);
		Element body = payload.getChild("body", namespace);
		Element balance = body.getChild("balance", namespace);
		Element services = body.getChild("services", namespace);
		XMLExporterBalance xmlBalance = new XMLExporterBalance(balance);
		XMLExporterServices xmlServices = new XMLExporterServices(services);
		EZPrinterData ezData = getEZPrintData(xmlBalance, xmlServices, body);
		
		String tcCode = null;
		if (TarmedRequirements.hasTCContract(rs)
			&& ezData.paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			tcCode = TarmedRequirements.getTCCode(rs);
		} else if (ezData.paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			tcCode = "01";
		}
		XMLPrinterUtil.updateContext(rn, fall, pat, mnd, rs, ezData.paymentMode);
		
		Kontakt adressat;
		if (ezData.paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			adressat = fall.getRequiredContact(TarmedRequirements.INSURANCE);
		} else {
			adressat = fall.getGarant();
		}
		if ((adressat == null) || (!adressat.exists())) {
			adressat = pat;
		}
		adressat.getPostAnschrift(true); // damit sicher eine existiert
		
		String offenRp = ezData.due.getCentsAsString();
		ESR esr =
			new ESR(rs.getInfoString(TarmedACL.getInstance().ESRNUMBER), rs.getInfoString(TarmedACL
				.getInstance().ESRSUB), rn.getRnId(), ESR.ESR27);
		
		if (withESR == true) {
			EZPrinter ezPrinter = new EZPrinter();
			actBrief = ezPrinter.doPrint(rn, ezData, text, esr, monitor);
		}
		if (withForms == false) {
			// avoid dead letters
			XMLPrinterUtil.deleteBrief(actBrief);
			Hub.setMandant(mSave);
			return true;
		}
		printer = CoreHub.localCfg.get("Drucker/A4/Name", null); //$NON-NLS-1$
		String tarmedTray = CoreHub.localCfg.get("Drucker/A4/Schacht", null); //$NON-NLS-1$
		if (StringTool.isNothing(tarmedTray)) {
			tarmedTray = null;
		}
		XMLPrinterUtil.createBrief("TR44_S1", adressat, text);
		
		StringBuilder sb = new StringBuilder();
		Element root = xmlRn.getRootElement();
		Namespace ns = root.getNamespace();
		//Element invoice=root.getChild("invoice",ns); //$NON-NLS-1$
		if (payload.getAttributeValue("copy").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
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
		
		Element treatment = body.getChild("treatment", ns); //$NON-NLS-1$
		Element diagnosis = treatment.getChild("diagnosis", ns); //$NON-NLS-1$
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
			text.replace("\\[F53\\]", diagnosis.getText()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// lookup EAN numbers in services and set field 98
		HashSet<String> eanUniqueSet = new HashSet<String>();
		List allServices = services.getChildren(); //$NON-NLS-1$
		for (Object object : allServices) {
			if (object instanceof Element) {
				Element service = (Element) object;
				String tariftype = service.getAttributeValue("tariff_type");
				// look into all tarmed 001 and physio 311 services
				if (tariftype != null && (tariftype.equals("001") || tariftype.equals("311"))) {
					String ean_responsible = service.getAttributeValue("responsible_id");
					if (ean_responsible != null && !ean_responsible.isEmpty()) {
						eanUniqueSet.add(ean_responsible);
					}
					String ean_provider = service.getAttributeValue("provider_id");
					if (ean_provider != null && !ean_provider.isEmpty()) {
						eanUniqueSet.add(ean_provider);
					}
				}
			}
		}
		String[] eanArray = XMLPrinterUtil.getEANArray(eanUniqueSet);
		HashMap<String, String> eanMap = XMLPrinterUtil.getEANHashMap(eanArray);
		text.replace("\\[F98\\]", XMLPrinterUtil.getEANList(eanArray));
		
		Kontakt zuweiser = fall.getRequiredContact("Zuweiser");
		if (zuweiser != null) {
			String ean = TarmedRequirements.getEAN(zuweiser);
			if (!ean.equals(TarmedRequirements.EAN_PSEUDO)) {
				text.replace("\\[F23\\]", ean);
			}
		}
		
		@SuppressWarnings("unchecked")
		SortedList<Element> ls =
			new SortedList<Element>(services.getChildren(), new RnComparator());
		
		Element remark = body.getChild(XMLExporter.ELEMENT_REMARK); //$NON-NLS-1$
		if (remark != null) {
			final String rem = remark.getText();
			text.getPlugin().findOrReplace(Messages.RnPrintView_remark, new ReplaceCallback() {
				@Override
				public String replace(final String in){
					return Messages.RnPrintView_remarksp + rem;
				}
			});
		}
		XMLPrinterUtil.replaceHeaderFields(text, rn, ezData.paymentMode);
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
			tp.setFont("Helvetica", SWT.NORMAL, 8); //$NON-NLS-1$
			sb.setLength(0);
			if (r.set(s.getAttributeValue("date_begin")) == false) { //$NON-NLS-1$
				continue;
			}
			sb.append(r.toString(TimeTool.DATE_GER)).append("\t"); //$NON-NLS-1$
			sb.append(XMLPrinterUtil.getValue(s, "tariff_type")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "ref_code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "session", "1")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			if (XMLPrinterUtil.getValue(s, "body_location").startsWith("l")) //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("L\t");
			else if (XMLPrinterUtil.getValue(s, "body_location").startsWith("r")) //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("R\t");
			else
				sb.append(" \t");
			sb.append(XMLPrinterUtil.getValue(s, "quantity")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "unit_mt")).append("\t"); //$NON-NLS-1$
			sb.append(XMLPrinterUtil.getValue(s, "scale_factor_mt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "unit_factor_mt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$

			sb.append(XMLPrinterUtil.getValue(s, "unit_tt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "scale_factor_tt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "unit_factor_tt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			
			// set responsible (field 77) and provider (field 78)
			String tariftype = s.getAttributeValue("tariff_type");
			// look into all tarmed 001 and physio 311 services
			if (tariftype != null && (tariftype.equals("001") || tariftype.equals("311"))) {
				String ean_provider = s.getAttributeValue("provider_id"); //$NON-NLS-1$
				if (ean_provider != null && !ean_provider.isEmpty()) {
					sb.append(eanMap.get(ean_provider) + "\t"); //$NON-NLS-1$
				}
				String ean_responsible = s.getAttributeValue("responsible_id"); //$NON-NLS-1$
				if (ean_responsible != null && !ean_responsible.isEmpty()) {
					sb.append(eanMap.get(ean_responsible) + "\t"); //$NON-NLS-1$
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
				XMLPrinterUtil.deleteBrief(actBrief);
				logger.error("Fehlerhaftes Format für amount bei " + sb.toString());
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
			sb.append(
				Integer.toString(XMLPrinterUtil.guessVatCode(XMLPrinterUtil.getValue(s, "vat_rate")))).append("\t"); //$NON-NLS-1$
			
			sb.append(am);
			seitentotal += dLine;
			sb.append("\n"); //$NON-NLS-1$
			cursor = tp.insertText(cursor, sb.toString(), SWT.LEFT);

			tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
			cursor = tp.insertText(cursor, "\t" + s.getAttributeValue("name") + "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$

			cmAvail -= cmPerLine;
			if (cmAvail <= 0) {
				StringBuilder footer = new StringBuilder();
				cursor = tp.insertText(cursor, "\n\n", SWT.LEFT); //$NON-NLS-1$
				footer
					.append("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tZwischentotal\t").append(df.format(seitentotal)); //$NON-NLS-1$
				tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
				cursor = tp.insertText(cursor, footer.toString(), SWT.LEFT);
				seitentotal = 0.0;
				if (tcCode != null) {
					esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
				}
				
				if (text.getPlugin().print(printer, tarmedTray, false) == false) {
					// avoid dead letters
					XMLPrinterUtil.deleteBrief(actBrief);
					Hub.setMandant(mSave);
					return false;
				}
				
				XMLPrinterUtil
					.insertPage("TR44_S2", ++page, adressat, rn, ezData.paymentMode, text);
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
				XMLPrinterUtil.deleteBrief(actBrief);
				Hub.setMandant(mSave);
				return false;
			}
			XMLPrinterUtil.insertPage("TR44_S2", ++page, adressat, rn, ezData.paymentMode, text);
			cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			monitor.worked(2);
		}
		
		cursor = text.getPlugin().insertTextAt(0, 250, 190, 45, " ", SWT.LEFT); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "Code\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "Satz\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "Betrag\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "MWSt\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "MWSt.-Nr.:\t"); //$NON-NLS-1$
		Element vat = balance.getChild("vat", ns);
		String vatNumber = XMLPrinterUtil.getValue(vat, "vat_number");
		if (vatNumber.equals(" ")) {
			vatNumber = "keine";
		} else {
			vatNumber = vatNumber + " MWST";
		}
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, vatNumber + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "Anzahlung:\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, ezData.paid + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, true, "Gesamtbetrag:\t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, false, xmlBalance.getTotal() + "\n"); //$NON-NLS-1$
		// second line
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "0\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatRate(0, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatAmount(0, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatVat(0, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, true, "Währung:\t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "CHF\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "\t\t\t\t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, true, "davon PFL:\t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, false, xmlBalance.getAmountObligations()
				+ "\n"); //$NON-NLS-1$
		// third line
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "1\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatRate(1, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatAmount(1, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatVat(1, vat) + "\n"); //$NON-NLS-1$
		// forth line
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "2\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatRate(2, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatAmount(2, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, getVatVat(2, vat) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "\t\t\t\t\t\t\t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, true, "Fälliger Betrag:\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, true, xmlBalance.getDue() + "\n"); //$NON-NLS-1$
		
		if (tcCode != null) {
			esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
		}
		
		if (text.getPlugin().print(printer, tarmedTray, false) == false) {
			// avoid dead letters
			XMLPrinterUtil.deleteBrief(actBrief);
			Hub.setMandant(mSave);
			return false;
		}
		monitor.worked(2);
		// avoid dead letters
		XMLPrinterUtil.deleteBrief(actBrief);
		Hub.setMandant(mSave);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// never mind
		}
		return true;
	}
	
	private String getVatVat(int code, Element vat){
		Element vatrate = getVatRateElement(code, vat);
		String rate = vatrate.getAttributeValue("vat");
		if (rate != null && !rate.isEmpty()) {
			return rate;
		}
		return "0.00";
	}
	
	private String getVatAmount(int code, Element vat){
		Element vatrate = getVatRateElement(code, vat);
		String amount = vatrate.getAttributeValue("amount");
		if (amount != null && !amount.isEmpty()) {
			return amount;
		}
		return "0.00";
	}
	
	private String getVatRate(int code, Element vat){
		Element vatrate = getVatRateElement(code, vat);
		String rate = vatrate.getAttributeValue("vat_rate");
		if (rate != null && !rate.isEmpty()) {
			return rate;
		}
		return "0.00";
	}
	
	private Element getVatRateElement(int code, Element vat){
		List<?> children = vat.getChildren();
		for (Object object : children) {
			if (object instanceof Element) {
				Element element = (Element) object;
				String rate = element.getAttributeValue("vat_rate");
				int rateCode = XMLPrinterUtil.guessVatCode(rate);
				if (rateCode == code) {
					return element;
				}
			}
		}
		return null;
	}
}
