/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - updated for 4.4 and various fixes
 *
 *******************************************************************************/

/*  BITTE KEINE ÄNDERUNGEN AN DIESEM FILE OHNE RÜCKSPRACHE MIT MIR weirich@elexis.ch */
/*  THIS FILE IS FROZEN. DO NOT MODIFY */

package ch.elexis.TarmedRechnung;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Properties;

import javax.xml.transform.Source;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.JDOMSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.base.ch.arzttarife.importer.TrustCenters;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45Exporter;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45Exporter.EsrType;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45Validator;
import ch.elexis.base.ch.arzttarife.xml.update.XmlVersionUpdate44to45;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.rechnung.RnOutputDialog;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.core.utils.PlatformHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Rechnung;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

/**
 * Exportiert eine Elexis-Rechnung im XML 4.0 Format von xmldata.ch Bitte KEINE
 * Änderungen an dieser Klasse durchführen. Senden Sie Verbesserungsvorschläge
 * oder Wünsche als Mail oder direkt als Patch an weirich@elexis.ch.
 *
 * zur Weiterverarbeitung verwendet werden. DoExport(..) liefert ein
 * JDOM-Dokument, das die gewünschte Rechnung enthält. Diese kann vom Aufrufer
 * dann an einen Intermediär oder auf einen Drucker ausgegeben werden. Der
 * Output dieses Exporters ist TrustX zertifiziert. Änderungen sollten in den
 * seltensten Fällen nötig sein. Falls doch: Fehlermeldungen bitte an
 * weirich@elexis.ch
 *
 * @author gerry
 *
 */
public class XMLExporter implements IRnOutputter {

	private static Logger logger = LoggerFactory.getLogger(XMLExporter.class);

	// constants to access vat information from the extinfo of the Rechnungssteller
	public static final String VAT_ISMANDANTVAT = "at.medevit.medelexis.vat_ch/IsMandantVat";
	public static final String VAT_MANDANTVATNUMBER = "at.medevit.medelexis.vat_ch/MandantVatNumber";

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
	public static final String ELEMENT_REMARK = ATTR_REMARK; // $NON-NLS-1$
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
	public static final String ATTR_AMOUNT_REMINDER = "amount_reminder"; //$NON-NLS-1$
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

