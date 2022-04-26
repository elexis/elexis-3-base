package ch.elexis.tarmed.printer;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.jdom.Document;
import org.jdom.Element;

import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.RnStatus;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class XMLPrinterUtil {

	public static void updateContext(Rechnung rechnung, Fall fall, Patient patient, Mandant mandant,
			Rechnungssteller rechnungssteller, String paymentMode) {
		ElexisEventDispatcher.fireSelectionEvents(rechnung, fall, patient, rechnungssteller);

		ICoverage coverage = CoreModelServiceHolder.get().load(fall.getId(), ICoverage.class).orElse(null);
		IMandator mandator = CoreModelServiceHolder.get().load(mandant.getId(), IMandator.class).orElse(null);
		IContact biller = CoreModelServiceHolder.get().load(rechnungssteller.getId(), IMandator.class).orElse(null);

		// make sure the Textplugin can replace all fields
		fall.setInfoString("payment", paymentMode);
		fall.setInfoString("Gesetz", TarmedRequirements.getGesetz(coverage));
		mandant.setInfoElement("EAN", TarmedRequirements.getEAN(mandator));
		rechnungssteller.setInfoElement("EAN", TarmedRequirements.getEAN(biller));
		mandant.setInfoElement("KSK", TarmedRequirements.getKSK(mandator));
		mandant.setInfoElement("NIF", TarmedRequirements.getNIF(mandator));
		if (!mandant.equals(rechnungssteller)) {
			rechnungssteller.setInfoElement("EAN", TarmedRequirements.getEAN(biller));
			rechnungssteller.setInfoElement("KSK", TarmedRequirements.getKSK(biller));
			rechnungssteller.setInfoElement("NIF", TarmedRequirements.getNIF(biller));
		}
	}

	/**
	 * Make a guess for the correct code value for the provided vat rate. Guessing
	 * is necessary as the correct code is not part of the XML invoice.
	 *
	 * @param vatRate
	 * @return
	 */
	public static int guessVatCode(String vatRate) {
		if (vatRate != null && !vatRate.isEmpty()) {
			double scale = Double.parseDouble(vatRate);
			// make a guess for the correct code
			if (scale == 0)
				return 0;
			else if (scale < 7)
				return 1;
			else
				return 2;
		}
		return 0;
	}

	public static void insertPage(String templateName, final int page, final Kontakt adressat, final Rechnung rn,
			final Document xmlRn, final String paymentMode, TextContainer text) {
		createBrief(templateName, adressat, text);
		replaceHeaderFields(text, rn, xmlRn, paymentMode);
		text.replace("\\[Seite\\]", StringTool.pad(StringTool.LEFT, '0', Integer.toString(page), 2)); //$NON-NLS-1$
	}

	public static Brief createBrief(final String template, final Kontakt adressat, TextContainer text) {
		return text.createFromTemplateName(null, template, Brief.RECHNUNG, adressat, Messages.RnPrintView_tarmedBill);
	}

	public static boolean deleteBrief(Brief brief) {
		if (brief != null) {
			return brief.delete();
		}
		return true;
	}

	public static String getEANList(String[] eans) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < eans.length; i++) {
			if (i > 0)
				sb.append("  ");
			sb.append(Integer.toString(i + 1) + "/" + eans[i]);
		}
		return sb.toString();
	}

	public static String[] getEANArray(HashSet<String> responsibleEANs) {
		String[] eans = responsibleEANs.toArray(new String[responsibleEANs.size()]);
		return eans;
	}

	public static HashMap<String, String> getEANHashMap(String[] eans) {
		HashMap<String, String> ret = new HashMap<String, String>();
		for (int i = 0; i < eans.length; i++) {
			ret.put(eans[i], Integer.toString(i + 1));
		}
		return ret;
	}

	public static void replaceHeaderFields(final TextContainer text, final Rechnung rn, final Document xmlRn,
			final String paymentMode) {
		Fall fall = rn.getFall();
		Mandant m = rn.getMandant();

		String titel;
		String titelMahnung;

		// implementation specific headers
		if (XMLExporter.getXmlVersion(xmlRn.getRootElement()).equals("4.0")) {
			replace40HeaderFields(text, rn, xmlRn);
		} else if (XMLExporter.getXmlVersion(xmlRn.getRootElement()).equals("4.4")) {
			replace44HeaderFields(text, rn, xmlRn);
		}

		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) { // $NON-NLS-1$
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

		IMandator mandator = CoreModelServiceHolder.get().load(m.getId(), IMandator.class).orElse(null);
		IPatient patient = CoreModelServiceHolder.get().load(fall.getPatient().getId(), IPatient.class).orElse(null);

		if (fall.getAbrechnungsSystem().equals("IV")) { //$NON-NLS-1$
			text.replace("\\[NIF\\]", TarmedRequirements.getNIF(mandator)); //$NON-NLS-1$
			String ahv = TarmedRequirements.getAHV(patient);
			if (StringTool.isNothing(ahv)) {
				ahv = fall.getRequiredString("AHV-Nummer");
			}
			text.replace("\\[F60\\]", ahv); //$NON-NLS-1$
		} else {
			text.replace("\\[NIF\\]", TarmedRequirements.getKSK(mandator)); //$NON-NLS-1$
			text.replace("\\[F60\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		text.replace("\\?\\?\\??[a-zA-Z0-9 \\.]+\\?\\?\\??", "");
	}

	private static void replace44HeaderFields(TextContainer text, Rechnung rn, Document xmlRn) {
		Element xmlPayload = xmlRn.getRootElement().getChild("payload", XMLExporter.nsinvoice);
		Element xmlInvoice = xmlPayload.getChild("invoice", XMLExporter.nsinvoice);
		if (xmlInvoice != null) {
			String requestId = xmlInvoice.getAttributeValue(XMLExporter.ATTR_REQUEST_ID);
			String requestDate = xmlInvoice.getAttributeValue(XMLExporter.ATTR_REQUEST_DATE);
			TimeTool date = new TimeTool(requestDate);
			text.replace("\\[F1\\]", //$NON-NLS-1$
					requestId + " - " + date.toString(TimeTool.DATE_GER) + " " + date.toString(TimeTool.TIME_FULL));
		} else {
			text.replace("\\[F1\\]", rn.getRnId()); //$NON-NLS-1$
		}
	}

	private static void replace40HeaderFields(TextContainer text, Rechnung rn, Document xmlRn) {
		text.replace("\\[F1\\]", rn.getRnId()); //$NON-NLS-1$
	}

	public static String getValue(final Element s, final String field) {
		String ret = s.getAttributeValue(field);
		if (StringTool.isNothing(ret)) {
			return " "; //$NON-NLS-1$
		}
		return ret;
	}

	public static String getValue(final Element s, final String field, String defaultValue) {
		String ret = s.getAttributeValue(field);
		if (StringTool.isNothing(ret)) {
			return defaultValue;
		}
		return ret;
	}

	public static Object print(final Object cur, final ITextPlugin p, final int size, final int align, boolean bold,
			final String text) {
		if (bold) {
			p.setFont("Helvetica", SWT.BOLD, size); //$NON-NLS-1$
		} else {
			p.setFont("Helvetica", SWT.NORMAL, size); //$NON-NLS-1$
		}
		return p.insertText(cur, text, align);
	}

	public static Object print(final Object cur, final ITextPlugin p, final boolean small, final String text) {
		if (small) {
			p.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
		} else {
			p.setFont("Helvetica", SWT.NORMAL, 9); //$NON-NLS-1$
		}
		return p.insertText(cur, text, SWT.LEFT);
	}
}
