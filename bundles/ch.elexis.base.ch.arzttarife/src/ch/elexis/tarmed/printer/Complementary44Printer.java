package ch.elexis.tarmed.printer;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IRnOutputter.TYPE;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.data.util.SortedList;
import ch.elexis.core.model.IContact;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
import ch.elexis.tarmed.printer.ComplementaryEZPrinter.EZPrinterData;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.fd.invoice440.request.BalanceType;
import ch.fd.invoice440.request.BodyType;
import ch.fd.invoice440.request.DiagnosisType;
import ch.fd.invoice440.request.GarantType;
import ch.fd.invoice440.request.RecordDRGType;
import ch.fd.invoice440.request.RecordDrugType;
import ch.fd.invoice440.request.RecordLabType;
import ch.fd.invoice440.request.RecordMigelType;
import ch.fd.invoice440.request.RecordOtherType;
import ch.fd.invoice440.request.RecordParamedType;
import ch.fd.invoice440.request.RecordServiceType;
import ch.fd.invoice440.request.RecordTarmedType;
import ch.fd.invoice440.request.ReminderType;
import ch.fd.invoice440.request.RequestType;
import ch.fd.invoice440.request.ServicesType;
import ch.fd.invoice440.request.TreatmentType;
import ch.fd.invoice440.request.VatRateType;
import ch.fd.invoice440.request.VatType;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class Complementary44Printer {
	
	private static Logger logger = LoggerFactory.getLogger(Complementary44Printer.class);
	
	private static final String FREETEXT = "freetext";
	private static final String BY_CONTRACT = "by_contract";
	private static final String SPACE = " ";
	
	private static double cmPerLine = 0.67; // Höhe pro Zeile (0.65 plus Toleranz)
	private static double cmFirstPage = 12.0; // Platz auf der ersten Seite
	private static double cmMiddlePage = 21.0; // Platz auf Folgeseiten
	private static double cmFooter = 4; // Platz für Endabrechnung
	private double cmAvail = 21.4; // Verfügbare Druckhöhe in cm
	
	private TextContainer text;
	
	private Brief actBrief;
	
	private String printer;
	private String tarmedTray;
	private TimeTool tTime;
	private double sideTotal;
	
	private Fall fall;
	private Patient pat;
	private Mandant rnMandant;
	private Rechnungssteller rnSteller;
	private RequestType request;
	
	private static DecimalFormat df = new DecimalFormat(StringConstants.DOUBLE_ZERO);
	
	public Complementary44Printer(TextContainer text){
		this.text = text;
		tTime = new TimeTool();
		
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(custom);
	}
	
	private EZPrinterData getEZPrintData(BalanceType balance, ServicesType services,
		BodyType body){
		EZPrinterData ret = new ComplementaryEZPrinter.EZPrinterData();
		XML44Services xmlServices = new XML44Services(services);
		
		ret.amountComplementary = xmlServices.getComplementaryMoney();
		ret.amountUnclassified = xmlServices.getOtherMoney();
		
		ret.due = new Money(balance.getAmountDue());
		// Subtract reminder if present, will be added by EZPrinter
		double dReminder = balance.getAmountReminder();
		if (dReminder > 0) {
			ret.due.subtractMoney(new Money(dReminder));
		}
		ret.paid = new Money(balance.getAmountPrepaid());
		
		GarantType eTiers = body.getTiersGarant();
		if (eTiers == null) {
			ret.paymentMode = XMLExporter.TIERS_PAYANT;
		}
		return ret;
	}
	
	public boolean doPrint(Rechnung rn, Document xmlRn, TYPE rnType, String saveFile,
		boolean withESR, boolean withForms, boolean doVerify, IProgressMonitor monitor){
		
		Mandant mSave = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		monitor.subTask(rn.getLabel());
		
		if (!initBasicInvoiceValues(rn, xmlRn)) {
			return false;
		}
		initPrinterSettings();
		
		BodyType body = request.getPayload().getBody();
		BalanceType balance = body.getBalance();
		ServicesType services = body.getServices();
		EZPrinterData ezData = getEZPrintData(balance, services, body);
		
		String tcCode = null;
		if (ezData.paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			tcCode = TarmedRequirements
				.getTCCode(CoreModelServiceHolder.get().load(rnSteller.getId(), IContact.class)
					.orElse(null));
		} else if (ezData.paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			tcCode = "01";
		}
		
		XMLPrinterUtil.updateContext(rn, fall, pat, rnMandant, rnSteller, ezData.paymentMode);
		
		ESR esr = new ESR(rnSteller.getInfoString(TarmedACL.getInstance().ESRNUMBER),
			rnSteller.getInfoString(TarmedACL.getInstance().ESRSUB), rn.getRnId(), ESR.ESR27);
		
		if (withESR == true) {
			ComplementaryEZPrinter ezPrinter = new ComplementaryEZPrinter();
			actBrief = ezPrinter.doPrint(rn, ezData, text, esr, monitor);
		}
		if (withForms == false) {
			// avoid dead letters
			XMLPrinterUtil.deleteBrief(actBrief);
			Hub.setMandant(mSave);
			return true;
		}
		
		Kontakt adressat = loadAddressee(ezData.paymentMode);
		XMLPrinterUtil.createBrief(ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S1, adressat,
			text);
		
		if (request.getPayload().isCopy()) {
			text.replace("\\[F5\\]", Messages.RnPrintView_yes); //$NON-NLS-1$
		} else {
			text.replace("\\[F5\\]", Messages.RnPrintView_no); //$NON-NLS-1$
		}
		
		addFallSpecificLines();
		addDiagnoses(body.getTreatment());
		addRemarks(body.getRemark());
		// adds values to reminder fields or "" if it's no reminder
		addReminderFields(request.getPayload().getReminder(), rn.getNr());
		
		List<Object> serviceRecords = services.getRecordTarmedOrRecordDrgOrRecordLab();
		
		// lookup EAN numbers in services
		String[] eanArray = initEanArray(serviceRecords);
		HashMap<String, String> eanMap = XMLPrinterUtil.getEANHashMap(eanArray);
		text.replace("\\[F98\\]", XMLPrinterUtil.getEANList(eanArray));
		
		// add the various record services 
		SortedList<Object> serviceRecordsSorted =
			new SortedList<Object>(serviceRecords, new Rn44Comparator());
		
		XMLPrinterUtil.replaceHeaderFields(text, rn, xmlRn, ezData.paymentMode);
		text.replace("\\[F.+\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Object cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
		int page = 1;
		sideTotal = 0.0;
		ITextPlugin tp = text.getPlugin();
		cmAvail = cmFirstPage;
		monitor.worked(2);
		StringBuilder sb = new StringBuilder();
		
		for (Object obj : serviceRecordsSorted) {
			tp.setFont("Helvetica", SWT.NORMAL, 8); //$NON-NLS-1$
			sb.setLength(0);
			String recText = "";
			String name = "";
			
			if (obj instanceof RecordServiceType) {
				RecordServiceType rec = (RecordServiceType) obj;
				recText = getRecordServiceString(rec, sb, eanMap);
				name = rec.getName();
			}
			
			if (recText == null) {
				continue;
			}
			cursor = tp.insertText(cursor, recText, SWT.LEFT);
			tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
			cursor = tp.insertText(cursor, "\t" + name + "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
			
			cmAvail -= cmPerLine;
			if (cmAvail <= cmPerLine) {
				addSubTotalLine(cursor, tp, balance, tcCode, esr);
				addESRCodeLine(balance, tcCode, esr);
				if (needDeadLetterAvoidance(mSave)) {
					return false;
				}
				
				XMLPrinterUtil.insertPage(ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S2,
					++page, adressat, rn, xmlRn, ezData.paymentMode, text);
				cursor = text.getPlugin().insertText("[Rechnungszeilen]", "\n", SWT.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
				cmAvail = cmMiddlePage;
				monitor.worked(2);
			}
		}
		
		addBalanceLines(cursor, tp, balance, ezData.paid);
		addESRCodeLine(balance, tcCode, esr);
		
		if (needDeadLetterAvoidance(mSave)) {
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
	
	private void addReminderFields(ReminderType reminder, String nr){
		String reminderDate = "";
		String reminderNr = "";
		
		if (reminder != null) {
			String reminderLevel = reminder.getReminderLevel();
			reminderNr = nr + "_m" + reminderLevel;
			
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			XMLGregorianCalendar date = reminder.getRequestDate();
			GregorianCalendar cal = date.toGregorianCalendar();
			reminderDate = df.format(cal.getTime());
		}
		text.replace("\\[F44.MDatum\\]", reminderDate);
		text.replace("\\[F44.MNr\\]", reminderNr);
	}
	
	private void initPrinterSettings(){
		printer = CoreHub.localCfg.get("Drucker/A4/Name", null); //$NON-NLS-1$
		tarmedTray = CoreHub.localCfg.get("Drucker/A4/Schacht", null); //$NON-NLS-1$
		if (StringTool.isNothing(tarmedTray)) {
			tarmedTray = null;
		}
	}
	
	private Kontakt loadAddressee(String paymentMode){
		
		Kontakt addressee;
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			// TP
			addressee = fall.getCostBearer();
		} else if (paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			// TG
			Kontakt invoiceReceiver = fall.getGarant();
			if (invoiceReceiver.equals(pat)) {
				Kontakt legalGuardian = pat.getLegalGuardian();
				if (legalGuardian != null) {
					addressee = legalGuardian;
				} else {
					addressee = pat;
				}
			} else {
				addressee = invoiceReceiver;
			}
		} else {
			addressee = fall.getGarant();
		}
		addressee.getPostAnschrift(true); // damit sicher eine existiert
		return addressee;
	}
	
	private void addSubTotalLine(Object cursor, ITextPlugin tp, BalanceType balance, String tcCode,
		ESR esr){
		StringBuilder footer = new StringBuilder();
		footer.append("\t\t\t\t\t\tZwischentotal\t").append(df.format(sideTotal)); //$NON-NLS-1$
		tp.setFont("Helvetica", SWT.BOLD, 7); //$NON-NLS-1$
		cursor = tp.insertText(cursor, footer.toString(), SWT.LEFT);
		// needed to make sure ESRCodeLine gets inserted correctly
		cursor = text.getPlugin().insertTextAt(0, 0, 0, 0, "", SWT.LEFT); //$NON-NLS-1$
		sideTotal = 0.0;
	}
	
	private void addFallSpecificLines(){
		BodyType body = request.getPayload().getBody();
		if (body != null) {
			String gesetzDatum = "Falldatum";
			String gesetzNummer = "Fall-Nr.";
			String gesetzZSRNIF = "ZSR-Nr.(P)";
			if (body.getUvg() != null) {
				gesetzDatum = "Unfalldatum";
				gesetzNummer = "Unfall-Nr.";
			}
			if (body.getIvg() != null) {
				gesetzDatum = "Verfügungsdatum";
				gesetzNummer = "Verfügungs-Nr.";
				gesetzZSRNIF = "NIF-Nr.(P)";
			}
			String vekaNumber =
				StringUtils.defaultIfBlank((String) fall.getExtInfoStoredObjectByKey("VEKANr"), "");
			
			text.replace("\\[F44.Datum\\]", gesetzDatum);
			text.replace("\\[F44.Nummer\\]", gesetzNummer);
			
			text.replace("\\[F44.FDatum\\]", getFDatum(body, fall));
			text.replace("\\[F44.FNummer\\]", getFNummer(body, fall));
			
			text.replace("\\[F44.ZSRNIF\\]", gesetzZSRNIF);
			text.replace("\\[F44.VEKANr\\]", vekaNumber);
		}
	}
	
	private String getFDatum(BodyType body, Fall fall){
		if (body.getUvg() != null) {
			String ret = fall.getInfoString("Unfalldatum");
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
		}
		if (body.getIvg() != null) {
			String ret = fall.getInfoString("Verfügungsdatum");
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
		}
		return fall.getBeginnDatum();
	}
	
	private String getFNummer(BodyType body, Fall fall){
		if (body.getUvg() != null) {
			String ret = fall.getInfoString("Unfall-Nr.");
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
			ret = fall.getInfoString("Unfallnummer");
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
		}
		if (body.getIvg() != null) {
			String ret = fall.getInfoString("Verfügungs-Nr.");
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
			ret = fall.getInfoString("Verfügungsnummer");
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
		}
		return fall.getFallNummer();
	}
	
	private void addRemarks(final String remark){
		if (remark != null && !remark.isEmpty()) {
			text.getPlugin().findOrReplace(Messages.RnPrintView_remark, new ReplaceCallback() {
				@Override
				public String replace(final String in){
					return Messages.RnPrintView_remarksp + remark;
				}
			});
		}
	}
	
	private void addESRCodeLine(BalanceType balance, String tcCode, ESR esr){
		String offenRp = new Money(balance.getAmountDue()).getCentsAsString();
		if (tcCode != null) {
			esr.printESRCodeLine(text.getPlugin(), offenRp, tcCode);
		}
	}
	
	private boolean needDeadLetterAvoidance(Mandant mSave){
		if (text.getPlugin().print(printer, tarmedTray, false) == false) {
			// avoid dead letters
			XMLPrinterUtil.deleteBrief(actBrief);
			Hub.setMandant(mSave);
			return true;
		}
		return false;
	}
	
	private boolean initBasicInvoiceValues(Rechnung rn, Document xmlRn){
		fall = rn.getFall();
		rnMandant = rn.getMandant();
		if (fall == null || rnMandant == null) {
			logger.error("Fall and/or Mandant of invoice is null");
			return false;
		}
		
		pat = fall.getPatient();
		Hub.setMandant(rnMandant);
		rnSteller = rnMandant.getRechnungssteller();
		if (pat == null || rnSteller == null) {
			logger.error("Patient and/or Rechnungssteller is null");
			return false;
		}
		
		request = TarmedJaxbUtil.unmarshalInvoiceRequest440(xmlRn);
		if (request == null) {
			logger.error("Could not unmarshall xml document for invoice");
			return false;
		}
		return true;
	}
	
	private String[] initEanArray(List<Object> serviceRecords){
		HashSet<String> eanUniqueSet = new HashSet<String>();
		
		for (Object record : serviceRecords) {
			String responsibleEAN = null;
			String providerEAN = null;
			
			if (record instanceof RecordServiceType) {
				RecordServiceType recService = (RecordServiceType) record;
				responsibleEAN = recService.getResponsibleId();
				providerEAN = recService.getProviderId();
			} else if (record instanceof RecordTarmedType) {
				RecordTarmedType recTarmed = (RecordTarmedType) record;
				responsibleEAN = recTarmed.getResponsibleId();
				providerEAN = recTarmed.getProviderId();
			}
			
			if (responsibleEAN != null && !responsibleEAN.isEmpty()) {
				eanUniqueSet.add(responsibleEAN);
			}
			
			if (providerEAN != null && !providerEAN.isEmpty()) {
				eanUniqueSet.add(providerEAN);
			}
		}
		
		return XMLPrinterUtil.getEANArray(eanUniqueSet);
	}
	
	private String getRecordServiceString(RecordServiceType rec, StringBuilder sb,
		HashMap<String, String> eanMap){
		if (rec.getDateBegin() == null) {
			return null;
		}
		
		tTime.set(rec.getDateBegin().toGregorianCalendar());
		sb.append(tTime.toString(TimeTool.DATE_GER)).append("\t"); //$NON-NLS-1$
		sb.append(getTarifType(rec)).append("\t");//$NON-NLS-1$
		String code = rec.getCode();
		sb.append(code).append("\t"); //$NON-NLS-1$
		sb.append(rec.getQuantity()).append("\t"); //$NON-NLS-1$
		
		sb.append(df.format(rec.getUnit())).append("\t"); //$NON-NLS-1$
		sb.append(df.format(1.00)).append("\t");
		sb.append(df.format(rec.getVatRate())).append("\t"); //$NON-NLS-1$
		double amount = rec.getAmount();
		sb.append(df.format(amount));
		sideTotal += amount;
		sb.append("\n"); //$NON-NLS-1$
		
		return sb.toString();
	}
	
	private void addBalanceLines(Object cursor, ITextPlugin tp, BalanceType balance, Money paid){
		cursor = text.getPlugin().insertTextAt(0, 255, 190, 45, " ", SWT.LEFT); //$NON-NLS-1$
		
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "\t\t\t\t\t\t\t\t\t"); //$NON-NLS-1$
		
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "Anzahlung:\t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, df.format(paid) + "\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, true, "Fälliger Betrag:\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, true,
			df.format(balance.getAmount()) + "\n"); //$NON-NLS-1$
		
		// second line
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "\t\t\t\t\t\t\t\t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "Währung:\t\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, "CHF\t"); //$NON-NLS-1$
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.RIGHT, false, "MWSt.-Nr.:\t"); //$NON-NLS-1$
		VatType vat = balance.getVat();
		String vatNumber = vat.getVatNumber();
		if (vatNumber == null || vatNumber.equals(" ")) {
			vatNumber = "\tkeine";
		} else {
			vatNumber = vatNumber + " MWST";
		}
		cursor = XMLPrinterUtil.print(cursor, tp, 7, SWT.LEFT, false, vatNumber + "\n"); //$NON-NLS-1$
	}
	
	private void addDiagnoses(TreatmentType treatment){
		if (treatment == null) {
			logger.debug("no treatments defined");
			return;
		}
		
		List<DiagnosisType> diagnoses = treatment.getDiagnosis();
		if (diagnoses == null || diagnoses.isEmpty()) {
			logger.warn("No diagnoses found to print at the tarmed invoice request");
			return;
		}
		
		List<String> occuredCodes = new ArrayList<String>();
		String type = "";
		String freetext = "";
		StringBuilder dCodesBuilder = new StringBuilder();
		for (DiagnosisType diagnose : diagnoses) {
			String dType = diagnose.getType();
			if (dType.equals(FREETEXT)) {
				freetext = diagnose.getValue();
				continue;
			}
			
			if (type.isEmpty()) {
				type = dType;
				dCodesBuilder.append(diagnose.getCode());
				occuredCodes.add(diagnose.getCode());
			} else if (type.equals(dType)) {
				// add each code only once
				if (!occuredCodes.contains(diagnose.getCode())) {
					dCodesBuilder.append("; "); //$NON-NLS-1$
					dCodesBuilder.append(diagnose.getCode());
					occuredCodes.add(diagnose.getCode());
				}
			}
		}
		
		if (type.equals(BY_CONTRACT)) {
			type = "TI-Code"; //$NON-NLS-1$
		}
		
		text.replace("\\[F51\\]", type); //$NON-NLS-1$
		text.replace("\\[F52\\]", dCodesBuilder.toString()); //$NON-NLS-1$
		text.replace("\\[F53\\]", freetext); //$NON-NLS-1$
	}
	
	private String getTarifType(RecordServiceType rec){
		if (rec instanceof RecordOtherType) {
			RecordOtherType other = (RecordOtherType) rec;
			return other.getTariffType();
		} else if (rec instanceof RecordDrugType) {
			RecordDrugType drug = (RecordDrugType) rec;
			return drug.getTariffType();
		} else if (rec instanceof RecordDRGType) {
			RecordDRGType drg = (RecordDRGType) rec;
			return drg.getTariffType();
		} else if (rec instanceof RecordMigelType) {
			RecordMigelType migel = (RecordMigelType) rec;
			return migel.getTariffType();
		} else if (rec instanceof RecordLabType) {
			RecordLabType lab = (RecordLabType) rec;
			return lab.getTariffType();
		} else if (rec instanceof RecordParamedType) {
			RecordParamedType param = (RecordParamedType) rec;
			return param.getTariffType();
		}
		return "";
	}
	
	private double getVatAmount(int code, VatType vat){
		VatRateType vatRate = getVatRateElement(code, vat);
		if (vatRate != null) {
			return vatRate.getAmount();
		}
		return 0.0D;
	}
	
	private double getVatRate(int code, VatType vat){
		VatRateType vatRate = getVatRateElement(code, vat);
		if (vatRate != null) {
			return vatRate.getVatRate();
		}
		return 0.0D;
	}
	
	private double getVatVat(int code, VatType vat){
		VatRateType vatRate = getVatRateElement(code, vat);
		if (vatRate != null) {
			return vatRate.getVat();
		}
		return 0.0D;
	}
	
	private VatRateType getVatRateElement(int code, VatType vat){
		List<VatRateType> vatRates = vat.getVatRate();
		for (VatRateType vatRate : vatRates) {
			double rate = vatRate.getVatRate();
			int vatCode = XMLPrinterUtil.guessVatCode(rate + "");
			if (vatCode == code) {
				return vatRate;
			}
		}
		return null;
	}
}
