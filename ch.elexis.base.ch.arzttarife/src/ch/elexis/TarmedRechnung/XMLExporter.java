/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

/*  BITTE KEINE ÄNDERUNGEN AN DIESEM FILE OHNE RÜCKSPRACHE MIT MIR weirich@elexis.ch */
/*  THIS FILE IS FROZEN. DO NOT MODIFY */

package ch.elexis.TarmedRechnung;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.xml.transform.Source;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.preferences.CorePreferenceInitializer;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.NamedBlob;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.TrustCenters;
import ch.elexis.data.Verrechnet;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

/**
 * Exportiert eine Elexis-Rechnung im XML 4.0 Format von xmldata.ch Bitte KEINE Änderungen an dieser
 * Klasse durchführen. Senden Sie Verbesserungsvorschläge oder Wünsche als Mail oder direkt als
 * Patch an weirich@elexis.ch.
 * 
 * zur Weiterverarbeitung verwendet werden. DoExport(..) liefert ein JDOM-Dokument, das die
 * gewünschte Rechnung enthält. Diese kann vom Aufrufer dann an einen Intermediär oder auf einen
 * Drucker ausgegeben werden. Der Output dieses Exporters ist TrustX zertifiziert. Änderungen
 * sollten in den seltensten Fällen nötig sein. Falls doch: Fehlermeldungen bitte an
 * weirich@elexis.ch
 * 
 * @author gerry
 * 
 */
public class XMLExporter implements IRnOutputter {
	
	private static Logger logger = LoggerFactory.getLogger(XMLExporter.class);

	// constants to access vat information from the extinfo of the Rechnungssteller
	public static final String VAT_ISMANDANTVAT = "at.medevit.medelexis.vat_ch/IsMandantVat";
	public static final String VAT_MANDANTVATNUMBER =
		"at.medevit.medelexis.vat_ch/MandantVatNumber";
	
	public static final String ATTR_REMARK = "remark"; //$NON-NLS-1$
	public static final String ELEMENT_TIERS_PAYANT = "tiers_payant"; //$NON-NLS-1$
	public static final String ELEMENT_TIERS_GARANT = "tiers_garant"; //$NON-NLS-1$
	public static final String ATTR_CODE = "code"; //$NON-NLS-1$
	public static final String BIRTHDEFECT = "birthdefect"; //$NON-NLS-1$
	public static final String DISEASE = "disease"; //$NON-NLS-1$
	public static final String FREETEXT = "freetext"; //$NON-NLS-1$
	public static final String ATTR_BIRTHDATE = "birthdate"; //$NON-NLS-1$
	public static final String ELEMENT_VAT = "vat"; //$NON-NLS-1$
	public static final String ELEMENT_VAT_NUMBER = "vat_number"; //$NON-NLS-1$
	public static final String ATTR_VAT_RATE = "vat_rate"; //$NON-NLS-1$
	public static final String ATTR_TARIFF_TYPE = "tariff_type"; //$NON-NLS-1$
	public static final String ELEMENT_REMARK = ATTR_REMARK; //$NON-NLS-1$
	private static final String ELEMENT_PAYLOAD = "payload"; //$NON-NLS-1$
	private static final String ATTR_PAYLOAD_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_PAYLOAD_COPY = "copy"; //$NON-NLS-1$
	private static final String ATTR_PAYLOAD_STORNO = "storno"; //$NON-NLS-1$
	public static final String ATTR_EAN_PARTY = "ean_party"; //$NON-NLS-1$
	private static final String ATTR_MODUS = "modus"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	private static final String ELEMENT_REQUEST = "request"; //$NON-NLS-1$
	private static final String ATTR_REQUEST_TIMESTAMP = "request_timestamp"; //$NON-NLS-1$
	public static final String ATTR_REQUEST_DATE = "request_date"; //$NON-NLS-1$
	public static final String ATTR_REQUEST_ID = "request_id"; //$NON-NLS-1$
	public static final String TIERS_GARANT = "TG"; //$NON-NLS-1$
	public static final String TIERS_PAYANT = "TP"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_PHYSIO = "amount_physio"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_MIGEL = "amount_migel"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_LAB = "amount_lab"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_DRUG = "amount_drug"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_UNCLASSIFIED = "amount_unclassified"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_CANTONAL = "amount_cantonal"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_TARMED_TT = "amount_tarmed.tt"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_TARMED_MT = "amount_tarmed.mt"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_TARMED = "amount_tarmed"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT = "amount"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_TT = "amount_tt"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_MT = "amount_mt"; //$NON-NLS-1$
	public static final String ATTR_QUANTITY = "quantity"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_DUE = "amount_due"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_PREPAID = "amount_prepaid"; //$NON-NLS-1$
	public static final String ELEMENT_BALANCE = "balance"; //$NON-NLS-1$
	public static final String ELEMENT_INVOICE = "invoice"; //$NON-NLS-1$
	public static final String ELEMENT_BODY = "body"; //$NON-NLS-1$
	public static final String ATTR_BODY_ROLE = "role"; //$NON-NLS-1$
	public static final String ATTR_BODY_PLACE = "place"; //$NON-NLS-1$
	public static final String ELEMENT_ANNULMENT = "annulment"; //$NON-NLS-1$
	public static final String FIELDNAME_TIMESTAMPXML = "TimeStampXML"; //$NON-NLS-1$
	public static final String ELEMENT_REMINDER = "reminder";
	public static final String ATTR_REMINDER_LEVEL = "reminder_level";
	
