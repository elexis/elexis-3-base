package ch.elexis.pdfBills.privat;

import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TG_M0;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TG_M1;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TG_M2;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TG_M3;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TP_M0;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TP_M1;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TP_M2;
import static ch.elexis.pdfBills.RnOutputter.CFG_MSGTEXT_TP_M3;

import java.io.BufferedOutputStream;

/**
 *
 */

/**
 * @author sramakri
 *
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.TarmedRechnung.XMLExporterUtil;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.services.ICoverageService.Tiers;
import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.services.holder.InvoiceServiceHolder;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.data.Fall;
import ch.elexis.data.Mandant;
import ch.elexis.data.Rechnung;
import ch.elexis.ebanking.qr.QRBillDataBuilder;
import ch.elexis.ebanking.qr.QRBillDataException;
import ch.elexis.ebanking.qr.QRBillDataException.SourceType;
import ch.elexis.ebanking.qr.QRBillImage;
import ch.elexis.pdfBills.OutputterUtil;
import ch.elexis.pdfBills.PreferencePage;
import ch.elexis.pdfBills.QrRnOutputter;
import ch.elexis.pdfBills.RnOutputter;
import ch.elexis.pdfBills.print.PrintProcess;
import ch.elexis.pdfBills.print.ScriptInitializer;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.XMLTool;

public class ElexisPDFGenerator {
	public String leftMargin;
	public String rightMargin;
	public String topMargin;
	public String bottomMargin;
	public String besrMarginVertical;
	public String besrMarginHorizontal;

	private String billXmlFile;
	private Document domDocument;
	private String billNr;
	private String billVersion;

	private String eanList;
	private String vatList;

	private Money mReminders;
	private Money mTotal;
	private Money mDue;
	private InvoiceState invoiceState;

	private List<File> printed;
	private Rechnung rechnung;

	private boolean print;

	private enum XsltType {
		RECLAIM, PATBILL, PATBILL_M1, PATBILL_M2, PATBILL_M3
	}

	public ElexisPDFGenerator(String billXmlFile, String nr) {
		this(billXmlFile, nr, InvoiceState.OPEN);
	}

	public ElexisPDFGenerator(String billXmlFile, String nr, InvoiceState invoiceState) {
		this.print = true;
		this.billXmlFile = billXmlFile;
		this.billNr = nr;
		this.invoiceState = invoiceState;
		domDocument = readDom();
		this.billVersion = getXmlVersion();
		this.eanList = getEanList();
		this.vatList = getVatList();
		initializeMarginSettings();
		// check if connection available, or running in headless test
		rechnung = null;
		if (Rechnung.getDefaultConnection() != null) {
			rechnung = Rechnung.getFromNr(nr);
		}
		// since we can put reminders on the tarmed 4.0 bill, we have to provide the
		// information via parameters for the esr page (section2.xsl)
		if (rechnung != null && rechnung.exists()) {
			this.mReminders = rechnung.getRemindersBetrag();
			this.mTotal = rechnung.getBetrag().addMoney(mReminders);
			this.mDue = new Money(mTotal.doubleValue() - rechnung.getAnzahlung().doubleValue());
		} else {
			this.mReminders = new Money();
			this.mTotal = new Money();
			this.mDue = new Money();
		}
	}

	private void initializeMarginSettings() {
		bottomMargin = PreferencePage
				.getSetting(RnOutputter.CFG_ROOT + getMarginBillVersion() + "/" + RnOutputter.CFG_MARGINBOTTOM) + "cm";
		leftMargin = PreferencePage
				.getSetting(RnOutputter.CFG_ROOT + getMarginBillVersion() + "/" + RnOutputter.CFG_MARGINLEFT) + "cm";
		rightMargin = PreferencePage
				.getSetting(RnOutputter.CFG_ROOT + getMarginBillVersion() + "/" + RnOutputter.CFG_MARGINRIGHT) + "cm";
		topMargin = PreferencePage
				.getSetting(RnOutputter.CFG_ROOT + getMarginBillVersion() + "/" + RnOutputter.CFG_MARGINTOP) + "cm";
		besrMarginVertical = PreferencePage.getSetting(
				RnOutputter.CFG_ROOT + getMarginBillVersion() + "/" + RnOutputter.CFG_BESR_MARGIN_VERTICAL) + "cm";
		besrMarginHorizontal = PreferencePage.getSetting(
				RnOutputter.CFG_ROOT + getMarginBillVersion() + "/" + RnOutputter.CFG_BESR_MARGIN_HORIZONTAL) + "cm";
	}

	public String getBillVersion() {
		return billVersion;
	}

	private String getMarginBillVersion() {
		if ("4.5".equals(billVersion)) {
			return "4.4";
		}
		return billVersion;
	}

	private org.w3c.dom.Document readDom() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			return dbf.newDocumentBuilder().parse(new File(billXmlFile));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error parsing XML", e);
		}
		return null;
	}

	private void generatePdf(File styleSheet, File output) {
		generatePdf(styleSheet, output, false);
	}

	private void generatePdf(File styleSheet, File output, boolean withQr) {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(output));
				FileInputStream inputStream = new FileInputStream(billXmlFile);
				FileInputStream xsltStream = new FileInputStream(styleSheet)) {
			BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
			ServiceReference<IFormattedOutputFactory> fopFactoryRef = bundleContext
					.getServiceReference(IFormattedOutputFactory.class);
			if (fopFactoryRef != null) {
				try {
					IFormattedOutputFactory fopFactory = bundleContext.getService(fopFactoryRef);

					IFormattedOutput foOutputt = fopFactory.getFormattedOutputImplementation(ObjectType.XMLSTREAM,
							OutputType.PDF);

					Map<String, String> parameters = new HashMap<>();
					parameters.put("versionParam", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$
					parameters.put("leftMargin", leftMargin); //$NON-NLS-1$
					parameters.put("rightMargin", rightMargin); //$NON-NLS-1$
					parameters.put("topMargin", topMargin); //$NON-NLS-1$
					parameters.put("bottomMargin", bottomMargin); //$NON-NLS-1$
					parameters.put("besrMarginVertical", besrMarginVertical); //$NON-NLS-1$
					parameters.put("besrMarginHorizontal", besrMarginHorizontal); //$NON-NLS-1$
					parameters.put("headerLine1", getConfigValue(RnOutputter.CFG_ESR_HEADER_1, StringUtils.SPACE)); //$NON-NLS-1$
					parameters.put("headerLine2", getConfigValue(RnOutputter.CFG_ESR_HEADER_2, StringUtils.SPACE)); //$NON-NLS-1$
					parameters.put("messageText", getMessagePDFText(invoiceState));// $NON-NLS-1$
					parameters.put("eanList", eanList); //$NON-NLS-1$
					parameters.put("vatList", vatList); //$NON-NLS-1$
					parameters.put("amountTotal", XMLTool.moneyToXmlDouble(mTotal)); //$NON-NLS-1$
					parameters.put("amountDue", XMLTool.moneyToXmlDouble(mDue)); //$NON-NLS-1$
					if (withQr) {
						parameters.put("qrJpeg", getEncodedQr(rechnung));
					}
					if (CoreHub.localCfg.get(RnOutputter.CFG_PRINT_USEGUARANTORPOSTAL, false)) {
						parameters.put("guarantorPostal", getGuarantorPostal(rechnung));
					} else {
						parameters.put("guarantorPostal", StringUtils.EMPTY);
					}
					if (CoreHub.localCfg.get(RnOutputter.CFG_ESR_COUVERT_LEFT, false)) {
						parameters.put("couvertLeft", "true");
					} else {
						parameters.put("couvertLeft", StringUtils.EMPTY);
					}
					Optional<IInvoice> invoice = getInvoice();
					if (invoice.isPresent()) {
						parameters.put("billerLine", getBillerLine(invoice.get()));
						parameters.put("guarantorLine", getGuarantorLine(invoice.get()));
						parameters.put("creditorLine", getCreditorLine(invoice.get()));
						parameters.put("insuranceLine", getInsuranceLine(invoice.get()));
					} else {
						parameters.put("billerLine", StringUtils.EMPTY);
						parameters.put("guarantorLine", StringUtils.EMPTY);
						parameters.put("creditorLine", StringUtils.EMPTY);
						parameters.put("insuranceLine", StringUtils.EMPTY);
					}
					if (mReminders != null && !mReminders.isZero()) {
						parameters.put("amountReminders", XMLTool.moneyToXmlDouble(mReminders));
					}

					foOutputt.transform(inputStream, xsltStream, out, parameters, new BundleURIResolver());
				} catch (IllegalStateException e) {
					ExHandler.handle(e);
				}
				bundleContext.ungetService(fopFactoryRef);
			} else {
				throw new IllegalStateException("No IFormattedOutput implementation available");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error outputting bill", e);
			throw new IllegalStateException("Error outputting bill", e);
		}
	}

	private String getMessagePDFText(final InvoiceState invoiceState) {
		String key = "";
		String invStateTxt = rechnung.getInvoiceState().toString();
		ch.elexis.data.Fall.Tiers tiers = rechnung.getFall().getTiersType();

		switch (invoiceState) {
		case UNKNOWN:
		case IN_EXECUTION:
		case STOP_LEGAL_PROCEEDING:
		case OWING:
		case PARTIAL_LOSS:
		case TOTAL_LOSS:
		case NOT_BILLED:
		case ONGOING:
		case TO_PRINT:
		case NOT_FROM_YOU:
		case NOT_FROM_TODAY:
		case FROM_TODAY:
		case EXCESSIVE_PAYMENT:
		case REJECTED:
		case BILLED:
		case PARTIAL_PAYMENT:
		case PAID:
		case OPEN_AND_PRINTED:
		case OPEN:
			key = (Fall.Tiers.GARANT == tiers) ? CFG_MSGTEXT_TG_M0 : CFG_MSGTEXT_TP_M0;
			invStateTxt = Messages.BillingDefaultMsg;
			break;
		case DEMAND_NOTE_1_PRINTED:
		case DEMAND_NOTE_1:
			key = (Fall.Tiers.GARANT == tiers) ? CFG_MSGTEXT_TG_M1 : CFG_MSGTEXT_TP_M1;
			invStateTxt = Messages.BillingDefaultMsg_M1;
			break;
		case DEMAND_NOTE_2_PRINTED:
		case DEMAND_NOTE_2:
			key = (Fall.Tiers.GARANT == tiers) ? CFG_MSGTEXT_TG_M2 : CFG_MSGTEXT_TP_M2;
			invStateTxt = Messages.BillingDefaultMsg_M2;
			break;
		case DEMAND_NOTE_3_PRINTED:
		case DEMAND_NOTE_3:
			key = (Fall.Tiers.GARANT == tiers) ? CFG_MSGTEXT_TG_M3 : CFG_MSGTEXT_TP_M3;
			invStateTxt = Messages.BillingDefaultMsg_M3;
			break;
		default:
			LoggerFactory.getLogger(getClass()).error("unknown state: " + invoiceState.toString());
			break;
		}

		return CoreHub.globalCfg.get(key, invStateTxt);

	}

	private String getInsuranceLine(IInvoice invoice) {
		IContact costBearer = invoice.getCoverage().getCostBearer();
		if (costBearer == null) {
			costBearer = invoice.getCoverage().getPatient();
		}
		String kEAN = TarmedRequirements.getEAN(costBearer);
		if (StringUtils.isNotBlank(kEAN)) {
			return getContactLine(costBearer);
		}
		return null;
	}

	private String getCreditorLine(IInvoice invoice) {
		IContact creditor = invoice.getMandator().getBiller();
		// update creditor if configured
		if (StringUtils.isNotBlank((String) creditor.getExtInfo(TarmedACL.getInstance().RNACCOUNTOWNER))) {
			Optional<IContact> loadedCreditor = CoreModelServiceHolder.get()
					.load((String) creditor.getExtInfo(TarmedACL.getInstance().RNACCOUNTOWNER), IContact.class);
			if (loadedCreditor.isPresent()) {
				creditor = loadedCreditor.get();
			}
		}
		return getContactLine(creditor);
	}

	private String getGuarantorLine(IInvoice invoice) {
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact guarantor = XMLExporterUtil.getGuarantor(tiersType.getShortName(), invoice.getCoverage().getPatient(),
				invoice.getCoverage());
		if (guarantor != null) {
			return getContactLine(guarantor);
		}
		return null;
	}

	private String getBillerLine(IInvoice invoice) {
		return getContactLine(invoice.getMandator().getBiller());
	}

	private String getContactLine(IContact contact) {
		if (contact != null) {
			if (contact.isOrganization()) {
				return contact.getDescription1();
			} else if (contact.isPerson()) {
				IPerson person = CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).get();
				StringBuilder ret = new StringBuilder();
				if (StringUtils.isNotBlank(person.getTitel())) {
					ret.append(StringUtils.SPACE).append(person.getTitel());
				}
				ret.append(StringUtils.SPACE).append(person.getDescription2());
				ret.append(StringUtils.SPACE).append(person.getDescription1());
				return ret.toString();
			}
		}
		return null;
	}

	private Optional<IInvoice> getInvoice() {
		if (rechnung != null) {
			return NoPoUtil.loadAsIdentifiable(rechnung, IInvoice.class);
		}
		return Optional.empty();
	}

	private String getGuarantorPostal(Rechnung rechnung) {
		if (rechnung != null) {
			Optional<IInvoice> invoice = CoreModelServiceHolder.get().load(rechnung.getId(), IInvoice.class);
			if (invoice.isPresent()) {
				IContact guarantor = invoice.get().getCoverage().getGuarantor();
				if (guarantor != null) {
					return guarantor.getPostalAddress();
				}
			}
		}
		return StringUtils.EMPTY;
	}

	private String getEncodedQr(Rechnung rechnung) {
		if (rechnung != null) {
			Optional<IInvoice> invoice = CoreModelServiceHolder.get().load(rechnung.getId(), IInvoice.class);
			if (invoice.isPresent()) {
				IContact biller = invoice.get().getMandator().getBiller();
				ESR esr = new ESR((String) biller.getExtInfo(TarmedACL.getInstance().ESRNUMBER),
						(String) biller.getExtInfo(TarmedACL.getInstance().ESRSUB),
						InvoiceServiceHolder.get().getCombinedId(invoice.get()), ESR.ESR27);
				IContact creditor = invoice.get().getMandator().getBiller();
				// update creditor if configured
				if (StringUtils.isNotBlank((String) creditor.getExtInfo(TarmedACL.getInstance().RNACCOUNTOWNER))) {
					Optional<IContact> loadedCreditor = CoreModelServiceHolder.get()
							.load((String) creditor.getExtInfo(TarmedACL.getInstance().RNACCOUNTOWNER), IContact.class);
					if (loadedCreditor.isPresent()) {
						creditor = loadedCreditor.get();
					}
				}
				String additionalInformation = (String) biller.getExtInfo(TarmedACL.getInstance().RNINFORMATION);

				QRBillDataBuilder builder = new QRBillDataBuilder(reloadAsPersonOrOrganization(creditor),
						invoice.get().getOpenAmount(), "CHF", reloadAsPersonOrOrganization(getDebitor(invoice.get())))
								.reference(esr.makeRefNr(false)).unstructuredRemark(additionalInformation);
				try {
					Optional<String> encodedImage = new QRBillImage(builder.build()).getEncodedImage();
					if (encodedImage.isPresent()) {
						return encodedImage.get();
					}
				} catch (QRBillDataException e) {
					LoggerFactory.getLogger(getClass()).error("Error creating qr code", e);
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "QR code Fehler",
							getErrorMessage(e, invoice));
				}
			}
		}
		return StringUtils.EMPTY;
	}

	private String getErrorMessage(QRBillDataException e, Optional<IInvoice> invoice) {
		StringBuilder sb = new StringBuilder();
		sb.append("Fehler beim Erstellen des QR codes.\n");
		if (e.getSourceType() == SourceType.CREDITOR) {
			sb.append("Problem mit der Rechnungssteller Information für ["
					+ invoice.get().getMandator().getBiller().getLabel() + "].\n");
			if (e.getContact() != null) {
				sb.append("Bitte die Addresse auf Vollständigkeit überprüfen.");
			}
		}
		if (e.getSourceType() == SourceType.DEBITOR) {
			if (invoice.get().getCoverage().getCostBearer() != null) {
				sb.append("Problem mit der Kostenträger Information für ["
						+ invoice.get().getCoverage().getCostBearer().getLabel() + "].");
				if (e.getContact() != null) {
					sb.append("Bitte die Addresse auf Vollständigkeit überprüfen.");
				}
			} else {
				sb.append("Problem mit der Kostenträger Information für [" + invoice.get().getCoverage().getLabel()
						+ "] kein Kostenträger.");
			}
		}
		if (e.getSourceType() == SourceType.AMOUNT) {
			sb.append("Problem mit dem Rechnungsbetrag.");
		}
		if (e.getSourceType() == SourceType.REMARK) {
			sb.append("Problem mit der Rechnungsbemerkung.");
		}
		return sb.toString();
	}

	private IContact reloadAsPersonOrOrganization(IContact contact) {
		if (contact.isPerson() && !(contact instanceof IPerson)) {
			return CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).get();
		} else if (contact.isOrganization() && !(contact instanceof IOrganization)) {
			return CoreModelServiceHolder.get().load(contact.getId(), IOrganization.class).get();
		} else {
			return contact;
		}
	}

	/**
	 * Get the configured value prefer global mandant specific value to local value.
	 *
	 * @param configId
	 * @return
	 */
	private String getConfigValue(String configId, String defaultValue) {
		if (rechnung != null) {
			Mandant mandant = rechnung.getMandant();
			String mandantValue = ConfigServiceHolder.getGlobal(configId + "/" + mandant.getId(), null);
			if (StringUtils.isNotBlank(mandantValue)) {
				return mandantValue;
			}
		}
		return CoreHub.localCfg.get(configId, defaultValue);
	}

	public void printBill(File rsc) {
		printed = new ArrayList<>();
		if (CoreHub.localCfg.get(PrivatQrRnOutputter.CFG_ROOT_PRIVAT + RnOutputter.CFG_PRINT_BESR, true)) {
			File pdf = new File(
					OutputterUtil.getPdfOutputDir(PrivatQrRnOutputter.CFG_ROOT_PRIVAT) + File.separator + billNr
							+ "_esr.pdf");
			generatePatBill(rsc, pdf);
			printPdf(pdf, true);
			printed.add(pdf);
		}
		if (CoreHub.localCfg.get(PrivatQrRnOutputter.CFG_ROOT_PRIVAT + RnOutputter.CFG_PRINT_RF, true)) {
			File pdf = new File(
					OutputterUtil.getPdfOutputDir(PrivatQrRnOutputter.CFG_ROOT_PRIVAT) + File.separator + billNr
							+ "_rf.pdf");
			generatePdf(getXsltForBill(rsc, XsltType.RECLAIM), pdf);
			printPdf(pdf, false);
			printed.add(pdf);
		}
	}

	public void printQrBill(File rsc) {
		printed = new ArrayList<>();
		if (CoreHub.localCfg.get(PrivatQrRnOutputter.CFG_ROOT_PRIVAT + QrRnOutputter.CFG_PRINT_BESR, true)) {
			File pdf = new File(
					OutputterUtil.getPdfOutputDir(PrivatQrRnOutputter.CFG_ROOT_PRIVAT) + File.separator + billNr
							+ "_esr.pdf");
			generateQrPatBill(rsc, pdf);
			printPdf(pdf, false);
			printed.add(pdf);
		}
		if (CoreHub.localCfg.get(PrivatQrRnOutputter.CFG_ROOT_PRIVAT + QrRnOutputter.CFG_PRINT_RF, true)) {
			File pdf = new File(
					OutputterUtil.getPdfOutputDir(PrivatQrRnOutputter.CFG_ROOT_PRIVAT) + File.separator + billNr
							+ "_rf.pdf");
			generatePdf(getXsltForBill(rsc, XsltType.RECLAIM), pdf);
			printPdf(pdf, false);
			printed.add(pdf);
		}
	}

	private void generateQrPatBill(File rsc, File pdf) {
		if (invoiceState == InvoiceState.DEMAND_NOTE_1 || invoiceState == InvoiceState.DEMAND_NOTE_1_PRINTED) {
			generatePdf(getQrXsltForBill(rsc, XsltType.PATBILL_M1), pdf, true);
		} else if (invoiceState == InvoiceState.DEMAND_NOTE_2 || invoiceState == InvoiceState.DEMAND_NOTE_2_PRINTED) {
			generatePdf(getQrXsltForBill(rsc, XsltType.PATBILL_M2), pdf, true);
		} else if (invoiceState == InvoiceState.DEMAND_NOTE_3 || invoiceState == InvoiceState.DEMAND_NOTE_3_PRINTED) {
			generatePdf(getQrXsltForBill(rsc, XsltType.PATBILL_M3), pdf, true);
		} else {
			generatePdf(getQrXsltForBill(rsc, XsltType.PATBILL), pdf, true);
		}
	}

	private void generatePatBill(File rsc, File pdf) {
		if (invoiceState == InvoiceState.DEMAND_NOTE_1 || invoiceState == InvoiceState.DEMAND_NOTE_1_PRINTED) {
			generatePdf(getXsltForBill(rsc, XsltType.PATBILL_M1), pdf);
		} else if (invoiceState == InvoiceState.DEMAND_NOTE_2 || invoiceState == InvoiceState.DEMAND_NOTE_2_PRINTED) {
			generatePdf(getXsltForBill(rsc, XsltType.PATBILL_M2), pdf);
		} else if (invoiceState == InvoiceState.DEMAND_NOTE_3 || invoiceState == InvoiceState.DEMAND_NOTE_3_PRINTED) {
			generatePdf(getXsltForBill(rsc, XsltType.PATBILL_M3), pdf);
		} else {
			generatePdf(getXsltForBill(rsc, XsltType.PATBILL), pdf);
		}
	}

	private boolean printPdf(File pdf, boolean useEsrPrinter) {
		if (print && isPrintingConfigured()) {
			// check if script initialization for windows should be performed
			if (CoreUtil.isWindows() && CoreHub.localCfg.get(RnOutputter.CFG_PRINT_USE_SCRIPT, false)
					&& !isScriptWinInitialized()) {
				initializeScriptWin();
			}
			String toPrinter = CoreHub.localCfg.get(RnOutputter.CFG_PRINT_PRINTER, StringUtils.EMPTY);
			String toTray = CoreHub.localCfg.get(RnOutputter.CFG_PRINT_TRAY, StringUtils.EMPTY);
			if (useEsrPrinter) {
				toPrinter = CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_PRINTER, StringUtils.EMPTY);
				toTray = CoreHub.localCfg.get(RnOutputter.CFG_ESR_PRINT_TRAY, StringUtils.EMPTY);
			}
			String printCommand = CoreHub.localCfg.get(RnOutputter.CFG_PRINT_COMMAND, StringUtils.EMPTY);
			if (printCommand != null) {
				PrintProcess process = new PrintProcess(printCommand);
				process.setPrinter(toPrinter);
				process.setTray(toTray);
				process.setFilename(pdf.getAbsolutePath());
				return process.execute();
			}
		}
		return true;
	}

	private boolean isPrintingConfigured() {
		return CoreHub.localCfg.get(RnOutputter.CFG_PRINT_DIRECT, false)
				&& !CoreHub.localCfg.get(RnOutputter.CFG_PRINT_COMMAND, StringUtils.EMPTY).isEmpty();
	}

	private boolean isScriptWinInitialized() {
		ScriptInitializer initializer = new ScriptInitializer("/rsc/script/win/SumatraPDF.exe");
		if (!initializer.existsInScriptFolder()) {
			return false;
		}
		if (!initializer.matchingFileSize()) {
			return false;
		}
		return true;
	}

	private void initializeScriptWin() {
		ScriptInitializer initializer = new ScriptInitializer("/rsc/script/win/SumatraPDF.exe");
		if (initializer.existsInScriptFolder()) {
			if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Script existiert bereits", "Script ["
					+ initializer.getFilename() + "] existiert bereits, soll die Datei überschrieben werden?")) {
				initializer.init();
			}
		} else {
			initializer.init();
		}
	}

	protected boolean isTierGarant() {
		if ("4.4".equals(billVersion)) {
			try {
				XPath xPath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xPath.compile("/request/payload/body/tiers_garant");
				Object result = expr.evaluate(domDocument, XPathConstants.NODESET);
				NodeList nodes = (NodeList) result;
				return nodes.getLength() > 0;
			} catch (XPathExpressionException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting bill type", e);
			}
		}
		// default is garant
		return true;
	}

	private IContact getDebitor(IInvoice invoice) {
		String tiers = XMLExporter.TIERS_PAYANT;
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		if (Tiers.GARANT == tiersType) {
			tiers = XMLExporter.TIERS_GARANT;
		}
		IContact ret = XMLExporterUtil.getGuarantor(tiers, invoice.getCoverage().getPatient(), invoice.getCoverage());
		if (tiersType == Tiers.PAYANT) {
			IContact costBearer = invoice.getCoverage().getCostBearer();
			if (costBearer != null && costBearer.isOrganization()) {
				ret = costBearer;
			}
		}
		return ret;
	}

	private String getEanList() {
		if ("4.4".equals(billVersion)) {
			HashSet<String> eanSet = new HashSet<>();
			try {
				XPath xPath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xPath.compile("/request/payload/body/services");
				Object result = expr.evaluate(domDocument, XPathConstants.NODESET);
				NodeList servicesElements = (NodeList) result;
				for (int i = 0; i < servicesElements.getLength(); i++) {
					Node serviceElement = servicesElements.item(i);
					NodeList childNodes = serviceElement.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node child = childNodes.item(j);
						if (child instanceof Element) {
							Element element = (Element) child;
							eanSet.add(element.getAttribute("responsible_id"));
							eanSet.add(element.getAttribute("provider_id"));
						}
					}
				}
				StringBuilder eanList = new StringBuilder();
				String[] eanArray = eanSet.toArray(new String[eanSet.size()]);
				for (int i = 0; i < eanArray.length; i++) {
					if (i > 0) {
						eanList.append(StringUtils.SPACE);
					}
					eanList.append(i + 1).append("/").append(eanArray[i]);
				}
				return eanList.toString();
			} catch (XPathExpressionException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting bill type", e);
			}
		} else if ("4.5".equals(billVersion)) {
			HashSet<String> eanSet = new HashSet<>();
			try {
				XPath xPath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xPath.compile("/request/payload/body/services");
				Object result = expr.evaluate(domDocument, XPathConstants.NODESET);
				NodeList servicesElements = (NodeList) result;
				for (int i = 0; i < servicesElements.getLength(); i++) {
					Node serviceElement = servicesElements.item(i);
					NodeList childNodes = serviceElement.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node child = childNodes.item(j);
						if (child instanceof Element) {
							Element element = (Element) child;
							eanSet.add(element.getAttribute("responsible_id"));
							eanSet.add(element.getAttribute("provider_id"));
						}
					}
				}
				StringBuilder eanList = new StringBuilder();
				String[] eanArray = eanSet.toArray(new String[eanSet.size()]);
				for (int i = 0; i < eanArray.length; i++) {
					if (i > 0) {
						eanList.append(StringUtils.SPACE);
					}
					eanList.append(i + 1).append("/").append(eanArray[i]);
				}
				return eanList.toString();
			} catch (XPathExpressionException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting bill type", e);
			}
		}
		return StringUtils.EMPTY;
	}

	private String getVatList() {
		if ("4.4".equals(billVersion)) {
			HashSet<String> vatrateSet = new HashSet<>();
			try {
				XPath xPath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xPath.compile("/request/payload/body/balance/vat/vat_rate");
				Object result = expr.evaluate(domDocument, XPathConstants.NODESET);
				NodeList vatrateElements = (NodeList) result;
				for (int i = 0; i < vatrateElements.getLength(); i++) {
					Node vatrateElement = vatrateElements.item(i);
					vatrateSet.add(((Element) vatrateElement).getAttribute("vat_rate"));
				}
				String[] vatRateArray = getVatRateArray(vatrateSet);

				StringBuilder vatrateList = new StringBuilder();
				for (int i = 0; i < vatRateArray.length; i++) {
					if (i > 0) {
						vatrateList.append(StringUtils.SPACE);
					}
					vatrateList.append(i).append("/").append(vatRateArray[i]);
				}
				return vatrateList.toString();
			} catch (XPathExpressionException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting vat rates", e);
			}
		} else if ("4.5".equals(billVersion)) {
			HashSet<String> vatrateSet = new HashSet<>();
			try {
				XPath xPath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xPath.compile("/request/payload/body/*/balance/vat/vat_rate");
				Object result = expr.evaluate(domDocument, XPathConstants.NODESET);
				NodeList vatrateElements = (NodeList) result;
				for (int i = 0; i < vatrateElements.getLength(); i++) {
					Node vatrateElement = vatrateElements.item(i);
					vatrateSet.add(((Element) vatrateElement).getAttribute("vat_rate"));
				}
				String[] vatRateArray = getVatRateArray(vatrateSet);

				StringBuilder vatrateList = new StringBuilder();
				for (int i = 0; i < vatRateArray.length; i++) {
					if (i > 0) {
						vatrateList.append(StringUtils.SPACE);
					}
					vatrateList.append(i).append("/").append(vatRateArray[i]);
				}
				return vatrateList.toString();
			} catch (XPathExpressionException e) {
				LoggerFactory.getLogger(getClass()).error("Error getting vat rates", e);
			}
		}
		return StringUtils.EMPTY;
	}

	private String[] getVatRateArray(HashSet<String> vatrateSet) {
		String[] ret = { "0", "2.5", "7.7" };
		String[] vatrates = vatrateSet.toArray(new String[vatrateSet.size()]);
		for (int i = 0; i < vatrates.length && i < 3; i++) {
			try {
				Float value = Float.valueOf(vatrates[i]);
				if (value > 0 && value < 7) {
					ret[1] = vatrates[i];
				} else if (value >= 7) {
					ret[2] = vatrates[i];
				}
			} catch (NumberFormatException e) {
				LoggerFactory.getLogger(getClass()).warn("Not valid vat rate found, using default", vatrates[i]);
			}
		}
		return ret;
	}

	protected String getXmlVersion() {
		Attr attr = domDocument.getDocumentElement().getAttributeNode("xsi:schemaLocation");
		String location = null;
		if (attr != null) {
			location = attr.getValue();
			if (location.contains("InvoiceRequest_400")) {//$NON-NLS-1$
				return "4.0";//$NON-NLS-1$
			} else if (location.contains("InvoiceRequest_440")) {//$NON-NLS-1$
				return "4.4";//$NON-NLS-1$
			} else if (location.contains("InvoiceRequest_450")) {//$NON-NLS-1$
				return "4.5";//$NON-NLS-1$
			}
		}
		return StringUtils.EMPTY;
	}

	protected File getXsltForBill(File rsc, XsltType type) {
		if ("4.0".equals(billVersion)) {
			throw new IllegalStateException("No privat patbill for tarmed 4.0 bills");
		} else if ("4.4".equals(billVersion)) {
			if (type == XsltType.PATBILL) {
				return new File(rsc, "44_patbill.xsl");
			} else if (type == XsltType.PATBILL_M1) {
				return new File(rsc, "44_patbill_m1.xsl");
			} else if (type == XsltType.PATBILL_M2) {
				return new File(rsc, "44_patbill_m2.xsl");
			} else if (type == XsltType.PATBILL_M3) {
				return new File(rsc, "44_patbill_m3.xsl");
			} else if (type == XsltType.RECLAIM) {
				return new File(rsc, "44_services.xsl");
			}
		} else if ("4.5".equals(billVersion)) {
			if (type == XsltType.PATBILL) {
				return new File(rsc, "45_patbill.xsl");
			} else if (type == XsltType.PATBILL_M1) {
				return new File(rsc, "45_patbill_m1.xsl");
			} else if (type == XsltType.PATBILL_M2) {
				return new File(rsc, "45_patbill_m2.xsl");
			} else if (type == XsltType.PATBILL_M3) {
				return new File(rsc, "45_patbill_m3.xsl");
			} else if (type == XsltType.RECLAIM) {
				return new File(rsc, "45_services.xsl");
			}
		}
		return null;
	}

	protected File getQrXsltForBill(File rsc, XsltType type) {
		if ("4.0".equals(billVersion) || "4.4".equals(billVersion)) {
			throw new IllegalStateException("No QR patbill for tarmed 4.0 or 4.4 bills");
		} else if ("4.5".equals(billVersion)) {
			if (type == XsltType.PATBILL) {
				return new File(rsc, "45_qr_patbill.xsl");
			} else if (type == XsltType.PATBILL_M1) {
				return new File(rsc, "45_qr_patbill_m1.xsl");
			} else if (type == XsltType.PATBILL_M2) {
				return new File(rsc, "45_qr_patbill_m2.xsl");
			} else if (type == XsltType.PATBILL_M3) {
				return new File(rsc, "45_qr_patbill_m3.xsl");
			} else if (type == XsltType.RECLAIM) {
				return new File(rsc, "45_services.xsl");
			}
		}
		return null;
	}

	/**
	 * Get the files generated by the last call to
	 * {@link ElexisPDFGenerator#printBill(File)}.
	 *
	 * @return
	 */
	public List<File> getPrintedBill() {
		return printed;
	}

	public void setPrint(boolean value) {
		this.print = value;
	}
}
