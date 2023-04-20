package ch.elexis.covid.cert.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.xml.type.internal.DataValue.Base64;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.covid.cert.service.CertificateInfo.Type;
import ch.elexis.covid.cert.service.rest.CovidCertificateApi;
import ch.elexis.covid.cert.service.rest.model.RecoveryModel;
import ch.elexis.covid.cert.service.rest.model.RevokeModel;
import ch.elexis.covid.cert.service.rest.model.SuccessResponse;
import ch.elexis.covid.cert.service.rest.model.TestModel;
import ch.elexis.covid.cert.service.rest.model.VaccinationModel;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

@Component(service = CertificatesService.class)
public class CertificatesService {

	public static final String CFG_DEFAULT_VACCPRODUCT = "ch.elexis.covid.cert/default/vaccproductcode";
	public static final String CFG_DEFAULT_TESTPRODUCT = "ch.elexis.covid.cert/default/testproductcode";

	private static String CFG_MODE = "ch.elexis.covid.cert/mode";

	public static String CFG_TESTCENTERNAME = "ch.elexis.covid.cert/testcentername";

	public static String CFG_OTP = "ch.elexis.covid.cert/otp";
	public static String CFG_OTP_TIMESTAMP = "ch.elexis.covid.cert/otptimestamp";

	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	private IDocumentStore omnivoreDocumentStore;

	@Reference
	private IConfigService configService;

	private CovidCertificateApi covidCertificateApi;

	private Properties keyProperties;

	public enum Mode {
		PROD("https://ws.covidcertificate.bag.admin.ch"), TEST("https://ws.covidcertificate-a.bag.admin.ch");

		private String url;

		Mode(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}
	}

	@Activate
	private void activate() {
		// test if a fragment with keys is available
		InputStream propertiesStream = getClass().getResourceAsStream("/rsc/keys.properties");
		if (propertiesStream != null) {
			keyProperties = new Properties();
			try {
				keyProperties.load(propertiesStream);
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error loading keys properties", e);
			}
		} else {
			LoggerFactory.getLogger(getClass()).error("No keys properties fragement available");
		}
		covidCertificateApi = new CovidCertificateApi(getMode(), keyProperties);
	}

	public boolean isOtpSet() {
		return configService.getActiveMandator(CFG_OTP, null) != null;
	}

	public String getOtp() {
		return configService.getActiveMandator(CFG_OTP, StringUtils.EMPTY);
	}

	/**
	 * Use a {@link CovidCertificateApi} instance to get a new vaccination
	 * certificate from the rest API. If ok the {@link Result} contains the uvci of
	 * the created {@link CertificateInfo}.
	 *
	 *
	 * @param patient
	 * @param model
	 * @return
	 */
	public Result<String> createVaccinationCertificate(IPatient patient, VaccinationModel model) {
		Object result = covidCertificateApi.vaccination(model);
		if (result instanceof SuccessResponse) {
			try {
				String documentId = pdfToOmnivore(patient, Type.VACCINATION, (SuccessResponse) result);
				CertificateInfo.add(Type.VACCINATION, LocalDateTime.now(), documentId, ((SuccessResponse) result).uvci,
						patient);
				Result<String> ret = Result.OK(((SuccessResponse) result).uvci);

				if (((SuccessResponse) result).appDeliveryError != null
						&& ((SuccessResponse) result).appDeliveryError.errorMessage != null) {
					ret.addMessage(SEVERITY.OK,
							"App Transfer Fehler\n\n" + ((SuccessResponse) result).appDeliveryError.errorMessage);
				}
				return ret;
			} catch (ElexisException e) {
				LoggerFactory.getLogger(getClass()).error("Error saving vaccination cert pdf", e);
				return new Result<String>(SEVERITY.ERROR, 0, (String) e.getMessage(), (String) e.getMessage(), false);
			}
		} else {
			return new Result<String>(SEVERITY.ERROR, 0, (String) result, (String) result, false);
		}
	}