	public static final Namespace ns = Namespace
		.getNamespace("http://www.forum-datenaustausch.ch/invoice"); //$NON-NLS-1$
	public static final Namespace nsinvoice = Namespace.getNamespace("invoice", //$NON-NLS-1$
		"http://www.forum-datenaustausch.ch/invoice"); //$NON-NLS-1$
	public static final Namespace nsxsi = Namespace.getNamespace(
		"xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final Namespace nsxenc = Namespace.getNamespace(
		"nsxenc", "http://www.w3.org/2001/04/xmlenc#"); //$NON-NLS-1$ //$NON-NLS-2$	
	public static final Namespace nsds = Namespace.getNamespace(
		"ds", "http://www.w3.org/2000/09/xmldsig#"); //$NON-NLS-1$ //$NON-NLS-2$

	Fall actFall;
	Patient actPatient;
	Mandant actMandant;
	String tiers;
	
	Rechnung rn;
	
	private XMLExporterBalance xmlBalance;
	private XMLExporterTreatment xmlTreatment;

	private ESR besr;
	static TarmedACL ta;
	private String outputDir;
	
	private XMLExporterEsr9 esr9;
	public static final String PREFIX = "TarmedRn:"; //$NON-NLS-1$
	
	/**
	 * Reset exporter
	 */
	public void clear(){
		actFall = null;
		actPatient = null;
		actMandant = null;
		rn = null;
	}
	
	public XMLExporter(){
		ta = TarmedACL.getInstance();
		clear();
	}
	
