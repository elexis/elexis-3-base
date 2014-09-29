package at.medevit.elexis.ehc.docbox.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Consumable;
import org.openhealthtools.mdht.uml.cda.ManufacturedProduct;
import org.openhealthtools.mdht.uml.cda.Material;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.EN;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import ch.elexis.data.Mandant;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.Rezept;
import ehealthconnector.cda.documents.ch.CdaCh;
import ehealthconnector.cda.documents.ch.CdaChUtil;
import ehealthconnector.cda.documents.ch.Organization;

public class DocboxService {
	
	public static final String DOMAIN_KSK = "www.xid.ch/id/ksk"; //$NON-NLS-1$
	
	public static CdaCh getPrescriptionDocument(Rezept rezept){
		CdaCh document =
			EhcServiceComponent.getService().getCdaChDocument(rezept.getPatient(),
				rezept.getMandant());
		
		ClinicalDocument clinicalDocument = document.docRoot.getClinicalDocument();
		// RezeptID
		II id = DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.105.1.6");
		id.setExtension(getRezeptId(rezept));
		clinicalDocument.setId(id);
		// Ausstellungsdatum
		try {
			clinicalDocument.setEffectiveTime(CdaChUtil.createTSFromEuroDate(rezept.getDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Patient und Arzt bereits gesetzt, darum custodian
		Rechnungssteller rechnungssteller = rezept.getMandant().getRechnungssteller();
		Organization organization = new Organization(rechnungssteller.getLabel());
		organization.cAddAddress(EhcCoreMapper.getEhcAddress(rechnungssteller.getAnschrift()));
		organization.cAddPhone("");
		document.cSetCustodian(organization);
		// CDA body und rezeptzeilen
		Section section = CDAFactory.eINSTANCE.createSection();
		clinicalDocument.addSection(section);
		List<Prescription> prescriptions = rezept.getLines();
		for (Prescription prescription : prescriptions) {
			SubstanceAdministration administration =
				CDAFactory.eINSTANCE.createSubstanceAdministration();
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
			
			section.addSubstanceAdministration(administration);
		}
		
		return document;
	}
	
	private static String getRezeptId(Rezept rezept){
		Date now = new Date();
		
		StringBuilder sb = new StringBuilder();
		sb.append("202");
		sb.append(getZsr(rezept));
		sb.append(new SimpleDateFormat("yyyy").format(now));
		sb.append(new SimpleDateFormat("MM").format(now));
		sb.append(new SimpleDateFormat("dd").format(now));
		sb.append(new SimpleDateFormat("HH").format(now));
		sb.append(new SimpleDateFormat("mm").format(now));
		sb.append(new SimpleDateFormat("ss").format(now));
		sb.append(new SimpleDateFormat("SS").format(now));
		
		String checkString = sb.toString();
		int checkSum = 0;
		for (int i = 0; i < checkString.length(); i++) {
			checkSum += Integer.parseInt(checkString.substring(i, i + 1));
		}
		sb.append(String.valueOf(checkSum % 10));
		
		return sb.toString();
	}
	
	private static String getZsr(Rezept rezept){
		Mandant mandant = rezept.getMandant();
		Rechnungssteller rechnungssteller = mandant.getRechnungssteller();
		
		String zsr = rechnungssteller.getXid(DOMAIN_KSK);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.substring(zsr.length() - 6, zsr.length());
		}
		zsr = mandant.getXid(DOMAIN_KSK);
		if (zsr != null && !zsr.isEmpty() && zsr.length() >= 6) {
			return zsr.substring(zsr.length() - 6, zsr.length());
		}
		throw new IllegalStateException("Keine ZSR gefunden");
	}
	
}