	/**
	 * Use a {@link CovidCertificateApi} instance to get a new test certificate from
	 * the rest API. If ok the {@link Result} contains the uvci of the created
	 * {@link CertificateInfo}.
	 *
	 * @param patient
	 * @param model
	 * @return
	 */
	public Result<String> createTestCertificate(IPatient patient, TestModel model) {
		Object result = covidCertificateApi.test(model);
		if (result instanceof SuccessResponse) {
			try {
				String documentId = pdfToOmnivore(patient, Type.TEST, (SuccessResponse) result);
				CertificateInfo.add(Type.TEST, LocalDateTime.now(), documentId, ((SuccessResponse) result).uvci,
						patient);
				Result<String> ret = Result.OK(((SuccessResponse) result).uvci);

				if (((SuccessResponse) result).appDeliveryError != null
						&& ((SuccessResponse) result).appDeliveryError.errorMessage != null) {
					ret.addMessage(SEVERITY.OK,
							"App Transfer Fehler\n\n" + ((SuccessResponse) result).appDeliveryError.errorMessage);
				}
				return ret;
			} catch (ElexisException e) {
				LoggerFactory.getLogger(getClass()).error("Error saving test cert pdf", e);
				return new Result<String>(SEVERITY.ERROR, 0, (String) e.getMessage(), (String) e.getMessage(), false);
			}
		} else {
			return new Result<String>(SEVERITY.ERROR, 0, (String) result, (String) result, false);
		}
	}

	public Result<String> createRecoveryCertificate(IPatient patient, RecoveryModel model) {
		Object result = covidCertificateApi.recovery(model);
		if (result instanceof SuccessResponse) {
			try {
				String documentId = pdfToOmnivore(patient, Type.RECOVERY, (SuccessResponse) result);
				CertificateInfo.add(Type.RECOVERY, LocalDateTime.now(), documentId, ((SuccessResponse) result).uvci,
						patient);
				Result<String> ret = Result.OK(((SuccessResponse) result).uvci);

				if (((SuccessResponse) result).appDeliveryError != null
						&& ((SuccessResponse) result).appDeliveryError.errorMessage != null) {
					ret.addMessage(SEVERITY.OK,
							"App Transfer Fehler\n\n" + ((SuccessResponse) result).appDeliveryError.errorMessage);
				}
				return ret;
			} catch (ElexisException e) {
				LoggerFactory.getLogger(getClass()).error("Error saving recovery cert pdf", e);
				return new Result<String>(SEVERITY.ERROR, 0, (String) e.getMessage(), (String) e.getMessage(), false);
			}
		} else {
			return new Result<String>(SEVERITY.ERROR, 0, (String) result, (String) result, false);
		}
	}

	/**
	 * Use a {@link CovidCertificateApi} instance to revoke a certificate from the
	 * rest API.
	 *
	 * @param model
	 * @return
	 */
	public Result<String> revokeCertificate(IPatient patient, CertificateInfo info, RevokeModel model) {
		Object result = covidCertificateApi.revoke(model);
		if (result != null) {
			return new Result<String>(SEVERITY.ERROR, 0, (String) result, (String) result, false);
		} else {
			java.util.Optional<IDocument> document = omnivoreDocumentStore.loadDocument(info.getDocumentId());
			if (document.isPresent()) {
				omnivoreDocumentStore.removeDocument(document.get());
			}
			CertificateInfo.remove(info, patient);
			return Result.OK();
		}
	}

	private String pdfToOmnivore(IPatient patient, Type type, SuccessResponse result) throws ElexisException {
		ICategory category = omnivoreDocumentStore.createCategory("COVID Zertifikate");
		byte[] pdfBytes = Base64.decode(result.pdf);
		IDocument document = omnivoreDocumentStore.createDocument(patient.getId(),
				"COVID " + type.getLabel() + " Zertifikat", category.getName());
		document.setMimeType("pdf");
		omnivoreDocumentStore.saveDocument(document, new ByteArrayInputStream(pdfBytes));
		return document.getId();
	}

	public Mode getMode() {
		String modeName = configService.get(CFG_MODE, "PROD");
		return Mode.valueOf(modeName);
	}

	public void setMode(Mode mode) {
		configService.set(CFG_MODE, mode.name());
		// create a new api instance for the mode
		covidCertificateApi = new CovidCertificateApi(getMode(), keyProperties);
	}

	public String getOtpUrl() {
		if (getMode() == Mode.PROD) {
			return "https://www.covidcertificate.admin.ch/";
		} else {
			return "https://www.covidcertificate-a.admin.ch/";
		}
	}
}
