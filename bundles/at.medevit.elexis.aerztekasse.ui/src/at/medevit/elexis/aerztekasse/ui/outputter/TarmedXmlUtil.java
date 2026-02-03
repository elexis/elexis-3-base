package at.medevit.elexis.aerztekasse.ui.outputter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.aerztekasse.core.IAerztekasseService;
import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.XMLExporterUtil;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.FallConstants;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.ICoverageService.Tiers;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.fd.invoice450.request.DebitorAddressType;
import ch.fd.invoice450.request.GuarantorAddressType;
import ch.fd.invoice450.request.OnlineAddressType;
import ch.fd.invoice450.request.PatientAddressType;
import ch.fd.invoice450.request.TelecomAddressType;
import ch.fd.invoice500.request.RequestType;

public class TarmedXmlUtil {

	/**
	 * Update the tarmed xml {@link Document} of the {@link IInvoice} with
	 * aerztkasse specific information.
	 *
	 * @param invoice
	 * @param document
	 * @return
	 */
	public static Optional<Document> updateAerztekasseInfo(IInvoice invoice, Document document) {
		String version = TarmedJaxbUtil.getXMLVersion(document);
		if ("4.5".equals(version)) {
			ch.fd.invoice450.request.RequestType invoiceRequest = TarmedJaxbUtil.unmarshalInvoiceRequest450(document);

			invoiceRequest.getProcessing().getTransport().getVia().get(0).setVia(IAerztekasseService.AERZTEKASSE_GLN);
			if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
				invoiceRequest.getProcessing().setPrintCopyToGuarantor(true);
			}

			if (isBillingLaw(invoice, BillingLaw.KVG) && isCopyToPatient(invoiceRequest)
					&& isNoElectronicDelivery(invoice)) {
				setElectronicDelivery(invoiceRequest, false);
			} else if (isGuarantorLegalGuardian(invoice)) {
				updateFieldsIfMissingGuarantor(invoice, invoiceRequest, invoice.getCoverage().getPatient());
			}

			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
			if (TarmedJaxbUtil.marshallInvoiceRequest(invoiceRequest, xmlOutput)) {
				SAXBuilder builder = new SAXBuilder();
				try {
					return Optional.of(builder.build(new StringReader(xmlOutput.toString())));
				} catch (IOException | JDOMException e) {
					LoggerFactory.getLogger(TarmedXmlUtil.class).error("Error loading existing xml document", e);
				}
			}
		} else if ("5.0".equals(version)) {
			ch.fd.invoice500.request.RequestType invoiceRequest = TarmedJaxbUtil.unmarshalInvoiceRequest500(document);

			invoiceRequest.getProcessing().getTransport().getVia().get(0).setVia(IAerztekasseService.AERZTEKASSE_GLN);
			if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
				invoiceRequest.getProcessing().setPrintCopyToGuarantor(true);
			}

