package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ch.elexis.TarmedRechnung.Messages;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.fd.invoice450.request.RequestType;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;

public class Tarmed45Validator {

	private static Validator validator;

	/**
	 * Validate the invoice request from the {@link InputStream} against the xsd
	 * schema.
	 *
	 * @param request
	 * @return
	 */
	public synchronized List<String> validateRequest(InputStream request) {
		if (validator == null) {
			try {
				validator = initValidator();
			} catch (SAXException e) {
				LoggerFactory.getLogger(getClass()).error("Error creating validator", e);
				throw new IllegalStateException("Error creating validator");
			}
		}
		return validate(new StreamSource(request));
	}

	private Validator initValidator() throws SAXException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setResourceResolver(new LSResourceResolver() {

			private DOMImplementationLS impl;

			public DOMImplementationLS getDOMImpl() {
				if (impl == null) {
					try {
						DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
						impl = (DOMImplementationLS) registry.getDOMImplementation("LS 3.0");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				return impl;
			}

			public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
					String baseURI) {
				LoggerFactory.getLogger(Tarmed45Validator.class).info(
						"Resolve resource ns[" + namespaceURI + "] pubid[" + systemId + "] sysid[" + systemId + "]");
				if (StringUtils.isNotBlank(systemId) && systemId.contains("://")) {
					systemId = systemId.substring(systemId.lastIndexOf('/') + 1);
				}
				InputStream is = Tarmed45Validator.class.getResourceAsStream("/rsc/" + systemId);
				if (is == null) {
					LoggerFactory.getLogger(Tarmed45Validator.class)
							.warn("Could not resolve resource using impl [" + impl + "]");
					return null;
				}
				LSInput lsInput = getDOMImpl().createLSInput();
				lsInput.setByteStream(is);
				return lsInput;
			}
		});

		LoggerFactory.getLogger(Tarmed45Validator.class)
				.warn("Loading generalInvoiceRequest_450.xsd using factory [" + factory + "]");
		factory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
		Schema schema = factory.newSchema(
				new StreamSource(Tarmed45Validator.class.getResourceAsStream("/rsc/generalInvoiceRequest_450.xsd")));
		return schema.newValidator();
	}

	private List<String> validate(Source source) {
		MyErrorHandler errorHandler = new MyErrorHandler();
		try {

			validator.setErrorHandler(errorHandler);
			validator.validate(source);
		} catch (Exception ex) {
			errorHandler.exception(ex);
		}
		return errorHandler.getMessageList();
	}

	private static class MyErrorHandler implements ErrorHandler {
		public List<Exception> exceptions = new ArrayList<>();

		@Override
		public void error(SAXParseException exception) throws SAXException {
			exceptions.add(exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			exceptions.add(exception);
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			// Nothing
		}

		public void exception(Exception exception) {
			// Nothing this is not an xml related error
		}

		public List<String> getMessageList() {
			List<String> messageList = new ArrayList<>();
			for (Exception ex : exceptions) {
				String msg = ex.getMessage();
				if (msg == null || msg.length() == 0) {
					msg = ex.toString();
				}
				messageList.add(msg);
			}
			return messageList;
		}
	}

	/**
	 * Check if invoice and invoice request contain valid information.
	 *
	 * @param invoice
	 * @param invoiceRequest
	 * @return
	 */
	public Result<IInvoice> checkInvoice(IInvoice invoice, RequestType invoiceRequest) {
		Result<IInvoice> res = new Result<IInvoice>();

		IMandator m = invoice.getMandator();
		if (invoice.getState().numericValue() > InvoiceState.OPEN.numericValue()) {
			return res; // Wenn sie eh schon gedruckt war machen wir kein Büro mehr auf
		}

		if ((m == null)) {
			invoice.reject(InvoiceState.REJECTCODE.NO_MANDATOR, Messages.Validator_NoMandator);
			CoreModelServiceHolder.get().save(invoice);
			res.add(Result.SEVERITY.ERROR, 2, Messages.Validator_NoMandator, invoice, true);
		}
		ICoverage coverage = invoice.getCoverage();

		if (coverage == null || !CoverageServiceHolder.get().isValid(coverage)) {
			invoice.reject(InvoiceState.REJECTCODE.NO_CASE, Messages.Validator_NoCase);
			CoreModelServiceHolder.get().save(invoice);
			res.add(Result.SEVERITY.ERROR, 4, Messages.Validator_NoCase, invoice, true);
		}

		String ean = TarmedRequirements.getEAN(m);
		if (StringTool.isNothing(ean)) {
			invoice.reject(InvoiceState.REJECTCODE.NO_MANDATOR, Messages.Validator_NoEAN);
			CoreModelServiceHolder.get().save(invoice);
			res.add(Result.SEVERITY.ERROR, 3, Messages.Validator_NoEAN, invoice, true);
		}

		if (invoiceRequest.getPayload().getBody().getTreatment().getDiagnosis().isEmpty()) {
			invoice.reject(InvoiceState.REJECTCODE.NO_DIAG, Messages.Validator_NoDiagnosis);
			CoreModelServiceHolder.get().save(invoice);
			res.add(Result.SEVERITY.ERROR, 8, Messages.Validator_NoDiagnosis, invoice, true);
		}

		IContact costBearer = (coverage != null) ? coverage.getCostBearer() : null;
		// kostentraeger is optional for tiers garant else check if valid
		if (costBearer == null && invoiceRequest.getPayload().getBody().getTiersGarant() != null) {
			return res;
		} else {
			if (costBearer == null) {
				invoice.reject(InvoiceState.REJECTCODE.NO_GUARANTOR, Messages.Validator_NoName);
				CoreModelServiceHolder.get().save(invoice);
				res.add(Result.SEVERITY.ERROR, 7, Messages.Validator_NoName, invoice, true);
				return res;
			}
			ean = TarmedRequirements.getEAN(costBearer);

			if (StringTool.isNothing(ean) || (!ean.matches(TarmedRequirements.EAN_PATTERN))) {
				invoice.reject(InvoiceState.REJECTCODE.NO_GUARANTOR, Messages.Validator_NoEAN2);
				CoreModelServiceHolder.get().save(invoice);
				res.add(Result.SEVERITY.ERROR, 6, Messages.Validator_NoEAN2, invoice, true);
			}
			String bez = costBearer.getDescription1();
			if (StringTool.isNothing(bez)) {
				invoice.reject(InvoiceState.REJECTCODE.NO_GUARANTOR, Messages.Validator_NoName);
				CoreModelServiceHolder.get().save(invoice);
				res.add(Result.SEVERITY.ERROR, 7, Messages.Validator_NoName, invoice, true);
			}
		}
		return res;
	}
}
