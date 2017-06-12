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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.core.model.chmed16a.Posology;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.elexis.core.ui.exchange.KontaktMatcher;
import ch.elexis.core.ui.exchange.KontaktMatcher.CreateMode;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

@Component
public class EMediplanServiceImpl implements EMediplanService {
	private static Logger logger = LoggerFactory.getLogger(EMediplanServiceImpl.class);
	
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
			imageLoader.compression = 100;
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
				qrCodeWriter.encode(encodedJson, BarcodeFormat.QR_CODE, 470, 470, hintMap);
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
		byte[] zipped = Base64.getMimeDecoder().decode(content);
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
	
	@Override
	public Medication createModelFromChunk(String chunk){
		String json = getDecodedJsonString(chunk);
		if (chunk.length() > 8) {
			logger.debug("json version: " + chunk.substring(5, 8));
			GsonBuilder gb = new GsonBuilder();
			gb.registerTypeAdapter(Medication.class, new MedicationDeserializer());
			Gson g = gb.create();
			return g.fromJson(json, Medication.class);
		}
		else {
			logger.error("invalid json length - cannot parseable");
		}
		
		return null;
	}
	
	public void addExistingArticlesToMedication(Medication medication){
		if (medication != null) {
			findPatientForMedication(medication);
			List<Medicament> medicaments = new ArrayList<>();
			if (medication.Medicaments != null) {
				for (Medicament in : medication.Medicaments) {
					Medicament toAdd = in;
					if (in.Pos != null) {
						if (in.Pos.size() > 1) {
							// because of the flat representation of all medicaments
							// for each posology entry a copy of that medicament are created
							List<Posology> posologies = new ArrayList<>(in.Pos);
							for (Posology p : posologies) {
								try {
									Gson gson = new Gson();
									String json = gson.toJson(in);
									toAdd = gson.fromJson(json, Medicament.class);
									toAdd.Pos = new ArrayList<>();
									toAdd.Pos.add(p);
								} catch (Exception e) {
									logger.warn("cannot clone medicament id: " + toAdd.Id, e);
								}
								addMedicamentToMedication(medication, medicaments, toAdd);
							}
						} else {
							addMedicamentToMedication(medication, medicaments, toAdd);
						}
					}
				}
				medication.Medicaments = medicaments;
			}
		}
	}
	
	private void findPatientForMedication(Medication medication){
		if (medication.Patient != null) {
			String bDate = medication.Patient.BDt;
			Patient patient = KontaktMatcher.findPatient(medication.Patient.LName,
				medication.Patient.FName, bDate != null ? bDate.replace("-", "") : null, null, null,
				null,
				null, null, CreateMode.ASK);
			if (patient != null && patient.getId() != null && patient.exists()) {
				medication.Patient.patientId = patient.getId();
				medication.Patient.patientLabel = patient.getPersonalia();
			}
		}
		
	}

	private void addMedicamentToMedication(Medication medication, List<Medicament> medicaments,
		Medicament toAdd){
		if (toAdd.Pos != null && !toAdd.Pos.isEmpty()) {
			Posology pos = toAdd.Pos.get(0);
			StringBuffer buf = new StringBuffer();
			if (pos.D != null) {
				int size = pos.D.size();
				for (float f : pos.D) {
					buf.append((int) f);
					size--;
					if (size != 0) {
						buf.append("-");
					}
				}
			}
			toAdd.dosis = buf.toString();
			transformAppInstrToFreeTextDosage(toAdd);
			toAdd.dateFrom = pos.DtFrom;
			toAdd.dateTo = pos.DtTo;
		}
		
		findArticleForMedicament(toAdd);
		
		// check if db already contains this prescription
		if (!findPresciptionsByMedicament(medication, toAdd).isEmpty()) {
			toAdd.exists = true;
		}
		medicaments.add(toAdd);
	}

	private void transformAppInstrToFreeTextDosage(Medicament toAdd){
		if (toAdd.dosis.isEmpty() && toAdd.AppInstr != null) {
			String[] split = toAdd.AppInstr.split("\\" + Medicament.FREETEXT_PREFIX);
			if (split.length > 1) {
				toAdd.AppInstr = split[0];
				int idx = split[1].lastIndexOf(Medicament.FREETEXT_POSTFIX);
				if (idx > 0) {
					toAdd.dosis = split[1].substring(0, idx);
				}
			}
		}
	}

	@Override
	public List<Prescription> findPresciptionsByMedicament(Medication medication,
		Medicament medicament){
		if (medicament.artikelstammItem != null && medication.Patient != null
			&& medication.Patient.patientId != null) {
			Query<Prescription> qre = new Query<>(Prescription.class);
			qre.add(Prescription.FLD_PATIENT_ID, Query.LIKE, medication.Patient.patientId);
			qre.add(Prescription.FLD_ARTICLE, Query.LIKE,
				medicament.artikelstammItem.storeToString());
			qre.add(Prescription.FLD_DOSAGE, Query.LIKE, medicament.dosis);
			qre.orderBy(true, PersistentObject.FLD_LASTUPDATE);
			
			List<Prescription> execute = qre.execute();
			
			TimeTool now = new TimeTool();
			now.add(TimeTool.SECOND, 5);
			return execute.parallelStream().filter(p -> !p.isStopped(now))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	private void findArticleForMedicament(Medicament medicament)
	{
		Query<ArtikelstammItem> qbe = new Query<>(ArtikelstammItem.class);
		if (medicament.IdType == 2)
		{
			//GTIN
			ArtikelstammItem artikelstammItem = ArtikelstammItem.findByEANorGTIN(medicament.Id);
			if (artikelstammItem != null) {
				medicament.artikelstammItem = artikelstammItem;
			}
		}
		else if (medicament.IdType == 3)
		{
			//PHARMACODE
			ArtikelstammItem artikelstammItem = ArtikelstammItem.findByPharmaCode(medicament.Id);
			if (artikelstammItem != null)
			{
				medicament.artikelstammItem = artikelstammItem;
			}
		}
	}

	public class MedicationDeserializer implements JsonDeserializer<Medication> {
		
		Gson g = new GsonBuilder().create();
		
		@Override
		public Medication deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context){
			Medication u = null;
			try {
				try {
					u = g.fromJson(json, Medication.class);
					return u;
				} catch (JsonSyntaxException e) {
					// because version incompatibility of 16A the MedicalData 'Med' attribute will be removed
					// MedicalData 'Med' has different types in the version 16A 
					if (json.getAsJsonObject().get("Patient") != null) {
						json.getAsJsonObject().get("Patient").getAsJsonObject().remove("Med");
					}
					u = g.fromJson(json, Medication.class);
					logger.warn("json parsed successfully - by removing the 'Med' attribute");
				}
			} catch (Exception e) {
				logger.error("unexpected json error", e);
			}
			return u;
			
		}
		
	}
}
