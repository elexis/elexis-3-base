package ch.elexis.tarmed.printer;

import static ch.elexis.tarmed.printer.TarmedTemplateRequirement.TT_TARMED_S1;
import static ch.elexis.tarmed.printer.TarmedTemplateRequirement.TT_TARMED_S2;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

public class XML40Printer {
	
	private static Logger logger = LoggerFactory.getLogger(XML40Printer.class);
	
	private static double cmPerLine = 0.67; // Höhe pro Zeile (0.65 plus Toleranz)
	private static double cmFirstPage = 13.0; // Platz auf der ersten Seite
	private static double cmMiddlePage = 21.0; // Platz auf Folgeseiten
	private static double cmFooter = 4.5; // Platz für Endabrechnung
	
	private TarmedACL ta = TarmedACL.getInstance();
	
	private TextContainer text;
	
	private Brief actBrief;
	
	private double cmAvail = 21.4; // Verfügbare Druckhöhe in cm
	
	private String printer;
	
	private static DecimalFormat df = new DecimalFormat(StringConstants.DOUBLE_ZERO);
	
	private static Namespace namespace = Namespace
		.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
	
	public XML40Printer(TextContainer text){
		this.text = text;
	}
	
	private EZPrinter.EZPrinterData getEZPrintData(Element balance, Element invoice){
		EZPrinter.EZPrinterData ret = new EZPrinter.EZPrinterData();
		
		try {
			ret.amountTarmed =
				XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_TARMED));
			ret.amountDrug =
				XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_DRUG));
			ret.amountLab =
				XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_LAB));
			ret.amountMigel =
				XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_MIGEL));
			ret.amountPhysio =
				XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_PHYSIO));
			ret.amountUnclassified = XMLTool
				.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_UNCLASSIFIED));
			
			ret.due =
				XMLTool.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_DUE));
			ret.paid =
				XMLTool
					.xmlDoubleToMoney(balance.getAttributeValue(XMLExporter.ATTR_AMOUNT_PREPAID));
			
			Element eTiers = invoice.getChild(XMLExporter.ELEMENT_TIERS_GARANT, namespace);
			if (eTiers == null) {
				ret.paymentMode = XMLExporter.TIERS_PAYANT;
			}
		} catch (NumberFormatException e) {
			// just log and work with default values
			logger.error("Getting data for EZ failed.", e);
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
		
		Element invoice = xmlRn.getRootElement().getChild(XMLExporter.ELEMENT_INVOICE, namespace);
		Element balance = invoice.getChild(XMLExporter.ELEMENT_BALANCE, namespace);
		EZPrinterData ezData = getEZPrintData(balance, invoice);
		
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
		Kontakt legalGuardian = pat.getLegalGuardian();
		if ((adressat == null) || (!adressat.exists()) || legalGuardian != null) {
			if (legalGuardian != null) {
				adressat = legalGuardian;
			} else {
				adressat = pat;
			}
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
		XMLPrinterUtil.createBrief(TT_TARMED_S1, adressat, text);
		
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
		List allServices = detail.getChild(XMLExporterServices.ELEMENT_SERVICES, ns).getChildren(); //$NON-NLS-1$
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
		String[] eanArray = XMLPrinterUtil.getEANArray(eanUniqueSet);
		HashMap<String, String> eanMap = XMLPrinterUtil.getEANHashMap(eanArray);
		text.replace("\\[F98\\]", XMLPrinterUtil.getEANList(eanArray));
		
		Kontakt zuweiser = fall.getRequiredContact("Zuweiser");
		if (zuweiser != null) {
			String ean = TarmedRequirements.getEAN(zuweiser);
			if (ean != null && !ean.equals(TarmedRequirements.EAN_PSEUDO)) {
				text.replace("\\[F23\\]", ean);
			}
		}
		
		Element services = detail.getChild(XMLExporterServices.ELEMENT_SERVICES, ns); //$NON-NLS-1$
		@SuppressWarnings("unchecked")
		SortedList<Element> ls =
			new SortedList<Element>(services.getChildren(), new RnComparator());
		
		Element remark = invoice.getChild(XMLExporter.ELEMENT_REMARK); //$NON-NLS-1$
		if (remark != null) {
			final String rem = remark.getText();
			text.getPlugin().findOrReplace(Messages.RnPrintView_remark, new ReplaceCallback() {
				@Override
				public String replace(final String in){
					return Messages.RnPrintView_remarksp + rem;
				}
			});
		}
		XMLPrinterUtil.replaceHeaderFields(text, rn, xmlRn, ezData.paymentMode);
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
			sb.append(XMLPrinterUtil.getValue(s, "tariff_type")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "ref_code")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "number")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			if (XMLPrinterUtil.getValue(s, "body_location").startsWith("l")) //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("L\t");
			else if (XMLPrinterUtil.getValue(s, "body_location").startsWith("r")) //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("R\t");
			else
				sb.append(" \t");
			sb.append(XMLPrinterUtil.getValue(s, "quantity")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			String val = s.getAttributeValue("unit.mt"); //$NON-NLS-1$
			if (StringTool.isNothing(val)) {
				val = s.getAttributeValue("unit"); //$NON-NLS-1$
				if (StringTool.isNothing(val)) {
					val = "\t"; //$NON-NLS-1$
				}
			}
			sb.append(val).append("\t"); //$NON-NLS-1$
			sb.append(XMLPrinterUtil.getValue(s, "scale_factor.mt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			val = s.getAttributeValue("unit_factor.mt"); //$NON-NLS-1$
			if (StringTool.isNothing(val)) {
				val = s.getAttributeValue("unit_factor"); //$NON-NLS-1$
				if (StringTool.isNothing(val)) {
					val = "\t"; //$NON-NLS-1$
				}
			}
			sb.append(val).append("\t"); //$NON-NLS-1$
			sb.append(XMLPrinterUtil.getValue(s, "unit.tt")).append("\t\t"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(XMLPrinterUtil.getValue(s, "unit_factor.tt")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$
			
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
					XMLPrinterUtil.deleteBrief(actBrief);
					Hub.setMandant(mSave);
					return false;
				}
				
				XMLPrinterUtil.insertPage(TT_TARMED_S2, ++page, adressat, rn, xmlRn,
					ezData.paymentMode, text);
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
			XMLPrinterUtil.insertPage(TT_TARMED_S2, ++page, adressat, rn, xmlRn,
				ezData.paymentMode, text);
			cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			monitor.worked(2);
		}
		StringBuilder footer = new StringBuilder(100);
		//Element balance=invoice.getChild("balance",ns); //$NON-NLS-1$
		
		cursor = text.getPlugin().insertTextAt(0, 220, 190, 45, " ", SWT.LEFT); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, "\tTARMED AL \t"); //$NON-NLS-1$
		footer.append(balance.getAttributeValue("amount_tarmed.mt")) //$NON-NLS-1$
			.append("  (").append(balance.getAttributeValue("unit_tarmed.mt")).append(")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cursor = XMLPrinterUtil.print(cursor, tp, false, footer.toString());
		cursor = XMLPrinterUtil.print(cursor, tp, true, "Physio \t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, false,
				XMLPrinterUtil.getValue(balance, "amount_physio")); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, "\tMiGeL \t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, false,
				XMLPrinterUtil.getValue(balance, "amount_migel")); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, "\tÜbrige \t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, false,
				XMLPrinterUtil.getValue(balance, "amount_unclassified")); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, "\n\tTARMED TL \t"); //$NON-NLS-1$
		footer.setLength(0);
		footer.append(balance.getAttributeValue("amount_tarmed.tt")) //$NON-NLS-1$
			.append("  (").append(balance.getAttributeValue("unit_tarmed.tt")).append(")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cursor = XMLPrinterUtil.print(cursor, tp, false, footer.toString());
		cursor = XMLPrinterUtil.print(cursor, tp, true, "Labor \t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, false, XMLPrinterUtil.getValue(balance, "amount_lab")); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, "\tMedi \t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil
				.print(cursor, tp, false, XMLPrinterUtil.getValue(balance, "amount_drug")); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, "\tKantonal \t"); //$NON-NLS-1$
		cursor =
			XMLPrinterUtil.print(cursor, tp, false,
				XMLPrinterUtil.getValue(balance, "amount_cantonal")); //$NON-NLS-1$
		
		footer.setLength(0);
		footer.append("\n\n").append("■ Gesamtbetrag\t\tCHF\t\t").append(df.format(sumTotal)) //$NON-NLS-1$ //$NON-NLS-2$
			.append("\tdavon PFL \t").append(df.format(sumPfl)).append("\tAnzahlung \t") //$NON-NLS-1$ //$NON-NLS-2$
			.append(ezData.paid.getAmountAsString())
			.append("\tFälliger Betrag \t").append(ezData.due.getAmountAsString()); //$NON-NLS-1$
		
		Element vat = balance.getChild("vat", ns);
		String vatNumber = XMLPrinterUtil.getValue(vat, "vat_number");
		if (vatNumber.equals(" "))
			vatNumber = "keine";
		
		footer.append("\n\n■ MwSt.Nr. \t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, true, footer.toString());
		cursor = XMLPrinterUtil.print(cursor, tp, false, vatNumber + "\n\n"); //$NON-NLS-1$
		
		Boolean isVat =
			(Boolean) mnd.getRechnungssteller().getInfoElement(XMLExporter.VAT_ISMANDANTVAT);
		if (isVat != null && isVat) {
			cursor = XMLPrinterUtil.print(cursor, tp, true, "  Code\tSatz\t\tBetrag\t\tMwSt\n"); //$NON-NLS-1$
			tp.setFont("Helvetica", SWT.NORMAL, 9); //$NON-NLS-1$
			footer.setLength(0);
			
			List<Element> rates = vat.getChildren();
			
			// get vat lines ordered by code
			List<String> vatLines = new ArrayList<String>();
			for (Element rate : rates) {
				StringBuilder vatBuilder = new StringBuilder();
				int code = XMLPrinterUtil.guessVatCode(XMLPrinterUtil.getValue(rate, "vat_rate"));
				// set amount of tabs needed according, use 7
				String amount = XMLPrinterUtil.getValue(rate, "amount");
				String tabs = "\t\t";
				if (amount.length() > 7)
					tabs = "\t";
				
				vatBuilder.append("■ ").append(Integer.toString(code)).append("\t")
					.append(XMLPrinterUtil.getValue(rate, "vat_rate")).append("\t\t")
					.append(amount).append(tabs)
					.append(XMLPrinterUtil.getValue(rate, "vat")).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			
			cursor = XMLPrinterUtil.print(cursor, tp, false, footer.toString());
			cursor = XMLPrinterUtil.print(cursor, tp, true, "\n Total\t\t\t"); //$NON-NLS-1$
			// set amount of tabs needed according to amount, use 8 as font is
			// bold
			String amount = ezData.due.getAmountAsString();
			String tabs = "\t\t";
			if (amount.length() > 8)
				tabs = "\t";
			
			footer.setLength(0);
			footer.append(amount).append(tabs).append(XMLPrinterUtil.getValue(vat, "vat")); //$NON-NLS-1$
		} else {
			cursor = XMLPrinterUtil.print(cursor, tp, true, "\n Total\t\t"); //$NON-NLS-1$
			footer.setLength(0);
			footer.append(ezData.due.getAmountAsString()); //$NON-NLS-1$
		}
		
		tp.setFont("Helvetica", SWT.BOLD, 9); //$NON-NLS-1$
		tp.insertText(cursor, footer.toString(), SWT.LEFT);
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
}