	/**
	 * Output a Collection of bills. This essentially lets the user modify the output settings (if
	 * any) and then calls doExport() für each bill in rnn
	 * 
	 * @param type
	 *            desired mode (original, copy, storno)
	 * @param rnn
	 *            a Collection of Rechnung - Objects to output
	 */
	@Override
	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rnn,
		Properties props){
		Result<Rechnung> ret = new Result<Rechnung>();
		if (outputDir == null) {
			SWTHelper.SimpleDialog dlg =
				new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
					@Override
					public Control getControl(Composite parent){
						return createSettingsControl(parent);
					}
					
					@Override
					public void beforeClosing(){
						// Nothing
					}
				});
			if (dlg.open() != Dialog.OK) {
				return ret;
			}
		}
		for (Rechnung rn : rnn) {
			if (doExport(rn, outputDir + File.separator + rn.getNr() + ".xml", type, false) == null) { //$NON-NLS-1$
				ret.add(Result.SEVERITY.ERROR, 1, Messages.XMLExporter_ErrorInBill + rn.getNr(),
					rn, true);
			}
		}
		return ret;
	}
	
	/**
	 * Wa want to be informed on cancellings of any bills
	 * 
	 * @param rn
	 *            we don't mind, we always return true
	 */
	@Override
	public boolean canStorno(final Rechnung rn){
		return true;
	}
	
	private void negate(Element el, String attr){
		String v = el.getAttributeValue(attr);
		if (!StringTool.isNothing(v)) {
			if (!v.equals(StringConstants.DOUBLE_ZERO)) {
				if (v.startsWith(StringConstants.DASH)) {
					v = v.substring(1);
				} else {
					v = StringConstants.DASH + v;
				}
				el.setAttribute(attr, v);
			}
		}
	}
	
	/**
	 * Export a bill as XML. We do, in fact first check whether this bill was exported already. And
	 * if so we do not create it again but load the old one. There is deliberately no possibility to
	 * avoid this behaviour. (One can only delete or storno a bill and recreate it (even then the
	 * stored xml remains stored. Additionally, the caller can chose to store the bill as XML in the
	 * file system. This is done if the parameter dest ist given. On success the caller will receive
	 * a JDOM Document containing the bill.
	 * 
	 * @param rechnung
	 *            the bill to export
	 * @param dest
	 *            a full filepath to save the final document (or null to not save it)
	 * @param type
	 *            Type of output (original, copy, storno)
	 * @param doVerify
	 *            true if the bill should be sent trough a verifyer after creation.
	 * @return the jdom XML-Document that contains the bill. Might be null on failure.
	 */
	public Document doExport(final Rechnung rechnung, final String dest,
		final IRnOutputter.TYPE type, final boolean doVerify){
		clear();
		// create a object for managing vat rates and values on invoice level
		VatRateSum vatSummer = new VatRateSum();
		rn = rechnung;
		
		if (xmlBillExists(rechnung)) {
			logger.info("Updating existing bill for " + rechnung.getNr());
			Document updated = updateExistingXmlBill(rechnung, dest, type, doVerify);
			if (updated != null) {
				return updated;
			}
		}

		if (type.equals(TYPE.STORNO)) {
			SWTHelper.showError(Messages.XMLExporter_StornoImpossibleCaption,
				Messages.XMLExporter_StornoImpossibleText);
			return null;
		}
		
		actFall = rn.getFall();
		actPatient = actFall.getPatient();
		actMandant = rn.getMandant();
		Kontakt kostentraeger = actFall.getRequiredContact(TarmedRequirements.INSURANCE);
		
		if (kostentraeger == null) {
			kostentraeger = actPatient;
		}

		logger.info("Creating new bill for " + rechnung.getNr());
		Document xmlRn;
		Element root = new Element(ELEMENT_REQUEST, nsinvoice);
		root.addNamespaceDeclaration(nsxsi);
		root.addNamespaceDeclaration(nsxenc);
		root.addNamespaceDeclaration(nsxsi);
		root.addNamespaceDeclaration(nsinvoice);
		root.setAttribute("schemaLocation", //$NON-NLS-1$
			"http://www.forum-datenaustausch.ch/invoice generalInvoiceRequest_440.xsd", nsxsi); //$NON-NLS-1$
		
		root.setAttribute(ATTR_MODUS, getRole(actFall));
		root.setAttribute(ATTR_LANGUAGE, Locale.getDefault().getLanguage());
		xmlRn = new Document(root);
		
		// services are needed for the balance
		XMLExporterServices services = null;
		List<Konsultation> lb = rn.getKonsultationen();
		for (Konsultation b : lb) {
			List<Verrechnet> lv = b.getLeistungen();
			if (lv.size() == 0) {
				continue;
			}
			
			services = XMLExporterServices.buildServices(rn, vatSummer);
		}
		
		//balance is needed by other parts so initialize first
		initBalanceData(rechnung, services, vatSummer);

		//processing
		XMLExporterProcessing processing = XMLExporterProcessing.buildProcessing(rechnung, this);
		root.addContent(processing.getElement());

		//payload
		Element payload = new Element(ELEMENT_PAYLOAD, nsinvoice);
		payload.setAttribute(ATTR_PAYLOAD_TYPE, "invoice"); //$NON-NLS-1$
		payload.setAttribute(ATTR_PAYLOAD_COPY, type.equals(IRnOutputter.TYPE.COPY) ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		payload
			.setAttribute(ATTR_PAYLOAD_STORNO, type.equals(IRnOutputter.TYPE.STORNO) ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$

		//invoice
		String ts = null;
		if (type.equals(IRnOutputter.TYPE.COPY)) {
			ts = rn.getExtInfo(FIELDNAME_TIMESTAMPXML);
			if (StringTool.isNothing(ts)) {
				ts = Long.toString(new Date().getTime() / 1000);
				rn.setExtInfo(FIELDNAME_TIMESTAMPXML, ts);
			}
		} else {
			ts = Long.toString(new Date().getTime() / 1000);
			rn.setExtInfo(FIELDNAME_TIMESTAMPXML, ts);
		}
		
		Element invoice = new Element(ELEMENT_INVOICE, nsinvoice);
		invoice.setAttribute(ATTR_REQUEST_TIMESTAMP, ts);
		invoice.setAttribute(ATTR_REQUEST_ID, rn.getRnId());
		invoice.setAttribute(ATTR_REQUEST_DATE,
			new TimeTool(rn.getDatumRn()).toString(TimeTool.DATE_MYSQL) + "T00:00:00"); // 10154 //$NON-NLS-1$
		payload.addContent(invoice);
		
		//body
		Element body = new Element(ELEMENT_BODY, nsinvoice);
		body.setAttribute(ATTR_BODY_ROLE, "physician");
		body.setAttribute(ATTR_BODY_PLACE, "practice");
		
		//prolog
		XMLExporterProlog prolog = XMLExporterProlog.buildProlog(rechnung, this);
		body.addContent(prolog.getElement());

		//remark
		String bem = rn.getBemerkung();
		if (!StringTool.isNothing(bem)) {
			Element remark = new Element(ELEMENT_REMARK, nsinvoice);
			remark.setText(rn.getBemerkung());
			body.addContent(remark);
		}
		
		// add the balance
		body.addContent(xmlBalance.getElement());
		
		//esr9
		esr9 = XMLExporterEsr9.buildEsr9(rechnung, xmlBalance, this);
		body.addContent(esr9.getElement());

		//tiers garant or payant
		XMLExporterTiers xmlTiers = XMLExporterTiers.buildTiers(rechnung, this);
		tiers = xmlTiers.getTiers();
		body.addContent(xmlTiers.getElement());

		//insurance
		XMLExporterInsurance xmlInsurance = XMLExporterInsurance.buildInsurance(rechnung, this);
		body.addContent(xmlInsurance.getElement());

		xmlTreatment = XMLExporterTreatment.buildTreatment(rechnung, this);
		body.addContent(xmlTreatment.getElement());
		
		if(services!=null) {
			body.addContent(services.getElement());
		} else {
			logger.warn("services is null!");
		}


		payload.addContent(body);
		
		root.addContent(payload);
		
		if (rn.setBetrag(xmlBalance.getTotal().roundTo5()) == false) {
			rn.reject(RnStatus.REJECTCODE.SUM_MISMATCH, Messages.XMLExporter_SumMismatch);
			
		} else if (doVerify) {
			new Validator().checkBill(this, new Result<Rechnung>());
		}
		
		checkXML(xmlRn, dest, rn, doVerify);
		
		if (rn.getStatus() != RnStatus.FEHLERHAFT) {
			try {
				StringWriter stringWriter = new StringWriter();
				XMLOutputter xout = new XMLOutputter(Format.getCompactFormat());
				xout.output(xmlRn, stringWriter);
				NamedBlob blob = NamedBlob.load(PREFIX + rn.getNr());
				blob.putString(stringWriter.toString());
				if (dest != null) {
					writeFile(xmlRn, dest);
					
				}
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.alert(Messages.XMLExporter_ErrorCaption,
					MessageFormat.format(Messages.XMLExporter_CouldNotWriteFile, dest));
				return null;
			}
		}
		return xmlRn;
	}
	
	private Document updateExistingXmlBill(Rechnung rechnung, String dest, TYPE type,
		boolean doVerify){
		// If the bill exists already in the database, it has been output
		// earlier, so we don't
		// recreate it. We must, however, reflect changes that happened
		// since it was output:
		// Payments, state changes, obligations
		NamedBlob blob = NamedBlob.load(PREFIX + rechnung.getNr());
		SAXBuilder builder = new SAXBuilder();

		try {
			Document ret = builder.build(new StringReader(blob.getString()));
			Element root = ret.getRootElement();
			if (getXmlVersion(root).equals("4.0")) {
				updateExisting4Xml(root, type, rechnung);
			} else if (getXmlVersion(root).equals("4.4")) {
				updateExisting44Xml(root, type, rechnung);
				
				int status = rechnung.getStatus();
				if (status == RnStatus.MAHNUNG_1) {
					dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m1.xml");
					addReminderEntry(root, rechnung, "1");
				} else if (status == RnStatus.MAHNUNG_2) {
					dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m2.xml");
					addReminderEntry(root, rechnung, "2");
				} else if (status == RnStatus.MAHNUNG_3) {
					dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m3.xml");
					addReminderEntry(root, rechnung, "3");
				}
			} else {
				logger.warn("Bill in unknown XML version " + getXmlVersion(root)
					+ ", recreating bill.");
				return null;
			}
			checkXML(ret, dest, rn, doVerify);
			
			if (dest != null) {
				if (type.equals(TYPE.STORNO)) {
					writeFile(ret, dest.toLowerCase().replaceFirst("\\.xml$", "_storno.xml")); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					writeFile(ret, dest);
				}
			}
			StringWriter stringWriter = new StringWriter();
			XMLOutputter xout = new XMLOutputter(Format.getCompactFormat());
			xout.output(ret, stringWriter);
			blob.putString(stringWriter.toString());
			return ret;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.XMLExporter_ReadErrorCaption,
				Messages.XMLExporter_ReadErrorText);
			// What should we do -> We create it from scratch
			return null;
		}
	}
	
	private void addReminderEntry(Element root, Rechnung rechnung, String reminderLevel){
		boolean firstReminder = false;
		Element payload = root.getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		payload.setAttribute(ATTR_PAYLOAD_TYPE, "reminder"); //$NON-NLS-1$
		
		TimeTool tt = new TimeTool(new Date());
		String timestamp = Long.toString(tt.getTimeInMillis() / 1000);
		String dateString = tt.toString(TimeTool.DATE_MYSQL) + "T00:00:00";
		
		Element reminder = payload.getChild(ELEMENT_REMINDER, nsinvoice);
		if (reminder == null) {
			reminder = new Element(ELEMENT_REMINDER, nsinvoice);
			firstReminder = true;
		}
		reminder.setAttribute(ATTR_REQUEST_TIMESTAMP, timestamp); //$NON-NLS-1$
		reminder.setAttribute(ATTR_REQUEST_DATE, dateString); //$NON-NLS-1$
		reminder.setAttribute(ATTR_REQUEST_ID, rechnung.getRnId()); //$NON-NLS-1$
		reminder.setAttribute(ATTR_REMINDER_LEVEL, reminderLevel); //$NON-NLS-1$
		
		if (firstReminder) {
			payload.addContent(1, reminder);
		}
	}
	
	private void updateExisting44Xml(Element root, TYPE type, Rechnung rechnung){
		Money mPaid = rn.getAnzahlung();
		
		Element payload = root.getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		XMLExporterBalance xmlBalance = new XMLExporterBalance(balance);
		if (!mPaid.equals(xmlBalance.getPrepaid())) {
			xmlBalance.setPrepaid(mPaid);
			Money mDue = xmlBalance.getAmountObligations();
			mDue.subtractMoney(mPaid);
			mDue.roundTo5();
			xmlBalance.setDue(mDue);
		}
		if (type.equals(IRnOutputter.TYPE.COPY)) {
			payload.setAttribute("copy", Boolean.toString(true));//$NON-NLS-1$
		} else if (type.equals(TYPE.STORNO)) {
			payload.setAttribute("storno", Boolean.toString(true));//$NON-NLS-1$
			Element services = body.getChild("services", XMLExporter.nsinvoice);//$NON-NLS-1$
			XMLExporterServices xmlServices = new XMLExporterServices(services);
			xmlServices.negateAll();
			
			xmlBalance.negateAmount();
			xmlBalance.negateAmountObligations();
			xmlBalance.setDue(new Money());
			xmlBalance.setPrepaid(new Money());
		}
	}
	
	private void updateExisting4Xml(Element root, TYPE type, Rechnung rechnung){
		Namespace namespace = Namespace.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
		Money mPaid = rn.getAnzahlung();

		Element invoice = root.getChild("invoice", namespace);//$NON-NLS-1$
		Element balance = invoice.getChild("balance", namespace);//$NON-NLS-1$
		Money anzInBill = XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount_prepaid"));//$NON-NLS-1$
		if (!mPaid.equals(anzInBill)) {
			balance.setAttribute("amount_prepaid", XMLTool.moneyToXmlDouble(mPaid));//$NON-NLS-1$
			Money mDue = XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount_obligations"));//$NON-NLS-1$
			mDue.subtractMoney(mPaid);
			mDue.roundTo5();
			balance.setAttribute("amount_due", XMLTool.moneyToXmlDouble(mDue));//$NON-NLS-1$
		}
		if (type.equals(IRnOutputter.TYPE.COPY)) {
			invoice.setAttribute("resend", Boolean.toString(true));//$NON-NLS-1$
		} else if (type.equals(TYPE.STORNO)) {
			Element detail = invoice.getChild("detail", namespace);//$NON-NLS-1$
			Element services = detail.getChild("services", namespace);//$NON-NLS-1$
			@SuppressWarnings("unchecked")
			List<Element> sr = services.getChildren();
			for (Element el : sr) {
				try {
					negate(el, "quantity");//$NON-NLS-1$
					negate(el, "amount.mt");//$NON-NLS-1$
					negate(el, "amount.tt");//$NON-NLS-1$
					negate(el, "amount");//$NON-NLS-1$
				} catch (Exception ex) {
					ExHandler.handle(ex);
				}
			}
			negate(balance, "amount");//$NON-NLS-1$
			negate(balance, "amount_tarmed");//$NON-NLS-1$
			negate(balance, "amount_tarmed.mt");//$NON-NLS-1$
			negate(balance, "amount_tarmed.tt");//$NON-NLS-1$
			negate(balance, "amount_cantonal");//$NON-NLS-1$
			negate(balance, "amount_unclassified");//$NON-NLS-1$
			negate(balance, "amount_drug");//$NON-NLS-1$
			negate(balance, "amount_lab");//$NON-NLS-1$
			negate(balance, "amount_migel");//$NON-NLS-1$
			negate(balance, "amount_physio");//$NON-NLS-1$
			negate(balance, "amount_obligations");//$NON-NLS-1$
			balance.setAttribute("amount_due", StringConstants.DOUBLE_ZERO);//$NON-NLS-1$
			balance.setAttribute("amount_prepaid", StringConstants.DOUBLE_ZERO);//$NON-NLS-1$
			
			// change the purpose if a payant element is present
			Element payant = invoice.getChild("tiers_payant");//$NON-NLS-1$
			if (payant != null) {
				payant.setAttribute("purpose", ELEMENT_ANNULMENT); //$NON-NLS-1$
			}
		}
	}
	
	public static String getXmlVersion(Element root){
		String location =
			root.getAttributeValue(
				"schemaLocation", Namespace.getNamespace("http://www.w3.org/2001/XMLSchema-instance"));//$NON-NLS-1$ //$NON-NLS-2$
		if (location != null && !location.isEmpty()) {
			if (location.contains("InvoiceRequest_400")) {//$NON-NLS-1$
				return "4.0";//$NON-NLS-1$
			} else if (location.contains("InvoiceRequest_440")) {//$NON-NLS-1$
				return "4.4";//$NON-NLS-1$
			}
		}
		return location;//$NON-NLS-1$
	}

	private boolean xmlBillExists(Rechnung rechnung){
		return NamedBlob.exists(PREFIX + rechnung.getNr());
	}

	protected Element buildGuarantor(Kontakt garant, Kontakt patient){
		// Patient wird im override des MediPort Plugins verwendet
		// Hinweis:
		// XML Standard:
		// http://www.forum-datenaustausch.ch/mdinvoicerequest_xml4.00_v1.2_d.pdf
		// Dort steht beim Feld 11310: Gesetzlicher Vertreter des Patienten.
		Element guarantor = new Element("guarantor", XMLExporter.nsinvoice); //$NON-NLS-1$
		guarantor.addContent(XMLExporterUtil.buildAdressElement(garant));
		return guarantor;
	}

	@Override
	public String getDescription(){
		return Messages.XMLExporter_TarmedForTrustCenter;
	}
	
	/**
	 * Validate XML of the created bill against the appropriate schema. Subclasses can override to
	 * provide specific handling of errors. The default implementation will mark the bill as
	 * erroneous if STRICT_BILLING is active and XML Schema errors are present.
	 * 
	 * @param xmlDoc
	 *            the bill
	 * @param dest
	 *            the destination path if the user chose output to file. Might be null
	 * @param rn
	 *            the bill to output
	 * @param doVerify
	 *            false if the user doesn't want strict validity check (subclasses may ignore)
	 */
	protected void checkXML(final Document xmlDoc, String dest, final Rechnung rn,
		final boolean doVerify){
		if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)) {
			Source source = new JDOMSource(xmlDoc);
			String path =
				PlatformHelper.getBasePath("ch.elexis.base.ch.arzttarife") + File.separator + "rsc"; //$NON-NLS-1$ //$NON-NLS-2$
			List<String> errs = null;
			// validate depending on tarmed version
			if (getXmlVersion(xmlDoc.getRootElement()).equals("4.0")) {
				logger.info("Validating XML against MDInvoiceRequest_400.xsd");
				errs =
					XMLTool.validateSchema(
						path + File.separator + "MDInvoiceRequest_400.xsd", source); //$NON-NLS-1$
			} else if (getXmlVersion(xmlDoc.getRootElement()).equals("4.4")) {
				logger.info("Validating XML against generalInvoiceRequest_440.xsd");
				errs =
					XMLTool.validateSchema(
						path + File.separator + "generalInvoiceRequest_440.xsd", source); //$NON-NLS-1$
			} else {
				errs =
					Collections.singletonList("Bill in unknown XML version "
						+ getXmlVersion(xmlDoc.getRootElement()));
			}
			
			if (!errs.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String err : errs) {
					sb.append(err).append(StringConstants.LF);
				}
				logger.error(sb.toString());
				rn.reject(RnStatus.REJECTCODE.VALIDATION_ERROR, sb.toString());
				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
				StringWriter sw = new StringWriter();
				try {
					xout.output(xmlDoc, sw);
				} catch (IOException e) {
					logger.error("Failed getting document as String.", e);
					return;
				}
				logger.debug(sw.toString());
			}
		}
		
	}
	
	@Override
	public Control createSettingsControl(final Object parent){
		final Composite parentInc = (Composite) parent;
		Composite ret = new Composite(parentInc, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		Label l = new Label(ret, SWT.NONE);
		l.setText(Messages.XMLExporter_PleaseEnterOutputDirectoryForBills);
		l.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		final Text text = new Text(ret, SWT.READ_ONLY | SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Button b = new Button(ret, SWT.PUSH);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				outputDir = new DirectoryDialog(parentInc.getShell(), SWT.OPEN).open();
				CoreHub.localCfg.set(PreferenceConstants.RNN_EXPORTDIR, outputDir);
				text.setText(outputDir);
			}
		});
		b.setText(Messages.XMLExporter_Change);
		outputDir =
			CoreHub.localCfg.get(PreferenceConstants.RNN_EXPORTDIR,
				CorePreferenceInitializer.getDefaultDBPath());
		text.setText(outputDir);
		return ret;
	}
	
	protected void writeFile(final Document doc, final String dest) throws IOException{
		FileOutputStream fout = new FileOutputStream(dest);
		OutputStreamWriter cout = new OutputStreamWriter(fout, "UTF-8"); //$NON-NLS-1$
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(doc, cout);
		cout.close();
		fout.close();
		int status_vorher = rn.getStatus();
		if ((status_vorher == RnStatus.OFFEN) || (status_vorher == RnStatus.MAHNUNG_1)
			|| (status_vorher == RnStatus.MAHNUNG_2) || (status_vorher == RnStatus.MAHNUNG_3)) {
			rn.setStatus(status_vorher + 1);
		}
		rn.addTrace(Rechnung.OUTPUT, getDescription() + ": " //$NON-NLS-1$
			+ RnStatus.getStatusText(rn.getStatus()));
	}
	
	@Override
	public boolean canBill(final Fall fall){
		Kontakt garant = fall.getGarant();
		Kontakt kostentraeger = fall.getRequiredContact(TarmedRequirements.INSURANCE);
		if ((garant != null) && (kostentraeger != null)) {
			if (garant.isValid()) {
				if (kostentraeger.isValid()) {
					if (kostentraeger.istOrganisation()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void saveComposite(){
		// Nothing
	}
	
	protected String getIntermediateEAN(final Fall fall){
		// Try to find the intermediate EAN. If we have explicitely set
		// an intermediate EAN, we'll use this one. Otherweise, we'll
		// check whether the mandator has a TC contract. if so, we try to
		// find the TC's EAN.
		// If nothing appropriate is found, we'll try to use the receiver EAN
		// or at least the guarantor EAN.
		// If everything fails we use a pseudo EAN to make the Validators happy
		String iEAN = TarmedRequirements.getIntermediateEAN(actFall);
		if (iEAN.length() == 0) {
			if (TarmedRequirements.hasTCContract(actMandant)) {
				String trustCenter = TarmedRequirements.getTCName(actMandant);
				if (trustCenter.length() > 0) {
					iEAN = TrustCenters.getTCEAN(trustCenter);
				}
			}
		}
		return iEAN;
	}
	
	protected String getSenderEAN(Mandant actMandant){
		return TarmedRequirements.getEAN(actMandant);
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
				sumvat += (amount / (100.0)) * scale;
			}
			
			@Override
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
			sumvat += (amount / (100.0)) * scale;
		}
	}
	
	/**
	 * Initialize balance related data structures of the export.
	 * 
	 * @param rechnung
	 * @param services
	 * @param vatSummer
	 */
	private void initBalanceData(Rechnung rechnung, XMLExporterServices services,
		VatRateSum vatSummer){
		xmlBalance = XMLExporterBalance.buildBalance(rechnung, services, vatSummer, this);
		
		besr =
			new ESR(actMandant.getRechnungssteller().getInfoString(XMLExporter.ta.ESRNUMBER),
				actMandant.getRechnungssteller().getInfoString(XMLExporter.ta.ESRSUB),
				rechnung.getRnId(), ESR.ESR27);
	}

	public ESR getBesr(){
		return besr;
	}

	public Money getDueMoney(){
		return xmlBalance.getDue();
	}
	
	public List<IDiagnose> getDiagnoses(){
		return xmlTreatment.getDiagnoses();
	}
	
	protected String getRole(final Fall fall){
		return "production"; //$NON-NLS-1$
	}
}
