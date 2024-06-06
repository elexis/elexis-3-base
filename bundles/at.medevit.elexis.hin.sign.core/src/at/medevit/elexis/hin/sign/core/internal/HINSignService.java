package at.medevit.elexis.hin.sign.core.internal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import at.medevit.elexis.emediplan.core.EMediplanUtil;
import at.medevit.elexis.emediplan.core.model.print.Medication;
import at.medevit.elexis.hin.auth.core.GetAuthCodeWithStateSupplier;
import at.medevit.elexis.hin.auth.core.IHinAuthService;
import at.medevit.elexis.hin.auth.core.IHinAuthUi;
import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.status.ObjectStatus;

@Component
public class HINSignService implements IHinSignService {

	private static Logger logger = LoggerFactory.getLogger(HINSignService.class);

	private Mode mode;

	@Reference
	private IConfigService configService;
	
	@Reference
	private IHinAuthService hinAuthService;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	private IHinAuthUi authUi;
	
	@Reference
	private Gson gson;

	private String currentState;

	@Activate
	public void activate() {
		if (StringUtils.isNotBlank(System.getProperty("hinsign.test"))) {
			setMode(Mode.TEST);
		} else {
			setMode(Mode.PROD);
		}
	}

	@Override
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public ObjectStatus<?> createPrescription(String chmed) {
		Optional<String> adSwissAuthToken = getADSwissAuthToken();
		if (adSwissAuthToken.isPresent()) {
			Optional<String> authHandle = getEPDAuthHandle(adSwissAuthToken.get());
			if(authHandle.isPresent()) {
				CliProcess cliProcess = CliProcess.createPrescription(authHandle.get(), chmed, mode);
				if (cliProcess.execute()) {
					if (cliProcess.getOutput() != null && !cliProcess.getOutput().isEmpty()
							&& cliProcess.getOutput().get(0).startsWith("https://eprescription.hin.ch")) {
						return ObjectStatus.OK(cliProcess.getOutput().get(0));
					}
				} else {
					logger.error("Error executing cli\n["
							+ cliProcess.getOutput().stream().collect(Collectors.joining("\n")) + "]");
					Map<?, ?> map = cliProcess.getOutputAsMap();
					if (map != null) {
						return ObjectStatus.ERROR(map);
					}
					return ObjectStatus.ERROR("Authentication failed");
				}
			}
		}
		return ObjectStatus.ERROR("Authentication failed");
	}

	@Override
	public ObjectStatus<?> verifyPrescription(String chmedUrl) {
		CliProcess cliProcess = CliProcess.verifyPrescription(chmedUrl, mode);
		if (cliProcess.execute()) {
			Map<?, ?> map = cliProcess.getOutputAsMap();
			if (map != null) {
				return ObjectStatus.OK(map);
			}
		} else {
			logger.error(
					"Error executing cli\n[" + cliProcess.getOutput().stream().collect(Collectors.joining("\n")) + "]");
			Map<?, ?> map = cliProcess.getOutputAsMap();
			if (map != null) {
				return ObjectStatus.ERROR(map);
			}
		}
		return ObjectStatus.ERROR("Verification failed");
	}

	@Override
	public ObjectStatus<?> revokePrescription(String chmedId) {
		Optional<String> adSwissAuthToken = getADSwissAuthToken();
		if (adSwissAuthToken.isPresent()) {
			Optional<String> authHandle = getEPDAuthHandle(adSwissAuthToken.get());
			if (authHandle.isPresent()) {
				CliProcess cliProcess = CliProcess.revokePrescription(authHandle.get(), chmedId, mode);
				if (cliProcess.execute()) {
					Map<?, ?> map = cliProcess.getOutputAsMap();
					return ObjectStatus.OK(map);
				} else {
					logger.error("Error executing cli\n["
							+ cliProcess.getOutput().stream().collect(Collectors.joining("\n")) + "]");
					Map<?, ?> map = cliProcess.getOutputAsMap();
					if (map != null) {
						return ObjectStatus.ERROR(map);
					}
					return ObjectStatus.ERROR("Authentication failed");
				}
			}
		}
		return ObjectStatus.ERROR("Authentication failed");
	}

	protected Optional<String> getADSwissAuthToken() {
		if (hinAuthService != null) {
			return hinAuthService.getToken(Collections.singletonMap(IHinAuthService.TOKEN_GROUP,
					mode == Mode.TEST ? "ADSwiss_CI-Test" : "ADSwiss_CI"));
		} else {
			logger.error("No HIN auth service");
		}
		return Optional.empty();
	}

