package at.medevit.elexis.ehc.docbox.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Consumable;
import org.openhealthtools.mdht.uml.cda.InfrastructureRootTypeId;
import org.openhealthtools.mdht.uml.cda.ManufacturedProduct;
import org.openhealthtools.mdht.uml.cda.Material;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.CV;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.EN;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
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
import ehealthconnector.cda.documents.ch.CdaCh;
import ehealthconnector.cda.documents.ch.Organization;

public class DocboxService {
	private static final Logger logger = LoggerFactory.getLogger(DocboxService.class);
	
	public static final String DOMAIN_KSK = "www.xid.ch/id/ksk"; //$NON-NLS-1$

	public static CdaCh getPrescriptionDocument(Rezept rezept){
		CdaCh document =
			EhcServiceComponent.getService().getCdaChDocument(rezept.getPatient(),
				rezept.getMandant());
		
		ClinicalDocument clinicalDocument = document.docRoot.getClinicalDocument();
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
		organization.cAddAddress(EhcCoreMapper.getEhcAddress(rechnungssteller.getAnschrift()));
		String phone = (String) rechnungssteller.get(Rechnungssteller.FLD_PHONE1);
		if (!StringTool.isNothing(phone)) {
			organization.cAddPhone(phone);
		}
		document.cSetCustodian(organization);
		// add ZSR to custodian organization
		id = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.105.1.1.2");
		id.setExtension(getZsr(rezept));
		clinicalDocument.getCustodian().getAssignedCustodian()
			.getRepresentedCustodianOrganization().getIds().add(id);
		// CDA body und rezeptzeilen
		Section section = CDAFactory.eINSTANCE.createSection();
		clinicalDocument.addSection(section);
		
		CV prescriptionCode = DatatypesFactory.eINSTANCE.createCV();
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
		sectionText.append("<list>");

		List<Prescription> prescriptions = rezept.getLines();
		for (Prescription prescription : prescriptions) {
			SubstanceAdministration administration =
				CDAFactory.eINSTANCE.createSubstanceAdministration();
			administration.setMoodCode(x_DocumentSubstanceMood.RQO);
			// pharmacode
			ManufacturedProduct product = CDAFactory.eINSTANCE.createManufacturedProduct();
			String pharmaCode = prescription.getArtikel().getPharmaCode();
			II pharmacodeId = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.2.6.1");
			pharmacodeId.setExtension(pharmaCode);
			product.getIds().add(pharmacodeId);
			// atc code as material
			String atcCode = prescription.getArtikel().getATC_code();
			if (atcCode != null && !atcCode.isEmpty()) {
				Material material = CDAFactory.eINSTANCE.createMaterial();
				material.setCode(DatatypesFactory.eINSTANCE.createCE(atcCode,
					"2.16.840.1.113883.6.73"));
				EN articelLabel = DatatypesFactory.eINSTANCE.createEN();
				articelLabel.addText(prescription.getArtikel().getLabel());
				material.setName(articelLabel);
				product.setManufacturedMaterial(material);
				
			}
			Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
			consumable.setManufacturedProduct(product);
			administration.setConsumable(consumable);
			// artikel, dosierung und bemerkung in text
			ED text = DatatypesFactory.eINSTANCE.createED();
			text.addText(prescription.getArtikel().getLabel());
			String dosis = prescription.getDosis();
			if (dosis != null && !dosis.isEmpty()) {
				text.addText(", " + dosis);
			}
			String remark = prescription.getBemerkung();
			if (remark != null && !remark.isEmpty()) {
				text.addText(", " + remark);
			}
			administration.setText(text);
			// text of section
			sectionText.append("<item>" + text.getText() + "</item>");
			
			section.addSubstanceAdministration(administration);
		}
		sectionText.append("</list>");
		section.createStrucDocText(sectionText.toString());

		return document;
	}
	
	public static ByteArrayOutputStream getPrescriptionPdf(CdaCh document){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		URL xslt = null;
		xslt = DocboxService.class.getResource("/rsc/xsl/prescription.xsl");
		
		ByteArrayOutputStream documentXml = new ByteArrayOutputStream();
		document.cPrintXmlToStream(documentXml);
		
		try {
			generatePdf(documentXml, xslt.openStream(), out);
		} catch (IOException e) {
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
		int checkSum = 0;
		for (int i = 0; i < checkString.length(); i++) {
			checkSum += Integer.parseInt(checkString.substring(i, i + 1));
		}
		sb.append(String.valueOf(checkSum % 10));
		
		return sb.toString();
	}
	
	private static String getIdZsr(Rezept rezept){
		String zsr = getZsr(rezept);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.substring(zsr.length() - 6, zsr.length());
		}
		throw new IllegalStateException("Keine ZSR gefunden");
	}

	private static String getZsr(Rezept rezept){
		Mandant mandant = rezept.getMandant();
		Rechnungssteller rechnungssteller = mandant.getRechnungssteller();
		
		String zsr = rechnungssteller.getXid(DOMAIN_KSK);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr;
		}
		zsr = mandant.getXid(DOMAIN_KSK);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr;
		}
		throw new IllegalStateException("Keine ZSR gefunden");
	}
	
}
