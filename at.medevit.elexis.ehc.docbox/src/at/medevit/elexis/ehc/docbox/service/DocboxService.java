package at.medevit.elexis.ehc.docbox.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.xml.utils.DefaultErrorHandler;
import org.ehealth_connector.cda.ch.AbstractCdaCh;
import org.ehealth_connector.common.Organization;
import org.ehealth_connector.common.Telecoms;
import org.ehealth_connector.common.enums.AddressUse;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.Author;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Consumable;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.InfrastructureRootTypeId;
import org.openhealthtools.mdht.uml.cda.ManufacturedProduct;
import org.openhealthtools.mdht.uml.cda.Material;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.cda.Supply;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_PQ;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.PIVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.PQ;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.TEL;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassSupply;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
import org.openhealthtools.mdht.uml.hl7.vocab.SetOperator;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActClassDocumentEntryAct;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import ch.elexis.data.Mandant;
import ch.elexis.data.Person;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.Rezept;
import ch.elexis.docbox.ws.client.SendClinicalDocumentClient;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class DocboxService {
	private static final Logger logger = LoggerFactory.getLogger(DocboxService.class);
	
	public static final String DOMAIN_KSK = "www.xid.ch/id/ksk"; //$NON-NLS-1$
	
	public static final String[] DOSE_TIME = {
		"190001010800", "190001011200", "190001011600", "190001012000"
	};

	public static AbstractCdaCh<?> getPrescriptionDocument(Rezept rezept){
		AbstractCdaCh<?> document =
			EhcServiceComponent.getService().getCdaChDocument(rezept.getPatient(),
				rezept.getMandant());
		
		ClinicalDocument clinicalDocument = document.getDocRoot().getClinicalDocument();
		// clear template ids for now
		clinicalDocument.getTemplateIds().clear();

		// CDA type and template id
		InfrastructureRootTypeId typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
		typeId.setRoot("2.16.840.1.113883.1.3");
		typeId.setExtension("POCD_HD000040");
		clinicalDocument.setTypeId(typeId);

		II id = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.1.1.1");
		id.setExtension("CDA-CH");
		clinicalDocument.setId(id);
		clinicalDocument.getTemplateIds().add(id);

		id = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.105");
		id.setExtension("SwissMedicalSuite_ERezept");
		clinicalDocument.setId(id);
		clinicalDocument.getTemplateIds().add(id);

		// set loinc code
		CE loinccode = DatatypesFactory.eINSTANCE.createCE();
		loinccode.setCodeSystem("2.16.840.1.113883.6.1");
		loinccode.setCode("57833-6");
		loinccode.setDisplayName("Prescriptions");
		loinccode.setCodeSystemName("LOINC");
		clinicalDocument.setCode(loinccode);
		
		// RezeptID
		id = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.105.1.6");
		id.setExtension(getRezeptId(rezept));
		clinicalDocument.setId(id);
		// Ausstellungsdatum
		TS timestamp = DatatypesFactory.eINSTANCE.createTS();
		TimeTool rezeptDate = new TimeTool(rezept.getDate());
		timestamp.setValue(rezeptDate.toString(TimeTool.TIMESTAMP));
		clinicalDocument.setEffectiveTime(timestamp);
		// confidentiality
		CE confidentiality = DatatypesFactory.eINSTANCE.createCE();
		confidentiality.setCodeSystem("2.16.840.1.113883.5.25");
		confidentiality.setCode("N");
		clinicalDocument.setConfidentialityCode(confidentiality);
		
		// add empty id to patient role
		PatientRole patientRole = clinicalDocument.getPatientRoles().get(0);
		patientRole.getIds().add(DatatypesFactory.eINSTANCE.createII());

		// add empty time to author
		Author author = clinicalDocument.getAuthors().get(0);
		author.setTime(DatatypesFactory.eINSTANCE.createTS());

		// Patient und Arzt bereits gesetzt, darum custodian
		Rechnungssteller rechnungssteller = rezept.getMandant().getRechnungssteller();
		Organization organization = null;
		if (rechnungssteller.istOrganisation()) {
			organization =
				new Organization(rechnungssteller.get(Rechnungssteller.FLD_NAME1) + " "
					+ rechnungssteller.get(Rechnungssteller.FLD_NAME2));
		} else {
			organization =
				new Organization(rechnungssteller.get(Person.TITLE) + " "
					+ rechnungssteller.get(Rechnungssteller.FLD_NAME1) + " "
					+ rechnungssteller.get(Rechnungssteller.FLD_NAME2));
		}
		organization.addAddress(EhcCoreMapper.getEhcAddress(rechnungssteller.getAnschrift()));
		String phone = (String) rechnungssteller.get(Rechnungssteller.FLD_PHONE1);
		if (!StringTool.isNothing(phone)) {
			Telecoms telcoms = organization.getTelecoms();
			telcoms.addPhone(phone, AddressUse.BUSINESS);
		}
		document.setCustodian(organization);
		// add ZSR to custodian organization
		id = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.105.1.1.2");
		id.setExtension(getZsr(rezept));
		clinicalDocument.getCustodian().getAssignedCustodian()
			.getRepresentedCustodianOrganization().getIds().add(id);
		// CDA body und rezeptzeilen
		Section section = CDAFactory.eINSTANCE.createSection();
		clinicalDocument.addSection(section);
		
		CE prescriptionCode = DatatypesFactory.eINSTANCE.createCE();
		prescriptionCode.setNullFlavor(NullFlavor.NA);
		CD translationCode = DatatypesFactory.eINSTANCE.createCD();
		translationCode.setCodeSystem("2.16.756.5.30.1.105.2.2");
		translationCode.setCode("VERORDNETEMEDIKAMENTE");
		prescriptionCode.getTranslations().add(translationCode);
		section.setCode(prescriptionCode);
		
		ST title = DatatypesFactory.eINSTANCE.createST();
		title.addText("Medikamente");
		section.setTitle(title);
		StringBuilder sectionText = new StringBuilder();
		addMedicationTextStart(sectionText);
		List<Prescription> prescriptions = rezept.getLines();
		for (int idx = 0; idx < prescriptions.size(); idx++) {
			Prescription prescription = prescriptions.get(idx);
			SubstanceAdministration administration =
				CDAFactory.eINSTANCE.createSubstanceAdministration();
			administration.setMoodCode(x_DocumentSubstanceMood.RQO);
			// pharmacode
			String pharmaCode = prescription.getArtikel().getPharmaCode();
			II pharmacodeId = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.2.6.1");
			pharmacodeId.setExtension(pharmaCode);
			administration.getIds().add(pharmacodeId);
			
			// atc code as material
			String atcCode = prescription.getArtikel().getATC_code();
			ManufacturedProduct product = CDAFactory.eINSTANCE.createManufacturedProduct();
			Material material = CDAFactory.eINSTANCE.createMaterial();
			if (atcCode != null && !atcCode.isEmpty()) {
				material.setCode(DatatypesFactory.eINSTANCE.createCE(atcCode,
					"2.16.840.1.113883.6.73"));
			} else {
				material.setCode(DatatypesFactory.eINSTANCE.createCE());
			}
			product.setManufacturedMaterial(material);
			Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
			consumable.setManufacturedProduct(product);
			administration.setConsumable(consumable);

			addDose(administration, prescription.getDosis());
			addRemark(administration, prescription.getBemerkung());
			addQuantity(administration, 1);

			// artikel, dosierung und bemerkung in text
			ED text = DatatypesFactory.eINSTANCE.createED();
			TEL reference = DatatypesFactory.eINSTANCE.createTEL();
			reference.setValue("#m" + idx);
			text.setReference(reference);
			//			text.addText("<reference value=\"#m" + idx + "\" />");
			administration.setText(text);
			// text of section
			addMedicationText(sectionText, prescription, idx);
			
			section.addSubstanceAdministration(administration);
		}
		addMedicationTextEnd(sectionText);
		section.createStrucDocText(sectionText.toString());

		return document;
	}
	
	private static void addMedicationText(StringBuilder sectionText, Prescription prescription,
		int id){
		// article
		sectionText.append("<tr>\n<td>\n");
		sectionText.append("<content ID=\"m" + id + "\"> " + prescription.getArtikel().getLabel()
			+ "</content>");
		sectionText.append("\n</td>");
		// dose
		sectionText.append("\n<td>\n");
		sectionText.append(prescription.getDosis());
		sectionText.append("\n</td>");
		// valid date range
		sectionText.append("\n<td>\n");
		String endDate = prescription.getEndDate();
		if (endDate != null && !endDate.isEmpty()) {
			sectionText.append("Gültig bis " + endDate);
		}
		sectionText.append("\n</td>");
		// remarks
		sectionText.append("\n<td>\n");
		sectionText.append(prescription.getBemerkung());
		sectionText.append("\n</td>\n</tr>");
	}

	private static void addMedicationTextStart(StringBuilder sectionText){
		sectionText.append("<table>\n<thead>\n<tr>\n")
			.append("<th>Präparat</th><th>Dosis</th><th>Gültigkeit</th><th>Verabreichung</th>")
			.append("\n</tr>\n</thead>\n").append("<tbody>\n");
	}
	
	private static void addMedicationTextEnd(StringBuilder sectionText){
		sectionText.append("\n</tbody>\n</table>\n");
	}

	private static void addQuantity(SubstanceAdministration administration, int quantity){
		if (administration != null) {
			EntryRelationship relationship = CDAFactory.eINSTANCE.createEntryRelationship();
			relationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
			relationship.setInversionInd(false);
			Supply supply = CDAFactory.eINSTANCE.createSupply();
			supply.setClassCode(ActClassSupply.SPLY);
			supply.setMoodCode(x_DocumentSubstanceMood.INT);
			PQ quantityPQ = DatatypesFactory.eINSTANCE.createPQ();
			quantityPQ.setValue(Double.valueOf(quantity));
			supply.setQuantity(quantityPQ);
			relationship.setSupply(supply);
			administration.getEntryRelationships().add(relationship);
		}
	}
	
	private static void addRemark(SubstanceAdministration administration, String bemerkung){
		if (administration != null && bemerkung != null && !bemerkung.isEmpty()) {
			EntryRelationship relationship = CDAFactory.eINSTANCE.createEntryRelationship();
			relationship.setTypeCode(x_ActRelationshipEntryRelationship.SPRT);
			Act act = CDAFactory.eINSTANCE.createAct();
			act.setClassCode(x_ActClassDocumentEntryAct.INFRM);
			act.setMoodCode(x_DocumentActMood.RQO);
			CD code = DatatypesFactory.eINSTANCE.createCD();
			code.setNullFlavor(NullFlavor.NA);
			ED text = DatatypesFactory.eINSTANCE.createED();
			text.addText(bemerkung);
			code.setOriginalText(text);
			act.setCode(code);
			relationship.setAct(act);
			administration.getEntryRelationships().add(relationship);
		}
	}
	
	private static void addDose(SubstanceAdministration administration, String dosis){
		ArrayList<Float> doseFloats = Prescription.getDoseAsFloats(dosis);
		if (!doseFloats.isEmpty()) {
			if (doseFloats.size() == 1) {
				// assume per day
				if (doseFloats.get(0) > 0) {
					addEffectiveTime(administration, "", "1", "d");
					addDoseQuantity(administration, doseFloats.get(0), "1");
				}
			} else {
				// morning, midday, evening, night
				for (int i = 0; i < doseFloats.size(); i++) {
					if (doseFloats.get(i) > 0) {
						SubstanceAdministration doseAdministration =
							addDoseAdministration(administration);
						if (i < 4) {
							addEffectiveTime(doseAdministration, DOSE_TIME[i], "1", "d");
						}
						addDoseQuantity(doseAdministration, doseFloats.get(i), "1");
					}
				}
			}
		}
		// default do not encode ... info has to be in remark or text form
	}

	private static SubstanceAdministration addDoseAdministration(
		SubstanceAdministration administration){
		if (administration != null) {
			EntryRelationship relationship = CDAFactory.eINSTANCE.createEntryRelationship();
			relationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
			SubstanceAdministration ret = CDAFactory.eINSTANCE.createSubstanceAdministration();
			ret.setMoodCode(x_DocumentSubstanceMood.RQO);
			
			ManufacturedProduct product = CDAFactory.eINSTANCE.createManufacturedProduct();
			Material material = CDAFactory.eINSTANCE.createMaterial();
			material.setCode(DatatypesFactory.eINSTANCE.createCE());
			product.setManufacturedMaterial(material);
			Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
			consumable.setManufacturedProduct(product);
			ret.setConsumable(consumable);

			relationship.setSubstanceAdministration(ret);
			administration.getEntryRelationships().add(relationship);
			return ret;
		}
		return null;
	}
	
	private static void addDoseQuantity(SubstanceAdministration administration, Float value,
		String unit){
		if (administration != null) {
			IVL_PQ doseQuantity = DatatypesFactory.eINSTANCE.createIVL_PQ();
			PQ quantity = DatatypesFactory.eINSTANCE.createPQ();
			quantity.setValue(Double.valueOf(value));
			quantity.setUnit(unit);
			doseQuantity.setCenter(quantity);
			administration.setDoseQuantity(doseQuantity);
		}
	}

	private static void addEffectiveTime(SubstanceAdministration administration, String time,
		String periodValue, String periodUnit){
		if (administration != null) {
			PIVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createPIVL_TS();
			effectiveTime.setOperator(SetOperator.A);
			IVL_TS phase = DatatypesFactory.eINSTANCE.createIVL_TS();
			phase.setValue(time);
			effectiveTime.setPhase(phase);
			PQ period = DatatypesFactory.eINSTANCE.createPQ();
			period.setValue(Double.parseDouble(periodValue));
			period.setUnit(periodUnit);
			effectiveTime.setPeriod(period);
			administration.getEffectiveTimes().add(effectiveTime);
		}
	}

	public static ByteArrayOutputStream getPrescriptionPdf(AbstractCdaCh<?> document){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		URL xslt = null;
		xslt = DocboxService.class.getResource("/rsc/xsl/prescription.xsl");
		
		ByteArrayOutputStream documentXml = new ByteArrayOutputStream();
		try {
			CDAUtil.save(document.getDocRoot().getClinicalDocument(), documentXml);

			generatePdf(documentXml, xslt.openStream(), out);
		} catch (Exception e) {
			logger.error("Could not create prescription PDF" + e);
		}

		return out;
	}
	
	public static String sendPrescription(InputStream xmlFile, InputStream pdfFile){
		SendClinicalDocumentClient send = new SendClinicalDocumentClient();
		
		boolean success = send.sendClinicalDocument(xmlFile, pdfFile);
		String message = send.getMessage();
		if (!success) {
			message = "FAILED " + message;
		}
		return message;
	}

	private static void generatePdf(ByteArrayOutputStream documentXml, InputStream xslt,
		ByteArrayOutputStream pdf){

		FopFactory fopFactory = FopFactory.newInstance();
		
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		
		// Setup output
		try {
			// Construct fop with desired output format
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdf);
			
			// Setup XSLT
			Transformer transformer = getTransformerForXSLT(xslt);
			
			// Setup input for XSLT transformation
			Source src = new StreamSource(new ByteArrayInputStream(documentXml.toByteArray()));
			
			// Resulting SAX events (the generated FO) must be piped through to
			// FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			
			// Start XSLT transformation and FOP processing
			transformer.transform(src, res);
		} catch (TransformerException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} catch (FOPException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} finally {
			try {
				pdf.close();
			} catch (IOException e) {
				// ignore exception on close ...
			}
		}
	}

	private static Transformer getTransformerForXSLT(InputStream xslt)
		throws TransformerConfigurationException{
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer ret = factory.newTransformer(new StreamSource(xslt));
		ret.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		ret.setErrorListener(new DefaultErrorHandler() {
			@Override
			public void error(TransformerException exception) throws TransformerException{
				super.error(exception);
				throw exception;
			}
			
			@Override
			public void fatalError(TransformerException exception) throws TransformerException{
				super.error(exception);
				throw exception;
			}
		});
		return ret;
	}

	private static String getRezeptId(Rezept rezept){
		Date now = new Date();
		
		StringBuilder sb = new StringBuilder();
		sb.append("202");
		sb.append(getIdZsr(rezept));
		sb.append(new SimpleDateFormat("yyyy").format(now));
		sb.append(new SimpleDateFormat("MM").format(now));
		sb.append(new SimpleDateFormat("dd").format(now));
		sb.append(new SimpleDateFormat("HH").format(now));
		sb.append(new SimpleDateFormat("mm").format(now));
		sb.append(new SimpleDateFormat("ss").format(now));
		// Milliseconds ... 
		String millis = new SimpleDateFormat("SS").format(now);
		if (millis.length() == 1) {
			sb.append("0").append(millis);
		} else if (millis.length() == 2) {
			sb.append(millis);
		} else if (millis.length() == 3) {
			sb.append(millis.substring(1));
		} else {
			sb.append("00");
		}
		
		String checkString = sb.toString();
		try {
			int checkSum = 0;
			for (int i = 0; i < checkString.length(); i++) {
				checkSum += Integer.parseInt(checkString.substring(i, i + 1));
			}
			sb.append(String.valueOf(checkSum % 10));
		} catch (NumberFormatException ne) {
			logger.error("Could not generate checksum for [" + checkString + "]");
			throw ne;
		}
		return sb.toString();
	}
	
	private static String getIdZsr(Rezept rezept){
		String zsr = getZsr(rezept);
		if (zsr != null && !zsr.isEmpty()) {
			if (zsr.length() >= 6) {
				return zsr.substring(zsr.length() - 6, zsr.length());
			}
		}
		throw new IllegalStateException("Keine ZSR gefunden");
	}

	private static String getZsr(Rezept rezept){
		Mandant mandant = rezept.getMandant();
		Rechnungssteller rechnungssteller = mandant.getRechnungssteller();
		
		String zsr = rechnungssteller.getXid(DOMAIN_KSK);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.replaceAll("\\.", "");
		}
		zsr = rechnungssteller.getInfoString("KSK");
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.replaceAll("\\.", "");
		}
		zsr = mandant.getXid(DOMAIN_KSK);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.replaceAll("\\.", "");
		}
		zsr = mandant.getInfoString("KSK");
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.replaceAll("\\.", "");
		}
		throw new IllegalStateException("Keine ZSR gefunden");
	}
	
}