			if (isBillingLaw(invoice, BillingLaw.KVG) && isCopyToPatient(invoiceRequest)
					&& isNoElectronicDelivery(invoice)) {
				setElectronicDelivery(invoiceRequest, false);
			} else if (isGuarantorLegalGuardian(invoice)) {
				updateFieldsIfMissingGuarantor(invoice, invoiceRequest, invoice.getCoverage().getPatient());
			}

			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
			if (TarmedJaxbUtil.marshallInvoiceRequest(invoiceRequest, xmlOutput)) {
				SAXBuilder builder = new SAXBuilder();
				try {
					return Optional.of(builder.build(new StringReader(xmlOutput.toString())));
				} catch (IOException | JDOMException e) {
					LoggerFactory.getLogger(TarmedXmlUtil.class).error("Error loading existing xml document", e);
				}
			}
		}
		return Optional.empty();
	}
	
	private static void updateFieldsIfMissingGuarantor(IInvoice invoice, RequestType invoiceRequest, IContact contact) {
		ch.fd.invoice500.request.GuarantorAddressType guarantor = null;
		if (invoiceRequest.getPayload().getBody().getTiersGarant() != null) {
			guarantor = invoiceRequest.getPayload().getBody().getTiersGarant().getGuarantor();
		} else if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
			guarantor = invoiceRequest.getPayload().getBody().getTiersPayant().getGuarantor();
		}
		if (guarantor != null && guarantor.getPerson() != null) {
			ch.fd.invoice500.request.TelecomAddressType guarantorTelcom = guarantor.getPerson().getTelecom();
			if (StringUtils.isNotBlank(contact.getMobile())
					&& (guarantorTelcom == null || guarantorTelcom.getPhone().isEmpty())) {
				if (guarantorTelcom == null) {
					guarantorTelcom = new ch.fd.invoice500.request.TelecomAddressType();
					guarantor.getPerson().setTelecom(guarantorTelcom);
				}
				guarantorTelcom.getPhone().add(StringUtils.abbreviate(contact.getMobile(), 25));
			}
			ch.fd.invoice500.request.OnlineAddressType guarantorOnline = guarantor.getPerson().getOnline();
			if (StringUtils.isNotBlank(contact.getEmail())
					&& (guarantorOnline == null || guarantorOnline.getEmail().isEmpty())) {
				if (guarantorOnline == null) {
					guarantorOnline = new ch.fd.invoice500.request.OnlineAddressType();
					guarantor.getPerson().setOnline(guarantorOnline);
				}
				String email = XMLExporterUtil.getValidXMLString(StringUtils.left(contact.getEmail(), 70));
				if (!email.matches(".+@.+")) { //$NON-NLS-1$
					email = "mail@invalid.invalid"; //$NON-NLS-1$
				}
				guarantorOnline.getEmail().add(email);
			}
		}
	}

	private static void updateFieldsIfMissingGuarantor(IInvoice invoice,
			ch.fd.invoice450.request.RequestType invoiceRequest, IContact contact) {
		GuarantorAddressType guarantor = null;
		if (invoiceRequest.getPayload().getBody().getTiersGarant() != null) {
			guarantor = invoiceRequest.getPayload().getBody().getTiersGarant().getGuarantor();
		} else if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
			guarantor = invoiceRequest.getPayload().getBody().getTiersPayant().getGuarantor();
		}
		if (guarantor != null && guarantor.getPerson() != null) {
			TelecomAddressType guarantorTelcom = guarantor.getPerson().getTelecom();
			if (StringUtils.isNotBlank(contact.getMobile())
					&& (guarantorTelcom == null || guarantorTelcom.getPhone().isEmpty())) {
				if (guarantorTelcom == null) {
					guarantorTelcom = new TelecomAddressType();
					guarantor.getPerson().setTelecom(guarantorTelcom);
				}
				guarantorTelcom.getPhone().add(StringUtils.abbreviate(contact.getMobile(), 25));
			}
			OnlineAddressType guarantorOnline = guarantor.getPerson().getOnline();
			if (StringUtils.isNotBlank(contact.getEmail())
					&& (guarantorOnline == null || guarantorOnline.getEmail().isEmpty())) {
				if (guarantorOnline == null) {
					guarantorOnline = new OnlineAddressType();
					guarantor.getPerson().setOnline(guarantorOnline);
				}
				String email = XMLExporterUtil.getValidXMLString(StringUtils.left(contact.getEmail(), 70));
				if (!email.matches(".+@.+")) { //$NON-NLS-1$
					email = "mail@invalid.invalid"; //$NON-NLS-1$
				}
				guarantorOnline.getEmail().add(email);
			}
		}
	}

	private static boolean isCopyToPatient(ch.fd.invoice450.request.RequestType invoiceRequest) {
		if (invoiceRequest.getProcessing() != null) {
			return invoiceRequest.getProcessing().isPrintCopyToGuarantor();
		}
		return false;
	}

	private static boolean isCopyToPatient(ch.fd.invoice500.request.RequestType invoiceRequest) {
		if (invoiceRequest.getProcessing() != null) {
			return invoiceRequest.getProcessing().isPrintCopyToGuarantor();
		}
		return false;
	}

	private static void setElectronicDelivery(ch.fd.invoice450.request.RequestType invoiceRequest, boolean value) {
		if (!value) {
			// remove telcom and email if no electronic delivery should be performed
			if (invoiceRequest.getPayload().getBody().getTiersGarant() != null) {
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getDebitor() != null) {
					DebitorAddressType debitor = invoiceRequest.getPayload().getBody().getTiersGarant().getDebitor();
					setTelcomNull(debitor);
					setOnlineNull(debitor);
				}
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getPatient() != null) {
					PatientAddressType patient = invoiceRequest.getPayload().getBody().getTiersGarant().getPatient();
					setTelcomNull(patient);
					setOnlineNull(patient);
				}
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getGuarantor() != null) {
					GuarantorAddressType guarantor = invoiceRequest.getPayload().getBody().getTiersGarant()
							.getGuarantor();
					setTelcomNull(guarantor);
					setOnlineNull(guarantor);
				}
			} else if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getDebitor() != null) {
					DebitorAddressType debitor = invoiceRequest.getPayload().getBody().getTiersPayant().getDebitor();
					setTelcomNull(debitor);
					setOnlineNull(debitor);
				}
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getPatient() != null) {
					PatientAddressType patient = invoiceRequest.getPayload().getBody().getTiersPayant().getPatient();
					setTelcomNull(patient);
					setOnlineNull(patient);
				}
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getGuarantor() != null) {
					GuarantorAddressType guarantor = invoiceRequest.getPayload().getBody().getTiersPayant()
							.getGuarantor();
					setTelcomNull(guarantor);
					setOnlineNull(guarantor);
				}
			}
		}
	}

	private static void setElectronicDelivery(ch.fd.invoice500.request.RequestType invoiceRequest, boolean value) {
		if (!value) {
			// remove telcom and email if no electronic delivery should be performed
			if (invoiceRequest.getPayload().getBody().getTiersGarant() != null) {
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getDebitor() != null) {
					ch.fd.invoice500.request.DebitorAddressType debitor = invoiceRequest.getPayload().getBody()
							.getTiersGarant().getDebitor();
					setTelcomNull(debitor);
					setOnlineNull(debitor);
				}
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getPatient() != null) {
					ch.fd.invoice500.request.PatientAddressType patient = invoiceRequest.getPayload().getBody()
							.getTiersGarant().getPatient();
					setTelcomNull(patient);
					setOnlineNull(patient);
				}
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getGuarantor() != null) {
					ch.fd.invoice500.request.GuarantorAddressType guarantor = invoiceRequest.getPayload().getBody()
							.getTiersGarant()
							.getGuarantor();
					setTelcomNull(guarantor);
					setOnlineNull(guarantor);
				}
			} else if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getDebitor() != null) {
					ch.fd.invoice500.request.DebitorAddressType debitor = invoiceRequest.getPayload().getBody()
							.getTiersPayant().getDebitor();
					setTelcomNull(debitor);
					setOnlineNull(debitor);
				}
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getPatient() != null) {
					ch.fd.invoice500.request.PatientAddressType patient = invoiceRequest.getPayload().getBody()
							.getTiersPayant().getPatient();
					setTelcomNull(patient);
					setOnlineNull(patient);
				}
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getGuarantor() != null) {
					ch.fd.invoice500.request.GuarantorAddressType guarantor = invoiceRequest.getPayload().getBody()
							.getTiersPayant()
							.getGuarantor();
					setTelcomNull(guarantor);
					setOnlineNull(guarantor);
				}
			}
		}
	}

	private static void setOnlineNull(ch.fd.invoice500.request.GuarantorAddressType guarantor) {
		if (guarantor.getPerson() != null) {
			guarantor.getPerson().setOnline(null);
		}
	}

	private static void setTelcomNull(ch.fd.invoice500.request.GuarantorAddressType guarantor) {
		if (guarantor.getPerson() != null) {
			guarantor.getPerson().setTelecom(null);
		}
	}

	private static void setOnlineNull(ch.fd.invoice500.request.PatientAddressType patient) {
		if (patient.getPerson() != null) {
			patient.getPerson().setOnline(null);
		}
	}

	private static void setTelcomNull(ch.fd.invoice500.request.PatientAddressType patient) {
		if (patient.getPerson() != null) {
			patient.getPerson().setTelecom(null);
		}
	}

	private static void setOnlineNull(ch.fd.invoice500.request.DebitorAddressType debitor) {
		if (debitor.getCompany() != null) {
			debitor.getCompany().setOnline(null);
		} else if (debitor.getPerson() != null) {
			debitor.getPerson().setOnline(null);
		}
	}

	private static void setTelcomNull(ch.fd.invoice500.request.DebitorAddressType debitor) {
		if (debitor.getCompany() != null) {
			debitor.getCompany().setTelecom(null);
		} else if (debitor.getPerson() != null) {
			debitor.getPerson().setTelecom(null);
		}
	}

	private static void setOnlineNull(GuarantorAddressType guarantor) {
		if (guarantor.getPerson() != null) {
			guarantor.getPerson().setOnline(null);
		}
	}

	private static void setTelcomNull(GuarantorAddressType guarantor) {
		if (guarantor.getPerson() != null) {
			guarantor.getPerson().setTelecom(null);
		}
	}

	private static void setOnlineNull(PatientAddressType patient) {
		if (patient.getPerson() != null) {
			patient.getPerson().setOnline(null);
		}
	}

	private static void setTelcomNull(PatientAddressType patient) {
		if (patient.getPerson() != null) {
			patient.getPerson().setTelecom(null);
		}
	}

	private static void setOnlineNull(DebitorAddressType debitor) {
		if (debitor.getCompany() != null) {
			debitor.getCompany().setOnline(null);
		} else if (debitor.getPerson() != null) {
			debitor.getPerson().setOnline(null);
		}
	}

	private static void setTelcomNull(DebitorAddressType debitor) {
		if (debitor.getCompany() != null) {
			debitor.getCompany().setTelecom(null);
		} else if (debitor.getPerson() != null) {
			debitor.getPerson().setTelecom(null);
		}
	}

	private static boolean isBillingLaw(IInvoice invoice, BillingLaw law) {
		if (invoice.getCoverage() != null && invoice.getCoverage().getBillingSystem() != null
				&& invoice.getCoverage().getBillingSystem().getLaw() != null) {
			return invoice.getCoverage().getBillingSystem().getLaw() == law;
		}
		return false;
	}

	private static boolean isNoElectronicDelivery(IInvoice invoice) {
		if (invoice.getCoverage() != null) {
			return StringConstants.ONE
					.equals(invoice.getCoverage().getExtInfo(FallConstants.FLD_EXT_NO_ELECTRONIC_DELIVERY));
		}
		return false;
	}

	private static boolean isGuarantorLegalGuardian(IInvoice invoice) {
		if (invoice.getCoverage().getPatient().getLegalGuardian() != null) {
			IContact guardian = invoice.getCoverage().getPatient().getLegalGuardian();
			Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
			IContact guarantor = ch.elexis.TarmedRechnung.XMLExporterUtil.getGuarantor(tiersType.getShortName(),
					invoice.getCoverage().getPatient(), invoice.getCoverage());
			return guarantor != null && guarantor.getId().equals(guardian.getId());
		}
		return false;
	}
}