	protected Optional<String> getEPDAuthHandle(String bearerToken) {
		Optional<String> existingHandle = validateEPDAuthHandle(
				configService.getActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE, null));
		if(existingHandle.isEmpty()) {
			existingHandle = getEPDAuthHandle(bearerToken, authUi);
			if (existingHandle.isPresent()) {
				// expires after 12h
				Long expires = System.currentTimeMillis() + ((1000 * 60 * 60) * 12);
				configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE, existingHandle.get());
				configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE_EXPIRES, Long.toString(expires));
			}
		}
		return existingHandle;
	}
	
	private Optional<String> validateEPDAuthHandle(String existingHandle) {
		if (StringUtils.isNotBlank(existingHandle)) {
			String tokenExpires = configService.getActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE_EXPIRES, null);
			if (StringUtils.isNotBlank(tokenExpires)) {
				Long expires = Long.parseLong(tokenExpires);
				if (System.currentTimeMillis() > expires) {
					configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE, null);
					configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE_EXPIRES, null);
				} else {
					return Optional.of(existingHandle);
				}
			} else {
				return Optional.of(existingHandle);
			}
		}
		return Optional.empty();
	}

	protected Optional<String> getEPDAuthHandle(String bearerToken, IHinAuthUi authUi) {
		try {
			URL serverURL = getEPDAuthServiceAuthCodeUrl();
			logger.info("Using EPD auth code url [" + serverURL + "]");
			HttpURLConnection httpConnection = (HttpURLConnection) serverURL.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(false);
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);
			httpConnection.setRequestProperty("accept", "application/json");
			httpConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);

			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 200 && responseCode < 300) {
				InputStream in = httpConnection.getInputStream();
				String encoding = httpConnection.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				String body = IOUtils.toString(in, encoding);
				@SuppressWarnings("rawtypes")
				Map map = gson.fromJson(body, Map.class);
				String epdAuthUrl = (String) map.get("epdAuthUrl");
				logger.info("Got EPD auth url [" + epdAuthUrl + "]");
				if (StringUtils.isNotBlank(epdAuthUrl)) {
					Optional<String> epdAuthCode = getEpdAuthCode(epdAuthUrl, authUi);
					if(epdAuthCode.isPresent()) {
						serverURL = getEPDAuthServiceAuthHandleUrl();
						logger.info("Using EPD auth handle url [" + serverURL + "]");
						httpConnection = (HttpURLConnection) serverURL.openConnection();
						httpConnection.setRequestMethod("POST");
						httpConnection.setDoOutput(true);
						httpConnection.setDoInput(true);
						httpConnection.setUseCaches(false);
						httpConnection.setRequestProperty("accept", "application/json");
						httpConnection.setRequestProperty("Content-Type", "application/json");
						httpConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);
						
						PrintWriter out = new PrintWriter(httpConnection.getOutputStream());
						out.println(gson.toJson(Collections.singletonMap("authCode", epdAuthCode.get())));
						out.close();

						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(httpConnection.getInputStream()));
						String line;
						StringBuffer responseMessage = new StringBuffer();
						while ((line = bufferedReader.readLine()) != null) {
							responseMessage.append(line);
						}
						bufferedReader.close();
						if(StringUtils.isNotBlank(responseMessage)) {
							@SuppressWarnings("rawtypes")
							Map respondeMap = gson.fromJson(responseMessage.toString(), Map.class);
							if(respondeMap.containsKey("authHandle")) {
								return Optional.ofNullable((String)respondeMap.get("authHandle"));
							}
						}
					} else {
						logger.warn("Failed to get EPD auth code");
					}
				}
			} else {
				logger.warn("Failed to get EPD auth handle response code [" + responseCode + "]");
			}
		} catch (Exception e) {
			logger.warn("Failed to get EPD auth handle", e);
			if (hinAuthService != null) {
				Optional<String> message = hinAuthService.handleException(e, Collections.singletonMap(
						IHinAuthService.TOKEN_GROUP, mode == Mode.TEST ? "ADSwiss_CI-Test" : "ADSwiss_CI"));
				if (message.isPresent()) {
					logger.warn("HIN Auth message", message.get());
				}
			}
		}
		return Optional.empty();
	}

	private URL getEPDAuthServiceAuthCodeUrl() throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append(getADSwissAuthServiceBaseUrl() + "EPDAuth");
		sb.append("?targetUrl=");
		sb.append(URLEncoder.encode(getRedirectUri() + "/" + getCurrentState(true), StandardCharsets.UTF_8));
		sb.append("&style=redirect");
		return new URL(sb.toString());
	}

	private URL getEPDAuthServiceAuthHandleUrl() throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append(getADSwissAuthServiceBaseUrl() + "EPDAuth/auth_handle");
		return new URL(sb.toString());
	}

	private String getADSwissAuthServiceBaseUrl() {
		return mode == Mode.TEST ? "https://oauth2.ci-prep.adswiss.hin.ch/authService/"
				: "https://oauth2.ci.adswiss.hin.ch/authService/";
	}

	private Optional<String> getEpdAuthCode(String epdAuthUrl, IHinAuthUi iHinAuthUi) {
		iHinAuthUi.openBrowser(epdAuthUrl);
		Object value = iHinAuthUi.getWithCancelableProgress("HIN Berechtigung im Browser bestätigen.",
				new GetAuthCodeWithStateSupplier(getCurrentState(false)));
		if (value instanceof String) {
			return Optional.of((String) value);
		}
		return Optional.empty();
	}
	
	private String getCurrentState(boolean refresh) {
		if (refresh) {
			currentState = UUID.randomUUID().toString();
		}
		return currentState;
	}
	
	private String getRedirectUri() {
		return "https://tools.medelexis.ch/hin/ac";
	}

	public Optional<String> getChmedId(String encodedChmed) {
		if (StringUtils.isNotBlank(encodedChmed)) {
			String decodedChmed = EMediplanUtil.getDecodedJsonString(encodedChmed);
			Map<?, ?> chmedMap = gson.fromJson(decodedChmed, Map.class);
			if (chmedMap != null) {
				return Optional.ofNullable((String) chmedMap.get("Id"));
			}
		}
		return Optional.empty();
	}

	public boolean isPrescriptionExists(ObjectStatus<?> status) {
		if (status.get() instanceof Map) {
			Map<?, ?> statusMap = (Map<?, ?>) status.get();
			return statusMap.get("error_code") != null
					&& statusMap.get("error_code").equals("prescription_already_exists");
		}
		return false;
	}

	@Override
	public void setPrescriptionUrl(IRecipe iRecipe, String url) {
		IBlob blob = getOrCreateBlob(iRecipe);
		Map<Object, Object> map = blob.getMapContent();
		if (map.isEmpty()) {
			map = new Hashtable<>();
		}
		map.put("url", url);
		blob.setMapContent(map);
		CoreModelServiceHolder.get().save(blob);
	}

	@Override
	public Optional<String> getPrescriptionUrl(IRecipe iRecipe) {
		IBlob blob = getBlob(iRecipe);
		if(blob != null) {
			return Optional.ofNullable((String) blob.getMapContent().get("url"));
		}
		return Optional.empty();
	}

	private IBlob getBlob(IRecipe iRecipe) {
		return CoreModelServiceHolder.get().load(iRecipe.getId(), IBlob.class).orElse(null);
	}

	private IBlob getOrCreateBlob(IRecipe iRecipe) {
		IBlob blob = CoreModelServiceHolder.get().load(iRecipe.getId(), IBlob.class).orElse(null);
		if (blob == null) {
			blob = CoreModelServiceHolder.get().create(IBlob.class);
			blob.setId(iRecipe.getId());
		}
		CoreModelServiceHolder.get().save(blob);
		return blob;
	}

	@Override
	public ObjectStatus<?> exportPrescriptionPdf(IRecipe iRecipe, OutputStream output) {
		Optional<String> url = getPrescriptionUrl(iRecipe);
		if(url.isPresent()) {
			Optional<Image> qrCode = getQrCode(url.get());
			
			at.medevit.elexis.emediplan.core.model.print.Medication medication = at.medevit.elexis.emediplan.core.model.print.Medication
					.fromPrescriptions(iRecipe.getMandator(), iRecipe.getPatient(), iRecipe.getPrescriptions());
			try {
				createPdf(qrCode, medication, output);
			} catch (Exception e) {
				LoggerFactory.getLogger(getClass()).error("Failed to create eprescription pdf", e);
				return ObjectStatus.ERROR("Failed to create eprescription pdf");
			}
			return ObjectStatus.OK("Created eprescription to output", url.get());
		}
		return ObjectStatus.ERROR("No eprescription url set");
	}

	private void createPdf(Optional<Image> qrCode, Medication medication, OutputStream output) {
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IFormattedOutputFactory> fopFactoryRef = bundleContext
				.getServiceReference(IFormattedOutputFactory.class);
		if (fopFactoryRef != null) {
			IFormattedOutputFactory fopFactory = bundleContext.getService(fopFactoryRef);
			IFormattedOutput foOutput = fopFactory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF);
			HashMap<String, String> parameters = new HashMap<>();
			qrCode.ifPresent(qr -> {
				parameters.put("qrJpeg", getEncodedQr(qr)); //$NON-NLS-1$
			});
			foOutput.transform(medication, HINSignService.class.getResourceAsStream("/rsc/xslt/eprescription.xslt"), //$NON-NLS-1$
					output, parameters);
			bundleContext.ungetService(fopFactoryRef);
		} else {
			throw new IllegalStateException("No IFormattedOutputFactory available"); //$NON-NLS-1$
		}
	}

	private String getEncodedQr(Image qr) {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { qr.getImageData() };
			imageLoader.compression = 100;
			imageLoader.save(output, SWT.IMAGE_JPEG);
			return "data:image/jpg;base64," + Base64.getEncoder().encodeToString(output.toByteArray()); //$NON-NLS-1$
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error encoding QR", e); //$NON-NLS-1$
		}
		return StringUtils.EMPTY;
	}

	protected Optional<Image> getQrCode(String prescriptionUrl) {
		Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(prescriptionUrl, BarcodeFormat.QR_CODE, 470, 470, hintMap);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();

			ImageData data = new ImageData(width, height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					data.setPixel(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
				}
			}
			return Optional.of(new Image(Display.getDefault(), data));
		} catch (WriterException e) {
			LoggerFactory.getLogger(getClass()).error("Error creating QR", e); //$NON-NLS-1$
			return Optional.empty();
		}
	}
}
