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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.transform.Source;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum.VatRateElement;
import ch.elexis.artikel_ch.data.Medical;
import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.artikel_ch.data.MedikamentImporter;
import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.preferences.CorePreferenceInitializer;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LaborLeistung;
import ch.elexis.data.Mandant;
import ch.elexis.data.NamedBlob;
import ch.elexis.data.Organisation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.PhysioLeistung;
import ch.elexis.data.RFE;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.RnStatus;
import ch.elexis.data.RnStatus.REJECTCODE;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.TrustCenters;
import ch.elexis.data.Verrechnet;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Log;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;
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
	// constants to access vat information from the extinfo of the Rechnungssteller
	public static final String VAT_ISMANDANTVAT = "at.medevit.medelexis.vat_ch/IsMandantVat";
	public static final String VAT_MANDANTVATNUMBER =
		"at.medevit.medelexis.vat_ch/MandantVatNumber";
	
	public static final String ATTR_REMARK = "remark"; //$NON-NLS-1$
	public static final String ELEMENT_TIERS_PAYANT = "tiers_payant"; //$NON-NLS-1$
	public static final String ELEMENT_TIERS_GARANT = "tiers_garant"; //$NON-NLS-1$
	private static final String ELEMENT_EMAIL = "email"; //$NON-NLS-1$
	private static final String ELEMENT_ONLINE = "online"; //$NON-NLS-1$
	public static final String ATTR_CODE = "code"; //$NON-NLS-1$
	private static final String ICPC = "ICPC"; //$NON-NLS-1$
	private static final String BY_CONTRACT = "by_contract"; //$NON-NLS-1$
	private static final String BIRTHDEFECT = "birthdefect"; //$NON-NLS-1$
	public static final String DISEASE = "disease"; //$NON-NLS-1$
	private static final String FREETEXT = "freetext"; //$NON-NLS-1$
	private static final String ATTR_BIRTHDATE = "birthdate"; //$NON-NLS-1$
	private static final String ATTR_PARTICIPANT_NUMBER = "participant_number"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ELEMENT_VAT = "vat"; //$NON-NLS-1$
	private static final String ELEMENT_VAT_NUMBER = "vat_number"; //$NON-NLS-1$
	private static final String ATTR_UNIT_TARMED_TT = "unit_tarmed.tt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_TARMED_MT = "unit_tarmed.mt"; //$NON-NLS-1$
	private static final String ATTR_DATE_BEGIN = "date_begin"; //$NON-NLS-1$
	private static final String ATTR_RECORD_ID = "record_id"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_UNCLASSIFIED = "record_unclassified"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_PHYSIO = "record_physio"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_MIGEL = "record_migel"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_DRUG = "record_drug"; //$NON-NLS-1$
	private static final String TARMED_TRUE = "true"; //$NON-NLS-1$
	private static final String ATTR_UNIT_FACTOR = "unit_factor"; //$NON-NLS-1$
	private static final String ATTR_UNIT = "unit"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_LAB = "record_lab"; //$NON-NLS-1$
	private static final String ATTR_OBLIGATION = "obligation"; //$NON-NLS-1$
	private static final String ATTR_VALIDATE = "validate"; //$NON-NLS-1$
	private static final String ATTR_VAT_RATE = "vat_rate"; //$NON-NLS-1$
	private static final String ATTR_EXTERNAL_FACTOR_TT = "external_factor.tt"; //$NON-NLS-1$
	private static final String ATTR_SCALE_FACTOR_TT = "scale_factor.tt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_FACTOR_TT = "unit_factor.tt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_TT = "unit.tt"; //$NON-NLS-1$
	private static final String ATTR_EXTERNAL_FACTOR_MT = "external_factor.mt"; //$NON-NLS-1$
	private static final String ATTR_SCALE_FACTOR_MT = "scale_factor.mt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_FACTOR_MT = "unit_factor.mt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_MT = "unit.mt"; //$NON-NLS-1$
	private static final String ATTR_BODY_LOCATION = "body_location"; //$NON-NLS-1$
	private static final String ATTR_MEDICAL_ROLE = "medical_role"; //$NON-NLS-1$
	private static final String ATTR_BILLING_ROLE = "billing_role"; //$NON-NLS-1$
	private static final String ATTR_EAN_RESPONSIBLE = "ean_responsible"; //$NON-NLS-1$
	private static final String ATTR_EAN_PROVIDER = "ean_provider"; //$NON-NLS-1$
	public static final String ATTR_TARIFF_TYPE = "tariff_type"; //$NON-NLS-1$
	private static final String ATTR_TREATMENT = "treatment"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_TARMED = "record_tarmed"; //$NON-NLS-1$
	private static final String VK_SCALE = "VK_Scale"; //$NON-NLS-1$
	private static final String TL = "TL"; //$NON-NLS-1$
	private static final String AL = "AL"; //$NON-NLS-1$
	public static final String ELEMENT_REMARK = ATTR_REMARK; //$NON-NLS-1$
	private static final String ATTR_CASE_ID = "case_id"; //$NON-NLS-1$
	private static final String ATTR_INVOICE_DATE = "invoice_date"; //$NON-NLS-1$
	private static final String ATTR_INVOICE_ID = "invoice_id"; //$NON-NLS-1$
	private static final String ATTR_INVOICE_TIMESTAMP = "invoice_timestamp"; //$NON-NLS-1$
	private static final String ATTR_VERSION_DB = "version_db"; //$NON-NLS-1$
	private static final String ATTR_VERSION_SOFTWARE = "version_software"; //$NON-NLS-1$
	private static final String ATTR_FOCUS = "focus"; //$NON-NLS-1$
	private static final String ELEMENT_VALIDATOR = "validator"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
	private static final String ELEMENT_SOFTWARE = "software"; //$NON-NLS-1$
	private static final String ELEMENT_GENERATOR = "generator"; //$NON-NLS-1$
	private static final String ELEMENT_PACKAGE = "package"; //$NON-NLS-1$
	private static final String ELEMENT_PROLOG = "prolog"; //$NON-NLS-1$
	private static final String ELEMENT_RECIPIENT = "recipient"; //$NON-NLS-1$
	private static final String ELEMENT_INTERMEDIATE = "intermediate"; //$NON-NLS-1$
	private static final String ATTR_EAN_PARTY = "ean_party"; //$NON-NLS-1$
	private static final String ELEMENT_SENDER = "sender"; //$NON-NLS-1$
	private static final String ELEMENT_HEADER = "header"; //$NON-NLS-1$
	private static final String ATTR_ROLE = "role"; //$NON-NLS-1$
	private static final String ELEMENT_REQUEST = "request"; //$NON-NLS-1$
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
	public static final String ATTR_AMOUNT_TT = "amount.tt"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_MT = "amount.mt"; //$NON-NLS-1$
	public static final String ATTR_QUANTITY = "quantity"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_DUE = "amount_due"; //$NON-NLS-1$
	public static final String ELEMENT_SERVICES = "services"; //$NON-NLS-1$
	private static final String ELEMENT_DETAIL = "detail"; //$NON-NLS-1$
	private static final String ATTR_RESEND = "resend"; //$NON-NLS-1$
	private static final String ATTR_AMOUNT_OBLIGATIONS = "amount_obligations"; //$NON-NLS-1$
	public static final String ATTR_AMOUNT_PREPAID = "amount_prepaid"; //$NON-NLS-1$
	public static final String ELEMENT_BALANCE = "balance"; //$NON-NLS-1$
	public static final String ELEMENT_INVOICE = "invoice"; //$NON-NLS-1$
	public static final String ELEMENT_ANNULMENT = "annulment"; //$NON-NLS-1$
	public static final Namespace ns = Namespace.getNamespace(ELEMENT_INVOICE,
		"http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
	public static final String FIELDNAME_TIMESTAMPXML = "TimeStampXML"; //$NON-NLS-1$
	Fall actFall;
	Patient actPatient;
	Mandant actMandant;
	double tpTarmedTL = 0;
	double tpTarmedAL = 0;
	
	List<IDiagnose> diagnosen = new ArrayList<IDiagnose>();
	
	Rechnung rn;
	
	private Money mTarmed;
	private Money mTarmedTL;
	private Money mTarmedAL;
	private Money mKant;
	private Money mUebrige;
	private Money mAnalysen;
	private Money mMigel;
	private Money mPhysio;
	private Money mMedikament;
	private Money mTotal;
	private Money mPaid;
	private Money mDue;
	static TarmedACL ta;
	private String outputDir;
	private static final String PREFIX = "TarmedRn:"; //$NON-NLS-1$
	private static final Log log = Log.get("XMLExporter"); //$NON-NLS-1$
	private static final String TARMED_FALSE = "false";
	
	/**
	 * Reset exporter
	 */
	public void clear(){
		actFall = null;
		actPatient = null;
		actMandant = null;
		tpTarmedTL = 0;
		tpTarmedAL = 0;
		diagnosen = new ArrayList<IDiagnose>();
		rn = null;
		
		mTarmed = new Money();
		mTarmedTL = new Money();
		mTarmedAL = new Money();
		mKant = new Money();
		mUebrige = new Money();
		mAnalysen = new Money();
		mMigel = new Money();
		mPhysio = new Money();
		mMedikament = new Money();
		mTotal = new Money();
		mPaid = new Money();
		mDue = new Money();
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
	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rnn,
		Properties props){
		Result<Rechnung> ret = new Result<Rechnung>();
		if (outputDir == null) {
			SWTHelper.SimpleDialog dlg =
				new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
					public Control getControl(Composite parent){
						return createSettingsControl(parent);
					}
					
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
	@SuppressWarnings("unchecked")
	public Document doExport(final Rechnung rechnung, final String dest,
		final IRnOutputter.TYPE type, final boolean doVerify){
		clear();
		// create a object for managing vat rates and values on invoice level
		VatRateSum vatSummer = new VatRateSum();
		Namespace nsxsi =
			Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$ //$NON-NLS-2$
		// Namespace
		// nsschema=Namespace.getNamespace("schemaLocation","http://www.xmlData.ch/xmlInvoice/XSD
		// MDInvoiceRequest_400.xsd");
		Namespace nsdef = Namespace.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
		rn = rechnung;
		mPaid = rn.getAnzahlung();
		
		if (NamedBlob.exists(PREFIX + rechnung.getNr())) {
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
				Element invoice = root.getChild(ELEMENT_INVOICE, ns);
				Element balance = invoice.getChild(ELEMENT_BALANCE, ns);
				Money anzInBill =
					XMLTool.xmlDoubleToMoney(balance.getAttributeValue(ATTR_AMOUNT_PREPAID));
				if (!mPaid.equals(anzInBill)) {
					balance.setAttribute(ATTR_AMOUNT_PREPAID, XMLTool.moneyToXmlDouble(mPaid)); // 10335
					mDue =
						XMLTool
							.xmlDoubleToMoney(balance.getAttributeValue(ATTR_AMOUNT_OBLIGATIONS));
					mDue.subtractMoney(mPaid);
					mDue.roundTo5();
					balance.setAttribute(ATTR_AMOUNT_DUE, XMLTool.moneyToXmlDouble(mDue)); // 10340
				}
				if (type.equals(IRnOutputter.TYPE.COPY)) {
					invoice.setAttribute(ATTR_RESEND, Boolean.toString(true));
				} else if (type.equals(TYPE.STORNO)) {
					Element detail = invoice.getChild(ELEMENT_DETAIL, ns);
					Element services = detail.getChild(ELEMENT_SERVICES, ns);
					List<Element> sr = services.getChildren();
					for (Element el : sr) {
						try {
							negate(el, ATTR_QUANTITY);
							// negate(el,"unit.mt");
							// negate(el,"unit.tt");
							negate(el, ATTR_AMOUNT_MT);
							negate(el, ATTR_AMOUNT_TT);
							// negate(el,"unit");
							negate(el, ATTR_AMOUNT);
							/*
							 * Money betrag=XMLTool.xmlDoubleToMoney(el.getAttributeValue
							 * ("amount")); el.setAttribute("amount",
							 * XMLTool.moneyToXmlDouble(betrag.negate()));
							 */
							
						} catch (Exception ex) {
							ExHandler.handle(ex);
						}
					}
					// Money
					// betrag=XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount"));
					// balance.setAttribute("amount",XMLTool.moneyToXmlDouble(betrag.negate()));
					negate(balance, ATTR_AMOUNT);
					negate(balance, ATTR_AMOUNT_TARMED);
					negate(balance, ATTR_AMOUNT_TARMED_MT);
					negate(balance, ATTR_AMOUNT_TARMED_TT);
					negate(balance, ATTR_AMOUNT_CANTONAL);
					negate(balance, ATTR_AMOUNT_UNCLASSIFIED);
					negate(balance, ATTR_AMOUNT_DRUG);
					negate(balance, ATTR_AMOUNT_LAB);
					negate(balance, ATTR_AMOUNT_MIGEL);
					negate(balance, ATTR_AMOUNT_PHYSIO);
					negate(balance, ATTR_AMOUNT_OBLIGATIONS);
					balance.setAttribute(ATTR_AMOUNT_DUE, StringConstants.DOUBLE_ZERO);
					balance.setAttribute(ATTR_AMOUNT_PREPAID, StringConstants.DOUBLE_ZERO);
					
					// change the purpose if a payant element is present
					Element payant = invoice.getChild(ELEMENT_TIERS_PAYANT, ns);
					if (payant != null) {
						payant.setAttribute("purpose", ELEMENT_ANNULMENT); //$NON-NLS-1$
					}
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
		// We try to figure out whether we should use Tiers Payant or Tiers
		// Garant.
		// if unsure, we make it TG
		String tiers = TIERS_GARANT;
		Patient pat = actFall.getPatient();
		Kontakt rnAdressat = actFall.getGarant();
		
		if ((kostentraeger != null) && (kostentraeger.isValid())) {
			if (rnAdressat.equals(kostentraeger)) {
				tiers = TIERS_PAYANT;
			} else {
				tiers = TIERS_GARANT;
			}
		} else {
			kostentraeger = rnAdressat;
			tiers = TIERS_GARANT;
		}
		String tcCode = TarmedRequirements.getTCCode(actMandant);
		
		if (kostentraeger == null) {
			kostentraeger = actPatient;
		}
		Document xmlRn; // Ziffern "Referenzhandbuch Arztrechnung XML 4.0"
		Element root = new Element(ELEMENT_REQUEST, ns); // 10020/21
		// root.addNamespaceDeclaration(nsdef);
		root.addNamespaceDeclaration(nsxsi); // 10022
		root.setAttribute("schemaLocation", //$NON-NLS-1$
			"http://www.xmlData.ch/xmlInvoice/XSD MDInvoiceRequest_400.xsd", nsxsi); //$NON-NLS-1$
		
		// Rolle
		root.setAttribute(ATTR_ROLE, getRole(actFall)); // 10030/32
		xmlRn = new Document(root);
		
		// header
		Element header = new Element(ELEMENT_HEADER, ns); // 10050
		root.addContent(header);
		Element sender = new Element(ELEMENT_SENDER, ns); // 10051
		String mEAN = TarmedRequirements.getEAN(actMandant); // (String)actMandant.getInfoElement(
		// "EAN");
		
		sender.setAttribute(ATTR_EAN_PARTY, getSenderEAN(actMandant));
		String kEAN = TarmedRequirements.getEAN(kostentraeger); // (String)kostentraeger.
		String kg = kostentraeger.getLabel();
		// getInfoElement("EAN");
		String rEAN = TarmedRequirements.getRecipientEAN(kostentraeger);
		if (rEAN.equals("unknown")) { //$NON-NLS-1$
			rEAN = kEAN;
		}
		
		String iEAN = getIntermediateEAN(actFall);
		if (StringTool.isNothing(iEAN)) {
			// make validator happy
			if (!rEAN.matches("(20[0-9]{11}|76[0-9]{11})")) { //$NON-NLS-1$
				if (kEAN.matches("(20[0-9]{11}|76[0-9]{11})")) { //$NON-NLS-1$
					iEAN = kEAN;
				} else {
					iEAN = TarmedRequirements.EAN_PSEUDO;
				}
			} else {
				iEAN = rEAN;
			}
		}
		Element intermediate = new Element(ELEMENT_INTERMEDIATE, ns); // 10052
		intermediate.setAttribute(ATTR_EAN_PARTY, iEAN);
		
		Element recipient = new Element(ELEMENT_RECIPIENT, ns); // 10053
		recipient.setAttribute(ATTR_EAN_PARTY, rEAN);
		
		header.addContent(sender);
		header.addContent(intermediate);
		header.addContent(recipient);
		
		// prolog
		Element prolog = new Element(ELEMENT_PROLOG, ns); // 10060
		root.addContent(prolog);
		VersionInfo vi = new VersionInfo(CoreHub.Version);
		// Versionen unter 100 werden nicht akzeptiert
		// int
		// tmi=Integer.parseInt(vi.maior())*100+Integer.parseInt(vi.minor())*10+Integer.parseInt(vi.
		// rev());
		Element spackage = new Element(ELEMENT_PACKAGE, ns); // 10070
		spackage.setText("Elexis"); //$NON-NLS-1$
		spackage.setAttribute(ATTR_VERSION, vi.getMaior() + vi.getMinor() + vi.getRevision()); // 10071
		spackage.setAttribute(ATTR_ID, "0"); // 10072 //$NON-NLS-1$
		prolog.addContent(spackage);
		
		Element generator = new Element(ELEMENT_GENERATOR, ns); // 10080
		Element gsoft = new Element(ELEMENT_SOFTWARE, ns); // 10081
		generator.addContent(gsoft);
		gsoft.setText("JDOM"); //$NON-NLS-1$
		// 10082
		gsoft.setAttribute(ATTR_VERSION, "100"); // Damit die Version akzeptiert wird, muss sie //$NON-NLS-1$
		// wieder mindestens 100 sein
		gsoft.setAttribute(ATTR_ID, StringConstants.ZERO); // 10083
		prolog.addContent(generator);
		
		Element validator = new Element(ELEMENT_VALIDATOR, ns); // 10100
		validator.setAttribute(ATTR_FOCUS, "tarmed"); // 10111 //$NON-NLS-1$
		validator.setAttribute(ATTR_VERSION_SOFTWARE,
			vi.getMaior() + vi.getMinor() + vi.getRevision()); // 10130
		validator.setAttribute(ATTR_VERSION_DB, "401"); // 10131 //$NON-NLS-1$
		validator.setAttribute(ATTR_ID, StringConstants.ZERO); // 10132
		validator.setText("Elexis TarmedVerifier"); //$NON-NLS-1$
		prolog.addContent(validator);
		
		// invoice
		Element invoice = new Element(ELEMENT_INVOICE, ns); // 10150
		root.addContent(invoice);
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
		
		invoice.setAttribute(ATTR_INVOICE_TIMESTAMP, ts); // 10152
		invoice.setAttribute(ATTR_INVOICE_ID, rn.getRnId()); // 10153
		invoice.setAttribute(ATTR_INVOICE_DATE,
			new TimeTool(rn.getDatumRn()).toString(TimeTool.DATE_MYSQL) + "T00:00:00"); // 10154 //$NON-NLS-1$
		invoice.setAttribute(ATTR_RESEND, Boolean.toString(type.equals(IRnOutputter.TYPE.COPY))); // 10170
		invoice.setAttribute(ATTR_CASE_ID, rn.getFall().getId()); // 10180
		String bem = rn.getBemerkung();
		if (!StringTool.isNothing(bem)) { // 10200
			Element remark = new Element(ELEMENT_REMARK, ns); // 10201
			remark.setText(rn.getBemerkung());
			invoice.addContent(remark);
		}
		
		// 10250 weggelassen
		
		// Balance aufbauen // 10300
		String curr = (String) CoreHub.actMandant.getInfoElement(Messages.XMLExporter_Currency); // 10310
		if (StringTool.isNothing(curr)) {
			curr = "CHF"; //$NON-NLS-1$
		}
		List<Konsultation> lb = rn.getKonsultationen();
		
		Element services = new Element(ELEMENT_SERVICES, ns);
		
		// DecimalFormat df = new DecimalFormat("#0.00");
		
		// Alle Informationen je Konsultation sammeln
		// alle Preise (in Rappen) auflisten
		String lastDate = StringConstants.EMPTY;
		int sessionNumber = 1;
		
		// To make the validator happy, the attribute date_begin must duplicate
		// exactly
		// the date of the first billing position end date_end must duplicate
		// exactly
		// the date of the last billed consultation. If we have non-billed
		// entries in
		// the patient's record we must forget these for the sake of
		// xml-confirmity
		// so we use ttFirst and ttLast to check he the dates (instead of the
		// begin end end
		// dates that are stored in the bill
		TimeTool ttFirst = new TimeTool(TimeTool.END_OF_UNIX_EPOCH);
		TimeTool ttLast = new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
		int recordNumber = 1;
		
		// use double to sum AL and TL protion -> more accurate result than using Money
		double sumTarmedAL = 0.0;
		double sumTarmedTL = 0.0;
		
		for (Konsultation b : lb) {
			List<IDiagnose> ld = b.getDiagnosen();
			for (IDiagnose dg : ld) {
				String dgc = dg.getCode();
				if (dgc != null) {
					diagnosen.add(dg);
				}
			}
			List<Verrechnet> lv = b.getLeistungen();
			if (lv.size() == 0) {
				continue;
			}
			TimeTool tt = new TimeTool(b.getDatum());
			if (tt.isBefore(ttFirst)) { // make validator happy
				ttFirst.set(tt);
			}
			if (tt.isAfter(ttLast)) { // make validator even happier
				ttLast.set(tt);
			}
			String dateShort = tt.toString(TimeTool.DATE_COMPACT);
			String dateForTarmed = makeTarmedDatum(b.getDatum());
			if (dateShort.equals(lastDate)) {
				sessionNumber++;
			} else {
				sessionNumber = 1;
			}
			
			lastDate = dateShort;
			// unit.mt x unit_factor.mt x scale_factor.mt x external_factor.mt x
			// quantity =
			// amount.mt
			// unit.tt x unit_factor.tt x scale_factor.tt x external_factor.tt x
			// quantity =
			// amount.tt
			// amount
			boolean bRFE = false; // RFE already encoded
			for (Verrechnet vv : lv) {
				Element el;
				double zahl = vv.getZahl();
				IVerrechenbar v = vv.getVerrechenbar();
				
				if (v == null) {
					log.log(Messages.XMLExporter_ErroneusBill + rn.getNr()
						+ " Null-Verrechenbar bei Kons " //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
						+ b.getLabel(), Log.ERRORS);
					continue;
				}
				if (v instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v;
					String arzl = vv.getDetail(AL);
					String tecl = vv.getDetail(TL);
					double primaryScale = vv.getPrimaryScaleFactor();
					double secondaryScale = vv.getSecondaryScaleFactor();
					
					double tlTl, tlAL, mult;
					mult = tl.getVKMultiplikator(tt, actFall);
					if ((arzl != null && !arzl.isEmpty()) && (tecl != null && !tecl.isEmpty())) {
						tlTl = Double.parseDouble(tecl);
						tlAL = Double.parseDouble(arzl);
						
					} else {
						tlTl = tl.getTL();
						tlAL = tl.getAL();
					}
					// build monetary values of this TarmedLeistung
					Money mAL =
						new Money((int) Math.round(tlAL * mult * zahl * primaryScale
							* secondaryScale));
					Money mTL =
						new Money((int) Math.round(tlTl * mult * zahl * primaryScale
							* secondaryScale));
					Money mAmountLocal =
						new Money((int) Math.round((tlAL + tlTl) * mult * zahl * primaryScale
							* secondaryScale));
					
					// sum tax points and monetary value
					tpTarmedTL += tlTl * zahl;
					tpTarmedAL += tlAL * zahl;
					
					sumTarmedAL += tlAL * mult * zahl * primaryScale * secondaryScale;
					sumTarmedTL += tlTl * mult * zahl * primaryScale * secondaryScale;
					
					mTarmed.addCent(mAmountLocal.getCents());
					
					el = new Element(ELEMENT_RECORD_TARMED, ns); // 22000
					el.setAttribute(ATTR_TREATMENT, "ambulatory"); // 22050 //$NON-NLS-1$
					el.setAttribute(ATTR_TARIFF_TYPE, "001"); // 22060 //$NON-NLS-1$
					Hashtable<String, String> ext = tl.loadExtension();
					String bezug = ext.get("Bezug"); // 22360 //$NON-NLS-1$
					if (!StringTool.isNothing(bezug)) {
						el.setAttribute("ref_code", bezug); //$NON-NLS-1$
					}
					el.setAttribute(ATTR_EAN_PROVIDER, TarmedRequirements.getEAN(b.getMandant())); // 22390
					el.setAttribute(ATTR_EAN_RESPONSIBLE, getResponsibleEAN(b)); // 22400
					el.setAttribute(ATTR_BILLING_ROLE, "both"); // 22410 //$NON-NLS-1$
					el.setAttribute(ATTR_MEDICAL_ROLE, "self_employed"); // 22430 //$NON-NLS-1$
					
					el.setAttribute(ATTR_BODY_LOCATION, TarmedLeistung.getSide(vv)); // 22450
					
					el.setAttribute(ATTR_UNIT_MT, XMLTool.doubleToXmlDouble(tlAL / 100.0, 2)); // 22470
					el.setAttribute(ATTR_UNIT_FACTOR_MT, XMLTool.doubleToXmlDouble(mult, 2)); // 22480
					// (strebt
					// gegen
					// 0)
					el.setAttribute(ATTR_SCALE_FACTOR_MT,
						XMLTool.doubleToXmlDouble(primaryScale, 1)); // 22490
					el.setAttribute(ATTR_EXTERNAL_FACTOR_MT,
						XMLTool.doubleToXmlDouble(secondaryScale, 1)); // 22500
					el.setAttribute(ATTR_AMOUNT_MT, XMLTool.moneyToXmlDouble(mAL)); // 22510
					
					el.setAttribute(ATTR_UNIT_TT, XMLTool.doubleToXmlDouble(tlTl / 100.0, 2)); // 22520
					el.setAttribute(ATTR_UNIT_FACTOR_TT, XMLTool.doubleToXmlDouble(mult, 2)); // 22530
					el.setAttribute(ATTR_SCALE_FACTOR_TT,
						XMLTool.doubleToXmlDouble(primaryScale, 1)); // 22540
					el.setAttribute(ATTR_EXTERNAL_FACTOR_TT,
						XMLTool.doubleToXmlDouble(secondaryScale, 1)); // 22550
					el.setAttribute(ATTR_AMOUNT_TT, XMLTool.moneyToXmlDouble(mTL)); // 22560
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 22570
					setVatAttribute(vv, mAmountLocal, el, vatSummer); // 22590 //$NON-NLS-1$
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 22620
					
					el.setAttribute(ATTR_OBLIGATION,
						Boolean.toString(TarmedLeistung.isObligation(vv)));
					if (!bRFE) {
						List<RFE> rfes = RFE.getRfeForKons(b.getId());
						if (rfes.size() > 0) {
							StringBuilder sb = new StringBuilder();
							for (RFE rfe : rfes) {
								sb.append("551_").append(rfe.getCode()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
							}
							el.setAttribute(ATTR_REMARK, sb.toString());
						}
						bRFE = true;
					}
					
				} else if (v instanceof Labor2009Tarif) {
					el = new Element(ELEMENT_RECORD_LAB, ns); // 28000
					el.setAttribute(ATTR_TARIFF_TYPE, v.getCodeSystemCode());
					Labor2009Tarif ll = (Labor2009Tarif) v;
					double mult = ll.getFactor(tt, actFall);
					Money preis = vv.getNettoPreis();
					double korr = preis.getCents() / mult;
					el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
					setVatAttribute(vv, mAmountLocal, el, vatSummer); // 28590
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
					mAnalysen.addMoney(mAmountLocal);
					
				} else if (v instanceof LaborLeistung) {
					el = new Element(ELEMENT_RECORD_LAB, ns); // 28000
					el.setAttribute(ATTR_TARIFF_TYPE, "316"); // 28060 //$NON-NLS-1$
					LaborLeistung ll = (LaborLeistung) v;
					double mult = ll.getFactor(tt, actFall);
					// Money preis = vv.getEffPreis(); // b.getEffPreis(v);
					Money preis = vv.getNettoPreis();
					double korr = preis.getCents() / mult;
					el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
					setVatAttribute(vv, mAmountLocal, el, vatSummer); // 28590 //$NON-NLS-1$
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
					mAnalysen.addMoney(mAmountLocal);
				} else if ((v instanceof Medikament) || (v instanceof Medical)
					|| (v.getCodeSystemCode() == "400")) { //$NON-NLS-1$
					el = new Element(ELEMENT_RECORD_DRUG, ns);
					Artikel art = (Artikel) v;
					double mult = art.getFactor(tt, actFall);
					Money preis = vv.getNettoPreis();
					Money mAmountLocal = new Money(preis);
					// new as of 3/2011: Correct handling of package fractions
					Money einzelpreis = vv.getBruttoPreis();
					einzelpreis.multiply(vv.getPrimaryScaleFactor());
					
					double cnt = vv.getSecondaryScaleFactor();
					if (cnt != 1.0) {
						zahl *= cnt;
					} else {
						mAmountLocal.multiply(zahl);
					}
					
					// end corrections
					el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(einzelpreis));
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2));
					el.setAttribute(ATTR_TARIFF_TYPE, "400"); // Pharmacode-basiert //$NON-NLS-1$
					String pk = ((Artikel) v).getPharmaCode();
					el.setAttribute(ATTR_CODE, StringTool.pad(StringTool.LEFT, '0', pk, 7));
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal));
					setVatAttribute(vv, mAmountLocal, el, vatSummer);
					String ckzl = art.getExt(MedikamentImporter.KASSENTYP);
					if (ckzl.equals("1")) {
						el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
					} else {
						el.setAttribute(ATTR_OBLIGATION, TARMED_FALSE);
					}
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
					mMedikament.addMoney(mAmountLocal);
				} else if (v instanceof MiGelArtikel) {
					el = new Element(ELEMENT_RECORD_MIGEL, ns);
					// Money preis = vv.getEffPreis(); // b.getEffPreis(v);
					Money preis = vv.getNettoPreis();
					el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(preis));
					el.setAttribute(ATTR_UNIT_FACTOR, "1.0"); //$NON-NLS-1$
					el.setAttribute(ATTR_TARIFF_TYPE, "452"); // MiGeL ab 2001-basiert //$NON-NLS-1$
					el.setAttribute(ATTR_CODE, ((MiGelArtikel) v).getCode());
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal));
					setVatAttribute(vv, mAmountLocal, el, vatSummer);
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
					mMigel.addMoney(mAmountLocal);
				} else if (v instanceof PhysioLeistung) {
					el = new Element(ELEMENT_RECORD_PHYSIO, ns);
					el.setAttribute(ATTR_TARIFF_TYPE, v.getCodeSystemCode()); // 28060
					PhysioLeistung pl = (PhysioLeistung) v;
					double mult = pl.getFactor(tt, actFall);
					Money preis = vv.getNettoPreis();
					double korr = preis.getCents() / mult;
					el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
					setVatAttribute(vv, mAmountLocal, el, vatSummer); // 28590
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
					// get EAN provider
					String ean = TarmedRequirements.getEAN(b.getMandant());
					if (ean.equals(TarmedRequirements.EAN_PSEUDO))
						ean = "unknown";
					el.setAttribute(ATTR_EAN_PROVIDER, ean);
					// get EAN resposible
					ean = getResponsibleEAN(b);
					if (ean.equals(TarmedRequirements.EAN_PSEUDO))
						ean = "unknown";
					el.setAttribute(ATTR_EAN_RESPONSIBLE, ean);
					
					mPhysio.addMoney(mAmountLocal);
					
				} else {
					Money preis = vv.getNettoPreis();
					el = new Element(ELEMENT_RECORD_UNCLASSIFIED, ns);
					el.setAttribute(ATTR_TARIFF_TYPE, v.getCodeSystemCode());
					el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(preis));
					el.setAttribute(ATTR_UNIT_FACTOR, "1.0"); //$NON-NLS-1$
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal));
					setVatAttribute(vv, mAmountLocal, el, vatSummer);
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
					el.setAttribute(ATTR_OBLIGATION, "false"); //$NON-NLS-1$
					el.setAttribute("external_factor", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
					mUebrige.addMoney(mAmountLocal);
				}
				el.setAttribute(ATTR_RECORD_ID, Integer.toString(recordNumber++)); // 22010
				el.setAttribute("number", Integer.toString(sessionNumber)); // 22030 //$NON-NLS-1$
				el.setAttribute(ATTR_QUANTITY, Double.toString(zahl)); // 22350
				el.setAttribute(ATTR_DATE_BEGIN, dateForTarmed); // 22370
				el.setText(vv.getText()); // 22340
				// el.setAttribute("code",v.getCode()); // 22330
				setAttributeWithDefault(el, ATTR_CODE, v.getCode(), StringConstants.ZERO); // 22330
				services.addContent(el);
			}
		}
		mTotal.addMoney(mTarmed).addMoney(mAnalysen).addMoney(mMedikament).addMoney(mUebrige)
			.addMoney(mKant).addMoney(mPhysio).addMoney(mMigel);
		
		Element balance = new Element(ELEMENT_BALANCE, ns); // 10300
		balance.setAttribute("currency", curr); // 10310 //$NON-NLS-1$
		balance.setAttribute(ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mTotal)); // 10330
		// mPaid=rn.getAnzahlung();
		balance.setAttribute(ATTR_AMOUNT_PREPAID, XMLTool.moneyToXmlDouble(mPaid)); // 10335
		mDue = new Money(mTotal);
		mDue.subtractMoney(mPaid);
		mDue.roundTo5();
		
		// round and create Money from sumTarmed double values
		mTarmedAL = new Money((int) Math.round(sumTarmedAL));
		mTarmedTL = new Money((int) Math.round(sumTarmedTL));
		
		// chfDue=Math.round(chfDue*20.0)/20.0;
		balance.setAttribute(ATTR_AMOUNT_DUE, XMLTool.moneyToXmlDouble(mDue)); // 10340
		balance.setAttribute(ATTR_AMOUNT_TARMED, XMLTool.moneyToXmlDouble(mTarmed)); // 10341
		balance.setAttribute(ATTR_UNIT_TARMED_MT, XMLTool.doubleToXmlDouble(tpTarmedAL / 100.0, 2)); // 10348
		balance.setAttribute(ATTR_AMOUNT_TARMED_MT, XMLTool.moneyToXmlDouble(mTarmedAL)); // 10349
		balance.setAttribute(ATTR_UNIT_TARMED_TT, XMLTool.doubleToXmlDouble(tpTarmedTL / 100.0, 2)); // 10350
		balance.setAttribute(ATTR_AMOUNT_TARMED_TT, XMLTool.moneyToXmlDouble(mTarmedTL)); // 10351
		balance.setAttribute(ATTR_AMOUNT_CANTONAL, StringConstants.DOUBLE_ZERO); // 10342
		balance.setAttribute(ATTR_AMOUNT_UNCLASSIFIED, XMLTool.moneyToXmlDouble(mUebrige)); // 10343
		balance.setAttribute(ATTR_AMOUNT_LAB, XMLTool.moneyToXmlDouble(mAnalysen)); // 10344
		balance.setAttribute(ATTR_AMOUNT_PHYSIO, XMLTool.moneyToXmlDouble(mPhysio)); // 10346
		balance.setAttribute(ATTR_AMOUNT_DRUG, XMLTool.moneyToXmlDouble(mMedikament)); // 10347
		balance.setAttribute(ATTR_AMOUNT_MIGEL, XMLTool.moneyToXmlDouble(mMigel)); // 10345
		balance.setAttribute(ATTR_AMOUNT_OBLIGATIONS, XMLTool.moneyToXmlDouble(mTotal)); // 10352
		
		// 10370 Vat on invoice level
		Element vat = new Element(ELEMENT_VAT, ns);
		
		String vatNumber = actMandant.getRechnungssteller().getInfoString(VAT_MANDANTVATNUMBER);
		if (vatNumber != null && vatNumber.length() > 0)
			vat.setAttribute(ELEMENT_VAT_NUMBER, vatNumber);
		
		vat.setAttribute(ELEMENT_VAT, XMLTool.doubleToXmlDouble(vatSummer.sumvat, 2));
		
		// 10380 Vat on rate level
		VatRateElement[] vatValues = vatSummer.rates.values().toArray(new VatRateElement[0]);
		Arrays.sort(vatValues);
		for (VatRateElement rate : vatValues) {
			Element vatrate = new Element(ATTR_VAT_RATE, ns);
			vatrate.setAttribute(ATTR_VAT_RATE, XMLTool.doubleToXmlDouble(rate.scale, 2));
			vatrate.setAttribute(ATTR_AMOUNT, XMLTool.doubleToXmlDouble(rate.sumamount, 2));
			vatrate.setAttribute(ELEMENT_VAT, XMLTool.doubleToXmlDouble(rate.sumvat, 2));
			vat.addContent(vatrate);
		}
		
		balance.addContent(vat);
		invoice.addContent(balance);
		
		String esrmode = actMandant.getRechnungssteller().getInfoString(ta.ESR5OR9);
		Element esr; // 10400
		String userdata = rn.getRnId();
		ESR besr =
			new ESR(actMandant.getRechnungssteller().getInfoString(ta.ESRNUMBER), actMandant
				.getRechnungssteller().getInfoString(ta.ESRSUB), userdata, ESR.ESR27);
		
		// String ESRNumber=m.getInfoString(ta.ESRNUMBER);
		// String ESRSubid=m.getInfoString(ta.ESRSUB);
		// Zur Zeit nur esr9 unterstützt
		if (esrmode.equals("esr5")) { // esr5 oder esr9 //$NON-NLS-1$
			esr = new Element("esr5", ns); //$NON-NLS-1$
			esr.setAttribute(ATTR_PARTICIPANT_NUMBER, besr.makeParticipantNumber(true)); // Teilnehmernummer
			esr.setAttribute(ATTR_TYPE, actMandant.getRechnungssteller().getInfoString(ta.ESRPLUS)); // 15
			// oder
			// 15plus
			// (mit
			// oder ohne Betrag)
			// String refnr="01000234004554504"; // TODO
			// String codingline="01322234°3423424"; // TODO
			// esr.setAttribute("reference_number",refnr); // Referenz-Nummer
			// esr.setAttribute("coding_line",codingline); // codierzeile
		} else if (esrmode.equals("esr9")) { // esr9 // 10403 //$NON-NLS-1$
			esr = new Element("esr9", ns); //$NON-NLS-1$
			esr.setAttribute(ATTR_PARTICIPANT_NUMBER, besr.makeParticipantNumber(true));// 9-stellige
			// Teilnehmernummer
			// 10451
			// esr.setAttribute("type",m.getInfoString(ta.ESRPLUS)); // 16or27
			// oder 16or27plus
			esr.setAttribute(ATTR_TYPE, "16or27"); // Nur dieses Format unterstützt 10461 //$NON-NLS-1$
			String refnr = besr.makeRefNr(true);
			// String
			// codingline=besr.createCodeline(mDue.getCentsAsString(),null);
			String codingline =
				besr.createCodeline(XMLTool.moneyToXmlDouble(mDue).replaceFirst("[.,]", ""), null); //$NON-NLS-1$ //$NON-NLS-2$
			esr.setAttribute("reference_number", refnr); // 16 oder 27 stellige ref nr 10470 //$NON-NLS-1$
			esr.setAttribute("coding_line", codingline); // codierzeile 10479 //$NON-NLS-1$
		} else {
			MessageDialog.openError(null, Messages.XMLExporter_MandatorErrorCaption,
				Messages.XMLExporter_MandatorErrorText);
			return null;
		}
		String bankid = actMandant.getRechnungssteller().getInfoString(ta.RNBANK);
		if (!bankid.equals("")) { // Bankverbindung -> BESR 10480 //$NON-NLS-1$
			Organisation bank = Organisation.load(bankid);
			Element eBank = new Element("bank", ns); // 10500 //$NON-NLS-1$
			Element company = buildAdressElement(bank); // 10511-10670
			eBank.addContent(company);
			esr.addContent(eBank);
		}
		invoice.addContent(esr);
		
		Element eTiers = null;
		if (tiers.equals(TIERS_GARANT)) {
			eTiers = new Element(ELEMENT_TIERS_GARANT, ns); // 11020 //$NON-NLS-1$
			String paymentPeriode = actMandant.getRechnungssteller().getInfoString("rnfrist"); //$NON-NLS-1$
			if (StringTool.isNothing(paymentPeriode)) {
				paymentPeriode = "30"; //$NON-NLS-1$
			}
			eTiers.setAttribute("payment_periode", "P" + paymentPeriode + "D"); // 11021 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			eTiers = new Element(ELEMENT_TIERS_PAYANT, ns); // 11260 //$NON-NLS-1$
			// to simplify things for now we do no accept modifications
			eTiers.setAttribute("invoice_modification", "false"); // 11262 //$NON-NLS-1$ //$NON-NLS-2$
			eTiers.setAttribute("purpose", ELEMENT_INVOICE); // 11265 //$NON-NLS-1$
		}
		
		Element biller = new Element("biller", ns); // 11070 -> 11400 //$NON-NLS-1$
		// biller.setAttribute("ean_party",actMandant.getInfoString("EAN")); //
		// 11402
		biller.setAttribute(ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(actMandant.getRechnungssteller())); // 11402
		biller.setAttribute("zsr", TarmedRequirements.getKSK(actMandant.getRechnungssteller())); // actMandant.getInfoString //$NON-NLS-1$
		// ("KSK"));
		// // 11403
		String spec = actMandant.getRechnungssteller().getInfoString(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			biller.setAttribute("specialty", spec); // 11404 //$NON-NLS-1$
		}
		biller.addContent(buildRechnungsstellerAdressElement(actMandant.getRechnungssteller())); // 11600-11680
		eTiers.addContent(biller);
		
		Element provider = new Element("provider", ns); // 11080 -> 11800 //$NON-NLS-1$
		// 11802
		provider.setAttribute(ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(actMandant.getRechnungssteller())); // 11802
		provider.setAttribute("zsr", TarmedRequirements.getKSK(actMandant.getRechnungssteller())); // actMandant.getInfoString //$NON-NLS-1$
		// ("KSK"));
		// // 11803
		spec = actMandant.getRechnungssteller().getInfoString(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			provider.setAttribute("specialty", spec); // 11804 //$NON-NLS-1$
		}
		provider.addContent(buildRechnungsstellerAdressElement(actMandant.getRechnungssteller())); // 11830-11680
		eTiers.addContent(provider);
		
		Element onlineElement = null; // tschaller: see comments in
		// buildOnlineElement
		
		Element insurance = new Element("insurance", ns); // 11090 //$NON-NLS-1$
		// The 'insurance' element is optional in Tiers Garant so in TG we only
		// insert this Element,
		// if we have all data absolutely correct.
		// In Tiers Payant, the insurance element is mandatory, and,
		// furthermore, MUST be an
		// Organization. So in TP, we insert an insurance element in any case,
		// and, if the guarantor
		// is a person, we "convert" it to an organization to make the validator
		// happy
		if (tiers.equals(TIERS_GARANT)) {
			if (kostentraeger.istOrganisation()) {
				if (kEAN.matches("[0-9]{13,13}")) { //$NON-NLS-1$
					insurance.setAttribute(ATTR_EAN_PARTY, kEAN);
					insurance.addContent(buildAdressElement(kostentraeger));
					eTiers.addContent(insurance);
				}
			}
		} else {
			// insurance.addContent(buildAdressElement(kostentraeger)); // must
			// be an organization,
			// so we fake one
			/*
			 * if(!kEAN.matches("[0-9]{13,13}")){ kEAN="2000000000000"; }
			 */
			insurance.setAttribute(ATTR_EAN_PARTY, kEAN);
			Element company = new Element("company", ns); //$NON-NLS-1$
			Element companyname = new Element("companyname", ns); //$NON-NLS-1$
			companyname.setText(StringTool.limitLength(kostentraeger.get(Kontakt.FLD_NAME1), 35));
			company.addContent(companyname);
			company.addContent(buildPostalElement(kostentraeger));
			company.addContent(buildTelekomElement(kostentraeger));
			// company.addContent(buildOnlineElement(kostentraeger)); //
			// tschaller: see comments in
			// buildOnlineElement
			onlineElement = buildOnlineElement(kostentraeger);
			if (onlineElement != null) {
				company.addContent(onlineElement);
			}
			
			insurance.addContent(company);
			eTiers.addContent(insurance);
			// note this may lead to a person mistreated as organization. So
			// these faults should be
			// caught when generating bills
			
		}
		
		Element patient = new Element("patient", ns); // 11100 //$NON-NLS-1$
		
		// patient.setAttribute("unique_id",rn.getFall().getId()); // this is
		// optional and should be
		// ssn13 type. leave it out for now
		String gender = "male"; //$NON-NLS-1$
		if (pat == null) {
			MessageDialog.openError(null, Messages.XMLExporter_ErrorCaption,
				Messages.XMLExporter_NoPatientText);
			return null;
		}
		if (StringTool.isNothing(pat.getGeschlecht())) { // we fall back to
			// female. why not?
			pat.set(Person.SEX, Person.FEMALE);
		}
		if (pat.getGeschlecht().equals(Person.FEMALE)) {
			gender = "female"; //$NON-NLS-1$
		}
		patient.setAttribute("gender", gender); //$NON-NLS-1$
		String gebDat = pat.getGeburtsdatum();
		if (StringTool.isNothing(gebDat)) { // make validator happy if we don't
			// know the birthdate
			patient.setAttribute(ATTR_BIRTHDATE, "0001-00-00T00:00:00"); //$NON-NLS-1$
		} else {
			patient.setAttribute(ATTR_BIRTHDATE,
				new TimeTool(pat.getGeburtsdatum()).toString(TimeTool.DATE_MYSQL) + "T00:00:00"); //$NON-NLS-1$
		}
		patient.addContent(buildAdressElement(pat));
		eTiers.addContent(patient);
		
		Element guarantor = buildGuarantor(rnAdressat, actPatient); // 11110
		eTiers.addContent(guarantor);
		
		Element referrer = new Element("referrer", ns); // 11120 //$NON-NLS-1$
		Kontakt auftraggeber = actFall.getRequiredContact("Zuweiser");
		if (auftraggeber != null) {
			referrer.setAttribute(ATTR_EAN_PARTY, TarmedRequirements.getEAN(auftraggeber)); // auftraggeber.
			
			referrer.setAttribute("zsr", TarmedRequirements.getKSK(auftraggeber)); // auftraggeber. //$NON-NLS-1$
			
			referrer.addContent(buildAdressElement(auftraggeber));
			eTiers.addContent(referrer);
		}
		
		if (tiers.equals(TIERS_GARANT) && (TarmedRequirements.hasTCContract(actMandant))) {
			Element demand = new Element("demand", ns); //$NON-NLS-1$
			demand.setAttribute("tc_demand_id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			
			demand.setAttribute("tc_token", besr.createCodeline(XMLTool.moneyToXmlDouble(mDue) //$NON-NLS-1$
				.replaceFirst("[.,]", ""), tcCode)); //$NON-NLS-1$ //$NON-NLS-2$
			demand.setAttribute("insurance_demand_date", makeTarmedDatum(rn.getDatumRn())); //$NON-NLS-1$
			eTiers.addContent(demand);
		}
		
		invoice.addContent(eTiers);
		
		Element detail = new Element(ELEMENT_DETAIL, ns); // 15000
		
		detail.setAttribute(ATTR_DATE_BEGIN, makeTarmedDatum(ttFirst.toString(TimeTool.DATE_GER))); // 15002
		detail.setAttribute("date_end", makeTarmedDatum(ttLast.toString(TimeTool.DATE_GER))); // 15002 //$NON-NLS-1$
		
		detail.setAttribute("canton", actMandant.getInfoString(ta.KANTON)); // 15004 //$NON-NLS-1$
		detail.setAttribute("service_locality", "practice"); // 15021 //$NON-NLS-1$ //$NON-NLS-2$
		
		// 15030 ff weggelassen
		for (IDiagnose diagnose : diagnosen) {
			Element diagnosis = new Element("diagnosis", ns); // 15500 //$NON-NLS-1$
			/*
			 * String dgType=actMandant.getInfoString(ta.DIAGSYS); if(dgType.equals("")){
			 * dgType="by_contract"; }
			 */
			String diagnosisType = match_diag(diagnose.getCodeSystemName());
			diagnosis.setAttribute(ATTR_TYPE, diagnosisType); // 15510
			String code = diagnose.getCode();
			if (diagnosisType.equalsIgnoreCase(FREETEXT)) {
				code = "";
				diagnosis.setText(diagnose.getText());
			} else {
				if (code.length() > 12) {
					code = code.substring(0, 12);
				}
			}
			
			diagnosis.setAttribute(ATTR_CODE, code);
			detail.addContent(diagnosis);
		}
		
		String gesetz = TarmedRequirements.getGesetz(actFall);
		
		Element versicherung = new Element(gesetz.toLowerCase(), ns); // 16700
		versicherung.setAttribute("reason", match_type(actFall.getGrund())); //$NON-NLS-1$
		if (gesetz.equalsIgnoreCase("ivg")) { //$NON-NLS-1$
			String caseNumber = actFall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			if ((!caseNumber.matches("[0-9]{14}")) && // seit 1.1.2000 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{10}")) && // bis 31.12.1999 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{9}")) && // auch bis 31.12.1999 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{6}"))) { // Nummer für Abklärungsmassnahmen //$NON-NLS-1$
				/* die spinnen, die Bürokraten */
				if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)) {
					rn.reject(REJECTCODE.VALIDATION_ERROR, Messages.XMLExporter_IVCaseNumberInvalid);
				} else {
					caseNumber = "123456"; // sometimes it's better to cheat than to fight //$NON-NLS-1$
					// bureaucrazy
				}
			}
			versicherung.setAttribute(ATTR_CASE_ID, caseNumber);
			String ahv = TarmedRequirements.getAHV(pat).replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
			if (ahv.length() == 0) {
				ahv =
					actFall.getRequiredString(TarmedRequirements.SSN).replaceAll(
						"[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
			}
			boolean bAHVValid = ahv.matches("[0-9]{11}") || ahv.matches("[0-9]{13}"); //$NON-NLS-1$ //$NON-NLS-2$
			if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)
				&& (bAHVValid == false)) {
				rn.reject(REJECTCODE.VALIDATION_ERROR, Messages.XMLExporter_AHVInvalid);
			} else {
				versicherung.setAttribute("ssn", ahv); //$NON-NLS-1$
			}
			String nif =
				TarmedRequirements.getNIF(actMandant.getRechnungssteller()).replaceAll(
					"[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
			if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)
				&& (!nif.matches("[0-9]{1,7}"))) { //$NON-NLS-1$
				rn.reject(REJECTCODE.VALIDATION_ERROR, Messages.XMLExporter_NIFInvalid);
			} else {
				versicherung.setAttribute("nif", nif); //$NON-NLS-1$
			}
		} else if (gesetz.equalsIgnoreCase("uvg")) { //$NON-NLS-1$
			String casenumber = actFall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			if (StringTool.isNothing(casenumber)) {
				casenumber = actFall.getRequiredString(TarmedRequirements.ACCIDENT_NUMBER);
			}
			if (!StringTool.isNothing(casenumber)) {
				versicherung.setAttribute(ATTR_CASE_ID, casenumber);
			}
			String vnummer = actFall.getRequiredString(TarmedRequirements.INSURANCE_NUMBER);
			if (!StringTool.isNothing(vnummer)) {
				versicherung.setAttribute("patient_id", vnummer); //$NON-NLS-1$
			}
		} else {
			String vnummer = actFall.getRequiredString(TarmedRequirements.INSURANCE_NUMBER);
			if (StringTool.isNothing(vnummer)) {
				vnummer = actFall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			}
			if (StringTool.isNothing(vnummer)) {
				vnummer = pat.getId();
			}
			versicherung.setAttribute("patient_id", vnummer); // 16720 //$NON-NLS-1$
		}
		String casedate = actFall.getInfoString("Unfalldatum"); // 16740 //$NON-NLS-1$
		if (StringTool.isNothing(casedate)) {
			casedate = rn.getDatumVon();
		}
		versicherung.setAttribute("case_date", makeTarmedDatum(casedate)); //$NON-NLS-1$
		// versicherung.setAttribute("case_id",actFall.getFallNummer()); //
		// 16730
		setAttributeIfNotEmpty(versicherung, "contract_number", actFall //$NON-NLS-1$
			.getInfoString("Vertragsnummer")); // 16750 //$NON-NLS-1$
		detail.addContent(versicherung);
		
		detail.addContent(services); // 20000
		invoice.addContent(detail);
		if (rn.setBetrag(mTotal.roundTo5()) == false) {
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
	
	public String getDescription(){
		return Messages.XMLExporter_TarmedForTrustCenter;
	}
	
	public Element buildRechnungsstellerAdressElement(final Kontakt k){
		return buildAdressElement(k, false, true);
	}
	
	public Element buildAdressElement(final Kontakt k){
		return buildAdressElement(k, false, false);
	}
	
	public Element buildAdressElement(final Kontakt k, final boolean useAnschrift){
		return buildAdressElement(k, useAnschrift, false);
	}
	
	public Element buildAdressElement(final Kontakt k, final boolean useAnschrift,
		boolean checkAnrede){
		Element ret;
		boolean anredeOrganization = false;
		if (checkAnrede) {
			String anrede = (String) k.getInfoElement("Anrede");
			anredeOrganization = (anrede == null || anrede.isEmpty());
		}
		if (k.istPerson() == false || anredeOrganization) {
			ret = new Element("company", ns); //$NON-NLS-1$
			Element companyname = new Element("companyname", ns); //$NON-NLS-1$
			companyname.setText(StringTool.limitLength(k.get(Kontakt.FLD_NAME1), 35));
			ret.addContent(companyname);
			ret.addContent(buildPostalElement(k));
			ret.addContent(buildTelekomElement(k));
			Element onlineElement = buildOnlineElement(k);
			if (onlineElement != null) {
				ret.addContent(onlineElement);
			}
		} else {
			ret = new Element("person", ns); //$NON-NLS-1$
			Element familyname = new Element("familyname", ns); //$NON-NLS-1$
			Element givenname = new Element("givenname", ns); //$NON-NLS-1$
			
			String anschrift = k.get(Kontakt.FLD_ANSCHRIFT);
			if (!useAnschrift || StringTool.isNothing(anschrift)
				|| anschrift.equals(k.createStdAnschrift())) {
				setAttributeIfNotEmptyWithLimit(ret, "salutation", k.getInfoString("Anrede"), 35); //$NON-NLS-1$ //$NON-NLS-2$
				setAttributeIfNotEmptyWithLimit(ret, "title", k.get(Person.TITLE), 35); //$NON-NLS-1$
				familyname.setText(StringTool.limitLength(k.get(Kontakt.FLD_NAME1), 35));
				String gn = k.get(StringTool.limitLength(Kontakt.FLD_NAME2, 35));
				if (StringTool.isNothing(gn)) {
					gn = "Unbekannt"; // make validator happy //$NON-NLS-1$
				}
				givenname.setText(gn);
				ret.addContent(familyname);
				ret.addContent(givenname);
				ret.addContent(buildPostalElement(k));
			} else {
				Postanschrift postAnschrift = new Postanschrift(k);
				familyname.setText(StringTool.limitLength(postAnschrift.name, 35));
				givenname.setText(StringTool.limitLength(postAnschrift.vorname, 35));
				
				setAttributeIfNotEmptyWithLimit(ret, "salutation", postAnschrift.anrede, 35); //$NON-NLS-1$
				ret.addContent(familyname);
				ret.addContent(givenname);
				ret.addContent(buildPostalElement(postAnschrift));
			}
			
			ret.addContent(buildTelekomElement(k));
			Element onlineElement = buildOnlineElement(k);
			if (onlineElement != null) {
				ret.addContent(onlineElement);
			}
		}
		return ret;
	}
	
	public Element buildPostalElement(final Kontakt k){
		Element ret = new Element("postal", ns); //$NON-NLS-1$
		addElementIfExists(ret, "pobox", null, StringTool.limitLength(k.getInfoString("Postfach"), //$NON-NLS-1$ //$NON-NLS-2$
			35), null);
		addElementIfExists(ret,
			"street", null, StringTool.limitLength(k.get(Kontakt.FLD_STREET), 35), null); //$NON-NLS-1$
		Element zip =
			addElementIfExists(ret,
				"zip", null, StringTool.limitLength(k.get(Kontakt.FLD_ZIP), 9), "0000"); //$NON-NLS-1$ //$NON-NLS-2$
		setAttributeIfNotEmpty(zip,
			"countrycode", StringTool.limitLength(k.get(Kontakt.FLD_COUNTRY), 3)); //$NON-NLS-1$
		addElementIfExists(
			ret,
			"city", null, StringTool.limitLength(k.get(Kontakt.FLD_PLACE), 35), Messages.XMLExporter_Unknown); //$NON-NLS-1$
		return ret;
	}
	
	public Element buildPostalElement(final Postanschrift postanschrift){
		Element ret = new Element("postal", ns); //$NON-NLS-1$
		addElementIfExists(ret, "pobox", null, StringTool.limitLength(postanschrift.adresse2, 35), //$NON-NLS-1$
			null);
		addElementIfExists(ret, "street", null, StringTool.limitLength(postanschrift.adresse1, 35), //$NON-NLS-1$
			null);
		Element zip =
			addElementIfExists(ret, "zip", null, StringTool.limitLength(postanschrift.plz, 9), //$NON-NLS-1$
				"0000"); //$NON-NLS-1$
		setAttributeIfNotEmpty(zip, "countrycode", StringTool.limitLength(postanschrift.land, 3)); //$NON-NLS-1$
		addElementIfExists(ret, "city", null, StringTool.limitLength(postanschrift.ort, 35), //$NON-NLS-1$
			Messages.XMLExporter_unknown);
		return ret;
	}
	
	public Element buildOnlineElement(final Kontakt k){
		// Element ret = new Element("online", ns);
		// String email = StringTool.limitLength(k.get("E-Mail"), 70);
		// if (!email.matches(".+@.+")) {
		// email = "mail@invalid.invalid";
		// }
		// addElementIfExists(ret, "email", null, k.get("E-Mail"),
		// "mail@invalid.invalid");
		// addElementIfExists(ret, "url", null,
		// StringTool.limitLength(k.get("Website"), 100), null);
		// return ret;
		
		// Tony Schaller, 28.12.2008:
		// optimized code: online element is created when it contains real
		// content, only
		Element ret = null;
		
		// mail adresse
		String value = StringTool.limitLength(k.get(Kontakt.FLD_E_MAIL), 70);
		if (!value.equals(StringConstants.EMPTY)) {
			if (!value.matches(".+@.+")) { //$NON-NLS-1$
				value = "mail@invalid.invalid"; //$NON-NLS-1$
			}
			if (ret == null) {
				ret = new Element(ELEMENT_ONLINE, ns);
			}
			addElementIfExists(ret, ELEMENT_EMAIL, null, value, null);
		}
		
		// webseite
		value = StringTool.limitLength(k.get(Kontakt.FLD_WEBSITE), 100);
		if (!value.equals(StringConstants.EMPTY)) {
			if (ret == null) {
				ret = new Element(ELEMENT_ONLINE, ns);
				addElementIfExists(ret, ELEMENT_EMAIL, null, "mail@invalid.invalid", null); //$NON-NLS-1$
			}
			addElementIfExists(ret, "url", null, value, null); //$NON-NLS-1$
		}
		return ret;
	}
	
	public Element buildTelekomElement(final Kontakt k){
		Element ret = new Element("telecom", ns); //$NON-NLS-1$
		addElementIfExists(ret,
			"phone", null, StringTool.limitLength(k.get(Kontakt.FLD_PHONE1), 25), //$NON-NLS-1$
			"555-555 55 55"); //$NON-NLS-1$
		addElementIfExists(ret,
			"fax", null, StringTool.limitLength(k.get(Kontakt.FLD_FAX), 25), null); //$NON-NLS-1$
		return ret;
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
		if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, false)) {
			Source source = new JDOMSource(xmlDoc);
			String path =
				PlatformHelper.getBasePath("ch.elexis.arzttarife_ch") + File.separator + "rsc"; //$NON-NLS-1$ //$NON-NLS-2$
			List<String> errs =
				XMLTool.validateSchema(path + File.separator + "MDInvoiceRequest_400.xsd", source); //$NON-NLS-1$
			if (!errs.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String err : errs) {
					sb.append(err).append(StringConstants.LF);
				}
				rn.reject(RnStatus.REJECTCODE.VALIDATION_ERROR, sb.toString());
			}
		}
		
	}
	
	public static String makeTarmedDatum(final String datum){
		return new TimeTool(datum).toString(TimeTool.DATE_MYSQL) + "T00:00:00"; //$NON-NLS-1$
	}
	
	private Element addElementIfExists(final Element parent, final String name, final String attr,
		String val, final String defValue){
		if (StringTool.isNothing(val)) {
			val = defValue;
		}
		if (!StringTool.isNothing(val)) {
			Element ret = new Element(name, ns);
			if (attr == null) {
				ret.setText(val);
			} else {
				ret.setAttribute(attr, val);
			}
			parent.addContent(ret);
			return ret;
		}
		return null;
	}
	
	private void setAttributeWithDefault(final Element element, final String name, String value,
		final String def){
		if (element != null) {
			if (!StringTool.isNothing(name)) {
				if (StringTool.isNothing(value)) {
					value = def;
				}
				element.setAttribute(name, value);
			}
		}
	}
	
	private boolean setAttributeIfNotEmptyWithLimit(final Element element, final String name,
		String value, final int len){
		if (value.length() >= len) {
			value = value.substring(0, len - 1);
		}
		return setAttributeIfNotEmpty(element, name, value);
	}
	
	private boolean setAttributeIfNotEmpty(final Element element, final String name,
		final String value){
		if (element == null) {
			return false;
		}
		if (StringTool.isNothing(name)) {
			return false;
		}
		if (StringTool.isNothing(value)) {
			return false;
		}
		element.setAttribute(name, value);
		return true;
	}
	
	private String match_type(final String type){
		if (type == null) {
			return DISEASE;
		}
		if (type.equalsIgnoreCase(Fall.TYPE_DISEASE)) {
			return DISEASE;
		}
		if (type.equalsIgnoreCase(Fall.TYPE_ACCIDENT)) {
			return "accident"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(Fall.TYPE_MATERNITY)) {
			return "maternity"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(Fall.TYPE_PREVENTION)) {
			return "prevention"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(Fall.TYPE_BIRTHDEFECT)) {
			return BIRTHDEFECT;
		}
		return DISEASE;
	}
	
	private String match_diag(final String name){
		if (name.equalsIgnoreCase(FREETEXT)) {
			return FREETEXT;
		}
		if (name.equalsIgnoreCase("ICD-10")) { //$NON-NLS-1$
			return "ICD10"; //$NON-NLS-1$
		}
		if (name.equalsIgnoreCase("by contract")) { //$NON-NLS-1$
			return BY_CONTRACT;
		}
		if (name.equalsIgnoreCase(ICPC)) {
			return ICPC;
		}
		if (name.equalsIgnoreCase(BIRTHDEFECT)) {
			return BIRTHDEFECT;
		}
		return BY_CONTRACT;
	}
	
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
	
	protected String getRole(final Fall fall){
		return "production"; //$NON-NLS-1$
	}
	
	protected Element buildGuarantor(Kontakt garant, Kontakt patient){
		// Patient wird im override des MediPort Plugins verwendet
		// Hinweis:
		// XML Standard:
		// http://www.forum-datenaustausch.ch/mdinvoicerequest_xml4.00_v1.2_d.pdf
		// Dort steht beim Feld 11310: Gesetzlicher Vertreter des Patienten.
		Element guarantor = new Element("guarantor", ns); //$NON-NLS-1$
		guarantor.addContent(buildAdressElement(garant));
		return guarantor;
	}
	
	protected String getSenderEAN(Mandant actMandant){
		return TarmedRequirements.getEAN(actMandant);
	}
	
	private class Postanschrift {
		private String anrede = StringConstants.EMPTY;
		private String name = StringConstants.EMPTY;
		private String vorname = StringConstants.EMPTY;
		private String adresse1 = StringConstants.EMPTY;
		private String adresse2 = StringConstants.EMPTY;
		private String plz = StringConstants.EMPTY;
		private String ort = StringConstants.EMPTY;
		private String land = StringConstants.EMPTY;
		
		public Postanschrift(final Kontakt k){
			super();
			init(k);
		}
		
		private void init(final Kontakt k){
			String postAnschrift = k.getPostAnschrift(true);
			
			// Zeilen lesen
			StringTokenizer tokenizer = new StringTokenizer(postAnschrift, StringConstants.LF);
			List<String> zeileList = new Vector<String>();
			while (tokenizer.hasMoreElements()) {
				zeileList.add(tokenizer.nextToken());
			}
			// Zeilen interpretieren (so gut es geht)
			String plzOrt = ""; //$NON-NLS-1$
			String nameVorname = ""; //$NON-NLS-1$
			final int len = zeileList.size();
			switch (len) {
			case 0: // Kann gar nicht sein, aber man weiss ja nie!
				throw new IllegalArgumentException(Messages.XMLExporter_NoPostal);
			case 1: // Nur Name vorname
				nameVorname = zeileList.get(0);
				break;
			case 2: // NameVorname, Ortsangaben
				nameVorname = zeileList.get(0);
				plzOrt = zeileList.get(1);
				break;
			case 3: // NameVorname, Adr1, Ortsangaben ODER Anrede, NameVorname,
				// Ortsangaben
				if (zeileList.get(0).indexOf(" ") < 0) { //$NON-NLS-1$
					// Erste Zeile Anrede
					anrede = zeileList.get(0);
					nameVorname = zeileList.get(1);
					plzOrt = zeileList.get(2);
				} else {
					// Erste Zeile NameVorname
					nameVorname = zeileList.get(0);
					adresse1 = zeileList.get(1);
					plzOrt = zeileList.get(2);
				}
				break;
			case 4: // NameVorname, Adr1, Adr2, Ortsangaben ODER Anrede,
				// NameVorname, Adr1,
				// Ortsangaben
				if (zeileList.get(0).indexOf(" ") < 0) { //$NON-NLS-1$
					// Erste Zeile Anrede
					anrede = zeileList.get(0);
					nameVorname = zeileList.get(1);
					adresse1 = zeileList.get(2);
					plzOrt = zeileList.get(3);
				} else {
					// Erste Zeile NameVorname
					nameVorname = zeileList.get(0);
					adresse1 = zeileList.get(1);
					adresse2 = zeileList.get(2);
					plzOrt = zeileList.get(3);
				}
				break;
			default:
				if (len > 4) { // Anrede, NameVorname, Adr1, Adr2, Ortsangaben
					anrede = zeileList.get(0);
					nameVorname = zeileList.get(1);
					adresse1 = zeileList.get(2);
					adresse2 = zeileList.get(3);
					plzOrt = zeileList.get(4);
				}
				break;
			}
			
			// NameVorname aufteilen. Z.B. Von Allmen Christoph
			if (!StringTool.isNothing(nameVorname)) {
				nameVorname = nameVorname.trim();
				int index = nameVorname.lastIndexOf(" "); // Z.B. Von Allmen Christoph //$NON-NLS-1$
				if (index > 0) {
					name = nameVorname.substring(0, index);
					vorname = nameVorname.substring(index + 1);
				} else {
					name = nameVorname;
				}
			}
			
			// plzOrt parsen. Z.B. CH-3600 Lenzburg
			land = k.get(Kontakt.FLD_COUNTRY);
			if (plzOrt.length() > 3 && plzOrt.substring(0, 3).indexOf("-") > 0) { //$NON-NLS-1$
				// Land exists
				int index = plzOrt.indexOf("-"); //$NON-NLS-1$
				land = plzOrt.substring(0, index);
				plzOrt = plzOrt.substring(index + 1);
			}
			plz = k.get(Kontakt.FLD_ZIP);
			if (plzOrt.indexOf(" ") > 0) { //$NON-NLS-1$
				// Read zip code
				int index = plzOrt.indexOf(StringConstants.SPACE);
				plz = plzOrt.substring(0, index);
				plzOrt = plzOrt.substring(index + 1);
			}
			ort = plzOrt;
		}
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
	 * Set the correct VAT Attribute based on the Verrechent and the info if the Rechnungssteller
	 * has to pay VAT.
	 * 
	 * @param verrechnet
	 * @param amount
	 * @param el
	 */
	private void setVatAttribute(Verrechnet verrechnet, Money amount, Element el, VatRateSum vatsum){
		double value = 0.0;
		
		String vatScale = verrechnet.getDetail(Verrechnet.VATSCALE);
		if (vatScale != null && vatScale.length() > 0)
			value = Double.parseDouble(vatScale);
		
		el.setAttribute(ATTR_VAT_RATE, Double.toString(value)); //$NON-NLS-1$
		
		vatsum.add(value, amount.doubleValue());
	}
	
	/**
	 * Determine the EAN of the responsible Kontakt for a Konsultation. The search for thee right
	 * contact is in the following order.<br\>
	 * 1. configured via ResponsibleComposite on RechnungsPref preference page for the Mandant of
	 * the consultation<br\>
	 * 2. Rechnungssteller of the Mandant of the consultation if not an organization<br\>
	 * 3. the Mandant of the consultation<\br>
	 * 
	 * @param kons
	 * @return
	 */
	private String getResponsibleEAN(Konsultation kons){
		Kontakt responsibleKontakt = null;
		
		String responsibleId =
			(String) kons.getMandant().getInfoElement(TarmedRequirements.RESPONSIBLE_INFO_KEY);
		if (responsibleId != null && !responsibleId.isEmpty()) {
			responsibleKontakt = Mandant.load(responsibleId);
		} else {
			Rechnungssteller rechnungssteller = kons.getMandant().getRechnungssteller();
			String anrede = rechnungssteller.getInfoString("Anrede");
			// only way to determine if rechnungssteller is a organization is testing empty anrede
			if (anrede != null && !anrede.isEmpty()) {
				responsibleKontakt = rechnungssteller;
			} else {
				responsibleKontakt = kons.getMandant();
			}
		}
		return TarmedRequirements.getEAN(responsibleKontakt);
	}
}