	public static final Namespace ns = Namespace.getNamespace("http://www.forum-datenaustausch.ch/invoice"); //$NON-NLS-1$
	public static final Namespace nsinvoice = Namespace.getNamespace("invoice", //$NON-NLS-1$
			"http://www.forum-datenaustausch.ch/invoice"); //$NON-NLS-1$
	public static final Namespace nsxsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final Namespace nsxenc = Namespace.getNamespace("nsxenc", "http://www.w3.org/2001/04/xmlenc#"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Namespace nsds = Namespace.getNamespace("ds", "http://www.w3.org/2000/09/xmldsig#"); //$NON-NLS-1$ //$NON-NLS-2$

	String tiers;

	IInvoice invoice;
	private ESR besr;
	static TarmedACL ta;
	private String outputDir;

	// default true, keep old behavior
	private boolean printAtIntermediate = true;
	public static final String PREFIX = "TarmedRn:"; //$NON-NLS-1$

	private Tarmed45Exporter exporter;
	private Tarmed45Validator validator;

	private EsrType esrType;

	public XMLExporter() {
		ta = TarmedACL.getInstance();
		exporter = new Tarmed45Exporter();
		validator = new Tarmed45Validator();
	}

	/**
	 * Output a Collection of bills. This essentially lets the user modify the
	 * output settings (if any) and then calls doExport() für each bill in rnn
	 *
	 * @param type desired mode (original, copy, storno)
	 * @param rnn  a Collection of Rechnung - Objects to output
	 */
	@Override
	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rnn, Properties props) {
		Result<Rechnung> ret = new Result<Rechnung>();
		if (outputDir == null) {
			SWTHelper.SimpleDialog dlg = new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
				@Override
				public Control getControl(Composite parent) {
					return createSettingsControl(parent);
				}

				@Override
				public void beforeClosing() {
					// Nothing
				}
			});
			if (dlg.open() != Dialog.OK) {
				return ret;
			}
		}
		ProgressMonitorDialog progress = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		try {
			progress.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.RechnungsDrucker_PrintingBills, rnn.size());
					exporter.setUpdateElectronicDelivery(true);
					for (Rechnung rn : rnn) {
						if (doExport(rn, outputDir + File.separator + rn.getNr() + ".xml", type, //$NON-NLS-1$
								false) == null) {
							ret.add(Result.SEVERITY.ERROR, 1, Messages.XMLExporter_ErrorInBill + rn.getNr(), rn, true);
						}
						monitor.worked(1);
						if (monitor.isCanceled()) {
							break;
						}
					}
					exporter.setUpdateElectronicDelivery(false);
					monitor.done();
				}
			});

		} catch (InvocationTargetException | InterruptedException e) {
			LoggerFactory.getLogger(XMLExporter.class).error("Error outputting bills", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.RechnungsDrucker_MessageErrorWhilePrinting,
					Messages.RechnungsDrucker_MessageErrorWhilePrinting + "[" + e.getMessage() + "]");
		}
		return ret;
	}

	/**
	 * Wa want to be informed on cancellings of any bills
	 *
	 * @param rn we don't mind, we always return true
	 */
	@Override
	public boolean canStorno(final Rechnung rn) {
		return true;
	}

	public boolean isPrintAtIntermediate() {
		return printAtIntermediate;
	}

	public void setPrintAtIntermediate(boolean value) {
		printAtIntermediate = value;
	}

	private void negate(Element el, String attr) {
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

	protected Document doExport450(final Rechnung rechnung, final String dest, final IRnOutputter.TYPE type,
			final boolean doVerify) {
		// create a object for managing vat rates and values on invoice level
		invoice = CoreModelServiceHolder.get().load(rechnung.getId(), IInvoice.class)
				.orElseThrow(() -> new IllegalStateException("Could not load invoice [" + rechnung.getId() + "]"));

		return null;
	}

	/**
	 * Export a bill as XML. We do, in fact first check whether this bill was
	 * exported already. And if so we do not create it again but load the old one.
	 * There is deliberately no possibility to avoid this behaviour. (One can only
	 * delete or storno a bill and recreate it (even then the stored xml remains
	 * stored. Additionally, the caller can chose to store the bill as XML in the
	 * file system. This is done if the parameter dest ist given. On success the
	 * caller will receive a JDOM Document containing the bill.
	 *
	 * @param rechnung the bill to export
	 * @param dest     a full filepath to save the final document (or null to not
	 *                 save it)
	 * @param type     Type of output (original, copy, storno)
	 * @param doVerify true if the bill should be sent trough a verifyer after
	 *                 creation.
	 * @return the jdom XML-Document that contains the bill. Might be null on
	 *         failure.
	 */
	public Document doExport(final Rechnung rechnung, final String dest, final IRnOutputter.TYPE type,
			final boolean doVerify) {
		// clear();
		invoice = CoreModelServiceHolder.get().load(rechnung.getId(), IInvoice.class)
				.orElseThrow(() -> new IllegalStateException("Could not load invoice [" + rechnung.getId() + "]"));

		exporter.setEsrType(getEsrTypeOrFallback(invoice));

		if (xmlBillExists(invoice)) {
			logger.info("Updating existing bill for " + invoice.getNumber());
			Document updated = updateExistingXmlBill(invoice, dest, type, doVerify);
			if (updated != null) {
				return updated;
			}
		}

		if (type.equals(TYPE.STORNO)) {
			SWTHelper.showError(Messages.XMLExporter_StornoImpossibleCaption,
					Messages.XMLExporter_StornoImpossibleText);
			return null;
		}

		logger.info("Creating new bill for " + rechnung.getNr());
		ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
		if (exporter.doExport(invoice, xmlOutput, type)) {
			Document xmlRn = getAsJdomDocument(xmlOutput).orElse(null);
			ch.fd.invoice450.request.RequestType invoiceRequest = TarmedJaxbUtil
					.unmarshalInvoiceRequest450(new ByteArrayInputStream(xmlOutput.toByteArray()));
			if (doVerify) {
				Result<IInvoice> res = validator.checkInvoice(invoice, invoiceRequest);
				// new Validator().checkBill(invoice, xmlRn, new Result<IInvoice>());
			}
			// save rounded amount
			CoreModelServiceHolder.get().save(invoice);

			checkXML(xmlRn, dest, invoice, doVerify);

			if (invoice.getState() != InvoiceState.DEFECTIVE) {
				try {

					setExistingXml(invoice, xmlRn);
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
		return null;
	}

	/**
	 * If current esrType is null (default is {@link EsrType#esrQR}) or
	 * {@link EsrType#esrQR}, check if there is an IBAN available for the biller. If
	 * none is available fallback to {@link EsrType#esr9}.
	 *
	 * @param invoice
	 * @return
	 */
	public EsrType getEsrTypeOrFallback(IInvoice invoice) {
		if (esrType == null || esrType == EsrType.esrQR) {
			String iban = (String) invoice.getMandator().getBiller().getExtInfo("IBAN");
			if (StringUtils.isEmpty(iban)) {
				logger.warn("No IBAN for ESRQR with biller [" + invoice.getMandator().getBiller().getLabel()
						+ "] fallback to ESR9 for invoice [" + invoice.getNumber() + "]");
				return EsrType.esr9;
			}
		}
		return esrType;
	}

	private String getInvoiceId(IInvoice invoice) {
		IPatient patient = invoice.getCoverage().getPatient();
		String pid = StringTool.pad(StringTool.LEFT, '0', patient.getPatientNr(), 6);
		String nr = StringTool.pad(StringTool.LEFT, '0', invoice.getNumber(), 6);
		return pid + nr;
	}

	private Document updateExistingXmlBill(IInvoice invoice, String dest, TYPE type, boolean doVerify) {
		// If the bill exists already in the database, it has been output
		// earlier, so we don't
		// recreate it. We must, however, reflect changes that happened
		// since it was output:
		// Payments, state changes, obligations
		// initialize variables
		// coverage = invoice.getCoverage();
		// mandator = invoice.getMandator();
		try {
			Document ret = getExistingXml(invoice).get();
			Element root = ret.getRootElement();
			// update existing xml from 4.4 to 4.5 for esr qr
			if (getXmlVersion(root).equals("4.4") && !CoreUtil.isTestMode()) {
				XmlVersionUpdate44to45 versionUpdate = new XmlVersionUpdate44to45(invoice);
				versionUpdate.update();
				// reload updated xml 4.5
				ret = getExistingXml(invoice).get();
				root = ret.getRootElement();
			}

			if (getXmlVersion(root).equals("4.0")) {
				updateExisting4Xml(root, type, invoice);
			} else if (getXmlVersion(root).equals("4.4")) {
				updateExisting44Xml(root, type, invoice);

				InvoiceState state = invoice.getState();
				if (state == InvoiceState.DEMAND_NOTE_1 || state == InvoiceState.DEMAND_NOTE_1_PRINTED) {
					if (dest != null) {
						dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m1.xml");
					}
					addReminderEntry(root, invoice, "1");
				} else if (state == InvoiceState.DEMAND_NOTE_2 || state == InvoiceState.DEMAND_NOTE_2_PRINTED) {
					if (dest != null) {
						dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m2.xml");
					}
					addReminderEntry(root, invoice, "2");
				} else if (state == InvoiceState.DEMAND_NOTE_3 || state == InvoiceState.DEMAND_NOTE_3_PRINTED) {
					if (dest != null) {
						dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m3.xml");
					}
					addReminderEntry(root, invoice, "3");
				}
			} else if (getXmlVersion(root).equals("4.5")) {
				Optional<?> invoiceRequest = getExistingXmlModel(invoice, "4.5");
				if (invoiceRequest.isPresent()) {
					exporter.updateExistingXml((ch.fd.invoice450.request.RequestType) invoiceRequest.get(), type,
							invoice, this);

					InvoiceState state = invoice.getState();
					if (state == InvoiceState.DEMAND_NOTE_1 || state == InvoiceState.DEMAND_NOTE_1_PRINTED) {
						if (dest != null) {
							dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m1.xml");
						}
						exporter.addReminderEntry((ch.fd.invoice450.request.RequestType) invoiceRequest.get(), invoice,
								"1");
					} else if (state == InvoiceState.DEMAND_NOTE_2 || state == InvoiceState.DEMAND_NOTE_2_PRINTED) {
						if (dest != null) {
							dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m2.xml");
						}
						exporter.addReminderEntry((ch.fd.invoice450.request.RequestType) invoiceRequest.get(), invoice,
								"2");
					} else if (state == InvoiceState.DEMAND_NOTE_3 || state == InvoiceState.DEMAND_NOTE_3_PRINTED) {
						if (dest != null) {
							dest = dest.toLowerCase().replaceFirst("\\.xml$", "_m3.xml");
						}
						exporter.addReminderEntry((ch.fd.invoice450.request.RequestType) invoiceRequest.get(), invoice,
								"3");
					} else if (state.getState() < InvoiceState.DEMAND_NOTE_1.getState()
							&& exporter.isReminder((ch.fd.invoice450.request.RequestType) invoiceRequest.get())) {
						exporter.removeReminderEntry((ch.fd.invoice450.request.RequestType) invoiceRequest.get(),
								invoice);
					}
					ret = getAsJdomDocument((ch.fd.invoice450.request.RequestType) invoiceRequest.get()).orElse(null);
				}
			} else {
				logger.warn("Bill in unknown XML version " + getXmlVersion(root) + ", recreating bill.");
				return null;
			}
			checkXML(ret, dest, invoice, doVerify);

			if (dest != null) {
				if (type.equals(TYPE.STORNO) && !dest.toLowerCase().endsWith("_storno.xml")) { //$NON-NLS-1$
					writeFile(ret, dest.toLowerCase().replaceFirst("\\.xml$", "_storno.xml")); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					writeFile(ret, dest);
				}
			}
			setExistingXml(invoice, ret);
			return ret;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.XMLExporter_ReadErrorCaption, Messages.XMLExporter_ReadErrorText);
			// What should we do -> We create it from scratch
			return null;
		}
	}

	protected Optional<?> getExistingXmlModel(IInvoice invoice, String version) {
		IBlob blob = CoreModelServiceHolder.get().load(PREFIX + invoice.getNumber(), IBlob.class).orElse(null);
		if (blob != null && blob.getStringContent() != null && !blob.getStringContent().isEmpty()) {
			if ("4.5".equals(version)) {
				ch.fd.invoice450.request.RequestType invoiceRequest = TarmedJaxbUtil
						.unmarshalInvoiceRequest450(new ByteArrayInputStream(blob.getStringContent().getBytes()));
				return Optional.ofNullable(invoiceRequest);
			}
		}
		return Optional.empty();
	}

	public Optional<Document> getAsJdomDocument(ByteArrayOutputStream outputStream) {
		if (outputStream != null) {
			SAXBuilder builder = new SAXBuilder();
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
				return Optional.of(builder.build(inputStream));
			} catch (IOException | JDOMException e) {
				LoggerFactory.getLogger(getClass()).error("Error loading as jdom document", e);
			}
		}
		return Optional.empty();
	}

	public Optional<Document> getAsJdomDocument(ch.fd.invoice450.request.RequestType request) {
		if (request != null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			TarmedJaxbUtil.marshallInvoiceRequest(request, outputStream);
			return getAsJdomDocument(outputStream);
		}
		return Optional.empty();
	}

	public Optional<Document> getExistingXml(IInvoice invoice) {
		IBlob blob = CoreModelServiceHolder.get().load(PREFIX + invoice.getNumber(), IBlob.class).orElse(null);
		if (blob != null && blob.getStringContent() != null && !blob.getStringContent().isEmpty()) {
			SAXBuilder builder = new SAXBuilder();
			try {
				return Optional.of(builder.build(new StringReader(blob.getStringContent())));

			} catch (IOException | JDOMException e) {
				LoggerFactory.getLogger(getClass()).error("Error loadgin existing xml document", e);
			}

		}
		return Optional.empty();
	}

	public void setExistingXml(IInvoice invoice, Document document) throws IOException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputter xout = new XMLOutputter(Format.getCompactFormat());
		xout.output(document, stringWriter);
		IBlob blob = CoreModelServiceHolder.get().load(PREFIX + invoice.getNumber(), IBlob.class).orElseGet(() -> {
			IBlob newBlob = CoreModelServiceHolder.get().create(IBlob.class);
			newBlob.setId(PREFIX + invoice.getNumber());
			return newBlob;
		});
		blob.setStringContent(stringWriter.toString());
		CoreModelServiceHolder.get().save(blob);
	}

	private void addReminderEntry(Element root, IInvoice invoice, String reminderLevel) {
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
		reminder.setAttribute(ATTR_REQUEST_TIMESTAMP, timestamp); // $NON-NLS-1$
		reminder.setAttribute(ATTR_REQUEST_DATE, dateString); // $NON-NLS-1$
		reminder.setAttribute(ATTR_REQUEST_ID, getInvoiceId(invoice)); // $NON-NLS-1$
		reminder.setAttribute(ATTR_REMINDER_LEVEL, reminderLevel); // $NON-NLS-1$

		// add amount reminder and recalculate amount due
		Element body = payload.getChild("body", XMLExporter.nsinvoice);
		if (body != null) {
			Element balance = body.getChild("balance", XMLExporter.nsinvoice);
			Money amountReminder = invoice.getDemandAmount();
			balance.setAttribute(XMLExporter.ATTR_AMOUNT_REMINDER, XMLTool.moneyToXmlDouble(amountReminder));
			// rewrite amount due
			Money mDue = new Money(invoice.getTotalAmount());
			mDue.addMoney(amountReminder);
			mDue.subtractMoney(invoice.getPayedAmount());
			balance.setAttribute(XMLExporter.ATTR_AMOUNT_DUE, XMLTool.moneyToXmlDouble(mDue));
		}

		if (firstReminder) {
			@SuppressWarnings("unchecked")
			List<Element> children = payload.getChildren();
			List<Element> newChildren = new ArrayList<>();
			for (int i = 0; i < children.size(); i++) {
				newChildren.add(children.get(i));
				// add reminder after invoice
				if (children.get(i).getName().equals("invoice")) {
					newChildren.add(reminder);
				}
			}
			payload.removeContent();
			payload.setContent(newChildren);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateExisting44Xml(Element root, TYPE type, IInvoice existingInvoice) {
		Money mPaid = existingInvoice.getPayedAmount();
		// update processing, print_at_intermediate and transport via EAN
		Element processing = root.getChild("processing", XMLExporter.nsinvoice);//$NON-NLS-1$
		String intermediatePrint = processing.getAttributeValue(XMLExporterProcessing.ATTR_INTERMEDIAT_PRINT);
		if (("1".equals(intermediatePrint) || "true".equals(intermediatePrint)) && !isPrintAtIntermediate()) {
			processing.setAttribute(XMLExporterProcessing.ATTR_INTERMEDIAT_PRINT, "0");
		} else if (("0".equals(intermediatePrint) || "false".equals(intermediatePrint)) && isPrintAtIntermediate()) {
			processing.setAttribute(XMLExporterProcessing.ATTR_INTERMEDIAT_PRINT, "1");
		}
		Element transport = processing.getChild(XMLExporterProcessing.ELEMENT_TRANSPORT, XMLExporter.nsinvoice);
		if (transport != null) {
			Element via = transport.getChild(XMLExporterProcessing.ELEMENT_TRANSPORT_VIA, XMLExporter.nsinvoice);
			String iEAN = XMLExporterProcessing.getIntermediateEAN(existingInvoice, this);
			if (iEAN != null && !iEAN.isEmpty()) {
				via.setAttribute(XMLExporterProcessing.ATTR_TRANSPORT_VIA_VIA, iEAN);
			}
		}
		if (CoverageServiceHolder.get().getCopyForPatient(invoice.getCoverage())) {
			processing.setAttribute(XMLExporterProcessing.ATTR_PATIENT_COPY_PRINT, "1");
		} else {
			processing.setAttribute(XMLExporterProcessing.ATTR_PATIENT_COPY_PRINT, "0");
		}

		// update payload and balance
		Element payload = root.getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$

		// update patient information
		Element tiersGarant = body.getChild("tiers_garant", XMLExporter.nsinvoice);//$NON-NLS-1$
		if (tiersGarant != null && existingInvoice.getCoverage().getPatient() != null) {
			Element patientUpdate = buildPatient(existingInvoice.getCoverage());
			if (patientUpdate != null) {
				List<Element> tiersChildren = tiersGarant.getChildren();
				// remove existing patient children
				ListIterator<Element> iterator = tiersChildren.listIterator();
				while (iterator.hasNext()) {
					Object obj = iterator.next();
					if (obj instanceof Element && "patient".equals(((Element) obj).getName())) {
						iterator.remove();
					}
				}
				tiersChildren.add(getPatientIndex(tiersChildren), patientUpdate);
			}
		}

		// update guarantor information
		if (tiersGarant != null) {
			IContact guarantorContact = XMLExporterUtil.getGuarantor(XMLExporter.TIERS_GARANT,
					existingInvoice.getCoverage().getPatient(), existingInvoice.getCoverage());
			if (guarantorContact != null) {
				Element guarantorUpdate = buildGuarantor(guarantorContact, existingInvoice.getCoverage().getPatient());
				if (guarantorUpdate != null) {
					List<Element> tiersChildren = tiersGarant.getChildren();
					// remove existing guarantor children
					ListIterator<Element> iterator = tiersChildren.listIterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						if (obj instanceof Element && "guarantor".equals(((Element) obj).getName())) {
							iterator.remove();
						}
					}
					tiersChildren.add(getGuarantorIndex(tiersChildren), guarantorUpdate);
				}
			}
		}

		Element balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		XMLExporterBalance xmlBalance = new XMLExporterBalance(balance);
		// fix for erroneous bills without amount_prepaid
		// (https://redmine.medelexis.ch/issues/6624)
		tryToFixPrepaid(xmlBalance, mPaid);
		if (!mPaid.equals(xmlBalance.getPrepaid())) {
			xmlBalance.setPrepaid(mPaid);
			Money mDue = xmlBalance.getAmount();
			mDue.addMoney(xmlBalance.getReminder());
			mDue.subtractMoney(mPaid);
			mDue.roundTo5();
			xmlBalance.setDue(mDue);
		}
		// always update copy information
		payload.setAttribute(ATTR_PAYLOAD_COPY, type.equals(IRnOutputter.TYPE.COPY) ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		if (type.equals(TYPE.STORNO)) {
			payload.setAttribute("storno", Boolean.toString(true));//$NON-NLS-1$
			processing.setAttribute(XMLExporterProcessing.ATTR_PATIENT_COPY_PRINT, "0");
			Element services = body.getChild("services", XMLExporter.nsinvoice);//$NON-NLS-1$
			XMLExporterServices xmlServices = new XMLExporterServices(services);
			xmlServices.negateAll();

			xmlBalance.negateAmount();
			xmlBalance.negateAmountObligations();
			xmlBalance.setDue(new Money());
			xmlBalance.setPrepaid(new Money());
		}
	}

	private int getGuarantorIndex(List<Element> tiersChildren) {
		int index = 0;
		for (Object object : tiersChildren) {
			if (object instanceof Element) {
				String name = ((Element) object).getName();
				if ("biller".equals(name) || "provider".equals(name) || "insurance".equals(name)
						|| "patient".equals(name) || "insured".equals(name)) {
					index++;
				}
			}
		}
		return index;
	}

	private int getPatientIndex(List<Element> tiersChildren) {
		int index = 0;
		for (Object object : tiersChildren) {
			if (object instanceof Element) {
				String name = ((Element) object).getName();
				if ("biller".equals(name) || "provider".equals(name) || "insurance".equals(name)) {
					index++;
				}
			}
		}
		return index;
	}

	private void tryToFixPrepaid(XMLExporterBalance xmlBalance, Money mPaid) {
		if (!xmlBalance.hasPrepaid()) {
			xmlBalance.setPrepaid(mPaid);
		}
		Money xmlAmount = xmlBalance.getAmount();
		Money xmlDue = xmlBalance.getDue();
		Money xmlPrepaid = xmlBalance.getPrepaid();
		Money xmlReminder = xmlBalance.getReminder();

		double diffDouble = (xmlAmount.doubleValue() + xmlReminder.doubleValue())
				- (xmlPrepaid.doubleValue() + xmlDue.doubleValue());
		// this is an erroneous bill
		if (Math.abs(diffDouble) > 1) {
			xmlBalance
					.setDue(new Money((xmlAmount.doubleValue() + xmlReminder.doubleValue()) - xmlPrepaid.doubleValue())
							.roundTo5());
		}
	}

	private void updateExisting4Xml(Element root, TYPE type, IInvoice existingInvoice) {
		Namespace namespace = Namespace.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
		Money mPaid = existingInvoice.getPayedAmount();

		Element invoice = root.getChild("invoice", namespace);//$NON-NLS-1$
		fixCanton(invoice, namespace);
		Element balance = invoice.getChild("balance", namespace);//$NON-NLS-1$
		Money anzInBill = XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount_prepaid"));//$NON-NLS-1$
		if (!mPaid.equals(anzInBill)) {
			Money mAmount = XMLTool.xmlDoubleToMoney(balance.getAttributeValue("amount"));//$NON-NLS-1$
			// never pay more than the total on XML bill, those cases are handled in Elexis
			// Rechnung and UI
			if (mPaid.isMoreThan(mAmount)) {
				mPaid = mAmount;
			}
			balance.setAttribute("amount_prepaid", XMLTool.moneyToXmlDouble(mPaid));//$NON-NLS-1$

			Money mDue = new Money(mAmount).subtractMoney(mPaid).roundTo5();
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

	private void fixCanton(Element invoice, Namespace namespace) {
		Element detail = invoice.getChild("detail", namespace);
		String canton = detail.getAttributeValue("canton", namespace);
		if (canton == null || canton.isEmpty()) {
			detail.setAttribute("canton", "AG");
		}
	}

	public static String getXmlVersion(Element root) {
		String location = root.getAttributeValue("schemaLocation", //$NON-NLS-1$
				Namespace.getNamespace("http://www.w3.org/2001/XMLSchema-instance"));//$NON-NLS-1$
		if (location != null && !location.isEmpty()) {
			if (location.contains("InvoiceRequest_400")) {//$NON-NLS-1$
				return "4.0";//$NON-NLS-1$
			} else if (location.contains("InvoiceRequest_440")) {//$NON-NLS-1$
				return "4.4";//$NON-NLS-1$
			} else if (location.contains("InvoiceRequest_450")) {//$NON-NLS-1$
				return "4.5";//$NON-NLS-1$
			}
		}
		return location;// $NON-NLS-1$
	}

	private boolean xmlBillExists(IInvoice invoice) {
		IBlob blob = CoreModelServiceHolder.get().load(PREFIX + invoice.getNumber(), IBlob.class).orElse(null);
		return blob != null && blob.getStringContent() != null && !blob.getStringContent().isEmpty();
	}

	protected Element buildGuarantor(IContact garant, IContact patient) {
		// Patient wird im override des MediPort Plugins verwendet
		// Hinweis:
		// XML Standard:
		// http://www.forum-datenaustausch.ch/mdinvoicerequest_xml4.00_v1.2_d.pdf
		// Dort steht beim Feld 11310: Gesetzlicher Vertreter des Patienten.
		Element guarantor = new Element("guarantor", XMLExporter.nsinvoice); //$NON-NLS-1$
		guarantor.addContent(XMLExporterUtil.buildAdressElement(garant));
		return guarantor;
	}

	protected Element buildPatient(ICoverage coverage) {
		Element patientElement = new Element("patient", XMLExporter.nsinvoice); //$NON-NLS-1$
		// patient.setAttribute("unique_id",rn.getFall().getId()); // this is
		// optional and should be
		// ssn13 type. leave it out for now
		if (coverage.getPatient() == null) {
			MessageDialog.openError(null, Messages.XMLExporter_ErrorCaption, Messages.XMLExporter_NoPatientText);
			return null;
		}
		patientElement.setAttribute("gender", //$NON-NLS-1$
				coverage.getPatient().getGender().toString().toLowerCase());
		LocalDateTime dateOfBirth = coverage.getPatient().getDateOfBirth();
		if (dateOfBirth == null) { // make validator happy if we don't
			// know the birthdate
			patientElement.setAttribute(XMLExporter.ATTR_BIRTHDATE, "0001-00-00T00:00:00"); //$NON-NLS-1$
		} else {
			patientElement.setAttribute(XMLExporter.ATTR_BIRTHDATE,
					XMLExporterUtil.makeTarmedDatum(dateOfBirth.toLocalDate())); // $NON-NLS-1$
		}
		patientElement.addContent(XMLExporterUtil.buildAdressElement(coverage.getPatient()));
		if (StringUtils.isNotBlank((String) coverage.getExtInfo("VEKANr"))
				&& StringUtils.isNotBlank((String) coverage.getExtInfo("VEKAValid"))) {
			Element cardElement = new Element("card", XMLExporter.nsinvoice);
			cardElement.setAttribute("card_id", (String) coverage.getExtInfo("VEKANr"));
			cardElement.setAttribute("expiry_date",
					XMLExporterUtil.makeTarmedDatum((String) coverage.getExtInfo("VEKAValid")));
			patientElement.addContent(cardElement);
		}
		XMLExporterUtil.addSSNAttribute(patientElement, coverage.getPatient(), coverage, invoice, true);
		return patientElement;
	}

	@Override
	public String getDescription() {
		return Messages.XMLExporter_TarmedForTrustCenter;
	}

	/**
	 * Validate XML of the created bill against the appropriate schema. Subclasses
	 * can override to provide specific handling of errors. The default
	 * implementation will mark the bill as erroneous if STRICT_BILLING is active
	 * and XML Schema errors are present.
	 *
	 * @param xmlDoc   the bill
	 * @param dest     the destination path if the user chose output to file. Might
	 *                 be null
	 * @param invoice  the bill to output
	 * @param doVerify false if the user doesn't want strict validity check
	 *                 (subclasses may ignore)
	 */
	protected void checkXML(final Document xmlDoc, String dest, final IInvoice invoice, final boolean doVerify) {
		if (ConfigServiceHolder.getUser(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)) {
			Source source = new JDOMSource(xmlDoc);
			String path = PlatformHelper.getBasePath("ch.elexis.base.ch.arzttarife") + File.separator + "rsc"; //$NON-NLS-1$ //$NON-NLS-2$
			List<String> errs = null;
			// validate depending on tarmed version
			if (getXmlVersion(xmlDoc.getRootElement()).equals("4.0")) {
				logger.info("Validating XML against MDInvoiceRequest_400.xsd");
				errs = XMLTool.validateSchema(path + File.separator + "MDInvoiceRequest_400.xsd", //$NON-NLS-1$
						source);
			} else if (getXmlVersion(xmlDoc.getRootElement()).equals("4.4")) {
				logger.info("Validating XML against generalInvoiceRequest_440.xsd");
				errs = XMLTool.validateSchema(path + File.separator + "generalInvoiceRequest_440.xsd", source); //$NON-NLS-1$
			} else if (getXmlVersion(xmlDoc.getRootElement()).equals("4.5")) {
				logger.info("Validating XML against generalInvoiceRequest_450.xsd");
				errs = validator.validateRequest(toInputStream(xmlDoc));
			} else {
				errs = Collections
						.singletonList("Bill in unknown XML version " + getXmlVersion(xmlDoc.getRootElement()));
			}

			if (!errs.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String err : errs) {
					sb.append(err).append(StringConstants.LF);
				}
				logger.error(sb.toString());
				invoice.reject(InvoiceState.REJECTCODE.VALIDATION_ERROR, sb.toString());
				CoreModelServiceHolder.get().save(invoice);
				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
				File invalidDir = new File(CoreHub.getWritableUserDir(), "validation_error");
				if (!invalidDir.exists()) {
					invalidDir.mkdir();
				}
				File invalidFile = new File(invalidDir, invoice.getNumber() + "_invalid_tarmed.xml");
				invalidDir.deleteOnExit();
				invalidFile.deleteOnExit();
				try (FileWriter writer = new FileWriter(invalidFile)) {
					xout.output(xmlDoc, writer);
					logger.info("Temporary invalid xml written to [" + invalidFile.getAbsolutePath() + "]");
				} catch (IOException e) {
					logger.error("Failed writing invalid xml", e);
				}
			}
		}

	}

	@Override
	public Control createSettingsControl(final Object parent) {
		final Composite parentInc = (Composite) parent;
		Composite ret = new Composite(parentInc, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		Label l = new Label(ret, SWT.NONE);
		l.setText(Messages.XMLExporter_PleaseEnterOutputDirectoryForBills);
		l.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		Button b = new Button(ret, SWT.PUSH);
		b.setText(Messages.XMLExporter_Change);
		GridData buttonData = new GridData();
		buttonData.widthHint = 75;
		b.setLayoutData(buttonData);
		final Text text = new Text(ret, SWT.READ_ONLY | SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				outputDir = new DirectoryDialog(parentInc.getShell(), SWT.OPEN).open();
				LocalConfigService.set(PreferenceConstants.RNN_EXPORTDIR, outputDir);
				text.setText(outputDir);
			}
		});
		outputDir = LocalConfigService.get(PreferenceConstants.RNN_EXPORTDIR, CoreUtil.getDefaultDBPath());
		text.setText(outputDir);
		return ret;
	}

	protected void writeFile(final Document doc, final String dest) throws IOException {
		FileOutputStream fout = new FileOutputStream(dest);
		OutputStreamWriter cout = new OutputStreamWriter(fout, "UTF-8"); //$NON-NLS-1$
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(doc, cout);
		cout.close();
		fout.close();
		InvoiceState previousState = invoice.getState();
		if ((previousState == InvoiceState.OPEN) || (previousState == InvoiceState.DEMAND_NOTE_1)
				|| (previousState == InvoiceState.DEMAND_NOTE_2) || (previousState == InvoiceState.DEMAND_NOTE_3)) {
			InvoiceState newState = InvoiceState.fromState(previousState.numericValue() + 1);
			invoice.setState(newState);
			CoreModelServiceHolder.get().save(invoice);
		}
		invoice.addTrace(Rechnung.OUTPUT, getDescription() + ": " //$NON-NLS-1$
				+ invoice.getState().getLocaleText());
		CoreModelServiceHolder.get().save(invoice);
	}

	protected InputStream toInputStream(final Document doc) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter cout = new OutputStreamWriter(out, "UTF-8"); //$NON-NLS-1$
			XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
			xout.output(doc, cout);
			cout.close();
			out.close();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			LoggerFactory.getLogger(XMLExporter.class).error("Error converting document to input stream", e);
		}
		return null;
	}

	@Override
	public boolean canBill(final Fall fall) {
		Kontakt garant = fall.getGarant();
		Kontakt kostentraeger = fall.getCostBearer();
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
	public void saveComposite() {
		// Nothing
	}

	protected String getIntermediateEAN(final ICoverage coverage) {
		// Try to find the intermediate EAN. If we have explicitely set
		// an intermediate EAN, we'll use this one. Otherweise, we'll
		// check whether the mandator has a TC contract. if so, we try to
		// find the TC's EAN.
		// If nothing appropriate is found, we'll try to use the receiver EAN
		// or at least the guarantor EAN.
		// If everything fails we use a pseudo EAN to make the Validators happy
		String iEAN = TarmedRequirements.getIntermediateEAN(coverage);
		if (iEAN.length() == 0) {
			if (TarmedRequirements.hasTCContract(invoice.getMandator())) {
				String trustCenter = TarmedRequirements.getTCName(invoice.getMandator());
				if (trustCenter.length() > 0) {
					iEAN = TrustCenters.getTCEAN(trustCenter);
				}
			}
		}
		return iEAN;
	}

	protected String getSenderEAN(IMandator actMandant) {
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

			VatRateElement(double scale) {
				this.scale = scale;
				sumamount = 0;
				sumvat = 0;
			}

			void add(double amount) {
				this.sumamount += amount;
				sumvat += (amount / (100.0 + scale)) * scale;
			}

			@Override
			public int compareTo(VatRateElement other) {
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

		public void add(double scale, double amount) {
			VatRateElement element = rates.get(Double.valueOf(scale));
			if (element == null) {
				element = new VatRateElement(scale);
				rates.put(Double.valueOf(scale), element);
			}
			element.add(amount);
			sumvat += (amount / (100.0 + scale)) * scale;
		}
	}

	public ESR getBesr() {
		return besr;
	}

	protected String getRole(final ICoverage coverage) {
		return "production"; //$NON-NLS-1$
	}

	public void setEsrType(EsrType esrType) {
		this.esrType = esrType;
	}

	@Override
	public void openOutput(IInvoice invoice, LocalDateTime timestamp, InvoiceState invoiceState) {
		String outputDir = LocalConfigService.get(PreferenceConstants.RNN_EXPORTDIR, CoreUtil.getDefaultDBPath());
		XMLFileUtil.lookupFile(outputDir, invoice, timestamp, invoiceState).ifPresent(xmlFile -> {
			if (xmlFile.exists()) {
				Program.launch(xmlFile.getAbsolutePath());
			} else {
				LoggerFactory.getLogger(getClass()).info("File [" + xmlFile.getAbsolutePath() + "] does not exist"); //$NON-NLS-1$
			}
		});
	}

	@Override
	public void customizeDialog(Object rnOutputDialog) {
		if (rnOutputDialog instanceof RnOutputDialog) {
			((RnOutputDialog) rnOutputDialog).setOkButtonText(Messages.Core_DoSend);
		}
	}
}