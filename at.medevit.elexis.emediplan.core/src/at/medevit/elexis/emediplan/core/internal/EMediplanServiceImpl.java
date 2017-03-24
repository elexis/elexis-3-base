/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;

@Component
public class EMediplanServiceImpl implements EMediplanService {
	
	private Gson gson;
	
	public EMediplanServiceImpl(){
		gson = new GsonBuilder().create();
	}
	
	@Override
	public void exportEMediplanPdf(Mandant author, Patient patient,
		List<Prescription> prescriptions, OutputStream output){
		if (prescriptions != null && !prescriptions.isEmpty() && output != null) {
			Optional<String> jsonString = getJsonString(author, patient, prescriptions);
			Optional<Image> qrCode =
				jsonString.map(json -> getQrCode(json)).orElse(Optional.empty());
			
			Optional<at.medevit.elexis.emediplan.core.model.print.Medication> jaxbModel =
				getJaxbModel(author, patient, prescriptions);
			jaxbModel.ifPresent(model -> {
				createPdf(qrCode, model, output);
			});
		}
	}
	
	private void createPdf(Optional<Image> qrCode, Object jaxbModel, OutputStream output){
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IFormattedOutputFactory> fopFactoryRef =
			bundleContext.getServiceReference(IFormattedOutputFactory.class);
		if (fopFactoryRef != null) {
			IFormattedOutputFactory fopFactory = bundleContext.getService(fopFactoryRef);
			IFormattedOutput foOutput =
				fopFactory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF);
			HashMap<String, String> parameters = new HashMap<>();
			parameters.put("logoJpeg", getEncodedLogo());
			qrCode.ifPresent(qr -> {
				parameters.put("qrJpeg", getEncodedQr(qr));
			});
			foOutput.transform(jaxbModel,
				EMediplanServiceImpl.class.getResourceAsStream("/rsc/xslt/emediplan.xslt"), output,
				parameters);
			bundleContext.ungetService(fopFactoryRef);
		} else {
			throw new IllegalStateException("No IFormattedOutputFactory available");
		}
	}
	
	private String getEncodedQr(Image qr){
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] {
				qr.getImageData()
			};
			imageLoader.compression = 95;
			imageLoader.save(output, SWT.IMAGE_JPEG);
			return "data:image/jpg;base64,"
				+ Base64.getEncoder().encodeToString(output.toByteArray());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error encoding QR", e);
		}
		return "";
	}
	
	private String getEncodedLogo(){
		try(InputStream input =  getClass().getResourceAsStream("/rsc/img/Logo_Full.jpeg"); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			IOUtils.copy(input, output);
			return "data:image/jpg;base64,"
				+ Base64.getEncoder().encodeToString(output.toByteArray());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error encoding logo", e);
		}
		return "";
	}
	
	protected Optional<Image> getQrCode(@NonNull String json){
		String encodedJson = getEncodedJson(json);
		
		Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			BitMatrix bitMatrix =
				qrCodeWriter.encode(encodedJson, BarcodeFormat.QR_CODE, 150, 150, hintMap);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();
			
			ImageData data =
				new ImageData(width, height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					data.setPixel(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
				}
			}
			return Optional.of(new Image(Display.getDefault(), data));
		} catch (WriterException e) {
			LoggerFactory.getLogger(getClass()).error("Error creating QR", e);
			return Optional.empty();
		}
	}
	
	/**
	 * Get the encoded (Header with zipped and Base64 encoded content) String. The header of the
	 * current CHMED Version is added to the resulting String.
	 * 
	 * @param json
	 * @return
	 */
	protected String getEncodedJson(@NonNull String json){
		StringBuilder sb = new StringBuilder();
		// header for compresses json
		sb.append("CHMED16A1");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
			gzip.write(json.getBytes());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error encoding json", e);
			throw new IllegalStateException("Error encoding json", e);
		}
		sb.append(Base64.getEncoder().encodeToString(out.toByteArray()));
		return sb.toString();
	}
	
	/**
	 * Get the decoded String, from the zipped and Base64 encoded String. The first 9 characters
	 * (CHMED header) are ignored.
	 * 
	 * @param encodedJson
	 * @return
	 */
	protected String getDecodedJsonString(@NonNull String encodedJson){
		String content = encodedJson.substring(9);
		byte[] zipped = Base64.getDecoder().decode(content);
		StringBuilder sb = new StringBuilder();
		try {
			GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(zipped));
			InputStreamReader reader = new InputStreamReader(gzip);
			BufferedReader in = new BufferedReader(reader);
			// Probably only single json line, but just to be sure ... 
			String read;
			while ((read = in.readLine()) != null) {
				sb.append(read);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error decoding json", e);
			throw new IllegalStateException("Error decoding json", e);
		}
		return sb.toString();
	}
	
	protected Optional<at.medevit.elexis.emediplan.core.model.print.Medication> getJaxbModel(
		Mandant author, Patient patient, List<Prescription> prescriptions){
		at.medevit.elexis.emediplan.core.model.print.Medication medication =
			at.medevit.elexis.emediplan.core.model.print.Medication.fromPrescriptions(author,
				patient, prescriptions);
		return Optional.ofNullable(medication);
	}
	
	protected Optional<String> getJsonString(Mandant author, Patient patient,
		List<Prescription> prescriptions){
		Medication medication = Medication.fromPrescriptions(author, patient, prescriptions);
		return Optional.ofNullable(gson.toJson(medication));
	}
}
